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
      //* Find String in Source File                                                               *
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
      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

     fQSRCF     IF   F  240        Disk    ExtFile(GIFILE)
     f                                     ExtMbr(GIMBR)
     f                                     UsrOpn

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'FNDSTR'

     d/Copy QCPYSRC,FNDSTR

      // Procedure prototypes for modul 'NBRRNG'

     d/Copy QCPYSRC,NBRRNG

      // Procedure prototype for procedure 'QUSCRTUS'

     d/Copy QCPYSRC,QUSCRTUS

      // Procedure prototype for procedure 'QUSDLTUS'

     d/Copy QCPYSRC,QUSDLTUS

      // Procedure prototype for procedure 'QUSPTRUS'

     d/Copy QCPYSRC,QUSPTRUS

      // Procedure prototype for procedure 'QUSLMBR'

     d/Copy QCPYSRC,QUSLMBR

      // Data structure for format 'ERRC0100' for error code

     d/Copy QCPYSRC,ERRC0100

      // Data structure for format 'GHDR0300' for generic header

     d/Copy QCPYSRC,GHDR0300

      // Data structure for format 'MBRL0330' for procedure 'QUSLMBR'

     d/Copy QCPYSRC,MBRL0330

      // Data structure for format 'MBRD0300' for procedure 'QUSRMBRD'

     d/Copy QCPYSRC,MBRD0300

      // Record format for file FNDSTRI

     dFNDSTRIRF      E DS                  ExtName(FNDSTRI)

      // Record format for file QSRCF

     dQSRCFRF          DS           240
     d SRCSEQ                         6A
     d SRCDAT                         6A
     d SRCDTA                       228A

      // Global field declarations

     dGILoop           S              1N   Inz(*On)                             Loop
     dGIFILE           S             21A                                        File
     dGIMBR            S             10A                                        Member
     dGIUP             C                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'         Upper case
     dGILO             C                   'abcdefghijklmnopqrstuvwxyz'         Lower case

      //*==========================================================================================*
      //* Get Handle                                                                               *
      //*==========================================================================================*

     pFNDSTR_getHandle...
     p                 B                   Export
     dFNDSTR_getHandle...
     d                 PI            10S 0                                      <-- Handle

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLIHDL            S             10S 0                                      Handle

      //*------------------------------------------------------------------------------------------*

      /Free

       // Set handle
       LIHDL =
       NBRRNG_getNumber(
        'HANDLE' // --> Number range
       );

       // Write file 'Status'
       Exec SQL
          INSERT
             INTO
          FNDSTRS
             (
              XSHDL,
              XSCNT,
              XSCNL
             )
          VALUES
             (
              :LIHDL,
              0,
              '*NO'
             );

       // Leave procedure
       Return LIHDL;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pFNDSTR_getHandle...
     p                 E

      //*==========================================================================================*
      //* Resolve Generic Search Elements                                                          *
      //*==========================================================================================*

     pFNDSTR_resolveGenericSearchElements...
     p                 B                   Export
     dFNDSTR_resolveGenericSearchElements...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLIUsrSpc         S               *                                        User space
     dLICnt            S             10I 0                                      Counter

      //*------------------------------------------------------------------------------------------*

      /Free

       // Declare cursor
       Exec SQL
          DECLARE
             GENERIC
          CURSOR FOR
             SELECT
                *
             FROM
                FNDSTRI
             WHERE
                XIHDL = :LPHDL AND
                XIMBR = '*'
             FOR UPDATE;

       // Open cursor
       Exec SQL
          OPEN
             GENERIC;

       DoW GILoop;

         // Fetch from cursor
         Exec SQL
            FETCH NEXT FROM
               GENERIC
            INTO
               :FNDSTRIRF;

         // Leave condition
         If SQLCOD = 100 Or
               SQLCOD < 0;
           Leave;
         EndIf;

         // Delete generic input element
         Exec SQL
            DELETE FROM
               FNDSTRI
            WHERE
               CURRENT OF
                  GENERIC;

         // Create user space
         Reset ERRC0100;
         QUSCRTUS(
          'FNDSTR    ' +
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
            'FNDSTR    ' +
            'QTEMP     ' : // --> Qual. user space
            LIUsrSpc :     // <-- Return pointer
            ERRC0100       // <-> Error code
           );

           If ERRCExcId = *Blanks;

             // List members
             Reset ERRC0100;
             QUSLMBR(
              'FNDSTR    ' +
              'QTEMP     ' :   // --> Qual. user space
              'MBRL0330' :     // --> Format name
              XIFILE + XILIB : // --> Qual. file
              '*ALL' :         // --> Member name
              '0' :            // --> Override process
              ERRC0100         // <-> Error code
             );

             If ERRCExcId = *Blanks;

               // Set pointer of generic header
               GHDR0300Ptr = LIUsrSpc;

               For LICnt = 1 To GHDRNbrEtry;

                 // Set pointer of list area
                 MBRL0330Ptr =
                 LIUsrSpc +                    // Addr. user space
                 GHDROffLstSct +               // Offset to list area
                 ((LICnt - 1) * GHDRSizeEtry); // Offset to record

                 // Set pointer of information area
                 MBRD0300APtr = LIUsrSpc + MBRLOffMbrInf;

                 // Write file 'Input'
                 Exec SQL
                    INSERT
                       INTO
                    FNDSTRI
                       (
                        XIHDL,
                        XILIB,
                        XIFILE,
                        XIMBR
                       )
                    VALUES
                       (
                        :LPHDL,
                        :XILIB,
                        :XIFILE,
                        :MBRDMbr
                       );

               EndFor;

             EndIf;

           EndIf;

           // Delete user space
           Reset ERRC0100;
           QUSDLTUS(
            'FNDSTR    ' +
            'QTEMP     ' : // --> Qual. user space
            ERRC0100       // <-> Error code
           );

         EndIf;

       EndDo;

       // Close cursor
       Exec SQL
          CLOSE
             GENERIC;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pFNDSTR_resolveGenericSearchElements...
     p                 E

      //*==========================================================================================*
      //* Get Number Of Search Elements                                                            *
      //*==========================================================================================*

     pFNDSTR_getNumberOfSearchElements...
     p                 B                   Export
     dFNDSTR_getNumberOfSearchElements...
     d                 PI             6S 0                                      <-- Number of elem.
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      // Local field declarations

     dLIELEM           S              6S 0                                      Number of Elements

      //*------------------------------------------------------------------------------------------*

      /Free

       // Initialize 'Number of elements'
       LIELEM = *Zero;

       // Get number of elements
       Exec SQL
          SELECT
             COUNT(*)
          INTO
             :LIELEM
          FROM
             FNDSTRI
          WHERE
             XIHDL = :LPHDL;

       // Leave procedure
       Return LIELEM;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pFNDSTR_getNumberOfSearchElements...
     p                 E

      //*==========================================================================================*
      //* Search                                                                                   *
      //*==========================================================================================*

     pFNDSTR_search...
     p                 B                   Export
     dFNDSTR_search...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LPSTG                         40A   Const                                --> String
     d LPFCOL                         3S 0 Const                                --> From column
     d LPTCOL                         3S 0 Const                                --> To column
     d LPCASE                        10A   Const                                --> Case

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLILIB            S             10A                                        Library
     dLIFILE           S             10A                                        File
     dLIQFILE          S             20A                                        Qualified file
     dLITYP1           S              1A                                        Type 1
     dLITYP2           S              1A                                        Type 2
     dLIRLNG           S              5S 0                                      Record length
     dLISTR            S              3S 0                                      Start
     dLILNG            S              3S 0                                      Length
     dLISTMT           S              6S 0                                      Statement
     dLILINE           S            228A                                        Line
     dLISTG            S             40A                                        String
     dLICNT            S              6S 0                                      Counter
     dLICNL            S             10A                                        Cancel

      //*------------------------------------------------------------------------------------------*

      /Free

       // Initialize counter
       LICNT = *Zero;

       // Initialize control break
       LILIB = *LoVal;
       LIFILE = *LoVal;

       // Declare cursor
       Exec SQL
          DECLARE
             FNDSTRI
          CURSOR FOR
             SELECT
                *
             FROM
                FNDSTRI
             WHERE
                XIHDL = :LPHDL
             ORDER BY
                XIHDL,
                XILIB,
                XIFILE,
                XIMBR
             FOR READ ONLY;

       // Open cursor
       Exec SQL
          OPEN
             FNDSTRI;

       DoW GILoop;

         // Fetch from cursor
         Exec SQL
            FETCH NEXT FROM
               FNDSTRI
            INTO
               :FNDSTRIRF;

         // Leave condition
         If SQLCOD = 100 Or
               SQLCOD < 0;
           Leave;
         EndIf;

         // Increase counter
         LICNT = LICNT + 1;

         If %Rem(LICNT : 100) = *Zero;

           // Update file 'Status'
           Exec SQL
              UPDATE
                 FNDSTRS
              SET
                 XSCNT = :LICNT
              WHERE
                 XSHDL = :LPHDL;

           // Initialize 'Cancel'
           LICNL = *Blanks;

           // Check if search has to be canceled
           Exec SQL
              SELECT
                 XSCNL
              INTO
                 :LICNL
              FROM
                 FNDSTRS
              WHERE
                 XSHDL = :LPHDL;

           // Search has to be canceled
           If LICNL = '*YES';
             Leave;
           EndIf;

         EndIf;

         // Control break
         If XILIB <> LILIB Or
               XIFILE <> LIFILE;

           // Set control break
           LILIB = XILIB;
           LIFILE = XIFILE;

           // Initialize attributes
           LITYP1 = *Blank;
           LITYP2 = *Blank;
           LIRLNG = *Zero;

           // Get attributes
           Exec SQL
              SELECT
                 TABLE_TYPE,
                 FILE_TYPE,
                 ROW_LENGTH
              INTO
                 :LITYP1,
                 :LITYP2,
                 :LIRLNG
              FROM
                 SYSTABLES
              WHERE
                 TABLE_SCHEMA = :XILIB AND
                 TABLE_NAME = :XIFILE;

           // Not the right attributes
           If Not (LITYP1 = 'P' And
                 LITYP2 = 'S' And
                 LIRLNG <> *Zero);
             Iter;
           EndIf;

           // Get record length
           LIRLNG = LIRLNG - %Size(SRCSEQ) - %Size(SRCDAT);
           If LIRLNG > %Size(SRCDTA);
             LIRLNG = %Size(SRCDTA);
           EndIf;

           // Get start and length
           If LPFCOL <= LIRLNG;
             LISTR = LPFCOL;
           Else;
             LISTR = LIRLNG;
           EndIf;
           If LPTCOL > LIRLNG;
             LILNG = LIRLNG - LPFCOL + 1;
           Else;
             LILNG = LPTCOL - LPFCOL + 1;
           EndIf;

         EndIf;

         // Not the right attributes
         If Not (LITYP1 = 'P' And
               LITYP2 = 'S' And
               LIRLNG <> *Zero);
           Iter;
         EndIf;

         // Open member
         GIFILE = %Trim(XILIB) + '/' + %Trim(XIFILE);
         GIMBR = XIMBR;
         Open(E) QSRCF;

         // Error during open of member occured
         If %Error;
           Iter;
         EndIf;

         // Initialize statement
         LISTMT = *Zero;

         DoW GILoop;

           // Read member
           Read QSRCF QSRCFRF;

           // Leave condition
           If %EOF;
             Leave;
           EndIf;

           // Increase statement
           LISTMT = LISTMT + 1;

           // Initialize string
           LISTG = LPSTG;

           // Initialize line
           LILINE = %SubSt(SRCDTA : LISTR : LILNG);

           // Ignore upper/lower case
           If LPCASE = '*IGNORE';
             LISTG = %XLate(GILO:GIUP:LISTG);
             LILINE = %XLate(GILO:GIUP:LILINE);
           EndIf;

           // No match
           If %Scan(%TrimR(LISTG) : LILINE) = *Zero;
             Iter;
           EndIf;

           // Write file 'Output'
           Exec SQL
              INSERT
                 INTO
              FNDSTRO
                 (
                  XOHDL,
                  XOLIB,
                  XOFILE,
                  XOMBR,
                  XOSTMT,
                  XOLINE
                 )
              VALUES
                 (
                  :LPHDL,
                  :XILIB,
                  :XIFILE,
                  :XIMBR,
                  :LISTMT,
                  :SRCDTA
                 );

         EndDo;

         // Close member
         Close QSRCF;

       EndDo;

       // Update file 'Status'
       Exec SQL
          UPDATE
             FNDSTRS
          SET
             XSCNT = -1
          WHERE
             XSHDL = :LPHDL;

       // Close cursor
       Exec SQL
          CLOSE
             FNDSTRI;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pFNDSTR_search...
     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     pFNDSTR_clear...
     p                 B                   Export
     dFNDSTR_clear...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      /Free

       // Clear file 'Status'
       Exec SQL
          DELETE FROM
             FNDSTRS
          WHERE
             XSHDL = :LPHDL;

       // Clear file 'Input'
       Exec SQL
          DELETE FROM
             FNDSTRI
          WHERE
             XIHDL = :LPHDL;

       // Clear file 'Output'
       Exec SQL
          DELETE FROM
             FNDSTRO
          WHERE
             XOHDL = :LPHDL;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pFNDSTR_clear...
     p                 E
