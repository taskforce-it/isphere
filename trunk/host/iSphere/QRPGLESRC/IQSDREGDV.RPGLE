      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* iSphere Register Debug View (IQSDREGDV) API                                              *
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

       // Procedure prototypes for modul 'IQSDREGDV'

      /copy qcpysrc,iqsdregdv
      /copy qcpysrc,srcdbgapi
      /copy qcpysrc,qmhsndpm
      /copy qcpysrc,qmhrsnem
      /copy qcpysrc,types
      /copy qcpysrc,errc0100

     d main...
     d                 pr
     d                                     extproc('main')
     d  o_viewId                                 like(go_viewId)
     d  o_viewLines                              like(go_viewLines)
     d  o_rtnLib                                 like(go_rtnLib)
     d  o_viewTmstmp                             like(go_viewTmstmp)
     d  i_qObj                             const likeds(gi_qObj)
     d  i_objType                          const like(gi_objType)
     d  i_module                           const like(gi_module)
     d  i_viewNumber                       const like(gi_viewNumber)
     d  io_errCode                32767a         options(*varsize)

     d kill...
     d                 pr
     d                                     extproc('kill')
     d  i_msgText                   256a   const varying options(*varsize)

      //*==========================================================================================*
      //* Program entry point                                                                      *
      //*==========================================================================================*

     d IQSDREGDV...
     d                 pi
     d  go_viewId                    10i 0                                      <-- View id
     d  go_viewLines                 10i 0                                      <-- View lines
     d  go_rtnLib                    10a                                        <-- Returned library
     d  go_viewTmstmp                13a                                        <-- View timestamp
     d  gi_qObj                            const likeds(qObj_t)                 --> Qualified object
     d  gi_objType                   10a   const                                --> Object type
     d  gi_module                    10a   const                                --> Module
     d  gi_viewNumber                10i 0 const                                --> View number
     d  gio_errCode               32767a         options(*nopass:*omit:*varsize)<-> Error code

     d p_errCode       c                   9

     d errCode         ds                  likeds(errCode_t)
      /free

       *inlr = *on;

       if (%parms() >= p_errCode and %addr(gio_errCode) <> *null);
          // Parameter 'errCode' has been passed => return errors
          main(go_viewId: go_viewLines: go_rtnLib: go_viewTmstmp
               : gi_qObj: gi_objType: gi_module: gi_viewNumber: gio_errCode);
       else;
          clear errCode;
          if (%addr(gio_errCode) <> *null);
             // Parameter 'errCode' has been omitted => ignore errors
             errCode.bytPrv = %size(errCode);
          else;
             // Parameter 'errCode' has not been passed => signal error
             errCode.bytPrv = 0;
          endif;

          main(go_viewId: go_viewLines: go_rtnLib: go_viewTmstmp
               : gi_qObj: gi_objType: gi_module: gi_viewNumber: errCode);
       endif;

      /end-free

      //*==========================================================================================*
      //* Main procedure                                                                           *
      //*==========================================================================================*
     p main...
     p                 b
     d                 pi
     d  o_viewId                                 like(go_viewId)
     d  o_viewLines                              like(go_viewLines)
     d  o_rtnLib                                 like(go_rtnLib)
     d  o_viewTmstmp                             like(go_viewTmstmp)
     d  i_qObj                             const likeds(gi_qObj)
     d  i_objType                          const like(gi_objType)
     d  i_module                           const like(gi_module)
     d  i_viewNumber                       const like(gi_viewNumber)
     d  io_errCode                32767a         options(*varsize)

     d rsnm0100        ds                  likeds(rsnm0100_t) inz
     d lclErrCode      ds                  likeds(errCode_t) inz
      /free

       monitor;

          QteRegisterDebugView(
             o_viewId: o_viewLines: o_rtnLib: o_viewTmstmp
             : i_qObj: i_objType: i_module: i_viewNumber: io_errCode);

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

