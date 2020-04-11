 
import java.io.*; 
import java.util.*; 
 

class InterpreterException extends Exception {   
  String errStr; // descreve os erros 
  
  public InterpreterException(String str) {  
    errStr = str;  
  }    
   
  public String toString() {   
    return errStr;  
  }   
}   
   
// O interpretador 
class SBasic {   
  final int PROG_SIZE = 10000; // tamanho m�ximo de programa
 
  // S�o os tipos de token.  
  final int NONE = 0;  
  final int DELIMITER = 1;  
  final int VARIABLE = 2;  
  final int NUMBER = 3;  
  final int COMMAND = 4;  
  final int QUOTEDSTR = 5; 
  
  // Tipos de erros.  
  final int SYNTAX = 0;  
  final int UNBALPARENS = 1;  
  final int NOEXP = 2;  
  final int DIVBYZERO = 3;  
  final int EQUALEXPECTED = 4;  
  final int NOTVAR = 5;  
  final int LABELTABLEFULL = 6;  
  final int DUPLABEL = 7;  
  final int UNDEFLABEL = 8;  
  final int THENEXPECTED = 9;  
  final int TOEXPECTED = 10;  
  final int NEXTWITHOUTFOR = 11;  
  final int RETURNWITHOUTGOSUB = 12;  
  final int MISSINGQUOTE = 13; 
  final int FILENOTFOUND = 14;  
  final int FILEIOERROR = 15;  
  final int INPUTIOERROR = 16; 
 
  // Representa��o interna das palavras-chave do Interpretador. 
  final int UNKNCOM = 0; 
  final int PRINT = 1; 
  final int INPUT = 2; 
  final int IF = 3; 
  final int THEN = 4; 
  final int FOR = 5; 
  final int NEXT = 6; 
  final int TO = 7; 
  final int GOTO = 8; 
  final int GOSUB = 9; 
  final int RETURN = 10; 
  final int END = 11; 
  final int EOL = 12; 
 
  // Indica final do programa
  final String EOP = "\0";  
  
  // C�digo para operadores duplos, como <=. 
  final char LE = 1; 
  final char GE = 2; 
  final char NE = 3; 
 
  // Array para as vari�veis   
  private double vars[]; 
  
  // Essa classe vincula palavras-chaves com seus tokens de palavra-chave. 
  class Keyword {  
    String keyword; // forma de string
    int keywordTok; // representa��o interna  
 
    Keyword(String str, int t) { 
      keyword = str; 
      keywordTok = t; 
    } 
  } 
 
  /* Tabela de palavras-chave com suas representa��o interna
   *  Todas as palavras-chave deve ser inseridas em letras minusculas. */ 
  Keyword kwTable[] = {  
    new Keyword("print", PRINT), 
    new Keyword("input", INPUT), 
    new Keyword("if", IF), 
    new Keyword("then", THEN), 
    new Keyword("goto", GOTO), 
    new Keyword("for", FOR), 
    new Keyword("next", NEXT), 
    new Keyword("to", TO), 
    new Keyword("gosub", GOSUB), 
    new Keyword("return", RETURN), 
    new Keyword("end", END) 
  }; 
 
  private char[] prog; // referencia ao array de programas
  private int progIdx; // indice atual do programa
 
  private String token; // mantem o token atual   
  private int tokType;  // mantem o tipo de token  
 
  private int kwToken; // representa��o interna de uma palavra-chave
 
  // Suporte para o loops FOR. 
  class ForInfo { 
    int var; // vari�vei contadora  
    double target; // valor alvo  
    int loc; // indice no codigo-fonte pelo qual iterar  
  } 
 
  // Pilha para o loop FOR. 
  private Stack fStack; 
 
  // Define entradas da tabela de r�tulo. 
  class Label { 
    String name; // label  
    int loc; // o �ndice de localiza�� odo r�tulo no arquivo fonte  
    public Label(String n, int i) { 
      name = n; 
      loc = i; 
    } 
  } 
 
  // A mapa de r�tulos
  private TreeMap labelTable; 
 
  // Pilha de gosubs. 
  private Stack gStack; 
 
  // operadores relacionais. 
  char rops[] = { 
    GE, NE, LE, '<', '>', '=', 0 
  }; 
 
  /* Cria uma string contendo os operadores
   * relacionais a fim de tornar sua verifica��o
   * mais conveniente */ 
  String relops = new String(rops); 
 
