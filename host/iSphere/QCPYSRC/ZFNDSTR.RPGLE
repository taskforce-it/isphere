000100220309      //*------------------------------------------------------------------------------------------*
000200220309      //*                                                                                          *
000300220309      //* Find String in Stream File, Prototypes and Type Definitions                              *
000400220309      //*                                                                                          *
000500220309      //*------------------------------------------------------------------------------------------*
000600220309      //*                                                                                          *
000700220309      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
000800220309      //* All rights reserved. This program and the accompanying materials                         *
000900220309      //* are made available under the terms of the Common Public License v1.0                     *
001000220309      //* which accompanies this distribution, and is available at                                 *
001100220309      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
001200220309      //*                                                                                          *
001300220309      //*------------------------------------------------------------------------------------------*
001400220309
001500220309     dZFNDSTR_getHandle...
001600220309     d                 PR            10S 0                                      <-- Handle
001700220309
001800220309     dZFNDSTR_resolveGenericSearchElements...
001900220309     d                 PR
002000220309     d                               10S 0 Const                                --> Handle
002100220309
002200220309     dZFNDSTR_getNumberOfSearchElements...
002300220309     d                 PR             6S 0                                      <-- Number of elem.
002400220309     d                               10S 0 Const                                --> Handle
002500220309
002600220309     dZFNDSTR_search...
002700220309     d                 PR
002800220309     d handle                        10S 0 Const                                --> Handle
002900220309     d showRecords                     N   Const                                --> Show records
003000220309     d matchOpt                      10A   Const                                --> Match option
003100220309     d numArguments                  10I 0 Const                                --> Number of args.
003200220309     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
003300220309     d                                     Options(*varsize)
003400220309     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)
003500220309
003600220309     dZFNDSTR_clear...
003700220309     d                 PR
003800220309     d                               10S 0 Const                                --> Handle
003900220309
004000220309      /copy qcpysrc,srchopts_t
