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
      //*         --PARM--  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*ALL)');                           *
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

      // Common type definitions

     d/Copy QCPYSRC,TYPES

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

      // Procedure prototype for procedure 'QUSRJOBI'

     d/Copy QCPYSRC,QUSRJOBI

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

     d/Copy QCPYSRC,ICONV

      // Locale functions

     d/Copy QCPYSRC,LOCALE

     d matchesResult_t...
     d                 DS                  Qualified Based(pDummy)
     d  mustBeFound                    N
     d  hasBeenFound                   N

      // Record format template for file QSRCF

     dQSRCFRF_t        DS           240    Qualified Based(pDummy)
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

      // iconv() handle
     d GIconv          DS                  qualified
     d  isInit                         N   inz(*off)
     d  job_ccsid                          like(CCSID) inz(-1)
     d  nl_ccsid                           like(CCSID) inz(-1)
     d  hIconv                             likeds(iconv_t) inz

      // This array has a 1:1 relation to
      // the LSARGS input array of procedure FNDSTR_search().
     d GRegex          DS                  Dim(SRCHOPTS_ARGUMENTS_SIZE)
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

     d memberAttrs_t   DS                  Qualified Based(pDummy)
     d  file                         10A
     d  lib                          10A
     d  mbr                          10A
     d  srcType                      10A
     d  lastChgData                    Z

     d GCMemberAttrs   DS                  likeds(memberAttrs_t) inz

      // Local procedures
      //
     d performSearch...
     d                 PR                  Extproc('performSearch')
     d LPHDL                         10S 0 Const                                --> Handle
     d LSFILE                        10A   Const                                --> Source File
     d LSLIB                         10A   Const                                --> Library
     d LSMBR                         10A   Const                                --> Member
     d LIRLNG                         5S 0 Const                                --> Record length
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCOPT                      10A   Const                                --> Match option
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     d getFinalResult...
     d                 PR              N   Extproc('getFinalResult')
     d  i_matchOpt                   10A   Const
     d  i_numArgs                    10I 0 Const
     d  i_matchesResult...
     d                                     Const LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     d initResultArray...
     d                 PR                  Extproc('initResultArray')
     d                                     LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d  i_numArgs                    10I 0 Const
     d  i_searchArgs                       Const Likeds(searchArgument_t)
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     d removeMember...
     d                 PR                  Extproc('removeMember')
     d  i_handle                     10S 0 Const
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const

     d resolveMemberAttrs...
     d                 PR                  Likeds(memberAttrs_t)
     d                                     Extproc('resolveMemberAttrs')
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

     d updateStatus...
     d                 PR                  Extproc('updateStatus')
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

     d freeRegEx       PR                  Extproc('freeRegEx')

     d initIconv       PR                  Extproc('initIconv')

     d freeIconv       PR                  Extproc('freeIconv')

     d cvtCcsid        PR                        like(searchArgument_t.string)
     d                                     Extproc('cvtCcsid')
     d  i_string                           value like(searchArgument_t.string)

     d retrieveJobCcsid...
     d                 PR            10I 0 Extproc('retrieveJobCcsid')

     d removeFileOpenMessage...
     d                 PR                  Extproc('removeFileOpenMessage')
     d  i_msgId                       7A   Const

      //*==========================================================================================*
      //* Get Handle                                                                               *
      //*==========================================================================================*

     pFNDSTR_getHandle...
     p                 B                   Export
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

       // Free iconv() handle
       freeIconv();

       // Leave procedure
       Return LIHDL;

      /End-Free

       //*------------------------------------------------------------------------------------------

     p                 E

      //*==========================================================================================*
      //* Resolve Generic Search Elements (exclusively used by DEMO1)                              *
      //*==========================================================================================*

     pFNDSTR_resolveGenericSearchElements...
     p                 B                   Export
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     dFNDSTRIRF      E DS                  ExtName(FNDSTRI) Qualified

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
              'QTEMP     ' :                       // --> Qual. user space
              'MBRL0330' :                         // --> Format name
              FNDSTRIRF.XIFILE + FNDSTRIRF.XILIB : // --> Qual. file
              '*ALL' :                             // --> Member name
              '0' :                                // --> Override process
              ERRC0100                             // <-> Error code
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
                        :FNDSTRIRF.XILIB,
                        :FNDSTRIRF.XIFILE,
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

     p                 E

      //*==========================================================================================*
      //* Get Number Of Search Elements                                                            *
      //*==========================================================================================*

     pFNDSTR_getNumberOfSearchElements...
     p                 B                   Export
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

     p                 E

      //*==========================================================================================*
      //* Search                                                                                   *
      //*==========================================================================================*

     pFNDSTR_search...
     p                 B                   Export
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCOPT                      10A   Const                                --> Match option
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

      // Record format for file FNDSTRI

     dFNDSTRIRF      E DS                  ExtName(FNDSTRI) Qualified

     dctrlBreak        DS                  Qualified
     d  LILIB                        10A                                        Library
     d  LIFILE                       10A                                        File

     dfileAttrs        DS                  Qualified
     d  LITYP1                        1A                                        Type 1
     d  LITYP2                        1A                                        Type 2
     d  LIRLNG                        5S 0                                      Record length

     dLICNT            S              6S 0                                      Counter
     dLICNL            S             10A                                        Cancel

      //*------------------------------------------------------------------------------------------*

      /Free

       If (LISIZE <= 0);
         updateStatus(LPHDL: -1);
         Return;
       EndIf;

       // Clear cache of member attributes
       Clear GCMemberAttrs;

       // Initialize character translation
       initIconv();

       // Initialize counter
       LICNT = *Zero;

       // Initialize control break
       ctrlBreak.LILIB = *LoVal;
       ctrlBreak.LIFILE = *LoVal;

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
         If FNDSTRIRF.XILIB <> ctrlBreak.LILIB Or
            FNDSTRIRF.XIFILE <> ctrlBreak.LIFILE;

           // Set control break
           ctrlBreak.LILIB = FNDSTRIRF.XILIB;
           ctrlBreak.LIFILE = FNDSTRIRF.XIFILE;

           // Initialize attributes
           fileAttrs.LITYP1 = *Blank;
           fileAttrs.LITYP2 = *Blank;
           fileAttrs.LIRLNG = *Zero;

           // Get attributes
           Exec SQL
              SELECT
                 TABLE_TYPE,
                 FILE_TYPE,
                 ROW_LENGTH
              INTO
                 :fileAttrs.LITYP1,
                 :fileAttrs.LITYP2,
                 :fileAttrs.LIRLNG
              FROM
                 QSYS2/SYSTABLES
              WHERE
                 TABLE_SCHEMA = :FNDSTRIRF.XILIB AND
                 TABLE_NAME = :FNDSTRIRF.XIFILE;

           // Not the right attributes
           If Not (fileAttrs.LITYP1 = 'P' And
                 fileAttrs.LITYP2 = 'S' And
                 fileAttrs.LIRLNG <> *Zero);
             Iter;
           EndIf;

           // Get record length
           fileAttrs.LIRLNG = fileAttrs.LIRLNG -
                              %Size(QSRCFRF_t.SRCSEQ) -
                              %Size(QSRCFRF_t.SRCDAT);
           If fileAttrs.LIRLNG > %Size(QSRCFRF_t.SRCDTA);
             fileAttrs.LIRLNG = %Size(QSRCFRF_t.SRCDTA);
           EndIf;

         EndIf;

         performSearch(FNDSTRIRF.XIHDL
                       : FNDSTRIRF.XIFILE: FNDSTRIRF.XILIB: FNDSTRIRF.XIMBR
                       : fileAttrs.LIRLNG
                       : LSSHWRCDS: LSMTCOPT: LISIZE: LSARGS);

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

     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     pFNDSTR_clear...
     p                 B                   Export
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      /Free

       // Free regular expression handles
       freeRegEx();

       // Free iconv handle
       freeIconv();

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

     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     p performSearch...
     p                 B
     d                 PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LSFILE                        10A   Const                                --> Source File
     d LSLIB                         10A   Const                                --> Library
     d LSMBR                         10A   Const                                --> Member
     d LIRLNG                         5S 0 Const                                --> Record length
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCOPT                      10A   Const                                --> Match option
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     dLISTR            S              3S 0                                      Start
     dLILNG            S              3S 0                                      Length
     dLISTMT           S              6S 0                                      Statement
     dLILINE           S            228A                                        Line
     dLISTG            S                   Like(searchArgument_t.string)        String
     dLIIDX            S             10I 0                                      Search arg. index
     dLIRCDS           S             10I 0                                      Counter for records

      // This array has a 1:1 relation to
      // the LSARGS input array.
     d matchesResult   DS                  LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d                                     Inz

     d rc              S             10I 0
     d cflags          S             10I 0
     d nmatch          S             10U 0 Inz(1)
     d regmatch        DS                  Likeds(regmatch_t) inz
     d error           S            256A

     d searchArgFound  S               N
     d recordFound     S               N
     d memberFound     S               N

     d srcLine         DS                  LikeDs(QSRCFRF_t)
      //*------------------------------------------------------------------------------------------*

      /free

         // Open member
         GIFILE = %Trim(LSLIB) + '/' + %Trim(LSFILE);
         GIMBR = LSMBR;
         Open(E) QSRCF;

         // Error during open of member occured
         If %Error;
           Return;
         Else;
           removeFileOpenMessage('CPF4011');
         EndIf;

         // Initialize match result array
         matchesResult = initResultArray(LISIZE: LSARGS);

         // Initialize statement
         LISTMT = *Zero;

         // Initialize record counter
         LIRCDS = *Zero;

         DoW GILoop;

           // Read member
           Read QSRCF srcLine;

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
             LILINE = %SubSt(srcLine.SRCDTA : LISTR : LILNG);

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
               // Perform regular expression search:
               // Compile regular expression
               If not GRegex(LIIDX).isInit and not GRegex(LIIDX).isError;
                 LISTG = cvtCcsid(LISTG);
                 rc = regcomp(GRegex(LIIDX).handle: LISTG: cflags);
                 If rc = 0;
                   GRegex(LIIDX).isInit = *ON;
                 Else;
                   GRegex(LIIDX).isInit = *OFF;
                   GRegex(LIIDX).isError = *ON;
                   regerror(
                     rc: GRegex(LIIDX).handle: %addr(error): %size(error));
                 Endif;
               Endif;

               // Match regular expression
               If GRegex(LIIDX).isInit and not GRegex(LIIDX).isError;
                 LILINE = cvtCcsid(LILINE);
                 rc = regexec(
                        GRegex(LIIDX).handle: LILINE: nmatch: regmatch: 0);
                 Select;
                 When rc = 0;             // found!
                   searchArgFound = *ON;
                 When rc = REG_NOMATCH;   // not found
                   searchArgFound = *OFF;
                 Other;                   // error!
                   GRegex(LIIDX).isError = *ON;
                   regerror(
                     rc: GRegex(LIIDX).handle: %addr(error): %size(error));
                 Endsl;
               Endif;
             Else;
               // Perform standard string search:
               searchArgFound = %Scan(%TrimR(LISTG) : LILINE) > *Zero;
             Endif;

             If searchArgFound;
               matchesResult(LIIDX).hasBeenFound = *ON;
               If (matchesResult(LIIDX).mustBeFound);
                 // Set 'recordFond' for match 'all/any' mode
                 recordFound = *ON;
               Else;
                 // For "contains not" criterias,
                 // records are not logged.
               EndIf;
             EndIf;

           EndFor;

           If (LSMTCOPT = SRCHOPTS_MATCH_LINE);
             // Set 'recordFond' for match 'line mode'
             recordFound = getFinalResult(
                             SRCHOPTS_MATCH_ALL: LISIZE: matchesResult);
             // Reset match result array
             matchesResult = initResultArray(LISIZE: LSARGS);
           EndIf;

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

           addMember(LPHDL: LSFILE: LSLIB: LSMBR: LISTMT: srcLine.SRCDTA);

         EndDo;

         // Close member
         Close QSRCF;

         If (LSMTCOPT <> SRCHOPTS_MATCH_LINE);

           // Evaluate final result
           memberFound = getFinalResult(LSMTCOPT: LISIZE: matchesResult);

           // Set final result
           If (LIRCDS > 0);
             If (not memberFound);
               // Remove statement records in case
               // of a "must not contain" condition has not been met.
               removeMember(LPHDL: LSFILE: LSLIB: LSMBR);
             EndIf;
           Else;
             If (memberFound);
               // Ensure at least a "member record" in case
               // we only had "must not contain" condition.
               addMember(LPHDL: LSFILE: LSLIB: LSMBR: 0
                         : %trim(LSLIB) + '/' + %trim(LSFILE)
                           + '.' + %trim(LSMBR));
             EndIf;
           EndIf;
         EndIf;

      /end-free
     p                 E

      //*==========================================================================================*
      //* Initialize result array.                                                                 *
      //*==========================================================================================*

     p initResultArray...
     p                 B                   Export
     d                 PI                  LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d  i_numArgs                    10I 0 Const
     d  i_searchArgs                       Const Likeds(searchArgument_t)
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     d i               S             10i 0
     d matchesResult   DS                  LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
      //*------------------------------------------------------------------------------------------*

      /Free

         Clear matchesResult;

         For i = 1 to i_numArgs;
           Select;
           When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS);
             matchesResult(i).mustBeFound = *ON;
           When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT);
             matchesResult(i).mustBeFound = *OFF;
           EndSl;
           matchesResult(i).hasBeenFound = *OFF;
         EndFor;

         Return matchesResult;

      /end-free
     p                 E

      //*==========================================================================================*
      //* Computes the final result.                                                               *
      //*==========================================================================================*

     p getFinalResult...
     p                 B                   Export
     d                 PI              N
     d  i_matchOpt                   10A   Const
     d  i_numArgs                    10I 0 Const
     d  i_matchesResult...
     d                                     Const LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     d i               S             10I 0
     d result          S               N
      //*------------------------------------------------------------------------------------------*

      /Free

         Select;
         When (i_matchOpt = SRCHOPTS_MATCH_ALL);
           result = *ON;
           For i = 1 to i_numArgs;
             If ((i_matchesResult(i).mustBeFound and
                  i_matchesResult(i).hasBeenFound) or
                 (not i_matchesResult(i).mustBeFound and
                  not i_matchesResult(i).hasBeenFound));
               // No further action required
             Else;
               result = *OFF;
               Leave;
             EndIf;
           EndFor;
         When (i_matchOpt = SRCHOPTS_MATCH_ANY);
           result = *OFF;
           For i = 1 to i_numArgs;
             If ((i_matchesResult(i).mustBeFound and
                  i_matchesResult(i).hasBeenFound) or
                 (not i_matchesResult(i).mustBeFound and
                  not i_matchesResult(i).hasBeenFound));
               result = *ON;
               Leave;
             EndIf;
           EndFor;
         EndSl;

         Return result;

      /end-free
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
      //* Resolves the attributes of a given member.                                               *
      //*==========================================================================================*

     p resolveMemberAttributes...
     p                 B                   Export
     d                 PI                  Likeds(memberAttrs_t)
     d  i_file                       10A   Const
     d  i_lib                        10A   Const
     d  i_mbr                        10A   Const

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
           If (i_file <> GCMemberAttrs.file or
               i_lib <> GCMemberAttrs.lib or
               i_mbr <> GCMemberAttrs.mbr);

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

             GCMemberAttrs.file = i_file;
             GCMemberAttrs.lib = i_lib;
             GCMemberAttrs.mbr = i_mbr;
             GCMemberAttrs.srcType = mbrd0100.srcType;
             GCMemberAttrs.lastChgData = %date(apiTimestamp.date: *CYMD0)
                                         + %time(apiTimestamp.time: *HMS0);
           EndIf;

           Return GCMemberAttrs;

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

     d memberAttrs     DS                  Likeds(memberAttrs_t) Inz
      //*------------------------------------------------------------------------------------------*

      /Free

           // Retrieve member attributes
           memberAttrs = resolveMemberAttributes(i_file: i_lib: i_mbr);

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
                  XOTYPE,
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
                  :memberAttrs.srcType,
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

         For LIIDX = 1 to %elem(GRegex);
            If (GRegex(LIIDX).isInit);
               regfree(GRegex(LIIDX).handle);
               GRegex(LIIDX).isInit = *OFF;
               GRegex(LIIDX).isError = *OFF;
               GRegex(LIIDX).handle = *ALLx'00';
            EndIf;
         EndFor;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Initialize character translation.
      //*==========================================================================================*

     P initIconv       B
     D                 PI

     D from            DS                  likeds(QtqCode_t)
     D to              DS                  likeds(QtqCode_t)
     D rc              S             10I 0 inz

     D DIGITS          C                   const(%len(GIconv.nl_ccsid))
     D DECPOS          C                   const(%decpos(GIconv.nl_ccsid))
      /free

         // Free iconv() handle
         freeIconv(); // Just in case ...

         GIconv.job_ccsid = retrieveJobCcsid();
         GIconv.nl_ccsid = %dec(%str(nl_langinfo(CODESET)): DIGITS: DECPOS);

         If (GIconv.job_ccsid = GIconv.nl_ccsid);
            GIconv.isInit = *off;
            Return;
         EndIf;

         from.CCSID = GIconv.job_ccsid;
         from.ConvAlt = 0;    // Conversion alternative
         from.SubsAlt = 0;    // Substitution alternative
         from.ShiftAlt = 1;   // Shift-state alternative
         from.InpLenOp = 0;   // Input length option
         from.ErrorOpt = 1;   // Error option for mixed data
         from.Reserved = *ALLx'00';

         to.CCSID = GIconv.nl_ccsid;
         to.ConvAlt = 0;      // Conversion alternative
         to.SubsAlt = 0;      // Substitution alternative
         to.ShiftAlt = 1;     // Shift-state alternative
         to.InpLenOp = 0;     // Input length option
         to.ErrorOpt = 1;     // Error option for mixed data
         to.Reserved = *ALLx'00';

         // Cache iconv() handle
         GIconv.isInit = *on;
         GIconv.hIconv = QtqIconvOpen(to: from);

      /end-free
     P                 E

      //*==========================================================================================*
      //* Free iconv handle.
      //*==========================================================================================*

     P freeIConv       B
     D                 PI
      /free

         If (not GIconv.isInit);
            Return;
         EndIf;

         iconv_close(GIconv.hIconv);

         Clear GIconv;
         GIconv.isInit = *off;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Retrieve job ccsid.
      //*==========================================================================================*

     P retrieveJobCcsid...
     P                 B
     D                 PI            10I 0

     D qJob            DS                  likeds(qJob_t) inz
     D jobi0400        DS                  likeds(jobi0400_t) inz
      /free

         Clear qJob;
         qJob.name = '*';

         QUSRJOBI(jobi0400: %size(jobi0400): 'JOBI0400': qJob: '');

         return jobi0400.ccsid;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Converts a given string to the CCSID of the current locale.                              *
      //*==========================================================================================*

     P cvtCcsid        B
     D                 PI                        like(searchArgument_t.string)
     D  i_string                           value like(searchArgument_t.string)

     D rc              S             10I 0 inz

     D bufferSize      S             10U 0 inz
     D inBytesLeft     S             10U 0 inz
     D outBytesLeft    S             10U 0 inz
     D outBuffer       S            512A
     D outLength       S             10I 0
     D pInBuf          S               *   inz
     D pOutBuf         S               *   inz
      /Free

         If (not GIconv.isInit);
            Return i_string;
         EndIf;

         bufferSize = %size(outBuffer);
         inBytesLeft = %len(%trimR(i_string));
         outBytesLeft = bufferSize;
         pInBuf = %addr(i_string: *data);
         pOutBuf= %addr(outBuffer);
         rc = iconv(giconv.hIconv: pInBuf: inBytesLeft: pOutBuf: outBytesLeft);

         outLength = bufferSize - outBytesLeft;

         return %subst(outBuffer: 1: outLength);

      /End-Free
     p                 E

      //*==========================================================================================*
      //* Removes 'Buffer length longer than record for member DEMO5.' (CPF4011) message.          *
      //*==========================================================================================*

     p removeFileOpenMessage...
     p                 b
     d                 pi
     d  i_msgId                       7A   Const

      // Receive Program Message (QMHRCVPM) API
     d QMHRCVPM...
     d                 pr                  extpgm('QMHRCVPM')
     d  o_msgInf                  65535a          options(*varsize)
     d  i_msgInfLen                  10i 0 const
     d  i_format                      8a   const
     d  i_callStkE                  256a   const
     d  i_callStkC                   10i 0 const
     d  i_msgType                    10a   const
     d  i_msgKey                      4a   const
     d  i_wait                       10i 0 const
     d  i_action                     10a   const
     d  io_ErrCode                65535a          options(*varsize)

      // Inclomplete format RCVM0100
     d rcvm0100...
     d                 ds                  qualified inz
     d  bytRet                       10i 0                                      Bytes ret.
     d  bytAvl                       10i 0                                      Bytes avail.
     d  msgSev                       10i 0                                      Msg Severty
     d  msgID                         7a                                        Msg ID
     d  msgType                       2a                                        Msg Type
     d  msgKey                        4a                                        Msg Key

      // Remove Program Messages (QMHRMVPM) API
     d QMHRMVPM...
     d                 pr                  extpgm('QMHRMVPM')
     d  i_callStkE                  256a   const
     d  i_callStkC                   10i 0 const
     d  i_msgKey                      4a   const
     d  i_msgToRmv                   10a   const
     d  io_ErrCode                65535a          options(*varsize)

      // Incomplete IBM i API error code
     d errCode         ds                  qualified
     d  bytPrv                       10i 0 inz(%size(errCode))
     d  bytAvl                       10i 0 inz

      /Free

         QMHRCVPM(rcvm0100: %size(rcvm0100): 'RCVM0100'
                  : '*': 1: '*LAST': '': 0: '*SAME': errCode);

         if (rcvm0100.msgID = i_msgId);
           QMHRMVPM('*': 1: rcvm0100.msgKey: '*BYKEY': errCode);
         endif;

      /End-Free
     p                 e