  // Construtor do Small Basic. 
  public SBasic(String progName)  
      throws InterpreterException { 
 
    char tempbuf[] = new char[PROG_SIZE]; 
    int size; 
 
    // Carre o programa a executar. 
    size = loadProgram(tempbuf, progName); 
 
    if(size != -1) { 
      // Cria um array adequadamente dimensionado para armazenar o programa. 
      prog = new char[size]; 
 
      // Copia o programa para o array de programas. 
      System.arraycopy(tempbuf, 0, prog, 0, size); 
    } 
  } 
 
  // Carrrega um programa 
  private int loadProgram(char[] p, String fname) 
    throws InterpreterException 
  { 
    int size = 0; 
 
    try { 
      FileReader fr = new FileReader(fname); 
 
      BufferedReader br = new BufferedReader(fr); 
 
      size = br.read(p, 0, PROG_SIZE); 
 
      fr.close(); 
    } catch(FileNotFoundException exc) { 
      handleErr(FILENOTFOUND); 
    } catch(IOException exc) { 
      handleErr(FILEIOERROR); 
    }    
 
    // Se o arquivo termina com uma marca de EOF, volta. 
    if(p[size-1] == (char) 26) size--; 
 
    return size; // retorna o tamanho do programa
  } 
 
  // Executa o programa 
  public void run() throws InterpreterException { 
 
    // Inicializa uma nova execu��o do programa. 
    vars = new double[26];   
    fStack = new Stack(); 
    labelTable = new TreeMap(); 
    gStack = new Stack(); 
    progIdx = 0; 
 
    scanLabels(); // localiza os r�tulos no programa  
 
    sbInterp(); // executa 
 
  } 
 
  // Ponto de entrada para o interpretador. 
  private void sbInterp() throws InterpreterException 
  { 
 
    // Este � o loop principal do interpretador. 
    do { 
      getToken(); 
      // verifica a presen�a de uma instru��o de atribui��o. 
      if(tokType==VARIABLE) { 
        putBack(); // retorna a var para o fluxo de entrada(input stream)  
        assignment(); // trata instru��o de atribui��o 
      } 
      else // � palavra-chave
        switch(kwToken) { 
          case PRINT: 
            print(); 
            break; 
          case GOTO: 
            execGoto(); 
            break; 
          case IF: 
            execIf(); 
            break; 
          case FOR: 
            execFor(); 
            break; 
          case NEXT: 
            next(); 
            break; 
          case INPUT: 
            input(); 
            break; 
          case GOSUB: 
            gosub(); 
            break; 
          case RETURN: 
            greturn(); 
            break; 
          case END: 
            return; 
        } 
    } while (!token.equals(EOP)); 
  } 
 
  // Localiza todos os rot�los.  
  private void scanLabels() throws InterpreterException 
  { 
    int i; 
    Object result; 
 
    // Ve se o primeiro token do arquivo � um r�tulo. 
    getToken(); 
    if(tokType==NUMBER)  
      labelTable.put(token, new Integer(progIdx)); 
 
    findEOL(); 
 
    do {      
      getToken(); 
      if(tokType==NUMBER) {// deve ser um n�mero de linha
        result = labelTable.put(token, 
                                new Integer(progIdx)); 
        if(result != null) 
          handleErr(DUPLABEL); 
      } 
 
      // se n�o � uma linha em branco, procura a pr�xima linha.  
      if(kwToken != EOL) findEOL(); 
    } while(!token.equals(EOP)); 
    progIdx = 0; // reset index to start of program 
  } 
 
  // Find the start of the next line. 
  private void findEOL() 
  { 
    while(progIdx < prog.length && 
          prog[progIdx] != '\n') ++progIdx; 
    if(progIdx < prog.length) progIdx++; 
  } 
 
  // Atribui��o de um valor de uma vari�vel. 
  private void assignment() throws InterpreterException 
  { 
    int var; 
    double value; 
    char vname; 
 
    // Obtem o nome da vari�vel 
    getToken(); 
    vname = token.charAt(0); 
 
    if(!Character.isLetter(vname)) { 
      handleErr(NOTVAR); 
      return; 
    } 
 
    // Converte em indice para a tabela de variaveis. 
    var = (int) Character.toUpperCase(vname) - 'A'; 
    
    // Obtem o sinal de igual. 
    getToken(); 
    if(!token.equals("=")) { 
      handleErr(EQUALEXPECTED); 
      return; 
    } 
 
    // Obt�m o valor a atribuir. 
    value = evaluate(); 
 
    // Atribui o valor. 
    vars[var] = value; 
  } 
 
