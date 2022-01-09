      //*------------------------------------------------------------------------------------------*
      //* Find String in Message File                                                              *
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
      //*         >>PARM<<  RPGPPOPT(*LVL2);                                                       *
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

     dNULL             C                   -1

      // Local procedures

     d addMessageDescription...
     d                 PR                  Extproc('addMessageDescription')
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_msgID                       7A   Const
     d  i_msgText                   132A   Const

     d updateStatus...
     d                 PR                  Extproc('updateStatus')
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

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
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCALL                        N   Const                                --> Match all finds
     d LSSECLVLTX                      N   Const                                --> Inc. 2.level txt
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dLISTG            S             40A   varying                              String
     dLITXT            S            132A   varying                              Text to be searched
     dLIHELP           S           3000A   varying                              Text to be searched
     dLICNT            S              6S 0                                      Counter
     dLICNL            S             10A                                        Cancel
     dLIRTVOPT         S             10A                                        Retrieve options
     dLIMSGID          S              7A                                        Message ID
     dLIMSG            S            132A                                        Message
     dLISTR            S              3S 0                                      Start
     dLILNG            S              3S 0                                      Length
     dLIRCDS           S             10I 0                                      Counter for records
     dLIIDX            S             10I 0                                      Search arg. index

      // This array has a 1:1 relation to
      // the LSARGS input array.
     d matchesResult   DS                  Qualified
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d                                     Inz
     d  mustBeFound                    N
     d  hasBeenFound                   N

     d recordFound     S               N

      //*------------------------------------------------------------------------------------------*

      /Free

       If (LISIZE <= 0);
         updateStatus(LPHDL: -1);
         Return;
       EndIf;

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
           updateStatus(LPHDL: LICNT);

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

         // Set "matches to find / not to find"
         For LIIDX = 1 to LISIZE;
           Select;
           When (LSARGS(LIIDX).operator = SRCHOPTS_OPERATOR_CONTAINS);
             matchesResult(LIIDX).mustBeFound = *ON;
           When (LSARGS(LIIDX).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT);
             matchesResult(LIIDX).mustBeFound = *OFF;
           EndSl;
           matchesResult(LIIDX).hasBeenFound = *OFF;
         EndFor;

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

           // Add second level text
           If (LSSECLVLTX and RTVMLenMsgHAvl > 0);
             LIHELP = %SubSt(RTVM0300 : RTVMOffMsgH + 1 : RTVMLenMsgHRtn);
           Else;
             LIHELP = '';
           EndIf;

           // Set retrieve option
           LIRTVOPT = '*NEXT';

           // Check search arguments
           recordFound = *OFF;
           For LIIDX = 1 to LISIZE;

             // Get start and length
             Select;
             When (LSARGS(LIIDX).fromColumn = NULL);
               LISTR = 1;
             When (LSARGS(LIIDX).fromColumn <= 132);
               LISTR = LSARGS(LIIDX).fromColumn;
             Other;
               LISTR = 132;
             EndSl;

             Select;
             When (LSARGS(LIIDX).toColumn = NULL);
               LILNG = 132;
             When LSARGS(LIIDX).toColumn > 132;
               LILNG = 132 - LSARGS(LIIDX).fromColumn + 1;
             Other;
               LILNG = LSARGS(LIIDX).toColumn - LSARGS(LIIDX).fromColumn + 1;
             EndSl;

             // Initialize message text
             LITXT = %trimR(%SubSt(LIMSG : LISTR : LILNG));

             // Initialize string
             LISTG = %trimR(LSARGS(LIIDX).string);

             // Ignore upper/lower case
             If LSARGS(LIIDX).case = SRCHOPTS_CASE_IGNORE;
               LISTG = %XLate(GILO:GIUP:LISTG);
               LITXT = %XLate(GILO:GIUP:LITXT);
               LIHELP = %XLate(GILO:GIUP:LIHELP);
             EndIf;

             // Match!
             If %Scan(LISTG : LITXT) > *Zero or
                %Scan(LISTG : LIHELP) > *Zero;
               matchesResult(LIIDX).hasBeenFound = *ON;
               if (matchesResult(LIIDX).mustBeFound);
                 recordFound = *ON;
               Else;
                 If (LSMTCALL);
                   recordFound = *OFF;
                   Leave;
                 EndIf;
               EndIf;
             Else;
               if (not matchesResult(LIIDX).mustBeFound);
                 recordFound = *ON;
               Else;
                 If (LSMTCALL);
                   recordFound = *OFF;
                   Leave;
                 EndIf;
               EndIf;
             EndIf;

           EndFor;

           // Check record found
           // (record matches at least 1 criterum)
           If (not recordFound);
             Iter;
           EndIf;

           // Count output records
           LIRCDS += 1;

           // Check whether or not all records
           // must be added to the output file
           If (not LSSHWRCDS and LIRCDS > 1);
             Iter;
           EndIf;

           addMessageDescription(LPHDL: XIMSGF: XILIB: LIMSGID: LIMSG);

         EndDo;

       EndDo;

       // Update file 'Status'
       updateStatus(LPHDL: -1);

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

      //*==========================================================================================*
      //* Adds a member to the output file.                                                      *
      //*==========================================================================================*

     p addMessageDescription...
     p                 B                   Export
     d                 PI
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_msgID                       7A   Const
     d  i_msgText                   132A   Const

      //*------------------------------------------------------------------------------------------*

      /Free

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
                  :i_handle,
                  :i_lib,
                  :i_file,
                  :i_msgID,
                  :i_msgText
                 );

      /End-Free
     p                 E

      //*==========================================================================================*
      //* Updates the status field of the result table.                                            *
      //*==========================================================================================*

     p updateStatus...
     p                 B                   Export
     d                 PI
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

      //*------------------------------------------------------------------------------------------*

      /Free

           // Update file 'Status'
           Exec SQL
              UPDATE
                 XFNDSTRS
              SET
                 XSCNT = :i_status
              WHERE
                 XSHDL = :i_handle;

      /End-Free
     p                 E
