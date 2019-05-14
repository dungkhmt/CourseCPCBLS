package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.model;

import java.util.ArrayList;

public class Element {
	double violation;
	ArrayList<Integer> element;
	public Element() {
		
	}
	public Element(double violation, ArrayList<Integer> element) {
		super();
		this.violation = violation; 
		this.element = element;
	}
	public double getViolation() {
		return violation;
	}
	public void setViolation(double violation) {
		this.violation = violation;
	}
	public ArrayList<Integer> getElement() {
		return element;
	}
	public void setElement(ArrayList<Integer> element) {
		this.element = element;
	}
	
}
