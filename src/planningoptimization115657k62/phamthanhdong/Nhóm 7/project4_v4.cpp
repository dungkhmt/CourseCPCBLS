#include<bits/stdc++.h>
using namespace std;

#define M 20
#define N 35
#define MAX_EVO 2500
// Number of individuals in each generation 
#define POPULATION_SIZE 200
#define MAX_PENALIZE 10

int  Q [N][M]; // matrix Q[i][j] is number of product ith in shelf j
int d[M+1][M+1]; //d[i][j] distance from point i to j 
int q[N];  // q[i] is number of product ith employee needs
int max_units[N];
int max_S = - 1;
int min_S = 99999999;
int it = 0;
	
  
// Function to generate random numbers in given range  
int random_num(int start, int end) 
{ 
	int random_int;
    int range = (end-start)+1; 
   	random_int = start+(rand()%range); 
    return random_int; 
}

int random_num(int start, int end, int it) 
{ 
	int random_int;
    int range = (end-start)+1; 
    double val = (double)rand() / RAND_MAX;
    if(val < 0.4 )
    	random_int = 0;
    else{
    	random_int = start+(rand()%range); 
	}
    return random_int; 
}
	

// tra ve ke ngau nhien
int mutated_genes(int it){
	int r;
	if(it < MAX_EVO  / 3)
		r = random_num(0, M);	
	else if(it <  2*  MAX_EVO / 3){
		r = random_num(1, M, it);	
	}
	else{
		r = 0;
	}
	return r;
}


int mutated_genes(){
	int r;
	r = random_num(0, M);		
	return r;
}


// create chromosome or string of genes 
vector<int> create_gnome() 
{ 
    int len = M; 
    vector<int> gnome; 
    for(int i = 0; i < len; i++)
    	gnome.push_back( mutated_genes() );
    return gnome; 
}

class Individual 
{ 
public: 
    vector<int> chromosome; 
    unsigned long long int fitness; 
    int optimizer;
    Individual(vector<int> chromosome); 
    Individual mate(Individual parent2, int it); 
    int cal_fitness(); 
    int path_length();
    void evolution();
    bool checkValid(int i);
};

Individual::Individual(vector<int> chromosome) 
{ 
    this->chromosome = chromosome; 
    fitness = cal_fitness();  
    optimizer = path_length();
}; 


bool check_exist(vector<int> chromosome, int k){
	for(int i = 0; i < M; i++){
		if(chromosome[i] == k)
			return true;
	}
	return false;
}

vector<int> swap_mutate(vector<int> chromosome){
	int t;
		
	
	
	for(int i = 0; i < M; i++){
		if(chromosome[i] == 0){
			t = i;
			break;
		}
			
	}
	
	// kiem tra xem co thieu san pham khong
		for(int j = 0; j < N; j++){
		int arr = 0;
			for(int i = 0; i < M; i++){
				arr += Q[j][chromosome[i]];
			}
			if(arr < q[j]){
			// them ke de tranh tinh trang thieu hang
			// bo sunng them 1 so ke
				bool flag = true;
				do{
					int temp = random_num(1, M);
					if(check_exist(chromosome, temp) == false){
						chromosome[t] = temp;
						flag = false;
						t++;
						break;
					}	
				}while(t < M && flag);
			}
		}
	
	
	// xoa moi diem bang 0 o giua
	chromosome.erase(remove(chromosome.begin(), chromosome.end(), 0), chromosome.end());
	for(int i = chromosome.size(); i < M; i++){
		chromosome.push_back(0);
	}
	

	
	return chromosome;
}


Individual Individual::mate(Individual par2, int it) 
{ 

    vector<int> child_chromosome; 
    int len = chromosome.size(); 
    
    if(it < MAX_EVO  / 3){
    	for(int i = 0; i < len; i++) 
    		{ 

        	float p = random_num(0, 100)/100; 
  
        	if(p < 0.25){
        			child_chromosome.push_back( chromosome[i] ); 
				}
			
        	else if(p < 0.5){
            		child_chromosome.push_back( par2.chromosome[i] ); 

  			}
        	else{
            	child_chromosome.push_back( mutated_genes(MAX_EVO-2)); 
        	}
    	}
	}
	else if(it <  2 * MAX_EVO ){

			for(int i = 0; i < len; i++) 
    		{ 

        	float p = random_num(0, 100)/100; 
  
        	if(p < 0.3){
    
            	child_chromosome.push_back( par2.chromosome[i] ); 
 
  			}
        	else if(p < 0.6){
        	
            		child_chromosome.push_back(chromosome[i] ); 

  			}
        	else{
        		int t =  mutated_genes(it);
        		child_chromosome.push_back(t);
        	
            		
        	}
    	}
		
	}
	else{
			for(int i = 0; i < len; i++) 
    		{ 

        	float p = random_num(0, 100)/100; 
  
        	if(p < 0.35){
    
            	child_chromosome.push_back( chromosome[i] ); 
 
  			}
        	else if(p < 0.7){
        	
            		child_chromosome.push_back( par2.chromosome[i] ); 

  			}
        	else{
        		int t =  mutated_genes(it);
        		child_chromosome.push_back(t);
        	
            		
        	}
    	}
    	
	}
     		
  	int t;
		
		
  	child_chromosome = swap_mutate(child_chromosome);
  	
    return Individual(child_chromosome); 
}; 
  

