      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* iSphere Retrieve View Text (IQSDRTVVT) API                                               *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2018 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
      //*         >>PARM<<  DBGVIEW(*LIST);                                                        *
      //*         >>PARM<<  OPTIMIZE(*NONE);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'IQSDRTVVT'

      /copy qcpysrc,iqsdrtvvt
      /copy qcpysrc,sdvt0100
      /copy qcpysrc,srcdbgapi
      /copy qcpysrc,qmhsndpm
      /copy qcpysrc,qmhrsnem
      /copy qcpysrc,types
      /copy qcpysrc,errc0100

     d main...
     d                 pr
     d                                     extproc('main')
     d  io_rcvVar                 32767a         options(*varsize)
     d  i_lenRcvVar                        const like(gi_lenRcvVar)
     d  i_viewId                           const like(gi_viewId)
     d  i_startLine                        const like(gi_startLine)
     d  i_numLines                         const like(gi_numLines)
     d  i_lineLength                       const like(gi_lineLength)
     d  io_errCode                32767a         options(*varsize)

     d kill...
     d                 pr
     d                                     extproc('kill')
     d  i_msgText                   256a   const varying options(*varsize)

      //*==========================================================================================*
      //* Program entry point                                                                      *
      //*==========================================================================================*

     d IQSDRTVVT...
     d                 pi
     d  go_rcvVar                 32767a         options(*varsize)              <-- Text lines
     d  gi_lenRcvVar                 10i 0 const                                --> Length of rcvVar
     d  gi_viewId                    10i 0 const                                --> View id
     d  gi_startLine                 10i 0 const                                --> Start line
     d  gi_numLines                  10i 0 const                                --> Number of lines
     d  gi_lineLength                10i 0 const                                --> Lenght of line
     d  go_errCode                32767a         options(*nopass: *varsize)     <-> Error code

     d p_errCode       c                   7

     d errCode         ds                  likeds(errCode_t)
      /free

         *inlr = *on;

         if (%parms() >= p_errCode and %addr(go_errCode) <> *null);
           // Parameter 'errCode' has been passed => return errors
           main(go_rcvVar: gi_lenrcvVar: gi_viewId: gi_startLine
                : gi_numLines: gi_lineLength: go_errCode);
         else;
           clear errCode;
           if (%addr(go_errCode) <> *null);
             // Parameter 'errCode' has been omitted => ignore errors
             errCode.bytPrv = %size(errCode);
           else;
             // Parameter 'errCode' has not been passed => signal error
             errCode.bytPrv = 0;
           endif;

           main(go_rcvVar: gi_lenrcvVar: gi_viewId: gi_startLine
                : gi_numLines: gi_lineLength: errCode);
         endif;

      /end-free

      //*==========================================================================================*
      //* Main procedure                                                                           *
      //*==========================================================================================*
     p main...
     p                 b
     d                 pi
     d  o_rcvVar                  32767a         options(*varsize)
     d  i_lenRcvVar                        const like(gi_lenRcvVar)
     d  i_viewId                           const like(gi_viewId)
     d  i_startLine                        const like(gi_startLine)
     d  i_numLines                         const like(gi_numLines)
     d  i_lineLength                       const like(gi_lineLength)
     d  io_errCode                32767a         options(*varsize)

     d header          ds                  likeds(iqsdrtvvt_header_t)
     d                                     based(pHeader)

     d vewt0100        ds                  likeds(vewt0100_t)
     d                                     based(pVewt0100)
     d vewt0100Line    s          32767a   based(pVewt0100Line)

     d errCode         ds                  likeds(errCode_t)
     d                                     based(pErrCode)

     d pRcvVar         s               *
     d entrySize       s             10i 0
     d offset          s             10i 0
     d numLines        s             10i 0
     d numLinesAvl     s             10i 0
     d sdvt0100        ds                  likeds(sdvt0100_t)
     d                                     based(pSdvt0100)
     d sdvt0100Line    s          32767a   based(pSdvt0100Line)
     d lastSdvt0100    ds                  likeds(sdvt0100)
     d                                     based(pLastSdvt0100)

     d rsnm0100        ds                  likeds(rsnm0100_t) inz
     d lclErrCode      ds                  likeds(errCode_t) inz
      /free

         if (i_lenRcvVar < IQSDRTVVT_HEADER_MIN_SIZE);
            kill('Minimum length of message information is not valid: ' +
                 %char(i_lenRcvVar) +
                 '. The minimum size is ' +
                 %char(IQSDRTVVT_HEADER_MIN_SIZE) + ' bytes');
         endif;

         monitor;

            pRcvVar = %addr(o_rcvVar);
            pHeader = %addr(o_rcvVar);
            pErrCode = %addr(io_errCode);
            pVewt0100 = %alloc(i_lenRcvVar);
            pLastSdvt0100 = *null;

            if (i_lenRcvVar >= %size(header));
               clear header;
               header.bytAvl = %size(header);
               header.bytRtn = %size(header);
            else;
               %subst(header: 1: i_lenRcvVar) = *ALLx'00';
               header.bytAvl = %size(header);
               header.bytRtn = i_lenRcvVar;
            endif;

            offset = %size(header);
            numLines = 0;

            QteRetrieveViewText(
               vewt0100: i_lenRcvVar: i_viewId: i_startLine: i_numLines
               : i_lineLength: io_errCode);

            entrySize = %size(sdvt0100) + vewt0100.lineLength;

            numLinesAvl =
               (vewt0100.bytAvl - %size(vewt0100)) / vewt0100.lineLength;

            header.bytAvl += numLinesAvl * vewt0100.lineLength;

            if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_NUM_LINES_AVL);
               header.numLinesAvl = numLinesAvl;
            endif;

            dow (errCode.bytAvl = 0 and vewt0100.bytAvl > 0
                 and numLines < vewt0100.numLinesRtn
                 and offset + entrySize <= i_lenRcvVar);

               numLines += 1;

               // get line from returned buffer
               pVewt0100Line = %addr(vewt0100) + %size(vewt0100)
                               + (numLines - 1) * vewt0100.lineLength;

               // copy line data
               pSdvt0100 = pHeader + offset;
               pLastSdvt0100 = pSdvt0100;

               clear sdvt0100;
               sdvt0100.offsNextLine = offset + entrySize;
               sdvt0100.lineLength = vewt0100.lineLength;
               sdvt0100.offsLine = offset + %size(sdvt0100);

               pSdvt0100Line = pHeader + sdvt0100.offsLine;

               %subst(sdvt0100Line: 1: vewt0100.lineLength) =
                  %subst(vewt0100Line: 1: vewt0100.lineLength);

               // update header information
               header.bytRtn += vewt0100.lineLength;

               if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_NUM_LINES_RTN);
                  header.numLinesRtn += 1;
               endif;

               if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_LAST_LINE);
                  header.lastLine = i_startLine + header.numLinesRtn - 1;
               endif;

               if (header.numLinesRtn = 1);

                  if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_LINE_LENGTH);
                     header.lineLength = vewt0100.lineLength;
                  endif;

                  if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_FIRST_LINE);
                     header.firstLine = i_startLine;
                  endif;

                  if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_OFFS_LEN_LINE_ENTRY);
                     header.lenLineE = entrySize;
                  endif;

                  if (i_lenRcvVar >= IQSDRTVVT_MIN_SIZE_OFFS_FIRST_LINE);
                     header.offsFirstLine = offset;
                  endif;
               endif;

               // set offset to next view entry
               offset += entrySize;

            enddo;

            if (pLastSdvt0100 <> *null);
               lastSdvt0100.offsNextLine = 0;
            endif;

         on-error;

            if (pvewt0100 <> *null);
              dealloc(n) pvewt0100;
            endif;

            rsnm0100.toStkCnt = 1;
            rsnm0100.toStkEMod = '*NONE';
            rsnm0100.toStkEPgm = '*NONE';
            rsnm0100.lenStkEQual = 10;
            rsnm0100.toStkE = '*PGMBDY';
            qmhrsnem('': lclErrCode: rsnm0100
                     : %size(rsnm0100): 'RSNM0100': '*': 0);
         endmon;

         if (pvewt0100 <> *null);
           dealloc(n) pvewt0100;
         endif;

      /end-free
     p                 e

      //*==========================================================================================*
      //* send an *ESCAPE message to kill the program.                                             *
      //*==========================================================================================*
     p kill...
     p                 b
     d                 pi
     d  i_msgText                   256a   const varying options(*varsize)

     d msgKey          s              4A   inz
     d qMsgF           ds                  likeds(qObj_t) inz
     d errCode         ds                  likeds(errCode_t) inz
      /free

         clear qMsgF;
         qMsgF.name = 'QCPFMSG';
         qMsgF.lib  = 'QSYS';

         clear errCode;
         errCode.bytPrv = %size(errCode);
         QMHSNDPM('CPF9898': qMsgF: i_msgText: %len(i_msgText): '*ESCAPE'
                  : '*CTLBDY': 1: msgKey: errCode);

      /end-free
     p                 e

