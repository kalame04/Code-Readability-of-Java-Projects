package classSatellite;

import java.util.ArrayList;

public class javaClass4 {
	
	private String[] strList;
	ArrayList<String> content = new ArrayList<String>();
	
	javaClass w = new javaClass();
	
	
	public ArrayList<String> splitWord(String word){
		
		if (word.indexOf("_") != -1){
		    strList = word.split("\\_");
		}
		else if (word.indexOf("-") != -1){
			strList = word.split("\\-");
		}
		else {
			strList = word.split("(?=\\p{Upper})");
		}
		
		for(String str: strList){
			content.add(str.toLowerCase());
		}
		return content;
	}

}