  // Executa ma vers�o simples da instru��o PRINT. 
  private void print() throws InterpreterException 
  { 
    double result; 
    int len=0, spaces; 
    String lastDelim = ""; 
 
    do { 
      getToken(); // obt�m o proximo item da lista 
      if(kwToken==EOL || token.equals(EOP)) break; 
 
      if(tokType==QUOTEDSTR) { // � string 
        System.out.print(token); 
        len += token.length(); 
        getToken(); 
      } 
      else { // � express�o 
        putBack(); 
        result = evaluate(); 
        getToken(); 
        System.out.print(result); 
 
        // Adiciona o comprimento de sa�da a soma total
        Double t = new Double(result); 
        len += t.toString().length(); // Salva o comprimento 
      } 
      lastDelim = token; 
 
      // Se firgula, move-se para pr�xima parada de tabula��o 
      if(lastDelim.equals(",")) { 
        // Calcula o n�mero de espa�os pelos quais se move at� a proxima tabula��o
        spaces = 8 - (len % 8);  
        len += spaces; // Adiciona na posi��o de position 
        while(spaces != 0) {  
          System.out.print(" "); 
          spaces--; 
        } 
      } 
      else if(token.equals(";")) { 
        System.out.print(" "); 
        len++; 
      } 
      else if(kwToken != EOL && !token.equals(EOP)) 
        handleErr(SYNTAX);  
    } while (lastDelim.equals(";") || lastDelim.equals(",")); 
 
    if(kwToken==EOL || token.equals(EOP)) { 
      if(!lastDelim.equals(";") && !lastDelim.equals(",")) 
        System.out.println(); 
    } 
    else handleErr(SYNTAX);  
  } 
 
  // Executa o GOTO 
  private void execGoto() throws InterpreterException 
  { 
    Integer loc; 
 
    getToken(); // obt�m o rotulo para o qual ir 
 
    // Localiza a locaiz�a�o do r�tulo
    loc = (Integer) labelTable.get(token); 
 
    if(loc == null) 
      handleErr(UNDEFLABEL); // r�tulo n�o definido 
    else // inicia execu��o do programa nesse loc 
      progIdx = loc.intValue(); 
  } 
 
  // Executa o IF
  private void execIf() throws InterpreterException 
  { 
    double result; 
 
    result = evaluate(); // obtem o valor da express�o
 
    /* Se o resultado for true (n�o-zero), 
       Processe alvo do IF. Caso contr�rio, mova-se para
       a pr�xima linha do programa. */ 
    if(result != 0.0) {  
      getToken(); 
      if(kwToken != THEN) { 
        handleErr(THENEXPECTED); 
        return; 
      } // se n�o, a instru��o alvoser� executada  
    } 
    else findEOL(); // localiza o inicio da pr�xima linha
  } 
 
  // Executa o FOR
  private void execFor() throws InterpreterException 
  { 
    ForInfo stckvar = new ForInfo(); 
    double value; 
    char vname; 
 
    getToken(); // L� a vari�vel de controle 
    vname = token.charAt(0); 
    if(!Character.isLetter(vname)) { 
      handleErr(NOTVAR); 
      return; 
    } 
 
    // Salva indice de var de controle 
    stckvar.var = Character.toUpperCase(vname) - 'A'; 
 
    getToken(); // l� o sinal igual
    if(token.charAt(0) != '=') { 
      handleErr(EQUALEXPECTED); 
      return; 
    } 
 
    value = evaluate(); // obtem o valor inicial 
 
    vars[stckvar.var] = value; 
 
    getToken(); // l� e descarta o TO 
    if(kwToken != TO) handleErr(TOEXPECTED); 
 
    stckvar.target = evaluate(); // obtem o valor alvo
 
    /* Se o loop pode exceutar pelo menos uma vez, 
       insere a info na pilha. */ 
    if(value >= vars[stckvar.var]) {  
      stckvar.loc = progIdx; 
      fStack.push(stckvar); 
    } 
    else // caso contr�rio, pula todo o c�digo do loop 
      while(kwToken != NEXT) getToken(); 
  } 
 
  // Executa o NEXT.  
  private void next() throws InterpreterException 
  { 
    ForInfo stckvar; 
 
    try { 
      // Recupere informa��es para este loop For
      stckvar = (ForInfo) fStack.pop(); 
      vars[stckvar.var]++; // Controle de incremento var
   
      // Se feito, volte.
      if(vars[stckvar.var] > stckvar.target) return;  
 
      // Caso contr�rio, restaure as informa��es.
      fStack.push(stckvar); 
      progIdx = stckvar.loc;  // loop  
    } catch(EmptyStackException exc) { 
      handleErr(NEXTWITHOUTFOR); 
    } 
  } 
 
