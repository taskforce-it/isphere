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
      //* Demo of iSphere Message File Search API                                                  *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
      //*         >>PARM<<  TGTRLS(V5R4M0);                                                        *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hBNDDIR('ISPHERE')

      //*------------------------------------------------------------------------------------------*

     fXFNDSTRI  O    E             Disk    Rename(XFNDSTRI : XFNDSTRIRF)

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'XFNDSTR'

     d/Copy QCPYSRC,XFNDSTR

      // Global field declarations

     dGIHDL            S             10S 0                                      Handle
     dGIELE            S              6S 0                                      Elements

      //*------------------------------------------------------------------------------------------*

      /Free

       // Get handle
       GIHDL = XFNDSTR_getHandle();

       // Set search elements
       Clear XFNDSTRIRF;
       XIHDL = GIHDL;
       XILIB = 'CMODEU';
       XIMSGF = 'XCMDMSG';
       Write XFNDSTRIRF;

       // Get number of search elements (Only if neccessary)
       GIELE = XFNDSTR_getNumberOfSearchElements(
        GIHDL // --> Handle
       );

       // Do search
       XFNDSTR_search(
        GIHDL :    // --> Handle
        'Vorkommen' : // --> String
        1 :        // --> From column
        132 :      // --> To column
        '*IGNORE'  // --> Case (*IGNORE / *MATCH)
       );

       // The search results reside in file XFNDSTRO. They have to be read and processed
       // ...
       // ...
       // ...

       // Clear files
       XFNDSTR_clear(
        GIHDL // --> Handle
       );

       // Finish
       *INLR = *On;

      /End-Free
