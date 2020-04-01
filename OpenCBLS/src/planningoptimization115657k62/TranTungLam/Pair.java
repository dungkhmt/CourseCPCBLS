package buoi4;

public class Pair<W, H> {
	private W w;
	private H h;
	public Pair(W w, H h) {
		this.w = w;
		this.h = h;
	}
	public W getW() {
		return w;
	}
	public H getH() {
		return h;
	}
	public void setW(W w) {
		this.w = w;
	}
	public void setH(H h) {
		this.h = h;
	}
}
