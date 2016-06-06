package helpers;

public class Loops {
	
	SampleDataMaker samDataMak = new SampleDataMaker();
	
	public Loops(){
		
	}
	
	////////////this makes a for loop that adds the elements in a int array///////////////////////////////////////////
	public int aForLoop(int[] myIntArray){
		int sum=0;
		
		for(int i=0; i<myIntArray.length; i++){
			sum =+ myIntArray[i];
		}
		
		return sum;
	}
	
////////////this makes a while loop that keeps going until it makes an even number///////////////////////////////////
	public int aWhileLoop(){
		int lastNum=0;
		boolean isEven = false;
		
		while(isEven==false){
			lastNum = samDataMak.makeInt();
			isEven = lastNum%2 == 0 ? true : false;  //<--ternary operator
		}
		
		return lastNum;
	}
	
////////////this makes a do while loop that keeps going until it produces a number larger than the int it is given//
	public int aDoWhileLoop(int limiter){
		int lastNum;
		
		do{
			lastNum = samDataMak.makeInt();
		}while(lastNum<limiter);
		
		return lastNum;
	}
	
	
////////////this makes a switch statement that produces a random number and then returns a string describing what it created//
	public String aSwitchStatement(){
		String result;
		int randomDigit = samDataMak.makeInt();
		
		switch(randomDigit){
			case 0:
				result = "0 was randomly created";
				break;
			case 1:
				result = "1 was randomly created";
				break;
			case 2:
				result = "2 was randomly created";
				break;
			case 3:
				result = "3 was randomly created";
				break;
			case 4:
				result = "4 was randomly created";
				break;
			case 5:
				result = "5 was randomly created";
				break;
			default:
				result = "Randomly created number is >5";
				break;
		}
		
		return result;
	}

}
