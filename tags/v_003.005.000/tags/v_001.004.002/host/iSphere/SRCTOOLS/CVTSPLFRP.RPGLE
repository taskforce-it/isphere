      /Title Convert Spool File to HTML or PDF
      **********************************************************************************
      *** Program name:?CVTSPLFRP                                  Date:?2005-08-09  ***
      **********************************************************************************
      ***    Copyright - Free RPG/i5 Tools                                           ***
      ***       E-Mail - Utilities@FreeRPGTools.com                                  ***
      ***  Description - Convert Spool File to PDF                                   ***
      ***                                                                            ***
      ***    Maintenance History: (Most recent is listed first)                      ***
      ***                                                                            ***
      ***    Project  Date of      Pgmr                                              ***
      ***     Id.   Last Change Initials Description of Changes                  ***
      ***   xxxxxxx  2008-04-08   FRT       Globalize idx Field                     ***
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
      *** STRPREPRC Compile Options:                                                 ***
      ***   >>PRE-COMPILER<<                                                         ***
      ***     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                ***
      ***                              SRCFILE(&SL/&SF) SRCMBR(&SM);                 ***
      ***       >>COMPILE<<                                                          ***
      ***         >>PARM<<  DFTACTGRP(*NO);                                          ***
      ***         >>PARM<<  ACTGRP(*CALLER);                                         ***
      ***         >>PARM<<  DBGVIEW(*NONE);                                          ***
      ***         >>PARM<<  OPTIMIZE(*FULL);                                         ***
      ***         >>PARM<<  TGTRLS(V5R4M0);                                          ***
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
     Fcvtwork02 IF   F  382        DISK

     Fcvtwork01 UF A F  378        DISK

      /Title  Proto-Type definition section
      ***?Program Entry Parameters
     DConvertPDF       PR                  ExtPgm('CVTSPLFRP')
     D paTitle                       50A                                        Report Title
     D SpoolInfo                           Like(SplInfo)                        Spool Information
     D paBookmark                     7A                                        Bookmark Option
     D BookMarkPos                         Like(BMarkPos)                       BookMark POS Option
     D BookMarkKey                         Like(BMarkKey)                       BookMark KEY Option

      ***?Program Interface Parameters
     DConvertPDF       PI
     D paTitle                       50A                                        Report Title
     D SpoolInfo                           Like(SplInfo)                        Spool Information
     D paBookmark                     7A                                        Bookmark Option
     D BookMarkPos                         Like(BMarkPos)                       BookMark POS Option
     D BookMarkKey                         Like(BMarkKey)                       BookMark KEY Option

     D WritePDF        PR
     D   iaOutput                   378A   CONST OPTIONS(*VARSIZE)

     D AddEscape       PR           378A
     D   iaInput                    378A

     D PDFHeader       PR

     D PDFPages        PR

     D PDFTrailer      PR

     D NewPage         PR

     D EndPage         PR

     D NumToText       PR            10A
     D    iiNum                      10I 0 CONST

     D NewObject       PR

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
     DIdx              S             10I 0                                      Index Field
     Didx1             S             10I 0                                      Index Field
     DTick             S              1A   INZ(X'7D')                           Single Quote
     D aiObject        S             10I 0 DIM(32767)                           PDF Object Array
     D aaStart         S             10A   DIM(32767)                           Str Pos of PDF Optns
     D wiObject        S             10I 0                                      Current Object #
     D wiChrCount      S             10I 0                                      Count Bytes Written
     D wiPage          S             10I 0                                      Current Page Number
     D wiStart         S             10I 0                                      Str Pos of Text
     D waBookmark      S            378A                                        Bookmark Text
     D wiOccurs        S              5I 0                                      Occr of BkMark Key

      ***?Compile-Time Arrays

      ***?Default Field Overlay Data Structure
     DOverlays         DS

      ***?spooled file information returned by API
     D SplInfo         DS
     D  zaReturned                   10I 0
     D  zaAvailabl                   10I 0
     D  zaIntJobId                   16A
     D  zaSplfId                     16A
     D  zaJobName                    10A
     D  zaUser                       10A
     D  zaJobNbr                      6A
     D  zaSplFile                    10A
     D  zaSplNbr                     10I 0
     D  zaFormType                   10A
     D  zaUsrDta                     10A
     D  zaStatus                     10A
     D  zaFilAvail                   10A
     D  zaHold                       10A
     D  zaSave                       10A
     D  ziPages                      10I 0
     D  ziCurrPage                   10I 0
     D  ziFromPage                   10I 0
     D  ziToPage                     10I 0
     D  ziLastPage                   10I 0
     D  ziRestart                    10I 0
     D  ziCopies                     10I 0
     D  ziCopyRem                    10I 0
     D  ziLPI                        10I 0
     D  ziCPI                        10I 0
     D  ziOutPty                      2A
     D  zaOutq                       10A
     D  zaOutqLib                    10A
     D  zaOpenDate                    7A
     D  zaOpenTime                    6A
     D  zaPrtFile                    10A
     D  zaPrtfLib                    10A
     D  zaPgmName                    10A
     D  zaPgmLib                     10A
     D  zaAcgCode                    15A
     D  zaPrtTxt                     30A
     D  ziRcdLen                     10I 0
     D  ziMaxRcds                    10I 0
     D  zaDevType                    10A
     D  zaPrtType                    10A
     D  zaDocName                    12A
     D  zaFlrName                    64A
     D  zaS36Proc                     8A
     D  zaFidelity                   10A
     D  zaRplUnprt                    1A
     D  zaRplChar                     1A
     D  ziPageLen                    10I 0
     D  ziPageWdth                   10I 0
     D  ziSepartrs                   10I 0
     D  ziOvrFlw                     10I 0
     D  zaDBCS                       10A
     D  zaDBCSExt                    10A
     D  zaDBCSSOSI                   10A
     D  zaDBCSRotn                   10A
     D  zaDBCSCPI                    10I 0
     D  zaGraphics                   10A
     D  zaCodePage                   10A
     D  zaFormDf                     10A
     D  zaFormDfLb                   10A
     D  ziDrawer                     10I 0
     D  zaFont                       10A
     D  zaS36SplId                    6A
     D  ziRotation                   10I 0
     D  ziJustify                    10I 0
     D  zaDuplex                     10A
     D  zaFoldRcds                   10A
     D  zaCtlChar                    10A
     D  zaAlign                      10A
     D  zaPrtQlty                    10A
     D  zaFormFeed                   10A
     D  zaVolumes                    71A
     D  zaLabels                     17A
     D  zaExchange                   10A
     D  zaCharCode                   10A
     D  ziTotRcds                    10I 0
     D  ziMultiUp                    10I 0
     D  zaFrontOvl                   10A
     D  zaFrtOvlLb                   10A
     D  znFOOffDwn                   15P 5
     D  znFOOffAcr                   15P 5
     D  zaBackOvl                    10A
     D  zaBckOvlLb                   10A
     D  znBOOffDwn                   15P 5
     D  znBOOffAcr                   15P 5
     D  zaUOM                        10A
     D  zaPagDfn                     10A
     D  zaPagDfnLb                   10A
     D  zaSpacing                    10A
     D  znPointSiz                   15P 5
     D  znFMOffDwn                   15P 5
     D  znFMOffAcr                   15P 5
     D  znBMOffDwn                   15P 5
     D  znBMOffAcr                   15P 5
     D  znPageLen                    15P 5
     D  znPageWdth                   15P 5
     D  zaMethod                     10A
     D  zaAFP                         1A
     D  zaChrSet                     10A
     D  zaChrSetLb                   10A
     D  zaCdePagNm                   10A
     D  zaCdePgeLb                   10A
     D  zaCdeFnt                     10A
     D  zaCdeFntLb                   10A
     D  zaDBCSFnt                    10A
     D  zaDBCSFntL                   10A
     D  zaUserDef                    10A
     D  zaReduce                     10A
     D  zaReserv1                     1A
     D  ziOutBin                     10I 0
     D  ziCCSID                      10I 0
     D  zaUserText                  100A
     D  zaSystem                      8A
     D  zaOrigId                      8A
     D  zaCreator                    10A

      ***?Bookmark *POS option parameters
     D BMarkPos        DS
     D   ziPosCount                   5I 0
     D   znPosLine                    3P 0
     D   znPosChar                    3P 0
     D   znPosLen                     3P 0

      ***?Bookmark *KEY option parameters
     D BMarkKey        DS
     D   ziKeyCount                   5I 0
     D   ziLen                        5I 0
     D   zaKeyStr                   378A
     D   znKeyOccur                   3P 0
     D   znKeyOff                     3P 0
     D   znKeyLen                     3P 0

      ***?Input spooled file data including control characters
     D InputData       DS
     D   zaSkipLine                   3A
     D   zzSkipLine                   3S 0 OVERLAY(zaSkipLine:1)
     D   zaSpceLine                   1A
     D   zzSpceLine                   1S 0 OVERLAY(zaSpceLine:1)
     D   zaInput                    378A

      ***?Output PDF-format data
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
       // ***?Output a PDF header
       PDFHeader();

       // ***?Create PDF page 'objects'
       PDFPages();

       // ***?Output a PDF trailer
       PDFTrailer();

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

        SpoolInfo = SplInfo;
        BookMarkPos = BMarkPos;
        BookMarkKey = BMarkKey;

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

        SplInfo  = SpoolInfo;
        BMarkPos = BookMarkPos;
        BMarkKey = BookMarkKey;

        EndSR;
       // **********************************************************************************

      /End-free

      **********************************************************************************
      *****  Procedure:?PDFHeader                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create a PDF 'header'                                     *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P PDFHeader       B

      /Title  Proto-Type definition section
     D PDFHeader       PI

      /Title  Data definition section
      ***?Procedure Work Variables
     D liPage          S             10I 0
     D liPageObj       S             10I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?Create catalog object
       WritePDF('%PDF-1.0');
       WritePDF('%âãÏÓ');
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF('/Type /Catalog');
       WritePDF('/Pages 5 0 R');
       WritePDF('/Outlines 2 0 R');
       WritePDF('/PageMode /UseOutlines');
       WritePDF('>>');
       WritePDF('endobj');

       // ***?Create outlines object
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF('/Type /Outlines');
       WritePDF('/Count '+%trim(NumToText(ziPages)));
       WritePDF(  '/First 9 0 R');

       WritePDF(  '/Last  '
           + %trim(NumToText((ziPages*4)+5))
           + ' 0 R');
       WritePDF('>>');
       WritePDF('endobj');

       // ***?Create procedures object
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('[/PDF /Text]');
       WritePDF('endobj');

       // ***?Create fonts object
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF ('/Type /Font');
       WritePDF ('/Subtype /Type1');
       WritePDF ('/Name /F1');
       WritePDF ('/BaseFont /Courier');
       WritePDF ('/Encoding /WinAnsiEncoding');
       WritePDF ('>>');
       WritePDF ('endobj');

       // ***?Create pages object
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF ('<<');
       WritePDF ('/Type /Pages');
       WritePDF('/Count '+%trim(NumToText(ziPages)));

       // ***?Write list of child pages
       liPage    = wiObject + 1;
       liPageObj = liPage;
       WritePDF (  '/Kids ['
           + %trim(NumToText(liPage))
           + ' 0 R');

       DOW liPage < ziPages + wiObject;
         liPage = liPage + 1;
         liPageObj = liPageObj + 4;
         WritePDF (  '       '
             + %trim(NumToText(liPageObj))
             + ' 0 R');
       EndDo;

       WritePDF ('       ]');

       WritePDF ('>>');
       WritePDF ('endobj');

      /END-FREE
     P PDFHeader       E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?PDFPages                                 Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create PDF pages                                          *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P PDFPages        B

      /Title  Proto-Type definition section
     D PDFPages        PI

      /Title  Data definition section
      ***?Procedure Work Variables
     D liLine          S             10I 0
     D liLength        S              5I 0
     D liChar          S              5I 0
     D liX             S              5I 0
     D liY             S              5I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?Create page object for first page
       wiPage = 0;
       liX = 0;

       // ***?Read spooled file data from input work file
       READ cvtwork02 InputData;
       *INLR = %EOF;

       DOW *INLR = *OFF;

       // ***?Skip to a line if specified, handling page throw if it occurs
         IF zaSkipLine <> *Blanks;
           IF zzSkipLine < liLine or liLine = 0;
             IF wiPage <> 0;
               EndPage();
             EndIf;
             NewPage();
             liLine = zzSkipLine;
             liY
                 = (612/ziPageLen) * (ziPagelen-liLine);
           ELSE;
             liY
                 = -((612/ziPageLen) * (zzSkipLine-liLine));
             liLine = zzSkipLine;
           EndIf;
         EndIf;

       // ***?Space a number of lines if specified
         IF zaSpceLine <> *Blanks;
           liLine = liLine + zzSpceLine;
           liY
               = -((612/ziPageLen) * zzSpceLine);
         EndIf;

       // ***?Set up bookmark if position option specified
         IF paBookmark = '*POS';
           IF liLine = znPosLine and waBookmark = *Blanks;
             waBookmark = %trim(%subst(zaInput  :
                 znPosChar:
                 znPosLen ));
           EndIf;
         EndIf;

       // ***?Set up bookmark if key option specified
         IF paBookmark = '*KEY';
           liChar = %Scan(%TrimR(zaKeyStr):zaInput:1);
           IF liChar > 0;
             wiOccurs = wiOccurs + 1;
             IF wiOccurs = znKeyOccur;
               liChar = liChar + znKeyOff;
               liLength = znKeyLen;
               IF liChar + liLength > ziPageWdth;
                 liLength = ziPageWdth - liChar;
               EndIf;
               IF liChar < 1;
                 liChar = 1;
               EndIf;
               IF liChar + liLength <= ziPageWdth;
                 waBookmark = %trim(%subst(zaInput  :
                     liChar   :
                     liLength ));
               EndIf;
             EndIf;
           EndIf;
         EndIf;

       // ***?Add escape character before special characters \, ( and )
         zaInput = AddEscape(zaInput);

       // ***?Output the line of text
         WritePDF(  %trim(NumToText(liX))
             + ' '
             + %trim(NumToText(liY))
             + ' Td ('
             + %trimr(zaInput)
             + ') Tj');

         READ cvtwork02 InputData;
         *INLR = %EOF;

       EndDo;

       EndPage();

      /END-FREE
     P PDFPages        E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?PDFTrailer                               Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create a PDF trailer                                      *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P PDFTrailer      B

      /Title  Proto-Type definition section
     D PDFTrailer      PI

      /Title  Data definition section
      ***?Procedure Work Variables
     D laDateTime      S             14A
     D liXRef          S             10I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?Create information object
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF(  '/Creator ('
           + %trim(zaPgmLib)
           + '/'
           + %trim(zaPgmName)
           + ')' );
       IF %subst(zaOpenDate:1:1) = '0';
         laDateTime = '19' + %subst(zaOpenDate:2:6)
             + zaOpenTime;
       ELSE;
         laDateTime = '20' + %subst(zaOpenDate:2:6)
             + zaOpenTime;
       EndIf;
       WritePDF(  '/CreationDate (D:'
           + laDateTime + ')');
       WritePDF('/Title (' + %trim(paTitle) + ')');
       WritePDF('/Producer (CVTSPLF)');
       WritePDF('/Keywords ()');
       WritePDF(  '/Author ('
           + %trim(zaJobNbr)
           + '/'
           + %trim(zaUser)
           + '/'
           + %trim(zaJobName)
           + ')' );
       WritePDF('>>');
       WritePDF('endobj');

       // ***?Create cross-reference
       liXref = wiChrCount - 1;
       WritePDF('xref 0 '
           + %trim(NumToText(wiObject+1)) );
       WritePDF('0000000000 65535 f');

       For idx = 1 to wiObject;
         WritePDF(aaStart(idx) + ' 00000 n');
       EndFor;

       // ***?Write trailer
       WritePDF('trailer');
       WritePDF('<<');
       WritePDF('/Size '
           + %trim(NumToText(wiObject+1)));
       WritePDF('/Root 1 0 R');
       WritePDF('/Info '
           + %trim(NumToText(wiObject))
           + ' 0 R');
       WritePDF('>>');
       WritePDF('startxref');
       WritePDF(%trim(NumToText(liXref)));
       WritePDF('%%EOF');

      /END-FREE
     P PDFTrailer      E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?NewObject                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Create a new PDF 'object'                                 *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P NewObject       B

      /Title  Proto-Type definition section
     D NewObject       PI

      /Title  Data definition section
      ***?Procedure Work Variables
     D lsDataLen       S             10S 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       wiObject = wiObject + 1;
       idx = wiObject;
       lsDataLen = wiChrCount;
       aaStart(idx) = %Char(lsDataLen);
      /END-FREE

     P NewObject       E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?WritePDF                                 Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Output PDF data                                           *****
      *****       Input : Data to Output                                           *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P WritePDF        B

      /Title  Proto-Type definition section
     D WritePDF        PI
     D   iaOutput                   378A   CONST OPTIONS(*VARSIZE)

      /Title  Data definition section
      ***?Procedure Work Variables
     D liLength        S              5I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
       // ***?Update byte count with length of data to be written
     C     ' '           CHECKR    iaOutput      liLength

     C/Free
       wiChrCount= wiChrCount + liLength + 2;

       // ***?Output data to work file
       zaOutput = %trimr(iaOutput);
       WRITE cvtwork01 OutputData;

      /END-FREE
     P WritePDF        E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?NumToText                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Convert a number to text                                  *****
      *****       Input : Number to Convert                                        *****
      *****     Returns : Alpha version of Number                                  *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P NumToText       B

      /Title  Proto-Type definition section
     D NumToText       PI            10A
     D    iiNum                      10I 0 CONST

      /Title  Data definition section
      ***?Procedure Work Variables
     D laSign          S              1A
     D laInput         S             10A
     D laOutput        S             10A
     D liIn            S              5I 0
     D liOut           S              5I 0
     D liNum           S             10I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?Set up sign if and make number positive if number is negative
       IF iiNum < 0;
         laSign = '-';
         liNum = -iiNum;
       ELSE;
         laSign = ' ';
         liNum = iiNum;
       EndIf;

       // ***?Number number to work character variable
      /END-FREE
     C                   MOVE      liNum         laInput
      /FREE
       // ***?Skip over leading zeros
       liIn  = 1;
       liOut = 1;
       DOW liIn < %size(laInput)
             and %subst(laInput:liIn:1) = '0';
         liIn = liIn + 1;
       EndDo;

       // ***?Move digits to output area
       DOW liIn  <= %size(laInput)
             and liOut <= %size(laOutput);
         %subst(laOutput:liOut:1)
             = %subst(laInput :liIn :1);
         liIn  = liIn  + 1;
         liOut = liOut + 1;
       EndDo;

       // ***?Add sign
       IF laSign = '-';
         laOutput = laSign + laOutput;
       EndIf;

       // ***?Return number in text format
       RETURN laOutput;

      /END-FREE
     P NumToText       E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?AddEscape                                Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Add an escape character before special characters         *****
      *****       Input : Character Data                                           *****
      *****     Returns : Character Data With Escape                               *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P AddEscape       B

      /Title  Proto-Type definition section
     D AddEscape       PI           378A
     D   iaInput                    378A

      /Title  Data definition section
      ***?Procedure Work Variables
     D laOutput        S            378A
     D laChar          S              1A
     D liLength        S              5I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
       // ***?Determine length of input data
     C     ' '           CHECKR    iaInput       liLength
     C/Free
       // ***?Work through input data and prefix special characters with escape
       idx = 1;
       idx1 = 0;
       DOW idx <= liLength;

         laChar = %subst(iaInput:idx:1);
         IF laChar = '\' or laChar = '(' or laChar = ')';
           idx1 += 1;
           %subst(laOutput:idx1:1) = '\';
         EndIf;
         idx1 += 1;
         %subst(laOutput:idx1:1) = laChar;
         idx += 1;
       EndDo;

       RETURN laOutput;

      /END-FREE
     P AddEscape       E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?NewPage                                  Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Procedure to create a new page object                     *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P NewPage         B

      /Title  Proto-Type definition section
     D NewPage         PI

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       // ***?Create a page object
       wiPage = wiPage + 1;
       NewObject();

       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF('/Type /Page');
       WritePDF('/Parent 5 0 R');
       WritePDF(  '/Resources << /Font <<'
           + ' /F1 4 0 R >>'
           + ' /ProcSet 3 0 R >>');
       WritePDF('/MediaBox [0 0 792 612]');
       WritePDF(  '/Contents '
           + %trim(NumToText(wiObject+1))
           + ' 0 R');
       WritePDF('>>');
       WritePDF('endobj');

       // ***?Set up bookmark if *PAGNBR option specified
       IF paBookmark = '*PAGNBR';
         waBookmark = 'Page '
             + %trim(NumToText(wiPage));
       ELSE;
         waBookmark = *Blanks;
         wiOccurs   = 0;
       EndIf;

       // ***?Create a stream object
       NewObject();

       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF(  '<< /Length '
           + %trim(NumToText(wiObject+1))
           + ' 0 R >>');
       WritePDF('stream');
       wiStart = wiChrCount;
       WritePDF('BT');

       // ***?Determine font size to use from Characters per inch setting
       SELECT;
       WHEN ziCPI = 50;
         WritePDF('/F1 20 Tf');
       WHEN ziCPI = 120;
         WritePDF('/F1 9 Tf');
       WHEN ziCPI = 150;
         WritePDF('/F1 8 Tf');
       WHEN ziCPI = 167;
         WritePDF('/F1 6 Tf');
       OTHER;
         WritePDF('/F1 10 Tf');
       ENDSL;

      /END-FREE
     P NewPage         E
      **********************************************************************************

      **********************************************************************************
      *****  Procedure:?EndPage                                  Date:?2006-01-05  *****
      **********************************************************************************
      *****     Purpose: Procedure to finish a page object.                        *****
      *****       Input : None                                                     *****
      *****     Returns : None                                                     *****
      *****                                                                        *****
      *****  Date of      Pgmr                                                     *****
      ***** Last Change Initials Description of Changes                         *****
      *****  2006-01-06   FRT       Initial Creation                               *****
      *****                                                                        *****
      **********************************************************************************
     P EndPage         B

      /Title  Proto-Type definition section
     D EndPage         PI

      /Title  Data definition section
      ***?Procedure Work Variables
     D liLength        S             10I 0

     ?/Eject
      /Title  P R O C E D U R E  M A I N  L I N E
      **********************************************************************************
      *****                   P R O C E D U R E  M A I N  L I N E                  *****
      **********************************************************************************
     C/Free
       WritePDF('ET');
       liLength = wiChrCount- wiStart;
       WritePDF('endstream');
       WritePDF('endobj');

       // ***?Create indirect length object for stream
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF(%trim(NumToText(liLength)));
       WritePDF('endobj');

       // ***?Create outline object
       waBookmark = AddEscape(waBookMark);
       NewObject();
       WritePDF(%trim(NumToText(wiObject))+' 0 obj');
       WritePDF('<<');
       WritePDF('/Parent 2 0 R');
       WritePDF(  '/Title  ('
           + %trimr(waBookmark) + ')');
       IF wiPage > 1;
         WritePDF(  '/Prev '
             + %trim(NumToText(wiObject-4))
             + ' 0 R');
       EndIf;
       IF wiPage < ziPages;
         WritePDF(  '/Next '
             + %trim(NumToText(wiObject+4))
             + ' 0 R');
       EndIf;
       WritePDF('/Dest ['
           + %trim(NumToText(wiObject-3))
           + ' 0 R /XYZ 0 792 0]');
       WritePDF('>>');
       WritePDF('endobj');

      /End-free
     P EndPage         E
      **********************************************************************************

