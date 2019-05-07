package khmtk60.miniprojects.G8;

import khmtk60.miniprojects.G8.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.G8.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.G8.model.MinMaxTypeMultiKnapsackInputItem;

public class TestData {
	MinMaxTypeMultiKnapsackInput input;
	public TestData(String fn){
		this.input = MinMaxTypeMultiKnapsackInput.loadFromFile(fn);
	}
	
	public static void main(String[] args){
		TestData test = new TestData("E:\\TaiLieuHocTap\\LocalSearch\\MiniProject\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-1000.json");
		MinMaxTypeMultiKnapsackInputItem[] items;
		MinMaxTypeMultiKnapsackInputBin[] bins;
		items = test.input.getItems();
		bins = test.input.getBins();
		
		for(int i =0; i< items.length; i++){
			System.out.println("tem"+i + "-w: "+ items[i].getW()+"-p: "+items[i].getP()+"-t: "+items[i].getT()+"-r: "+items[i].getR());
		}
		System.out.println("bin"+ bins.length);
	/*	System.out.println("/////////////////////////////////////////////////////////////////");
		for(int i =0; i<bins.length;i++){
			System.out.println("bin"+i+1+"-W: "+bins[i].getCapacity()+"-LW"+bins[i].getMinLoad()+"-P: "+bins[i].getP()+"-T: "+bins[i].getT()+"-R: "+bins[i].getR());
		}*/
		
	}
}
