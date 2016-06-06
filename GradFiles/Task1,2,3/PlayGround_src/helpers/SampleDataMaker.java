package helpers;

import java.util.Random;

public class SampleDataMaker {
	
	public SampleDataMaker(){
		
	}
	
	//////////////produces a random number between 0-9////////////////////////////////////////////////////////////////////
	public int makeInt(){
		int myInt;
		
		myInt = (int)(Math.random()*10-1);
		
		return myInt;
	}
	
	////////////produces a random char A-Z, a-z, [, \, ], ^, _, `/////////////////////////////////////////////////////////
	public char makeChar(){
		Random rand = new Random();
		
		char myChar = (char)(rand.nextInt(57)+65);
		
		return myChar;	
	}
	
	///////////produces a string made from random chars of the length given by the parameter stringLength////////////////
	public String makeString(int stringLength){
		String str = "";
		
		for(int i=0; i<stringLength; i++){
			str = str + this.makeChar();
		}
		return str;
	}
	
	/////////produces an array of random integers of the size given by the parameter arraySize ///////////////////////////
	public int[] makeIntArray(int arraySize){
		int[] myIntArray = new int[arraySize];
		
		for(int i=0; i<arraySize; i++){
			myIntArray[i] = this.makeInt();
		}
		
		return myIntArray;
	}
	
	////////produces an array of random chars of the size given by the parameter arraySize/////////////////////////////////
	public char[] makeCharArray(int arraySize){
		char[] myCharArray = new char[arraySize];
		
		for(int i=0; i<arraySize; i++){
			myCharArray[i] = this.makeChar();
		}
		
		return myCharArray;
	}
	
	/////////produces an array of random strings of the length given by the parameter stringLength of the size given by the parameter arraySize/////
	public String[] makeStringArray(int arraySize, int stringLength){
		String[] myStringArray = new String[arraySize];
		
		for(int i=0; i<arraySize; i++){
			myStringArray[i] = this.makeString(stringLength);
		}
		
		return myStringArray;
	}
	

}
