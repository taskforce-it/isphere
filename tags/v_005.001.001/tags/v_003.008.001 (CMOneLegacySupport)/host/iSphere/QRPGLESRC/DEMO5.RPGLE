      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Demo of iSphere IQMHRTVM API                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(*NEW);                                                          *
      //*         >>PARM<<  DBGVIEW(*LIST);                                                        *
      //*         >>PARM<<  OPTIMIZE(*NONE);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hBNDDIR('ISPHERE')
      /copy qcpysrc,h_spec

      /copy qcpysrc,types
      /copy qcpysrc,iqmhrtvm

      //*------------------------------------------------------------------------------------------*

     D sndMsg...
     D                 PR                         extproc('sndMsg')
     D  i_text                      128A   value  varying

     D PGM_ENTRY_POINT...
     D                 pr                  extpgm('DEMO5')

      //*==========================================================================================*
      //*  Program Entry Point
      //*==========================================================================================*
     D PGM_ENTRY_POINT...
     D                 pi

     D i               s             10i 0 inz
     D qMsgF           ds                  likeds(qObj_t) inz
     D errCode         ds                  likeds(errCode_t) inz
     D msgInf          s          65535a   based(pMsgInf)
     D msgInfSize      s             10i 0 inz
     D header          ds                  likeds(iqmhrtvm_header_t)
     D                                     based(pHeader)
     D msgDesc         ds          8192    qualified based(pMsgDesc)
     D  bytRtn                       10i 0

     D numMsgToRead    s             10i 0 inz(20000)
     D msgText         s            128a   varying

      /copy qcpysrc,RTVM0300

      //*------------------------------------------------------------------------------------------*
      /free

         *INLR = *On;

         msgInfSize = 1024 * 1024 * 14; // 14 MB buffer, roughly 20000 messages
         pMsgInf = %alloc(msgInfSize);
         pHeader = pMsgInf;

         qMsgF.name = 'QCPFMSG';
         qMsgF.lib  = 'QSYS';

         IQMHRTVM(msgInf: msgInfSize: 'RTVM0300'
                  : '': qMsgF: '*YES': errCode:
                  IQMHRTVM_FIRST: numMsgToRead);

         dow (header.numMsgRtn > 0);

            for i = 1 to header.numMsgRtn;
               if (i = 1);
                  pMsgDesc = pMsgInf + header.offsFirstMsgD;
               endif;

               RTVM0300 = %subst(msgDesc: 1: msgDesc.bytRtn);
               msgText = %subst(RTVM0300: RTVMOffMsg + 1: RTVMLenMsgRtn);

               // Display the first 3 messages of the chunk
               if (i <= 3);
                  sndMsg(RTVMMsgId + ': ' + msgText);
               endif;

               pMsgDesc = pMsgDesc + RTVMByteRtn;
            endfor;

            // Display the last messages of the chunk
            sndMsg(RTVMMsgId + ': ' + msgText);

            // Display some statistics
            sndMsg('Number of messages returned: ' + %char(header.numMsgRtn));
            sndMsg('Bytes returned:              ' + %char(header.bytRtn));
            sndMsg('Bytes available:             ' + %char(header.bytAvl));

            IQMHRTVM(msgInf: msgInfSize: 'RTVM0300'
                     : RTVMMsgId: qMsgF: '*YES': errCode
                     : IQMHRTVM_NEXT: numMsgToRead);
         enddo;

         dealloc(n) pMsgInf;

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

