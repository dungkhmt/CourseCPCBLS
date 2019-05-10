package khmtk60.miniprojects.G2.src.localsearch.model;

public interface IFloatFunction extends FloatInvariant {
    public float getMinValue();

    public float getMaxValue();

    public float getValue();

    public float getAssignDelta(VarIntLS x, int val);

    public float getSwapDelta(VarIntLS x, VarIntLS y);

    public float getAssignDelta(VarIntLS x, int valx, VarIntLS y, int valy);
}
