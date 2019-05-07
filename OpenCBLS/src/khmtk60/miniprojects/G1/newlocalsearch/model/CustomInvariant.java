package khmtk60.miniprojects.G1.newlocalsearch.model;


public interface CustomInvariant {
	public CustomVarIntLS[] getVariables();
	public void propagateInt(CustomVarIntLS x, int val);
	public void initPropagate();
	public boolean verify();
}
