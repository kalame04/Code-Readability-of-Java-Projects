/*-------------------------------------------------------------
 * OBJECT ORIENTED MODELLING
 * ASSIGNMENT 2: CALCULATING THE READABILITY OF A METHOD
 * AUTHOR: AMER KALEEMULLAH
 * DATE: APRIL 13, 2015
 *-------------------------------------------------------------*/


package methRead;

import static java.lang.Math.abs;

import java.util.*;

public class CalculateReadability 
{
	
	private String funcName;
	private String[] funcList;
	private double[] concreteness;
	private String[] words;
	private String[] variables;
	private int[] decVar;
	private List<List<Integer>> usedVar;
       
	public CalculateReadability(String funcName,String[] funcList,double[] concreteness,
			String[] words,String[] vars,int[] decVar,List<List<Integer>> usedVar){
		
		this.funcName = funcName;
		this.funcList = funcList;
		this.concreteness = concreteness;
		this.words = words;
		this.variables = vars;
		this.decVar = decVar;
		this.usedVar = usedVar;
		
	}


/*-------------------------------------------------------------
 * THIS METHOD CALCULATES THE READABILITY BASED ON V,L,I
 * V -> variable
 * L -> where the variable was used
 * I -> where the variable was declared
 *-------------------------------------------------------------*/
	public String funcBodyReadability(String func_name, String[] func_list, double[] concreteness, String[] word, 
									  String[] var_name, int[] var_dec, List<List<Integer>> var_use)
	{
	
	    int num = 0;
	    double readability = 0.0;
	
	    for(int i=0;i<500;i++)
	    {
	
	        if(func_list[i].equals(func_name))
	        {
	            int reading_dist = 0;
	
	            for(int j = 0; j < var_use.get(i).size(); j++) //WHAT LINE CONTAINS V
	            {               
	                reading_dist+= abs(var_dec[i] - var_use.get(i).get(j));
	            }
	
	            double con = getConcretness(concreteness, word, var_name[i]);    //EVALUATE THE CONCRETENESS OF V
	
	            if(var_use.get(i).isEmpty())
	            {  
	                reading_dist+= var_dec[i];
	            }
	            
	            num++;
	            readability+= con / reading_dist;  //EVALUATE THE READABILITY OF V
	       }
	   }
	    
		double meth_readability = readability/num;     //EVALUATE THE READABILITY OF THE METHOD
		
		return String.valueOf(meth_readability);
	}
	
	/*-------------------------------------------------------------
	 * THIS METHOD READS THE TEXT FILE: jasperReport2_04.txt
	 *-------------------------------------------------------------*/
	
	public double getConcretness(double[] concreteness,String[] word,String variable )
	{
	
	    for(int i=0;i<39500;i++)
	    {
	        if(word[i].equals(variable))
	        {
	            return concreteness[i];    
	        }
	    }
	    
	    return 0;       
	    
	}

}
