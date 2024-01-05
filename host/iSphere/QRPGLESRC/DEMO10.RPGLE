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
      //* Demo of iSphere Stream File Search API                                                   *
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

     fZFNDSTRI  O    E             Disk    Rename(ZFNDSTRI : ZFNDSTRIRF)

      //*------------------------------------------------------------------------------------------*

     d/Copy QCPYSRC,ZFNDSTR

      // Global field declarations

     dGIHDL            S             10S 0                                      Handle
     dGIELE            S              6S 0                                      Elements
     dsearchArguments  DS                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

      //*------------------------------------------------------------------------------------------*

      /Free

       // Get handle
       GIHDL = ZFNDSTR_getHandle();

       // Set search elements
       Clear ZFNDSTRIRF;
       XIHDL = GIHDL;
       XIDIR = '/tmp';
       XISTMF = '*'; // '*' means all stream files in the directory
       Write ZFNDSTRIRF;

       // Resolve generic search elements
       ZFNDSTR_resolveGenericSearchElements(
         GIHDL // --> Handle
       );

       // Get number of search elements (Only if neccessary)
       GIELE = ZFNDSTR_getNumberOfSearchElements(
         GIHDL // --> Handle
       );

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'element';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 512;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       ZFNDSTR_search(
         GIHDL :              // --> Handle
         *ON :                // --> Show all records
         SRCHOPTS_MATCH_ALL : // --> Match all search arguments
         1 :                  // --> Number of search arguments
         searchArguments      // --> search arguments
       );

       // The search results reside in file ZFNDSTRO. They have to be read and processed
       // ...
       // ...
       // ...

       // Clear files
       ZFNDSTR_clear(
         GIHDL // --> Handle
       );

       // Finish
       *INLR = *On;

      /End-Free
