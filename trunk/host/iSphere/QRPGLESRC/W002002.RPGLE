      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2016 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Wrapper (XFNDSTR - search)                                                               *
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

      // Procedure prototyp for procedure 'W002002'

     dW002002          PR
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchAll                        N   Const                                --> Match all finds
     d firstLvlText                    N   Const                                --> Inc. 1.level txt
     d secLvlText                      N   Const                                --> Inc. 2.level txt
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d messageId                       N   Const Options(*nopass)               --> Inc. message id

      // Procedure interface for procedure 'W002002'

     dW002002          PI
     d LPHDL                         10S 0 Const                                --> Handle
     d LSSHWRCDS                       N   Const                                --> Show records
     d LSMTCALL                        N   Const                                --> Match all finds
     d LSFSTLVLTX                      N   Const                                --> Inc. 1.level txt
     d LSSECLVLTX                      N   Const                                --> Inc. 2.level txt
     d LISIZE                        10I 0 Const                                --> Number of args.
     d LSARGS                              Const Likeds(searchArgument_t)       --> String
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
     d LSMESSAGEID                     N   Const Options(*nopass)               --> Inc. message id

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'XFNDSTR'

     d/Copy QCPYSRC,XFNDSTR

      //*------------------------------------------------------------------------------------------*

      // Locale field declarations

     d p_LSMESSAGEID   c                   8

      //*------------------------------------------------------------------------------------------*

      /Free

       // TODO: move parameter LSMESSAGEID after LSSECLVLTX and
       //       remove the *nopass option.
       If (%parms() >= p_LSMESSAGEID);
         // Call service program
         XFNDSTR_search(
          LPHDL :       // --> Handle
          LSSHWRCDS :   // --> Show records
          LSMTCALL :    // --> Match all finds
          LSFSTLVLTX :  // --> Inc. 1.level txt
          LSSECLVLTX :  // --> Inc. 2.level txt
          LSMESSAGEID : // --> Inc. message id
          LISIZE :      // --> Number of args.
          LSARGS
         );
       Else;
         // Call service program
         XFNDSTR_search(
          LPHDL :       // --> Handle
          LSSHWRCDS :   // --> Show records
          LSMTCALL :    // --> Match all finds
          LSFSTLVLTX :  // --> Inc. 1.level txt
          LSSECLVLTX :  // --> Inc. 2.level txt
          *Off :        // not passed: Inc. message id
          LISIZE :      // --> Number of args.
          LSARGS        // --> Search arguments
         );
       EndIf;

       // Leave procedure
       Return;

      /End-Free
