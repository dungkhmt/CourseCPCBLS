package model;

import java.util.ArrayList;

public class MiniProject {
	Bin bins[];
	Item items[];
	int M,N,R,T;
	int kickoff;
	int bestValue;
	int solution[];
	int wrItem;
	long time;
	long distime = 50000;
	private boolean X[][];
	private boolean t[][];
	private boolean r[][];
	
	public void loadData(String fn) {
		MinMaxTypeMultiKnapsackInput input = new MinMaxTypeMultiKnapsackInput();
		input = input.loadFromFile(fn);
		MinMaxTypeMultiKnapsackInputBin bins[] = input.getBins();
		MinMaxTypeMultiKnapsackInputItem items[] = input.getItems();
		
		this.M = bins.length;
		this.N = items.length;
		this.T = 0;
		this.R = 0;
		
		this.bins = new Bin[M];
		this.items = new Item[N];
		
		for(int i = 0;i<M;i++) {
			this.bins[i] = new Bin(bins[i],i);
		}
		
		for(int i = 0; i<N;i++) {
			this.items[i] = new Item(items[i]);
			this.items[i].id = i;
			if(items[i].getT()>T)
				T = items[i].getT();
			if(items[i].getR()>R)
				R = items[i].getR();
		}
		

		R++;
		T++;
		this.X = new boolean[M][N];
		this.r = new boolean[M][R];
		this.t = new boolean[M][T];
		
		for(int i = 0; i<M; i++) {
			for(int j = 0;j<N;j++) {
				this.X[i][j] = false;
			}
		}
		
		for(int j = 0;j<N;j++) {
			for(int i: items[j].getBinIndices() ) {
				this.X[i][j] = true;
				this.bins[i].sumW += this.items[j].w;
			}
		}
		
		for(int i = 0; i<M; i++) {
			for(int j = 0;j<R;j++) {
				this.r[i][j] = false;
			}
		}
		
		for(int i = 0; i<M; i++) {
			for(int j = 0;j<T;j++) {
				this.t[i][j] = false;
			}
		}
	
	}
	
	public void initState() {
		this.kickoff = 0;
		this.bestValue = 0;
		this.solution = new int[N];
		for(int i = 0;i<N;i++)
			solution[i] = -1;
//		waitBin = 0;
		wrItem = 0;
//		for(int i=0;i<M;i++)
//			for(int j =0;j<N;j++)
//				if(X[i][j] && (bins[i].P < items[j].p || bins[i].W < items[j].w)) {
//					X[i][j] = false;
//					bins[i].sumW -= items[j].w;
//					items[j].D --;
//					if(items[j].D ==0 )
//						this.kickoff ++;
//				}
//		
		for(int i =0;i<M;i++) {
			
			
			for(int j=0;j<N;j++) {
				if(X[i][j] && (bins[i].P < items[j].p || bins[i].W < items[j].w)) {
					X[i][j] = false;
					bins[i].sumW -= items[j].w;
					items[j].D --;
				}
			}
			
			if(bins[i].LW<=bins[i].sumW)
				continue;
			for(int j =0;j<N;j++)
				if(X[i][j]) {
					X[i][j] = false;
					items[j].D--;
				}
		}
		//for(int i =0;i<M;i++)
	//		System.out.println("-lw"+bins[i].LW+"w"+bins[i].w+"sw"+bins[i].sumW);
		//for(int i =0;i<N;i++)
		//	System.out.print(items[i].D+" ");
		System.out.printf("\n kickoff = %d, wr = %d, obj=%d\n",this.kickoff,this.wrItem,this.bestValue);
		time = System.currentTimeMillis();
		
	}
	
	public void search(int i) throws InterruptedException {
		if(i>=N) {
			saveSolution();
			return;
		}
		if(System.currentTimeMillis() - time > this.distime) return;
		if(bestValue >= N - kickoff) return;
		
		int domain[] = getDomain(i);
		Event event = new Event();
		if(domain!=null)
			for(int j:domain) {
				//System.out.println(i+"="+j);
				if(putItem(i,j,event))
					search(i+1);
				redo(i,j,event);
			}
		//System.out.println("kickOut"+i);
		items[i].bin = -1;
		kickoff ++;
		//System.out.println("kickoff"+i);
		search(i+1);
		setDomain(i,domain);
		kickoff --;
	}
	
	public int[] getDomain(int j) {
		if(items[j].D ==0)
			return null;
		int domain[] = new int[items[j].D];
		int idx = 0;
		
		for(int i = 0;i<M;i++)
			if(X[i][j]) {
				X[i][j] = false;
				domain[idx] = i;
				bins[i].sumW -= items[j].w;
				items[j].D --;
				idx++;
			}
		
		
		
		return domain;
	}
	
