package khmtk60.miniprojects.G1.newlocalsearch.model;


public interface CustomIFloatFunction extends CustomInvariant{
    public double getMinValue();

    public double getMaxValue();

    public double getValue();

    public double getAssignDelta(CustomVarIntLS x, int val);

    public double getSwapDelta(CustomVarIntLS x, CustomVarIntLS y);
}
