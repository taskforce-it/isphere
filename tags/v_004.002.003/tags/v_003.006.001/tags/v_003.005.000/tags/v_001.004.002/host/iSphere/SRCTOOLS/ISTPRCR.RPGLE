      /Eject
      /Title System Procedures
      **********************************************************************************
      *****                                                      Date:?2005-05-18  *****
      **********************************************************************************
      *****  Copyright - Free RPG/i5 Tools                                         *****
      *****     E-Mail - Utilities@FreeRPGTools.com                                *****
      *****     Purpose: Service Program for Free RPG/i5 Tools Software.           *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2010-06-22   FRT       Add NOGRP Option                               *****
      *****  2007-03-15   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
      ***?                             Creation Keywords                             ***
      *** Keyword      Keyword Data                                                ***
      *** BNDDIR        ISTBDIR QC2LE                                               ***
      ***                                                                           ***
      **********************************************************************************
     /If Not Defined(Prototypes)
      **********************************************************************************
      *** STRPREPRC Compile Options:                                                 ***
      ***   >>PRE-COMPILER<<                                                         ***
      ***     >>CRTCMD<< CRTRPGMOD  MODULE(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);    ***
      ***       >>COMPILE<<                                                          ***
      ***         >>PARM<<  TGTRLS(V5R4M0);                                          ***
      ***         >>PARM<<  DBGVIEW(*NONE);                                          ***
      ***         >>PARM<<  OPTIMIZE(*FULL);                                         ***
      ***       >>END-COMPILE<<                                                      ***
      ***       >>EXECUTE<<                                                          ***
      ***   >>END-PRE-COMPILER<<                                                     ***
      **********************************************************************************
     H COPYRIGHT('Copyright(c) Free RPG Tools - http:\\www.FreeRPGTools.com')
     H DEBUG OPTION(*NODEBUGIO:*SRCSTMT)
     H DATFMT(*ISO-)
     H FixNbr(*Zoned:*InputPacked)
     H Indent('| ')
     H TIMFMT(*ISO.)
     H TRUNCNBR(*YES)
     H BNDDIR('QC2LE':'ISPHERE')
     /If Defined(FRT_Header)
     H DFTACTGRP(*NO)
     /If Defined(NEWGRP)
     H ACTGRP(*NEW)
     /EOF
     /EndIf
     /If Defined(NOGRP)
     /EOF
     /EndIf
     H ACTGRP(*CALLER)
     /EOF
     /Endif
     H NoMain
     /Endif

      ***?QCMDEXC Program Parameters
     DCmdExec          PR                  ExtPgm('QCMDEXC')
     D CommandData                         Like(Command)
     D CommandLength                       Like(Length)

      ***?Retrieve Operational Descriptor Information
     D CEEDOD          pr
     D                               10i 0 Const
     D                               10i 0
     D                               10i 0
     D                               10i 0
     D                               10i 0
     D                               10i 0
     D                               12a   Options(*Omit)

      ***?Center Text Procedure
     DCenter           PR         32767A   Varying
     D                                     OPDESC
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)


      ***?Retrieve Object Description
     DObjText          PR            50A
     D                               10A   CONST
     D                               10A   CONST
     D                               10A   CONST

      ***?Variable Lookup Function
     DIn               PR             1A
     D                              128A   CONST
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)
     D                              128A   CONST OPTIONS(*NOPASS)

      ***?Convert to Lower Case
     DLCase            PR         32767A   Varying
     D                                     OPDESC
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)

      ***?Convert to Upper Case
     DUCase            PR         32767A   Varying
     D                                     OPDESC
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)

      ***?Execute Command
     DExecuteCommand   PR            67A
     D                                     OPDESC
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)
     D                                 N   OPTIONS(*NOPASS) CONST

      ***?Check Command
     DCheckCommand     PR         32767A   Varying
     D                                     OPDESC
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)
     D                                 N   OPTIONS(*NOPASS) CONST

      ***?Retrieve System Value
     DSystemValue      PR         32767A   Varying
     D                                     OPDESC
     D                               10A   CONST

      ***?Send Program Message
     DSndPgmMsg        PR
     D                                7A   CONST
     D                               10A   CONST
     D                               10A   CONST
     D                              256A   CONST
     D                                8A   CONST
     D                               10A   CONST
     D                                5S 0 CONST

      ***?Send User Message
     DSndUsrMsg        PR
     D                                7A   CONST
     D                               10A   CONST
     D                               10A   CONST
     D                              256A   CONST
     D                               10A   CONST
     D                               10A   CONST
     D                               10A   CONST

      *** Retrieve Member Description
     DMbrText          PR            50A
     D                               10A   CONST
     D                               10A   CONST
     D                               10A   CONST

      ***?Retrieve System Name Procedure
     D RtvSysName      PR            10I 0
     D   SystemName                   8A

      ***?Retrieve File Information
     DFileInfo         PR            50A
     D                               10A   CONST                                Library Name
     D                               10A   CONST                                File Name
     D                               10A   CONST                                Member Name

      ***?Retrieve File Format Name
     DFileFormatName   PR            10A
     D                               10A   CONST                                Library Name
     D                               10A   CONST                                File Name

      ***?Check for Interactive Job
     DInteractiveJob   PR              N

     /If Defined(Prototypes)
     /Eof
     /Endif

      /Title  Data Definition Section
      ***?Define Global Variables
     DCommand          S            512A                                        QCMDEXC Command
     DLength           S             15P 5 Inz(%Size(Command))                  QCMDEXC Length

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

      **********************************************************************************
      *****  Procedure:?Center                                   Date:?2003-11-11  *****
      **********************************************************************************
      *****     Purpose: Center Text.                                              *****
      *****       Input : Text to Center                                           *****
      *****     Returns : Centered Text                                            *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-11   ISP                                                      *****
      *****                                                                        *****
      **********************************************************************************
     P Center          B                   Export

      /Title  Proto-Type definition section
     D Center          PI                  Like(Text)
     D                                     OPDESC
     D  Input                     32767A   CONST
     D                                     OPTIONS(*varsize)

      /Title  Data definition section
      ***?Procedure Work Variables
     DText             S          32767A   Varying                              Centering Text
     DSaveText         S                   Like(Input)                          Centering Text
     DTxtLength        S                   Like(Length)                         Centering Text
     DTextLen          S                   Like(Length)                         Centering Text
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0


     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Get Length of Data
       ceedod( 1
             : DescType
             : DataType
             : DescInfo1
             : DescInfo2
             : length
             : *omit
             );

         // **?Center Data in Field
       SaveText = %SubSt(Input:1:Length);
       TxtLength = %Len(%Trim(SaveText)) / 2;
       Eval(h) TextLen = Length / 2;
       If TxtLength < TextLen;
         Text = *Blanks;
         Text = %Subst(Text:1:(TextLen - TxtLength)) + %Trim(SaveText);
       Else;
         Text = SaveText;
       EndIf;

       Return Text;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - IN
      **********************************************************************************
      *****  Procedure:?IN                                       Date:?2005-05-01  *****
      **********************************************************************************
      *****     Purpose: Lookup Function                                           *****
      *****       Input : Search Argument                                          *****
      *****               Search Elements                                          *****
      *****     Returns : True or False                                            *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-05-18   ISP       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P In              B                   Export

      /Title  Proto-Type definition section
     D In              PI             1A
     D  $Search                     128A   CONST
     D  $Element01                  128A   CONST OPTIONS(*NOPASS)
     D  $Element02                  128A   CONST OPTIONS(*NOPASS)
     D  $Element03                  128A   CONST OPTIONS(*NOPASS)
     D  $Element04                  128A   CONST OPTIONS(*NOPASS)
     D  $Element05                  128A   CONST OPTIONS(*NOPASS)
     D  $Element06                  128A   CONST OPTIONS(*NOPASS)
     D  $Element07                  128A   CONST OPTIONS(*NOPASS)
     D  $Element08                  128A   CONST OPTIONS(*NOPASS)
     D  $Element09                  128A   CONST OPTIONS(*NOPASS)
     D  $Element10                  128A   CONST OPTIONS(*NOPASS)
     D  $Element11                  128A   CONST OPTIONS(*NOPASS)
     D  $Element12                  128A   CONST OPTIONS(*NOPASS)
     D  $Element13                  128A   CONST OPTIONS(*NOPASS)
     D  $Element14                  128A   CONST OPTIONS(*NOPASS)
     D  $Element15                  128A   CONST OPTIONS(*NOPASS)
     D  $Element16                  128A   CONST OPTIONS(*NOPASS)
     D  $Element17                  128A   CONST OPTIONS(*NOPASS)
     D  $Element18                  128A   CONST OPTIONS(*NOPASS)
     D  $Element19                  128A   CONST OPTIONS(*NOPASS)
     D  $Element20                  128A   CONST OPTIONS(*NOPASS)
     D  $Element21                  128A   CONST OPTIONS(*NOPASS)
     D  $Element22                  128A   CONST OPTIONS(*NOPASS)
     D  $Element23                  128A   CONST OPTIONS(*NOPASS)
     D  $Element24                  128A   CONST OPTIONS(*NOPASS)
     D  $Element25                  128A   CONST OPTIONS(*NOPASS)

      /Title  Data definition section
      ***?Procedure Work Variables
     DFound            S               N
     DIdx              S              5S 0
     DSearch           S            128A

     DElements         DS
     D Element01                    128A   Overlay(Elements:*Next)
     D Element02                    128A   Overlay(Elements:*Next)
     D Element03                    128A   Overlay(Elements:*Next)
     D Element04                    128A   Overlay(Elements:*Next)
     D Element05                    128A   Overlay(Elements:*Next)
     D Element06                    128A   Overlay(Elements:*Next)
     D Element07                    128A   Overlay(Elements:*Next)
     D Element08                    128A   Overlay(Elements:*Next)
     D Element09                    128A   Overlay(Elements:*Next)
     D Element10                    128A   Overlay(Elements:*Next)
     D Element11                    128A   Overlay(Elements:*Next)
     D Element12                    128A   Overlay(Elements:*Next)
     D Element13                    128A   Overlay(Elements:*Next)
     D Element14                    128A   Overlay(Elements:*Next)
     D Element15                    128A   Overlay(Elements:*Next)
     D Element16                    128A   Overlay(Elements:*Next)
     D Element17                    128A   Overlay(Elements:*Next)
     D Element18                    128A   Overlay(Elements:*Next)
     D Element19                    128A   Overlay(Elements:*Next)
     D Element20                    128A   Overlay(Elements:*Next)
     D Element21                    128A   Overlay(Elements:*Next)
     D Element22                    128A   Overlay(Elements:*Next)
     D Element23                    128A   Overlay(Elements:*Next)
     D Element24                    128A   Overlay(Elements:*Next)
     D Element25                    128A   Overlay(Elements:*Next)
     D ElementsA                    128A   Dim(25) Overlay(Elements:1)

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /Free
        Found = *OFF;
        Exsr SetElements;

        Idx = %Lookup(Search:ElementsA:1);
        If Idx > 0 and Idx <= (%Parms - 1);
          Found = *ON;
        EndIf;

        Return Found;
      /End-Free
      **********************************************************************************

     ?/Eject
      /Title Set Array Elements
      **********************************************************************************
      *****  Subroutine : SetElements                                              *****
      *****     Purpose : Set Array Elements.                                      *****
      **********************************************************************************
     C     SetElements   BegSr

     C                   Eval      Elements = *Blanks

      ***?Set Search Field
     C                   If        %addr($Search)<>*null
     C                   Eval      Search = $Search
     C                   Endif

      ***?Set Elements
     C                   If        %addr($Element01)<>*null and %Parms > 1
     C                   Eval      Element01 = $Element01
     C                   Endif

     C                   If        %addr($Element02)<>*null and %Parms > 2
     C                   Eval      Element02 = $Element02
     C                   Endif

     C                   If        %addr($Element03)<>*null and %Parms > 3
     C                   Eval      Element03 = $Element03
     C                   Endif

     C                   If        %addr($Element04)<>*null and %Parms > 4
     C                   Eval      Element04 = $Element04
     C                   Endif

     C                   If        %addr($Element05)<>*null and %Parms > 5
     C                   Eval      Element05 = $Element05
     C                   Endif

     C                   If        %addr($Element06)<>*null and %Parms > 6
     C                   Eval      Element06 = $Element06
     C                   Endif

     C                   If        %addr($Element07)<>*null and %Parms > 7
     C                   Eval      Element07 = $Element07
     C                   Endif

     C                   If        %addr($Element08)<>*null and %Parms > 8
     C                   Eval      Element08 = $Element08
     C                   Endif

     C                   If        %addr($Element09)<>*null and %Parms > 9
     C                   Eval      Element09 = $Element09
     C                   Endif

     C                   If        %addr($Element10)<>*null and %Parms > 10
     C                   Eval      Element10 = $Element10
     C                   Endif

     C                   If        %addr($Element11)<>*null and %Parms > 11
     C                   Eval      Element11 = $Element11
     C                   Endif

     C                   If        %addr($Element12)<>*null and %Parms > 12
     C                   Eval      Element12 = $Element12
     C                   Endif

     C                   If        %addr($Element13)<>*null and %Parms > 13
     C                   Eval      Element13 = $Element13
     C                   Endif

     C                   If        %addr($Element14)<>*null and %Parms > 14
     C                   Eval      Element14 = $Element14
     C                   Endif

     C                   If        %addr($Element15)<>*null and %Parms > 15
     C                   Eval      Element15 = $Element15
     C                   Endif

     C                   If        %addr($Element16)<>*null and %Parms > 19
     C                   Eval      Element16 = $Element16
     C                   Endif

     C                   If        %addr($Element17)<>*null and %Parms > 17
     C                   Eval      Element17 = $Element17
     C                   Endif

     C                   If        %addr($Element18)<>*null and %Parms > 18
     C                   Eval      Element18 = $Element18
     C                   Endif

     C                   If        %addr($Element19)<>*null and %Parms > 29
     C                   Eval      Element19 = $Element19
     C                   Endif

     C                   If        %addr($Element20)<>*null and %Parms > 20
     C                   Eval      Element20 = $Element20
     C                   Endif

     C                   If        %addr($Element20)<>*null and %Parms > 22
     C                   Eval      Element20 = $Element20
     C                   Endif

     C                   If        %addr($Element21)<>*null and %Parms > 21
     C                   Eval      Element21 = $Element21
     C                   Endif

     C                   If        %addr($Element22)<>*null and %Parms > 22
     C                   Eval      Element22 = $Element22
     C                   Endif

     C                   If        %addr($Element23)<>*null and %Parms > 23
     C                   Eval      Element23 = $Element23
     C                   Endif

     C                   If        %addr($Element24)<>*null and %Parms > 24
     C                   Eval      Element24 = $Element24
     C                   Endif

     C                   If        %addr($Element25)<>*null and %Parms > 25
     C                   Eval      Element25 = $Element25
     C                   Endif

     C                   EndSR
      **********************************************************************************

     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - ObjText
      **********************************************************************************
      *****  Procedure:?ObjText                                  Date:?2005-05-02  *****
      **********************************************************************************
      *****     Purpose: Retrieve Object Description.                              *****
      *****       Input : Object Library                                           *****
      *****               Object Name                                              *****
      *****               Object Type                                              *****
      *****     Returns : Object Text                                              *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-05-18   ISP       Initial creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P ObjText         B                   Export

      /Title  Proto-Type definition section
     D ObjText         PI            50A
     D  $Library                     10A   CONST
     D  $FileName                    10A   CONST
     D  $ObjType                     10A   CONST

      /Title  Data definition section
      ***?Procedure Work Variables
     DText             S             50A                                        Centering Text
     DLength           S             10I 0 Inz(%Size(OBJD0200))
     DFormat           S              8A   Inz('OBJD0200')
     DObjType          S             10A

      ***?Default Field Overlay Data Structure
     DOverlays         DS
     D Object                        20A
     D  FileName                     10A   Overlay(Object:01)
     D  Library                      10A   Overlay(Object:11)

      ***?Type Definition for the OBJL0300 format.
     DOBJD0200         DS                                                       Qus OBJD0200
     D BytesRet                      10I 0                                      Bytes Returned
     D BytesAval                     10I 0                                      Bytes Available
     D ObjectName                    10A                                        Object Name
     D LibraryName                   10A                                        Library Name
     D ObjectType                    10A                                        Object Type
     D ReturnLib                     10A                                        Return Library
     D ASPNumber                     10I 0                                      ASP Number
     D ObjOwner                      10A                                        Object Owner
     D ObjDomain                      2A                                        Object Domain
     D CreateStamp                   13A                                        Create Date & Time
     D ChangeStamp                   13A                                        Change Date & Time
     D ExtobjAtr                     10A                                        Ext Obj Attribute
     D TextDesc                      50A                                        Text Description
     D SourceFile                    10A                                        Source File Name
     D SourceLib                     10A                                        Source Library
     D SourceMbr                     10A                                        Source Member

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   10A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      ***?Initialize Variables
     C                   Eval      Library = $Library
     C                   Eval      FileName = $FileName
     C                   Eval      ObjType = $ObjType

      ***?Call Retrieve Object Description API (QUSROBJD)
     C                   Call(E)   'QUSROBJD'
     C                   Parm                    OBJD0200
     C                   Parm                    Length
     C                   Parm                    Format
     C                   Parm                    Object
     C                   Parm                    ObjType
     C                   Parm                    apiError

     C                   If        apiMsgID = *Blanks
     C                   Eval      Text = TextDesc
     C                   Else
     C                   Eval      Text = *Blanks
     C                   EndIf

     C                   Return    Text

     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - CheckCommand
      **********************************************************************************
      *****  Procedure:?CheckCommand                             Date:?2005-04-29  *****
      **********************************************************************************
      *****     Purpose: Check Command Syntax.                                     *****
      *****       Input : Command                                                  *****
      *****               Length of Input Text                                     *****
      *****     Returns : ErrorAPI Data (80 characters)                            *****
      *****               Formatted Command                                        *****
      *****                                                                        *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-03-17  FRT       Initial Creation                                *****
      *****                                                                        *****
      **********************************************************************************
     P CheckCommand    B                   Export

      /Title Proto-Type definition section
      ***?Check/Format Command Process
     D CheckCommand    PI                  Like(ReturnCommand)
     D                                     OPDESC
     D  $CommandToExecute...
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)
     D  $PromptCommand...
     D                                 N   OPTIONS(*NOPASS) CONST

      ***?Process Command ProtoType
     D ProcessCommand  PR                  ExtPgm('QCAPCMD')
     D  #CmdExecute                        Like($Command)
     D  #Length                            Like(Length)
     D  #ControlBlock                      Like(ControlBlock)
     D  #BlockLength                       Like(BlockLength)
     D  #FormatName                        Like(FormatName)
     D  #RtnCommand                        Like(RtnCommand)
     D  #RtnCmdLen                         Like(RtnCmdLen)
     D  #RtnCmdLenA                        Like(RtnCmdLenA)
     D  #apiError                          Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DReturnCommand    S          32767A   Varying
     D$Command         S          32000A
     DControlBlock     S                   Like(CPOP0100)                       Optn Control Blk
     DBlockLength      S             10I 0 Inz(%Size(ControlBlock))             Control Block Len
     DFormatName       S              8A   Inz('CPOP0100')                      Format Name
     DRtnCommand       S                   Like($Command)                       Return Command
     DRtnCmdLen        S             10I 0 Inz(%Size(RtnCommand))               Return Command Len
     DRtnCmdLenA       S             10I 0                                      Return Command Len
     DIdx              S              5S 0
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0
     DPromptCommand    S               N                                        Prompt Command

      ***?Command Check Procedure Return Parameters
     DReturnParm       DS
     D NewCommand                          Like($Command)
     D ErrorCode                           Like(apiError)

      ***?The Process Commands API Data Structure
     DCPOP0100         DS
     D CMDPRTYPE                     10I 0 Inz(8)                               Command Process Type
     D DBCSHandle                     1A   Inz('0')                             DBCS Data Handling
     D Prompter                       1A   Inz('1')                             Prompter Action
     D CmdStrSyn                      1A   Inz('0')                             Command String Synta
     D MessageKey                     4A   Inz(*ALLX'00')                       Message Key
     D Reserved                       9A   Inz(*ALLX'00')                       Reserved

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
        If %Parms = 1;
          PromptCommand = *OFF;
        Else;
          PromptCommand = $PromptCommand;
        EndIf;
        If PromptCommand = *Blanks;
          PromptCommand = *OFF;
        EndIf;

         // **?Get Length of Data
       ceedod( 1
             : DescType
             : DataType
             : DescInfo1
             : DescInfo2
             : length
             : *omit
             );

       If %addr($CommandToExecute)<>*null;
         Prompter = PromptCommand;
         $Command = %Trim(%SubSt($CommandToExecute:1:Length));
         ControlBlock = CPOP0100;
         // **?Call Process Commands API
         CallP(E) ProcessCommand($Command:
                                 Length:
                                 ControlBlock:
                                 BlockLength:
                                 FormatName:
                                 RtnCommand:
                                 RtnCmdLen:
                                 RtnCmdLenA:
                                 apiError);

         If apiMsgID = *Blanks;
           NewCommand = RtnCommand;
           ErrorCode = apiError;

         Else;
           NewCommand = $Command;
           ErrorCode = apiError;
         Endif;

         Idx = %Scan(' ':NewCommand:1);
         If Idx > 0;
           NewCommand = %SubSt(NewCommand:(Idx+1):(%Len(NewCommand)-Idx));
         EndIf;

       Else;
         NewCommand = *Blanks;
         apiMsgID = 'NOPARM';
       Endif;

       Return ReturnParm;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - LCase
      **********************************************************************************
      *****  Procedure:?LCase                                    Date:?2006-01-10  *****
      **********************************************************************************
      *****     Purpose: Convert Text to Lower Case.                               *****
      *****       Input : Text Data                                                *****
      *****     Returns : Lower Case Text Data                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-10  FRT        Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P LCase           B                   Export

      /Title Proto-Type definition section
      ***?Check/Format Command Process
     D LCase           PI                  Like(ReturnText)
     D                                     OPDESC
     D  $InputText                32767A   CONST
     D                                     OPTIONS(*varsize)

      ***?Convert Case Prototype
     D ConvertCase     PR                  ExtProc( 'QlgConvertCase' )
     D  $ControlBlock                      Const Like(ControlBlock)
     D                            32767    Options( *VarSize )
     D                            32767    Options( *VarSize )
     D                               10I 0 Const
     D  $ErrorCode                         Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DInputText        S          32767A   Varying
     DReturnText       S          32767A   Varying
     DInputString      S          32767A
     DReturnString     S          32767A
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0

      ***?Request Control Block
     D ControlBlock    DS                  Inz
     D  RequestType                  10I 0
     D  InputData                    10I 0
     D  CaseRequest                  10I 0
     D  CDRReserved                  10A   Inz (*AllX'00')

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Get Length of Data
       ceedod( 1
             : DescType
             : DataType
             : DescInfo1
             : DescInfo2
             : length
             : *omit
             );

        InputText = %SubSt($InputText:1:Length);
        InputString = InputText;
        ReturnString = ReturnText;

        RequestType = 1;
        InputData = 0;
        CaseRequest = 1;

         // **?Convert Text to Lower Case
        CallP(E) ConvertCase(ControlBlock:
                             InputString:
                             ReturnString:
                             Length:
                             apiError);

         // **?Return Text to Program
        ReturnText = %SubSt(ReturnString:1:Length);
        Return ReturnText;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - UCase
      **********************************************************************************
      *****  Procedure:?UCase                                    Date:?2006-01-10  *****
      **********************************************************************************
      *****     Purpose: Convert Text to Upper Case.                               *****
      *****       Input : Text Data                                                *****
      *****     Returns : Upper Case Text Data                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-10  FRT        Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P UCase           B                   Export

      /Title Proto-Type definition section
      ***?Check/Format Command Process
     D UCase           PI                  Like(ReturnText)
     D                                     OPDESC
     D  $InputText                32767A   CONST
     D                                     OPTIONS(*varsize)

      ***?Convert Case Prototype
     D ConvertCase     PR                  ExtProc( 'QlgConvertCase' )
     D  $ControlBlock                      Const Like(ControlBlock)
     D                            32767    Options( *VarSize )
     D                            32767    Options( *VarSize )
     D                               10I 0 Const
     D  $ErrorCode                         Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DInputText        S          32767A   Varying
     DReturnText       S          32767A   Varying
     DInputString      S          32767A
     DReturnString     S          32767A
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0

      ***?Request Control Block
     D ControlBlock    DS                  Inz
     D  RequestType                  10I 0
     D  InputData                    10I 0
     D  CaseRequest                  10I 0
     D  CDRReserved                  10A   Inz (*AllX'00')

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Get Length of Data
       ceedod( 1
             : DescType
             : DataType
             : DescInfo1
             : DescInfo2
             : length
             : *omit
             );

        InputText = %SubSt($InputText:1:Length);
        InputString = InputText;
        ReturnString = ReturnText;

        RequestType = 1;
        InputData = 0;
        CaseRequest = 0;

         // **?Convert Text to Upper Case
        CallP(E) ConvertCase(ControlBlock:
                             InputString:
                             ReturnString:
                             Length:
                             apiError);

         // **?Return Text to Program
        ReturnText = %SubSt(ReturnString:1:Length);
        Return ReturnText;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - ExecuteCommand
      **********************************************************************************
      *****  Procedure:?ExecuteCommand                           Date:?2006-03-16  *****
      **********************************************************************************
      *****     Purpose: Execute Command                                           *****
      *****       Input : Command                                                  *****
      *****     Returns : Error Code                                               *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-03-16  FRT        Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P ExecuteCommand  B                   Export

      /Title Proto-Type definition section
      ***?Retrieve System Value
     D ExecuteCommand  PI                  Like(apiError)
     D                                     OPDESC
     D  $CommandToExecute...
     D                            32767A   CONST
     D                                     OPTIONS(*varsize)
     D  $PromptCommand...
     D                                 N   OPTIONS(*NOPASS) CONST

      ***?Process Command ProtoType
     D ProcessCommand  PR                  ExtPgm('QCAPCMD')
     D  #CmdExecute                        Like(CommandExecute)
     D  #Length                            Like(Length)
     D  #ControlBlock                      Like(ControlBlock)
     D  #BlockLength                       Like(BlockLength)
     D  #FormatName                        Like(FormatName)
     D  #RtnCommand                        Like(RtnCommand)
     D  #RtnCmdLen                         Like(RtnCmdLen)
     D  #RtnCmdLenA                        Like(RtnCmdLenA)
     D  #apiError                          Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DCommandExecute...
     D                 S          32702A
     DError            S               N
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0
     DControlBlock     S                   Like(CPOP0100)                       Optn Control Blk
     DBlockLength      S             10I 0 Inz(%Size(ControlBlock))             Control Block Len
     DFormatName       S              8A   Inz('CPOP0100')                      Format Name
     DRtnCommand       S                   Like(CommandExecute)                 Return Command
     DRtnCmdLen        S             10I 0 Inz(%Size(RtnCommand))               Return Command Len
     DRtnCmdLenA       S             10I 0                                      Return Command Len
     DIdx              S              5S 0
     DTick             S              1A   Inz(X'7D')
     DPromptCommand    S               N                                        Prompt Command

      ***?The Process Commands API Data Structure
     DCPOP0100         DS
     D CMDPRTYPE                     10I 0 Inz(0)                               Command Process Type
     D DBCSHandle                     1A   Inz('0')                             DBCS Data Handling
     D Prompter                       1A   Inz('0')                             Prompter Action
     D CmdStrSyn                      1A   Inz('0')                             Command String Synta
     D MessageKey                     4A   Inz(*ALLX'00')                       Message Key
     D Reserved                       9A   Inz(*ALLX'00')                       Reserved

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A
     D  apiErrorInd                    N

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
        If %Parms = 1 or (%Parms = 2 and $PromptCommand = '0');
          PromptCommand = *OFF;
        Else;
          PromptCommand = $PromptCommand;
        EndIf;
        If PromptCommand = *Blanks;
          PromptCommand = *OFF;
        EndIf;

         // **?Get Length of Data
       ceedod( 1
             : DescType
             : DataType
             : DescInfo1
             : DescInfo2
             : length
             : *omit
             );

       If %addr($CommandToExecute)<>*null;
         CommandExecute = %Trim(%SubSt($CommandToExecute:1:Length));

         // **?Validate Command Syntax
         CMDPRTYPE = 1;
         ControlBlock = CPOP0100;
         CallP(E) ProcessCommand(CommandExecute:
                                 Length:
                                 ControlBlock:
                                 BlockLength:
                                 FormatName:
                                 RtnCommand:
                                 RtnCmdLen:
                                 RtnCmdLenA:
                                 apiError);

         If apiMsgID = *Blanks and Not %Error;
         // **?Call Process Commands API
           Prompter = PromptCommand;
           CMDPRTYPE = 0;
           ControlBlock = CPOP0100;
           CommandExecute = RtnCommand;
           CallP(E) ProcessCommand(CommandExecute:
                                   Length:
                                   ControlBlock:
                                   BlockLength:
                                   FormatName:
                                   RtnCommand:
                                   RtnCmdLen:
                                   RtnCmdLenA:
                                   apiError);
           If apiMsgID = *Blanks and Not %Error;
             Error = *OFF;
           Else;
             Error = *ON;
           Endif;

         Else;
           Error = *ON;
         Endif;

       Else;
         Error = *ON;
       Endif;

       apiErrorInd = Error;

       Return  apiError;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - SystemValue
      **********************************************************************************
      *****  Procedure:?SystemValue                              Date:?2006-01-10  *****
      **********************************************************************************
      *****     Purpose: Retrieve System Value.                                    *****
      *****       Input : Value Name                                               *****
      *****     Returns : Value Data                                               *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-13  FRT        Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P SystemValue     B                   Export

      /Title Proto-Type definition section
      ***?Retrieve System Value
     D SystemValue     PI                  Like($Value)
     D                                     OPDESC
     D  $ValueName                   10A   CONST

      /Title  Data definition section
      ***?Procedure Work Variables
     D$Value           S          32767A   Varying
     D DescType        s             10i 0
     D DataType        s             10i 0
     D DescInfo1       s             10i 0
     D DescInfo2       s             10i 0
     D Length          s             10i 0
     DOffset           S              5S 0                                      Value Start Pos
     DResultLen        S             10I 0 Inz(%Size(Result))                   Result Field Length
     DNumberVals       S             10I 0 INZ(1)                               Number of Values

      ***?Result field
     DResult           DS
     D NbrValRet                     10I 0 Inz(1)                               # of Values Returned
     D RetOffSet                     10I 0 Inz(10)                              Return Value Offset
     D ReturnValue                32767A                                        Return Value

      ***?Get System Value
     D GetSystemValue  PR                  ExtPgm('QWCRSVAL')
     D  $Result                            Const Like(Result)
     D                               10I 0 Const
     D                               10I 0 Const
     D                               10A   Const
     D  $ErrorCode                         Like(apiError)

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Get System Value
       CallP(E) GetSystemValue(Result:
                               ResultLen:
                               1:
                               $ValueName:
                               apiError);

       Offset = RetOffset + 9;
       $Value = %SubSt(ReturnValue:
                       Offset:
                       (%Len(ReturnValue) - Offset));

       Return $Value;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Send Program Message Function
      **********************************************************************************
      *****  Procedure:?SndPgmMsg                                Date:?2005-12-14  *****
      **********************************************************************************
      *****     Purpose: Send Program Message                                      *****
      *****       Input : Message ID                                               *****
      *****               Message File Library                                     *****
      *****               Message File                                             *****
      *****               Message Data                                             *****
      *****               Message Type                                             *****
      *****               Message Call Stack Entry                                 *****
      *****               Message Call Stack Count                                 *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-12-14   ISP                                                      *****
      *****                                                                        *****
      **********************************************************************************
     P SndPgmMsg       B                   Export

      /Title  Proto-Type definition section
     D SndPgmMsg       PI
     D  MessageId                     7A   CONST
     D  $MessageLib                  10A   CONST
     D  $MessageFile                 10A   CONST
     D  MessageData                 256A   CONST
     D  MessageType                   8A   CONST
     D  MessageCSE                   10A   CONST
     D  MessageCSC                    5S 0 CONST

     D PgmMsgAPI       PR                  ExtPgm('QMHSNDPM')
     D   MessageID                    7A   CONST                                Message ID
     D   QualMsgF                    20A   CONST                                Message File
     D   MsgData                    256A   CONST                                Message Data
     D   MsgDtaLen                   10I 0 CONST                                Message Data Length
     D   MsgType                     10A   CONST                                Message Type
     D   CallStkEnt                  10A   CONST                                Call Stack Entry
     D   CallStkCnt                  10I 0 CONST                                Call Stack Counter
     D   MessageKey                   4A   CONST                                Message Key
     D   MessageError                      CONST Like(ApiError)                 Error Data Structure

      /Title  Data definition section
      ***?Procedure Work Variables

      ***?Message File Name Data Structure
     D MessageName     DS
     D  MessageFile                  10A   Overlay(MessageName:1)
     D  MessageLib                   10A   Overlay(MessageName:11)

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   10A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Send Program Message
       MessageFile = $MessageFile;
       MessageLib = $MessageLib;

       Callp(E)  PgmMsgAPI(MessageID:
                           MessageName:
                           MessageData:
                           %Len(%Trim(MessageData)):
                           MessageType:
                           MessageCSE:
                           MessageCSC:
                           '    ':
                           ApiError);

       Return;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Send User Message Function
      **********************************************************************************
      *****  Procedure:?SndUsrMsg                                Date:?2005-12-15  *****
      **********************************************************************************
      *****     Purpose: Send Program Message                                      *****
      *****       Input : Message ID                                               *****
      *****               Message File                                             *****
      *****               Message File Library                                     *****
      *****               Message Data                                             *****
      *****               Message Queue                                            *****
      *****               Message Queue Library                                    *****
      *****               Message Type                                             *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-12-15   ISP                                                      *****
      *****                                                                        *****
      **********************************************************************************
     P SndUsrMsg       B                   Export

      /Title  Proto-Type definition section
     D SndUsrMsg       PI
     D  $MessageId                    7A   CONST
     D  $MessageLibrary...
     D                               10A   CONST
     D  $MessageFile                 10A   CONST
     D  $MessageData                256A   CONST
     D  $MessageQueueName...
     D                               10A   CONST
     D  $MessageQueueLibrary...
     D                               10A   CONST
     D  $MessageType                 10A   CONST

     D UsrMsgAPI       PR                  ExtPgm('QMHSNDM')
     D   MessageID                    7A   CONST                                Message ID
     D   MsgFile                     20A   CONST                                Message File
     D   MsgData                    256A   CONST                                Message Data
     D   MsgDtaLen                   10I 0 CONST                                Message Data Length
     D   MsgType                     10A   CONST                                Message Type
     D   MsgQAry                     20A   CONST DIM(1)                         Message Queue Array
     D   MsgQCount                   10I 0 CONST                                Message Queue Count
     D   MsgQReply                   20A   CONST                                Message Queue Reply
     D   MessageKey                   4A   CONST                                Message Key
     D   MessageError                      CONST Like(ApiError)                 Error Data Structure

      /Title  Data definition section
      ***?Procedure Work Variables

      ***?Message File Name Data-Structure
     D MessageName     DS
     D  MessageFile                  10A   Overlay(MessageName:1)
     D  MessageLibrary...
     D                               10A   Overlay(MessageName:11)

      ***?Message Queue Name Data-Structure
     D MessageQueue    DS
     D  MessageQueueName...
     D                               10A   Overlay(MessageQueue:1)
     D  MessageQueueLibrary...
     D                               10A   Overlay(MessageQueue:11)

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   10A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Send User Message
       MessageFile = $MessageFile;
       MessageLibrary = $MessageLibrary;
       MessageQueueName = $MessageQueueName;
       MessageQueueLibrary = $MessageQueueLibrary;

       Callp(E)  UsrMsgAPI($MessageID:
                           MessageName:
                           $MessageData:
                           %Len(%Trim($MessageData)):
                           $MessageType:MessageQueue:
                           1:
                           '    ':
                           '    ':
                           ApiError);

       Return;

      /END-FREE
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - MbrText
      **********************************************************************************
      *****  Procedure:?MbrText                                  Date:?2005-05-02  *****
      **********************************************************************************
      *****     Purpose: Retrieve Member Description.                              *****
      *****       Input : Source File Library                                      *****
      *****               Source File Name                                         *****
      *****               Member Name                                              *****
      *****     Returns : Member Text                                              *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-05-18   ISP       Initial creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P MbrText         B                   Export

      /Title  Proto-Type definition section
     D MbrText         PI            50A
     D  $Library                     10A   CONST
     D  $FileName                    10A   CONST
     D  $MbrName                     10A   CONST

      /Title  Data definition section
      ***?Procedure Work Variables
     DText             S             50A                                        Centering Text
     DLength           S             10I 0 Inz(%Size(QUSM0200))
     DFormat           S              8A   Inz('MBRD0200')
     DMbrName          S                   Like($MbrName)
     DOverride         S              1A

      ***?Default Field Overlay Data Structure
     DOverlays         DS
     D SourceFile                    20A
     D  FileName                     10A   Overlay(SourceFile:01)
     D  Library                      10A   Overlay(SourceFile:11)

      ***?Record structure for QUSRMBRD MBRD0200 format
     DQUSM0200         DS                                                       Qdb Mbrd0200
     D QUSBRTN03               1      4B 0                                      Bytes Returned
     D QUSBAVL04               5      8B 0                                      Bytes Available
     D QUSDFILN00              9     18                                         Db File Name
     D QUSDFILL00             19     28                                         Db File Lib
     D QUSMN03                29     38                                         Member Name
     D QUSFILA01              39     48                                         File Attr
     D QUSST01                49     58                                         Src Type
     D QUSCD03                59     71                                         Crt Date
     D QUSSCD                 72     84                                         Src Change Date
     D  ChgDate                       7S 0 Overlay(QUSSCD:1)
     D  ChgTime                       6S 0 Overlay(QUSSCD:8)
     D TextDesc               85    134                                         Text Desc
     D QUSSFIL01             135    135                                         Src File
     D QUSEFIL               136    136                                         Ext File
     D QUSLFIL               137    137                                         Log File
     D QUSOS                 138    138                                         Odp Share
     D QUSERVED12            139    140                                         Reserved
     D QUSNBRCR              141    144B 0                                      Num Cur Rec
     D QUSNBRDR              145    148B 0                                      Num Dlt Rec
     D QUSDSS                149    152B 0                                      Dat Spc Size
     D QUSAPS                153    156B 0                                      Acc Pth Size
     D QUSNBRDM              157    160B 0                                      Num Dat Mbr
     D QUSCD04               161    173                                         Change Date
     D QUSSD                 174    186                                         Save Date
     D QUSRD                 187    199                                         Rest Date
     D QUSED                 200    212                                         Exp Date
     D QUSNDU                213    216B 0                                      Nbr Days Used
     D QUSDLU                217    223                                         Date Lst Used
     D QUSURD                224    230                                         Use Reset Date
     D QUSRSV101             231    232                                         Reserved1
     D QUSDSSM               233    236B 0                                      Data Spc Sz Mlt
     D QUSAPSM               237    240B 0                                      Acc Pth Sz Mlt
     D QUSMTC                241    244B 0                                      Member Text Ccsid
     D QUSOAI                245    248B 0                                      Offset Add Info
     D QUSLAI                249    252B 0                                      Length Add Info
     D QUSNCRU               253    256U 0                                      Num Cur Rec U
     D QUSNDRU               257    260U 0                                      Num Dlt Rec U
     D QUSRSV203             261    266                                         Reserved2

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   10A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      ***?Initialize Variables
     C                   Eval      Library = $Library
     C                   Eval      FileName = $FileName
     C                   Eval      MbrName = $MbrName

      ***?Call Retrieve Member Description API (QUSRMBRD)
     C                   Call(E)   'QUSRMBRD'
     C                   Parm                    QUSM0200
     C                   Parm                    Length
     C                   Parm                    Format
     C                   Parm                    SourceFile
     C                   Parm                    MbrName
     C                   Parm      '1'           Override
     C                   Parm                    apiError

     C                   If        apiMsgID = *Blanks
     C                   Eval      Text = TextDesc
     C                   Else
     C                   Eval      Text = *Blanks
     C                   EndIf

     C                   Return    Text

     P                 E
      **********************************************************************************

      /Title  Procedure - RtvSysName
      **********************************************************************************
      *****  Procedure:?RtvSysName                               Date:?2004-07-20  *****
      **********************************************************************************
      *****     Purpose : Retrieve System Name procedure.                          *****
      *****       Input : SysName = name of system returned.                       *****
      *****     Returns : 0 = Success                                              *****
      *****               negative value if an error occurred.  See below          *****
      *****               r a list of possible negative values.                    *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2005-05-20   ISP                                                      *****
      *****                                                                        *****
      **********************************************************************************
     P RtvSysName      B                   Export
     D RtvSysName      PI            10I 0
     D   SysName                      8A

     D QWCRNETA        PR                  ExtPgm('QWCRNETA')
     D   RcvVar                   32766A   OPTIONS(*VARSIZE)
     D   RcvVarLen                   10I 0 const
     D   NbrNetAtr                   10I 0 const
     D   AttrNames                   10A   const
     D   ErrorCode                  256A

      ***?Error code structure
     D EC              DS
     D  EC_BytesP              1      4B 0 INZ(256)                             Bytes Provided
     D  EC_BytesA              5      8B 0 INZ(0)                               Bytes Available
     D  EC_MsgID               9     15                                         Msg ID of Error Msg
     D  EC_Reserve            16     16                                         Reserved
     D  EC_MsgDta             17    256                                         Msg Data of Error

      ***?Receiver variable for QWCRNETA with only one attribute
     D RV              ds
     D   RV_Attrs                    10I 0                                      Number of Attrs ret
     D   RV_Offset                   10I 0                                      Offset to first att
     D   RV_Data                      1A   DIM(1000)                            Add'l data returned

      ***?Network attribute structure
     D p_NA            S               *
     D NA              ds                  based(p_NA)
     D   NA_Attr                     10A                                        Attribute Name
     D   NA_Type                      1A                                        Type of Data.  C,B
     D   NA_Status                    1A                                        L=Locked, Blank=Norm
     D   NA_Length                   10I 0                                      Length of Data
     D   NA_DataChr                1000A                                        Actual Data(Char)
     D   NA_DataInt                  10I 0 overlay(NA_DataChr:1)                Actual Data(Bin)

      ***?Call API to get system name - -1 = API returned an error
     C                   CallP     QWCRNETA(RV: %size(RV): 1: 'SYSNAME': EC)
     C                   If        EC_BytesA > 0
     C                   Return    -1
     C                   EndIf

      ***?-2 = RcvVar contained data that we dont understand :(
     C                   If        RV_Attrs <> 1
     C                               or RV_Offset < 8
     C                               or RV_Offset > 1000
     C                   Return    -2
     C                   EndIf

      ***?Attach NetAttr structure
     C                   Eval      RV_Offset = RV_Offset - 7
     C                   Eval      p_NA = %addr(RV_Data(RV_Offset))

      ***?-3 = NetAttr structure had data that we don't understand :(
     C                   If        NA_Attr <> 'SYSNAME'
     C                               or NA_Length < 1
     C                               or NA_Length > 8
     C                   Return    -3
     C                   EndIf

      ***?-4 = Network attributes are locked
     C                   If        NA_Status = 'L'
     C                   Return    -4
     C                   EndIf

      ***?Successful
     C                   Eval      SysName = %subst(NA_DataChr:1:NA_Length)
     C                   Return    0
     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - FileInfo
      **********************************************************************************
      *****  Procedure:?FileInfo                                 Date:?2005-09-19  *****
      **********************************************************************************
      *****     Purpose: Retrieve File Information.                                *****
      *****       Input : File Library                                             *****
      *****               File Name                                                *****
      *****               File Member                                              *****
      *****     Returns : File Records                                             *****
      *****               File Size                                                *****
      *****               Index Size                                               *****
      *****               Deleted Records                                          *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-06-07   FRT       Initial creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P FileInfo        B                   Export

      /Title  Proto-Type definition section
     D FileInfo        PI            50A
     D  $Library                     10A   CONST
     D  $FileName                    10A   CONST
     D  $MemberName                  10A   CONST

      /Title  Data definition section
      ***?Procedure Work Variables
     DLength           S             10I 0 Inz(%Size(MBRD0200))
     DFormat           S              8A   Inz('MBRD0200')
     DOverride         S              1A

      ***?Default Field Overlay Data Structure
     DOverlays         DS
     D File                          20A
     D  FileName                     10A   Overlay(File:01)
     D  Library                      10A   Overlay(File:11)

      ***?Type Definition for the OBJL0300 format.
     DMBRD0200         DS                                                       Qus MBRD0200
     D BytesRet                      10I 0                                      Bytes Returned
     D BytesAval                     10I 0                                      Bytes Available
     D DatabaseFile                  10A                                        Database file name
     D DatabaseLib                   10A                                        Database library nam
     D MemberName                    10A                                        Member name
     D FileAttr                      10A                                        File attribute
     D SourceType                    10A                                        Source type
     D CreateStamp                   13A                                        Creation date/Time
     D Changestamp                   13A                                        Last Change
     D MemberText                    50A                                        Member text
     D SourceFile                     1A                                        Source file
     D RemoteFile                     1A                                        Remote File
     D LogPhyFile                     1A                                        Log or Phy File
     D ODPSharing                     1A                                        ODP Sharing
     D Reserved1                      2A                                        Reserved
     D CurrentRecs                   10I 0                                      Current Records
     D DeletedRecs                   10I 0                                      Deleted Records
     D DataSpace                     10I 0                                      Data Space
     D IndexSpace                    10I 0                                      Index Space
     D Members                       10I 0                                      Phy file Members
     D MbrChgStamp                   13A                                        Chg Date & Time
     D MbrSavStamp                   13A                                        Save Date & Time
     D MbrRstStamp                   13A                                        Rst Date & Time
     D ExpireDate                     7A                                        Expire Date
     D Reserved2                      6A                                        Reserved
     D DaysUsed                      10I 0                                      Number of Days used
     D LastUsed                       7A                                        Date Last Used
     D ResetDate                      7A                                        use Reset Date
     D Reserved3                      2A                                        Reserved
     D DataMulti                     10I 0                                      Data Space Mltplyr
     D AccessMulti                   10I 0                                      Acces Space Mltplyr
     D MbrTextCCSID                  10I 0                                      Mbr Text Desc CCSID
     D OffsetAddtl                   10I 0                                      Offset to addtl
     D LenAddtl                      10I 0                                      Length to addtl
     D ExtRecords                    10I 0                                      Current Records
     D ExtDeleted                    10I 0                                      Deleted Records
     D Reserved4                      6A                                        Reserved

      ***?Return Value Data Structure
     D Return          DS
     D  ReturnValue                  50A
     D   Records                     10S 0 Overlay(ReturnValue:1)               Number of Records
     D   Deleted                     10S 0 Overlay(ReturnValue:*Next)           Deleted Records
     D   DataSize                    10S 0 Overlay(ReturnValue:*Next)           Size of Data
     D   IndexSize                   10S 0 Overlay(ReturnValue:*Next)           Size of Index

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      ***?Initialize Variables
     C                   Eval      Library = $Library
     C                   Eval      FileName = $FileName
     C                   Eval      MemberName = $MemberName

      ***?Call Retrieve Object Description API (QUSROBJD)
     C                   Call(E)   'QUSRMBRD'
     C                   Parm                    MBRD0200
     C                   Parm                    Length
     C                   Parm                    Format
     C                   Parm                    File
     C                   Parm                    Membername
     C                   Parm      '0'           Override
     C                   Parm                    apiError

     C                   If        apiMsgID = *Blanks
     C                   Eval      Records   = CurrentRecs
     C                   Eval      Deleted   = DeletedRecs
     C                   Eval      DataSize  = DataSpace
     C                   Eval      IndexSize = IndexSpace
     C                   Else
     C                   Eval      Records   = *Zeros
     C                   Eval      Deleted   = *Zeros
     C                   Eval      DataSize  = *Zeros
     C                   Eval      IndexSize = *Zeros
     C                   EndIf

     C                   Return    ReturnValue

     P                 E
      **********************************************************************************

     ?/Eject
      /Title  Procedure - Retrieve File Format Name
      **********************************************************************************
      *****  Procedure:?FileFormatName                           Date:?2007-03-06  *****
      **********************************************************************************
      *****     Purpose: Retrieve File Format Name                                 *****
      *****       Input : File Library                                             *****
      *****               File Name                                                *****
      *****     Returns : Format Name                                              *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2007-03-06   FRT       Initial creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P FileFormatName  B                   Export

      /Title  Proto-Type definition section
     D FileFormatName  PI            10A
     D  $Library                     10A   CONST
     D  $FileName                    10A   CONST

     D GetFormatName   PR                  EXTPGM('QUSRMBRD')
     D  $MBRD0300                          Like(MBRD0300)
     D  $Length                            Like(Length) CONST
     D  $Format                            Like(Format) CONST
     D  $File                              Like(File) CONST
     D  $Membername                        Like(Membername) CONST
     D  $Override                          Like(Override) CONST
     D  $apiError                          Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DLength           S             10I 0 Inz(%Size(MBRD0300))
     DFormat           S              8A   Inz('MBRD0300')
     DOverride         S              1A

      ***?Default Field Overlay Data Structure
     DOverlays         DS
     D File                          20A
     D  FileName                     10A   Overlay(File:01)
     D  Library                      10A   Overlay(File:11)

      ***?Type Definition for the MBRD0300 format.
     DMBRD0300         DS                                                       Qus MBRD0300
     D BytesRet                      10I 0                                      Bytes Returned
     D BytesAval                     10I 0                                      Bytes Available
     D DatabaseFile                  10A                                        Database file name
     D DatabaseLib                   10A                                        Database library nam
     D MemberName                    10A                                        Member name
     D FileAttr                      10A                                        File attribute
     D SourceType                    10A                                        Source type
     D CreateStamp                   13A                                        Creation date/Time
     D Changestamp                   13A                                        Last Change
     D MemberText                    50A                                        Member text
     D SourceFile                     1A                                        Source file
     D RemoteFile                     1A                                        Remote File
     D LogPhyFile                     1A                                        Log or Phy File
     D ODPSharing                     1A                                        ODP Sharing
     D Reserved1                      2A                                        Reserved
     D CurrentRecs                   10I 0                                      Current Records
     D DeletedRecs                   10I 0                                      Deleted Records
     D DataSpace                     10I 0                                      Data Space
     D IndexSpace                    10I 0                                      Index Space
     D Members                       10I 0                                      Phy file Members
     D MbrChgStamp                   13A                                        Chg Date & Time
     D MbrSavStamp                   13A                                        Save Date & Time
     D MbrRstStamp                   13A                                        Rst Date & Time
     D ExpireDate                     7A                                        Expire Date
     D Reserved2                      6A                                        Reserved
     D DaysUsed                      10I 0                                      Number of Days used
     D LastUsed                       7A                                        Date Last Used
     D ResetDate                      7A                                        use Reset Date
     D Reserved3                      2A                                        Reserved
     D DataMulti                     10I 0                                      Data Space Mltplyr
     D AccessMulti                   10I 0                                      Acces Space Mltplyr
     D MbrTextCCSID                  10I 0                                      Mbr Text Desc CCSID
     D OffsetAddtl                   10I 0                                      Offset to addtl
     D LenAddtl                      10I 0                                      Length to addtl
     D ExtRecords                    10I 0                                      Current Records
     D ExtDeleted                    10I 0                                      Deleted Records
     D Reserved4                      6A                                        Reserved
     D JoinMember                     1A                                        Join Member
     D AccessPathMnt                  1A                                        Access Path Maint
     D SQLFileType                   10A                                        SQL File Type
     D Reserved5                      1A                                        Reserved
     D AllowRead                      1A                                        Allow Read Operation
     D AllowWrite                     1A                                        Allow Write Operate
     D AllowUpdate                    1A                                        Allow Update Operate
     D AllowDelete                    1A                                        Allow Delete Operate
     D Reserved6                      1A                                        Reserved
     D RecordstoForce                10I 0                                      Records to Force Wrt
     D MaximumDeleted                10I 0                                      Maximum % Deleted rc
     D InitialRecords                10I 0                                      Initial # of Records
     D Inc#ofRecords                 10I 0                                      Increment # Records
     D Max#Increments                10I 0                                      Maximum # of Incremt
     D Cur#Increments                10I 0                                      Current # of Incremt
     D RecordCapicity                10I 0                                      Record Capicity
     D FormatSelector                10A                                        Record Fmt Sltr Pgm
     D FmtSltrLibry                  10A                                        Record Fmt Sltr Lib
     D ConstraintIdx                  5I 0                                      Number of Con Idxs
     D OffsetConIdx                  10I 0                                      Offset to Con Idxs
     D Reserved7                     46A                                        Reserved
     D BasedOnFile                  112A                                        Based On File Data
     D ConstraintAry                320A                                        Constraint Indx Info

      ***?Based on File Data
     D PhysicalFile    DS
     D  PFFileName                   10A                                        Physical File Name
     D  PFLibrary                    10A                                        Library Name
     D  PFMember                     10A                                        Member Name
     D  PFFormat                     10A                                        Format Name
     D  PFFormat#                    10I 0                                      Format Number
     D  PFCurRecords                 10I 0                                      Current # of Records
     D  PFDeletedRec                 10I 0                                      Deleted # of Records
     D  PFAcPathSize                 10I 0                                      Access Path Size
     D  PFAcPathSizeM                10I 0                                      Access Path Size Mlt
     D  PFAcPathShrd                  1A                                        Access Path Shared
     D  PFAcPathValid                 1A                                        Access Path Valid
     D  PFAcPathHeld                  1A                                        Access Path Held
     D  PFAcPathFile                 10A                                        Access Path File
     D  PFAcPathLib                  10A                                        Access Path Library
     D  PFAcPathMbr                  10A                                        Access Path Member
     D  PFAcPathJrnld                 1A                                        Access Path Journald
     D  Reserved8                     2A                                        Reserved
     D  NbrRecords                   10I 0                                      Number of Records
     D  DltRecords                   10I 0                                      Dlt # of Records
     D  Reserved9                     8A                                        Reserved

      ***?Standard API Error Data Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(%size(apiError))
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrData                   50A

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /Free
         // **?Initialize Variables
          Library = $Library;
          FileName = $FileName;

         // **?Call Retrieve Object Description API (QUSROBJD)
          GetFormatName(MBRD0300:Length:Format:File:'*FIRST':'0':apiError);

          If apiMsgID = *Blanks;
            PhysicalFile = BasedonFile;
            If FileAttr = 'PF';
              PFFormat = %Trim(PFAcPathFile) + 'R';
            EndIf;
          EndIf;

          Return PFFormat;

      /End-Free
     P                 E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?InteractiveJob                           Date:?2007-01-01  *****
      **********************************************************************************
      *****     Purpose: Check if job is running interactive                       *****
      *****       Input : None                                                     *****
      *****     Returns : True or False (1/0)                                      *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2007-01-01   ISP       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     PInteractiveJob   B                   Export

      /Title  Proto-Type definition section
      ***?Check if job is running interactive
     DInteractiveJob   PI              N

      ***?Retrieve Job Information (QUSRJOBI) API
     DRetrieveJobInfo...
     D                 PR                  EXTPGM('QUSRJOBI')
     D $Receive                            Like(JobStatus)
     D $JobLength                    10I 0
     D $JobFormat                     8A
     D $JobName                      26A   CONST
     D $JobIndent                    15A   CONST
     D $ErrCode                            Like(apiError)

      /Title  Data definition section
      ***?Procedure Work Variables
     DInteractive      S               N
     DJobLength        S             10I 0 Inz(%Size(JobStatus))
     DJobFormat        S              8A   Inz('JOBI0100')

      ***?Job Status Data-Structure
     DJobStatus        DS
     D JobBytesRtn                   10I 0 Overlay(JobStatus:*Next)
     D JobBytesAvl                   10I 0 Overlay(JobStatus:*Next)
     D JobName                       10A   Overlay(JobStatus:*Next)
     D JobUser                       10A   Overlay(JobStatus:*Next)
     D JobNumber                      6A   Overlay(JobStatus:*Next)
     D JobIdentifier                 16A   Overlay(JobStatus:*Next)
     D JobStatusCode                 10A   Overlay(JobStatus:*Next)
     D JobType                        1A   Overlay(JobStatus:*Next)
     D JobSubtype                     1A   Overlay(JobStatus:*Next)
     D JobReserv1                     2A   Overlay(JobStatus:*Next)
     D JobRunPriority                10I 0 Overlay(JobStatus:*Next)
     D JobTimeSlice                  10I 0 Overlay(JobStatus:*Next)
     D JobDefaultWait                10I 0 Overlay(JobStatus:*Next)
     D JobPurge                      10A   Overlay(JobStatus:*Next)

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
      /FREE
         // **?Center Data in Field

       CallP RetrieveJobInfo(JobStatus:JobLength:JobFormat:
                             '*':' ':apiError);
       If JobType = 'I';
         Interactive = *ON;
       Else;
         Interactive = *OFF;
       EndIf;

       Return Interactive;

      /END-FREE
     P                 E
      **********************************************************************************

