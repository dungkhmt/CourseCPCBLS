package khmtk60.miniprojects.G1.newlocalsearch.model;

public interface CustomIconstraint extends CustomInvariant{
	public double violations();
	public double violations(CustomVarIntLS x);
	public double getAssignDelta(CustomVarIntLS x, int val);
	public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y);
}
