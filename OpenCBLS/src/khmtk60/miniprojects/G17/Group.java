package khmtk60.miniprojects.G17;

public class Group {
	private int t;
	private int r;
	private int[] items;

	public Group(int t, int r, int[] items) {
		super();
		this.setT(t);
		this.setR(r);
		this.setItems(items);
	}

	public int getT() {
		return t;
	}

	public void setT(int t) {
		this.t = t;
	}

	public int getR() {
		return r;
	}

	public void setR(int r) {
		this.r = r;
	}

	public int[] getItems() {
		return items;
	}

	public void setItems(int[] items) {
		this.items = items;
	}
}