	public void setDomain(int j,int domain[]) {
		for(int i:domain) {
			X[i][j] = true;
			bins[i].sumW += items[j].w;
			items[j].D ++;
		}
	}
	
	public void redo(int itemId,int binId,Event event) {
		Item item = items[itemId];
		Bin bin = bins[binId];
		bin.w -= item.w;
		bin.p -= item.p;
		bin.item--;
		
		item.bin = -1;
		if(event.layerEdit) {
			r[binId][item.r] = false;
			bin.R++;
		}
		
		if(event.typeEdit) {
			t[binId][item.t] = false;
			bin.T++;
		}
		
		for(int j: event.Change) {
//			if(items[j].D == 0) {
//				kickoff --;
//				System.out.println("not off");
//			}
			X[binId][j] = true;
			bin.sumW += items[j].w;
			items[j].D ++;
		}
		
		if(bin.wait)
			wrItem--;
		
		if(bin.LW <= bin.w) {
			bin.wait = false;
		}
		else 
			bin.wait = true;
		
		if(event.wait==-1) 
			this.wrItem += bin.item;
		
//		this.waitBin -= event.wait;
	}
	
	public boolean putItem(int itemId,int binId, Event event) {
		event.clear();
		
		Item item = items[itemId];
		Bin bin = bins[binId];
		item.bin=binId;
		
		if(!r[binId][item.r])
			event.layerEdit = true;
		if(!t[binId][item.t])
			event.typeEdit = true;
		
		bin.w += item.w;
		bin.p += item.p;
		
		if(event.typeEdit)
			bin.T --;
		if(event.layerEdit)
			bin.R --;
		
		r[binId][item.r] = true;
		t[binId][item.t] = true;
		
		for(int i = itemId + 1;i<N;i++) {
			if(X[binId][i]) {
				if(bin.T==0 && !t[binId][items[i].t]) {
					X[binId][i] = false;
					event.Change.add(i);
					bin.sumW -= items[i].w;
					items[i].D --;
				} else
				if(bin.R ==0 && !r[binId][items[i].r]) {
					X[binId][i] = false;
					event.Change.add(i);
					bin.sumW -= items[i].w;
					items[i].D --;
				}else
				if(bin.W < bin.w + items[i].w || bin.P < bin.p + items[i].p) {
					X[binId][i] = false;
					event.Change.add(i);
					bin.sumW -= items[i].w;
					items[i].D --;
				}
//				if(items[i].D == 0) {
//					this.kickoff ++;
//					System.out.println("kickoff");
//				}
			}
		}
		
		boolean wait = true;
		if(bin.LW <= bin.w)
			wait = false;
		
		if(bin.wait!=wait) {
			if(wait) {
//				this.waitBin += event.wait = +1;
				event.wait = 1;
			}else {
//				this.waitBin += event.wait = -1;
				event.wait = -1;
				this.wrItem -= bin.item;
			}
			
			bin.wait = wait;
		}
		if(bin.wait)
			this.wrItem++;
		
		bin.item++;
		if(bin.LW > bin.w + bin.sumW) 
			return false;
		return true;
	}
	
	void saveSolution() {

		System.gc();
		//System.out.printf("kickoff = %d, wr = %d, obj=%d\n",this.kickoff,this.wrItem,this.bestValue);
		if(bestValue<this.N-this.kickoff-this.wrItem) {
			
			bestValue = this.N-this.kickoff-this.wrItem;
			for(int i=0;i<N;i++) {
				solution[i] = items[i].bin;
				
				if(solution[i]!=-1) {
					if(bins[solution[i]].wait) {
						solution[i] = -1;
					}
				}

			}
			
			//System.out.printf("=> kickoff = %d, wr = %d, obj=%d\n",this.kickoff,this.wrItem,this.bestValue);
			time = System.currentTimeMillis();
		}
		//if(time%100==0)
		System.out.printf("\nkickoff = %d, wr = %d, obj=%d/%d\n",this.kickoff,this.wrItem,this.N-this.kickoff-this.wrItem,this.bestValue);
		
//		for(int i =0;i<N;i++)
//			System.out.println(i+" "+items[i].bin);
	}
	public void setDistTime(int time) {
		this.distime = time;
	}
	public void printSolution(String fn) {

		MinMaxTypeMultiKnapsackSolution s = new MinMaxTypeMultiKnapsackSolution(this.solution);
		s.writeToFile(fn);
	}
	
	
}
