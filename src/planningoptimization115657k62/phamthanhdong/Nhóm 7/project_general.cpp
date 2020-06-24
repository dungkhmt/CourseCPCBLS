#include<bits/stdc++.h>
#include<iostream>
#include<vector>
using namespace std;

#define M 20
#define N 35
// Number of individuals in each generation 
#define POPULATION_SIZE 1000 //kich thuoc quan the



int  Q[N][M]; // matrix Q[i][j] is number of product ith in shelf j
int d[M + 1][M + 1]; //d[i][j] distance from point i to j 
int q[N];  // q[i] is number of product ith employee needs
int mp[M];//route -  lo trinh duong di
//int product[N] = {0};//sum product of one sheft  - tính tong luong hang co the cung cap
//int len;//do dai quang duong

int max_units[N];
int max_S = -1;
int min_S = 99999999;
int it = 0;

//in trong so
void length_map(int mp[])
{
	
	int length  = d[0][mp[0]];
	
	for(int i  = 0;i<M-1;i++)
	{
		length+=d[mp[i]][mp[i+1]];
	}
		length+=d[mp[M-1]][0];
		cout << "\n Optimizer = " << length << endl;
}	

//tao cac hang hoa
void product_map(int mp[])
{

	int product[N];
	memset(product, 0, sizeof(product));
	for(int i = 0 ; i < M;i++)
	{
		if(mp[i] != 0)
		{
			for(int j = 0;j<N;j++)
			{
				product[j] += Q[j][mp[i]-1];
			}				
	    }
	
	}
		for(int j = 0;j<N;j++)
			{
				cout<<" Item number type: "<< j + 1 <<"/ "<<product[j] << " " << "we need " << q[j] << "\n";
			}	
}


// Function to generate random numbers in given range  
// Hàm tao so ngau nhiên trong pham vi dã cho
int random_num(int start, int end)
{
    int range = (end - start) + 1;
    int random_int = start + (rand() % range);
    return random_int;
}



// tra ve ke ngau nhien
int mutated_genes() {

    int r = random_num(0, M);
    return r;
}


// create chromosome or string of genes 
// tao nhiem sac the hoac chuoi gen
vector<int> create_gnome()
{
    int len = M;
    vector<int> gnome;
    for (int i = 0; i < len; i++)
        gnome.push_back(mutated_genes());
    return gnome;
}



class Individual
{
public:
    vector<int> chromosome;
    int fitness;
    int optimizer;
    int path_length();
    Individual(vector<int> chromosome);// ca the, nhan vao chuoi gen
    Individual mate(Individual parent2);// tu cha me
    int cal_fitness();
};



Individual::Individual(vector<int> chromosome)
{
    this->chromosome = chromosome;
    fitness = cal_fitness(); //cot loi cua bai toan
    optimizer = path_length();
};

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

Individual Individual::mate(Individual par2)
{

    vector<int> child_chromosome;//tao chuoi gen con

    int len = chromosome.size();
    for (int i = 0; i < len; i++)
    {

        float p = random_num(0, 100) / 100;

        if (p < 0.6)
            child_chromosome.push_back(chromosome[i]);

        else if (p < 0.90)
            child_chromosome.push_back(par2.chromosome[i]);

        else
            child_chromosome.push_back(mutated_genes());
    }

    return Individual(child_chromosome);
};

//fitness - ko duoc trung nhau giua cac ke hang ( neu i != j va khac diem 0)
int Individual::cal_fitness()
{
    int len = M;
    int fitness = 0;
    for (int i = 0; i < len; i++) {
        // penalize when have  same shelves
        for (int j = 0; j < len; j++) {
            if (i != j) {
                if (chromosome[i] != 0)
                    if (chromosome[i] == chromosome[j]) {
                        fitness += 10;//trung phat do vi pham dieu kien
                    }
            }
        }

    }

    // penalize when we end 0 point but restart at other point
    for (int i = len - 1; i >= 0; i--) {
        if (chromosome[i] != 0 && chromosome[i - 1] == 0) {
            fitness += 1;
        }
    }

    // penailize when units pf product is not enough
    for (int j = 0; j < N; j++) {
        int arr = 0;
        for (int i = 0; i < len; i++) {
            arr += Q[j][chromosome[i]-1];
        }
        if (arr < q[j])
		 {
            fitness += 1; //neu ko du so luong san pham, tang fitness
        }
        
    }
	
	int temp = this->path_length();
	fitness -= max_S / temp;
    

    int temp1 = 0;
    for (int j = 0; j < len; j++) {
             if (chromosome[j] == 0)
                    temp1++;  //thuong them do lo trinh ngan gon hon
    }
      //  fitness -= temp1 * 0.00001;
        
    return fitness;
};


