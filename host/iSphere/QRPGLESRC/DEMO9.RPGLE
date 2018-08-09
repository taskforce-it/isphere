12345 //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2018 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Demo of iSphere IQSDRTVVT API                                                            *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(*NEW);                                                          *
      //*         >>PARM<<  DBGVIEW(*ALL);                                                         *
      //*         >>PARM<<  OPTIMIZE(*NONE);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hBNDDIR('ISPHERE')

     fQSYSPRT   o    f  141        printer usropn oflind(*in70)

      /copy qcpysrc,types
      /copy qcpysrc,iqsdrtvmv
      /copy qcpysrc,sdmv0100
      /copy qcpysrc,iqsdrtvvt
      /copy qcpysrc,sdvt0100
      /copy qcpysrc,srcdbgapi

      //*------------------------------------------------------------------------------------------*

     D sndMsg...
     D                 PR                         extproc('sndMsg')
     D  i_text                      128A   value  varying

     D PGM_ENTRY_POINT...
     D                 pr                  extpgm('DEMO5')

     d sds            sds                  qualified
     d  pgmName                1     10a
     d  pgmLib                81     90a

      //*==========================================================================================*
      //*  Program Entry Point
      //*==========================================================================================*
     d PGM_ENTRY_POINT...
     d                 pi

     d i               s             10i 0 inz
     d qObj            ds                  likeds(qObj_t) inz
     d objType         s             10a
     d module          s             10a
     d registeredView...
     d                 ds                  qualified
     d  id                           10i 0
     d  numLines                     10i 0
     d  rtnLib                       10a
     d  tmstmp                       13a
     d totalLinesCounter...
     d                 s             10i 0
     d currentLine     s             10i 0
     d errCode         ds                  likeds(errCode_t) inz

     d viewsBuffer     s          32767a
     d hdrViews        ds                  likeds(iqsdrtvmv_header_t)
     d                                     based(pHdrViews)
     d sdmv0100        ds                  likeds(sdmv0100_t)
     d                                     based(pSdmv0100)
     d pFirstSdmv0100  s               *

     d linesBuffer     s          32767a
     d hdrLines        ds                  likeds(iqsdrtvvt_header_t)
     d                                     based(pHdrLines)
     d sdvt0100        ds                  likeds(sdvt0100_t)
     d                                     based(pSdvt0100)
     d viewLineBuffer  s           1024a   based(pViewLineBuffer)

     d printLine       ds                  qualified
     d  seqNbr                        6a
     d  space                         2a
     d  text                        133a

      // system -- Execute a Command
      //    Return value       Meaning
      //    ----------------   --------------------------------------
      //    0                  the command is successful
      //    -1                 for a NULL pointer
      //    1                  the command fails
     d system          pr            10i 0 extproc('system')
     d  i_clCommand                    *   value options(*string)

     d EXCP_MSGID      s              7a   import('_EXCP_MSGID')

     d isDebuggerStartedLocally...
     d                 s               n   inz(*off)

      //*------------------------------------------------------------------------------------------*
      /free

         *INLR = *On;

         monitor;

            // Start debugger
            if (system('QSYS/STRDBG PGM(*LIBL/' + %trim(sds.pgmName) + ') +
                        UPDPROD(*NO) DSPMODSRC(*NO) SRCDBGPGM(*SYSDFT)') = 0);
              isDebuggerStartedLocally = *on;
            endif;

            // Find view, prefer view type *LISTING
            pHdrViews = %addr(viewsBuffer);

            qObj.name = sds.pgmName;
            qObj.lib  = '*LIBL';
            objType = '*PGM';
            module = sds.pgmName;
            clear errCode;

            IQSDRTVMV(viewsBuffer: %size(viewsBuffer): 'SDMV0100'
                      : qObj: objType: module: errCode);

            for i = 1 to hdrViews.numViewsRtn;

               if (i = 1);
                  pSdmv0100 = pHdrViews + hdrViews.offsFirstView;
               else;
                  pSdmv0100 = pHdrViews + sdmv0100.offsNextView;
               endif;

               if (sdmv0100.viewType = SDMV_VIEW_TYPE_TEXT);
                  if (pFirstSdmv0100 = *null);
                     pFirstSdmv0100 = pSdmv0100;
                  endif;
               endif;

               if (sdmv0100.viewType = SDMV_VIEW_TYPE_LISTING);
                  leave;
               endif;

            endfor;

            if (i > hdrViews.numViewsRtn);
               if (pFirstSdmv0100 = *null);
                  pSdmv0100 = *null;
               else;
                  pSdmv0100 = pFirstSdmv0100;
               endif;
            endif;

            if (pSdmv0100 = *null);
               sndMsg('No *TEXT or *LISTING views found.');
            else;

               // Register debug view
               QteRegisterDebugView(
                  registeredView.id: registeredView.numLines
                  : registeredView.rtnLib: registeredView.tmstmp
                  : qObj: objType: module: sdmv0100.viewNumber: errCode);

               // Retrieve and print view test
               totalLinesCounter = 0;
               dow (totalLinesCounter < registeredView.numLines);

                  pHdrLines = %addr(linesBuffer);

                  // parameter #6 (line length) should be length of
                  // source data + 33 additional bytes for spaces,
                  // date and source sequence number.
                  IQSDRTVVT(
                     linesBuffer: %size(linesBuffer): registeredView.id
                     : totalLinesCounter + 1: 0: %len(printLine.text): errCode);

                  currentLine = 0;
                  dow (currentLine < hdrLines.numLinesRtn);

                     if (not %open(QSYSPRT));
                        open QSYSPRT;
                     endif;

                     currentLine += 1;
                     totalLinesCounter += 1;

                     if (currentLine = 1);
                        pSdvt0100 = pHdrLines + hdrLines.offsFirstLine;
                     else;
                        pSdvt0100 = pHdrLines + sdvt0100.offsNextLine;
                     endif;

                     pViewLineBuffer = pHdrLines + sdvt0100.offsLine;

                     clear printLine;
                     printLine.seqNbr =
                        %editc(%dec(totalLinesCounter: 6: 0): 'Z');
                     printLine.text =
                        %subst(viewLineBuffer:1:sdvt0100.lineLength);

                     write QSYSPRT printLine;

                  enddo;
               enddo;

            endif;

         on-error;
         endmon;


         // clean up
         if (%open(QSYSPRT));
            close QSYSPRT;
         endif;

         if (isDebuggerStartedLocally);
            system('QSYS/ENDDBG');
            isDebuggerStartedLocally = *off;
         endif;

      /end-free

      //*==========================================================================================*
      //*  Send program message to command line.
      //*==========================================================================================*
     P sndMsg...
     P                 b
     D                 PI
     D  i_text                      128A   value  varying

     D msgKey          S              4A   inz

     D qMsgF           DS                  qualified inz
     D  name                         10A
     D  lib                          10A

      //  API error code structure
     D errCode         DS                  qualified inz
     D  bytPrv                       10I 0
     D  bytAvl                       10I 0
     D  excID                         7A
     D  reserved                      1A
     D  excDta                      256A

      //  Send Program Message (QMHSNDPM) API
     D QMHSNDPM        PR                         extpgm('QMHSNDPM')
     D   i_msgID                      7A   const
     D   i_qMsgF                     20A   const
     D   i_msgData                32767A   const  options(*varsize )
     D   i_length                    10I 0 const
     D   i_msgType                   10A   const
     D   i_callStkE               32767A   const  options(*varsize )
     D   i_callStkC                  10I 0 const
     D   o_msgKey                     4A
     D   io_ErrCode               32767A          options(*varsize )
     D   i_lenStkE                   10I 0 const  options(*nopass  )
     D   i_callStkEQ                 20A   const  options(*nopass  )
     D   i_wait                      10I 0 const  options(*nopass  )
     D   i_callStkEDT                10A   const  options(*nopass  )
     D   i_ccsid                     10I 0 const  options(*nopass  )
      //*------------------------------------------------------------------------------------------*
      /Free

         clear qMsgF;
         qMsgF.name = 'QCPFMSG';
         qMsgF.lib  = '*LIBL';

         clear errCode;
         errCode.bytPrv = %size(errCode);
         QMHSNDPM('CPF9897': qMsgF: i_text: %len(i_text): '*INFO'
                  : '*CTLBDY': 1: msgKey: errCode);

      /End-Free
     P                 e

