#include "ortools/linear_solver/linear_solver.h"

using namespace std;

namespace operations_research {

const int N = 4;
int d[N][N] = 
	{ { 0, 3, 2, 4 }, { 9, 0, 2, 3 }, { 1, 2, 0, 7 },
			{ 1, 1, 4, 0 } };
const int T = 2*N - 2;

string str(int i)
{
	string ret = "";
	ret += (char) (i+'0');
	return ret;
}

void run() {
	// Create the linear solver with the GLOP backend.
	MPSolver solver("sportscheduling",
                MPSolver::CBC_MIXED_INTEGER_PROGRAMMING);

	// Create the variables x: x[i][j][t] = 1 if team i againsts team j at week k on i's pitch
	vector<vector<vector<MPVariable*> > > x;
	// f[i][j][k][t] = 1 if team i have to transport from j's stadium to k's stadium at week t
	vector<vector<vector<vector<MPVariable*> > > > f;

	x.resize(N);
	for (int i = 0; i < N; i++) {
		x[i].resize(N);
		for (int j = 0; j < N; j++) {
			x[i][j].resize(T);
			for (int t = 0; t < T; t++)
				x[i][j][t] = solver.MakeIntVar(0,1,"x" + to_string(i) + to_string(j) + to_string(t)); 
		}
	}

	f.resize(N);
	for (int i = 0; i < N; i++) {
		f[i].resize(N);
		for (int j = 0; j < N; j++) {
			f[i][j].resize(N);
			for (int k = 0; k < N; k++) {
				f[i][j][k].resize(T+1);
				for (int t = 0; t <= T; t++)
					f[i][j][k][t] = solver.MakeIntVar(0,1,"f" + to_string(i) + to_string(j) + to_string(k) + to_string(t));
			}
		}
	}

	const double infinity = solver.infinity();

	// [START constraints]
	// each team i play only one match at week t
	for (int i = 0; i < N; i++)
		for (int t = 0; t < T; t++) {
			MPConstraint* c = solver.MakeRowConstraint(1, 1);
			for (int j = 0; j < N; j++)
				if (i != j) {
					c->SetCoefficient(x[i][j][t], 1);
					c->SetCoefficient(x[j][i][t], 1);
				}
		}

	// sum_t(x[i][j][t]) = 1 \forall i, j
	// team i meets team j exactly once in i's stadium
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
			if ( i != j ) {
				MPConstraint* c = solver.MakeRowConstraint(1, 1);
				for (int t = 0; t < T; t++)
					c->SetCoefficient(x[i][j][t], 1);
			}

	// x[i][i][t] = sum_j(x[i][j][t])
	// x[i][i][t] = 1 if team i plays at their stadium
	for (int i = 0; i < N; i++)
		for (int t = 0; t < T; t++) {
			MPConstraint* c = solver.MakeRowConstraint(0, 0);
			for (int j = 0; j < N; j++)
				c->SetCoefficient(x[i][j][t], (i==j)?-1:1);
		}

	
	// x[j][i][t] + x[k][i][t+1] - f[i][j][k][t] <= 1
	// calculate variables f from x
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++) {
			for (int k = 0; k < N; k++) 
				for (int t = 0; t < T-1; t++) {
					MPConstraint* c = solver.MakeRowConstraint(-infinity, 1);
					c->SetCoefficient(x[j][i][t], 1);
					c->SetCoefficient(x[k][i][t+1], 1);
					c->SetCoefficient(f[i][j][k][t], -1);
				}

			// go home
			MPConstraint* c = solver.MakeRowConstraint(0, 0);
			c->SetCoefficient(x[j][i][T-1], -1);
			c->SetCoefficient(f[i][j][i][T-1], 1);

			// start from their stadium: used f[][][][T] instead of f[][][][0]
			MPConstraint* c1 = solver.MakeRowConstraint(0, 0);
			c1->SetCoefficient(x[j][i][0], -1);
			c1->SetCoefficient(f[i][i][j][T], 1);
		}
	
	// [END constraints]
	

	// [START objective]
	// Minimize sum_{i,j,k,t} f[i][j][k][t] * d[j][k];
	MPObjective* const objective = solver.MutableObjective();
	for (int i = 0; i < N; i++)
		for (int j = 0; j < N; j++)
			for (int k = 0; k < N; k++)
				for (int t = 0; t <= T; t++) 
					objective->SetCoefficient(f[i][j][k][t], d[j][k]);

	objective->SetMinimization();
	// [END objective]

	for (int i = 0; i < N; i++) {
		for (int j = 0; j < N; j++)
			cout << d[i][j] << " ";
		cout << endl;
	}

	// [START solve]
	const MPSolver::ResultStatus result_status = solver.Solve();
	// Check that the problem has an optimal solution.
	if (result_status != MPSolver::OPTIMAL) {
		LOG(FATAL) << "The problem does not have an optimal solution!";
	}
	// [END solve]

	cout << "Objective = " << objective->Value() << endl;
	for (int t = 0; t < T; t++) {
		cout << "START ROUND " << t << endl;
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if ( i != j && x[i][j][t]->solution_value() == 1) 
					cout << i << " " << j << endl;
		cout << "END ROUND " << t << endl;
	}	

	LOG(INFO) << "\nAdvanced usage:";
	LOG(INFO) << "Problem solved in " << solver.wall_time() << " milliseconds";
	LOG(INFO) << "Problem solved in " << solver.iterations() << " iterations";
	LOG(INFO) << "Problem solved in " << solver.nodes() << " branch-and-bound nodes";
}
}  // namespace operations_research

int main() {
	operations_research::run();
	return EXIT_SUCCESS;
}