// Overloading < operator 
bool operator<(const Individual& ind1, const Individual& ind2)
{
    return ind1.fitness < ind2.fitness;

}

/* load data from file */
void create() {

//Nhap vao so luong tung mat hang tren ke hang
    ifstream infile;
    infile.open("D:/Q.txt");
    if (!infile) {
        cerr << "Unable to open file Q\n" << endl;
        exit(1);   // call system to stop
    }
    //nhap vao 
    for (int i = 0; i < N; ++i) {
        for (int j = 0; j < M; ++j) {
            if (!(infile >> Q[i][j])) {
                cerr << "Unexpected end of file Q\n" << endl;
                exit(1);   // call system to stop
            }
        }
    }
    infile.close();


    // read file d(i, j) la khoang cach giua cac ke hang
    ifstream infile_d;
    infile_d.open("D:/distance.txt");
    if (!infile_d) {
        cerr << "Unable to open file d\n" << endl;
        exit(1);   // call system to stop
    }
    /* end code read text file */
    for (int i = 0; i < M + 1; ++i) {
        for (int j = 0; j < M + 1; ++j) {
            if (!(infile_d >> d[i][j])) {
                cerr << "Unexpected end of file d\n" << endl;
                exit(1);   // call system to stop
            }
        }
    }
    infile_d.close();


    // read file q(k) - nhap so luong hang hoa can nhu cau
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



int main()
{


   
    
    create();
    srand((unsigned)(time(0)));
    
    cout<<"Need: \n";
    for(int i = 0;i < N;i++)
    cout<<q[i]<<" ";
    cout<<endl;
    
    // current generation 
    int generation = 0;//gen dau tien

    vector<Individual> population;
    bool found = false;

    // create initial population 
    for (int i = 0; i < POPULATION_SIZE; i++)
    {
        vector<int> gnome = create_gnome();
        population.push_back(Individual(gnome));
    }

    while (!found)
    {
        // sort the population in increasing order of fitness score 
        //sap xep dan so tang dan theo nang luc
        sort(population.begin(), population.end());

        // if the individual having lowest fitness score ie.  
        // 0 then we know that we have reached to the target 
        // and break the loop 
        if (population[0].fitness <= 0)
        {
            found = true;// fitness == 0 , tiep tuc toi uu ( fitness dung de cho biet da toi uu chua )
            break;
        }

        // Otherwise generate new offsprings for new generation 
        vector<Individual> new_generation;

        // Perform Elitism, that mean 10% of fittest population 
        // goes to the next generation 
        int s = (10 * POPULATION_SIZE) / 100;
        for (int i = 0; i < s; i++)
            new_generation.push_back(population[i]);

        // From 50% of fittest population, Individuals 
        // will mate to produce offspring 
        s = (90 * POPULATION_SIZE) / 100;
        for (int i = 0; i < s; i++)
        {
            int len = population.size();
            int r = random_num(0, 50);
            Individual parent1 = population[r];
            r = random_num(0, 50);
            Individual parent2 = population[r];
            Individual offspring = parent1.mate(parent2);
            new_generation.push_back(offspring);
        }
        population = new_generation;
        cout << "Generation: " << generation << "\t";
        for (int i = 0; i < population[0].chromosome.size(); i++)
            {
            	int temp = (population[0].chromosome[i]);
            	mp[i]  = temp;
			    cout << population[0].chromosome[i] << " ";
			}
		length_map(mp);
		//product_map(mp);
        cout << "\t";
        cout << "Fitness: " << population[0].fitness << "\n";

        generation++;
    }
    cout << "Generation: " << generation << "\t";
    for (int i = 0; i < population[0].chromosome.size(); i++)
        {
        		int temp = (population[0].chromosome[i]);
            	mp[i]  = temp;
		        cout << population[0].chromosome[i] << " ";
		}
	length_map(mp);
	//product_map(mp);
    cout << "\t";
    cout << "Fitness: " << population[0].fitness << "\n";
}
