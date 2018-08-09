      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* iSphere Retrieve Module Views (IQSDRTVMV) API                                            *
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

       // Procedure prototypes for modul 'IQSDRTVMV'

      /copy qcpysrc,iqsdrtvmv
      /copy qcpysrc,sdmv0100
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
     d  i_format                           const like(gi_format)
     d  i_qObj                             const likeds(gi_qObj)
     d  i_objType                    10a   const
     d  i_module                     10a   const
     d  io_errCode                32767a         options(*varsize)

     d kill...
     d                 pr
     d                                     extproc('kill')
     d  i_msgText                   256a   const varying options(*varsize)

      //*==========================================================================================*
      //* Program entry point                                                                      *
      //*==========================================================================================*

     d IQSDRTVMV...
     d                 pi
     d  go_rcvVar                 32767a         options(*varsize)              <-- Module views
     d  gi_lenRcvVar                 10i 0 const                                --> Length of rcvVar
     d  gi_format                     8a   const                                --> Format name
     d  gi_qObj                            const likeds(qObj_t)                 --> Qualified object
     d  gi_objType                   10a   const                                --> Object type
     d  gi_module                    10a   const options(*nopass)               --> Module (*ALL)
     d  gio_errCode               32767a         options(*nopass:*omit:*varsize)<-> Error code

     d p_module        c                   6
     d p_errCode       c                   7

     d module          s                   like(gi_module)
     d errCode         ds                  likeds(errCode_t)
      /free

       *inlr = *on;

       if (%parms() >= p_module and %addr(gi_module) <> *null);
          module = gi_module;
       else;
          module = IQSDRTVMV_ALL;
       endif;

       if (%parms() >= p_errCode and %addr(gio_errCode) <> *null);
          // Parameter 'errCode' has been passed => return errors
          main(go_rcvVar: gi_lenrcvVar: gi_format: gi_qObj
               : gi_objType: module: gio_errCode);
       else;
          clear errCode;
          if (%addr(gio_errCode) <> *null);
             // Parameter 'errCode' has been omitted => ignore errors
             errCode.bytPrv = %size(errCode);
          else;
             // Parameter 'errCode' has not been passed => signal error
             errCode.bytPrv = 0;
          endif;

          main(go_rcvVar: gi_lenrcvVar: gi_format: gi_qObj
               : gi_objType: module: errCode);
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
     d  i_format                           const like(gi_format)
     d  i_qObj                             const likeds(gi_qObj)
     d  i_objType                    10a   const
     d  i_module                     10a   const
     d  io_errCode                32767a         options(*varsize)

     d header          ds                  likeds(iqsdrtvmv_header_t)
     d                                     based(pHeader)

     d vewl0100        ds                  likeds(vewl0100_t)
     d                                     based(pVewl0100)
     d vewl0100View    ds                  likeds(vewl0100_elem_t)
     d                                     based(pVewl0100View)

     d errCode         ds                  likeds(errCode_t)
     d                                     based(pErrCode)

     d pRcvVar         s               *
     d entrySize       s             10i 0
     d offset          s             10i 0
     d numViews        s             10i 0
     d numViewsAvl     s             10i 0
     d rtnLib          s             10a
     d sdmv0100        ds                  likeds(sdmv0100_t)
     d                                     based(pSdmv0100)
     d lastSdmv0100    ds                  likeds(sdmv0100)
     d                                     based(pLastSdmv0100)

     d rsnm0100        ds                  likeds(rsnm0100_t) inz
     d lclErrCode      ds                  likeds(errCode_t) inz
      /free

       if (i_lenRcvVar < IQSDRTVMV_HEADER_MIN_SIZE);
          kill('Minimum length of message information is not valid: ' +
               %char(i_lenRcvVar) +
               '. The minimum size is ' +
               %char(IQSDRTVMV_HEADER_MIN_SIZE) + ' bytes');
       endif;

       if (i_format <> 'SDMV0100');
          kill('Format ' + %trim(i_format) + ' is not valid. +
               The possible formats are: SDMV0100');
       endif;

       monitor;

          pRcvVar = %addr(o_rcvVar);
          pHeader = %addr(o_rcvVar);
          pErrCode = %addr(io_errCode);
          pVewl0100 = %alloc(i_lenRcvVar);
          pLastSdmv0100 = *null;

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
          numViews = 0;

          QteRetrieveModuleViews(
             vewl0100: i_lenRcvVar: 'VEWL0100': i_qObj: i_objType: i_module
             : rtnLib: io_errCode);

          entrySize = %size(sdmv0100);

          numViewsAvl =
             %int((vewl0100.bytAvl - %size(vewl0100)) / %size(vewl0100View));

          header.bytAvl += numViewsAvl * entrySize;

          dow (errCode.bytAvl = 0 and vewl0100.bytAvl > 0
               and numViews < vewl0100.numE
               and offset + entrySize <= i_lenRcvVar);

             numViews += 1;

             // get view from returned buffer
             pVewl0100View = %addr(vewl0100) + %size(vewl0100)
                             + (numViews - 1) * %size(vewl0100View);

             // copy view data
             pSdmv0100 = pHeader + offset;
             pLastSdmv0100 = pSdmv0100;

             clear sdmv0100;
             sdmv0100.offsNextView = offset + entrySize;
             sdmv0100.module = vewl0100View.module;
             sdmv0100.viewType = vewl0100View.viewType;
             sdmv0100.compilerId = vewl0100View.compilerId;
             sdmv0100.mainInd = vewl0100View.mainInd;
             sdmv0100.viewTmstmp = vewl0100View.viewTmstmp;
             sdmv0100.viewDesc = vewl0100View.viewDesc;
             sdmv0100.viewNumber = vewl0100View.viewNumber;
             sdmv0100.numOfViews = vewl0100View.numOfViews;

                // update header information
             header.bytRtn += entrySize;

             if (i_lenRcvVar >= IQSDRTVMV_MIN_SIZE_NUM_VIEWS_RTN);
                header.numViewsRtn += 1;
             endif;

             if (header.numViewsRtn = 1);

                if (i_lenRcvVar >= IQSDRTVMV_MIN_SIZE_LEN_VIEW_ENTRY);
                   header.lenViewE = entrySize;
                endif;

                if (i_lenRcvVar >= IQSDRTVMV_MIN_SIZE_FIRST_VIEW);
                   header.offsFirstView = offset;
                endif;
             endif;

             // set offset to next view entry
             offset += entrySize;

          enddo;

          if (pLastSdmv0100 <> *null);
             lastSdmv0100.offsNextView = 0;
          endif;

       on-error;

          if (pVewl0100 <> *null);
             dealloc(n) pVewl0100;
          endif;

          rsnm0100.toStkCnt = 1;
          rsnm0100.toStkEMod = '*NONE';
          rsnm0100.toStkEPgm = '*NONE';
          rsnm0100.lenStkEQual = 10;
          rsnm0100.toStkE = '*PGMBDY';
          qmhrsnem('': lclErrCode: rsnm0100
                   : %size(rsnm0100): 'RSNM0100': '*': 0);
       endmon;

       if (pVewl0100 <> *null);
          dealloc(n) pVewl0100;
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

