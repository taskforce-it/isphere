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
      //* Wrapper (FNDSTR - search)                                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
      //*         >>PARM<<  BNDDIR(ISPHERE);                                                       *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
      //*         >>PARM<<  TGTRLS(V5R4M0);                                                        *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'W001002'

     dW001002          PR
     d                               10S 0 Const                                --> Handle
     d                               40A   Const                                --> String
     d                                3S 0 Const                                --> From column
     d                                3S 0 Const                                --> To column
     d                               10A   Const                                --> Case

      // Procedure interface for procedure 'W001002'

     dW001002          PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LPSTG                         40A   Const                                --> String
     d LPFCOL                         3S 0 Const                                --> From column
     d LPTCOL                         3S 0 Const                                --> To column
     d LPCASE                        10A   Const                                --> Case

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'FNDSTR'

     d/Copy QCPYSRC,FNDSTR

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call service program
       FNDSTR_search(
        LPHDL :  // --> Handle
        LPSTG :  // --> String
        LPFCOL : // --> From column
        LPTCOL : // --> To column
        LPCASE   // --> Case
       );

       // Leave procedure
       Return;

      /End-Free