int Individual::cal_fitness() 
{ 
	bool flag = false;
    int len = M; 
    int fitness = 0; 
    for(int i = 0; i < len; i++){
    	// penalize when have  same shelves
    	for(int j = 0; j < len ; j++){
    		if(i != j ){
    			if(chromosome[i] != 0)
    				if(chromosome[i] == chromosome[j] ){
    					fitness +=   MAX_PENALIZE;
				}
			}
		}
    
	}
	
	// penalize when we end 0 point but restart at other point
	for(int i = len-1; i >= 0; i--){
	//	if(chromosome[i] != 0 &&  chromosome[i-1] == 0){
	if(chromosome[i] != 0 &&  chromosome[i-1] == 0 && this->checkValid(i-1) == false){
			fitness += MAX_PENALIZE;
		}
	}
	
	// penailize when units pf product is not enough
	for(int j = 0; j < N; j++){
		int arr = 0;
		for(int i = 0; i < len; i++){
			if(chromosome[i] != 0)
				arr += Q[j][chromosome[i]-1];
		}
		if(arr < q[j]){
			fitness +=  MAX_PENALIZE;
		}
	}
	
	
	// reward when individual have small path length
	int temp = this->path_length();
	fitness -= max_S * (0.05 * MAX_PENALIZE) / temp;
	
	
	for(int i = 0; i < len; i++){
		if(chromosome[i] != 0)
			flag = true;
			break;
		
	}
	
	if(flag == false){
		fitness = INT_MAX;
	}

    return fitness;     
};


bool Individual::checkValid(int i){
		for(int j = 0; j < i; j++){
			if(chromosome[j] != 0){
				return false;
			}
		}
		return true;
}

int Individual::path_length(){
	int len = chromosome.size(); 
	  // distance from 0 to first shelf
	int path_len = d[0][chromosome[0]];
	// distance from shelf to shelf
	for(int j = 0; j < len - 1 ; j++){
		path_len += d[chromosome[j]][chromosome[j+1]];
	}
	// distance from end shelf to point 0
	path_len += d[chromosome[len-1]][0];
	return path_len;
}

// Overloading < operator 
bool operator<(const Individual &ind1, const Individual &ind2) 
{ 
	return ind1.fitness < ind2.fitness; 
    
}


/* load data from file */
void create() {


	ifstream infile;
    infile.open("D:/Q.txt");
	
	if (!infile) {
    	cerr << "Unable to open file Q\n" << endl;
        exit(1);   // call system to stop
	}

	for (int i = 0; i < N; ++i) {
    	for (int j = 0; j < M ; ++j) {
        	if (!(infile >> Q[i][j])) {
            	cerr << "Unexpected end of file Q\n" << endl;
            	exit(1);   // call system to stop
        	}
    	}
	}

	infile.close();

	// read file d(i, j)
	ifstream infile_d;
    infile_d.open("D:/distance.txt");
	
	if (!infile_d) {
    	cerr << "Unable to open file d\n" << endl;
        exit(1);   // call system to stop
	}
/* end code read text file */

	for (int i = 0; i < M+1; ++i) {
    	for (int j = 0; j < M + 1 ; ++j) {
        	if (!(infile_d >> d[i][j])) {
            	cerr << "Unexpected end of file d\n" << endl;
            	exit(1);   // call system to stop
        	}
    	}
	}

	infile_d.close();
		     
		      
	// read file q(k)
	
	ifstream infile_q;
    infile_q.open("D:/need.txt");
	
	if (!infile_q) {
    	cerr << "Unable to open file q\n" << endl;
        exit(1);   // call system to stop
	}

	for (int i = 0; i < N; ++i) {
        	if (!(infile_q >> q[i])) {
            	cerr << "Unexpected end of file q\n" << endl;
            	exit(1);   // call system to stop
        	}
	}

	infile_q.close();	
	
		   
}

