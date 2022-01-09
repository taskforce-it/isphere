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
      //* Debugger session handler (dummy for retrieving module listing)                           *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISRCDBGSSN);                                                    *
      //*         >>PARM<<  DBGVIEW(*ALL);                                                         *
      //*         >>PARM<<  OPTIMIZE(*NONE);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,dbgssnhdlr
      /copy qcpysrc,srcdbgapi

      //*------------------------------------------------------------------------------------------*

     d sds            sds                  qualified
     d  pgmName                1     10a
     d  pgmLib                81     90a

     d performStartDebug...
     d                 pr
     d                                     extproc('performStartDebug')
     d  i_pgmList                          const  likeds(sdh_pgmList_t)
     d  i_pgmListNumE                10i 0 const

     d performEndDebug...
     d                 pr
     d                                     extproc('performEndDebug')

      //*==========================================================================================*
       //*  Program Entry Point
      //*==========================================================================================*
     d DBGSSNHDLR...
     d                 pi
     d  i_reason                     10a   const
     d  i_pgmList                          const  likeds(sdh_pgmList_t)
     d  i_pgmListNumE                10i 0 const

      //*------------------------------------------------------------------------------------------*
      /free

       select;
       when (i_reason = REASON_START);
          performStartDebug(i_pgmList: i_pgmListNumE);
       when (i_reason = REASON_DISPLAY);
       // ignored
       when (i_reason = REASON_STOP);
          performEndDebug();
       other;
       endsl;

       return;

      /end-free

      //*==========================================================================================*
       //*  Perform start debug actions
      //*==========================================================================================*
     p performStartDebug...
     p                 b
     d                 pi
     d  i_pgmList                          const  likeds(sdh_pgmList_t)
     d  i_pgmListNumE                10i 0 const

     d qPgm            ds                  likeds(qObj_t)
     d errCode         ds                  likeds(errCode_t)

      //*------------------------------------------------------------------------------------------*
      /free

       clear qPgm;
       qPgm.name = 'DBGSTPHDLR';
       qPgm.lib = '*LIBL';

       clear errCode;

       QteStartSourceDebug(qPgm: errCode);

      /end-free
     p                 e

      //*==========================================================================================*
       //*  Perform end debug actions
      //*==========================================================================================*
     p performEndDebug...
     p                 b
     d                 pi

       // Normal End (CEETREC) API
     d CEETREC...
     d                 pr                  extproc('CEETREC')
     d  i_cel_rc_mod                 10i 0 options(*omit)
     d  i_user_rc                    10i 0 options(*omit)

      //*------------------------------------------------------------------------------------------*
      /free

       // Reclaim activation group of debug session
       CEETREC(*omit: *omit);

      /end-free
     p                 e

