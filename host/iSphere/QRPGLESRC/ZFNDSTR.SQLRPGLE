000100220309      //*------------------------------------------------------------------------------------------*
000200220309      //* Find String in Stream File                                                               *
000300220309      //*------------------------------------------------------------------------------------------*
000400220309      //*                                                                                          *
000500220309      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
000600220309      //* All rights reserved. This program and the accompanying materials                         *
000700220309      //* are made available under the terms of the Common Public License v1.0                     *
000800220309      //* which accompanies this distribution, and is available at                                 *
000900220309      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
001000220309      //*                                                                                          *
001100220309      //*------------------------------------------------------------------------------------------*
001200220309      //*                                                                                          *
001300220309      //* Find String in Stream File                                                               *
001400220309      //*                                                                                          *
001500220309      //*------------------------------------------------------------------------------------------*
001600220309      //* STRPREPRC Compile Options:                                                               *
001700220309      //*   >>PRE-COMPILER<<                                                                       *
001800220309      //*     >>CRTCMD<< CRTSQLRPGI   OBJ(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                   *
001900220309      //*       >>COMPILE<<                                                                        *
002000220309      //*         >>PARM<<  OBJTYPE(*MODULE);                                                      *
002100220309      //*         >>PARM<<  COMMIT(*NONE);                                                         *
002200220309      //*         >>PARM<<  TGTRLS(&TR);                                                           *
002300220309      //*         >>PARM<<  CLOSQLCSR(*ENDMOD);                                                    *
002400220311      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
002500220309      //*         >>PARM<<  RPGPPOPT(*LVL2);                                                       *
002600220309      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
002700231006      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
002800231006      //*         --PARM--  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');                          *
002900231006      //*       >>END-COMPILE<<                                                                    *
003000220309      //*       >>EXECUTE<<                                                                        *
003100220309      //*   >>END-PRE-COMPILER<<                                                                   *
003200220309      //*------------------------------------------------------------------------------------------*
003300220309
003400220309     hNoMain bnddir('QC2LE')
003500220309      /copy qcpysrc,h_spec
003600220309      /copy qcpysrc,copyright
003700220309
003800220309      //*------------------------------------------------------------------------------------------*
003900220309
004000220310      /Copy QCPYSRC,TYPES
004100220309
004200220310      /Copy QCPYSRC,ZFNDSTR
004300220309
004400220310      /Copy QCPYSRC,NBRRNG
004500220309
004600220310      /Copy QCPYSRC,QUSRJOBI
004700220309
004800220310      /Copy QCPYSRC,REGEX
004900220309
005000220310      /Copy QCPYSRC,ICONV
005100220309
005200220310      /Copy QCPYSRC,LOCALE
005300220310
005400220404      /Copy QCPYSRC,QGETFILEA
005500220404
005600220310      /Copy QCPYSRC,STDIO_H
005700220310
005800220310      /Copy QCPYSRC,IFSIO_H
005900220309
006000220404      /Copy QCPYSRC,CLIB_H
006100220404
006200220404      // Local type templates
006300220404
006400220404     D SLASH           C                   '/'
006500220404     d path_t          S            256A   varying
006600220404
006700220309      // Local procedures
006800220309
006900220309     d performSearch...
007000220309     d                 PR                  Extproc('performSearch')
007100220310     d  i_HDL                        10S 0 Const                                --> Handle
007200220310     d  i_DIR                       256A   Const                                --> Directory
007300220310     d  i_STMF                      256A   Const                                --> Stream file
007400220310     d  i_SHWRCDS                      N   Const                                --> Show records
007500220310     d  i_MTCOPT                     10A   Const                                --> Match option
007600220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
007700220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
007800220309     d                                     Options(*varsize)
007900220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
008000220309
008100220309     d getFinalResult...
008200220309     d                 PR              N   Extproc('getFinalResult')
008300220309     d  i_matchOpt                   10A   Const
008400220309     d  i_numArgs                    10I 0 Const
008500220309     d  i_matchesResult...
008600220309     d                                     Const LikeDs(matchesResult_t)
008700220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
008800220309
008900220309     d initResultArray...
009000220309     d                 PR                  Extproc('initResultArray')
009100220309     d                                     LikeDs(matchesResult_t)
009200220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
009300220309     d  i_numArgs                    10I 0 Const
009400220309     d  i_searchArgs                       Const Likeds(searchArgument_t)
009500220309     d                                     Options(*varsize)
009600220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
009700220309
009800220310     d removeStreamFile...
009900220310     d                 PR                  Extproc('removeStreamFile')
010000220309     d  i_handle                     10S 0 Const
010100220310     d  i_dir                       256A   Const
010200220310     d  i_stmf                      256A   Const
010300220309
010400220310     d addStreamFile...
010500220310     d                 PR                  Extproc('addStreamFile')
010600220309     d  i_handle                     10S 0 Const
010700220310     d  i_dir                       256A   Const
010800220310     d  i_stmf                      256A   Const
010900220309     d  i_stmt                        6S 0 Const
011000220309     d  i_srcdta                    512A   Const
011100220309
011200220309     d updateStatus...
011300220309     d                 PR                  Extproc('updateStatus')
011400220309     d  i_handle                     10S 0 Const
011500220309     d  i_status                     10I 0 Const
011600220309
011700220310     d freeRegEx...
011800220310     d                 PR                  Extproc('freeRegEx')
011900220309
012000220310     d initIconv...
012100220310     d                 PR                  Extproc('initIconv')
012200220309
012300220310     d freeIconv...
012400220310     d                 PR                  Extproc('freeIconv')
012500220404
012600220404     d getDateLastChanged...
012700220404     d                 PR              Z   Extproc('getDateLastChanged')
012800220404     d i_path                              const like(path_t)
012900220404
013000220404     d createPath...
013100220404     d                 PR                  like(path_t)
013200220404     d                                     Extproc('createPath')
013300220404     d i_path                              const  likeds(Qlg_Path_Name_t)
013400220404
013500220404     d convertEpochToRpg...
013600220404     d                 PR              Z   Extproc('convertEpochToRpg')
013700220404     d i_epochTmStmp                 10I 0 value
013800220404
013900220310     d cvtCcsid...
014000220310     d                 PR                        like(searchArgument_t.string)
014100220309     d                                     Extproc('cvtCcsid')
014200220309     d  i_string                           value like(searchArgument_t.string)
014300220309
014400220309     d retrieveJobCcsid...
014500220309     d                 PR            10I 0 Extproc('retrieveJobCcsid')
014600220310
014700220310      // Global field declarations
014800220310
014900220310      // iconv() handle
015000220310     d GIconv          DS                  qualified
015100220310     d  isInit                         N   inz(*off)
015200220310     d  job_ccsid                          like(CCSID) inz(-1)
015300220310     d  nl_ccsid                           like(CCSID) inz(-1)
015400220310     d  hIconv                             likeds(iconv_t) inz
015500220310
015600220310      // This array has a 1:1 relation to
015700220310      // the i_ARGS input array of procedure ZFNDSTR_search().
015800220310     d GRegex          DS                  Dim(SRCHOPTS_ARGUMENTS_SIZE)
015900220310     d                                     Qualified
016000220310     d                                     Inz
016100220310     d  isInit                         N   Inz(*OFF)
016200220310     d  isError                        N   Inz(*OFF)
016300220310      /if defined(*V5R4M0)
016400220310     d  handle                             Likeds(regex_t) inz
016500220310      /else
016600220310     d  handle                             Like(regex_t) inz(*ALLx'00')
016700220310      /endif
016800220311
016900220311     d S_ISDIR         PR             1N
017000220311     d   mode                        10U 0 value
017100220310
017200220310     d matchesResult_t...
017300220310     d                 DS                  Qualified Based(pDummy)
017400220310     d  mustBeFound                    N
017500220310     d  hasBeenFound                   N
017600220310
017700220310     d g_Loop          S              1N   Inz(*On)                             Loop
017800220310     d g_UP            C                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'         Upper case
017900220310     d g_LO            C                   'abcdefghijklmnopqrstuvwxyz'         Lower case
018000220310     d NULL            C                   -1
018100220309
018200220309      //*==========================================================================================*
018300220309      //* Get Handle                                                                               *
018400220309      //*==========================================================================================*
018500220309
018600220310     p ZFNDSTR_getHandle...
018700220309     p                 B                   Export
018800220309
018900220309     d                 PI            10S 0                                      <-- Handle
019000220309
019100220309      // Locale field declarations
019200220309
019300220310     d l_HDL           S             10S 0                                      Handle
019400220309
019500220310      /Free
019600220309
019700220309       // Set handle
019800220310       l_HDL =
019900220309         NBRRNG_getNumber(
020000220309          'HANDLE' // --> Number range
020100220309         );
020200220309
020300220309       // Write file 'Status'
020400220309       Exec SQL
020500220309          INSERT
020600220309             INTO
020700220309          ZFNDSTRS
020800220309             (
020900220309              XSHDL,
021000220309              XSCNT,
021100220309              XSCNL
021200220309             )
021300220309          VALUES
021400220309             (
021500220310              :l_HDL,
021600220309              0,
021700220309              '*NO'
021800220309             );
021900220309
022000220309       // Free regular expression handles
022100220309       freeRegEx();
022200220309
022300220309       // Free iconv() handle
022400220309       freeIconv();
022500220309
022600220309       // Leave procedure
022700220310       Return l_HDL;
022800220309
022900220310      /End-Free
023000220309
023100220309     p                 E
023200220309
023300220309      //*==========================================================================================*
023400220309      //* Resolve Generic Search Elements                                                          *
023500220309      //*==========================================================================================*
023600220309
023700220310     p ZFNDSTR_resolveGenericSearchElements...
023800220309     p                 B                   Export
023900220309
024000220309     d                 PI
024100220310     d  i_HDL                        10S 0 Const                                --> Handle
024200220309
024300220309      // Locale field declarations
024400220309
024500220310     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified
024600220310
024700220310     d l_dir           s               *
024800220310     d l_stmf          s            256A
024900220311     d MyStat          S                   like(statds)
025000220309
025100220310      /Free
025200220309
025300220309       // Declare cursor
025400220309       Exec SQL
025500220309          DECLARE
025600220309             GENERIC
025700220309          CURSOR FOR
025800220309             SELECT
025900220309                *
026000220309             FROM
026100220309                ZFNDSTRI
026200220309             WHERE
026300220310                XIHDL = :i_HDL AND
026400220309                XISTMF = '*'
026500220309             FOR UPDATE;
026600220309
026700220309       // Open cursor
026800220309       Exec SQL
026900220309          OPEN
027000220309             GENERIC;
027100220309
027200220310       DoW g_Loop;
027300220309
027400220309         // Fetch from cursor
027500220309         Exec SQL
027600220309            FETCH NEXT FROM
027700220309               GENERIC
027800220309            INTO
027900220309               :ZFNDSTRIRF;
028000220309
028100220309         // Leave condition
028200220309         If SQLCOD = 100 Or
028300220309               SQLCOD < 0;
028400220309           Leave;
028500220309         EndIf;
028600220309
028700220309         // Delete generic input element
028800220309         Exec SQL
028900220309            DELETE FROM
029000220309               ZFNDSTRI
029100220309            WHERE
029200220309               CURRENT OF
029300220309                  GENERIC;
029400220310
029500220310         // Read contents of directory
029600220310         l_dir = opendir(%trim(ZFNDSTRIRF.XIDIR));
029700220311
029800220310         if l_dir <> *NULL;
029900220311
030000220310           p_dirent = readdir(l_dir);
030100220311
030200220310           dow p_dirent <> *NULL;
030300220311
030400220311             // Get the name of the stream file
030500220311             l_stmf = %subst(d_name:1:d_namelen);
030600220311
030700220311             if stat(
030800220311                  %trim(ZFNDSTRIRF.XIDIR) + '/' + %trim(l_stmf) :
030900220311                  %addr(mystat)
031000220311                ) >= 0;
031100220311
031200220311               p_statds = %addr(mystat);
031300220311
031400220311               if Not S_ISDIR(st_mode);
031500220311
031600220311                 // Write file 'Input'
031700220311                 Exec SQL
031800220311                    INSERT
031900220311                       INTO
032000220311                    ZFNDSTRI
032100220311                       (
032200220311                        XIHDL,
032300220311                        XIDIR,
032400220311                        XISTMF
032500220311                       )
032600220311                    VALUES
032700220311                       (
032800220311                        :i_HDL,
032900220311                        :ZFNDSTRIRF.XIDIR,
033000220311                        :l_stmf
033100220311                       );
033200220311
033300220311               endif;
033400220311
033500220311             endif;
033600220310
033700220310             p_dirent = readdir(l_dir);
033800220311
033900220310           enddo;
034000220311
034100220310           closedir(l_dir);
034200220311
034300220310         endif;
034400220309
034500220309       EndDo;
034600220309
034700220309       // Close cursor
034800220309       Exec SQL
034900220309          CLOSE
035000220309             GENERIC;
035100220309
035200220309       // Leave procedure
035300220309       Return;
035400220309
035500220310      /End-Free
035600220309
035700220309     p                 E
035800220309
035900220309      //*==========================================================================================*
036000220309      //* Get Number Of Search Elements                                                            *
036100220309      //*==========================================================================================*
036200220309
036300220310     p ZFNDSTR_getNumberOfSearchElements...
036400220309     p                 B                   Export
036500220309
036600220309     d                 PI             6S 0                                      <-- Number of elem.
036700220310     d  i_HDL                        10S 0 Const                                --> Handle
036800220309
036900220309      // Local field declarations
037000220309
037100220310     d l_ELEM          S              6S 0                                      Number of Elements
037200220309
037300220310      /Free
037400220309
037500220309       // Initialize 'Number of elements'
037600220310       l_ELEM = *Zero;
037700220309
037800220309       // Get number of elements
037900220309       Exec SQL
038000220309          SELECT
038100220309             COUNT(*)
038200220309          INTO
038300220310             :l_ELEM
038400220309          FROM
038500220309             ZFNDSTRI
038600220309          WHERE
038700220310             XIHDL = :i_HDL;
038800220309
038900220309       // Leave procedure
039000220310       Return l_ELEM;
039100220309
039200220310      /End-Free
039300220309
039400220309     p                 E
039500220309
039600220309      //*==========================================================================================*
039700220309      //* Search                                                                                   *
039800220309      //*==========================================================================================*
039900220309
040000220310     p ZFNDSTR_search...
040100220309     p                 B                   Export
040200220309
040300220309     d                 PI
040400220310     d  i_HDL                        10S 0 Const                                --> Handle
040500220310     d  i_SHWRCDS                      N   Const                                --> Show records
040600220310     d  i_MTCOPT                     10A   Const                                --> Match option
040700220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
040800220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
040900220309     d                                     Options(*varsize)
041000220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
041100220309
041200220309      // Record format for file ZFNDSTRI
041300220309
041400220310     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified
041500220310
041600220310      // Locale field declarations
041700220309
041800220310     d l_CNT           S              6S 0                                      Counter
041900220310     d l_CNL           S             10A                                        Cancel
042000220309
042100220310      /Free
042200220309
042300220310       If (i_SIZE <= 0);
042400220310         updateStatus(i_HDL: -1);
042500220309         Return;
042600220309       EndIf;
042700220309
042800220309       // Initialize character translation
042900220309       initIconv();
043000220309
043100220309       // Initialize counter
043200220310       l_CNT = *Zero;
043300220309
043400220309       // Declare cursor
043500220309       Exec SQL
043600220309          DECLARE
043700220309             ZFNDSTRI
043800220309          CURSOR FOR
043900220309             SELECT
044000220309                *
044100220309             FROM
044200220309                ZFNDSTRI
044300220309             WHERE
044400220310                XIHDL = :i_HDL
044500220309             ORDER BY
044600220309                XIHDL,
044700220310                XIDIR,
044800220310                XISTMF
044900220309             FOR READ ONLY;
045000220309
045100220309       // Open cursor
045200220309       Exec SQL
045300220309          OPEN
045400220309             ZFNDSTRI;
045500220309
045600220310       DoW g_Loop;
045700220309
045800220309         // Fetch from cursor
045900220309         Exec SQL
046000220309            FETCH NEXT FROM
046100220309               ZFNDSTRI
046200220309            INTO
046300220309               :ZFNDSTRIRF;
046400220309
046500220309         // Leave condition
046600220309         If SQLCOD = 100 Or
046700220309               SQLCOD < 0;
046800220309           Leave;
046900220309         EndIf;
047000220309
047100220309         // Increase counter
047200220310         l_CNT = l_CNT + 1;
047300220309
047400220310         If %Rem(l_CNT : 100) = *Zero;
047500220309
047600220309           // Update file 'Status'
047700220310           updateStatus(i_HDL: l_CNT);
047800220309
047900220309           // Initialize 'Cancel'
048000220310           l_CNL = *Blanks;
048100220309
048200220309           // Check if search has to be canceled
048300220309           Exec SQL
048400220309              SELECT
048500220309                 XSCNL
048600220309              INTO
048700220310                 :l_CNL
048800220309              FROM
048900220309                 ZFNDSTRS
049000220309              WHERE
049100220310                 XSHDL = :i_HDL;
049200220309
049300220309           // Search has to be canceled
049400220310           If l_CNL = '*YES';
049500220309             Leave;
049600220309           EndIf;
049700220309
049800220309         EndIf;
049900220309
050000220310         // Perform search
050100220310         performSearch(
050200220310           ZFNDSTRIRF.XIHDL :
050300220310           ZFNDSTRIRF.XIDIR:
050400220310           ZFNDSTRIRF.XISTMF:
050500220310           i_SHWRCDS:
050600220310           i_MTCOPT:
050700220310           i_SIZE:
050800220310           i_ARGS
050900220310         );
051000220310
051100220309       EndDo;
051200220309
051300220309       // Update file 'Status'
051400220310       updateStatus(i_HDL: -1);
051500220309
051600220309       // Close cursor
051700220309       Exec SQL
051800220309          CLOSE
051900220309             ZFNDSTRI;
052000220309
052100220309       // Leave procedure
052200220309       Return;
052300220309
052400220310      /End-Free
052500220309
052600220309     p                 E
052700220309
052800220309      //*==========================================================================================*
052900220309      //* Clear                                                                                    *
053000220309      //*==========================================================================================*
053100220309
053200220310     p ZFNDSTR_clear...
053300220309     p                 B                   Export
053400220309
053500220309     d                 PI
053600220310     d  i_HDL                        10S 0 Const                                --> Handle
053700220309
053800220310      /Free
053900220309
054000220309       // Free regular expression handles
054100220309       freeRegEx();
054200220309
054300220309       // Free iconv handle
054400220309       freeIconv();
054500220309
054600220309       // Clear file 'Status'
054700220309       Exec SQL
054800220309          DELETE FROM
054900220309             ZFNDSTRS
055000220309          WHERE
055100220310             XSHDL = :i_HDL;
055200220309
055300220309       // Clear file 'Input'
055400220309       Exec SQL
055500220309          DELETE FROM
055600220309             ZFNDSTRI
055700220309          WHERE
055800220310             XIHDL = :i_HDL;
055900220309
056000220309       // Clear file 'Output'
056100220309       Exec SQL
056200220309          DELETE FROM
056300220309             ZFNDSTRO
056400220309          WHERE
056500220310             XOHDL = :i_HDL;
056600220309
056700220309       // Leave procedure
056800220309       Return;
056900220309
057000220310      /End-Free
057100220309
057200220309     p                 E
057300220309
057400220309      //*==========================================================================================*
057500220310      //* Perform search                                                                           *
057600220309      //*==========================================================================================*
057700220309
057800220309     p performSearch...
057900220309     p                 B
058000220309
058100220309     d                 PI
058200220310     d  i_HDL                        10S 0 Const                                --> Handle
058300220310     d  i_DIR                       256A   Const                                --> Directory
058400220310     d  i_STMF                      256A   Const                                --> Stream file
058500220310     d  i_SHWRCDS                      N   Const                                --> Show records
058600220310     d  i_MTCOPT                     10A   Const                                --> Match option
058700220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
058800220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
058900220310     d                                     Options(*varsize)
059000220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
059100220309
059200220310     d l_STR           S              3S 0                                      Start
059300220310     d l_LNG           S              3S 0                                      Length
059400220310     d l_STMT          S              6S 0                                      Statement
059500220310     d l_STG           S                   Like(searchArgument_t.string)        String
059600220310     d l_IDX           S             10I 0                                      Search arg. index
059700220310     d l_RCDS          S             10I 0                                      Counter for records
059800220310     d l_RLNG          S              5S 0 Inz(512)                             Record length
059900231006      * Length must match SearchOptions.MAX_STRING_SIZE.
060000231006     d l_LINE          S            512A                                        Line
060100220310     d srcLine         S            512A
060200220309
060300220310      // This array has a 1:1 relation to the i_ARGS input array.
060400220309     d matchesResult   DS                  LikeDs(matchesResult_t)
060500220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
060600220309     d                                     Inz
060700220309
060800220309     d rc              S             10I 0
060900220309     d cflags          S             10I 0
061000220309     d nmatch          S             10U 0 Inz(1)
061100220309     d regmatch        DS                  Likeds(regmatch_t) inz
061200220309     d error           S            256A
061300220309
061400220309     d searchArgFound  S               N
061500220309     d recordFound     S               N
061600220310     d stmfFound       S               N
061700220310
061800220310     d l_filePtr       S               *
061900220310     d l_readPtr       S               *
062000220309
062100220310      /Free
062200220309
062300220310       // Open stream file
062400220310       l_filePtr =
062500220310         fopen(
062600220310           %trim(i_DIR) + '/' + %trim(i_STMF) :
062700220310           'r, o_ccsid=0'
062800220310         );
062900220310
063000220310       // Error during open of stream file occured
063100220310       If (l_filePtr = *null);
063200220310         Return;
063300220310       EndIf;
063400220310
063500220310       // Initialize match result array
063600220310       matchesResult = initResultArray(i_SIZE: i_ARGS);
063700220310
063800220310       // Initialize statement
063900220310       l_STMT = *Zero;
064000220310
064100220310       // Initialize record counter
064200220310       l_RCDS = *Zero;
064300220310
064400220310       DoW g_Loop;
064500220310
064600220310         // Read stream file
064700220310         srcLine = *blanks;
064800220310         l_readPtr =
064900220310           fgets(
065000220310             %addr(srcLine) :
065100220310             %len(srcLine) :
065200220310             l_filePtr
065300220310           );
065400220310
065500220310         // Leave condition
065600220310         If (l_readPtr = *null);
065700220310           Leave;
065800220310         EndIf;
065900220310
066000220310         // Replace control characters by spaces
066100220310         srcLine = %xlate(x'00' : ' ' : srcLine);
066200220310         srcLine = %xlate(x'05' : ' ' : srcLine);
066300220310         srcLine = %xlate(x'0D' : ' ' : srcLine);
066400220310         srcLine = %xlate(x'25' : ' ' : srcLine);
066500220310
066600220310         // Increase statement
066700220310         l_STMT = l_STMT + 1;
066800220310
066900220310         // Check search arguments
067000220310         recordFound = *OFF;
067100220310
067200220310         For l_IDX = 1 to i_SIZE;
067300220310
067400220310           // Get start and length
067500220310           Select;
067600220310           When (i_ARGS(l_IDX).fromColumn = NULL);
067700220310             l_STR = 1;
067800220310           When (i_ARGS(l_IDX).fromColumn <= l_RLNG);
067900220310             l_STR = i_ARGS(l_IDX).fromColumn;
068000220310           Other;
068100220310             l_STR = l_RLNG;
068200220310           EndSl;
068300220310
068400220310           Select;
068500220310           When (i_ARGS(l_IDX).toColumn = NULL);
068600220310             l_LNG = l_RLNG;
068700220310           When i_ARGS(l_IDX).toColumn > l_RLNG;
068800220310             l_LNG = l_RLNG - i_ARGS(l_IDX).fromColumn + 1;
068900220310           Other;
069000220310             l_LNG = i_ARGS(l_IDX).toColumn - i_ARGS(l_IDX).fromColumn + 1;
069100220310           EndSl;
069200220310
069300220310           // Initialize line
069400220310           l_LINE = %SubSt(srcLine : l_STR : l_LNG);
069500220310
069600220310           // Initialize string
069700220310           l_STG = i_ARGS(l_IDX).string;
069800220310
069900220310           // Initialize regular expression search
070000220310           cflags = REG_EXTENDED;
070100220310
070200220310           // Ignore upper/lower case
070300220310           If i_ARGS(l_IDX).case = SRCHOPTS_CASE_IGNORE;
070400220310             If i_ARGS(l_IDX).regex <> SRCHOPTS_ARG_REGEX;
070500220310               l_STG = %XLate(g_LO:g_UP:l_STG);
070600220310               l_LINE = %XLate(g_LO:g_UP:l_LINE);
070700220310             Else;
070800220310               cflags = cflags + REG_ICASE;
070900220310             Endif;
071000220310           EndIf;
071100220310
071200220310           // Initialize search result.
071300220310           searchArgFound = *OFF;
071400220310
071500220310           // Match!
071600220310           If i_ARGS(l_IDX).regex = SRCHOPTS_ARG_REGEX;
071700220310             // Perform regular expression search:
071800220310             // Compile regular expression
071900220310             If not GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
072000220310               l_STG = cvtCcsid(l_STG);
072100220310               rc = regcomp(GRegex(l_IDX).handle: l_STG: cflags);
072200220310               If rc = 0;
072300220310                 GRegex(l_IDX).isInit = *ON;
072400220310               Else;
072500220310                 GRegex(l_IDX).isInit = *OFF;
072600220310                 GRegex(l_IDX).isError = *ON;
072700220310                 regerror(
072800220310                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
072900220310               Endif;
073000220310             Endif;
073100220310
073200220310             // Match regular expression
073300220310             If GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
073400220310               l_LINE = cvtCcsid(l_LINE);
073500220310               rc = regexec(
073600220310                      GRegex(l_IDX).handle: l_LINE: nmatch: regmatch: 0);
073700220310               Select;
073800220310               When rc = 0;             // found!
073900220310                 searchArgFound = *ON;
074000220310               When rc = REG_NOMATCH;   // not found
074100220310                 searchArgFound = *OFF;
074200220310               Other;                   // error!
074300220310                 GRegex(l_IDX).isError = *ON;
074400220310                 regerror(
074500220310                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
074600220310               Endsl;
074700220310             Endif;
074800220310           Else;
074900220310             // Perform standard string search:
075000220310             searchArgFound = %Scan(%TrimR(l_STG) : l_LINE) > *Zero;
075100220310           Endif;
075200220310
075300220310           If searchArgFound;
075400220310             matchesResult(l_IDX).hasBeenFound = *ON;
075500220310             If (matchesResult(l_IDX).mustBeFound);
075600220310               // Set 'recordFond' for match 'all/any' mode
075700220310               recordFound = *ON;
075800220310             Else;
075900220310               // For "contains not" criterias,
076000220310               // records are not logged.
076100220310             EndIf;
076200220310           EndIf;
076300220310
076400220310         EndFor;
076500220310
076600220310         If (i_MTCOPT = SRCHOPTS_MATCH_LINE);
076700220310           // Set 'recordFond' for match 'line mode'
076800220310           recordFound = getFinalResult(
076900220310                           SRCHOPTS_MATCH_ALL: i_SIZE: matchesResult);
077000220310           // Reset match result array
077100220310           matchesResult = initResultArray(i_SIZE: i_ARGS);
077200220310         EndIf;
077300220310
077400220310         // Check record found
077500220310         // (record matches at least 1 criterum)
077600220310         If (not recordFound);
077700220310           Iter;
077800220310         EndIf;
077900220310
078000220310         // Count output records
078100220310         l_RCDS += 1;
078200220310
078300220310         // Check whether or not all records
078400220310         // must be added to the output file
078500220310         If (not i_SHWRCDS and l_RCDS > 1);
078600220310           Iter;
078700220310         EndIf;
078800220310
078900220310         addStreamFile(i_HDL: i_DIR: i_STMF: l_STMT: srcLine);
079000220310
079100220310       EndDo;
079200220310
079300220310       // Close stream file
079400220310       fclose(l_filePtr);
079500220310
079600220310       If (i_MTCOPT <> SRCHOPTS_MATCH_LINE);
079700220310
079800220310         // Evaluate final result
079900220310         stmfFound = getFinalResult(i_MTCOPT: i_SIZE: matchesResult);
080000220310
080100220310         // Set final result
080200220310         If (l_RCDS > 0);
080300220310           If (not stmfFound);
080400220310             // Remove statement records in case
080500220310             // of a "must not contain" condition has not been met.
080600220310             removeStreamFile(i_HDL: i_DIR: i_STMF);
080700220310           EndIf;
080800220310         Else;
080900220310           If (stmfFound);
081000220310             // Ensure at least a "stream file record" in case
081100220310             // we only had "must not contain" condition.
081200220310             addStreamFile(i_HDL: i_DIR: i_STMF: 0
081300220310                           : %trim(i_DIR) + '/' + %trim(i_STMF));
081400220310           EndIf;
081500220310         EndIf;
081600220310       EndIf;
081700220309
081800220310      /End-Free
081900220310
082000220309     p                 E
082100220309
082200220309      //*==========================================================================================*
082300220310      //* Initialize result array                                                                  *
082400220309      //*==========================================================================================*
082500220309
082600220309     p initResultArray...
082700220309     p                 B
082800220309
082900220309     d                 PI                  LikeDs(matchesResult_t)
083000220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
083100220309     d  i_numArgs                    10I 0 Const
083200220309     d  i_searchArgs                       Const Likeds(searchArgument_t)
083300220309     d                                     Options(*varsize)
083400220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
083500220309
083600220309     d i               S             10i 0
083700220309     d matchesResult   DS                  LikeDs(matchesResult_t)
083800220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
083900220309
084000220310      /Free
084100220309
084200220310       Clear matchesResult;
084300220310
084400220310       For i = 1 to i_numArgs;
084500220310         Select;
084600220310         When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS);
084700220310           matchesResult(i).mustBeFound = *ON;
084800220310         When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT);
084900220310           matchesResult(i).mustBeFound = *OFF;
085000220310         EndSl;
085100220310         matchesResult(i).hasBeenFound = *OFF;
085200220310       EndFor;
085300220310
085400220310       Return matchesResult;
085500220309
085600220310      /End-Free
085700220310
085800220309     p                 E
085900220309
086000220309      //*==========================================================================================*
086100220310      //* Computes the final result                                                                *
086200220309      //*==========================================================================================*
086300220309
086400220309     p getFinalResult...
086500220309     p                 B
086600220309
086700220309     d                 PI              N
086800220309     d  i_matchOpt                   10A   Const
086900220309     d  i_numArgs                    10I 0 Const
087000220309     d  i_matchesResult...
087100220309     d                                     Const LikeDs(matchesResult_t)
087200220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
087300220309
087400220309     d i               S             10I 0
087500220309     d result          S               N
087600220309
087700220310      /Free
087800220309
087900220310       Select;
088000220310       When (i_matchOpt = SRCHOPTS_MATCH_ALL);
088100220310         result = *ON;
088200220310         For i = 1 to i_numArgs;
088300220310           If ((i_matchesResult(i).mustBeFound and
088400220310                i_matchesResult(i).hasBeenFound) or
088500220310               (not i_matchesResult(i).mustBeFound and
088600220310                not i_matchesResult(i).hasBeenFound));
088700220310             // No further action required
088800220310           Else;
088900220310             result = *OFF;
089000220310             Leave;
089100220310           EndIf;
089200220310         EndFor;
089300220310       When (i_matchOpt = SRCHOPTS_MATCH_ANY);
089400220310         result = *OFF;
089500220310         For i = 1 to i_numArgs;
089600220310           If ((i_matchesResult(i).mustBeFound and
089700220310                i_matchesResult(i).hasBeenFound) or
089800220310               (not i_matchesResult(i).mustBeFound and
089900220310                not i_matchesResult(i).hasBeenFound));
090000220310             result = *ON;
090100220310             Leave;
090200220310           EndIf;
090300220310         EndFor;
090400220310       EndSl;
090500220310
090600220310       Return result;
090700220309
090800220310      /End-Free
090900220310
091000220309     p                 E
091100220309
091200220309      //*==========================================================================================*
091300220310      //* Removes a stream file from the output file                                               *
091400220309      //*==========================================================================================*
091500220309
091600220310     p removeStreamFile...
091700220309     p                 B
091800220309
091900220309     d                 PI
092000220309     d  i_handle                     10S 0 Const
092100220310     d  i_dir                       256A   Const
092200220310     d  i_stmf                      256A   Const
092300220309
092400220310      /Free
092500220309
092600220309       // Clear file 'Output'
092700220309       Exec SQL
092800220309          DELETE FROM
092900220309             ZFNDSTRO
093000220309          WHERE
093100220309             XOHDL = :i_handle and
093200220310             XODIR = :i_dir and
093300220310             XOSTMF = :i_stmf;
093400220309
093500220310      /End-Free
093600220310
093700220309     p                 E
093800220309
093900220309      //*==========================================================================================*
094000220310      //* Adds a stream file to the output file                                                    *
094100220309      //*==========================================================================================*
094200220309
094300220310     p addStreamFile...
094400220309     p                 B
094500220309
094600220309     d                 PI
094700220309     d  i_handle                     10S 0 Const
094800220310     d  i_dir                       256A   Const
094900220310     d  i_stmf                      256A   Const
095000220309     d  i_stmt                        6S 0 Const
095100220309     d  i_srcdta                    512A   Const
095200220309
095300220310     d stmfAttrs       DS                  Qualified
095400220310     d  srcType                      10A
095500220310     d  lastChgData                    Z
095600220310
095700220310     d l_pos           S              5S 0
095800220404     d path            S                   like(path_t) inz
095900220310      /Free
096000220309
096100220310       // Retrieve stream file attributes
096200220310       l_pos = %Scan('.' : i_stmf);
096300220310       If l_pos = *Zero;
096400220310         stmfAttrs.srcType = *blanks;
096500220310       Else;
096600220310         stmfAttrs.srcType = %SubSt(i_stmf : l_pos + 1);
096700220311         stmfAttrs.srcType = %XLate(g_LO:g_UP: stmfAttrs.srcType);
096800220310       EndIf;
096900220404
097000220404       path = %trim(i_dir) + SLASH + %trim(i_stmf);
097100220404       stmfAttrs.lastChgData = getDateLastChanged(path);
097200220310
097300220310       // Write file 'Output'
097400220310       Exec SQL
097500220310          INSERT
097600220310             INTO
097700220310          ZFNDSTRO
097800220310             (
097900220310              XOHDL,
098000220310              XODIR,
098100220310              XOSTMF,
098200220310              XOTYPE,
098300220310              XOSTMT,
098400220310              XOLINE,
098500220310              XOFLCD
098600220310             )
098700220310          VALUES
098800220310             (
098900220310              :i_handle,
099000220310              :i_dir,
099100220310              :i_stmf,
099200220310              :stmfAttrs.srcType,
099300220310              :i_stmt,
099400220310              :i_srcdta,
099500220310              :stmfAttrs.lastChgData
099600220310             );
099700220309
099800220310      /End-Free
099900220310
100000220309     p                 E
100100220309
100200220309      //*==========================================================================================*
100300220310      //* Updates the status field of the result table                                             *
100400220309      //*==========================================================================================*
100500220309
100600220309     p updateStatus...
100700220309     p                 B
100800220309
100900220309     d                 PI
101000220309     d  i_handle                     10S 0 Const
101100220309     d  i_status                     10I 0 Const
101200220309
101300220310      /Free
101400220309
101500220310       // Update file 'Status'
101600220310       Exec SQL
101700220310          UPDATE
101800220310             ZFNDSTRS
101900220310          SET
102000220310             XSCNT = :i_status
102100220310          WHERE
102200220310             XSHDL = :i_handle;
102300220309
102400220310      /End-Free
102500220309
102600220309     p                 E
102700220309
102800220309      //*==========================================================================================*
102900220310      //* Free regular expression handles                                                          *
103000220309      //*==========================================================================================*
103100220309
103200220310     p freeRegEx...
103300220310     p                 B
103400220309
103500220310     d                 PI
103600220309
103700220310     d l_IDX           S             10I 0 inz
103800220309
103900220310      /Free
104000220309
104100220310       For l_IDX = 1 to %elem(GRegex);
104200220310          If (GRegex(l_IDX).isInit);
104300220310             regfree(GRegex(l_IDX).handle);
104400220310             GRegex(l_IDX).isInit = *OFF;
104500220310             GRegex(l_IDX).isError = *OFF;
104600220310             GRegex(l_IDX).handle = *ALLx'00';
104700220310          EndIf;
104800220310       EndFor;
104900220309
105000220310      /End-Free
105100220309
105200220310     p                 E
105300220309
105400220309      //*==========================================================================================*
105500220310      //* Initialize character translation                                                         *
105600220309      //*==========================================================================================*
105700220309
105800220310     p initIconv...
105900220310     p                 B
106000220309
106100220310     d                 PI
106200220309
106300220310     d from            DS                  likeds(QtqCode_t)
106400220310     d to              DS                  likeds(QtqCode_t)
106500220310     d rc              S             10I 0 inz
106600220309
106700220310     d DIGITS          C                   const(%len(GIconv.nl_ccsid))
106800220310     d DECPOS          C                   const(%decpos(GIconv.nl_ccsid))
106900220310
107000220310      /Free
107100220309
107200220310       // Free iconv() handle
107300220310       freeIconv(); // Just in case ...
107400220310
107500220310       GIconv.job_ccsid = retrieveJobCcsid();
107600220310       GIconv.nl_ccsid = %dec(%str(nl_langinfo(CODESET)): DIGITS: DECPOS);
107700220310
107800220310       If (GIconv.job_ccsid = GIconv.nl_ccsid);
107900220310          GIconv.isInit = *off;
108000220310          Return;
108100220310       EndIf;
108200220310
108300220310       from.CCSID = GIconv.job_ccsid;
108400220310       from.ConvAlt = 0;    // Conversion alternative
108500220310       from.SubsAlt = 0;    // Substitution alternative
108600220310       from.ShiftAlt = 1;   // Shift-state alternative
108700220310       from.InpLenOp = 0;   // Input length option
108800220310       from.ErrorOpt = 1;   // Error option for mixed data
108900220310       from.Reserved = *ALLx'00';
109000220310
109100220310       to.CCSID = GIconv.nl_ccsid;
109200220310       to.ConvAlt = 0;      // Conversion alternative
109300220310       to.SubsAlt = 0;      // Substitution alternative
109400220310       to.ShiftAlt = 1;     // Shift-state alternative
109500220310       to.InpLenOp = 0;     // Input length option
109600220310       to.ErrorOpt = 1;     // Error option for mixed data
109700220310       to.Reserved = *ALLx'00';
109800220310
109900220310       // Cache iconv() handle
110000220310       GIconv.isInit = *on;
110100220310       GIconv.hIconv = QtqIconvOpen(to: from);
110200220309
110300220310      /End-Free
110400220309
110500220310     p                 E
110600220309
110700220404      //*==========================================================================================*
110800220404      //* Get file last changed date.                                                              *
110900220404      //*==========================================================================================*
111000220404
111100220404     P getDateLastChanged...
111200220404     P                 B
111300220404     D                 PI              Z
111400220404     D i_path                              const like(path_t)
111500220404
111600220404     D lastChanged     S               Z   inz
111700220404
111800220404     D rc              S             10I 0 inz
111900220404     D bytPrv          S             10U 0 inz
112000220404     D sizeNeeded      S             10U 0 inz
112100220404     D bytRet          S             10U 0 inz
112200220404     D path            DS                  likeds(Qlg_Path_Name_t) inz
112300220404     D attrArray       DS                  likeds(Qlg_attrArray_t) inz
112400220404     D rtnAttr         DS                  likeds(Qlg_rtnAttr_t)
112500220404     D                                     based(pRtnAttr)
112600220404     D timeEpo         S             10U 0 based(pTimeEpo)
112700220404      * ---------------------------------------------------------
112800220404      /free
112900220404
113000220404         // Prepare path name
113100220404         path = createPath(i_path);
113200220404
113300220404         // Specify what attributes we want
113400220404         clear attrArray;
113500220404         attrArray.numE = 1;
113600220404         attrArray.attr(1) = QP0L_ATTR_MODIFY_TIME;
113700220404
113800220404         // Allocate buffer for result of Qp0lGetAttr
113900220404         bytPrv = 32;
114000220404         pRtnAttr = %alloc(bytPrv);
114100220404         clear rtnAttr;
114200220404
114300220404         // Retrieve file attributes
114400220404         rc = Qp0lGetAttr(path
114500220404                          : attrArray
114600220404                          : pRtnAttr
114700220404                          : bytPrv
114800220404                          : sizeNeeded
114900220404                          : bytRet
115000220404                          : 0);
115100220404
115200220404         if rc = 0;
115300220404            pTimeEpo = pRtnAttr + %size(rtnAttr);
115400220404            lastChanged = convertEpochToRpg(timeEpo);
115500220404         else;
115600220404            lastChanged = *LoVal;
115700220404         endif;
115800220404
115900220404         return lastChanged;
116000220404
116100220404      /end-free
116200220404      *
116300220404     P                 E
116400220404
116500220404      //*==========================================================================================*
116600220404      //* Initializes the path structure for the Qp0lGetAttr API.                                  *
116700220404      //*==========================================================================================*
116800220404
116900220404     P createPath...
117000220404     P                 B
117100220404     D                 PI                  like(path_t)
117200220404     D i_path                              const  likeds(Qlg_Path_Name_t)
117300220404
117400220404     D path            DS                  likeds(Qlg_Path_Name_t) inz
117500220404      /free
117600220404
117700220404         clear path;
117800220404         path.CCSID               = QLG_PATH_JOB_CCSID;     // Use current job default CCSID
117900220404         path.Country_ID          = QLG_PATH_JOB_COUNTRYID; // Use the current job country
118000220404                                                            // or region ID
118100220404         path.Language_ID         = QLG_PATH_JOB_LANGUAGEID;// Use the current job lang. ID
118200220404         path.Reserved            = *ALLx'00';
118300220404         path.Path_Type           = QLG_CHAR_SINGLE;        // 0 = Path is character String,
118400220404                                                            //     1-byte delimiter.
118500220404                                                            // 1 = Path is pointer,
118600220404                                                            //     1-byte delimiter.
118700220404                                                            // 2 = Path is character String,
118800220404                                                            //     2-byte delimiter.
118900220404                                                            // 3 = Path is pointer,
119000220404                                                            //     2-byte delimiter.
119100220404         path.Path_Length         = %len(%trimR(i_path));
119200220404         path.Path_Name_Delimiter = SLASH;
119300220404         path.Reserved2           = *ALLx'00';
119400220404         path.Path_Name           = i_path;
119500220404
119600220404         return path;
119700220404
119800220404      /end-free
119900220404     P                 E
120000220404
120100220404      //*==========================================================================================*
120200220404      //* Converts a timestamp from Epoch to RPG format.                                           *
120300220404      //*==========================================================================================*
120400220404     P convertEpochToRpg...
120500220404     P                 B
120600220404     D                 PI              Z
120700220404     D i_epochTmStmp                 10I 0 value
120800220404
120900220404     D tmStmp          S               Z   inz
121000220404
121100220404     D ftimef          S             16A   inz('%Y-%m-%d%H.%M.%S')
121200220404
121300220404     D ftimest         DS                  qualified
121400220404     D  date                         10A
121500220404     D  time                          8A
121600220404     D  null                          1A   inz(x'00')
121700220404
121800220404     D time            DS                  likeds(tm_t)  inz
121900220404      /free
122000220404
122100220404         if (localtime_r(i_epochTmStmp: time) <> *NULL);
122200220404            if (strftime(%addr(ftimest):%size(ftimest):%trim(ftimef):time) > 0);
122300220404               tmStmp=%timestamp(ftimest.date + '-' + ftimest.time + '.000000');
122400220404            endif;
122500220404         endif;
122600220404
122700220404         return tmStmp;
122800220404
122900220404      /end-free
123000220404     P                 E
123100220404
123200220309      //*==========================================================================================*
123300220310      //* Free iconv handle                                                                        *
123400220309      //*==========================================================================================*
123500220309
123600220310     p freeIConv...
123700220310     p                 B
123800220309
123900220310     d                 PI
124000220309
124100220310      /Free
124200220309
124300220310       If (not GIconv.isInit);
124400220310          Return;
124500220310       EndIf;
124600220310
124700220310       iconv_close(GIconv.hIconv);
124800220310
124900220310       Clear GIconv;
125000220310       GIconv.isInit = *off;
125100220309
125200220310      /End-Free
125300220309
125400220310     p                 E
125500220309
125600220309      //*==========================================================================================*
125700220310      //* Retrieve job ccsid                                                                       *
125800220309      //*==========================================================================================*
125900220309
126000220310     p retrieveJobCcsid...
126100220310     p                 B
126200220309
126300220310     d                 PI            10I 0
126400220309
126500220310     d qJob            DS                  likeds(qJob_t) inz
126600220310     d jobi0400        DS                  likeds(jobi0400_t) inz
126700220309
126800220310      /Free
126900220309
127000220310       Clear qJob;
127100220310       qJob.name = '*';
127200220310
127300220310       QUSRJOBI(jobi0400: %size(jobi0400): 'JOBI0400': qJob: '');
127400220310
127500220310       return jobi0400.ccsid;
127600220309
127700220310      /End-Free
127800220309
127900220310     p                 E
128000220309
128100220309      //*==========================================================================================*
128200220310      //* Converts a given string to the CCSID of the current locale                               *
128300220309      //*==========================================================================================*
128400220309
128500220310     p cvtCcsid...
128600220310     p                 B
128700220309
128800220310     d                 PI                        like(searchArgument_t.string)
128900220310     d  i_string                           value like(searchArgument_t.string)
129000220309
129100220310     d rc              S             10I 0 inz
129200220309
129300220310     d bufferSize      S             10U 0 inz
129400220310     d inBytesLeft     S             10U 0 inz
129500220310     d outBytesLeft    S             10U 0 inz
129600220310     d outBuffer       S            512A
129700220310     d outLength       S             10I 0
129800220310     d pInBuf          S               *   inz
129900220310     d pOutBuf         S               *   inz
130000220309
130100220310      /Free
130200220309
130300220310       If (not GIconv.isInit);
130400220310          Return i_string;
130500220310       EndIf;
130600220310
130700220310       bufferSize = %size(outBuffer);
130800220310       inBytesLeft = %len(%trimR(i_string));
130900220310       outBytesLeft = bufferSize;
131000220310       pInBuf = %addr(i_string: *data);
131100220310       pOutBuf= %addr(outBuffer);
131200220310       rc = iconv(giconv.hIconv: pInBuf: inBytesLeft: pOutBuf: outBytesLeft);
131300220310
131400220310       outLength = bufferSize - outBytesLeft;
131500220310
131600220310       return %subst(outBuffer: 1: outLength);
131700220309
131800220310      /End-Free
131900220309
132000220309     p                 E
132100220311
132200220311      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
132300220311      *  This tests a file mode to see if a file is a directory.
132400220311      *
132500220311      * Here is the C code we're trying to duplicate:
132600220311      *      #define _S_IFDIR    0040000                                       */
132700220311      *      #define S_ISDIR(mode) (((mode) & 0370000) == _S_IFDIR)
132800220311      *
132900220311      * 1) ((mode) & 0370000) takes the file's mode and performs a
133000220311      *      bitwise AND with the octal constant 0370000.  In binary,
133100220311      *      that constant looks like: 00000000000000011111000000000000
133200220311      *      The effect of this code is to turn off all bits in the
133300220311      *      mode, except those marked with a '1' in the binary bitmask.
133400220311      *
133500220311      * 2) ((result of #1) == _S_IFDIR)  What this does is compare
133600220311      *      the result of step 1, above with the _S_IFDIR, which
133700220311      *      is defined to be the octal constant 0040000.  In decimal,
133800220311      *      that octal constant is 16384.
133900220311      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
134000220311     p S_ISDIR         B
134100220311
134200220311     d S_ISDIR         PI             1N
134300220311     d   mode                        10U 0 value
134400220311
134500220311     d                 DS
134600220311     d  dirmode                1      4U 0
134700220311     d  byte1                  1      1A
134800220311     d  byte2                  2      2A
134900220311     d  byte3                  3      3A
135000220311     d  byte4                  4      4A
135100220311
135200220311      * Turn off bits in the mode, as in step (1) above.
135300220311     c                   eval      dirmode = mode
135400220311
135500220311     c                   bitoff    x'FF'         byte1
135600220311     c                   bitoff    x'FE'         byte2
135700220311     c                   bitoff    x'0F'         byte3
135800220311     c                   bitoff    x'FF'         byte4
135900220311
136000220311      * Compare the result to 0040000, and return true or false.
136100220311     c                   if        dirmode = 16384
136200220311     c                   return    *On
136300220311     c                   else
136400220311     c                   return    *Off
136500220311     c                   endif
136600220311
136700220311     p                 E
