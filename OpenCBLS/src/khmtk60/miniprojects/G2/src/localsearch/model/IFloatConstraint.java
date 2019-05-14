package khmtk60.miniprojects.G2.src.localsearch.model;

public interface IFloatConstraint extends FloatInvariant{
	public float violations();
	public float violations(VarIntLS x);
	public float getAssignDelta(VarIntLS x, int val);
	public float getSwapDelta(VarIntLS x, VarIntLS y);
}
