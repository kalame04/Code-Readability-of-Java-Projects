package classSatellite;

public class javaClass3 {
	public javaClass3(){
		
		javaClass w = new javaClass();
		
	}
	
	/*////this prints a string from any class that has access to helpers
	with the simple command h.ps(string)//////////////////////////////*/
	public static void ps(String str){
		System.out.println(str);
	}
	
	/*////this prints an int array from any class that has access to helpers
	with the simple command h.pia(intArray)///////////////////////////////*/
	public static void pia(int[] intArray){
		String str = "";
		
		for(int i=0; i<intArray.length; i++){
			str = str + intArray[i] + ", ";
		}
		
		System.out.println(str);
	}
	
	/*////this prints a char array from any class that has access to helpers
	with the simple command h.pca(charArray)//////////////////////////////*/
	public static void pca(char[] charArray){
		String str = "";
		
		for(int i=0; i<charArray.length; i++){
			str = str + charArray[i] + ", ";
		}
		
		System.out.println(str);
	}
}
