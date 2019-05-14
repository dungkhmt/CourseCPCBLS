package khmtk60.miniprojects.G17;

public class Decompose {
	Group[] groups;

	public int[] decompose(int[] binOfItems) {

		int total = 0;
		for(Group g: groups) 
			total += g.getItems().length;
		
		int[] decomposedBinOfItems = new int[total];

		for (int i = 0; i < binOfItems.length; ++i)
			for(int itemIndex: groups[i].getItems())
				decomposedBinOfItems[itemIndex] = binOfItems[i];

		return decomposedBinOfItems;
	}


	public static void main(String[] args) {

	}

}
