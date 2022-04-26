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

     fXFNDSTRI  O    E             Disk    Rename(XFNDSTRI : XFNDSTRIRF)

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'XFNDSTR'

      /Copy QCPYSRC,XFNDSTR

      // Global field declarations

     dGIHDL            S             10S 0                                      Handle
     dGIELE            S              6S 0                                      Elements
     dsearchArguments  DS                  likeds(searchArgument_t) inz
     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)

      //*------------------------------------------------------------------------------------------*

      /Free

       // Get handle
       GIHDL = XFNDSTR_getHandle();

       // Set search elements
       Clear XFNDSTRIRF;
       XIHDL = GIHDL;
       XILIB = '*LIBL';
       XIMSGF = 'STRPREPRCE';
       Write XFNDSTRIRF;

       // Get number of search elements (Only if neccessary)
       GIELE = XFNDSTR_getNumberOfSearchElements(
        GIHDL // --> Handle
       );

       // Do search
       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(1).string = 'allowed';
       searchArguments(1).fromColumn = 1;
       searchArguments(1).toColumn = 132;
       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;

       searchArguments(2).operator = SRCHOPTS_OPERATOR_CONTAINS;
       searchArguments(2).string = 'mode';
       searchArguments(2).fromColumn = 1;
       searchArguments(2).toColumn = 132;
       searchArguments(2).case = SRCHOPTS_CASE_IGNORE;

       XFNDSTR_search(
        GIHDL :    // --> Handle
        *ON :           // --> Show all records
        *OFF :          // --> Match all search arguments
        *ON :           // --> Include first level text
        *OFF :          // --> Include second level text
        *OFF :          // --> Include message ID
        2 :             // --> Number of search arguments
        searchArguments // --> search arguments
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
