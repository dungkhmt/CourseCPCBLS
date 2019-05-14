package khmtk60.miniprojects.G2.src.localsearch.model;

public interface FloatInvariant {
	public VarIntLS[] getVariables();
	public void propagateInt(VarIntLS x, int val);
	public void initPropagate();	
	public FloatLocalSearchManager getLocalSearchManager();	
	public boolean verify();
}
