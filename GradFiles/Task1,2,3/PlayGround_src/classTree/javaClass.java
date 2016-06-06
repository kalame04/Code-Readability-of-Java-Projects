package classTree;
import helpers.*;

import java.util.Set;
import java.util.TreeSet;

public class javaClass {

	public static void main(String[] args) {
		
		int a = 11;
		int b = 6;
        int c = minFunction(a, b);
        int d;
        
        javaClass2 s = new javaClass2();
        javaClass3 t = new javaClass3();
        
        
        System.out.println("Minimum Value = " + c);
        c = Math.max(a, b);
        h.ps("Maximum Value = " + c);
        
        Set<Integer> set = new TreeSet<Integer>();
        
        set.add(11);
        set.add(b);
        
        System.out.println(set);
	}

	/** returns the minimum of two numbers */
	public static int minFunction(int n1, int n2) {
		int min;
		if (n1 > n2)
			min = n2;
		else
			min = n1;
		return min;
	}
	
	/** returns the maximum of two numbers */	
	public static int maxFunction(int n1, int n2){
		int max;   
		if (n1>n2)	   
			max = n1;
		else
			max = n2;
		return max;	   
	}

}
