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
      //* Find String in Message File                                                              *
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

      // Procedure prototypes for modul 'XFNDSTR'

     d/Copy QCPYSRC,XFNDSTR

      // Procedure prototypes for modul 'NBRRNG'

     d/Copy QCPYSRC,NBRRNG

      // Procedure prototype for procedure 'QMHRTVM'

     d/Copy QCPYSRC,QMHRTVM

      // Data structure for format 'RTVM0300' for procedure 'QMHRTVM'

     d/Copy QCPYSRC,RTVM0300

      // Data structure for format 'ERRC0100' for error code

     d/Copy QCPYSRC,ERRC0100

      // Record format for file XFNDSTRI

     dXFNDSTRIRF     E DS                  ExtName(XFNDSTRI)

      // Global field declarations

     dGILoop           S              1N   Inz(*On)                             Loop
     dGIUP             C                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'         Upper case
     dGILO             C                   'abcdefghijklmnopqrstuvwxyz'         Lower case

      //*==========================================================================================*
      //* Get Handle                                                                               *
      //*==========================================================================================*

     pXFNDSTR_getHandle...
     p                 B                   Export
     dXFNDSTR_getHandle...
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
          XFNDSTRS
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

     pXFNDSTR_getHandle...
     p                 E

      //*==========================================================================================*
      //* Get Number Of Search Elements                                                            *
      //*==========================================================================================*

     pXFNDSTR_getNumberOfSearchElements...
     p                 B                   Export
     dXFNDSTR_getNumberOfSearchElements...
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
             XFNDSTRI
          WHERE
             XIHDL = :LPHDL;

       // Leave procedure
       Return LIELEM;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pXFNDSTR_getNumberOfSearchElements...
     p                 E

      //*==========================================================================================*
      //* Search                                                                                   *
      //*==========================================================================================*

     pXFNDSTR_search...
     p                 B                   Export
     dXFNDSTR_search...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LPSTG                         40A   Const                                --> String
     d LPFCOL                         3S 0 Const                                --> From column
     d LPTCOL                         3S 0 Const                                --> To column
     d LPCASE                        10A   Const                                --> Case

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLISTG            S             40A                                        String
     dLITXT            S            132A                                        Text
     dLICNT            S              6S 0                                      Counter
     dLICNL            S             10A                                        Cancel
     dLIRTVOPT         S             10A                                        Abrufoption
     dLIMSGID          S              7A                                        Nachrichten-Id.
     dLIMSG            S            132A                                        Nachricht

      //*------------------------------------------------------------------------------------------*

      /Free

       // Initialize counter
       LICNT = *Zero;

       // Declare cursor
       Exec SQL
          DECLARE
             XFNDSTRI
          CURSOR FOR
             SELECT
                *
             FROM
                XFNDSTRI
             WHERE
                XIHDL = :LPHDL
             ORDER BY
                XIHDL,
                XILIB,
                XIMSGF
             FOR READ ONLY;

       // Open cursor
       Exec SQL
          OPEN
             XFNDSTRI;

       DoW GILoop;

         // Fetch from cursor
         Exec SQL
            FETCH NEXT FROM
               XFNDSTRI
            INTO
               :XFNDSTRIRF;

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
                 XFNDSTRS
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
                 XFNDSTRS
              WHERE
                 XSHDL = :LPHDL;

           // Search has to be canceled
           If LICNL = '*YES';
             Leave;
           EndIf;

         EndIf;

         // Initialize message id.
         LIMSGID = *Blanks;

         // Initialize retrieve option
         LIRTVOPT = '*FIRST';

         DoW GILoop;

           // Retrieve message
           Reset ERRC0100;
           QMHRTVM(
            RTVM0300 :        // <-- Message informat
            %Size(RTVM0300) : // --> Length of messag
            'RTVM0300' :      // --> Format name
            LIMSGID :         // --> Message identifi
            XIMSGF +
            XILIB :           // --> Qualified messag
            *Blanks :         // --> Replacement data
            *Zero :           // --> Length of replac
            '*NO' :           // --> Replace substitu
            '*YES' :          // --> Return format co
            ERRC0100 :        // <-> Error code
            LIRTVOPT :        // --> Retrieve option
            0 :               // --> CCSID to convert
            0                 // --> CCSID of replace
           );

           // Error occured
           If ERRCExcId <> *Blanks;
             Leave;
           EndIf;

           // End of message file
           If RTVMMsgId = *Blanks;
             Leave;
           EndIf;

           // Extract message id. and message
           LIMSGID = RTVMMsgId;
           LIMSG = %SubSt(RTVM0300 : RTVMOffMsg + 1 : RTVMLenMsgRtn);

           // Set retrieve option
           LIRTVOPT = '*NEXT';

           // Initialize string
           LISTG = LPSTG;

           // Initialize text
           LITXT = %SubSt(LIMSG : LPFCOL : LPTCOL - LPFCOL + 1);

           // Ignore upper/lower case
           If LPCASE = '*IGNORE';
             LISTG = %XLate(GILO:GIUP:LISTG);
             LITXT = %XLate(GILO:GIUP:LITXT);
           EndIf;

           // No match
           If %Scan(%TrimR(LISTG) : LITXT) = *Zero;
             Iter;
           EndIf;

           // Write file 'Output'
           Exec SQL
              INSERT
                 INTO
              XFNDSTRO
                 (
                  XOHDL,
                  XOLIB,
                  XOMSGF,
                  XOMSGID,
                  XOMSG
                 )
              VALUES
                 (
                  :LPHDL,
                  :XILIB,
                  :XIMSGF,
                  :LIMSGID,
                  :LIMSG
                 );

         EndDo;

       EndDo;

       // Update file 'Status'
       Exec SQL
          UPDATE
             XFNDSTRS
          SET
             XSCNT = -1
          WHERE
             XSHDL = :LPHDL;

       // Close cursor
       Exec SQL
          CLOSE
             XFNDSTRI;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pXFNDSTR_search...
     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     pXFNDSTR_clear...
     p                 B                   Export
     dXFNDSTR_clear...
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      /Free

       // Clear file 'Status'
       Exec SQL
          DELETE FROM
             XFNDSTRS
          WHERE
             XSHDL = :LPHDL;

       // Clear file 'Input'
       Exec SQL
          DELETE FROM
             XFNDSTRI
          WHERE
             XIHDL = :LPHDL;

       // Clear file 'Output'
       Exec SQL
          DELETE FROM
             XFNDSTRO
          WHERE
             XOHDL = :LPHDL;

       // Leave procedure
       Return;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pXFNDSTR_clear...
     p                 E
