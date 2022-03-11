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
002700220311      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
002800220311      //*         --PARM--  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*ALL)');                           *
002900220309      //*       >>END-COMPILE<<                                                                    *
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
005400220310      /Copy QCPYSRC,STDIO_H
005500220310
005600220310      /Copy QCPYSRC,IFSIO_H
005700220309
005800220309      // Local procedures
005900220309
006000220309     d performSearch...
006100220309     d                 PR                  Extproc('performSearch')
006200220310     d  i_HDL                        10S 0 Const                                --> Handle
006300220310     d  i_DIR                       256A   Const                                --> Directory
006400220310     d  i_STMF                      256A   Const                                --> Stream file
006500220310     d  i_SHWRCDS                      N   Const                                --> Show records
006600220310     d  i_MTCOPT                     10A   Const                                --> Match option
006700220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
006800220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
006900220309     d                                     Options(*varsize)
007000220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
007100220309
007200220309     d getFinalResult...
007300220309     d                 PR              N   Extproc('getFinalResult')
007400220309     d  i_matchOpt                   10A   Const
007500220309     d  i_numArgs                    10I 0 Const
007600220309     d  i_matchesResult...
007700220309     d                                     Const LikeDs(matchesResult_t)
007800220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
007900220309
008000220309     d initResultArray...
008100220309     d                 PR                  Extproc('initResultArray')
008200220309     d                                     LikeDs(matchesResult_t)
008300220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
008400220309     d  i_numArgs                    10I 0 Const
008500220309     d  i_searchArgs                       Const Likeds(searchArgument_t)
008600220309     d                                     Options(*varsize)
008700220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
008800220309
008900220310     d removeStreamFile...
009000220310     d                 PR                  Extproc('removeStreamFile')
009100220309     d  i_handle                     10S 0 Const
009200220310     d  i_dir                       256A   Const
009300220310     d  i_stmf                      256A   Const
009400220309
009500220310     d addStreamFile...
009600220310     d                 PR                  Extproc('addStreamFile')
009700220309     d  i_handle                     10S 0 Const
009800220310     d  i_dir                       256A   Const
009900220310     d  i_stmf                      256A   Const
010000220309     d  i_stmt                        6S 0 Const
010100220309     d  i_srcdta                    512A   Const
010200220309
010300220309     d updateStatus...
010400220309     d                 PR                  Extproc('updateStatus')
010500220309     d  i_handle                     10S 0 Const
010600220309     d  i_status                     10I 0 Const
010700220309
010800220310     d freeRegEx...
010900220310     d                 PR                  Extproc('freeRegEx')
011000220309
011100220310     d initIconv...
011200220310     d                 PR                  Extproc('initIconv')
011300220309
011400220310     d freeIconv...
011500220310     d                 PR                  Extproc('freeIconv')
011600220309
011700220310     d cvtCcsid...
011800220310     d                 PR                        like(searchArgument_t.string)
011900220309     d                                     Extproc('cvtCcsid')
012000220309     d  i_string                           value like(searchArgument_t.string)
012100220309
012200220309     d retrieveJobCcsid...
012300220309     d                 PR            10I 0 Extproc('retrieveJobCcsid')
012400220310
012500220310      // Global field declarations
012600220310
012700220310      // iconv() handle
012800220310     d GIconv          DS                  qualified
012900220310     d  isInit                         N   inz(*off)
013000220310     d  job_ccsid                          like(CCSID) inz(-1)
013100220310     d  nl_ccsid                           like(CCSID) inz(-1)
013200220310     d  hIconv                             likeds(iconv_t) inz
013300220310
013400220310      // This array has a 1:1 relation to
013500220310      // the i_ARGS input array of procedure ZFNDSTR_search().
013600220310     d GRegex          DS                  Dim(SRCHOPTS_ARGUMENTS_SIZE)
013700220310     d                                     Qualified
013800220310     d                                     Inz
013900220310     d  isInit                         N   Inz(*OFF)
014000220310     d  isError                        N   Inz(*OFF)
014100220310      /if defined(*V5R4M0)
014200220310     d  handle                             Likeds(regex_t) inz
014300220310      /else
014400220310     d  handle                             Like(regex_t) inz(*ALLx'00')
014500220310      /endif
014600220311
014700220311     d S_ISDIR         PR             1N
014800220311     d   mode                        10U 0 value
014900220310
015000220310     d matchesResult_t...
015100220310     d                 DS                  Qualified Based(pDummy)
015200220310     d  mustBeFound                    N
015300220310     d  hasBeenFound                   N
015400220310
015500220310     d g_Loop          S              1N   Inz(*On)                             Loop
015600220310     d g_UP            C                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'         Upper case
015700220310     d g_LO            C                   'abcdefghijklmnopqrstuvwxyz'         Lower case
015800220310     d NULL            C                   -1
015900220309
016000220309      //*==========================================================================================*
016100220309      //* Get Handle                                                                               *
016200220309      //*==========================================================================================*
016300220309
016400220310     p ZFNDSTR_getHandle...
016500220309     p                 B                   Export
016600220309
016700220309     d                 PI            10S 0                                      <-- Handle
016800220309
016900220309      // Locale field declarations
017000220309
017100220310     d l_HDL           S             10S 0                                      Handle
017200220309
017300220310      /Free
017400220309
017500220309       // Set handle
017600220310       l_HDL =
017700220309         NBRRNG_getNumber(
017800220309          'HANDLE' // --> Number range
017900220309         );
018000220309
018100220309       // Write file 'Status'
018200220309       Exec SQL
018300220309          INSERT
018400220309             INTO
018500220309          ZFNDSTRS
018600220309             (
018700220309              XSHDL,
018800220309              XSCNT,
018900220309              XSCNL
019000220309             )
019100220309          VALUES
019200220309             (
019300220310              :l_HDL,
019400220309              0,
019500220309              '*NO'
019600220309             );
019700220309
019800220309       // Free regular expression handles
019900220309       freeRegEx();
020000220309
020100220309       // Free iconv() handle
020200220309       freeIconv();
020300220309
020400220309       // Leave procedure
020500220310       Return l_HDL;
020600220309
020700220310      /End-Free
020800220309
020900220309     p                 E
021000220309
021100220309      //*==========================================================================================*
021200220309      //* Resolve Generic Search Elements                                                          *
021300220309      //*==========================================================================================*
021400220309
021500220310     p ZFNDSTR_resolveGenericSearchElements...
021600220309     p                 B                   Export
021700220309
021800220309     d                 PI
021900220310     d  i_HDL                        10S 0 Const                                --> Handle
022000220309
022100220309      // Locale field declarations
022200220309
022300220310     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified
022400220310
022500220310     d l_dir           s               *
022600220310     d l_stmf          s            256A
022700220311     d MyStat          S                   like(statds)
022800220309
022900220310      /Free
023000220309
023100220309       // Declare cursor
023200220309       Exec SQL
023300220309          DECLARE
023400220309             GENERIC
023500220309          CURSOR FOR
023600220309             SELECT
023700220309                *
023800220309             FROM
023900220309                ZFNDSTRI
024000220309             WHERE
024100220310                XIHDL = :i_HDL AND
024200220309                XISTMF = '*'
024300220309             FOR UPDATE;
024400220309
024500220309       // Open cursor
024600220309       Exec SQL
024700220309          OPEN
024800220309             GENERIC;
024900220309
025000220310       DoW g_Loop;
025100220309
025200220309         // Fetch from cursor
025300220309         Exec SQL
025400220309            FETCH NEXT FROM
025500220309               GENERIC
025600220309            INTO
025700220309               :ZFNDSTRIRF;
025800220309
025900220309         // Leave condition
026000220309         If SQLCOD = 100 Or
026100220309               SQLCOD < 0;
026200220309           Leave;
026300220309         EndIf;
026400220309
026500220309         // Delete generic input element
026600220309         Exec SQL
026700220309            DELETE FROM
026800220309               ZFNDSTRI
026900220309            WHERE
027000220309               CURRENT OF
027100220309                  GENERIC;
027200220310
027300220310         // Read contents of directory
027400220310         l_dir = opendir(%trim(ZFNDSTRIRF.XIDIR));
027500220311
027600220310         if l_dir <> *NULL;
027700220311
027800220310           p_dirent = readdir(l_dir);
027900220311
028000220310           dow p_dirent <> *NULL;
028100220311
028200220311             // Get the name of the stream file
028300220311             l_stmf = %subst(d_name:1:d_namelen);
028400220311
028500220311             if stat(
028600220311                  %trim(ZFNDSTRIRF.XIDIR) + '/' + %trim(l_stmf) :
028700220311                  %addr(mystat)
028800220311                ) >= 0;
028900220311
029000220311               p_statds = %addr(mystat);
029100220311
029200220311               if Not S_ISDIR(st_mode);
029300220311
029400220311                 // Write file 'Input'
029500220311                 Exec SQL
029600220311                    INSERT
029700220311                       INTO
029800220311                    ZFNDSTRI
029900220311                       (
030000220311                        XIHDL,
030100220311                        XIDIR,
030200220311                        XISTMF
030300220311                       )
030400220311                    VALUES
030500220311                       (
030600220311                        :i_HDL,
030700220311                        :ZFNDSTRIRF.XIDIR,
030800220311                        :l_stmf
030900220311                       );
031000220311
031100220311               endif;
031200220311
031300220311             endif;
031400220310
031500220310             p_dirent = readdir(l_dir);
031600220311
031700220310           enddo;
031800220311
031900220310           closedir(l_dir);
032000220311
032100220310         endif;
032200220309
032300220309       EndDo;
032400220309
032500220309       // Close cursor
032600220309       Exec SQL
032700220309          CLOSE
032800220309             GENERIC;
032900220309
033000220309       // Leave procedure
033100220309       Return;
033200220309
033300220310      /End-Free
033400220309
033500220309     p                 E
033600220309
033700220309      //*==========================================================================================*
033800220309      //* Get Number Of Search Elements                                                            *
033900220309      //*==========================================================================================*
034000220309
034100220310     p ZFNDSTR_getNumberOfSearchElements...
034200220309     p                 B                   Export
034300220309
034400220309     d                 PI             6S 0                                      <-- Number of elem.
034500220310     d  i_HDL                        10S 0 Const                                --> Handle
034600220309
034700220309      // Local field declarations
034800220309
034900220310     d l_ELEM          S              6S 0                                      Number of Elements
035000220309
035100220310      /Free
035200220309
035300220309       // Initialize 'Number of elements'
035400220310       l_ELEM = *Zero;
035500220309
035600220309       // Get number of elements
035700220309       Exec SQL
035800220309          SELECT
035900220309             COUNT(*)
036000220309          INTO
036100220310             :l_ELEM
036200220309          FROM
036300220309             ZFNDSTRI
036400220309          WHERE
036500220310             XIHDL = :i_HDL;
036600220309
036700220309       // Leave procedure
036800220310       Return l_ELEM;
036900220309
037000220310      /End-Free
037100220309
037200220309     p                 E
037300220309
037400220309      //*==========================================================================================*
037500220309      //* Search                                                                                   *
037600220309      //*==========================================================================================*
037700220309
037800220310     p ZFNDSTR_search...
037900220309     p                 B                   Export
038000220309
038100220309     d                 PI
038200220310     d  i_HDL                        10S 0 Const                                --> Handle
038300220310     d  i_SHWRCDS                      N   Const                                --> Show records
038400220310     d  i_MTCOPT                     10A   Const                                --> Match option
038500220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
038600220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
038700220309     d                                     Options(*varsize)
038800220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
038900220309
039000220309      // Record format for file ZFNDSTRI
039100220309
039200220310     d ZFNDSTRIRF    E DS                  ExtName(ZFNDSTRI) Qualified
039300220310
039400220310      // Locale field declarations
039500220309
039600220310     d l_CNT           S              6S 0                                      Counter
039700220310     d l_CNL           S             10A                                        Cancel
039800220309
039900220310      /Free
040000220309
040100220310       If (i_SIZE <= 0);
040200220310         updateStatus(i_HDL: -1);
040300220309         Return;
040400220309       EndIf;
040500220309
040600220309       // Initialize character translation
040700220309       initIconv();
040800220309
040900220309       // Initialize counter
041000220310       l_CNT = *Zero;
041100220309
041200220309       // Declare cursor
041300220309       Exec SQL
041400220309          DECLARE
041500220309             ZFNDSTRI
041600220309          CURSOR FOR
041700220309             SELECT
041800220309                *
041900220309             FROM
042000220309                ZFNDSTRI
042100220309             WHERE
042200220310                XIHDL = :i_HDL
042300220309             ORDER BY
042400220309                XIHDL,
042500220310                XIDIR,
042600220310                XISTMF
042700220309             FOR READ ONLY;
042800220309
042900220309       // Open cursor
043000220309       Exec SQL
043100220309          OPEN
043200220309             ZFNDSTRI;
043300220309
043400220310       DoW g_Loop;
043500220309
043600220309         // Fetch from cursor
043700220309         Exec SQL
043800220309            FETCH NEXT FROM
043900220309               ZFNDSTRI
044000220309            INTO
044100220309               :ZFNDSTRIRF;
044200220309
044300220309         // Leave condition
044400220309         If SQLCOD = 100 Or
044500220309               SQLCOD < 0;
044600220309           Leave;
044700220309         EndIf;
044800220309
044900220309         // Increase counter
045000220310         l_CNT = l_CNT + 1;
045100220309
045200220310         If %Rem(l_CNT : 100) = *Zero;
045300220309
045400220309           // Update file 'Status'
045500220310           updateStatus(i_HDL: l_CNT);
045600220309
045700220309           // Initialize 'Cancel'
045800220310           l_CNL = *Blanks;
045900220309
046000220309           // Check if search has to be canceled
046100220309           Exec SQL
046200220309              SELECT
046300220309                 XSCNL
046400220309              INTO
046500220310                 :l_CNL
046600220309              FROM
046700220309                 ZFNDSTRS
046800220309              WHERE
046900220310                 XSHDL = :i_HDL;
047000220309
047100220309           // Search has to be canceled
047200220310           If l_CNL = '*YES';
047300220309             Leave;
047400220309           EndIf;
047500220309
047600220309         EndIf;
047700220309
047800220310         // Perform search
047900220310         performSearch(
048000220310           ZFNDSTRIRF.XIHDL :
048100220310           ZFNDSTRIRF.XIDIR:
048200220310           ZFNDSTRIRF.XISTMF:
048300220310           i_SHWRCDS:
048400220310           i_MTCOPT:
048500220310           i_SIZE:
048600220310           i_ARGS
048700220310         );
048800220310
048900220309       EndDo;
049000220309
049100220309       // Update file 'Status'
049200220310       updateStatus(i_HDL: -1);
049300220309
049400220309       // Close cursor
049500220309       Exec SQL
049600220309          CLOSE
049700220309             ZFNDSTRI;
049800220309
049900220309       // Leave procedure
050000220309       Return;
050100220309
050200220310      /End-Free
050300220309
050400220309     p                 E
050500220309
050600220309      //*==========================================================================================*
050700220309      //* Clear                                                                                    *
050800220309      //*==========================================================================================*
050900220309
051000220310     p ZFNDSTR_clear...
051100220309     p                 B                   Export
051200220309
051300220309     d                 PI
051400220310     d  i_HDL                        10S 0 Const                                --> Handle
051500220309
051600220310      /Free
051700220309
051800220309       // Free regular expression handles
051900220309       freeRegEx();
052000220309
052100220309       // Free iconv handle
052200220309       freeIconv();
052300220309
052400220309       // Clear file 'Status'
052500220309       Exec SQL
052600220309          DELETE FROM
052700220309             ZFNDSTRS
052800220309          WHERE
052900220310             XSHDL = :i_HDL;
053000220309
053100220309       // Clear file 'Input'
053200220309       Exec SQL
053300220309          DELETE FROM
053400220309             ZFNDSTRI
053500220309          WHERE
053600220310             XIHDL = :i_HDL;
053700220309
053800220309       // Clear file 'Output'
053900220309       Exec SQL
054000220309          DELETE FROM
054100220309             ZFNDSTRO
054200220309          WHERE
054300220310             XOHDL = :i_HDL;
054400220309
054500220309       // Leave procedure
054600220309       Return;
054700220309
054800220310      /End-Free
054900220309
055000220309     p                 E
055100220309
055200220309      //*==========================================================================================*
055300220310      //* Perform search                                                                           *
055400220309      //*==========================================================================================*
055500220309
055600220309     p performSearch...
055700220309     p                 B
055800220309
055900220309     d                 PI
056000220310     d  i_HDL                        10S 0 Const                                --> Handle
056100220310     d  i_DIR                       256A   Const                                --> Directory
056200220310     d  i_STMF                      256A   Const                                --> Stream file
056300220310     d  i_SHWRCDS                      N   Const                                --> Show records
056400220310     d  i_MTCOPT                     10A   Const                                --> Match option
056500220310     d  i_SIZE                       10I 0 Const                                --> Number of args.
056600220310     d  i_ARGS                             Const Likeds(searchArgument_t)       --> String
056700220310     d                                     Options(*varsize)
056800220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
056900220309
057000220310     d l_STR           S              3S 0                                      Start
057100220310     d l_LNG           S              3S 0                                      Length
057200220310     d l_STMT          S              6S 0                                      Statement
057300220310     d l_STG           S                   Like(searchArgument_t.string)        String
057400220310     d l_IDX           S             10I 0                                      Search arg. index
057500220310     d l_RCDS          S             10I 0                                      Counter for records
057600220310     d l_RLNG          S              5S 0 Inz(512)                             Record length
057700220310     d l_LINE          S            512A                                        Line
057800220310     d srcLine         S            512A
057900220309
058000220310      // This array has a 1:1 relation to the i_ARGS input array.
058100220309     d matchesResult   DS                  LikeDs(matchesResult_t)
058200220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
058300220309     d                                     Inz
058400220309
058500220309     d rc              S             10I 0
058600220309     d cflags          S             10I 0
058700220309     d nmatch          S             10U 0 Inz(1)
058800220309     d regmatch        DS                  Likeds(regmatch_t) inz
058900220309     d error           S            256A
059000220309
059100220309     d searchArgFound  S               N
059200220309     d recordFound     S               N
059300220310     d stmfFound       S               N
059400220310
059500220310     d l_filePtr       S               *
059600220310     d l_readPtr       S               *
059700220309
059800220310      /Free
059900220309
060000220310       // Open stream file
060100220310       l_filePtr =
060200220310         fopen(
060300220310           %trim(i_DIR) + '/' + %trim(i_STMF) :
060400220310           'r, o_ccsid=0'
060500220310         );
060600220310
060700220310       // Error during open of stream file occured
060800220310       If (l_filePtr = *null);
060900220310         Return;
061000220310       EndIf;
061100220310
061200220310       // Initialize match result array
061300220310       matchesResult = initResultArray(i_SIZE: i_ARGS);
061400220310
061500220310       // Initialize statement
061600220310       l_STMT = *Zero;
061700220310
061800220310       // Initialize record counter
061900220310       l_RCDS = *Zero;
062000220310
062100220310       DoW g_Loop;
062200220310
062300220310         // Read stream file
062400220310         srcLine = *blanks;
062500220310         l_readPtr =
062600220310           fgets(
062700220310             %addr(srcLine) :
062800220310             %len(srcLine) :
062900220310             l_filePtr
063000220310           );
063100220310
063200220310         // Leave condition
063300220310         If (l_readPtr = *null);
063400220310           Leave;
063500220310         EndIf;
063600220310
063700220310         // Replace control characters by spaces
063800220310         srcLine = %xlate(x'00' : ' ' : srcLine);
063900220310         srcLine = %xlate(x'05' : ' ' : srcLine);
064000220310         srcLine = %xlate(x'0D' : ' ' : srcLine);
064100220310         srcLine = %xlate(x'25' : ' ' : srcLine);
064200220310
064300220310         // Increase statement
064400220310         l_STMT = l_STMT + 1;
064500220310
064600220310         // Check search arguments
064700220310         recordFound = *OFF;
064800220310
064900220310         For l_IDX = 1 to i_SIZE;
065000220310
065100220310           // Get start and length
065200220310           Select;
065300220310           When (i_ARGS(l_IDX).fromColumn = NULL);
065400220310             l_STR = 1;
065500220310           When (i_ARGS(l_IDX).fromColumn <= l_RLNG);
065600220310             l_STR = i_ARGS(l_IDX).fromColumn;
065700220310           Other;
065800220310             l_STR = l_RLNG;
065900220310           EndSl;
066000220310
066100220310           Select;
066200220310           When (i_ARGS(l_IDX).toColumn = NULL);
066300220310             l_LNG = l_RLNG;
066400220310           When i_ARGS(l_IDX).toColumn > l_RLNG;
066500220310             l_LNG = l_RLNG - i_ARGS(l_IDX).fromColumn + 1;
066600220310           Other;
066700220310             l_LNG = i_ARGS(l_IDX).toColumn - i_ARGS(l_IDX).fromColumn + 1;
066800220310           EndSl;
066900220310
067000220310           // Initialize line
067100220310           l_LINE = %SubSt(srcLine : l_STR : l_LNG);
067200220310
067300220310           // Initialize string
067400220310           l_STG = i_ARGS(l_IDX).string;
067500220310
067600220310           // Initialize regular expression search
067700220310           cflags = REG_EXTENDED;
067800220310
067900220310           // Ignore upper/lower case
068000220310           If i_ARGS(l_IDX).case = SRCHOPTS_CASE_IGNORE;
068100220310             If i_ARGS(l_IDX).regex <> SRCHOPTS_ARG_REGEX;
068200220310               l_STG = %XLate(g_LO:g_UP:l_STG);
068300220310               l_LINE = %XLate(g_LO:g_UP:l_LINE);
068400220310             Else;
068500220310               cflags = cflags + REG_ICASE;
068600220310             Endif;
068700220310           EndIf;
068800220310
068900220310           // Initialize search result.
069000220310           searchArgFound = *OFF;
069100220310
069200220310           // Match!
069300220310           If i_ARGS(l_IDX).regex = SRCHOPTS_ARG_REGEX;
069400220310             // Perform regular expression search:
069500220310             // Compile regular expression
069600220310             If not GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
069700220310               l_STG = cvtCcsid(l_STG);
069800220310               rc = regcomp(GRegex(l_IDX).handle: l_STG: cflags);
069900220310               If rc = 0;
070000220310                 GRegex(l_IDX).isInit = *ON;
070100220310               Else;
070200220310                 GRegex(l_IDX).isInit = *OFF;
070300220310                 GRegex(l_IDX).isError = *ON;
070400220310                 regerror(
070500220310                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
070600220310               Endif;
070700220310             Endif;
070800220310
070900220310             // Match regular expression
071000220310             If GRegex(l_IDX).isInit and not GRegex(l_IDX).isError;
071100220310               l_LINE = cvtCcsid(l_LINE);
071200220310               rc = regexec(
071300220310                      GRegex(l_IDX).handle: l_LINE: nmatch: regmatch: 0);
071400220310               Select;
071500220310               When rc = 0;             // found!
071600220310                 searchArgFound = *ON;
071700220310               When rc = REG_NOMATCH;   // not found
071800220310                 searchArgFound = *OFF;
071900220310               Other;                   // error!
072000220310                 GRegex(l_IDX).isError = *ON;
072100220310                 regerror(
072200220310                   rc: GRegex(l_IDX).handle: %addr(error): %size(error));
072300220310               Endsl;
072400220310             Endif;
072500220310           Else;
072600220310             // Perform standard string search:
072700220310             searchArgFound = %Scan(%TrimR(l_STG) : l_LINE) > *Zero;
072800220310           Endif;
072900220310
073000220310           If searchArgFound;
073100220310             matchesResult(l_IDX).hasBeenFound = *ON;
073200220310             If (matchesResult(l_IDX).mustBeFound);
073300220310               // Set 'recordFond' for match 'all/any' mode
073400220310               recordFound = *ON;
073500220310             Else;
073600220310               // For "contains not" criterias,
073700220310               // records are not logged.
073800220310             EndIf;
073900220310           EndIf;
074000220310
074100220310         EndFor;
074200220310
074300220310         If (i_MTCOPT = SRCHOPTS_MATCH_LINE);
074400220310           // Set 'recordFond' for match 'line mode'
074500220310           recordFound = getFinalResult(
074600220310                           SRCHOPTS_MATCH_ALL: i_SIZE: matchesResult);
074700220310           // Reset match result array
074800220310           matchesResult = initResultArray(i_SIZE: i_ARGS);
074900220310         EndIf;
075000220310
075100220310         // Check record found
075200220310         // (record matches at least 1 criterum)
075300220310         If (not recordFound);
075400220310           Iter;
075500220310         EndIf;
075600220310
075700220310         // Count output records
075800220310         l_RCDS += 1;
075900220310
076000220310         // Check whether or not all records
076100220310         // must be added to the output file
076200220310         If (not i_SHWRCDS and l_RCDS > 1);
076300220310           Iter;
076400220310         EndIf;
076500220310
076600220310         addStreamFile(i_HDL: i_DIR: i_STMF: l_STMT: srcLine);
076700220310
076800220310       EndDo;
076900220310
077000220310       // Close stream file
077100220310       fclose(l_filePtr);
077200220310
077300220310       If (i_MTCOPT <> SRCHOPTS_MATCH_LINE);
077400220310
077500220310         // Evaluate final result
077600220310         stmfFound = getFinalResult(i_MTCOPT: i_SIZE: matchesResult);
077700220310
077800220310         // Set final result
077900220310         If (l_RCDS > 0);
078000220310           If (not stmfFound);
078100220310             // Remove statement records in case
078200220310             // of a "must not contain" condition has not been met.
078300220310             removeStreamFile(i_HDL: i_DIR: i_STMF);
078400220310           EndIf;
078500220310         Else;
078600220310           If (stmfFound);
078700220310             // Ensure at least a "stream file record" in case
078800220310             // we only had "must not contain" condition.
078900220310             addStreamFile(i_HDL: i_DIR: i_STMF: 0
079000220310                           : %trim(i_DIR) + '/' + %trim(i_STMF));
079100220310           EndIf;
079200220310         EndIf;
079300220310       EndIf;
079400220309
079500220310      /End-Free
079600220310
079700220309     p                 E
079800220309
079900220309      //*==========================================================================================*
080000220310      //* Initialize result array                                                                  *
080100220309      //*==========================================================================================*
080200220309
080300220309     p initResultArray...
080400220309     p                 B
080500220309
080600220309     d                 PI                  LikeDs(matchesResult_t)
080700220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
080800220309     d  i_numArgs                    10I 0 Const
080900220309     d  i_searchArgs                       Const Likeds(searchArgument_t)
081000220309     d                                     Options(*varsize)
081100220309     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
081200220309
081300220309     d i               S             10i 0
081400220309     d matchesResult   DS                  LikeDs(matchesResult_t)
081500220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
081600220309
081700220310      /Free
081800220309
081900220310       Clear matchesResult;
082000220310
082100220310       For i = 1 to i_numArgs;
082200220310         Select;
082300220310         When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS);
082400220310           matchesResult(i).mustBeFound = *ON;
082500220310         When (i_searchArgs(i).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT);
082600220310           matchesResult(i).mustBeFound = *OFF;
082700220310         EndSl;
082800220310         matchesResult(i).hasBeenFound = *OFF;
082900220310       EndFor;
083000220310
083100220310       Return matchesResult;
083200220309
083300220310      /End-Free
083400220310
083500220309     p                 E
083600220309
083700220309      //*==========================================================================================*
083800220310      //* Computes the final result                                                                *
083900220309      //*==========================================================================================*
084000220309
084100220309     p getFinalResult...
084200220309     p                 B
084300220309
084400220309     d                 PI              N
084500220309     d  i_matchOpt                   10A   Const
084600220309     d  i_numArgs                    10I 0 Const
084700220309     d  i_matchesResult...
084800220309     d                                     Const LikeDs(matchesResult_t)
084900220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
085000220309
085100220309     d i               S             10I 0
085200220309     d result          S               N
085300220309
085400220310      /Free
085500220309
085600220310       Select;
085700220310       When (i_matchOpt = SRCHOPTS_MATCH_ALL);
085800220310         result = *ON;
085900220310         For i = 1 to i_numArgs;
086000220310           If ((i_matchesResult(i).mustBeFound and
086100220310                i_matchesResult(i).hasBeenFound) or
086200220310               (not i_matchesResult(i).mustBeFound and
086300220310                not i_matchesResult(i).hasBeenFound));
086400220310             // No further action required
086500220310           Else;
086600220310             result = *OFF;
086700220310             Leave;
086800220310           EndIf;
086900220310         EndFor;
087000220310       When (i_matchOpt = SRCHOPTS_MATCH_ANY);
087100220310         result = *OFF;
087200220310         For i = 1 to i_numArgs;
087300220310           If ((i_matchesResult(i).mustBeFound and
087400220310                i_matchesResult(i).hasBeenFound) or
087500220310               (not i_matchesResult(i).mustBeFound and
087600220310                not i_matchesResult(i).hasBeenFound));
087700220310             result = *ON;
087800220310             Leave;
087900220310           EndIf;
088000220310         EndFor;
088100220310       EndSl;
088200220310
088300220310       Return result;
088400220309
088500220310      /End-Free
088600220310
088700220309     p                 E
088800220309
088900220309      //*==========================================================================================*
089000220310      //* Removes a stream file from the output file                                               *
089100220309      //*==========================================================================================*
089200220309
089300220310     p removeStreamFile...
089400220309     p                 B
089500220309
089600220309     d                 PI
089700220309     d  i_handle                     10S 0 Const
089800220310     d  i_dir                       256A   Const
089900220310     d  i_stmf                      256A   Const
090000220309
090100220310      /Free
090200220309
090300220309       // Clear file 'Output'
090400220309       Exec SQL
090500220309          DELETE FROM
090600220309             ZFNDSTRO
090700220309          WHERE
090800220309             XOHDL = :i_handle and
090900220310             XODIR = :i_dir and
091000220310             XOSTMF = :i_stmf;
091100220309
091200220310      /End-Free
091300220310
091400220309     p                 E
091500220309
091600220309      //*==========================================================================================*
091700220310      //* Adds a stream file to the output file                                                    *
091800220309      //*==========================================================================================*
091900220309
092000220310     p addStreamFile...
092100220309     p                 B
092200220309
092300220309     d                 PI
092400220309     d  i_handle                     10S 0 Const
092500220310     d  i_dir                       256A   Const
092600220310     d  i_stmf                      256A   Const
092700220309     d  i_stmt                        6S 0 Const
092800220309     d  i_srcdta                    512A   Const
092900220309
093000220310     d stmfAttrs       DS                  Qualified
093100220310     d  srcType                      10A
093200220310     d  lastChgData                    Z
093300220310
093400220310     d l_pos           S              5S 0
093500220309
093600220310      /Free
093700220309
093800220310       // Retrieve stream file attributes
093900220310       l_pos = %Scan('.' : i_stmf);
094000220310       If l_pos = *Zero;
094100220310         stmfAttrs.srcType = *blanks;
094200220310       Else;
094300220310         stmfAttrs.srcType = %SubSt(i_stmf : l_pos + 1);
094400220311         stmfAttrs.srcType = %XLate(g_LO:g_UP: stmfAttrs.srcType);
094500220310       EndIf;
094600220310       // TODO: Get the last change date of the stream file
094700220310       stmfAttrs.lastChgData = *LoVal;
094800220310
094900220310       // Write file 'Output'
095000220310       Exec SQL
095100220310          INSERT
095200220310             INTO
095300220310          ZFNDSTRO
095400220310             (
095500220310              XOHDL,
095600220310              XODIR,
095700220310              XOSTMF,
095800220310              XOTYPE,
095900220310              XOSTMT,
096000220310              XOLINE,
096100220310              XOFLCD
096200220310             )
096300220310          VALUES
096400220310             (
096500220310              :i_handle,
096600220310              :i_dir,
096700220310              :i_stmf,
096800220310              :stmfAttrs.srcType,
096900220310              :i_stmt,
097000220310              :i_srcdta,
097100220310              :stmfAttrs.lastChgData
097200220310             );
097300220309
097400220310      /End-Free
097500220310
097600220309     p                 E
097700220309
097800220309      //*==========================================================================================*
097900220310      //* Updates the status field of the result table                                             *
098000220309      //*==========================================================================================*
098100220309
098200220309     p updateStatus...
098300220309     p                 B
098400220309
098500220309     d                 PI
098600220309     d  i_handle                     10S 0 Const
098700220309     d  i_status                     10I 0 Const
098800220309
098900220310      /Free
099000220309
099100220310       // Update file 'Status'
099200220310       Exec SQL
099300220310          UPDATE
099400220310             ZFNDSTRS
099500220310          SET
099600220310             XSCNT = :i_status
099700220310          WHERE
099800220310             XSHDL = :i_handle;
099900220309
100000220310      /End-Free
100100220309
100200220309     p                 E
100300220309
100400220309      //*==========================================================================================*
100500220310      //* Free regular expression handles                                                          *
100600220309      //*==========================================================================================*
100700220309
100800220310     p freeRegEx...
100900220310     p                 B
101000220309
101100220310     d                 PI
101200220309
101300220310     d l_IDX           S             10I 0 inz
101400220309
101500220310      /Free
101600220309
101700220310       For l_IDX = 1 to %elem(GRegex);
101800220310          If (GRegex(l_IDX).isInit);
101900220310             regfree(GRegex(l_IDX).handle);
102000220310             GRegex(l_IDX).isInit = *OFF;
102100220310             GRegex(l_IDX).isError = *OFF;
102200220310             GRegex(l_IDX).handle = *ALLx'00';
102300220310          EndIf;
102400220310       EndFor;
102500220309
102600220310      /End-Free
102700220309
102800220310     p                 E
102900220309
103000220309      //*==========================================================================================*
103100220310      //* Initialize character translation                                                         *
103200220309      //*==========================================================================================*
103300220309
103400220310     p initIconv...
103500220310     p                 B
103600220309
103700220310     d                 PI
103800220309
103900220310     d from            DS                  likeds(QtqCode_t)
104000220310     d to              DS                  likeds(QtqCode_t)
104100220310     d rc              S             10I 0 inz
104200220309
104300220310     d DIGITS          C                   const(%len(GIconv.nl_ccsid))
104400220310     d DECPOS          C                   const(%decpos(GIconv.nl_ccsid))
104500220310
104600220310      /Free
104700220309
104800220310       // Free iconv() handle
104900220310       freeIconv(); // Just in case ...
105000220310
105100220310       GIconv.job_ccsid = retrieveJobCcsid();
105200220310       GIconv.nl_ccsid = %dec(%str(nl_langinfo(CODESET)): DIGITS: DECPOS);
105300220310
105400220310       If (GIconv.job_ccsid = GIconv.nl_ccsid);
105500220310          GIconv.isInit = *off;
105600220310          Return;
105700220310       EndIf;
105800220310
105900220310       from.CCSID = GIconv.job_ccsid;
106000220310       from.ConvAlt = 0;    // Conversion alternative
106100220310       from.SubsAlt = 0;    // Substitution alternative
106200220310       from.ShiftAlt = 1;   // Shift-state alternative
106300220310       from.InpLenOp = 0;   // Input length option
106400220310       from.ErrorOpt = 1;   // Error option for mixed data
106500220310       from.Reserved = *ALLx'00';
106600220310
106700220310       to.CCSID = GIconv.nl_ccsid;
106800220310       to.ConvAlt = 0;      // Conversion alternative
106900220310       to.SubsAlt = 0;      // Substitution alternative
107000220310       to.ShiftAlt = 1;     // Shift-state alternative
107100220310       to.InpLenOp = 0;     // Input length option
107200220310       to.ErrorOpt = 1;     // Error option for mixed data
107300220310       to.Reserved = *ALLx'00';
107400220310
107500220310       // Cache iconv() handle
107600220310       GIconv.isInit = *on;
107700220310       GIconv.hIconv = QtqIconvOpen(to: from);
107800220309
107900220310      /End-Free
108000220309
108100220310     p                 E
108200220309
108300220309      //*==========================================================================================*
108400220310      //* Free iconv handle                                                                        *
108500220309      //*==========================================================================================*
108600220309
108700220310     p freeIConv...
108800220310     p                 B
108900220309
109000220310     d                 PI
109100220309
109200220310      /Free
109300220309
109400220310       If (not GIconv.isInit);
109500220310          Return;
109600220310       EndIf;
109700220310
109800220310       iconv_close(GIconv.hIconv);
109900220310
110000220310       Clear GIconv;
110100220310       GIconv.isInit = *off;
110200220309
110300220310      /End-Free
110400220309
110500220310     p                 E
110600220309
110700220309      //*==========================================================================================*
110800220310      //* Retrieve job ccsid                                                                       *
110900220309      //*==========================================================================================*
111000220309
111100220310     p retrieveJobCcsid...
111200220310     p                 B
111300220309
111400220310     d                 PI            10I 0
111500220309
111600220310     d qJob            DS                  likeds(qJob_t) inz
111700220310     d jobi0400        DS                  likeds(jobi0400_t) inz
111800220309
111900220310      /Free
112000220309
112100220310       Clear qJob;
112200220310       qJob.name = '*';
112300220310
112400220310       QUSRJOBI(jobi0400: %size(jobi0400): 'JOBI0400': qJob: '');
112500220310
112600220310       return jobi0400.ccsid;
112700220309
112800220310      /End-Free
112900220309
113000220310     p                 E
113100220309
113200220309      //*==========================================================================================*
113300220310      //* Converts a given string to the CCSID of the current locale                               *
113400220309      //*==========================================================================================*
113500220309
113600220310     p cvtCcsid...
113700220310     p                 B
113800220309
113900220310     d                 PI                        like(searchArgument_t.string)
114000220310     d  i_string                           value like(searchArgument_t.string)
114100220309
114200220310     d rc              S             10I 0 inz
114300220309
114400220310     d bufferSize      S             10U 0 inz
114500220310     d inBytesLeft     S             10U 0 inz
114600220310     d outBytesLeft    S             10U 0 inz
114700220310     d outBuffer       S            512A
114800220310     d outLength       S             10I 0
114900220310     d pInBuf          S               *   inz
115000220310     d pOutBuf         S               *   inz
115100220309
115200220310      /Free
115300220309
115400220310       If (not GIconv.isInit);
115500220310          Return i_string;
115600220310       EndIf;
115700220310
115800220310       bufferSize = %size(outBuffer);
115900220310       inBytesLeft = %len(%trimR(i_string));
116000220310       outBytesLeft = bufferSize;
116100220310       pInBuf = %addr(i_string: *data);
116200220310       pOutBuf= %addr(outBuffer);
116300220310       rc = iconv(giconv.hIconv: pInBuf: inBytesLeft: pOutBuf: outBytesLeft);
116400220310
116500220310       outLength = bufferSize - outBytesLeft;
116600220310
116700220310       return %subst(outBuffer: 1: outLength);
116800220309
116900220310      /End-Free
117000220309
117100220309     p                 E
117200220311
117300220311      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
117400220311      *  This tests a file mode to see if a file is a directory.
117500220311      *
117600220311      * Here is the C code we're trying to duplicate:
117700220311      *      #define _S_IFDIR    0040000                                       */
117800220311      *      #define S_ISDIR(mode) (((mode) & 0370000) == _S_IFDIR)
117900220311      *
118000220311      * 1) ((mode) & 0370000) takes the file's mode and performs a
118100220311      *      bitwise AND with the octal constant 0370000.  In binary,
118200220311      *      that constant looks like: 00000000000000011111000000000000
118300220311      *      The effect of this code is to turn off all bits in the
118400220311      *      mode, except those marked with a '1' in the binary bitmask.
118500220311      *
118600220311      * 2) ((result of #1) == _S_IFDIR)  What this does is compare
118700220311      *      the result of step 1, above with the _S_IFDIR, which
118800220311      *      is defined to be the octal constant 0040000.  In decimal,
118900220311      *      that octal constant is 16384.
119000220311      *+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
119100220311     p S_ISDIR         B
119200220311
119300220311     d S_ISDIR         PI             1N
119400220311     d   mode                        10U 0 value
119500220311
119600220311     d                 DS
119700220311     d  dirmode                1      4U 0
119800220311     d  byte1                  1      1A
119900220311     d  byte2                  2      2A
120000220311     d  byte3                  3      3A
120100220311     d  byte4                  4      4A
120200220311
120300220311      * Turn off bits in the mode, as in step (1) above.
120400220311     c                   eval      dirmode = mode
120500220311
120600220311     c                   bitoff    x'FF'         byte1
120700220311     c                   bitoff    x'FE'         byte2
120800220311     c                   bitoff    x'0F'         byte3
120900220311     c                   bitoff    x'FF'         byte4
121000220311
121100220311      * Compare the result to 0040000, and return true or false.
121200220311     c                   if        dirmode = 16384
121300220311     c                   return    *On
121400220311     c                   else
121500220311     c                   return    *Off
121600220311     c                   endif
121700220311
121800220311     p                 E
