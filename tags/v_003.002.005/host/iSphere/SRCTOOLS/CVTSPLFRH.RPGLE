      **********************************************************************************
      *** Program name:?CVTSPLFRH                                  Date:?2005-08-09  ***
      **********************************************************************************
      ***    Copyright - Free RPG/i5 Tools                                           ***
      ***       E-Mail - Utilities@FreeRPGTools.com                                  ***
      ***  Description - Convert Spool File to HTML                                  ***
      ***                                                                            ***
      ***    Maintenance History: (Most recent is listed first)                      ***
      ***                                                                            ***
      ***    Project  Date of      Pgmr                                              ***
      ***     Id.   Last Change Initials Description of Changes                  ***
      ***   xxxxxxx  2006-01-10   FRT       Initial Creation                        ***
      ***                                                                           ***
      **********************************************************************************
      ***?                             Creation Keywords                             ***
      *** Keyword      Keyword Data                                                ***
      *** *NONE         No special keywords are required                            ***
      ***                                                                           ***
      **********************************************************************************
      ***?                                 Overrides                                 ***
      *** DB-File      Override                                                    ***
      *** *NONE         No Overrides are used for this program                      ***
      ***                                                                           ***
      **********************************************************************************
      ***?                           Indicator Usage                                 ***
      *** XX    Description                                                       ***
      *** LR      Last Record Processed Indicator                                   ***
      ***                                                                           ***
      **********************************************************************************
      ***?                             Parameters                                    ***
      ***  Field     Length Usage Description                                   ***
      ***                                                                            ***
      **********************************************************************************
      *** Files Used:                                                                ***
      ***  Usage - I=Input, O=Output, W=Work, R=Reference, P=Print                   ***
      ***          D=Display, U=Update, C=Communication                              ***
      ***                                                                            ***
      ***Usage  Name         Description                                          ***
      ***   I     InputFile     Input File                                           ***
      ***                                                                            ***
      **********************************************************************************
      *** STRPREPRC Compile Options:                                                 ***
      ***   >>PRE-COMPILER<<                                                         ***
      ***     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                ***
      ***                              SRCFILE(&SL/&SF) SRCMBR(&SM);                 ***
      ***       >>COMPILE<<                                                          ***
      ***         >>PARM<<  DFTACTGRP(*NO);                                          ***
      ***         >>PARM<<  ACTGRP(*CALLER);                                         ***
      ***         >>PARM<<  DBGVIEW(*NONE);                                          ***
      ***         >>PARM<<  OPTIMIZE(*FULL);                                         ***
      ***         >>PARM<<  TGTRLS(&TR);                                             ***
      ***       >>END-COMPILE<<                                                      ***
      ***       >>EXECUTE<<                                                          ***
      ***   >>END-PRE-COMPILER<<                                                     ***
      **********************************************************************************
      /Title  Control Specifications
     H BNDDIR('ISPHERE')
     /Define FRT_Header
     /COPY SRCTOOLS,ISTPRCR
     /Undefine FRT_Header

      /Title  File definition section
      ***?Primary Work File
     Fcvtwork02 IF   F  382        DISK

      ***?Secondary Work File
     Fcvtwork01 UF A F  378        DISK

      /Title  Proto-Type definition section
      ***?Program Entry Parameters
     DConvertHTML      PR                  ExtPgm('CVTSPLFRH')
     D paTitle                       50A                                        Page Title
     D piPageLen                     10I 0                                      Page Length

      ***?Program Interface Parameters
     DConvertHTML      PI
     D paTitle                       50A                                        Page Title
     D piPageLen                     10I 0                                      Page Length

     D HTMLHeader      PR

     D HTMLFooter      PR

     D Convert         PR

     D Merge           PR                  LIKE(zaOutput)
     D    iaOutput                         LIKE(zaOutput)
     D    iaInput                          LIKE(zaInput)

     D SpceLines       PR
     D    isSpceLine                       LIKE(zzSpceLine)

     D SkipLines       PR
     D    isSkipLine                       LIKE(zzSkipLine)

     /Define Prototypes
     /COPY SRCTOOLS,ISTPRCR
     /Undefine Prototypes

      /Title  Data definition section
      ***?Define Program Standalone Fields
     DCurrDate         S               D   INZ(*JOB)
     DCurrTime         S               T   INZ(*SYS)
     DCommand          S           1024A                                        QCMDEXC Command
     DLength           S             15P 5 Inz(%Size(Command))                  QCMDEXC Length
     DError            S               N                                        Error Indicator
     DIdx              S              5S 0                                      Index Field
     DTick             S              1A   INZ(X'7D')                           Single Quote
     D aaHeader        S             80A   DIM(2) CTDATA PERRCD(1)              Std Hdr Lines
     D aaFooter        S             80A   DIM(1) CTDATA PERRCD(1)              Std Ftr Lines
     D wiLine          S             10I 0                                      Line Counter

      ***?Compile-Time Arrays

      ***?Default Field Overlay Data Structure
     DOverlays         DS

      ***?Input spooled file data including control characters
     D InputData       DS
     D   zaSkipLine                   3A
     D   zzSkipLine                   3S 0 OVERLAY(zaSkipLine:1)
     D   zaSpceLine                   1A
     D   zzSpceLine                   1S 0 OVERLAY(zaSpceLine:1)
     D   zaInput                    378A

      ***?Output HTML-format data
     D OutputData      DS
     D   zaOutput                   378A

      ***?Program Status Data Structure
     DProgramStatus   SDS
     D Program                       10A   Overlay(ProgramStatus:1)             Current Program
     D MsgId                          7A   Overlay(ProgramStatus:40)
     D ProgramLib                    10A   Overlay(ProgramStatus:81)            Program Library
     D JobName                       10A   Overlay(ProgramStatus:244)           Job Name
     D JobNumber                      6S 0 Overlay(ProgramStatus:264)           Job Number
     D CurrUser                      10A   Overlay(ProgramStatus:358)           Current User

      ***?Standard API Error Code Data-Structure
     D apiError        DS                  Inz
     D  apiLen                       10I 0 Inz(0)
     D  apiRLen                      10I 0
     D  apiMsgID                      7A
     D  apiResv1                      1A   Inz(X'00')
     D  apiErrText                   24A

     ?/Eject
      /Title  M A I N  L I N E
      **********************************************************************************
      *****                            M A I N  L I N E                            *****
      **********************************************************************************
     C/Free
       // ***?Output HTML header lines
       HTMLHeader();

       wiLine = 1;

       // ***?Convert spool file lines to HTML
       READ cvtwork02 InputData;
       *INLR = %EOF;
       DOW *INLR = *OFF;
         Convert();
         READ cvtwork02 InputData;
         *INLR = %EOF;
       EndDo;

       // ***?Output HTML footer lines
       HTMLFooter();

       // ***?Exit program
        Exsr EndProgram;
       // **********************************************************************************

     ?/Eject
      /Title  Controlled Program has Been Requested
       // **********************************************************************************
       // *****  Subroutine : EndProgram                                               *****
       // *****     Purpose : Controlled Program has Been Requested.                   *****
       // **********************************************************************************
        Begsr EndProgram;

       // ***?Exit program
        *INLR = *ON;
        Return;

        EndSR;
       // **********************************************************************************

     ?/Eject
      /Title  Process all one time functions
       // **********************************************************************************
       // *****  Subroutine : *INZSR                                                   *****
       // *****     Purpose : Process all one time functions.                          *****
       // **********************************************************************************
        Begsr *INZSR;

        EndSR;
       // **********************************************************************************

      /End-free

      **********************************************************************************
      *****  Procedure:?HTMLHeader                               Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create HTML header lines                                  *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P HTMLHeader      B

      /Title  Proto-Type definition section
     D HTMLHeader      PI

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       zaOutput = aaHeader(1);
       WRITE cvtwork01 OutputData;

       IF paTitle <> '*NONE';
         zaOutput   = '';
         WRITE cvtwork01 OutputData;
       EndIf;

       zaOutput = aaHeader(2);
       WRITE cvtwork01 OutputData;

      /END-FREE
     P HTMLHeader      E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?HTMLFooter                               Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create HTML footer line                                   *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P HTMLFooter      B

      /Title  Proto-Type definition section
     D HTMLFooter      PI

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       zaOutput = aaFooter(1);
       WRITE cvtwork01 OutputData;

      /END-FREE
     P HTMLFooter      E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?Convert                                  Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Convert spooled file data to HTML text                    *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P Convert         B

      /Title  Proto-Type definition section
     D Convert         PI

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?If 'space' position is zero, 'overprint' previous line
       IF zaSpceLine = '0';

         SETGT *HIVAL cvtwork01;
         READP cvtwork01 OutputData;
         *IN99 = %EOF;
         zaOutput = Merge(zaOutput:zaInput);
         UPDATE cvtwork01 OutputData;

       ELSE;

       // ***?Skip to a line if specified
         IF zaSkipLine <> *Blanks;
           SkipLines(zzSkipLine);
         EndIf;

       // ***?Space a number of lines if specified
         IF zaSpceLine <> *Blanks;
           SpceLines(zzSpceLine);
         EndIf;

       // ***?'Print' line
         zaOutput   = zaInput;
         WRITE cvtwork01 OutputData;

       EndIf;

       RETURN;

      /END-FREE
     P Convert         E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?Merge                                    Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Merge two overlaid lines of text                          *****
      *****       Input : Output Text                                              *****
      *****               Input Text                                               *****
      *****     Returns : Output Text                                              *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P Merge           B

      /Title  Proto-Type definition section
     D Merge           PI                  LIKE(zaOutput)
     D    iaOutput                         LIKE(zaOutput)
     D    iaInput                          LIKE(zaInput)

      /Title  Data definition section
      ***?Procedure Work Variables
     D laOutput        S                   LIKE(zaOutput)
     D i               S              5I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       i = 1;
       DOW i <= %size(iaInput )
             and  i <= %size(iaOutput)
             and  i <= %size(laOutput);
         IF %subst(iaInput:i:1) = *BLANK;
           %subst(laOutput:i:1) = %subst(iaOutput:i:1);
         ELSE;
           %subst(laOutput:i:1) = %subst(iaInput :i:1);
         EndIf;
         i = i + 1;
       EndDo;

       RETURN laOutput;

      /END-FREE
     P Merge           E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?SkipLines                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Skip to a given line number                               *****
      *****       Input : Skip to Line Number                                      *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P SkipLines       B

      /Title  Proto-Type definition section
     D SkipLines       PI
     D    isSkipLine                       LIKE(zzSkipLine)

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       zaOutput = *Blanks;

       IF wiLine > isSkipLine;

         DOW wiLine < piPageLen;
           WRITE cvtwork01 OutputData;
           wiLine = wiLine + 1;
         EndDo;

         // zaOutput   = '-------------------------';
         // WRITE cvtwork01 OutputData;

         zaOutput = *Blanks;
         wiLine = 1;

       EndIf;

       DOW wiLine < isSkipLine - 1;
         WRITE cvtwork01 OutputData;
         wiLine = wiLine + 1;
       EndDo;

       RETURN;

      /END-FREE
     P SkipLines       E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?SpceLines                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Space a number of lines                                   *****
      *****       Input : Number of Spaces                                         *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P SpceLines       B

      /Title  Proto-Type definition section
     D SpceLines       PI
     D    isSpceLine                       LIKE(zzSpceLine)

      /Title  Data definition section
      ***?Procedure Work Variables
     D liCount         S              5I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       wiLine  = wiLine  + 1;
       zaOutput = *Blanks;
       DOW liCount < isSpceLine - 1;
         WRITE cvtwork01 OutputData;
         wiLine  = wiLine  + 1;
         liCount = liCount + 1;
       EndDo;

       RETURN;

      /END-FREE
     P SpceLines       E
      **********************************************************************************
**
<html><head><title>xxxx</title></head><body>
<table><tr><td> <pre>
**
</pre></td></tr></table></body></html>
