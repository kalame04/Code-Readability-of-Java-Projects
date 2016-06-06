package classConc;

import java.util.ArrayList;
import java.util.List;

public class MethodWeightage {
	
	public MethodWeightage(){
		
	}
	
	public List magicalListGetter() {
	    List list = new ArrayList();

	    return list;
	}
	
	public int[] add(int[] x, int[] y){
		int[] a = x;
		int[] b = y;
		int sum[] = {0,0};
		
		for (int j = 0; j < a.length; j++){
			
			sum[0] += a[j];
		}
		
		
		for (int c: b){
			sum[1] += c;
		}
		return sum;
	}

}
