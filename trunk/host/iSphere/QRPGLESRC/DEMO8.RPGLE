      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2018 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Demo of iSphere IQSDRTVMV API                                                            *
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

      /copy qcpysrc,types
      /copy qcpysrc,iqsdrtvmv
      /copy qcpysrc,sdmv0100
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
     D PGM_ENTRY_POINT...
     D                 pi

     D i               s             10i 0 inz
     D qObj            ds                  likeds(qObj_t) inz
     D objType         s             10a
     D module          s             10a
     D errCode         ds                  likeds(errCode_t) inz
     D
     D buffer          s          32767a
     D header          ds                  likeds(iqsdrtvmv_header_t)
     D                                     based(pHeader)
     D sdmv0100        ds                  likeds(sdmv0100_t)
     D                                     based(pSdmv0100)
     D text            s            128a

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

            pHeader = %addr(buffer);

            qObj.name = sds.pgmName;
            qObj.lib  = '*LIBL';
            objType = '*PGM';
            module = sds.pgmName;
            clear errCode;

            IQSDRTVMV(buffer: %size(buffer): 'SDMV0100'
                      : qObj: objType: module: errCode);

            for i = 1 to header.numViewsRtn;
               pSdmv0100 = %addr(buffer) +
                           header.offsFirstView + ((i-1) * %size(sdmv0100));
               text = %trim(sdmv0100.module)
                      + ': (' + %trim(sdmv0100.viewType) + ') '
                      + sdmv0100.viewDesc;
               sndMsg(text);
            endfor;

         on-error;
         endmon;

         // clean up
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

