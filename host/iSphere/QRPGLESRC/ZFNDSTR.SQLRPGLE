      //*------------------------------------------------------------------------------------------*
      //* Find String in Stream File                                                               *
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
      //* Find String in Stream File                                                               *
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
      //*         --PARM--  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');                          *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain bnddir('QC2LE')
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

      /Copy QCPYSRC,TYPES

      /Copy QCPYSRC,ZFNDSTR

      /Copy QCPYSRC,NBRRNG

      /Copy QCPYSRC,QUSRJOBI

      /Copy QCPYSRC,REGEX

      /Copy QCPYSRC,ICONV

      /Copy QCPYSRC,LOCALE

      /Copy QCPYSRC,QGETFILEA

      /Copy QCPYSRC,STDIO_H

      /Copy QCPYSRC,IFSIO_H

      /Copy QCPYSRC,CLIB_H

      // Local type templates

     D SLASH           C                   '/'
     d path_t          S            256A   varying

      // Local procedures

     d performSearch...
     d                 PR                  Extproc('performSearch')
     d  i_HDL                        10S 0 Const                                --> Handle
     d  i_DIR                       256A   Const                                --> Directory
     d  i_STMF                      256A   Const                                --> Stream file
     d  i_SHWRCDS                      N   Const                                --> Show records
     d  i_MTCOPT                     10A   Const                                --> Match option
     d  i_SIZE                       10I 0 Const                                --> Number of args.
     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
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

     d removeStreamFile...
     d                 PR                  Extproc('removeStreamFile')
     d  i_handle                     10S 0 Const
     d  i_dir                       256A   Const
     d  i_stmf                      256A   Const

     d addStreamFile...
     d                 PR                  Extproc('addStreamFile')
     d  i_handle                     10S 0 Const
     d  i_dir                       256A   Const
     d  i_stmf                      256A   Const
     d  i_stmt                        6S 0 Const
     d  i_srcdta                    512A   Const

     d updateStatus...
     d                 PR                  Extproc('updateStatus')
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

     d freeRegEx...
     d                 PR                  Extproc('freeRegEx')

     d initIconv...
     d                 PR                  Extproc('initIconv')

     d freeIconv...
     d                 PR                  Extproc('freeIconv')

     d getDateLastChanged...
     d                 PR              Z   Extproc('getDateLastChanged')
     d i_path                              const like(path_t)

     d createPath...
     d                 PR                  like(path_t)
     d                                     Extproc('createPath')
     d i_path                              const  likeds(Qlg_Path_Name_t)

     d convertEpochToRpg...
     d                 PR              Z   Extproc('convertEpochToRpg')
     d i_epochTmStmp                 10I 0 value

     d cvtCcsid...
     d                 PR                        like(searchArgument_t.string)
     d                                     Extproc('cvtCcsid')
     d  i_string                           value like(searchArgument_t.string)

     d retrieveJobCcsid...
     d                 PR            10I 0 Extproc('retrieveJobCcsid')

      // Global field declarations

      // iconv() handle
     d GIconv          DS                  qualified
     d  isInit                         N   inz(*off)
     d  job_ccsid                          like(CCSID) inz(-1)
     d  nl_ccsid                           like(CCSID) inz(-1)
     d  hIconv                             likeds(iconv_t) inz

      // This array has a 1:1 relation to
      // the i_ARGS input array of procedure ZFNDSTR_search().
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

     d S_ISDIR         PR             1N
     d   mode                        10U 0 value

     d matchesResult_t...
     d                 DS                  Qualified Based(pDummy)
     d  mustBeFound                    N
     d  hasBeenFound                   N

     d g_Loop          S              1N   Inz(*On)                             Loop
     d g_UP            C                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'         Upper case
     d g_LO            C                   'abcdefghijklmnopqrstuvwxyz'         Lower case
     d NULL            C                   -1

      //*==========================================================================================*
      //* Get Handle                                                                               *
      //*==========================================================================================*

     p ZFNDSTR_getHandle...
     p                 B                   Export

     d                 PI            10S 0                                      <-- Handle

      // Locale field declarations

     d l_HDL           S             10S 0                                      Handle

      /Free

       // Set handle
       l_HDL =
         NBRRNG_getNumber(
          'HANDLE' // --> Number range
         );

       // Write file 'Status'
       Exec SQL
          INSERT
             INTO
          ZFNDSTRS
             (
              XSHDL,
              XSCNT,
              XSCNL
             )
          VALUES
             (
              :l_HDL,
              0,
              '*NO'
             );

       // Free regular expression handles
       freeRegEx();

       // Free iconv() handle
       freeIconv();

       // Leave procedure
       Return l_HDL;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Resolve Generic Search Elements                                                          *
      //*==========================================================================================*

     p ZFNDSTR_resolveGenericSearchElements...
     p                 B                   Export

     d                 PI
     d  i_HDL                        10S 0 Const                                --> Handle

      // Locale field declarations

     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified

     d l_dir           s               *
     d l_stmf          s            256A
     d MyStat          S                   like(statds)

      /Free

       // Declare cursor
       Exec SQL
          DECLARE
             GENERIC
          CURSOR FOR
             SELECT
                *
             FROM
                ZFNDSTRI
             WHERE
                XIHDL = :i_HDL AND
                XISTMF = '*'
             FOR UPDATE;

       // Open cursor
       Exec SQL
          OPEN
             GENERIC;

       DoW g_Loop;

         // Fetch from cursor
         Exec SQL
            FETCH NEXT FROM
               GENERIC
            INTO
               :ZFNDSTRIRF;

         // Leave condition
         If SQLCOD = 100 Or
               SQLCOD < 0;
           Leave;
         EndIf;

         // Delete generic input element
         Exec SQL
            DELETE FROM
               ZFNDSTRI
            WHERE
               CURRENT OF
                  GENERIC;

         // Read contents of directory
         l_dir = opendir(%trim(ZFNDSTRIRF.XIDIR));

         if l_dir <> *NULL;

           p_dirent = readdir(l_dir);

           dow p_dirent <> *NULL;

             // Get the name of the stream file
             l_stmf = %subst(d_name:1:d_namelen);

             if stat(
                  %trim(ZFNDSTRIRF.XIDIR) + '/' + %trim(l_stmf) :
                  %addr(mystat)
                ) >= 0;

               p_statds = %addr(mystat);

               if Not S_ISDIR(st_mode);

                 // Write file 'Input'
                 Exec SQL
                    INSERT
                       INTO
                    ZFNDSTRI
                       (
                        XIHDL,
                        XIDIR,
                        XISTMF
                       )
                    VALUES
                       (
                        :i_HDL,
                        :ZFNDSTRIRF.XIDIR,
                        :l_stmf
                       );

               endif;

             endif;

             p_dirent = readdir(l_dir);

           enddo;

           closedir(l_dir);

         endif;

       EndDo;

       // Close cursor
       Exec SQL
          CLOSE
             GENERIC;

       // Leave procedure
       Return;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Get Number Of Search Elements                                                            *
      //*==========================================================================================*

     p ZFNDSTR_getNumberOfSearchElements...
     p                 B                   Export

     d                 PI             6S 0                                      <-- Number of elem.
     d  i_HDL                        10S 0 Const                                --> Handle

      // Local field declarations

     d l_ELEM          S              6S 0                                      Number of Elements

      /Free

       // Initialize 'Number of elements'
       l_ELEM = *Zero;

       // Get number of elements
       Exec SQL
          SELECT
             COUNT(*)
          INTO
             :l_ELEM
          FROM
             ZFNDSTRI
          WHERE
             XIHDL = :i_HDL;

       // Leave procedure
       Return l_ELEM;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Search                                                                                   *
      //*==========================================================================================*

     p ZFNDSTR_search...
     p                 B                   Export

     d                 PI
     d  i_HDL                        10S 0 Const                                --> Handle
     d  i_SHWRCDS                      N   Const                                --> Show records
     d  i_MTCOPT                     10A   Const                                --> Match option
     d  i_SIZE                       10I 0 Const                                --> Number of args.
     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

      // Record format for file ZFNDSTRI

     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified

      // Locale field declarations

     d l_CNT           S              6S 0                                      Counter
     d l_CNL           S             10A                                        Cancel

      /Free

       If (i_SIZE <= 0);
         updateStatus(i_HDL: -1);
         Return;
       EndIf;

       // Initialize character translation
       initIconv();

       // Initialize counter
       l_CNT = *Zero;

       // Declare cursor
       Exec SQL
          DECLARE
             ZFNDSTRI
          CURSOR FOR
             SELECT
                *
             FROM
                ZFNDSTRI
             WHERE
                XIHDL = :i_HDL
             ORDER BY
                XIHDL,
                XIDIR,
                XISTMF
             FOR READ ONLY;

       // Open cursor
       Exec SQL
          OPEN
             ZFNDSTRI;

       DoW g_Loop;

         // Fetch from cursor
         Exec SQL
            FETCH NEXT FROM
               ZFNDSTRI
            INTO
               :ZFNDSTRIRF;

         // Leave condition
         If SQLCOD = 100 Or
               SQLCOD < 0;
           Leave;
         EndIf;

         // Increase counter
         l_CNT = l_CNT + 1;

         If %Rem(l_CNT : 100) = *Zero;

           // Update file 'Status'
           updateStatus(i_HDL: l_CNT);

           // Initialize 'Cancel'
           l_CNL = *Blanks;

           // Check if search has to be canceled
           Exec SQL
              SELECT
                 XSCNL
              INTO
                 :l_CNL
              FROM
                 ZFNDSTRS
              WHERE
                 XSHDL = :i_HDL;

           // Search has to be canceled
           If l_CNL = '*YES';
             Leave;
           EndIf;

         EndIf;

         // Perform search
         performSearch(
           ZFNDSTRIRF.XIHDL :
           ZFNDSTRIRF.XIDIR:
           ZFNDSTRIRF.XISTMF:
           i_SHWRCDS:
           i_MTCOPT:
           i_SIZE:
           i_ARGS
         );

       EndDo;

       // Update file 'Status'
       updateStatus(i_HDL: -1);

       // Close cursor
       Exec SQL
          CLOSE
             ZFNDSTRI;

       // Leave procedure
       Return;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Clear                                                                                    *
      //*==========================================================================================*

     p ZFNDSTR_clear...
     p                 B                   Export

     d                 PI
     d  i_HDL                        10S 0 Const                                --> Handle

      /Free

       // Free regular expression handles
       freeRegEx();

       // Free iconv handle
       freeIconv();

       // Clear file 'Status'
       Exec SQL
          DELETE FROM
             ZFNDSTRS
          WHERE
             XSHDL = :i_HDL;

       // Clear file 'Input'
       Exec SQL
          DELETE FROM
             ZFNDSTRI
          WHERE
             XIHDL = :i_HDL;

       // Clear file 'Output'
       Exec SQL
          DELETE FROM
             ZFNDSTRO
          WHERE
             XOHDL = :i_HDL;

       // Leave procedure
       Return;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Perform search                                                                           *
      //*==========================================================================================*

     p performSearch...
     p                 B

     d                 PI
     d  i_HDL                        10S 0 Const                                --> Handle
     d  i_DIR                       256A   Const                                --> Directory
     d  i_STMF                      256A   Const                                --> Stream file
     d  i_SHWRCDS                      N   Const                                --> Show records
     d  i_MTCOPT                     10A   Const                                --> Match option
     d  i_SIZE                       10I 0 Const                                --> Number of args.
     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     d l_STR           S              3S 0                                      Start
     d l_LNG           S              3S 0                                      Length
     d l_STMT          S              6S 0                                      Statement
     d l_STG           S                   Like(searchArgument_t.string)        String
     d l_IDX           S             10I 0                                      Search arg. index
     d l_RCDS          S             10I 0                                      Counter for records
     d l_RLNG          S              5S 0 Inz(512)                             Record length
      * Length must match SearchOptions.MAX_STRING_SIZE.
     d l_LINE          S            512A                                        Line
     d srcLine         S            512A

      // This array has a 1:1 relation to the i_ARGS input array.
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
     d stmfFound       S               N

     d l_filePtr       S               *
     d l_readPtr       S               *

      /Free

       // Open stream file
       l_filePtr =
         fopen(
           %trim(i_DIR) + '/' + %trim(i_STMF) :
           'r, o_ccsid=0'
         );

       // Error during open of stream file occured
       If (l_filePtr = *null);
         Return;
       EndIf;

       // Initialize match result array
       matchesResult = initResultArray(i_SIZE: i_ARGS);

       // Initialize statement
       l_STMT = *Zero;

       // Initialize record counter
       l_RCDS = *Zero;

       DoW g_Loop;

         // Read stream file
         srcLine = *blanks;
         l_readPtr =
           fgets(
             %addr(srcLine) :
             %len(srcLine) :
             l_filePtr
           );

         // Leave condition
         If (l_readPtr = *null);
           Leave;
         EndIf;

         // Replace control characters by spaces
         srcLine = %xlate(x'00' : ' ' : srcLine);
         srcLine = %xlate(x'05' : ' ' : srcLine);
         srcLine = %xlate(x'0D' : ' ' : srcLine);
         srcLine = %xlate(x'25' : ' ' : srcLine);

         // Increase statement
         l_STMT = l_STMT + 1;

         // Check search arguments
         recordFound = *OFF;

         For l_IDX = 1 to i_SIZE;

           // Get start and length
           Select;
           When (i_ARGS(l_IDX).fromColumn = NULL);
             l_STR = 1;
           When (i_ARGS(l_IDX).fromColumn <= l_RLNG);
             l_STR = i_ARGS(l_IDX).fromColumn;
           Other;
             l_STR = l_RLNG;
           EndSl;

           Select;
           When (i_ARGS(l_IDX).toColumn = NULL);
             l_LNG = l_RLNG;
           When i_ARGS(l_IDX).toColumn > l_RLNG;
             l_LNG = l_RLNG - i_ARGS(l_IDX).fromColumn + 1;
           Other;
             l_LNG = i_ARGS(l_IDX).toColumn - i_ARGS(l_IDX).fromColumn + 1;
           EndSl;

           // Initialize line
           l_LINE = %SubSt(srcLine : l_STR : l_LNG);

           // Initialize string
           l_STG = i_ARGS(l_IDX).string;

           // Initialize regular expression search
           cflags = REG_EXTENDED;

           // Ignore upper/lower case
           If i_ARGS(l_IDX).case = SRCHOPTS_CASE_IGNORE;
             If i_ARGS(l_IDX).regex <> SRCHOPTS_ARG_REGEX;
               l_STG = %XLate(g_LO:g_UP:l_STG);
               l_LINE = %XLate(g_LO:g_UP:l_LINE);
             Else;
               cflags = cflags + REG_ICASE;
             Endif;
           EndIf;

           // Initialize search result.
           searchArgFound = *OFF;

           // Match!
           If i_ARGS(l_IDX).regex = SRCHOPTS_ARG_REGEX;
             // Perform regular expression search:
             // Compile regular expression
             If not GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
               l_STG = cvtCcsid(l_STG);
               rc = regcomp(GRegex(l_IDX).handle: l_STG: cflags);
               If rc = 0;
                 GRegex(l_IDX).isInit = *ON;
               Else;
                 GRegex(l_IDX).isInit = *OFF;
                 GRegex(l_IDX).isError = *ON;
                 regerror(
                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
               Endif;
             Endif;

             // Match regular expression
             If GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
               l_LINE = cvtCcsid(l_LINE);
               rc = regexec(
                      GRegex(l_IDX).handle: l_LINE: nmatch: regmatch: 0);
               Select;
               When rc = 0;             // found!
                 searchArgFound = *ON;
               When rc = REG_NOMATCH;   // not found
                 searchArgFound = *OFF;
               Other;                   // error!
                 GRegex(l_IDX).isError = *ON;
                 regerror(
                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
               Endsl;
             Endif;
           Else;
             // Perform standard string search:
             searchArgFound = %Scan(%TrimR(l_STG) : l_LINE) > *Zero;
           Endif;

           If searchArgFound;
             matchesResult(l_IDX).hasBeenFound = *ON;
             If (matchesResult(l_IDX).mustBeFound);
               // Set 'recordFond' for match 'all/any' mode
               recordFound = *ON;
             Else;
               // For "contains not" criterias,
               // records are not logged.
             EndIf;
           EndIf;

         EndFor;

         If (i_MTCOPT = SRCHOPTS_MATCH_LINE);
           // Set 'recordFond' for match 'line mode'
           recordFound = getFinalResult(
                           SRCHOPTS_MATCH_ALL: i_SIZE: matchesResult);
           // Reset match result array
           matchesResult = initResultArray(i_SIZE: i_ARGS);
         EndIf;

         // Check record found
         // (record matches at least 1 criterum)
         If (not recordFound);
           Iter;
         EndIf;

         // Count output records
         l_RCDS += 1;

         // Check whether or not all records
         // must be added to the output file
         If (not i_SHWRCDS and l_RCDS > 1);
           Iter;
         EndIf;

         addStreamFile(i_HDL: i_DIR: i_STMF: l_STMT: srcLine);

       EndDo;

       // Close stream file
       fclose(l_filePtr);

       If (i_MTCOPT <> SRCHOPTS_MATCH_LINE);

         // Evaluate final result
         stmfFound = getFinalResult(i_MTCOPT: i_SIZE: matchesResult);

         // Set final result
         If (l_RCDS > 0);
           If (not stmfFound);
             // Remove statement records in case
             // of a "must not contain" condition has not been met.
             removeStreamFile(i_HDL: i_DIR: i_STMF);
           EndIf;
         Else;
           If (stmfFound);
             // Ensure at least a "stream file record" in case
             // we only had "must not contain" condition.
             addStreamFile(i_HDL: i_DIR: i_STMF: 0
                           : %trim(i_DIR) + '/' + %trim(i_STMF));
           EndIf;
         EndIf;
       EndIf;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Initialize result array                                                                  *
      //*==========================================================================================*

     p initResultArray...
     p                 B

     d                 PI                  LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d  i_numArgs                    10I 0 Const
     d  i_searchArgs                       Const Likeds(searchArgument_t)
     d                                     Options(*varsize)
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

     d i               S             10i 0
     d matchesResult   DS                  LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

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

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Computes the final result                                                                *
      //*==========================================================================================*

     p getFinalResult...
     p                 B

     d                 PI              N
     d  i_matchOpt                   10A   Const
     d  i_numArgs                    10I 0 Const
     d  i_matchesResult...
     d                                     Const LikeDs(matchesResult_t)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     d i               S             10I 0
     d result          S               N

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

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Removes a stream file from the output file                                               *
      //*==========================================================================================*

     p removeStreamFile...
     p                 B

     d                 PI
     d  i_handle                     10S 0 Const
     d  i_dir                       256A   Const
     d  i_stmf                      256A   Const

      /Free

       // Clear file 'Output'
       Exec SQL
          DELETE FROM
             ZFNDSTRO
          WHERE
             XOHDL = :i_handle and
             XODIR = :i_dir and
             XOSTMF = :i_stmf;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Adds a stream file to the output file                                                    *
      //*==========================================================================================*

     p addStreamFile...
     p                 B

     d                 PI
     d  i_handle                     10S 0 Const
     d  i_dir                       256A   Const
     d  i_stmf                      256A   Const
     d  i_stmt                        6S 0 Const
     d  i_srcdta                    512A   Const

     d stmfAttrs       DS                  Qualified
     d  srcType                      10A
     d  lastChgData                    Z

     d l_pos           S              5S 0
     d path            S                   like(path_t) inz
      /Free

       // Retrieve stream file attributes
       l_pos = %Scan('.' : i_stmf);
       If l_pos = *Zero;
         stmfAttrs.srcType = *blanks;
       Else;
         stmfAttrs.srcType = %SubSt(i_stmf : l_pos + 1);
         stmfAttrs.srcType = %XLate(g_LO:g_UP: stmfAttrs.srcType);
       EndIf;

       path = %trim(i_dir) + SLASH + %trim(i_stmf);
       stmfAttrs.lastChgData = getDateLastChanged(path);

       // Write file 'Output'
       Exec SQL
          INSERT
             INTO
          ZFNDSTRO
             (
              XOHDL,
              XODIR,
              XOSTMF,
              XOTYPE,
              XOSTMT,
              XOLINE,
              XOFLCD
             )
          VALUES
             (
              :i_handle,
              :i_dir,
              :i_stmf,
              :stmfAttrs.srcType,
              :i_stmt,
              :i_srcdta,
              :stmfAttrs.lastChgData
             );

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Updates the status field of the result table                                             *
      //*==========================================================================================*

     p updateStatus...
     p                 B

     d                 PI
     d  i_handle                     10S 0 Const
     d  i_status                     10I 0 Const

      /Free

       // Update file 'Status'
       Exec SQL
          UPDATE
             ZFNDSTRS
          SET
             XSCNT = :i_status
          WHERE
             XSHDL = :i_handle;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Free regular expression handles                                                          *
      //*==========================================================================================*

     p freeRegEx...
     p                 B

     d                 PI

     d l_IDX           S             10I 0 inz

      /Free

       For l_IDX = 1 to %elem(GRegex);
          If (GRegex(l_IDX).isInit);
             regfree(GRegex(l_IDX).handle);
             GRegex(l_IDX).isInit = *OFF;
             GRegex(l_IDX).isError = *OFF;
             GRegex(l_IDX).handle = *ALLx'00';
          EndIf;
       EndFor;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Initialize character translation                                                         *
      //*==========================================================================================*

     p initIconv...
     p                 B

     d                 PI

     d from            DS                  likeds(QtqCode_t)
     d to              DS                  likeds(QtqCode_t)
     d rc              S             10I 0 inz

     d DIGITS          C                   const(%len(GIconv.nl_ccsid))
     d DECPOS          C                   const(%decpos(GIconv.nl_ccsid))

      /Free

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

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Get file last changed date.                                                              *
      //*==========================================================================================*

     P getDateLastChanged...
     P                 B
     D                 PI              Z
     D i_path                              const like(path_t)

     D lastChanged     S               Z   inz

     D rc              S             10I 0 inz
     D bytPrv          S             10U 0 inz
     D sizeNeeded      S             10U 0 inz
     D bytRet          S             10U 0 inz
     D path            DS                  likeds(Qlg_Path_Name_t) inz
     D attrArray       DS                  likeds(Qlg_attrArray_t) inz
     D rtnAttr         DS                  likeds(Qlg_rtnAttr_t)
     D                                     based(pRtnAttr)
     D timeEpo         S             10U 0 based(pTimeEpo)
      * ---------------------------------------------------------
      /free

         // Prepare path name
         path = createPath(i_path);

         // Specify what attributes we want
         clear attrArray;
         attrArray.numE = 1;
         attrArray.attr(1) = QP0L_ATTR_MODIFY_TIME;

         // Allocate buffer for result of Qp0lGetAttr
         bytPrv = 32;
         pRtnAttr = %alloc(bytPrv);
         clear rtnAttr;

         // Retrieve file attributes
         rc = Qp0lGetAttr(path
                          : attrArray
                          : pRtnAttr
                          : bytPrv
                          : sizeNeeded
                          : bytRet
                          : 0);

         if rc = 0;
            pTimeEpo = pRtnAttr + %size(rtnAttr);
            lastChanged = convertEpochToRpg(timeEpo);
         else;
            lastChanged = *LoVal;
         endif;

         return lastChanged;

      /end-free
      *
     P                 E

      //*==========================================================================================*
      //* Initializes the path structure for the Qp0lGetAttr API.                                  *
      //*==========================================================================================*

     P createPath...
     P                 B
     D                 PI                  like(path_t)
     D i_path                              const  likeds(Qlg_Path_Name_t)

     D path            DS                  likeds(Qlg_Path_Name_t) inz
      /free

         clear path;
         path.CCSID               = QLG_PATH_JOB_CCSID;     // Use current job default CCSID
         path.Country_ID          = QLG_PATH_JOB_COUNTRYID; // Use the current job country
                                                            // or region ID
         path.Language_ID         = QLG_PATH_JOB_LANGUAGEID;// Use the current job lang. ID
         path.Reserved            = *ALLx'00';
         path.Path_Type           = QLG_CHAR_SINGLE;        // 0 = Path is character String,
                                                            //     1-byte delimiter.
                                                            // 1 = Path is pointer,
                                                            //     1-byte delimiter.
                                                            // 2 = Path is character String,
                                                            //     2-byte delimiter.
                                                            // 3 = Path is pointer,
                                                            //     2-byte delimiter.
         path.Path_Length         = %len(%trimR(i_path));
         path.Path_Name_Delimiter = SLASH;
         path.Reserved2           = *ALLx'00';
         path.Path_Name           = i_path;

         return path;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Converts a timestamp from Epoch to RPG format.                                           *
      //*==========================================================================================*
     P convertEpochToRpg...
     P                 B
     D                 PI              Z
     D i_epochTmStmp                 10I 0 value

     D tmStmp          S               Z   inz

     D ftimef          S             16A   inz('%Y-%m-%d%H.%M.%S')

     D ftimest         DS                  qualified
     D  date                         10A
     D  time                          8A
     D  null                          1A   inz(x'00')

     D time            DS                  likeds(tm_t)  inz
      /free

         if (localtime_r(i_epochTmStmp: time) <> *NULL);
            if (strftime(%addr(ftimest):%size(ftimest):%trim(ftimef):time) > 0);
               tmStmp=%timestamp(ftimest.date + '-' + ftimest.time + '.000000');
            endif;
         endif;

         return tmStmp;

      /end-free
     P                 E

      //*==========================================================================================*
      //* Free iconv handle                                                                        *
      //*==========================================================================================*

     p freeIConv...
     p                 B

     d                 PI

      /Free

       If (not GIconv.isInit);
          Return;
       EndIf;

       iconv_close(GIconv.hIconv);

       Clear GIconv;
       GIconv.isInit = *off;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Retrieve job ccsid                                                                       *
      //*==========================================================================================*

     p retrieveJobCcsid...
     p                 B

     d                 PI            10I 0

     d qJob            DS                  likeds(qJob_t) inz
     d jobi0400        DS                  likeds(jobi0400_t) inz

      /Free

       Clear qJob;
       qJob.name = '*';

       QUSRJOBI(jobi0400: %size(jobi0400): 'JOBI0400': qJob: '');

       return jobi0400.ccsid;

      /End-Free

     p                 E

      //*==========================================================================================*
      //* Converts a given string to the CCSID of the current locale                               *
      //*==========================================================================================*

     p cvtCcsid...
     p                 B

     d                 PI                        like(searchArgument_t.string)
     d  i_string                           value like(searchArgument_t.string)

     d rc              S             10I 0 inz

     d bufferSize      S             10U 0 inz
     d inBytesLeft     S             10U 0 inz
     d outBytesLeft    S             10U 0 inz
     d outBuffer       S            512A
     d outLength       S             10I 0
     d pInBuf          S               *   inz
     d pOutBuf         S               *   inz

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

      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
      *  This tests a file mode to see if a file is a directory.
      *
      * Here is the C code we're trying to duplicate:
      *      #define _S_IFDIR    0040000                                       */
      *      #define S_ISDIR(mode) (((mode) & 0370000) == _S_IFDIR)
      *
      * 1) ((mode) & 0370000) takes the file's mode and performs a
      *      bitwise AND with the octal constant 0370000.  In binary,
      *      that constant looks like: 00000000000000011111000000000000
      *      The effect of this code is to turn off all bits in the
      *      mode, except those marked with a '1' in the binary bitmask.
      *
      * 2) ((result of #1) == _S_IFDIR)  What this does is compare
      *      the result of step 1, above with the _S_IFDIR, which
      *      is defined to be the octal constant 0040000.  In decimal,
      *      that octal constant is 16384.
      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
     p S_ISDIR         B

     d S_ISDIR         PI             1N
     d   mode                        10U 0 value

     d                 DS
     d  dirmode                1      4U 0
     d  byte1                  1      1A
     d  byte2                  2      2A
     d  byte3                  3      3A
     d  byte4                  4      4A

      * Turn off bits in the mode, as in step (1) above.
     c                   eval      dirmode = mode

     c                   bitoff    x'FF'         byte1
     c                   bitoff    x'FE'         byte2
     c                   bitoff    x'0F'         byte3
     c                   bitoff    x'FF'         byte4

      * Compare the result to 0040000, and return true or false.
     c                   if        dirmode = 16384
     c                   return    *On
     c                   else
     c                   return    *Off
     c                   endif

     p                 E
