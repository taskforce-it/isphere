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
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'W001002'

     dW001002          PR
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchAll                      10A   Const                                --> Match option
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

      // Procedure interface for procedure 'W001002'

     dW001002          PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCOPT                      10A   Const                                --> Match option
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'FNDSTR'

     d/Copy QCPYSRC,FNDSTR

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call service program
       FNDSTR_search(
        LPHDL :     // --> Handle
        LSSHWRCDS : // --> Show records
        LSMTCOPT :  // --> Match option (all/any/line mode)
        LISIZE :    // --> Number of args.
        LSARGS      // --> Search arguments
       );

       // Leave procedure
       Return;

      /End-Free
