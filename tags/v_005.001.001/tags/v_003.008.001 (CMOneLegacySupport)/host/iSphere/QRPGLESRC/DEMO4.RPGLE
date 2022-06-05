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
      //* Demo to produce spooled files for testing purposes                                       *
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
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hBNDDIR('ISPHERE')
      /copy qcpysrc,h_spec

      //*------------------------------------------------------------------------------------------*

     FDEMO4T    IF   F  112        DISK    usropn
     f                                     extfile(g_demoTextFile)
     f                                     extmbr(g_demoTextMember)
     fDEMO4P    O    E             Printer usropn oflind(*in70)
     f                                     extfile(g_spooledfile)


     D g_demoTextFile...
     D                 s             21a   inz
     D g_demoTextMember...
     D                 s             10a   inz('DEMO4T')

     D g_spooledfile...
     D                 s             21a   inz('DEMO4P')

     D g_line          ds                  qualified
     D  seq                           6s 2
     D  date                          6s 0
     D  text                        100a
     D   marker                       3a   overlay(text)

     D g_msg           s             52a

     D sds            sds                  qualified
     D  pgmLib                81     90a

     D sndMsg...
     D                 PR                         extproc('sndMsg')
     D  i_text                      128A   value  varying

     D PGM_ENTRY_POINT...
     D                 pr                  extpgm('DEMO4')
     D  gi_orientation...
     D                                1a   const  options(*nopass)

      //*==========================================================================================*
      //*  Program Entry Point
      //*==========================================================================================*
     D PGM_ENTRY_POINT...
     D                 pi
     D  gi_orientation...
     D                                1a   const  options(*nopass)
      //*------------------------------------------------------------------------------------------*
      /Free

       *INLR = *On;

       if (%parms() >= 1);
          select;
          when (gi_orientation = 'P');
             g_spooledfile = 'DEMO4P';
          when (gi_orientation = 'L');
             g_spooledfile = 'DEMO4L';
          other;
             sndMsg('Invalid parameter: ' + gi_orientation +
                    '. Use ''P'' (portrait) or ''L'' (landscape).');
             return;
          endsl;
       endif;

       // Open files
       g_demoTextFile = %trim(sds.pgmLib) + '/' + 'QRPGLESRC';
       g_demoTextMember = 'DEMO4T';

       open DEMO4T;
       open DEMO4P;

       *in70 = *ON; // New page

       // Read and print sample text
       setll 1 DEMO4T;
       dow '1'; // read until EOF
         read DEMO4T g_line;
         if (%eof(DEMO4T));
           leave;
         endif;

         if (*in70);
            write NEW_PAGE;
            *in70 = *OFF;
         endif;

         select;
         when (g_line.text = '');
            // ignore;
         when (g_line.text = ':LF.');
            TEXT = '';
            write STANDARD;
         when (g_line.marker = '%1%');
            TEXT = %subst(g_line.text: 4);
            write HEADLINE1;
         when (g_line.marker = '%2%');
            TEXT = %subst(g_line.text: 4);
            write HEADLINE2;
         other;
            TEXT = g_line.text;
            write STANDARD;
         endsl;

       enddo;

       TEXT = *ALL'-';
       write STANDARD;

       // Close files
       close DEMO4T;
       close DEMO4P;

      /End-Free

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

