package pkg.Task_1_2_3;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/*--------------------------------------------------------------------------------------------------		
 * 		FINAL EXAM PROJECT
 * 		Author: Amer Kaleemullah
 *  	Course: Object Oriented Modeling
 *  
 *  	TASK 1: FILE VISITOR PATTERN: Visit folders and list files with .java extension and write to text file
 *--------------------------------------------------------------------------------------------------
 */

class ListFiles extends SimpleFileVisitor<Path> 
{
	List<Path> javaFiles;
	
	public ListFiles() 
	{
		javaFiles = new ArrayList<Path>();
    }

/*--------------------------------------------------------------------------------------------------		
 * 		OVERRIDE FUNCTION:
 *  		Visit File - Pass folder path and folder contents.
 *--------------------------------------------------------------------------------------------------
 */@Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) 
 {
	    if (file.toString().endsWith(".java"))//If the file in the folder has the extension .java
	    {
	    	javaFiles.add(file);//Add the file to the list of javaFiles
	    }
	 
	    return FileVisitResult.CONTINUE;
  }
 
 /*--------------------------------------------------------------------------------------------------		
  * 		OVERRIDE FUNCTION:
  *  		Failed File Visit
  *--------------------------------------------------------------------------------------------------
  */@Override
 public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException 
 {
	   	return super.visitFileFailed(file, exc);
 }
  
 /*--------------------------------------------------------------------------------------------------		
  * 		OVERRIDE FUNCTION:
  *  		Visit Directory
  *--------------------------------------------------------------------------------------------------
  */@Override
  public FileVisitResult preVisitDirectory(Path directory, BasicFileAttributes attributes) throws IOException 
  {
	  	return FileVisitResult.CONTINUE;
  }
   
}

public class Task1 
{
  /*--------------------------------------------------------------------------------------------------		
   * 	Method to create text file
   *--------------------------------------------------------------------------------------------------
   */
	public void createFile(String filename, String content)
	{
		try 
		{
			File file = new File("C:\\JavaBook\\PlayGround\\PlayGround_src_task1_" + filename); 
			System.out.println(content);
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			
		    FileOutputStream s = new FileOutputStream(file);
	        s.write(content.getBytes());
	        s.flush();
	        s.close();
		}
		
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

/*--------------------------------------------------------------------------------------------------		
 * 	Main Function, filepath -> textWriter -> Folder Path with .Java contents in every folder
 *--------------------------------------------------------------------------------------------------
 */
  public static void main(String[] args) 
  {
    try 
    {
      Path path = Paths.get("C:\\JavaBook\\PlayGround\\");//Enter the folder path for task 1
      ListFiles listFiles = new ListFiles();//List of Files
      Files.walkFileTree(path, listFiles);//File tree Walker
      int size = listFiles.javaFiles.size();
      
      if (size > 0)
      {
    	  String fW = "Path  \t                File Name\n";//File Writer - Write Header
      	  
    	  for (int i = 0; i < size; i++)
      	  {
      		  Path file = listFiles.javaFiles.get(i);
      		  String pa = file.toFile().getAbsolutePath();
      		  String currentDir = "C:\\JavaBook\\PlayGround\\";
      		  
      		  //File Writer: Write the path of the current directory and the all the java files in it
      		  fW += pa.substring(0, pa.lastIndexOf(File.separator)).replace(currentDir, "") + "\t" + file.getFileName() + "\n";
      	  }
    	  
    	  Task1 execute = new Task1();
    	  execute.createFile(size + ".txt", fW); 
      }
      
    } 
    
    catch (IOException e) 
    {
      e.printStackTrace();
    }
    
  }
  
}
