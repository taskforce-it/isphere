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

     fFNDSTRI   O    E             Disk    Rename(FNDSTRI : FNDSTRIRF)

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'FNDSTR'

     d/Copy QCPYSRC,FNDSTR

      // Global field declarations

     dGIHDL            S             10S 0                                      Handle
     dGIELE            S              6S 0                                      Elements
     dsearchArguments  DS                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

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
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'MYSTRG';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 228;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
       searchArguments(1).regex = SRCHOPTS_ARG_STRING;

       FNDSTR_search(
        GIHDL :         // --> Handle
        *ON :           // --> Show all records
        *ON :           // --> Match all search arguments
        1 :             // --> Number of search arguments
        searchArguments // --> search arguments
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