void find_max_bound(){
	for(int i = 0; i < M + 1; i++) {
		for(int j = 0; j < M + 1; j ++)
			max_S = max(max_S, d[i][j] );
		}
		
		max_S = max_S * (M+1);
		
}

void Individual::evolution(){
	vector<int> old_indiviual = this->chromosome;
	int old_path = this->path_length();
	
	
	int p1 = random_num(0, N); 
    int p2 = random_num(0, N); 
    
    int t1 = random_num(0, N); 
    int t2 = random_num(0, N); 
    
	 int temp_1 = this->chromosome[p1];
	 int temp_2 = this->chromosome[t1];
	 
	this->chromosome[p1] = this->chromosome[p2];
	this->chromosome[p2] = temp_1;
	 
	 
	this->chromosome[t1] = 	this->chromosome[t2];
	this->chromosome[t2] = temp_2; 
	 
    
  if(this->path_length() < old_path){
    	cout << "New Generation: " << "\t";
    	for(int i = 0; i < 	this->chromosome.size(); i++)
       		cout << 	this->chromosome[i] <<" "; 
    	cout << "\t";
    	cout<< "Fitness: "<< 	this->fitness << "\t"; 
    	cout<< "New Optimizer: "<< 	this->path_length() << "\n"; 
	}
	  
}



void check(vector<int> chromosome){
		for(int j = 0; j < N; j++){
		int arr = 0;
		for(int i = 0; i < M; i++){
			if(chromosome[i] != 0)
				arr += Q[j][chromosome[i]-1];
		}
		if(arr < q[j]){
			cout << "violation : " << j <<" we have: " << arr << " but we need :"  << q[j] << endl;
			return;
			
		}
	}
	cout << endl;
	cout << "we have more than need" << endl;
	
}
// Driver code 
int main() 
{ 

	create();
	find_max_bound();
	
    srand((unsigned)(time(0))); 
  
    // current generation 
    int generation = 0; 
  
    vector<Individual> population; 
    bool found = false; 
  
    // create initial population 
    for(int i = 0; i < POPULATION_SIZE; i++) 
    { 
        vector<int> gnome = create_gnome(); 
        population.push_back(Individual(gnome)); 
    } 
  
    while(!found && it <= MAX_EVO) 
    { 
    	it ++ ;
        // sort the population in increasing order of fitness score 
        sort(population.begin(), population.end()); 
	
       	
        if(population[0].fitness <= 0) 
        { 
        	cout << "Generation: " << "\t";
    		for(int i = 0; i < population[0].chromosome.size(); i++)
       			cout << population[0].chromosome[i] <<" "; 
       		cout << "\t";
       	
			cout<< "Fitness: "<< population[0].fitness << "\t"; 
            cout<< "Optimizer: "<< population[0].path_length() << "\n"; 			

// tao dot bien bang cach trao doi cac diem tren gen cua ca the

        
        population[0].evolution();
        
        found = true;
        break;

     	// TO-DO code
     	
        } 
        
        
        // Otherwise generate new offsprings for new generation 
        vector<Individual> new_generation; 
  
        // Perform Elitism, that mean 10% of fittest population 
        // goes to the next generation 
        int s = (10 * POPULATION_SIZE)/100; 
        for(int i = 0; i < s; i++) 
            new_generation.push_back(population[i]); 
  
        // From 50% of fittest population, Individuals 
        // will mate to produce offspring 
        s = (90 * POPULATION_SIZE) / 100; 
        for(int i = 0; i < s; i++) 
        { 
            int len = population.size(); 
            int r = random_num(0, 50); 
            Individual parent1 = population[r];
			 
            r = random_num(0, 50); 
            Individual parent2 = population[r]; 
            Individual offspring = parent1.mate(parent2, it); 
            
            new_generation.push_back(offspring);  
        } 
        population = new_generation; 
        cout<< "Generation: " << generation << "\t"; 
//        for(int i = 0; i < population[0].chromosome.size(); i++)
//       		cout << population[0].chromosome[i] <<" "; 
//       	cout << "\t";
        cout<< "Fitness: "<< population[0].fitness << "\t"; 
         cout<< "Optimizer: "<< population[0].optimizer << "\n"; 
         

        generation++; 
     } 
     
//        for(int i = 0; i < population[0].chromosome.size(); i++)
//       		cout << population[0].chromosome[i] <<" "; 
//       	cout << "\t";
       	
       	check(population[0].chromosome);

} 

 

 
