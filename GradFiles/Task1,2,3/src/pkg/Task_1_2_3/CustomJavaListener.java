package pkg.Task_1_2_3;
import org.Antlr4.JavaBaseListener;

import org.Antlr4.JavaParser;
import org.Antlr4.JavaParser.ClassBodyContext;
import org.Antlr4.JavaParser.ClassDeclarationContext;
import org.Antlr4.JavaParser.ConstructorBodyContext;
import org.Antlr4.JavaParser.MethodBodyContext;
import org.Antlr4.JavaParser.MethodDeclarationContext;
import org.Antlr4.JavaParser.PackageDeclarationContext;
import org.Antlr4.JavaParser.VariableDeclaratorContext;
import org.Antlr4.JavaParser.VariableDeclaratorIdContext;
import org.Antlr4.JavaParser.VariableDeclaratorsContext;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

/*--------------------------------------------------------------------------------------------------		
 * 		FINAL EXAM PROJECT
 * 		Author: Amer Kaleemullah
 *  	Course: Object Oriented Modeling
 *  
 *  	JAVA LISTENER
 *--------------------------------------------------------------------------------------------------
 */

public class CustomJavaListener extends JavaBaseListener 
{
    
    String className, methodName = "classField", pkgName, varName, varType;
    public static ArrayList<String> extract_data = new ArrayList<String>();
    
    public CustomJavaListener(CommonTokenStream p_tokens) 
    {
    	extract_data.add("PackageName\tClassName\tMethodName\tVarType\tVarDeclName\n");//Add Header to List    
    }
    
/*--------------------------------------------------------------------------------------------------		
 * 		OVERRIDE FUNCTION:
 *  		ENTER PACKAGE DECLARATION - Every time the token lexer/parser encounters a Package 
 *  									Declaration, perform the following: 
 *--------------------------------------------------------------------------------------------------
 */ @Override
    public void enterPackageDeclaration(PackageDeclarationContext ctx) 
    {	
    	pkgName = ctx.getText().replace("package", "").replace(";", "");//Remove "package" & ";" and keep only the package name
    	System.out.println(ctx.getRuleContext().getText());  
    }
    
/*--------------------------------------------------------------------------------------------------		
 * 		OVERRIDE FUNCTION:
 *  		ENTER CLASS DECLARATION - Every time the token lexer/parser encounters a Class 
 *  									Declaration, perform the following:
 *--------------------------------------------------------------------------------------------------
 */ @Override
    public void enterClassDeclaration(ClassDeclarationContext ctx) 
    {	   		
		List<ParseTree> tree = ctx.children;//Store Class Name in tree list
		className = tree.get(1).getText();
    }
    
 /*--------------------------------------------------------------------------------------------------		
  * 		OVERRIDE FUNCTION:
  *  		ENTER METHOD DECLARATION - Every time the token lexer/parser encounters a Method 
  *  									Declaration, perform the following:
  *--------------------------------------------------------------------------------------------------
  */@Override
    public void enterMethodDeclaration(MethodDeclarationContext ctx) 
    {
    	List<ParseTree> tree = ctx.children;//Store Method Name in tree list
    	methodName = tree.get(1).getText();
    }
    
  /*--------------------------------------------------------------------------------------------------		
   * 		OVERRIDE FUNCTION:
   *  		ENTER VARIABLE DECLARATION - Every time the token lexer/parser encounters a Variable 
   *  									Declaration, perform the following:
   *--------------------------------------------------------------------------------------------------
   */@Override
    public void enterVariableDeclarators(VariableDeclaratorsContext ctx) 
    {
    	varName = ctx.getText();//Get Variable Name
    	varType = ctx.parent.getText().replace(varName, "");//Remove Variable Name from token stream and keep only the type
    	
    	if (varName.contains("="))
    	{
    		varName = varName.substring(0, varName.indexOf("="));
    	}
    
    	varName = varName.replace("[^A-Za-z0-9]", "");
    	
    	extract_data.add(pkgName + "\t" + className + "\t" + methodName + "\t" + varType + "\t" + varName + "\n");//Store to ArrayList
    	
    	System.out.println(pkgName + "\t" + className + "\t" + methodName + "\t" + varType + "\t" + varName + "\n");
    }
   }