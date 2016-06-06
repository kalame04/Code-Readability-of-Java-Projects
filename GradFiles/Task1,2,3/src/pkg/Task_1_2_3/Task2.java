package pkg.Task_1_2_3;
import org.Antlr4.JavaLexer;
import org.Antlr4.JavaParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/*--------------------------------------------------------------------------------------------------		
 * 		FINAL EXAM PROJECT
 * 		Author: Amer Kaleemullah
 *  	Course: Object Oriented Modeling
 *  
 *  	TASK 2: Information Extraction, Package name, Class Name, Method Name, Variable Type, Variable Name 
 *--------------------------------------------------------------------------------------------------
 */
public class Task2 
{
	public static void createFile(String filename, String content)
	{
		try 
		{
			File txtFile = new File("C:\\JavaBook\\PlayGround\\PlayGround_src_task2_" + filename);
			
			if(!txtFile.exists())
			{
				txtFile.createNewFile();
    		}
	        
	        FileOutputStream fW = new FileOutputStream(txtFile);
	        
	        for (String content1 : CustomJavaListener.extract_data)
	        	fW.write((content1).getBytes());
	        
	        fW.flush();
	        fW.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception 
    {
		ListFiles files = new ListFiles();
		String workingDir = System.getProperty("user.dir") + "/Playground_src";
		Path file = Paths.get(workingDir);
		Files.walkFileTree(file, files);
		 
		for(int i = 0; i < files.javaFiles.size(); i++)
		{	
	        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(files.javaFiles.get(i).toFile())); 
	        JavaLexer lexer = new JavaLexer(input); 
	        CommonTokenStream tokens = new CommonTokenStream(lexer); 
	        JavaParser parser = new JavaParser(tokens); 
	        ParseTree tree = parser.compilationUnit();
	
	        CustomJavaListener java_listener = new CustomJavaListener(tokens);//Declare Java Listener
	
	        ParseTreeWalker walker = new ParseTreeWalker();
	        walker.walk(java_listener, tree);
		} 
		
		createFile(CustomJavaListener.extract_data.size() + ".txt", null);
    }
}