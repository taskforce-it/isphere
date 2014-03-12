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
      //* Demo of iSphere Source File Search API                                                   *
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

     fFNDSTRI   O    E             Disk    Rename(FNDSTRI : FNDSTRIRF)

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'FNDSTR'

     d/Copy QCPYSRC,FNDSTR

      // Global field declarations

     dGIHDL            S             10S 0                                      Handle
     dGIELE            S              6S 0                                      Elements

      //*------------------------------------------------------------------------------------------*

      /Free

       // Get handle
       GIHDL = FNDSTR_getHandle();

       // Set search elements
       Clear FNDSTRIRF;
       XIHDL = GIHDL;
       XILIB = 'MYLIB';
       XIFILE = 'MYFILE';
       XIMBR = '*'; // '*' means all members in the source file
       Write FNDSTRIRF;

       // Resolve generic search elements
       FNDSTR_resolveGenericSearchElements(
        GIHDL // --> Handle
       );

       // Get number of search elements (Only if neccessary)
       GIELE = FNDSTR_getNumberOfSearchElements(
        GIHDL // --> Handle
       );

       // Do search
       FNDSTR_search(
        GIHDL :    // --> Handle
        'MYSTRG' : // --> String
        1 :        // --> From column
        228 :      // --> To column
        '*IGNORE'  // --> Case (*IGNORE / *MATCH)
       );

       // The search results reside in file FNDSTRO. They have to be read and processed
       // ...
       // ...
       // ...

       // Clear files
       FNDSTR_clear(
        GIHDL // --> Handle
       );

       // Finish
       *INLR = *On;

      /End-Free
