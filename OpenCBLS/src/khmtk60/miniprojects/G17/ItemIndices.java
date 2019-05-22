package khmtk60.miniprojects.G17;

public class ItemIndices {
	public int bins;
	public int[] itemsIndices;

	public ItemIndices(int bins, int[] ItemIndices) {
		this.bins = bins;
		this.setItemIndices(ItemIndices);

	}

	public ItemIndices() {

	}

	public int getBins() {
		return bins;
	}

	public void setBins(int bins) {
		this.bins = bins;
	}

	public int[] getItemIndices() {
		return itemsIndices;
	}

	public void setItemIndices(int[] itemsIndices) {
		this.itemsIndices = itemsIndices;
	}

}

