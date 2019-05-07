package model;

import java.util.ArrayList;

public class Event {
	boolean typeEdit;
	boolean layerEdit;
	ArrayList<Integer> Change;
	int wait;
	
	public Event() {
		Change = new ArrayList<Integer>();
	}
	
	public void clear() {
		Change.clear();
		typeEdit = false;
		layerEdit = false;
		wait = 0;
	}
}
