// Demonstrate the parser.  
import java.io.*;

import javax.swing.JOptionPane; 
  
class PDemo {  
  public static void main(String args[])  throws IOException 
  {  
	  String expr; 
 
	  Parser p = new Parser();  
    
	  for(;;) 
	  {  
		  expr = JOptionPane.showInputDialog("Enter an empty expression to stop. ");  
		  if(expr.equals("")) break;  
		  try 
		  { 
			  JOptionPane.showMessageDialog(null, "Result: " + p.evaluate(expr)); 
		  } catch (ParserException exc) 
		  { 
			  System.out.println(exc); 
		  } 
	  }  
  }  
}

