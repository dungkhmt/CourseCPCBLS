package khmtk60.miniprojects.G17;

public class Bin {
	private double capacity;
	private double minLoad;
	private double p;
	private int t;
	private int r;
	private int idx;
	
	double _w;
	double _p;
	SetCount _t;
	SetCount _r;

	public double violations() {
		double vio = 0;

		vio += Math.max(0, _w - capacity);
		vio += Math.max(0, _p - p);
		vio += Math.max(0, _t.size() - t);
		vio += Math.max(0, _r.size() - r);
		vio += Math.max(0, minLoad - _w);

		// if (_w < minLoad)
		// vio += 0.5;

		return vio;
	}

	public void addItem(Item item) {
		_w += item.getW();
		_p += item.getP();
		_t.add(item.getT());
		_r.add(item.getR());
		item.setAssignTo(this.idx);
		return;
	}

	public void removeItem(Item item) {
		_w -= item.getW();
		_p -= item.getP();
		_t.remove(item.getT());
		_r.remove(item.getR());
		item.setAssignTo(-1);
	}

	public double getCapacity() {
		return capacity;
	}

	public void setCapacity(double capacity) {
		this.capacity = capacity;
	}

	public double getMinLoad() {
		return minLoad;
	}

	public void setMinLoad(double minLoad) {
		this.minLoad = minLoad;
	}

	public double getP() {
		return p;
	}

	public void setP(double p) {
		this.p = p;
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

	public Bin(double capacity, double minLoad,
			double p, int t, int r, int idx) {
		super();
		this.capacity = capacity;
		this.minLoad = minLoad;
		this.p = p;
		this.t = t;
		this.r = r;

		this._w = 0;
		this._p = 0;
		this._t = new SetCount();
		this._r = new SetCount();
		this.idx = idx;
	}


	public int getIdx() {
		return idx;
	}


	public void setIdx(int idx) {
		this.idx = idx;
	}
}

