#include "Analisador.hpp"
void Analisador::getToken()
{
    tokType = NONE;
    token = "";
    if(expIdx == exp.length())
    {
        token = EOE;
        return;
    }
    // Pula espaços em branco.
    while(expIdx < exp.length())
    {
        if (exp.at(expIdx) == ' ')
        ++expIdx;
    }
    // Espaços em branco finais indicam fim da expressão.
    if(expIdx == exp.length())
    {
        token = EOE;
        return;
    }
}