  // Executa o INPUT.  
  private void input() throws InterpreterException 
  { 
    int var; 
    double val = 0.0; 
    String str; 
 
    BufferedReader br = new 
      BufferedReader(new InputStreamReader(System.in)); 
 
    getToken(); // verifica se string prompt est� presente
    if(tokType == QUOTEDSTR) { 
      // se estiver, imprime-a e verifica se h� alguma virgula  
      System.out.print(token); 
      getToken(); 
      if(!token.equals(",")) handleErr(SYNTAX); 
      getToken(); 
    } 
    else System.out.print("? "); // Caso contr�rio, solicita com ? 
 
    // obt�m o var de entrada  
    var =  Character.toUpperCase(token.charAt(0)) - 'A'; 
 
    try { 
      str = br.readLine(); 
      val = Double.parseDouble(str); // l� o valor
    } catch (IOException exc) { 
      handleErr(INPUTIOERROR); 
    } catch (NumberFormatException exc) { 
      /* Talvez voc� queira tratar esse erro 
         diferentemente dos outros erros 
        do interpretador. */ 
      System.out.println("Invalid input."); 
    } 
 
    vars[var] = val; // armazena-o
  } 
 
  // Executa GOSUB.  
  private void gosub() throws InterpreterException 
  { 
    Integer loc; 
 
    getToken(); 
 
    // Localiza o r�tulo a chamar 
    loc = (Integer) labelTable.get(token);  
 
    if(loc == null) 
      handleErr(UNDEFLABEL); // r�tulo n�o definido
    else { 
      // Salva o lugar para o qual retorna 
      gStack.push(new Integer(progIdx)); 
 
      // Iniciaa execu��o do programa nessa loc 
      progIdx = loc.intValue(); 
    } 
  } 
 
  // Return do GOSUB. 
  private void greturn() throws InterpreterException 
  { 
    Integer t; 
 
    try { 
      // Restaura o index do programa. 
      t = (Integer) gStack.pop(); 
      progIdx = t.intValue(); 
    } catch(EmptyStackException exc) { 
      handleErr(RETURNWITHOUTGOSUB); 
    } 
 
  } 
 
