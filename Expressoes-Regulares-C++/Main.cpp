//---------------------------------------------------------------------------

#include <vcl.h>
#pragma hdrstop

#include "Main.h"
//---------------------------------------------------------------------------
#pragma package(smart_init)
#pragma resource "*.dfm"
TFrmPrincipal *FrmPrincipal;
//---------------------------------------------------------------------------
__fastcall TFrmPrincipal::TFrmPrincipal(TComponent* Owner)
	: TForm(Owner)
{
}
//---------------------------------------------------------------------------

void __fastcall TFrmPrincipal::FormShow(TObject *Sender)
{
    Panel1->SetFocus();
}
//---------------------------------------------------------------------------

void __fastcall TFrmPrincipal::SpeedButton1Click(TObject *Sender)
{
	AnsiString s = Edit1->Text;

	if (RadioButton1->Checked)
	{
        if (std::regex_match (s.c_str(), std::regex("[0-9]{5}\\-[0-9]{3}$") ))
			LabelValidacao->Caption = "Validado";
		else
	    	LabelValidacao->Caption = "N�o validado";
	}
    if (RadioButton2->Checked)
	{
		if (std::regex_match (s.c_str(), std::regex("[A-Z]{3}\\-[0-9]{4}$") ))
			LabelValidacao->Caption = "Validado";
		else
			LabelValidacao->Caption = "N�o validado";
	}
    if (RadioButton3->Checked)
	{
		if (std::regex_match (s.c_str(), std::regex("[0-9]{9}\\-[0-9]{2}$") ))
			LabelValidacao->Caption = "Validado";
		else
			LabelValidacao->Caption = "N�o validado";
	}
    if (RadioButton4->Checked)
	{
		if (std::regex_match (s.c_str(), std::regex("[\\+\\-]?[0-9]*\\.[0-9]+") ))
			LabelValidacao->Caption = "Validado";
		else
			LabelValidacao->Caption = "N�o validado";
	}
    if (RadioButton5->Checked)
	{
		if (std::regex_match (s.c_str(), std::regex("(0[1-9]|[1-2][0-9]|3[01])/(0[1-9]|1[0-2])/([12][0-9]{3})$") ))
			LabelValidacao->Caption = "Validado";
		else
			LabelValidacao->Caption = "N�o validado";
	}


}
//---------------------------------------------------------------------------

void __fastcall TFrmPrincipal::SpeedButton2Click(TObject *Sender)
{
    Edit1->Clear();
}
//---------------------------------------------------------------------------

