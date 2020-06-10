#include<bits/stdc++.h>
using namespace std;
string s;
vector<int> v;
int random(int minN, int maxN){
    return minN + rand() % (maxN + 1 - minN);
}
int main(){
    std::ios::sync_with_stdio(false);
    cin.tie(0);
    srand((int)time(0));
    int n=70;
    int m=40;
    int x=n*0.92;
    for(int i = 0; i < n; ++i){
        cout<<random(50,80)<<',';
    }
    cout<<"\n\n";
    for(int i = 0; i < n; ++i){
        cout<<random(1,m+4)<<',';
    }
    cout<<"\n\n";
    for(int i = 0; i < x; ++i){
        cout<<random(1,3)<<',';
    }
     for(int i = 0; i < n-x; ++i){
        cout<<random(4,6)<<',';
    }
    cout<<"\n\n";
    for(int i = 0; i < m; ++i){
        cout<<random(65,120)<<',';
    }
    cout<<"\n\n";
    for(int i = 0; i < m; ++i){
        cout<<1<<',';
    }
    return 0;
}