  // **************** Analisador de express�o **************** 
 
  
  private double evaluate() throws InterpreterException 
  {   
    double result = 0.0;   
 
    getToken();   
    if(token.equals(EOP))  
      handleErr(NOEXP); 
  
  
    result = evalExp1();   
 
    putBack();   
 
    return result;   
  }   
     
 
  private double evalExp1() throws InterpreterException 
  { 
    double l_temp, r_temp, result; 
    char op; 
 
    result = evalExp2(); 
    // Se no fim do programa, retorna. 
    if(token.equals(EOP)) return result; 
 
    op = token.charAt(0);   
 
    if(isRelop(op)) { 
      l_temp = result; 
      getToken(); 
      r_temp = evalExp1(); 
      switch(op) { // realiza a opera��o relacional
        case '<': 
          if(l_temp < r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
        case LE: 
          if(l_temp <= r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
        case '>': 
          if(l_temp > r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
        case GE: 
          if(l_temp >= r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
        case '=': 
          if(l_temp == r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
        case NE: 
          if(l_temp != r_temp) result = 1.0; 
          else result = 0.0; 
          break; 
      } 
    } 
    return result; 
  } 
  
  // Add ou subtrai 2 termos.   
  private double evalExp2() throws InterpreterException  
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
     
  // Multiplia ou divide 2 termos.   
  private double evalExp3() throws InterpreterException  
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
     
  // Processa um expoente.   
  private double evalExp4() throws InterpreterException  
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
     
  // Avaliar + ou -.   
  private double evalExp5() throws InterpreterException  
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
     
  // Processa um parentese na express�o   
  private double evalExp6() throws InterpreterException  
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
     
  // obt�m o valor de uma vari�vel.   
  private double atom() throws InterpreterException   
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
      case VARIABLE:   
        result = findVar(token);   
        getToken();   
        break;   
      default:   
        handleErr(SYNTAX);   
        break;   
    }   
    return result;  
  }   
     
   // Retorna o valor de uma vari�vel.   
  private double findVar(String vname) 
    throws InterpreterException  
  {   
    if(!Character.isLetter(vname.charAt(0))){   
      handleErr(SYNTAX);   
      return 0.0;   
    }   
    return vars[Character.toUpperCase(vname.charAt(0))-'A'];   
  }   
   
  // Retornar um token para o fluxo de entrada.   
  private void putBack()     
  {   
    if(token == EOP) return;  
    for(int i=0; i < token.length(); i++) progIdx--;   
  }   
   
  // Handle an error.   
  private void handleErr(int error) 
    throws InterpreterException  
  {   
    String[] err = {   
      "Syntax Error",   
      "Unbalanced Parentheses",   
      "No Expression Present",   
      "Division by Zero", 
      "Equal sign expected", 
      "Not a variable", 
      "Label table full", 
      "Duplicate label", 
      "Undefined label", 
      "THEN expected", 
      "TO expected", 
      "NEXT without FOR", 
      "RETURN without GOSUB", 
      "Closing quotes needed", 
      "File not found", 
      "I/O error while loading file", 
      "I/O error on INPUT statement" 
    };   
   
    throw new InterpreterException(err[error]);   
  }   
     
  // Obtem o pr�ximo token 
  private void getToken() throws InterpreterException 
  {   
    char ch; 
 
    tokType = NONE;   
    token = "";   
    kwToken = UNKNCOM; 
 
    // Check for end of program.   
    if(progIdx == prog.length) { 
      token = EOP;  
      return;  
    }  
 
    // Passe o espa�o em branco.
    while(progIdx < prog.length &&   
          isSpaceOrTab(prog[progIdx])) progIdx++; 
   
    // Trailing whitespace ends program.  
    if(progIdx == prog.length) {  
      token = EOP;  
      tokType = DELIMITER; 
      return;  
    }  
 
    if(prog[progIdx] == '\r') { // handle crlf 
      progIdx += 2; 
      kwToken = EOL; 
      token = "\r\n"; 
      return; 
    } 
 
    // Check operador relacional. 
    ch = prog[progIdx]; 
    if(ch == '<' || ch == '>') { 
      if(progIdx+1 == prog.length) handleErr(SYNTAX); 
 
      switch(ch) { 
        case '<': 
          if(prog[progIdx+1] == '>') { 
            progIdx += 2;; 
            token = String.valueOf(NE); 
          } 
          else if(prog[progIdx+1] == '=') { 
            progIdx += 2; 
            token = String.valueOf(LE); 
          } 
          else { 
            progIdx++; 
            token = "<"; 
          } 
          break; 
        case '>': 
          if(prog[progIdx+1] == '=') { 
            progIdx += 2;; 
            token = String.valueOf(GE); 
          } 
          else { 
            progIdx++; 
            token = ">"; 
          } 
          break; 
      } 
      tokType = DELIMITER; 
      return; 
    } 
   
    if(isDelim(prog[progIdx])) { 
      // � um operador.   
      token += prog[progIdx];   
      progIdx++;   
      tokType = DELIMITER;   
    }   
    else if(Character.isLetter(prog[progIdx])) {  
      // � uma vari�vel ou palavra-chave. 
      while(!isDelim(prog[progIdx])) {   
        token += prog[progIdx];   
        progIdx++;   
        if(progIdx >= prog.length) break;   
      }   
 
      kwToken = lookUp(token); 
      if(kwToken==UNKNCOM) tokType = VARIABLE;   
      else tokType = COMMAND; 
    } 
    else if(Character.isDigit(prog[progIdx])) { 
      // � um n�mero. 
      while(!isDelim(prog[progIdx])) {   
        token += prog[progIdx];   
        progIdx++;   
        if(progIdx >= prog.length) break;   
      }   
      tokType = NUMBER;   
    }   
    else if(prog[progIdx] == '"') { 
      // � uma string entre aspas. 
      progIdx++; 
      ch = prog[progIdx]; 
      while(ch !='"' && ch != '\r') { 
        token += ch; 
        progIdx++; 
        ch = prog[progIdx]; 
      } 
      if(ch == '\r') handleErr(MISSINGQUOTE); 
      progIdx++;  
      tokType = QUOTEDSTR; 
    } 
    else { // caracter desconhecido termina o programa
      token = EOP;  
      return;  
    }  
  }   
     
 
  private boolean isDelim(char c)   
  {   
    if((" \r,;<>+-/*%^=()".indexOf(c) != -1))   
      return true;   
    return false;   
  }   
 
 
  boolean isSpaceOrTab(char c)  
  { 
    if(c == ' ' || c =='\t') return true; 
    return false; 
  } 
 
  
  boolean isRelop(char c) { 
    if(relops.indexOf(c) != -1) return true; 
    return false; 
  } 
 
  /* Pesquisa uma representa��o interna do token na tabela de token. */ 
  private int lookUp(String s) 
  { 
    int i; 
 
    // Converte para minusculas. 
    s = s.toLowerCase(); 
   
    // Ve se o token est� na tabela. 
    for(i=0; i < kwTable.length; i++) 
      if(kwTable[i].keyword.equals(s)) 
        return kwTable[i].keywordTok; 
    return UNKNCOM; // unknown keyword 
  } 
}

