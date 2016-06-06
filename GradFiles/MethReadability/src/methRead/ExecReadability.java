/*-------------------------------------------------------------
 * OBJECT ORIENTED MODELLING
 * ASSIGNMENT 2: CALCULATING THE READABILITY OF A METHOD
 * AUTHOR: AMER KALEEMULLAH
 * DATE: APRIL 13, 2015
 *-------------------------------------------------------------*/

package methRead;

import java.io.File;
//import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExecReadability 
{	
	private static int lines = 40000;
	private static int lineBuff = 33558;
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException 
	{

	   File jasperFile = new File("C:/Users/Amer/Desktop/Graduate Final Project/Variables Text Files/jfreechart-1.0.19.txt");    
	   File concretenessFile = new File("Concreteness_ratings_Brysbaert_et_al_BRM.txt");
	   
	   //InputStream file1 = new FileInputStream(jasperFile);
	   //InputStream file2 = new FileInputStream(jasperFile);

	   String[] word = new String[lines];
	   String[] dom_pos = new String[lines];
	   Scanner JFile = new Scanner(jasperFile);
	   Scanner CFile = new Scanner(concretenessFile);
       //int n_source1, n_source2;
       int num = -2;
       
       String functionName = null;
	   String[] functionList = null;
	   double[] concreteness = null;
	   String[] words = null;
	   String[] variables = null;
	   int[] declerationVar = null;
	   List<List<Integer>> usedVar = null;
	   CalculateReadability r = new CalculateReadability(functionName,functionList,concreteness,words,variables,declerationVar,usedVar);

       double[] conc_m = new double[lines];
       double[] conc_sd = new double[lines];
       double[] percent_known = new double[lines];

       int[] bigram = new int[lines];
       int[] unknown = new int[lines];
       int[] total = new int[lines];
       int[] subtlex = new int[lines];

       String[] PackageName = new String[lineBuff];
       String[] ClassName = new String[lineBuff];
       String[] Methods = new String[lineBuff];
       String[] Type = new String[lineBuff];
       String[] Variable = new String[lineBuff];
       int[] DeclaredVariables = new int[lineBuff];
       List<List<Integer> > List = new ArrayList<List<Integer>>();
       
       /*-------------------------------------------------------------
	    * JASPER FILE ITERATOR
	    *-------------------------------------------------------------*/
       for(int i = 0; i < 33558; i++)
       {
    	   List.add(new ArrayList<Integer>());
	   }
       
       num = -1;

	   while(JFile.hasNextLine())
	   {
		   
		   String line = JFile.nextLine();
	       num++;
	
	       Scanner lineSplitter = new Scanner(line);    
	       String content;
	       content = lineSplitter.nextLine();
	       String[] split = content.split("\t");
	
	       PackageName[num] = split[0];
	       ClassName[num] = split[1];
	       Methods[num] = split[2];
	       Type[num] = split[3];
	       Variable[num] = split[4];
	
	       String temp =  split[5].replaceAll(",", "");
	       temp = temp.replace("[", " ");
	       temp = temp.replaceAll("]", " ");
	       Scanner for_temp = new Scanner(temp);
	
	       DeclaredVariables[num] = for_temp.nextInt();
	
	       while(for_temp.hasNextInt())
	       {
	    	   List.get(num).add(for_temp.nextInt());
	       }
	       
	       lineSplitter.close();
	       for_temp.close();
	       
	    }
	   JFile.close();
	   
	   
	   /*-------------------------------------------------------------
	    * CONCRETENESS FILE ITERATOR
	    *-------------------------------------------------------------*/
	   
      // n_source1 = num + 1;
       num = -2;
       
       while(CFile.hasNextLine())
       {
	    	String line = CFile.nextLine();
	    	num ++;
	
	        if(num == -1)   
	        	continue; //SKIP FIRST LINE
	
	        Scanner lineBreaker = new Scanner(line);    
	        String content;
	        content = lineBreaker.nextLine();
	        String[] split = content.split("\t", 9);
	
	        word[num] = split[0];
	        bigram[num] = Integer.parseInt(split[1]);
	        conc_m[num] = Double.parseDouble(split[2]);
	        conc_sd[num] = Double.parseDouble(split[3]);
	        unknown[num] = Integer.parseInt(split[4]);
	        total[num] = Integer.parseInt(split[5]);
	        percent_known[num] = Double.parseDouble(split[6]);
	        subtlex[num] = Integer.parseInt(split[7]);
	        dom_pos[num] = split[8];
	        
	        lineBreaker.close();
	        
        }
       
       CFile.close();
       
        //n_source2 = num;


        /*-------------------------------------------------------------
         * FILE WRITER
         *-------------------------------------------------------------*/
        
        File WriteToFile = new File("C:/Users/Amer/Desktop/Graduate Final Project/Output/Method Readability/jfreechart-1.0.19.txt");    
        FileOutputStream FOS = new FileOutputStream(WriteToFile);
        PrintWriter PrintTo = new PrintWriter(FOS);

        PrintTo.println("Package_Name" + "\t" + "Class_Name" + "\t" + "Function_Name" + "\t" + "Func_Readability");
        
        String[] hash = new String [33558];
        
        for(int i = 0; i < 33558; i++)
        {
            hash[i] = "";
        }
        
        int hash_number = 0;
        
        for(int i = 0; i<33558;i++)
        {
           int flag = 0;
           
           for(int j = 0; j<hash_number; j++)
           {
               if(Methods[i].equals(hash[j]) ) //TO AVOID REDUNDANCY OF CALCULATING METH_READABILITY
               {
                   flag=1;
                   break;
               }
           }
           
           if( flag == 0)
           {
	            hash[hash_number] = Methods[i];
	            hash_number ++;        		    
	
	            PrintTo.println(PackageName[i] + "\t"+ClassName[i] + "\t" + Methods[i] + "\t" 
	            + r.funcBodyReadability(Methods[i],Methods,conc_sd,word,Variable,DeclaredVariables,List));
           }
           
        }
        
        PrintTo.flush();
        FOS.close();
        PrintTo.close();
        
        System.out.print("APPLICATION ENDED, FILE CREATED IN WORKSPACE");

	}
}


