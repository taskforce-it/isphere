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
      //* Debugger program stop handler (dummy for retrieving module listing)                      *
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

      /copy qcpysrc,dbgstphdlr
      /copy qcpysrc,srcdbgapi

      //*------------------------------------------------------------------------------------------*

     d sds            sds                  qualified
     d  pgmName                1     10a
     d  pgmLib                81     90a

      //*==========================================================================================*
       //*  Program Entry Point
      //*==========================================================================================*
     d DBGSTPHDLR...
     d                 pi
     d  i_qPgm                             const  likeds(qObj_t)
     d  i_pgmType                    10a   const
     d  i_module                     10a   const
     d  i_stopReason                       const  likeds(psh_stopReason_t)
     d  i_rcvVar                  32767a   const  options(*varsize)
     d  i_numE                       10i 0 const
     d  i_msgData                 32767a   const  options(*varsize)

      //*------------------------------------------------------------------------------------------*
      /free

       return;

      /end-free

