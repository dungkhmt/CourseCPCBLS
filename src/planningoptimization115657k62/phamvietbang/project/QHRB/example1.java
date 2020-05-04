package planningoptimization115657k62.phamvietbang.project.QHRB;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class example1 {
	public static void main(String[] args) {
	Model mymodel=new Model("My model");
	IntVar[] x= new IntVar[5];
	for(int i=0;i<5;i++) {
		x[i]=mymodel.intVar("x["+i+"]",1,5);
	}
	mymodel.arithm(x[3],"<=",x[4]).post();;
	mymodel.arithm(x[4],"<=",3).post();;
	mymodel.ifThen(
			mymodel.arithm(x[2],"=",1),
			mymodel.arithm(x[4],"!=",2)
			);
	mymodel.arithm(mymodel.intOffsetView(x[2], 3),"!=",x[1]).post();
	mymodel.arithm(x[1], "+", x[4],"=",7).post();
	mymodel.arithm(x[2], "+", x[3],"=",mymodel.intOffsetView(x[1], 1)).post();
	mymodel.getSolver().solve();
	for(int i=0;i<5;i++) {
		System.out.println(x[i]);
	}
	}
	
}
