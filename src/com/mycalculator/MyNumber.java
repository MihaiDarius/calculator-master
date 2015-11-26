package com.mycalculator;

public class MyNumber {
	
	private double value = 0;
	private boolean decFlag = false; // Flag for if a decimal has been placed
	private double decMult = 1; // Is used to place numbers after the decimal in the right spot
	private double decMultMax = Math.pow(10, 15);
	private double temp;
	
	// Need to handle case where input is a decimal.
	public void setValue(double input) {
		value = input;
		// Needed to properly reset
		decMult = 1;
		// This handles the case where the input is a decimal
		if(value != Math.round(value)) {
			temp = input;
			decFlag = true;
			while(temp != Math.round(temp)) {
				temp *= 10;
				decMult *= 10;
			}
		}
		else decFlag = false;
	}
	
	public double getValue() {
		return value;
	}
	
	// Alternates the sign
	public void altSign() {
		value = 0 - value;
	}
	
	// Called with every number press
	public void updateVal(String input){
		if(input == ".") decFlag = true;
		else {
			temp = Integer.parseInt(input);
			// Handles inputs before the decimal place
			if(!decFlag) value = value * 10 + temp;
			// Handles inputs after the decimal place
			else if(decMult <= decMultMax) {
				decMult = decMult * 10; // For some reason this works but decMult/10 doesn't. Need to investigate
				temp = temp /decMult;
				value += temp;
			}
			// maybe round here to make things pretty?
		}
		// Requires to handle weird error where random number appear in the 16th decimal place
		value = Math.round(value*decMult) / ((double)decMult); 
	}
	
	// Handles clear clicks
	public void clrClick() {
		
		// Handles inputs before the decimal place
		if(!decFlag) value = Math.round((value - temp) / 10);
		// Handles inputs after the decimal place
		else {
			value -= temp;
			decMult = decMult / 10;
			if(decMult == 1) decFlag = false; // Resets flag when all decimal places are deleted
		}
		value = Math.round(value*decMult) / ((double)decMult);
	}

}
