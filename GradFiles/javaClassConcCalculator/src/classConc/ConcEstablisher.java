package classConc;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFileChooser;

public class ConcEstablisher {
	
			
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException, IOException
	{
		JButton open = new JButton();
		JFileChooser fc = new JFileChooser();
		
		fc.setCurrentDirectory(new File(System.getProperty("user.home")));
		fc.setDialogTitle("Please Select The Caller Callee Text File: ");
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		//int result = fileChooser.showOpenDialog(parent);
		
		if(fc.showOpenDialog(open) == JFileChooser.APPROVE_OPTION){
			
		}
		//System.out.println(fc.getSelectedFile().getAbsolutePath());
		
		File reportFile = new File(fc.getSelectedFile().getAbsolutePath());    
		Scanner JFile = new Scanner(reportFile);
		Scanner LineCounter = new Scanner(reportFile);
		
		int NumberLines = 0;
		
		while(LineCounter.hasNextLine())//COUNT # LINES IN TEXT FILE
		{
			LineCounter.nextLine();
			NumberLines++;
		}
		LineCounter.close();
		
		
		String[] Package = new String[NumberLines];
		String[] Class = new String[NumberLines];
		String[] Method = new String[NumberLines];
		String[] MethodReadability = new String[NumberLines];
		String[] ClassReadability = new String[NumberLines];
		String[] CallerMethod = new String[NumberLines];
		String[] CalledMethod = new String[NumberLines];
		List<List<String>> List = new ArrayList<List<String>>();
		List<String> Meth = new ArrayList<String>();
		
		int TotalOut = 0;
		List<Integer> TotalIn = new ArrayList<Integer>();
		int[] TotalInArray = new int[NumberLines];
		
		for(int i=0; i<NumberLines; i++)
		{
			List.add(new ArrayList<String>());
			TotalIn.add(0);
		}
		
		int num = -1;

		
		while(JFile.hasNextLine())
		{
			if(num==-1)//SKIP FIRST LINE
			{
				JFile.nextLine();
				num++;
			}
			else
			{
				String line = JFile.nextLine();
				Scanner lineSplitter = new Scanner(line);    
			    String content = lineSplitter.nextLine();
			    String[] split = content.split("\t");
			    
			    String[] temp = split[0].split("\\.");
			    
		    	Package[num] = temp[0];
		    	Class[num] = temp[1];
		    	CallerMethod[num] = temp[2].replaceAll(" ", "");
		    	
		    	Meth.add(CallerMethod[num]);

			    
			    String temp1 = split[1].replaceAll(",", "");
			    temp1 = temp1.replace("[", "");
			    temp1 = temp1.replace("]", "");
			    
			    Scanner for_temp1 = new Scanner(temp1);
			    
			    while(for_temp1.hasNext())
			    {
			    	List.get(num).add(for_temp1.next());
			    }
			    for_temp1.close();
			    
			    for(int i = 0; i < List.get(num).size(); i++)
			    {
			    	if(Meth.contains(List.get(num).get(i)))
			    	{
			    		int j = Meth.indexOf(List.get(num).get(i));
			    		TotalInArray[j]++;
			    		TotalIn.set(j,TotalInArray[j]);
			    	}
			    	
			    }
			 
			    //System.out.println(Class[num] + "\t" + Meth.get(num) + "\t" + List.get(num) 
			    	//	           + "\tTotal Out Calls: " + List.get(num).size());
			    
			    TotalOut+= List.get(num).size();
	
			    lineSplitter.close();
			    
			    num++;
			}
		    

		}
		JFile.close();
		
		num = 0;
		
		for(int x: TotalIn)
		{
			if(num<Meth.size())
			{
				System.out.println(Class[num] + "\t" + Meth.get(num) + "\tTotal In: " + x + "\t Total Out: " + List.get(num).size());
				num++;
			}
		}
		
		System.out.println("\nDone!\nTotal Code Calls: " +  TotalOut);
		System.out.println("Input file line count: " + NumberLines);
		
		
		
		num = 0;
		int run = 1;
		int[] ClassTotal = new int[NumberLines];
		ClassTotal[0] = List.get(num).size() + TotalIn.get(num);
		
		for(int i = 0; i<Meth.size(); i++)
		{
			if(Class[num]==Class[i])
			{
				ClassTotal[num]+= List.get(i).size() + TotalIn.get(i);
			}
			else
			{
				//System.out.println("Class Name: " + Class[num] + "\t Total In & Outs: " + ClassTotal[num]);
				num = i;
			}
		}
		
		
		
		
	}
			

}
