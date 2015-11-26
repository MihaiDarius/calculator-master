package com.mycalculator;

import android.app.Activity;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CalcMain extends Activity {

	private Vibrator myVib;
	Toast maxSizeError;
	Toast div0Error;
	Toast badSymbolError;
	Long lastToast = (long) 0; //This is needed to keep toasts from stacking up
	//Double num1 = (double) 0;
	//Double num2 = (double) 0;
	//Integer len1 = 0; // Holds the length of num1 so that it can be extracted and parsed later
	//Integer len2 = 0; // Holds the length of num2
	Double result = null;
	Double prevResult = (double) 0; 
	int operation = 13; // Holds the opcode
	/*
	 * '+' = 0
	 * '-' = 1
	 * '*' = 2
	 * '/' = 3
	 * 'none' = 13 
	 */
	TextView inBox;
	TextView outBox; 
	Boolean equalFlag = false; // Checks if = has been pressed
	// ensures only 1 decimal per # is entered
	//Boolean num1DecFlag = false; 
	Boolean num2Set = false;
	MyNumber num1 = new MyNumber();
	MyNumber num2 = new MyNumber();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calc_main);
		myVib = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // Hides the notification bar
		maxSizeError = Toast.makeText(getBaseContext(),"Maximum Characters Reached", Toast.LENGTH_SHORT);
		div0Error = Toast.makeText(getBaseContext(),"Cannot divide by zero", Toast.LENGTH_SHORT);
		badSymbolError = Toast.makeText(getBaseContext(),"Invalid symbol", Toast.LENGTH_SHORT);
		inBox = (TextView) findViewById(R.id.inputBox);
		inBox.setText(""); // Required so that users don't break the app by hitting an operation first
		outBox = (TextView) findViewById(R.id.outputBox);
		// Handles clear all function
		Button clr = (Button) findViewById(R.id.buttonClr);
		Button decimal = (Button) findViewById(R.id.buttonDec);
		decimal.setText("."); // Required for some reason. I am not sure why
		clr.setOnLongClickListener(new OnLongClickListener() { 
            @Override
            public boolean onLongClick(View v) {
            	myVib.vibrate(50);
            	inBox.setText("");
				outBox.setText("");
				equalFlag = false;
				num1.setValue(0);
				num2.setValue(0);
				operation = 13;
				num2Set = false;
                return true;
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calc_main, menu);
		return true;
	}
	
	// This is the command called whenever a digit key is hit
	public void numPress(View v) {
		// Provides haptic feedback
		myVib.vibrate(50);
		
		// Resets variables for next operation
		if(equalFlag == true) {
			inBox.setText("");
			outBox.setText("");
			equalFlag = false;
			num1.setValue(0);
			num2.setValue(0);
			operation = 13;
			num2Set = false;
		}
		
		Button b = (Button) v;
		// Gets the input digit
		String input = b.getText().toString();
		String curText = inBox.getText().toString();
		// checks that screen isn't overloaded
		if(curText.length() < 20) {
			// Updates the stored numbers
			if(operation == (Integer) 13) {
				num1.updateVal(input);
			}
			else {
				num2.updateVal(input);
				num2Set = true;
			}
		}
		// Handles the case when the screen is full
		else {
			// Checks if previous toast is still up. This keeps them from queuing
			if (lastToast <= System.currentTimeMillis()/1000 - 2000) {
				maxSizeError.cancel();
				div0Error.cancel();
				badSymbolError.cancel();
			}
			lastToast = System.currentTimeMillis()/1000;
			maxSizeError.show();
		}
		updateScreen();
	}
	
	// This is the command called when the clear key is hit. Make sure to check case (32+clr)
		public void clrClick(View v) {
			// Provides haptic feedback
			myVib.vibrate(50);
			if(!equalFlag){
				if(operation == 13) num1.clrClick();
				// Edit num2
				else if(num2Set) {
					num2.clrClick();
					// Case where num2 is completely deleted
					if(num2.getValue() == 0) num2Set = false;
				}
				else operation = 13;
				updateScreen();				
			}
			else {
				inBox.setText("");
				outBox.setText("");
				equalFlag = false;
				num1.setValue(0);
				num2.setValue(0);
				operation = 13;
				num2Set = false;
				
			}
		}

		// This is called when the equals button is pressed. Definitely will be changed with 
		public void equalClick(View v) {
			
			// Provides haptic feedback
			myVib.vibrate(50);

			if(operation != 13 && !num2Set) operation = 13;
			
			switch(operation) {
				case 13: 
					if(!equalFlag) {
						result = num1.getValue();
						prevResult = result;
					}
					break;
				case 0: result = num1.getValue() + num2.getValue();
					prevResult = result;
					break;
				case 1: result = num1.getValue() - num2.getValue();
					prevResult = result;
					break;
				case 2: result = num1.getValue() * num2.getValue();
					prevResult = result;
					break;
				case 3: 
					if(num2.getValue() != 0) {
						result = num1.getValue() / num2.getValue();
						prevResult = result;
					}
					else {
						// Checks if previous toast is still up. This keeps them from queuing
						result = (double) 0;
						if (lastToast <= System.currentTimeMillis()/1000 - 2000) {
							maxSizeError.cancel();
							div0Error.cancel();
							badSymbolError.cancel();
						}
						lastToast = System.currentTimeMillis()/1000;
						div0Error.show();
					}
						break;
			}
			equalFlag = true;
			updateScreen();
		}	
		
		// This is called when the equals button is pressed. Definitely will be changed with 
		public void opClick(View v) {
			// Provides haptic feedback
			myVib.vibrate(50);
			String curText = inBox.getText().toString();
			// Checks if any numbers have been entered yet
			if(inBox.getText().toString() != "") {
				// When one number is entered
				Button b = (Button) v;
				// Gets the opcode
				String input = b.getContentDescription().toString();
				// Handles the case where = has been pressed but no new #'s have been entered
				if (equalFlag){
					num1.setValue(prevResult);
					num2.setValue(0);
					outBox.setText("");
					operation = Integer.parseInt(input);
					equalFlag = false;
					num2Set = false;
				}
				// Handles case where max characters have been entered
				else if(curText.length() > 19) {
					maxSizeError.show();
				}
				// Handles case where no operation has been entered yet
				else if(operation == 13) {
					operation = Integer.parseInt(input);
					//inBox.setText(inBox.getText().toString() + op);
				}
				// Handles case where two operations are entered in a row 
				else if(!num2Set)
				{
					operation = Integer.parseInt(input);
				}
				// handles cases like "5+6+7"
				else {
					equalClick(v);
					num1.setValue(prevResult);
					num2.setValue(0);
					outBox.setText("");
					operation = Integer.parseInt(input);
					equalFlag = false;
					result = null;
				}
				updateScreen();
			}
			else {
				// Checks if previous toast is still up. This keeps them from queuing
				if (lastToast <= System.currentTimeMillis()/1000 - 2000) {
					maxSizeError.cancel();
					div0Error.cancel();
					badSymbolError.cancel();
				}
				lastToast = System.currentTimeMillis()/1000;
				badSymbolError.show();
			}
		}
		
		public void updateScreen() {
			String temp;
			// Handles data entry phase
			temp = Double.toString(num1.getValue());
			// Prints the appropriate operator
			switch(operation) {
				case 13: break;
				case 0: temp += " + ";
					break;
				case 1: temp += " - ";
					break;
				case 2: temp += " * ";
					break;
				case 3: temp += " / ";
						break;
			}
			
			if(operation != 13) temp += Double.toString(num2.getValue());
			inBox.setText(temp);
			// Handles calculation
			if(equalFlag) outBox.setText(Double.toString(result));
		}
		
		public void altSign(View v) {
			// Provides haptic feedback
			myVib.vibrate(50);
			if(num2Set) num2.altSign();
			else num1.altSign();
			updateScreen();
		}
		
		// Need to handle case where ans add too many chars to the screen!!!
		public void ansClick(View v) {
			if (equalFlag){
				num1.setValue(prevResult);
				num2.setValue(0);
				outBox.setText("");
				operation = 13;
				equalFlag = false;
				num2Set = false;
			}
			else if(operation == 13) num1.setValue(prevResult);
			else {
				num2.setValue(prevResult);
				num2Set = true;
			}
			updateScreen();
		}
}
