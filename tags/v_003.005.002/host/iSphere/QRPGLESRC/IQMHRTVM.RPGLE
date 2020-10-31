      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* iSphere Retrieve Message (IQMHRTVM) API                                                  *
      //*                                                                                          *
      //* For a detailed description of the API refer to:                                          *
      //* [workspace]/iSphere Core Plugin/doc/iSphere Retrieve Message API.doc                     *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
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
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
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

      // Procedure prototypes for modul 'IQMHRTVM'

      /copy qcpysrc,iqmhrtvm
      /copy qcpysrc,qmhrtvm
      /copy qcpysrc,qmhsndpm
      /copy qcpysrc,qmhrsnem
      /copy qcpysrc,types
      /copy qcpysrc,errc0100

     d main...
     d                 pr
     d                                     extproc('main')
     d  i_msgInf                  32767a         options(*varsize)
     d  i_lenMsgInf                        const like(gi_lenMsgInf)
     d  i_format                           const like(gi_format)
     d  i_msgId                            const like(gi_msgId)
     d  i_qMsgF                            const likeds(gi_qMsgF)
     d  i_rtnFmtCtl                        const like(gi_rtnFmtCtl)
     d  io_errCode                32767a         options(*varsize)
     d  i_rtvOpt                           const like(gi_rtvOpt)
     d  i_numMsgRtn                        const like(gi_numMsgRtn)
     d  i_ccsidText                        const like(gi_ccsidText)

     d kill...
     d                 pr
     d                                     extproc('kill')
     d  i_msgText                   256a   const varying options(*varsize)

      //*==========================================================================================*
      //* Program entry point                                                                      *
      //*==========================================================================================*

     d IQMHRTVM...
     d                 pi
     d  gi_msgInf                 32767a         options(*varsize)              <-- Message informat
     d  gi_lenMsgInf                 10i 0 const                                --> Length of messag
     d  gi_format                     8a   const                                --> Format name
     d  gi_msgId                      7a   const                                --> Message identifi
     d  gi_qMsgF                           const likeds(qObj_t)                 --> Qualified messag
     d  gi_rtnFmtCtl                 10a   const                                --> Return format co
     d  gio_errCode               32767a         options(*varsize)              <-> Error code
     d  gi_rtvOpt                    10a   const options(*nopass)               --> Retrieve option
     d  gi_numMsgRtn                 10i 0 const options(*nopass)               --> Number of messag
     d  gi_ccsidText                 10i 0 const options(*nopass)               --> CCSID to convert

     d p_rtvOpt        c                   8
     d p_numMsgRtn     c                   9
     d p_ccsidText     c                   10

     d rtvOpt          s                   like(gi_rtvOpt)
     d numMsgRtn       s                   like(gi_numMsgRtn)
     d ccsidText       s                   like(gi_ccsidText)
      /free

         *inlr = *on;

         if (%parms() >= p_rtvOpt and %addr(gi_rtvOpt) <> *null);
            rtvOpt = gi_rtvOpt;
         else;
            rtvOpt = '*FIRST';
         endif;

         if (%parms() >= p_numMsgRtn and %addr(gi_numMsgRtn) <> *null);
            numMsgRtn = gi_numMsgRtn;
         else;
            numMsgRtn = IQMHRTVM_ALL;
         endif;

         if (%parms() >= p_ccsidText and %addr(gi_ccsidText) <> *null);
            ccsidText = gi_ccsidText;
         else;
            ccsidText = 0;
         endif;

         main(gi_msgInf: gi_lenMsgInf: gi_format: gi_msgId: gi_qMsgF
              : gi_rtnFmtCtl: gio_errCode
              : rtvOpt: numMsgRtn: ccsidText);

      /end-free

      //*==========================================================================================*
      //* Main procedure                                                                           *
      //*==========================================================================================*
     p main...
     p                 b
     d                 pi
     d  i_msgInf                  32767a         options(*varsize)
     d  i_lenMsgInf                        const like(gi_lenMsgInf)
     d  i_format                           const like(gi_format)
     d  i_msgId                            const like(gi_msgId)
     d  i_qMsgF                            const likeds(gi_qMsgF)
     d  i_rtnFmtCtl                        const like(gi_rtnFmtCtl)
     d  io_errCode                32767a         options(*varsize)
     d  i_rtvOpt                           const like(gi_rtvOpt)
     d  i_numMsgRtn                        const like(gi_numMsgRtn)
     d  i_ccsidText                        const like(gi_ccsidText)

     d header          ds                  likeds(iqmhrtvm_header_t)
     d                                     based(pHeader)

     d rtvmBuffer      ds          8192    qualified
     d  bytRtn                       10i 0 inz
     d  bytAvl                       10i 0 inz
     D  msgId                 27     33a   inz

     d errCode         ds                  likeds(errCode_t)
     d                                     based(pErrCode)

     d pMsgInf         s               *
     d offset          s             10i 0
     d available       s             10i 0
     d numMessages     s             10i 0
     d rtnBuffer       s           8192a   based(pRtnBuffer)

     d rsnm0100        ds                  likeds(rsnm0100_t) inz
     d lclErrCode      ds                  likeds(errCode_t) inz
      /free

         if (i_lenMsgInf < IQMHRTVM_HEADER_MIN_SIZE);
            kill('Minimum length of message information is not valid: ' +
                 %char(i_lenMsgInf) +
                 '. The minimum size is ' +
                 %char(IQMHRTVM_HEADER_MIN_SIZE) + ' bytes');
         endif;

         if (i_format <> 'RTVM0300' and i_format <> 'RTVM0400');
            kill('Format ' + %trim(i_format) + ' is not valid. +
                  The possible formats are: RTVM0300, RTVM0400');
         endif;

         if (i_rtvOpt <> IQMHRTVM_FIRST and
             i_rtvOpt <> IQMHRTVM_NEXT and
             i_rtvOpt <> IQMHRTVM_MSGID);
            kill('Retrieve option ' + %trim(i_rtvOpt) + ' is not valid. +
                  The possible values are: *FIRST, *NEXT or *MSGID');
         endif;

         monitor;

            pMsgInf = %addr(i_msgInf);
            pHeader = %addr(i_msgInf);
            pErrCode = %addr(io_errCode);

            if (i_lenMsgInf >= %size(header));
               clear header;
               header.bytAvl = %size(header);
               header.bytRtn = %size(header);
            else;
               %subst(header: 1: i_lenMsgInf) = *ALLx'00';
               header.bytAvl = %size(header);
               header.bytRtn = i_lenMsgInf;
            endif;

            offset = %size(header);
            numMessages = 0;

            qmhrtvm(rtvmBuffer: %size(rtvmBuffer): i_format: i_msgId
                    : i_qMsgF : '': 0: '*NO': i_rtnFmtCtl: io_errCode
                    : i_rtvOpt: i_ccsidText: 0);

            dow (errCode.bytAvl = 0 and rtvmBuffer.bytAvl > 0);

               // header.bytAvl += rtvmBuffer.bytAvl;
               numMessages += 1;
               header.bytAvl += rtvmBuffer.bytAvl;

               if (offset + rtvmBuffer.bytAvl <= i_lenMsgInf);

                  // copy message data
                  pRtnBuffer = pMsgInf + offset;
                  %subst(rtnBuffer: 1: rtvmBuffer.bytRtn) =
                     %subst(rtvmBuffer: 1: rtvmBuffer.bytRtn);

                  // update header information
                  header.bytRtn += rtvmBuffer.bytRtn;

                  if (i_lenMsgInf >= IQMHRTVM_MIN_SIZE_NUM_MSG_RTN);
                     header.numMsgRtn += 1;
                  endif;

                  if (i_lenMsgInf >= IQMHRTVM_MIN_SIZE_LAST_MSG_ID);
                     header.lastMsgId = rtvmBuffer.msgId;
                  endif;

                  if (header.numMsgRtn = 1);
                     if (i_lenMsgInf >= IQMHRTVM_MIN_SIZE_FIRST_MSG_ID);
                        header.firstMsgId = rtvmBuffer.msgId;
                     endif;
                     if (i_lenMsgInf >=IQMHRTVM_MIN_SIZE_OFFSET_FIRST_MSG_DESC);
                        header.offsFirstMsgD = offset;
                     endif;
                  endif;

                  // set offset to next message description
                  offset += rtvmBuffer.bytRtn;
               else;
                  // exit, when the buffer is full, in case
                  // the caller wants to get all messages
                  if (i_numMsgRtn = IQMHRTVM_ALL);
                     leave;
                  endif;
               endif;

               // exit, when the specified number of messages are reached
               if (i_numMsgRtn <> IQMHRTVM_ALL and
                   numMessages >= i_numMsgRtn);
                  leave;
               endif;

               qmhrtvm(rtvmBuffer: %size(rtvmBuffer): i_format: rtvmBuffer.msgId
                       : i_qMsgF : '': 0: '*NO': i_rtnFmtCtl: io_errCode
                       : '*NEXT': i_ccsidText: 0);
            enddo;

         on-error;

            rsnm0100.toStkCnt = 1;
            rsnm0100.toStkEMod = '*NONE';
            rsnm0100.toStkEPgm = '*NONE';
            rsnm0100.lenStkEQual = 10;
            rsnm0100.toStkE = '*PGMBDY';
            qmhrsnem('': lclErrCode: rsnm0100
                     : %size(rsnm0100): 'RSNM0100': '*': 0);
         endmon;

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

