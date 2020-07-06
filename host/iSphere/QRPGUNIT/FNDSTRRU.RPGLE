      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2020 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* This service program is a iRPGUnit test suite for testing the 'iSphere Source File       *
      //* Search' feature. It call the procedures of the FNDSTR service program via the            *
      //* W001* wrapper programs.                                                                  *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*    >>PRE-COMPILER<<
      //*      >>CRTCMD<<  RUCRTTST    TSTPGM(&LI/&OB) +
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);
      //*      >>IMPORTANT<<
      //*        >>PARM<< COPTION(*SRCSTMT *EVENTF);
      //*        >>PARM<< DBGVIEW(*LIST);
      //*      >>END-IMPORTANT<<
      //*      >>EXECUTE<<
      //*    >>END-PRE-COMPILER<<
      //*------------------------------------------------------------------------------------------*

     h NoMain Option(*SrcStmt)

     fFNDSTRI   O    E             Disk    Rename(FNDSTRI : FNDSTRIRF) UsrOpn
     fFNDSTRO01 IF   E           K Disk    Rename(FNDSTRO : FNDSTRORF) UsrOpn

     d setUp...
     d                 pr                  extproc('setUp')

     d tearDown...
     d                 pr                  extproc('tearDown')

     d testSimple...
     d                 pr                  extproc('testSimple')

     d testSimpleCaseMatch...
     d                 pr                  extproc('testSimpleCaseMatch')

     d testSimpleNegative...
     d                 pr                  extproc('testSimpleNegative')

     d testSimpleRegex...
     d                 pr                  extproc('testSimpleRegex')

     d testSimpleCaseMatchRegex...
     d                 pr                  extproc('testSimpleCaseMatchRegex')

     d testCombined2Any...
     d                 pr                  extproc('testCombined2Any')

     d testCombined2All...
     d                 pr                  extproc('testCombined2All')

     d testCombined2Exclude...
     d                 pr                  extproc('testCombined2Exclude')

     d testLineMode2Exclude...
     d                 pr                  extproc('testLineMode2Exclude')

     d WRAPPER_getHandle...
     d                 pr            10S 0 extproc('WRAPPER_getHandle')

     d WRAPPER_search...
     d                 pr                  extproc('WRAPPER_search')
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchAll                      10A   Const                                --> Match option
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     d WRAPPER_clear...
     d                 pr                  extproc('WRAPPER_clear')
     d  i_handle                     10S 0 Value

     d prepareMemberList...
     d                 pr                  extproc('prepareMemberList')
     d LPHDL                         10S 0 Const                                --> Handle

     d removeFileOpenMessage...
     d                 PR                  Extproc('removeFileOpenMessage')
     d  i_msgId                       7A   Const

      /include qinclude,TESTCASE
      /include qcpysrc,FNDSTR

       //----------------------------------------------------------------------
       //   Setup/Tear Down
       //----------------------------------------------------------------------

     p setUp...
     p                 b                   export
     d                 pi
      /free

       if (not %open(FNDSTRI));
         open FNDSTRI;
         removeFileOpenMessage('CPF4028');
       endif;

       if (not %open(FNDSTRO01));
         open FNDSTRO01;
       endif;

      /end-free
     p                 e

     p tearDown...
     p                 b                   export
     d                 pi
      /free

       if (%open(FNDSTRI));
         close FNDSTRI;
       endif;

       if (%open(FNDSTRO01));
         close FNDSTRO01;
       endif;

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test simple string search.
       //----------------------------------------------------------------------
     p testSimple...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'demo';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        1 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('ISPHEREDVP': XOLIB: 'XOLIB');
           aEqual('QRPGLESRC': XOFILE: 'XOFILE');

           select;
           when XOMBR = 'DEMO01';
             iEqual(11: XOSTMT: 'XOSTMT');

           when XOMBR = 'DEMO02';
             iEqual(11: XOSTMT: 'XOSTMT');

           other;
             aEqual('DEMO': %subst(XOMBR: 1: 4): 'XOMBR');
           endsl;

         endif;
       enddo;

       iEqual(34: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test simple case-sensitive string search.
       //----------------------------------------------------------------------
     p testSimpleCaseMatch...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'DEMO5';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_MATCH;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        1 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('ISPHEREDVP': XOLIB: 'XOLIB');
           aEqual('QRPGLESRC': XOFILE: 'XOFILE');

           select;
           when XOMBR = 'DEMO05';
             iEqual(42: XOSTMT: 'XOSTMT');

           when XOMBR = 'DEMO07';
             iEqual(43: XOSTMT: 'XOSTMT');

           when XOMBR = 'DEMO08';
             iEqual(44: XOSTMT: 'XOSTMT');

           when XOMBR = 'DEMO09';
             iEqual(48: XOSTMT: 'XOSTMT');

           other;
             aEqual('DEMO': %subst(XOMBR: 1: 4): 'XOMBR');
           endsl;

         endif;
       enddo;

       iEqual(4: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test simple 'contains not' string search.
       //----------------------------------------------------------------------
     p testSimpleNegative...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT;
       searchArguments(1).string = 'DEMO5';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_MATCH;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        1 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('ISPHEREDVP': XOLIB: 'XOLIB');
           aEqual('QRPGLESRC': XOFILE: 'XOFILE');

           assert(%subst(XOMBR: 1: 4) = 'DEMO'
                  : 'Member does not start with DEMO*');

           assert(XOMBR <> 'DEMO5': 'Invalid member: DEMO5');
           assert(XOMBR <> 'DEMO7': 'Invalid member: DEMO7');
           assert(XOMBR <> 'DEMO8': 'Invalid member: DEMO8');
           assert(XOMBR <> 'DEMO9': 'Invalid member: DEMO9');

         endif;
       enddo;

       iEqual(5: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test simple regular expression search.
       //----------------------------------------------------------------------
     p testSimpleRegex...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = '(BUFF|alc)Len=';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_REGEX;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        1 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('ISPHEREDVP': XOLIB: 'XOLIB');
           aEqual('QRPGLESRC': XOFILE: 'XOFILE');

           assert(%subst(XOMBR: 1: 4) = 'DEMO'
                  : 'Member does not start with DEMO*');

           aEqual(XOMBR: 'DEMO7': 'DEMO7');

         endif;
       enddo;

       iEqual(2: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test simple case-sensitive regular expression search.
       //----------------------------------------------------------------------
     p testSimpleCaseMatchRegex...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = '(BUFF|alc)Len=';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_MATCH;
       searchArguments(1).regex = SRCHOPTS_ARG_REGEX;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        1 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('ISPHEREDVP': XOLIB: 'XOLIB');
           aEqual('QRPGLESRC': XOFILE: 'XOFILE');

           assert(%subst(XOMBR: 1: 4) = 'DEMO'
                  : 'Member does not start with DEMO*');

           aEqual(XOMBR: 'DEMO7': 'DEMO7');

         endif;
       enddo;

       iEqual(1: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test 'match any' search string search with two arguments.
       //----------------------------------------------------------------------
     p testCombined2Any...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'qcpysrc,iqdbrtvfd';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       searchArguments(2).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(2).string = 'qcpysrc,iqsdrtvmv';
       searchArguments(2).fromColumn = 1;
       searchArguments(2).toColumn = 228;
       searchArguments(2).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(2).regex = SRCHOPTS_ARG_STRING;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ANY : // --> Match any search arguments
        2 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           assert(XOMBR = 'DEMO7' or XOMBR = 'DEMO8' or XOMBR = 'DEMO9'
                  : 'Unexpected member: ' + %trim(XOMBR));

           assert(%subst(XOMBR: 1: 4) = 'DEMO'
                  : 'Member does not start with DEMO*');

         endif;
       enddo;

       iEqual(3: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test 'match all' search string search with two arguments.
       //----------------------------------------------------------------------
     p testCombined2All...
     p                 b                   export
     d                 pi

     dGIHDL            s             10S 0                                      Handle
     dsearchArguments  ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     dcount            s             10i 0
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'qcpysrc,iqsdrtvmv';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       searchArguments(2).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(2).string = 'qcpysrc,iqsdrtvvt';
       searchArguments(2).fromColumn = 1;
       searchArguments(2).toColumn = 228;
       searchArguments(2).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(2).regex = SRCHOPTS_ARG_STRING;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        2 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           assert(XOMBR = 'DEMO9'
                  : 'Unexpected member: ' + %trim(XOMBR));


           assert(XOSTMT = 35 or XOSTMT = 37
                  : 'Unexpected statement: ' + %char(XOSTMT));

           assert(%subst(XOMBR: 1: 4) = 'DEMO'
                  : 'Member does not start with DEMO*');

         endif;
       enddo;

       iEqual(2: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test 'match all' search string search with two arguments.
       //   First argument must be found.
       //   Second argument (regex) must not be found.
       //----------------------------------------------------------------------
     p testCombined2Exclude...
     p                 b                   export
     d                 pi

     d GIHDL           s             10S 0                                      Handle
     d searchArguments...
     d                 ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     d count           s             10i 0

     d FROM            c                   1
     d TO              c                   40
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'DEMO4P';
       searchArguments(1).fromColumn = FROM;             // From/to values must must be the
       searchArguments(1).toColumn = TO;                 // same for all search arguments.
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       searchArguments(2).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT;
       searchArguments(2).string = 'open|close';
       searchArguments(2).fromColumn = FROM;
       searchArguments(2).toColumn = TO;
       searchArguments(2).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(2).regex = SRCHOPTS_ARG_REGEX;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_ALL : // --> Match all search arguments
        2 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

         endif;
       enddo;

       iEqual(0: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Test 'line mode'. Search all lines that conain the first
       //   search argument, but that do not contain the second
       //   regular expression.
       //----------------------------------------------------------------------
     p testLineMode2Exclude...
     p                 b                   export
     d                 pi

     d GIHDL           s             10S 0                                      Handle
     d searchArguments...
     d                 ds                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
     d count           s             10i 0

     d FROM            c                   1
     d TO              c                   40
      /free

       // Get handle
       GIHDL = WRAPPER_getHandle();

       // Set search elements
       prepareMemberList(GIHDL);

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'DEMO4P';
       searchArguments(1).fromColumn = FROM;             // From/to values must must be the
       searchArguments(1).toColumn = TO;                 // same for all search arguments.
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       searchArguments(2).operator = SRCHOPTS_OPERATOR_CONTAINS_NOT;
       searchArguments(2).string = 'open|close';
       searchArguments(2).fromColumn = FROM;
       searchArguments(2).toColumn = TO;
       searchArguments(2).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(2).regex = SRCHOPTS_ARG_REGEX;

       WRAPPER_search(
        GIHDL :              // --> Handle
        *ON :                // --> Show all records
        SRCHOPTS_MATCH_LINE: // --> Match all search arguments
        2 :                  // --> Number of search arguments
        searchArguments      // --> search arguments
       );

       // Check search result

       count = 0;

       setll GIHDL FNDSTRORF;
       dow (%found(FNDSTRO01) and not %eof(FNDSTRO01));
         reade GIHDL FNDSTRORF;
         if (not %eof(FNDSTRO01));

           count += 1;

           aEqual('DEMO4': XOMBR: 'XOMBR');
           assert(XOSTMT = 37 or XOSTMT = 84
                  : 'Unexpected statement #' + %char(XOSTMT));

         endif;
       enddo;

       iEqual(2: count: 'count');

       on-exit;

       // Clear files
       WRAPPER_clear(
        GIHDL // --> Handle
       );

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   iSphere wrapper of FNDSTR_getHandle().
       //----------------------------------------------------------------------
     p WRAPPER_getHandle...
     p                 b
     d                 pi            10S 0

     d handle          s             10S 0

     dW001001          PR                  extpgm('W001001')
     d                               10S 0                                      <-- Handle
      /free

       W001001(handle);

       return handle;

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   iSphere wrapper of FNDSTR_search().
       //----------------------------------------------------------------------
     p WRAPPER_search...
     p                 b
     d                 pi
     d i_handle                      10S 0 Const                                --> Handle
     d i_showRecords                   N   Const                                --> Show records
     d i_matchAll                    10A   Const                                --> Match option
     d i_numArguments                10I 0 Const                                --> Number of args.
     d i_arguments                         Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     dW001002          PR                  extpgm('W001002')
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchAll                      10A   Const                                --> Match option
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
      /free

       W001002(i_handle: i_showRecords: i_matchAll: i_numArguments:i_arguments);

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   iSphere wrapper of FNDSTR_clear().
       //----------------------------------------------------------------------
     p WRAPPER_clear...
     p                 b
     d                 pi
     d  i_handle                     10S 0 Value

     dW001003          PR                  extpgm('W001003')
     d                               10S 0 Const                                --> Handle
      /free

       W001003(i_handle);

      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Prepare list of search members.
       //----------------------------------------------------------------------
     p prepareMemberList...
     p                 b
     d                 pi
     d LPHDL                         10S 0 Const                                --> Handle
      /free


       // Set search elements
       Clear FNDSTRIRF;
       XIHDL = LPHDL;
       XILIB = 'ISPHEREDVP';
       XIFILE = 'QRPGLESRC';

       XIMBR = 'DEMO1';
       Write FNDSTRIRF;

       XIMBR = 'DEMO2';
       Write FNDSTRIRF;

       XIMBR = 'DEMO3';
       Write FNDSTRIRF;

       XIMBR = 'DEMO4';
       Write FNDSTRIRF;

       XIMBR = 'DEMO5';
       Write FNDSTRIRF;

       XIMBR = 'DEMO6';
       Write FNDSTRIRF;

       XIMBR = 'DEMO7';
       Write FNDSTRIRF;

       XIMBR = 'DEMO8';
       Write FNDSTRIRF;

       XIMBR = 'DEMO9';
       Write FNDSTRIRF;


      /end-free
     p                 e

       //----------------------------------------------------------------------
       //   Removes 'Buffer length longer than record for member DEMO5.'
       //   (CPF4011) message.
       //----------------------------------------------------------------------

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
