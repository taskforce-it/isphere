      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2017 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Demo of iSphere IQDBRTVFD API                                                            *
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
      /copy qcpysrc,iqdbrtvfd
      /copy qcpysrc,rvfd0100

      //*------------------------------------------------------------------------------------------*

     D sndMsg...
     D                 PR                         extproc('sndMsg')
     D  i_text                      128A   value  varying

     D PGM_ENTRY_POINT...
     D                 pr                  extpgm('DEMO5')

     d sds            sds                  qualified
     d  pgmLib                81     90a

      //*==========================================================================================*
      //*  Program Entry Point
      //*==========================================================================================*
     D PGM_ENTRY_POINT...
     D                 pi

     D i               s             10i 0 inz
     D qFile           ds                  likeds(qObj_t) inz
     D qFileRtn        ds                  likeds(qObj_t) inz
     D errCode         ds                  likeds(errCode_t) inz
     D fldInf          s          65535a
     D header          ds                  likeds(iqdbrtvfd_header_t)
     D                                     based(pHeader)
     D rvfd0100        ds                  likeds(rvfd0100_t)
     D                                     based(pRvfd0100)
     D text            s            128a

      /copy qcpysrc,RTVM0300

      //*------------------------------------------------------------------------------------------*
      /free

         *INLR = *On;

         pHeader = %addr(fldInf);

         qFile.name = 'QRPGLESRC';
         qFile.lib  = '*LIBL';

         qFile.name = 'TYPES_SQL';
         qFile.lib  = %trim(sds.pgmLib);

         IQDBRTVFD(fldInf: %size(fldInf): 'RVFD0100'
                   : qFile: IQDBRTVFD_RCDFMT_FIRST: errCode
                   : IQDBRTVFD_OVERRIDE_NO
                   : IQDBRTVFD_SYSTEM_LCL
                   : IQDBRTVFD_FMT_TYPE_EXT);

         for i = 1 to header.numFldRtn;
            pRvfd0100 = %addr(fldInf) +
                        header.offsFirstFldD + ((i-1) * %size(rvfd0100));
            text = rvfd0100.name + ': ' + rvfd0100.type +
                   '(length=' + %char(rvfd0100.length) + ', ' +
                   'varLen='  + rvfd0100.isVarying + ', ' +
                   'decpos='  + %char(rvfd0100.decPos) + ', ' +
                   'buffLen=' + %char(rvfd0100.buffLen) + ', ' +
                   'alcLen='  + %char(rvfd0100.alcLen) + ', ' +
                   'ccsid='   + %char(rvfd0100.ccsid) + ')';
            sndMsg(text);
         endfor;

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

