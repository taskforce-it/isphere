      //*------------------------------------------------------------------------------------------*
      //* Find String in Source File                                                               *
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
      //*         >>PARM<<  RPGPPOPT(*LVL2);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain bnddir('QC2LE')
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

     fQSRCF     IF   F  240        Disk    ExtFile(GIFILE)
     f                                     ExtMbr(GIMBR)
     f                                     InfDs(srcMbrInf)
     f                                     UsrOpn

     d srcMbrInf       ds                  likeds(infDS_DB_t)

      //*------------------------------------------------------------------------------------------*

     d/define fileInfDS_DB
     d/Copy QCPYSRC,INFDS

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

      // Procedure prototype for procedure 'QUSRMBRD'

     d/Copy QCPYSRC,QUSRMBRD

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

      // Regular expression functions

     d/Copy QCPYSRC,REGEX

      // Character translation functions

     d*Copy QCPYSRC,ICONV

      // Locale functions

     d*Copy QCPYSRC,LOCALE

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

     dNULL             C                   -1

      // This array has a 1:1 relation to
      // the LSARGS input array of procedure FNDSTR_search().
     d gregex          DS                  Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d                                     Qualified
     d                                     Inz
     d  isInit                         N   Inz(*OFF)
     d  isError                        N   Inz(*OFF)
      /if defined(*V5R4M0)
     d  handle                             Likeds(regex_t) inz
      /else
     d  handle                             Like(regex_t) inz(*ALLx'00')
      /endif

      // Caching of member attributes

     d  memberAttrs    DS                  Qualified Inz
     d   file                        10A
     d   lib                         10A
     d   mbr                         10A
     d   lastChgData                   Z

      // Local procedures

     d removeMember...
     d                 PR                  Extproc('removeMember')
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const

     d addMember...
     d                 PR                  Extproc('addMember')
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const
     d  i_stmt                        6S 0 Const
     d  i_srcdta                    228A   Const
     d  i_lastChgDate                  Z   Const

     d updateStatus...
     d                 PR                  Extproc('updateStatus')
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

     d freeRegEx       PR                  Extproc('freeRegEx')

     d convertApiDateToTimestamp...
     d                 PR              Z   Extproc('convertApiDateToTimestamp')
     d  i_apiDate                    13A   Const

     d*cvtCcsid_lc     PR                        like(searchArgument_t.string)
     d*                                    Extproc('cvtCcsid_lc')
     d* i_string                           value like(searchArgument_t.string)

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

       // Free regular expression handles
       freeRegEx();

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

     d lastChangedDate...
     d                 S               Z

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

                 // Convert field "Last source change date and time"
                 lastChangedDate = convertApiDateToTimestamp(MBRDChgDate);

                 // Write file 'Input'
                 Exec SQL
                    INSERT
                       INTO
                    FNDSTRI
                       (
                        XIHDL,
                        XILIB,
                        XIFILE,
                        XIMBR,
                        XIFLCD
                       )
                    VALUES
                       (
                        :LPHDL,
                        :XILIB,
                        :XIFILE,
                        :MBRDMbr,
                        :lastChangedDate
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
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCALL                        N   Const                                --> Match all finds
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

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
     dLISTG            S                   Like(searchArgument_t.string)        String
     dLICNT            S              6S 0                                      Counter
     dLIRCDS           S             10I 0                                      Counter for records
     dLICNL            S             10A                                        Cancel
     dLIIDX            S             10I 0                                      Search arg. index

      // This array has a 1:1 relation to
      // the LSARGS input array.
     d matchesResult   DS                  Qualified
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d                                     Inz
     d  mustBeFound                    N
     d  hasBeenFound                   N

     d rc              S             10I 0
     d cflags          S             10I 0
     d nmatch          S             10U 0 Inz(1)
     d regmatch        DS                  Likeds(regmatch_t) inz
     d error           S            256A

     d searchArgFound  S               N
     d recordFound     S               N
     d memberFound     S               N

      //*------------------------------------------------------------------------------------------*

      /Free

       If (LISIZE <= 0);
         updateStatus(LPHDL: -1);
         Return;
       EndIf;

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
                XIMBR,
                XIFLCD
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
                 QSYS2/SYSTABLES
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

         EndIf;

         // Open member
         GIFILE = %Trim(XILIB) + '/' + %Trim(XIFILE);
         GIMBR = XIMBR;
         Open(E) QSRCF;

         // Error during open of member occured
         If %Error;
           Iter;
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

         // Initialize statement
         LISTMT = *Zero;

         // Initialize record counter
         LIRCDS = *Zero;

         // Initialize "member found" indicator
         memberFound = *OFF;

         DoW GILoop;

           // Read member
           Read QSRCF QSRCFRF;

           // Leave condition
           If %EOF;
             Leave;
           EndIf;

           // Increase statement
           LISTMT = LISTMT + 1;

           // Check search arguments
           recordFound = *OFF;
           For LIIDX = 1 to LISIZE;

             // Get start and length
             Select;
             When (LSARGS(LIIDX).fromColumn = NULL);
               LISTR = 1;
             When (LSARGS(LIIDX).fromColumn <= LIRLNG);
               LISTR = LSARGS(LIIDX).fromColumn;
             Other;
               LISTR = LIRLNG;
             EndSl;

             Select;
             When (LSARGS(LIIDX).toColumn = NULL);
               LILNG = LIRLNG;
             When LSARGS(LIIDX).toColumn > LIRLNG;
               LILNG = LIRLNG - LSARGS(LIIDX).fromColumn + 1;
             Other;
               LILNG = LSARGS(LIIDX).toColumn - LSARGS(LIIDX).fromColumn + 1;
             EndSl;

             // Initialize line
             LILINE = %SubSt(SRCDTA : LISTR : LILNG);

             // Initialize string
             LISTG = LSARGS(LIIDX).string;

             // Initialize regular expression search
             cflags = REG_EXTENDED;

             // Ignore upper/lower case
             If LSARGS(LIIDX).case = SRCHOPTS_CASE_IGNORE;
               If LSARGS(LIIDX).regex <> SRCHOPTS_ARG_REGEX;
                 LISTG = %XLate(GILO:GIUP:LISTG);
                 LILINE = %XLate(GILO:GIUP:LILINE);
               Else;
                 cflags = cflags + REG_ICASE;
               Endif;
             EndIf;

             // Initialize search result.
             searchArgFound = *OFF;

             // Match!
             If LSARGS(LIIDX).regex = SRCHOPTS_ARG_REGEX;
               If not gregex(LIIDX).isInit and not gregex(LIIDX).isError;
                 // LISTG = cvtCcsid_lc(LISTG);
                 rc = regcomp(gregex(LIIDX).handle: LISTG: cflags);
                 If rc = 0;
                   gregex(LIIDX).isInit = *ON;
                 Else;
                   gregex(LIIDX).isInit = *OFF;
                   gregex(LIIDX).isError = *ON;
                   regerror(
                     rc: gregex(LIIDX).handle: %addr(error): %size(error));
                 Endif;
               Endif;
               If gregex(LIIDX).isInit and not gregex(LIIDX).isError;
                 // LILINE = cvtCcsid_lc(LILINE);
                 rc = regexec(
                        gregex(LIIDX).handle: LILINE: nmatch: regmatch: 0);
                 Select;
                 When rc = 0;             // found!
                   searchArgFound = *ON;
                 When rc = REG_NOMATCH;   // not found
                   searchArgFound = *OFF;
                 Other;                   // error!
                   gregex(LIIDX).isError = *ON;
                   regerror(
                     rc: gregex(LIIDX).handle: %addr(error): %size(error));
                 Endsl;
               Endif;
             Else;
               searchArgFound = %Scan(%TrimR(LISTG) : LILINE) > *Zero;
             Endif;

             If searchArgFound;
               matchesResult(LIIDX).hasBeenFound = *ON;
               If (matchesResult(LIIDX).mustBeFound);
                 recordFound = *ON;
                 memberFound = *ON; // For now. Maybe changed below.
               Else;
                 // For "contains not" criterias,
                 // records are not logged.
               EndIf;
             Else;
               If (not matchesResult(LIIDX).mustBeFound);
                 memberFound = *ON; // For now. Maybe changed below.
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

           addMember(LPHDL: XIFILE: XILIB: XIMBR: LISTMT: SRCDTA: XIFLCD);

         EndDo;

         // Close member
         Close QSRCF;


         // Evaluate final result
         For LIIDX = 1 to LISIZE;
           If ((matchesResult(LIIDX).mustBeFound and
                matchesResult(LIIDX).hasBeenFound) or
               (not matchesResult(LIIDX).mustBeFound and
                not matchesResult(LIIDX).hasBeenFound));
             // That is just fine for "match all find conditions"
           Else;
             If (LSMTCALL);
               memberFound = *OFF;
               Leave;
             EndIf;
           EndIf;
         EndFor;

         // Set final result
         If (LIRCDS > 0);
           If (not memberFound);
             // Remove statement records in case
             // of a "must not contain" condition has not been met.
             removeMember(LPHDL: XIFILE: XILIB: XIMBR);
           EndIf;
         Else;
           If (memberFound);
             // Ensure at least a "member record" in case
             // we only had "must not contain" condition.
             addMember(LPHDL: XIFILE: XILIB: XIMBR: 0:
                       %trim(XILIB) + '/' + %trim(XIFILE) + '.' + %trim(XIMBR):
                        XIFLCD);
           EndIf;
         EndIf;

       EndDo;

       // Update file 'Status'
       updateStatus(LPHDL: -1);

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

       // Free regular expression handles
       freeRegEx();

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

      //*==========================================================================================*
      //* Removes a member from the output file.                                                   *
      //*==========================================================================================*

     p removeMember...
     p                 B                   Export
     d                 PI
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const

      //*------------------------------------------------------------------------------------------*

      /Free

       // Clear file 'Output'
       Exec SQL
          DELETE FROM
             FNDSTRO
          WHERE
             XOHDL = :i_handle and
             XOLIB = :i_lib and
             XOFILE = :i_file and
             XOMBR = :i_mbr;

      /End-Free
     p                 E

      //*==========================================================================================*
      //* Adds a member from the output file.                                                      *
      //*==========================================================================================*

     p addMember...
     p                 B                   Export
     d                 PI
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const
     d  i_stmt                        6S 0 Const
     d  i_srcdta                    228A   Const
      * TODO: Remove parameter i_lastChgDate, remove field XIFLCD from file FNDSTRI.
     d  i_lastChgDate                  Z   Const

     d mbrd0100        DS                  Likeds(mbrd0100_t) inz
     d qFile           DS                  Qualified
     d  name                         10A
     d  lib                          10A

     d apiTimestamp    DS                  Qualified
     d  date                          7A
     d  time                          8A
      //*------------------------------------------------------------------------------------------*

      /Free

           // Retrieve member attributes
           If (i_file <> memberAttrs.file or
               i_lib <> memberAttrs.lib or
               i_mbr <> memberAttrs.mbr);

             Clear qFile;
             qFile.name = i_file;
             qFile.lib = i_lib;

             QUSRMBRD(
               mbrd0100 :        // --> Receiver variable
               %size(mbrd0100) : // --> Length of receiver variable
               'MBRD0100' :      // --> Format of returned data
               qFile :           // --> Qualified file name
               i_mbr :           // --> Member name
               '0'               // --> Override processing
             );

             apiTimestamp = mbrd0100.srcChgDatTim;

             memberAttrs.file = i_file;
             memberAttrs.lib = i_lib;
             memberAttrs.mbr = i_mbr;
             memberAttrs.lastChgData = %date(apiTimestamp.date: *CYMD0)
                                       + %time(apiTimestamp.time: *HMS0);
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
                  XOLINE,
                  XOFLCD
                 )
              VALUES
                 (
                  :i_handle,
                  :i_lib,
                  :i_file,
                  :i_mbr,
                  :i_stmt,
                  :i_srcdta,
                  :memberAttrs.lastChgData
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
                 FNDSTRS
              SET
                 XSCNT = :i_status
              WHERE
                 XSHDL = :i_handle;

      /End-Free
     p                 E

      //*==========================================================================================*
      //* Free regular expression handles.
      //*==========================================================================================*

     P freeRegEx       B
     D                 PI

     D LIIDX           S             10I 0 inz
      /free

         For LIIDX = 1 to %elem(gregex);
            If (gregex(LIIDX).isInit);
               regfree(gregex(LIIDX).handle);
               gregex(LIIDX).isInit = *OFF;
               gregex(LIIDX).isError = *OFF;
               gregex(LIIDX).handle = *ALLx'00';
            EndIf;
         EndFor;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Converts an API date of format (CYYMMDDHHMMSS) to a RPG timestamp value.
      //*==========================================================================================*

     p convertApiDateToTimestamp...
     p                 B                   Export
     d                 PI              Z
     d  i_apiDate                    13A   Const

     d apiDate         DS                  Qualified
     d  cyymmdd                       7A
     d  hhmmss                        6A

     d timestamp       S               Z
      //*------------------------------------------------------------------------------------------*

      /Free

         apiDate = i_apiDate;

         timestamp = %date(apiDate.cyymmdd: *CYMD0)
                   + %time(apiDate.hhmmss: *HMS0);

         Return timestamp;

      /End-Free
     p                 E

      //*==========================================================================================*
      //* Converts a given string to the CCSID of the current locale.                              *
      //*==========================================================================================*

     P*cvtCcsid_lc     B
     D*                PI                        like(searchArgument_t.string)
     D* i_string                           value like(searchArgument_t.string)

     D*from            DS                  likeds(QtqCode_t)
     D*to              DS                  likeds(QtqCode_t)
     D*rc              S             10I 0 inz

     D*ic              DS                  likeds(iconv_t)
     D*bufferSize      S             10U 0 inz
     D*inBytesLeft     S             10U 0 inz
     D*outBytesLeft    S             10U 0 inz
     D*string          DS                  qualified
     D* length                        5I 0
     D* value                              like(i_string) overlay(string)
     D*pInBuf          S               *   inz
     D*pOutBuf         S               *   inz

     D*DIGITS          C                   const(%len(to.ccsid))
     D*DECPOS          C                   const(%decpos(to.ccsid))

      //*------------------------------------------------------------------------------------------*

      //Free

      // from.CCSID = 0;
      // from.ConvAlt = 0;
      // from.SubsAlt = 0;
      // from.ShiftAlt = 1;
      // from.InpLenOp = 0;
      // from.ErrorOpt = 1;
      // from.Reserved = *ALLx'00';

      // to.CCSID = %dec(%str(nl_langinfo(CODESET)): DIGITS: DECPOS);
      // to.ConvAlt = 0;
      // to.SubsAlt = 0;
      // to.ShiftAlt = 1;
      // to.InpLenOp = 0;
      // to.ErrorOpt = 1;
      // to.Reserved = *ALLx'00';

      // ic = QtqIconvOpen(to: from);

      // bufferSize = %size(string.value) - 2;
      // inBytesLeft = %len(%trimR(i_string));
      // outBytesLeft = bufferSize;
      // pInBuf = %addr(i_string) + 2;
      // pOutBuf= %addr(string.value) + 2;
      // rc = iconv(ic: pInBuf: inBytesLeft: pOutBuf: outBytesLeft);

      // string.length = bufferSize - outBytesLeft;

      // iconv_close(ic);

      // return %trimR(string.value);

      //End-Free
     p*                E

