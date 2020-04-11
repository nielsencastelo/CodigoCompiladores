import java.util.Stack;

public class Parenteses 
{
	private String exp; 
	private int expIdx;
	private Stack<String> S;
	Parenteses(String exp) 
	{
		this.exp = exp;
		S = new Stack<String>();
		expIdx = 0;
	}
	
	private boolean isOpen(char c)  
	{  
		if((" (".indexOf(c) != -1))  
	      return true;  
	    return false;  
	}
	public void evaluate()
	{
		for(int i = 0; i < exp.length();i++)
		{
			if(isOpen(exp.charAt(i)))
			{
				//S.push(exp.charAt(i));
			}
			else
			{
				if(S.isEmpty())
				System.out.println("Nada para casar");
			}
				
		}	
	}
}
