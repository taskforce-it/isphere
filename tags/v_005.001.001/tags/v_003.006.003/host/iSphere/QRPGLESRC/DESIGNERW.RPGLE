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
      //* 5250 Emulator - Designer (Wrapper)                                                       *
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
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'DESIGNERW'

     dDESIGNERW        PR

      // Procedure interface for procedure 'DESIGNERW'

     dDESIGNERW        PI

      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'HANDLER'

     dHANDLER          PR                  ExtPgm(g_PGM)
     d                               10A   Const                                --> Programm

      // Program status data structure

     dg_PSDS          SDS           429
     d g_LIB                  81     90A

      // Program

     dg_PGM            S             21A

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call programm
       g_PGM = %Trim(g_LIB) + '/HANDLER';
       HANDLER('DESIGNER');

       // Leave procedure
       *INLR = *On;

      /End-Free
