#ifndef ANALISADOR_HPP
#define ANALISADOR_HPP
#include <iostream>
#include <string>
using namespace std;
class Analisador
{
    public:
        // Tipos de Tokens
        int NONE  = 0;
        int DELIMITER = 1;
        int VARIABLE = 2;
        int NUMBER = 3;

        // Tipos de erros de sintaxe
        int SYNTAX = 0;
        int UNBALPARENS = 1;
        int NOEXP = 2;
        int DIVBYZERO = 3;
    private:
        string EOE = "\0"; // Fim da expressão
        string exp;        // referencia a string da expressão
        int expIdx;        // indice atual da expressão
        string token;      // mantem o token atual
        int tokType;       // mantém o tipo de token
    public:
        void getToken();
};
#endif // ANALISADOR
