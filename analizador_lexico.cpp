#include <QCoreApplication>

int main(int argc, char *argv[])
{
#include <stdio.h>
#include <conio.h>
#include <windows.h>
#include <string.h>
#include <iostream>
#define MAX 650
#define MAX_RES 23

using namespace std;

char *PalavrasReservadas[MAX_RES] = {"se","entao","inteiro","decimal","logico","texto","char","enquanto","verdadeiro","falso","declaracao","iniciar","terminar","fim","laia","imprima","parar","sobe","desce","como"};
char tokenSimbolo[MAX];
char auxWord [MAX];//variavel para guardar(tokens)
string tokenNumeros[MAX];
string tokenIdentificadores[MAX];
string TokenReservados[MAX];
string tokenNaoValidos[MAX];
boll eNumero = true;
boll eIdentificador = true;

//variavel aux guardar tokens
int auxTR=0;
int auxTI=0;
int auxTN=0;
int auxTNV=0;

//outros procedimentos
boll verificarNumero (char palavra)[]);
boll verificarIdentificador (char palavra)[]);
boll verificarReservada (char palavra)[]);
void imprimirTokenNumeros();
void imprimirTokenSimbolos();
void imprimirTokenReservados();
void imprimirTokenIdentificadores();
void imprimirTokenIdentificadoresNaoValidos();

main()
{

unsigned char entrada[MAX];
char *p = entrada;
printf("Entre com a descrição: ");
fflush(stdin);
gets(entrada);
fflush(stdin);	\\limpa
clrscr(); 	\\cleam


char palavra [MAX];//Variavel importante, pode guardar caracter
int indexPalavra=0;
int indexSimbolos=0;
// laços
for (int i=0;i<MAX;i++){tokenReservados[i] = "";}//fim ciclo


for (int i=0;i<MAX;i++){tokensNaoValidos[i] = "";}

for (int i=0i<MAX;i++);{
    palavra[i]= '\0';
}

//Este laço percorre cada carracter de entrada do programa fonte
while(*p!= '\0'	)
{
    if((*p != ' ') && (*p !=',')) // Primeiro revisa que se o destino de um espaço e virgula
    {
        if((*p== ';') || (*p=='"') || (*p=='(') || (*p==')') || (*p=='+') || (*p=='-') || (*p=='*') || (*p=='/') || (*p=='#') || (*p=='<') || (*p=='>') || 		   (*p=='='))
        {
            tokenSimbolos[indexSimbolos] = *p;
            indexSimbolos++;
        }
        else
        {
            palavra[indexPalavra] = *p;
            auxWord[indexPalavra] = *p;//a razao de usar as funcoes manda  como parametro o metodo verifica numero
            indexPalavra++;
        }
    }
    else if ((*p== '  ')||(*p== ' , '))
    {
        //guarda as palavras nos respectivos tokens segundo as funcoes
        if(verificarReservada(palavra)==true){
           tokenReservadas[auxTR] = palavra;
           auxTR++;
        }else if(verificarIdentificador(palavra)==true){
           tokenIdentificador[auxTI] = palavra;
           auxTI++;

        }else if(verificarNumero(auxWord)==true){
           tokenNumeros[auxTN] = auxWord;
           auxTN++;
        }
        for (int i=0;i<MAX;i++)[palavra[i]= '\0';}// para drena a palavra
        for (int i=0;i<MAX;i++)[auxWord[i]=	;}//para drena palavra auxiliar
        indexPalavra=0;
        p++;
        }

//Este e para examinar a ultima palavra formada quando nao se pode examinar
if(verificarReserva(palavra) ==true){
    tokenReservada[auxTR] =palavra;
    auxTR++;
}else if(verificarIdentificador(palavra) ==true){
    tokenIdentificadores[auxTI] =palavra;
    auxTI++;
}else if(verificarNumero(auxWord) ==true){
    tokenNumeros[auxTN] =auxWord;
    auxTN++;
}
//Imprime resultados
printf("\t\tEXPRESSão; %s\n",entrada));
imprimirtokenReservado();
imprimirtokenSimbolos ();
imprimirtokenIdentificadores();
imprimirtokenNumeros ();
imprimirtokenIdentificadoresNaoValidos ();
getch();
}

