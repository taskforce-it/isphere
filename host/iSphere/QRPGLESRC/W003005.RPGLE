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
      //* Wrapper (SPLF - setJob)                                                                  *
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

      // Procedure prototyp for procedure 'W003005'

     dW003005          PR
     d                               10A   Const                                --> Job name
     d                               10A   Const                                --> User name
     d                                6A   Const                                --> Job number

      // Procedure interface for procedure 'W003005'

     dW003005          PI
     d LPJOB                         10A   Const                                --> Job name
     d LPUSR                         10A   Const                                --> User name
     d LPNBR                          6A   Const                                --> Job number

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'SPLF'

     d/Copy QCPYSRC,SPLF

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call service program
       SPLF_setJob(
        LPJOB : // --> Job name
        LPUSR : // --> User name
        LPNBR   // --> Job number
       );

       // Leave procedure
       Return;

      /End-Free
