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
001100220309      //* Wrapper (ZFNDSTR - resolveGenericSearchElements)                                         *
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
003100220309      // Procedure prototyp for procedure 'W004005'
003200220309
003300220309     dW004005          PR
003400220309     d                               10S 0 Const                                --> Handle
003500220309
003600220309      // Procedure interface for procedure 'W004005'
003700220309
003800220309     dW004005          PI
003900220309     d LPHDL                         10S 0 Const                                --> Handle
004000220309
004100220309      //*------------------------------------------------------------------------------------------*
004200220309
004300220309      // Procedure Prototypes for Modul 'ZFNDSTR'
004400220309
004500220309     d/Copy QCPYSRC,ZFNDSTR
004600220309
004700220309      //*------------------------------------------------------------------------------------------*
004800220309
004900220309      /Free
005000220309
005100220309       // Call service program
005200220309       ZFNDSTR_resolveGenericSearchElements(
005300220309        LPHDL // --> Handle
005400220309       );
005500220309
005600220309       // Leave procedure
005700220309       Return;
005800220309
005900220309      /End-Free
