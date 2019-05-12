package khmtk60.miniprojects.G10.custom.model;

import khmtk60.miniprojects.G10.localsearch.model.Invariant;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;

public interface IFunctionD extends Invariant {
    public double getMinValue();
    public double getMaxValue();
    public double getValue();
    public double getAssignDelta(VarIntLS x, int val);
    public double getSwapDelta(VarIntLS x, VarIntLS y);
}
