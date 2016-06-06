package pkg.Task_1_2_3;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.MethodCallExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

/*--------------------------------------------------------------------------------------------------		
 * 		FINAL EXAM PROJECT
 * 		Author: Amer Kaleemullah
 *  	Course: Object Oriented Modeling
 *  
 *  	TASK 3: Information Extraction, Method Caller and Method Callee
 *--------------------------------------------------------------------------------------------------
 */

public class Task3 
{

	//private static HashMap<String,String> methods = new LinkedHashMap<String,String>();
	private static HashMap<String,Set> methods = new LinkedHashMap<String,Set>();
	
	static class ClassVisitor extends VoidVisitorAdapter<Object> 
	{
		private static String className = "";
		
		public void visit(japa.parser.ast.body.ClassOrInterfaceDeclaration n, Object arg) 
		{
			className = arg + "." + n.getName();
			new MethodVisitor().visit(n, className);
		}

	}

	static class MethodVisitor extends VoidVisitorAdapter<Object> {
		private static String methodName = "";
		
		@Override
		public void visit(MethodDeclaration n, Object arg) 
		{
			methodName = arg + "." + n.getName();
			new MethodCallVisitor().visit(n,methodName);
		}
		
	
	}

	static class MethodCallVisitor extends VoidVisitorAdapter<Object>{
		
		@Override
		public void visit(MethodCallExpr n, Object arg) 
		{
	          //String className=arg.toString();    
	          //methods.put(arg.toString(),className.substring(0,className.lastIndexOf(','))+","+n.getName());
			String className = arg.toString();  
			Set<String> set = methods.get(arg.toString());
			  
			  if (set == null){
				  set = new HashSet<String>();
				  methods.put(arg.toString(), set);
			  }
			  
			  set.add(className.substring(0, className.lastIndexOf('.')) + "." + n.getName());
			  //set.add(n.getName());
		}
	  
	
	}
	
	public static void main(String[] args) 
	{
		try 
		{
			ListFiles files = new ListFiles();
			String projPath = "C:\\JavaBook\\PlayGround\\";
			Path file = Paths.get(projPath);
			Files.walkFileTree(file, files);
			CompilationUnit compilationUnit = null;
			FileInputStream fileInputStream = null;
			String pkg = "";

			ClassVisitor codeVisitor = null;

			for (Path path : files.javaFiles) 
			{
				fileInputStream = new FileInputStream(path.toFile());
				
				try 
				{
					compilationUnit = JavaParser.parse(fileInputStream);
				} 
				
				finally 
				{
					fileInputStream.close();
				}
				
				pkg = compilationUnit.getPackage().getName().toString();
				codeVisitor = new ClassVisitor();
				codeVisitor.visit(compilationUnit, pkg);

			}

			File ouputFile = new File("C:\\JavaBook\\PlayGround\\PlayGround_src_task3_" + methods.size() + ".txt");
			FileWriter fW = new FileWriter(ouputFile);
			
			fW.write("Caller Method" + " \t " + "Callee Method\n");
			
			for (String callerMeth : methods.keySet()) 
			{
				fW.write(callerMeth + " \t "+ methods.get(callerMeth)+"\n");
				System.out.println(callerMeth + " \t " + methods.get(callerMeth)+"\n");
			}
			
			fW.close();

		} 
		
		catch (Exception ex) 
		{
			System.out.println("Exception in ProjectInfo " + ex.getMessage());
		}

	}

}
