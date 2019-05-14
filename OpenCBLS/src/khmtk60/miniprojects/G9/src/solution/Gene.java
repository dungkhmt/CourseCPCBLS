package solution;

public class Gene {
	public int index;
	public int bin;
	public double weight;
	public double price;
	public int type;
	public int rank;
	
	public Gene(int index, int bin, double weight, double price, int type, int rank) {
		this.index = index;
		this.bin = bin;
		this.weight = weight;
		this.price = price;
		this.type = type;
		this.rank = rank;
	}
}
