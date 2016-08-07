package Euler;

public class MyMethods {
	
	public boolean isPrime(int n) {
		for(int i = 2; i < n; ++i) {
			if(n % i == 0) {
				return false;
			}
		}
		return true;
	}
	
	public int intAt(int num, int index) {		// call with (this, index)... maybe change to (index) only
		String str = Integer.toString(num);
		return Integer.parseInt(str.charAt(index) + "");
	}
	
}