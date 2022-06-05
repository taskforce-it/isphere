      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Spooled Files                                                                            *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<< CRTSQLRPGI   OBJ(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                   *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  OBJTYPE(*MODULE);                                                      *
      //*         >>PARM<<  COMMIT(*NONE);                                                         *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*         >>PARM<<  CLOSQLCSR(*ENDMOD);                                                    *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'SPLF'

     d/Copy QCPYSRC,SPLF

      // Procedure prototypes for modul 'NBRRNG'

     d/Copy QCPYSRC,NBRRNG

      // Procedure prototypes for modul 'RTVMSGD'

     d/Copy QCPYSRC,RTVMSGD

      // Procedure prototype for procedure 'QUSCRTUS'

     d/Copy QCPYSRC,QUSCRTUS

      // Procedure prototype for procedure 'QUSDLTUS'

     d/Copy QCPYSRC,QUSDLTUS

      // Procedure prototype for procedure 'QUSPTRUS'

     d/Copy QCPYSRC,QUSPTRUS

      // Procedure prototype for procedure 'QUSLSPL'

     d/Copy QCPYSRC,QUSLSPL

      // Data structure for format 'ERRC0100' for error code

     d/Copy QCPYSRC,ERRC0100

      // Data structure for format 'GHDR0300' for generic header

     d/Copy QCPYSRC,GHDR0300

      // Data structure for format 'SPLF0300' for procedure 'QUSLSPL'

     d/Copy QCPYSRC,SPLF0300

      // Local procedures

     dgetCompareType...
     d                 PR             1A   ExtProc('getCompareType')
     d LPValue                      128A   Const Varying Options(*Varsize)      --> String Value

     dgetCompareValue...
     d                 PR           128A   Varying
     d                                     ExtProc('getCompareValue')
     d LPCompareType                  1A   Const
     d LPValue                      128A   Const Varying Options(*Varsize)      --> String Value

      // Global field declarations

     dGILoop           S              1N   Inz(*On)                             Loop
     dGISlcJob         S              1A                                        Selec. by job
     dGIJobName        S             10A                                        Job name
     dGIJobUsr         S             10A                                        User name
     dGIJobNbr         S              6A                                        Job number
     dGISlcUsr         S              1A                                        Selec. by user
     dGIUsr            S             10A                                        User
     dGISlcFormType    S              1A                                        Selec. by form type
     dGIFormType       S             10A                                        Form type
     dGISlcOutQ        S              1A                                        Selec. by out. queue
     dGIOutQ           S             10A                                        Output queue
     dGIOutQLib        S             10A                                        Output queue library
     dGISlcUsrDta      S              1A                                        Selec. by user data
     dGIUsrDta         S             10A                                        User data
     dGISlcDateTime    S              1A                                        Selec. by date/time
     dGIStrDate        S             10D   DatFmt(*ISO)                         Start date
     dGIStrTime        S              8T   TimFmt(*ISO)                         Start time
     dGIEndDate        S             10D   DatFmt(*ISO)                         End date
     dGIEndTime        S              8T   TimFmt(*ISO)                         End time
     dGISlcName        S              1A                                        Selec. by SplF name
     dGIName           S             10A   Varying                              SplF Name
     dGICmpTypeName    S              1A                                        Comp. type SplF name

     dGIMaxNumSplF     S             10I 0 Inz(-1)
     dGIErrorMsg       S            256A   Varying

     d EXACT_MATCH     c                   '-'
     d TRAILING_MATCH  c                   '1'
     d LEADING_MATCH   c                   '2'
     d GENERIC_MATCH   c                   '3'

      //*==========================================================================================*
      //* Prepare                                                                                  *
      //*==========================================================================================*

     pSPLF_prepare...
     p                 B                   Export
     dSPLF_prepare...
     d                 PI

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by job
       GISlcJob = *Blank;
       GIJobName = *Blanks;
       GIJobUsr = *Blanks;
       GIJobNbr = *Blanks;

       // Selection by user
       GISlcUsr = *Blank;
       GIUsr = *Blanks;

       // Selection by form type
       GISlcFormType = *Blank;
       GIFormType = *Blanks;

       // Selection by output queue
       GISlcOutQ = *Blank;
       GIOutQ = *Blanks;
       GIOutQLib = *Blanks;

       // Selection by user data
       GISlcUsrDta = *Blank;
       GIUsrDta = *Blanks;

       // Selection by date/time
       GISlcDateTime = *Blank;
       GIStrDate = *LoVal;
       GIStrTime = *LoVal;
       GIEndDate = *LoVal;
       GIEndTime = *LoVal;

       // Selection by spooled file name
       GISlcName = *Blank;
       GIName = *Blanks;
       GICmpTypeName = *Blank;

       // Return all spooled files
       GIMaxNumSplF = -1;

       // API Error Message
       GIErrorMsg = '';

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_prepare...
     p                 E

      //*==========================================================================================*
      //* Set maximum number of spooled files to return.
      //*==========================================================================================*

     pSPLF_setMaxNumSplF...
     p                 B                   Export
     dSPLF_setMaxNumSplF...
     d                 PI
     d LPMaxNumSplF                  10I 0 Const                                --> Max Nbr of SplFs

      //*------------------------------------------------------------------------------------------*

      /Free

       GIMaxNumSplF = LPMaxNumSplF;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setMaxNumSplF...
     p                 E

      //*==========================================================================================*
      //* Set Job                                                                                  *
      //*==========================================================================================*

     pSPLF_setJob...
     p                 B                   Export
     dSPLF_setJob...
     d                 PI
     d LPJobName                     10A   Const                                --> Job name
     d LPJobUsr                      10A   Const                                --> User name
     d LPJobNbr                       6A   Const                                --> Job number

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by job
       GISlcJob = 'X';
       GIJobName = LPJobName;
       GIJobUsr = LPJobUsr;
       GIJobNbr = LPJobNbr;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setJob...
     p                 E

      //*==========================================================================================*
      //* Set User                                                                                 *
      //*==========================================================================================*

     pSPLF_setUser...
     p                 B                   Export
     dSPLF_setUser...
     d                 PI
     d LPUsr                         10A   Const                                --> User

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by user
       GISlcUsr = 'X';
       GIUsr = LPUsr;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setUser...
     p                 E

      //*==========================================================================================*
      //* Set Form Type                                                                            *
      //*==========================================================================================*

     pSPLF_setFormType...
     p                 B                   Export
     dSPLF_setFormType...
     d                 PI
     d LPFormType                    10A   Const                                --> Form type

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by form type
       GISlcFormType = 'X';
       GIFormType = LPFormType;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setFormType...
     p                 E

      //*==========================================================================================*
      //* Set Output Queue                                                                         *
      //*==========================================================================================*

     pSPLF_setOutputQueue...
     p                 B                   Export
     dSPLF_setOutputQueue...
     d                 PI
     d LPOutQ                        10A   Const                                --> Output queue
     d LPOutQLib                     10A   Const                                --> Output queue lib

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by output queue
       GISlcOutQ = 'X';
       GIOutQ = LPOutQ;
       GIOutQLib = LPOutQLib;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setOutputQueue...
     p                 E

      //*==========================================================================================*
      //* Set User Data                                                                            *
      //*==========================================================================================*

     pSPLF_setUserData...
     p                 B                   Export
     dSPLF_setUserData...
     d                 PI
     d LPUsrDta                      10A   Const                                --> User data

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by user data
       GISlcUsrDta = 'X';
       GIUsrDta = LPUsrDta;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setUserData...
     p                 E

      //*==========================================================================================*
      //* Set Date Time                                                                            *
      //*==========================================================================================*

     pSPLF_setDateTime...
     p                 B                   Export
     dSPLF_setDateTime...
     d                 PI
     d LPStrDate                     10D   DatFmt(*ISO) Const                   --> Start date
     d LPStrTime                      8T   TimFmt(*ISO) Const                   --> Start time
     d LPEndDate                     10D   DatFmt(*ISO) Const                   --> End date
     d LPEndTime                      8T   TimFmt(*ISO) Const                   --> End time

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by date/time
       GISlcDateTime = 'X';
       GIStrDate = LPStrDate;
       GIStrTime = LPStrTime;
       GIEndDate = LPEndDate;
       GIEndTime = LPEndTime;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setDateTime...
     p                 E

      //*==========================================================================================*
      //* Set Name   (*generic*)                                                                   *
      //*==========================================================================================*

     pSPLF_setName...
     p                 B                   Export
     dSPLF_setName...
     d                 PI
     d LPName                        10A   Const                                --> SplF Name

      //*------------------------------------------------------------------------------------------*

      /Free

       // Selection by name
       GISlcName = 'X';
       GIName = %Trim(LPName);

       GICmpTypeName = getCompareType(GIName);
       GIName = getCompareValue(GICmpTypeName: GIName);

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_setName...
     p                 E

      //*==========================================================================================*
      //* Build                                                                                    *
      //*==========================================================================================*

     pSPLF_build...
     p                 B                   Export
     dSPLF_build...
     d                 PI            10S 0                                      <-- Handle

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLIHDL            S             10S 0                                      Handle
     dLICNT            S             10I 0                                      Counter
     dLIUsrSpc         S               *                                        User space
     dLIASPNbr         S             10I 0                                      ASP Number
     dLIFld            S             10I 0 Dim(50)                              Fields
     dLIJob            S             26A                                        Job
     dLIUsr            S             10A                                        User
     dLIFormType       S             10A                                        Form type
     dLIOutQ           S             20A                                        Output queue
     dLIUsrDta         S             10A                                        User data
     dLIStrDate        S              7A                                        Start date
     dLIStrTime        S              6A                                        Start time
     dLIEndDate        S              7A                                        End date
     dLIEndTime        S              6A                                        End time
     dLICrtDate        S             10D   DatFmt(*ISO)                         Creation date
     dLICrtTime        S              8T   TimFmt(*ISO)                         Creation time
     dLISts            S             10A                                        Status

     dLIOffs           S             10I 0

      //*------------------------------------------------------------------------------------------*

      /Free

       // Initialize handle
       LIHDL = -1;

       // Create user space
       Reset ERRC0100;
       QUSCRTUS(
        'SPLF      ' +
        'QTEMP     ' : // --> Qual. user space
        *Blanks :      // --> Extended attr.
        16384 :        // --> Initial size
        '0' :          // --> Initial value
        '*ALL' :       // --> Public authority
        *Blanks :      // --> Text description
        '*YES' :       // --> Replace
        ERRC0100       // <-> Error code
       );

       If ERRCExcId = *Blanks;

         // Retrieve pointer to user space
         Reset ERRC0100;
         QUSPTRUS(
          'SPLF      ' +
          'QTEMP     ' : // --> Qual. user space
          LIUsrSpc :     // <-- Return pointer
          ERRC0100       // <-> Error code
         );

         If ERRCExcId = *Blanks;

           // Fields
           LIFld(*) = *Zero;

           // ASP Number
           If GISlcJob = 'X';
             LIASPNbr = -1;
           Else;
             LIASPNbr = 0;
           EndIf;

           // Job
           If GISlcJob = 'X';
             LIJob = GIJobName + GIJobUsr + GIJobNbr;
           Else;
             LIJob = *Blanks;
           EndIf;

           // User
           Select;
           When GISlcJob = 'X';
             LIUsr = *Blanks;
           When GISlcUsr = 'X';
             LIUsr = GIUsr;
           Other;
             LIUsr = '*ALL' ;
           EndSl;

           // Form type
           Select;
           When GISlcJob = 'X';
             LIFormType = *Blanks;
           When GISlcFormType = 'X';
             LIFormType = GIFormType;
           Other;
             LIFormType = '*ALL' ;
           EndSl;

           // Output queue
           Select;
           When GISlcJob = 'X';
             LIOutQ = *Blanks;
           When GISlcOutQ = 'X';
             LIOutQ = GIOutQ + GIOutQLib;
           Other;
             LIOutQ = '*ALL' ;
           EndSl;

           // User data
           Select;
           When GISlcJob = 'X';
             LIUsrDta = *Blanks;
           When GISlcUsrDta = 'X';
             LIUsrDta = GIUsrDta;
           Other;
             LIUsrDta = '*ALL' ;
           EndSl;

           // Date/Time
           If GISlcDateTime = 'X' And
              (GIStrDate = *LoVal Or
                 (GIStrDate >= D'1900-01-01' And
                 GIStrDate <= D'2099-12-31')) And
              (GIEndDate = *HiVal Or
                 (GIEndDate >= D'1900-01-01' And
                 GIEndDate <= D'2099-12-31'));
             If GIStrDate = *LoVal;
               LIStrDate = '*FIRST';
               LIStrTime = *Blanks;
             Else;
               If GIStrDate <= D'1999-12-31';
                 %SubSt(LIStrDate : 1 : 1) = '0';
               Else;
                 %SubSt(LIStrDate : 1 : 1) = '1';
               EndIf;
               %SubSt(LIStrDate : 2 : 6) = %SubSt(%Char(GIStrDate : *ISO0):3:6);
               LIStrTime = %Char(GIStrTime : *ISO0);
             EndIf;
             If GIEndDate = *HiVal;
               LIEndDate = '*LAST';
               LIEndTime = *Blanks;
             Else;
               If GIEndDate <= D'1999-12-31';
                 %SubSt(LIEndDate : 1 : 1) = '0';
               Else;
                 %SubSt(LIEndDate : 1 : 1) = '1';
               EndIf;
               %SubSt(LIEndDate : 2 : 6) = %SubSt(%Char(GIEndDate : *ISO0):3:6);
               LIEndTime = %Char(GIEndTime : *ISO0);
             EndIf;
           Else;
             LIStrDate = '*ALL' ;
             LIStrTime = *Blanks;
             LIEndDate = *Blanks;
             LIEndTime = *Blanks;
           EndIf;

           // List Spooled Files
           Reset ERRC0100;
           QUSLSPL(
            'SPLF      ' +
            'QTEMP     ' : // --> User space
            'SPLF0300' :   // --> Format name
            LIUsr :        // --> User name
            LIOutQ :       // --> output queue
            LIFormType :   // --> Form type
            LIUsrDta :     // --> User data
            ERRC0100 :     // <-> Error code
            LIJob :        // --> Job
            LIFld :        // --> Fields
            *Zero :        // --> Number of fields
            LIASPNbr :     // --> ASP Number
            '*ALL' :       // --> Job system name
            LIStrDate :    // --> Start date
            LIStrTime :    // --> Start time
            LIEndDate :    // --> End date
            LIEndTime :    // --> End time
            '*'            // --> ASP Device
           );

           If ERRCExcId = *Blanks;

             // Set handle
             LIHDL =
             NBRRNG_getNumber(
              'HANDLE' // --> Number range
             );

             // Set pointer of generic header
             GHDR0300Ptr = LIUsrSpc;

             For LICNT = 1 To GHDRNbrEtry;

               // Set pointer of list area
               SPLF0300Ptr =
               LIUsrSpc +                    // Addr. user space
               GHDROffLstSct +               // Offset to list area
               ((LICNT - 1) * GHDRSizeEtry); // Offset to record

               // Spooled file name
               If GISlcName = 'X' And GIName <> '*';
                 Select;
                 When GICmpTypeName = EXACT_MATCH;
                   If GIName <> SPLFSplF;
                     Iter;
                   EndIf;
                 When GICmpTypeName = LEADING_MATCH;
                   If GIName <> %SubSt(SPLFSplF: 1: %Len(GIName));
                     Iter;
                   EndIf;
                 When GICmpTypeName = TRAILING_MATCH;
                   LIOffs = %Len(%TrimR(SPLFSplF)) - %Len(GIName);
                   If LIOffs < 0 Or GIName <> %SubSt(SPLFSplF: 1 + LIOffs);
                     Iter;
                   EndIf;
                 When GICmpTypeName = GENERIC_MATCH;
                   If %Scan(GIName: SPLFSplF) = 0;
                     Iter;
                   EndIf;
                 EndSl;
               EndIf;

               // Convert creation date
               LICrtDate = %Date(SPLFCrtDate : *CYMD0);

               // Convert creation time
               LICrtTime = %Time(SPLFCrtTime : *HMS0);

               // Status
               Select;
               When SPLFSts = 1;
                 LISts = '*READY';
               When SPLFSts = 2;
                 LISts = '*OPEN';
               When SPLFSts = 3;
                 LISts = '*CLOSED';
               When SPLFSts = 4;
                 LISts = '*SAVED';
               When SPLFSts = 5;
                 LISts = '*WRITING';
               When SPLFSts = 6;
                 LISts = '*HELD';
               When SPLFSts = 7;
                 LISts = '*MESSAGE';
               When SPLFSts = 8;
                 LISts = '*PENDING';
               When SPLFSts = 9;
                 LISts = '*PRINTER';
               When SPLFSts = 10;
                 LISts = '*FINISHED';
               When SPLFSts = 11;
                 LISts = '*SENDING';
               When SPLFSts = 12;
                 LISts = '*DEFERRED';
               Other;
                 LISts = '*UNKNOWN';
               EndSl;

               // Write file 'Spooled Files'
               Exec SQL
                  INSERT
                     INTO
                  SPLF
                     (
                      SFHDL,
                      SFCNT,
                      SFSPLF,
                      SFSPLFNBR,
                      SFJOBNAME,
                      SFJOBUSR,
                      SFJOBNBR,
                      SFJOBSYS,
                      SFCRTDATE,
                      SFCRTTIME,
                      SFCRTDATEX,
                      SFCRTTIMEX,
                      SFOUTQ,
                      SFOUTQLIB,
                      SFUSRDTA,
                      SFSTS,
                      SFPAGES,
                      SFOUTPTY,
                      SFCOPIES,
                      SFFORMTYPE
                     )
                  VALUES
                     (
                      :LIHDL,
                      :LICNT,
                      :SPLFSplF,
                      :SPLFSplFNbr,
                      :SPLFJobName,
                      :SPLFJobUsr,
                      :SPLFJobNbr,
                      :SPLFSys,
                      :LICrtDate,
                      :LICrtTime,
                      :SPLFCrtDate,
                      :SPLFCrtTime,
                      :SPLFOutQ,
                      :SPLFOutQLib,
                      :SPLFUsrDta,
                      :LISts,
                      :SPLFPages,
                      :SPLFOutPty,
                      :SPLFCopies,
                      :SPLFFormType
                     );

               //  Exit on maximum number of spooled files reached
               If (GIMaxNumSplF > 0 And LICNT > GIMaxNumSplF);
                 Leave;
               EndIf;

             EndFor;

           Else;
             GIErrorMsg = rtvMsgText(ERRCExcId: 'QCPFMSG': '*LIBL': ERRCExcDta);
           EndIf;

         EndIf;

         // Delete user space
         Reset ERRC0100;
         QUSDLTUS(
          'SPLF      ' +
          'QTEMP     ' : // --> Qual. user space
          ERRC0100       // <-> Error code
         );

       EndIf;

       // Leave procedure
       Return LIHDL;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_build...
     p                 E

      //*==========================================================================================*
      //* Set Name   (*generic*)                                                                   *
      //*==========================================================================================*

     pSPLF_getErrorMessage...
     p                 B                   Export
     dSPLF_getErrorMessage...
     d                 PI           256A

      //*------------------------------------------------------------------------------------------*

      /Free

       // Leave procedure
       Return GIErrorMsg;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_getErrorMessage...
     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     pSPLF_clear...
     p                 B                   Export
     dSPLF_clear...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      /Free

       // Clear file 'Spooled Files'
       Exec SQL
          DELETE FROM
             SPLF
          WHERE
             SFHDL = :LPHDL;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pSPLF_clear...
     p                 E

      //*==========================================================================================*
      //* Returns the compare type of a given string value.                                        *
      //*==========================================================================================*

     pgetCompareType...
     p                 B                   Export
     d                 PI             1A
     d LPValue                      128A   Const Varying Options(*Varsize)      --> String Value

     dLICompareType    S              1A
      //*------------------------------------------------------------------------------------------*

      /Free

       Select;
       When %Len(LPValue) >= 3 And
            %SubSt(LPValue: 1:1) = '*' And
            %SubSt(LPValue: %Len(LPValue):1) = '*';
         LICompareType = GENERIC_MATCH;
       When %Len(LPValue) >= 2 And
            %SubSt(LPValue: %Len(LPValue): 1) = '*';
         LICompareType = LEADING_MATCH;
       When %Len(LPValue) >= 2 And
            %SubSt(LPValue: 1: 1) = '*';
         LICompareType = TRAILING_MATCH;
       Other;
         LICompareType = EXACT_MATCH;
       EndSl;

       Return LICompareType;

      /End-Free

       //*------------------------------------------------------------------------------------------

     p                 E

      //*==========================================================================================*
      //* Returns the compare value of a given string value.                                       *
      //*==========================================================================================*

     pgetCompareValue...
     p                 B                   Export
     d                 PI           128A   Varying
     d LPCompareType                  1A   Const
     d LPValue                      128A   Const Varying Options(*Varsize)      --> String Value

     dLIValue          S            128A   Varying
      //*------------------------------------------------------------------------------------------*

      /Free

       Select;
       When LPCompareType = GENERIC_MATCH;
         LIValue = %SubSt(LPValue: 2: %Len(LPValue) - 2);
       When LPCompareType = LEADING_MATCH;
         LIValue = %SubSt(LPValue: 1: %Len(LPValue) - 1);
       When LPCompareType = TRAILING_MATCH;
         LIValue = %SubSt(LPValue: 2);
       Other;
         LIValue = LPValue;
       EndSl;

       Return LIValue;

      /End-Free

       //*------------------------------------------------------------------------------------------

     p                 E

