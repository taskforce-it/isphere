000100220309      //*------------------------------------------------------------------------------------------*
000200220309      //*                                                                                          *
000300220309      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
000400220309      //* All rights reserved. This program and the accompanying materials                         *
000500220309      //* are made available under the terms of the Common Public License v1.0                     *
000600220309      //* which accompanies this distribution, and is available at                                 *
000700220309      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
000800220309      //*                                                                                          *
000900220309      //*------------------------------------------------------------------------------------------*
001000220309      //*                                                                                          *
001100220309      //* Wrapper (ZFNDSTR - search)                                                               *
001200220309      //*                                                                                          *
001300220309      //*------------------------------------------------------------------------------------------*
001400220309      //* STRPREPRC Compile Options:                                                               *
001500220309      //*   >>PRE-COMPILER<<                                                                       *
001600220309      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
001700220309      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
001800220309      //*       >>COMPILE<<                                                                        *
001900220309      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
002000220309      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
002100220309      //*         >>PARM<<  BNDDIR(ISPHERE);                                                       *
002200220309      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
002300220309      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
002400220309      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
002500220309      //*         >>PARM<<  TGTRLS(&TR);                                                           *
002600220309      //*       >>END-COMPILE<<                                                                    *
002700220309      //*       >>EXECUTE<<                                                                        *
002800220309      //*   >>END-PRE-COMPILER<<                                                                   *
002900220309      //*------------------------------------------------------------------------------------------*
003000220309
003100220309      // Procedure prototyp for procedure 'W004002'
003200220309
003300220309     dW004002          PR
003400220309     d handle                        10S 0 Const                                --> Handle
003500220309     d showRecords                     N   Const                                --> Show records
003600220309     d matchAll                      10A   Const                                --> Match option
003700220309     d numArguments                  10I 0 Const                                --> Number of args.
003800220309     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
003900220309     d                                     Options(*varsize)
004000220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
004100220309
004200220309      // Procedure interface for procedure 'W004002'
004300220309
004400220309     dW004002          PI
004500220309     d LPHDL                         10S 0 Const                                --> Handle
004600220309     d LSSHWRCDS                       N   Const                                --> Show records
004700220309     d LSMTCOPT                      10A   Const                                --> Match option
004800220309     d LISIZE                        10I 0 Const                                --> Number of args.
004900220309     d LSARGS                              Const Likeds(searchArgument_t)       --> String
005000220309     d                                     Options(*varsize)
005100220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
005200220309
005300220309      //*------------------------------------------------------------------------------------------*
005400220309
005500220309      // Procedure Prototypes for Modul 'ZFNDSTR'
005600220309
005700220309     d/Copy QCPYSRC,ZFNDSTR
005800220309
005900220309      //*------------------------------------------------------------------------------------------*
006000220309
006100220309      /Free
006200220309
006300220309       // Call service program
006400220309       ZFNDSTR_search(
006500220309        LPHDL :     // --> Handle
006600220309        LSSHWRCDS : // --> Show records
006700220309        LSMTCOPT :  // --> Match option (all/any/line mode)
006800220309        LISIZE :    // --> Number of args.
006900220309        LSARGS      // --> Search arguments
007000220309       );
007100220309
007200220309       // Leave procedure
007300220309       Return;
007400220309
007500220309      /End-Free