//Inicio do desenvolvimento dos metodos para imprimir os resultados
void imprimirTokenSimbolos(){
    puts("\n	\n|SIMBOLOS TOKENS|\n	");
for int i=0; i<strlen(tokenSimbolos);i++){printf("%c\n",tokenSimbolos[i]);}
}

void imprimirTokenreservados(){
    puts("\n	\n|RESERVADOS TOKENS|\n	");
for (int i=0; i<auxTR;i++){printf("%d %s\n",i+1,tokenReservados[i]);}
}

void imprimirTokenIdentificadores(){
    puts("\n	\n|IDENTIFICADORES TOKENS|\n	");
for int i=0; i<auxTI;i++){printf("%d %s\n",i+1,tokenIdentificadores[i]);}
}

void imprimirTokenNumeros(){
    puts("\n	\n|NUMEROS TOKENS|\n	");
for int i=0; i<auxTN;i++){printf("%d %s\n",i+1, tokenNumeros[i]);}
}

void imprimirIdentificadoresNaoValidos(){
    puts(" ");
    printf("***************MENSAGEM******************");
    if(auxTNV ==0){
        printf("\t\t o ERROS, PROGRAMA EXISTE");
}else{
    for int i=0;i<auxTNV;i++)
{
        printf("\t\ ERRO: Token INVALIDO: %s \n", tokenNaovalidos[i]);
}
}
}
//Fim dos metodos
//Metodo para verificar se tokens formados corresponde a uma palavra reservada
bool verificarReservada (char palavra[]){
    int comp;
    bool eReservado=false;
    string str(palavra);
    for(int i=0; i<MAX_RES; i++)
    {
        comp = strcmp(Palavrareservada[i],palavra);
        if(comp==0)
        {
            eReservada = true;
            break;
        }
    }
    return eReservado;
}
//metodo para verificar se token e identificador
bool verificaIdentificador(char palavra[]){
    string auxPalavra = palavra;
    eIdentificador = false;
    short estado = 0;
    char *p = palavra;
    while(*p != '\0')
    {
        switch(estado)
        {
            case 0:
                if((isalpha(*p))||(*p=='_')){

                   estado = 1;
                   eIdentificador = true;

                }
                else{
                   estado = 2;
                   eIdentificador = false;
                }
                p++;
            break;
            case 1:
                if((isalpha(*p))||(isdigit(*p))||(*p== '_')){
                   estado = 1;
                   eIdentificador = true;
                }
                else{
                   estado = 2;
                   eIdentificador = false;
                }
                p++;
            break;
            case 2:
            // Nao e identificador

                eIdentificador = false;
                *p= '\0';
            break
        }
    }
    return eIdentificador;
}
//Metodo para verificar se o token e um numero inteiro decimal, guarda o tokens nao validos para mostrar

bool verificarNumero(char palavra[]){
    string auxPalavra = palavra;
    eNumero = false;
    short estado = 0, cont=0;
    char *p = palavra;
    while(*p!= '\0')
    {
        switch(estado)
        {
            case 0:
                if(isdigit(*p )){
                   estado = 0;
                   eNumero=true;
                   cont++;
                }

                else if (((*p ==	) && (cont==0)) || (isalpha(*p))){
                   estado = 2;
                   eNumero=false;
                }else if(*p=='.'){
                   estado = 1;
                   eNumero=false;
                }
                p++;
            break;
            case 1:
                if(isdigit(*p )){
                   estado = 1;
                   eNumero=true;
                }else
                {
                   estado = 2;
                   eNumero=false;
                }
                p++;
            break;
            case 2:
               eNumero = false;
               *p = '\0';
            break;
        }
    }
    if(eNumero == false){
        if(auxPalavra!=""){
           tokenNaoValidos[auxTNV] = auxPalavra;
           auxTNV++;
        }
    }
    return eNumero;
}
}

