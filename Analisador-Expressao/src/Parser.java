   
// Classe exceção.  
class ParserException extends Exception {  
  String errStr; // descreve os erros
 
  public ParserException(String str) { 
    errStr = str; 
  }   
  
  public String toString() {  
    return errStr; 
  }  
}  
  
class Parser {  
  // Esses são os tipo de token 
  final int NONE = 0; 
  final int DELIMITER = 1; 
  final int VARIABLE = 2; 
  final int NUMBER = 3; 
 
  // Esses são os tipos de erro de sintaxe
  final int SYNTAX = 0; 
  final int UNBALPARENS = 1; 
  final int NOEXP = 2; 
  final int DIVBYZERO = 3; 
 
  // Esse token indica o fim da expressão
  final String EOE = "\0"; 
 
  private String exp;   // referencia a string da expressão  
  private int expIdx;   // indice atual na expressão  
  private String token; // mantém o token atual  
  private int tokType;  // mantém o tipo de token  
  
  
  public double evaluate(String expstr) throws ParserException 
  {  
    double result;  
    exp = expstr;  
    expIdx = 0;   
   
    getToken();  
    if(token.equals(EOE))  
      handleErr(NOEXP); // nenhuma expressão presente 
 
    // Analisa sintaticamente e avalia a expressão.
    result = evalExp2();  
  
    if(!token.equals(EOE)) // Último token deve ser EOE  
      handleErr(SYNTAX);  
  
    return result;  
  }  
    
  // Adiciona ou subtrai 2 termos.  
  private double evalExp2() throws ParserException 
  {  
    char op;  
    double result; 
    double partialResult;  
 
    result = evalExp3();  
 
    while((op = token.charAt(0)) == '+' || op == '-') {  
      getToken();  
      partialResult = evalExp3();  
      switch(op) {  
        case '-':  
          result = result - partialResult;  
          break;  
        case '+':  
          result = result + partialResult;  
          break;  
      }  
    }  
    return result; 
  }  
    
  // Multiplica ou divide 2 fatores.  
  private double evalExp3() throws ParserException 
  {  
    char op;  
    double result; 
    double partialResult;  
    
    result = evalExp4();  
 
    while((op = token.charAt(0)) == '*' ||  
           op == '/' || op == '%') {  
      getToken();  
      partialResult = evalExp4();  
      switch(op) {  
        case '*':  
          result = result * partialResult;  
          break;  
        case '/':  
          if(partialResult == 0.0)  
            handleErr(DIVBYZERO);  
          result = result / partialResult;  
          break;  
        case '%':  
          if(partialResult == 0.0)  
            handleErr(DIVBYZERO);  
          result = result % partialResult;  
          break;  
      }  
    }  
    return result; 
  }  
    
  // Process um expoente  
  private double evalExp4() throws ParserException 
  {  
    double result; 
    double partialResult; 
    double ex;  
    int t;  
    
    result = evalExp5();  
 
    if(token.equals("^")) {  
      getToken();  
      partialResult = evalExp4();  
      ex = result;  
      if(partialResult == 0.0) {  
        result = 1.0;  
      } else  
        for(t=(int)partialResult-1; t > 0; t--)  
          result = result * ex;  
    }  
    return result; 
  }  
    
  // Avalia o operador unário + ou -.  
  private double evalExp5() throws ParserException 
  {  
    double result; 
    String  op;  
 
    op = "";  
    if((tokType == DELIMITER) &&  
        token.equals("+") || token.equals("-")) {  
      op = token;  
      getToken();  
 
    }  
    result = evalExp6();  
 
    if(op.equals("-")) result = -result; 
 
    return result;  
  }  
    
  // Processa os parenteses na expressão.  
  private double evalExp6() throws ParserException 
  {  
    double result; 
 
    if(token.equals("(")) {  
      getToken();  
      result = evalExp2();  
      if(!token.equals(")"))  
        handleErr(UNBALPARENS);  
      getToken();  
    }  
    else result = atom();  
 
    return result; 
  }  
    
  // obtém o valor de um número.  
  private double atom() throws ParserException  
  {  
    double result = 0.0; 
 
    switch(tokType) {  
      case NUMBER:  
        try {  
          result = Double.parseDouble(token);  
        } catch (NumberFormatException exc) {  
          handleErr(SYNTAX);  
        }  
        getToken();  
        break; 
      default:  
        handleErr(SYNTAX);  
        break;  
    }  
    return result; 
  }  
    
  // Handle do erro.  
  private void handleErr(int error) throws ParserException 
  {  
    String[] err = {  
      "Syntax Error",  
      "Unbalanced Parentheses",  
      "No Expression Present",  
      "Division by Zero"  
    };  
  
    throw new ParserException(err[error]);  
  }  
    
  // Obtém o próximo token.  
  private void getToken()  
  {  
    tokType = NONE;  
    token = "";  
     
    // Verifica se o fim da expressão foi alcançado  
    if(expIdx == exp.length()) { 
      token = EOE; 
      return; 
    } 
    
    // Pula espaços em branco. 
    while(expIdx < exp.length() &&  
      Character.isWhitespace(exp.charAt(expIdx))) ++expIdx;  
  
    // Espaços em branco finais indicam fim da expressão. 
    if(expIdx == exp.length()) { 
      token = EOE; 
      return; 
    } 
  
    if(isDelim(exp.charAt(expIdx))) { // é um operador 
      token += exp.charAt(expIdx);  
      expIdx++;  
      tokType = DELIMITER;  
    }  
    else if(Character.isLetter(exp.charAt(expIdx))) { // é uma variável  
      while(!isDelim(exp.charAt(expIdx))) {  
        token += exp.charAt(expIdx);  
        expIdx++;  
        if(expIdx >= exp.length()) break;  
      }  
      tokType = VARIABLE;  
    }  
    else if(Character.isDigit(exp.charAt(expIdx))) { // é um número 
      while(!isDelim(exp.charAt(expIdx))) {  
        token += exp.charAt(expIdx);  
        expIdx++;  
        if(expIdx >= exp.length()) break;  
      }  
      tokType = NUMBER;  
    }  
    else { // caracter desconhecido termina expressão 
      token = EOE; 
      return; 
    } 
  }  
    
  // Retorna true se o caracter c é um delimitador  
  private boolean isDelim(char c)  
  {  
    if((" +-/*%^=()".indexOf(c) != -1))  
      return true;  
    return false;  
  }  
    
} 

