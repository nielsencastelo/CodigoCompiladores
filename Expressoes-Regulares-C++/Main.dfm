object FrmPrincipal: TFrmPrincipal
  Left = 0
  Top = 0
  Caption = '..:: Express'#245'es Regulares - By Nielsen C. Damasceno'
  ClientHeight = 307
  ClientWidth = 667
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -27
  Font.Name = 'Tahoma'
  Font.Style = []
  OldCreateOrder = False
  Position = poDesktopCenter
  OnShow = FormShow
  PixelsPerInch = 96
  TextHeight = 33
  object Label1: TLabel
    Left = 8
    Top = 8
    Width = 168
    Height = 33
    Caption = 'Digite a String'
  end
  object LabelValidacao: TLabel
    Left = 152
    Top = 140
    Width = 208
    Height = 33
    Caption = '                          '
  end
  object Panel1: TPanel
    Left = 0
    Top = 229
    Width = 667
    Height = 78
    Align = alBottom
    TabOrder = 0
    ExplicitTop = 264
    object SpeedButton1: TSpeedButton
      Left = 64
      Top = 8
      Width = 112
      Height = 57
      Caption = 'Verificar'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      OnClick = SpeedButton1Click
    end
    object SpeedButton2: TSpeedButton
      Left = 192
      Top = 8
      Width = 112
      Height = 57
      Caption = 'Limpar'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      OnClick = SpeedButton2Click
    end
  end
  object Edit1: TEdit
    Left = 8
    Top = 48
    Width = 433
    Height = 41
    TabOrder = 1
    Text = '01/05/2017'
  end
  object GroupBox1: TGroupBox
    Left = 482
    Top = 0
    Width = 185
    Height = 229
    Align = alRight
    Caption = 'Padr'#245'es'
    TabOrder = 2
    ExplicitLeft = 464
    ExplicitTop = 8
    ExplicitHeight = 265
    object RadioButton1: TRadioButton
      Left = 24
      Top = 44
      Width = 113
      Height = 29
      Caption = 'CEP'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      TabOrder = 0
    end
    object RadioButton2: TRadioButton
      Left = 24
      Top = 79
      Width = 113
      Height = 25
      Caption = 'Placa'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      TabOrder = 1
    end
    object RadioButton3: TRadioButton
      Left = 24
      Top = 120
      Width = 113
      Height = 17
      Caption = 'CPF'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      TabOrder = 2
    end
    object RadioButton4: TRadioButton
      Left = 24
      Top = 152
      Width = 81
      Height = 17
      Caption = 'Float'
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      TabOrder = 3
    end
    object RadioButton5: TRadioButton
      Left = 24
      Top = 184
      Width = 113
      Height = 17
      Caption = 'Data'
      Checked = True
      Font.Charset = DEFAULT_CHARSET
      Font.Color = clWindowText
      Font.Height = -19
      Font.Name = 'Tahoma'
      Font.Style = []
      ParentFont = False
      TabOrder = 4
      TabStop = True
    end
  end
end
