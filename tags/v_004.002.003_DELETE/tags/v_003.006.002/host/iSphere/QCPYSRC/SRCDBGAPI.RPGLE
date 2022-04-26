      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /if not defined(SRCDBGAPI)
      /define SRCDBGAPI

      /copy qcpysrc,types

       // Receiver variable, used by various APIs
     d rcvVar_t        s          65535a   based(pTemplate)

       // Source Debug Handler Exit Program
     d DebugSessionHandler...
     d                 pr
     d  i_reason                     10a   const
     d  i_pgmList                          const  likeds(sdh_pgmList_t)
     d  i_pgmListNumE                10i 0 const

     d sdh_pgmList_t...
     d                 ds                  qualified based(pTemplate)
     d  pgm                                likeds(sdh_pgmListE_t) dim(50)

     d sdh_pgmListE_t...
     d                 ds                  qualified based(pTemplate)
     d  qPgm                               likeds(qObj_t)
     d  type                         10a

     d REASON_START    c                   '*START'
     d REASON_STARTJAVA...
     d                 c                   '*STARTJAVA'
     d REASON_STOP     c                   '*STOP'
     d REASON_DISPLAY  c                   '*DISPLAY'
     d REASON_RLSJOB   c                   '*RLSJOB'

       // Program-Stop Handler Exit Program
     d ProgramStopHandler...
     d                 pr
     d  i_qPgm                             const  likeds(qObj_t)
     d  i_pgmType                    10a   const
     d  i_module                     10a   const
     d  i_stopReason                       const  likeds(psh_stopReason_t)
     d  i_rcvVar                  32767a   const  options(*varsize)
     d  i_numE                       10i 0 const
     d  i_msgData                 32767a   const  options(*varsize)

     d psh_stopReason_t...
     d                 ds            10    qualified based(pTemplate)
     d  exception...
     d                                1a
     d  break...
     d                                1a
     d  step...
     d                                1a
     d  breakFailure...
     d                                1a
     d  watch...
     d                                1a
     d  watchFailure...
     d                                1a
     d  debuggerJobStopped...
     d                                1a
     d  reserved_1...
     d                                1a
     d  reserved_2...
     d                                1a
     d  reserved_3...
     d                                1a

       // Format of Message Data
     d psh_msgData_t...
     d                 ds                  qualified based(pTemplate)
     d  lenMsgData                   10i 0
     d  msgId                         7a
     d  qMsgF                              likeds(qObj_t)
     d  reserved_1                    1a
     d  msgData                     512a

       // Receiver variable
       // Reasons 1, 2, 3, 4:
       //   Stopped locations   Array of BINARY(4)
      //                               The line number in the statement view where the program
       //                               is stopped.
      //   Thread ID           CHAR(8) The thread identification of the thread where the
      //                               program is stopped. This value immediately follows
       //                               the last stopped location.
       // Reasons 5, 6:
       //   Watch Receiver Variable Header
     d psh_watchHeader_t...
     d                 ds                  qualified based(pTemplate)
     d  watchNumber                  10i 0
     d  offsStoppedPgmInf...
     d                               10i 0
     d  offsWatchInterruptInf...
     d                               10i 0

       //   Watch Stopped Program Information
     d psh_watchPgmInf_t...
     d                 ds                  qualified based(pTemplate)
     d  offsProcName                 10i 0
     d  lenProcName                  10i 0
     d  offsStoppedLocations...
     d                               10i 0
     d  numStoppedLocations...
     d                               10i 0
     d  stoppedLocationsFlag...
     d                                1a
     d  reserved_1                    3a
     d  threadId                      8a

       //   Watch Interrupt Information
     d psh_watchInterruptInf_t...
     d                 ds                  qualified based(pTemplate)
     d  qJob                               likeds(qJob_t)
     d  qPgm                               likeds(qObj_t)
     d  pgmType                      10a
     d  module                       10a
     d  locationsFlag                 1a
     d  reserved_1                    1a
     d  offsProcName                 10i 0
     d  lenProcName                  10i 0
     d  offsLocations                10i 0
     d  numLocations                 10i 0
     d  threadId                      8a
     d  offsClassFileName...
     d                               10i 0
     d  lenClassFileName...
     d                               10i 0

     d PGMTYPE_PGM     c                   '*PGM'
     d PGMTYPE_SRVPGM  c                   '*SRVPGM'
     d PGMTYPE_JAVA    c                   '*CLASS'

     d LOCATION_LINE_IN_STMT_VIEW...
     d                 c                   '1'
     d LOCATION_STMT_NUMBER...
     d                 c                   '2'
     d LOCATION_MI_NUMBER...
     d                 c                   '3'

       // Start Source Debug (QteStartSourceDebug) API
     d QteStartSourceDebug...
     d                 pr                  extproc('QteStartSourceDebug')
     d  i_qPgm                             const  likeds(qObj_t)
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

       // End Source Debug (QteEndSourceDebug) API
     d QteEndSourceDebug...
     d                 pr                  extproc('QteEndSourceDebug')
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

       // Retrieve Module Views (QteRetrieveModuleViews) API
     d QteRetrieveModuleViews...
     d                 pr                  extproc('QteRetrieveModuleViews')
     d  o_rcvVar                                  like(rcvVar_t)
     d                                            options(*varsize)
     d  i_lenRcvVar                  10i 0 const
     d  i_format                      8a   const
     d  i_qPgm                             const  likeds(qObj_t)
     d  i_pgmType                    10a   const
     d  i_module                     10a   const
     d  o_rtnLib                     10a
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

     d vewl0100_t...
     d                 ds                  qualified based(pTemplate)
     d  bytRet                       10i 0
     d  bytAvl                       10i 0
     d  numE                         10i 0
      // Array of vewl0100_elem_t

     d vewl0100_elem_t...
     d                 ds                  qualified based(pTemplate)
     d  module                       10a
     d  viewType                     10a
     d  compilerId                   20a
     d  mainInd                      10a
     d  viewTmstmp                   13a
     d  viewDesc                     50a
     d  reserved_1                    3a
     d  viewNumber                   10i 0
     d  numOfViews                   10i 0

       // Compiler IDs (compilerId)
     d CMPLR_ILE_C     c                   x'00050000'
     d CMPLR_ILE_CPP   c                   x'00050001'
     d CMPLR_ILE_CL    c                   x'00060000'
     d CMPLR_OPM_CL    c                   x'00060001'
     d CMPLR_OPM_CBL   c                   x'00070000'
     d CMPLR_ILE_CBL   c                   x'00070001'
     d CMPLR_OPM_RPG   c                   x'00170001'
     d CMPLR_ILE_RPG   c                   x'00170002'
     d CMPLR_JAVA      c                   x'001D0000'

       // Main Indicator (mainInd)
     d MAIN_YES        c                   '*MAIN'
     d MAIN_NO         c                   '*NOMAIN'

       // View type (viewType)
     d VIEW_TEXT       c                   '*TEXT'
     d VIEW_LISTING    c                   '*LISTING'
     d VIEW_STATEMENT  c                   '*STATEMENT'

       // Register Debug View (QteRegisterDebugView) API
     d QteRegisterDebugView...
     d                 pr                  extproc('QteRegisterDebugView')
     d  o_viewId                     10i 0
     d  o_viewLines                  10i 0
     d  o_rtnLib                     10a
     d  o_viewTmstmp                 13a
     d  i_qPgm                             const  likeds(qObj_t)
     d  i_pgmType                    10a   const
     d  i_module                     10a   const
     d  i_viewNumber                 10i 0 const
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

       // Retrieve View Text (QteRetrieveViewText) API
     d QteRetrieveViewText...
     d                 pr                  extproc('QteRetrieveViewText')
     d  o_rcvVar                                  like(rcvVar_t)
     d                                            options(*varsize)
     d  i_lenRcvVar                  10i 0 const
     d  i_viewId                     10i 0 const
     d  i_startLine                  10i 0 const
     d  i_numLines                   10i 0 const
     d  i_lineLength                 10i 0 const
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

     d vewt0100_t...
     d                 ds                  qualified based(pTemplate)
     d  bytRet                       10i 0
     d  bytAvl                       10i 0
     d  numLinesRtn                  10i 0
     d  lineLength                   10i 0
      // Array of char(*)

       // Add Breakpoint (QteAddBreakpoint) API
     d QteAddBreakpoint...
     d                 pr                  extproc('QteAddBreakpoint')
     d  i_viewId                     10i 0 const
     d  i_lineNumber                 10i 0 const
     d  i_columnNumber...
     d                               10i 0 const
     d  o_lineStmtView...
     d                               10i 0
     d  io_errCode                                likeds(errCode_t)
     d                                            options(*varsize)

      /endif
