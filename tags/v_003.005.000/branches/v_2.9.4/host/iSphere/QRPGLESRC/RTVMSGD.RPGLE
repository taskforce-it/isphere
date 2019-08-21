      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Retrieve Message Description                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<< CRTRPGMOD    MODULE(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  TRUNCNBR(*NO);                                                         *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      *-------------------------------------------------------------------------------------------*

      // Common data types

      /Copy QCPYSRC,TYPES

      // Prozedurenprototyp für Prozedur 'RtvMsgText, ...'

      /Copy QCPYSRC,RTVMSGD

      // Prozedurenprototyp für Prozedur 'QMHRTVM'

      /Copy QCPYSRC,QMHRTVM

      // Datenstruktur für Format 'RTVM0100' für Prozedur 'QMHRTVM'

      /Copy QCPYSRC,RTVM0100

      // Datenstruktur für Format 'ERRC0100' für Fehlercode

      /Copy QCPYSRC,ERRC0100

      // Local Prototypes

      // Retrieve message description
     d rtvMsgD...
     d                 pr            10i 0 extproc('rtvMsgD')
     d  o_rtnData                  4096a
     d  i_format                      8a   const
     d  i_msgID                       7a   const
     d  i_rplData                  4096a   const  varying
     d  i_qMsgF                            const  likeds(qObj_t)
     d  i_rplVars                    10a   const
     d  i_rtnCtrlChr                 10a   const

      //===========================================================================================*
      // Abrufen Text einer Nachrichtenbeschreibung
      //===========================================================================================*
     p rtvMsgText...
     p                 b                   Export
     d                 pi           250a   Varying
     d  i_msgID                       7a   Const
     d  i_msgF                       10a   Const
     d  i_lib                        10a   Const
     d  i_data                      256a   Const Varying

     d msgText         s            250a   Varying

     d qMsgF           ds                  LikeDs(qObj_t)
     d rtvm0100        ds                  LikeDs(rtvm0100_t)
      /free

         qMsgF.name = i_msgF;
         qMsgF.lib = i_lib;

         if (rtvMsgD(rtvm0100: 'RTVM0100': i_msgID: i_data:
                     qMsgF: '*YES': '*NO') <= 0);
            return '*' + i_msgID;
         endif;

         if (rtvm0100.lenMsgRtn <= 0);
            return '';
         endif;

         msgText = %subst(rtvm0100.data: 1: rtvm0100.lenMsgRtn);

         return msgText;

      /end-free
     p                 e

      //===========================================================================================*
      // Abrufen Hilfetext einer Nachrichtenbeschreibung
      //===========================================================================================*
     p rtvMsgHelp...
     p                 b                   Export
     d                 pi          3000a   Varying
     d  i_msgID                       7a   Const
     d  i_msgF                       10a   Const
     d  i_lib                        10a   Const
     d  i_data                      256a   Const Varying

     d msgHelp         s           3000a   Varying

     d qMsgF           ds                  LikeDs(qObj_t)
     d rtvm0100        ds                  LikeDs(rtvm0100_t)
      /free

         qMsgF.name = i_msgF;
         qMsgF.lib = i_lib;

         if (rtvMsgD(rtvm0100: 'RTVM0100': i_msgID: i_data:
                     qMsgF: '*YES': '*NO') <= 0);
            return '*' + i_msgID;
         endif;

         if (rtvm0100.lenMsgHRtn <= 0);
            return '';
         endif;

         msgHelp = %subst(rtvm0100.data
                          : 1 + rtvm0100.lenMsgRtn
                          : rtvm0100.lenMsgHRtn);

         return msgHelp;

      /end-free
     p                 e

      *=========================================================================
      *  *** Private ***
      *  Retrieve message description
      *=========================================================================
     p rtvMsgD...
     p                 b
     d                 pi            10i 0
     d  o_rtnData                  4096a
     d  i_format                      8a   const
     d  i_msgID                       7a   const
     d  i_rplData                  4096a   const  varying
     d  i_qMsgF                            const  likeds(qObj_t)
     d  i_rplVars                    10a   const
     d  i_rtnCtrlChr                 10a   const

      //  Local fields
     d rtnInf          ds                  qualified based(pRtnInf)
     d  bytRet                       10i 0
     d  bytAvl                       10i 0
      /free

         QMHRTVM(
            o_rtnData: %size(o_rtnData): i_format: i_msgID: i_qMsgF:
            i_rplData: %len(i_rplData): i_rplVars: i_rtnCtrlChr: ERRC0100);

         if (ERRCExcId <> '');
            clear o_rtnData;
            return -1;
         endif;

         pRtnInf = %addr(o_rtnData);

         return rtnInf.bytRet;

      /end-free
     p                 e

