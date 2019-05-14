package khmtk60.miniprojects.G10.custom.functions.conditionalsum;

import khmtk60.miniprojects.G10.localsearch.model.AbstractInvariant;
import khmtk60.miniprojects.G10.custom.model.IFunctionD;
import khmtk60.miniprojects.G10.localsearch.model.LocalSearchManager;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;

public class ConditionalSumD extends AbstractInvariant implements IFunctionD {

    private IFunctionD _f;
    private LocalSearchManager _ls = null;

    //Custom code
    public ConditionalSumD(VarIntLS[] x, double[] w, int val){
        _f = new ConditionalSumVarIntD(x,w,val);
    }
    // end of khmtk60.miniprojects.G10.custom code
    void post() {
    }

    public String name(){
        return "ConditionalSumD";
    }
    @Override
    public double getMinValue() {
        // TODO Auto-generated method stub
        return _f.getMinValue();
    }

    @Override
    public double getMaxValue() {
        // TODO Auto-generated method stub
        return _f.getMaxValue();
    }

    @Override
    public double getValue() {
        // TODO Auto-generated method stub
        return _f.getValue();
    }

    @Override
    public VarIntLS[] getVariables() {
        return _f.getVariables();
    }

    @Override
    public double getAssignDelta(VarIntLS x, int val) {
        // TODO Auto-generated method stub
        return _f.getAssignDelta(x,val);
    }

    @Override
    public double getSwapDelta(VarIntLS x, VarIntLS y) {
        // TODO Auto-generated method stub
        return _f.getSwapDelta(x,y);
    }

    @Override
    public void propagateInt(VarIntLS x, int val) {
        // DO NOTHING
    }

    @Override
    public void initPropagate() {
        // DO NOTHING

    }

    @Override
    public boolean verify() {
        return _f.verify();
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        // TODO Auto-generated method stub
        return _f.getLocalSearchManager();
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
//        // TODO Auto-generated method stub
//
//        LocalSearchManager ls = new LocalSearchManager();
//        VarIntLS[] x = new VarIntLS[1000];
//        for (int i = 0; i < x.length; i++) {
//            x[i] = new VarIntLS(ls, 0, 10000);
//            x[i].setValue(i);
//        }
//        //x[0].setValue(2);
//        for (int i = 0; i < 500; i++) {
//            x[i].setValue(2);
//        }
//
//        IFunctionD[] cf = new IFunctionD[x.length];
//        for(int i=0;i<cf.length;i++)
//        {
//            cf[i]=new FuncPlus(x[i], 1);
//        }
//        IFunctionD[] w1=new IFunctionD[x.length];
//        for (int i = 0; i < w1.length; i++) {
//            w1[i] = new FuncVarConst(ls,10);
//        }
//        IFunctionD val = new FuncVarConst(ls,3);
//        ConditionalSumD s = new ConditionalSumD(cf, w1, val);
//
//        ls.close();
//
//        khmtk60.miniprojects.G10.localsearch.applications.Test T = new khmtk60.miniprojects.G10.localsearch.applications.Test();
//        T.test(s,100000);

		/*
		System.out.println(s.getValue());

		int oldv = s.getValue();
		int dem = 0;
		for (int i = 0; i < 100000; i++) {
			int r1 = (int) (Math.random() * 1000);
			System.out.println("r1      =  " + r1);
			int r2 = (int) (Math.random() * 3);

			System.out.println("r2      =  " + r2);
			int dv = s.getAssignDelta(x[r1], r2);

			System.out.println("s.get   =   " + dv);
			x[r1].setValuePropagate(r2);
			int dd = s.getValue();

			System.out.println("value   =   " + dd);
			if (dd == dv + oldv) {
				oldv = dd;
				dem++;
			} else {
				System.out.println("ERROR");
				break;
			}
			System.out.println("------------------------------------------");

		}
		System.out.println("dem =  " + dem);
        */
    }

}