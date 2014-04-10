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
      //* Wrapper (XFNDSTR - clear)                                                                *
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
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'W002003'

     dW002003          PR
     d                               10S 0 Const                                --> Handle

      // Procedure interface for procedure 'W002003'

     dW002003          PI
     d LPHDL                         10S 0 Const                                --> Handle

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'XFNDSTR'

     d/Copy QCPYSRC,XFNDSTR

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call service program
       XFNDSTR_clear(
        LPHDL // --> Handle
       );

       // Leave procedure
       Return;

      /End-Free
