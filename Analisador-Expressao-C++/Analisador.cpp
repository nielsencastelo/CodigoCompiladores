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
    // Pula espa�os em branco.
    while(expIdx < exp.length())
    {
        if (exp.at(expIdx) == ' ')
        ++expIdx;
    }
    // Espa�os em branco finais indicam fim da express�o.
    if(expIdx == exp.length())
    {
        token = EOE;
        return;
    }
}
