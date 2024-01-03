000100220310      //*------------------------------------------------------------------------------------------*
000200220310      //*                                                                                          *
000300220310      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
000400220310      //* All rights reserved. This program and the accompanying materials                         *
000500220310      //* are made available under the terms of the Common Public License v1.0                     *
000600220310      //* which accompanies this distribution, and is available at                                 *
000700220310      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
000800220310      //*                                                                                          *
000900220310      //*------------------------------------------------------------------------------------------*
001000220310      //*                                                                                          *
001100220310      //* Demo of iSphere Stream File Search API                                                   *
001200220310      //*                                                                                          *
001300220310      //*------------------------------------------------------------------------------------------*
001400220310      //* STRPREPRC Compile Options:                                                               *
001500220310      //*   >>PRE-COMPILER<<                                                                       *
001600220310      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
001700220310      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
001800220310      //*       >>COMPILE<<                                                                        *
001900220310      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
002000220310      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
002100220310      //*         >>PARM<<  DBGVIEW(*LIST);                                                        *
002200220310      //*         >>PARM<<  OPTIMIZE(*NONE);                                                       *
002300220310      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
002400220310      //*         >>PARM<<  TGTRLS(&TR);                                                           *
002500220310      //*       >>END-COMPILE<<                                                                    *
002600220310      //*       >>EXECUTE<<                                                                        *
002700220310      //*   >>END-PRE-COMPILER<<                                                                   *
002800220310      //*------------------------------------------------------------------------------------------*
002900220310
003000220310     hBNDDIR('ISPHERE')
003100220310      /copy qcpysrc,h_spec
003200220310
003300220310      //*------------------------------------------------------------------------------------------*
003400220310
003500220310     fZFNDSTRI  O    E             Disk    Rename(ZFNDSTRI : ZFNDSTRIRF)
003600220310
003700220310      //*------------------------------------------------------------------------------------------*
003800220310
003900220310     d/Copy QCPYSRC,ZFNDSTR
004000220310
004100220310      // Global field declarations
004200220310
004300220310     dGIHDL            S             10S 0                                      Handle
004400220310     dGIELE            S              6S 0                                      Elements
004500220310     dsearchArguments  DS                  likeds(searchArgument_t) inz
004600220310     d                                     dim(SRCHOPTS_ARGUMENTS_SIZE)
004700220310
004800220310      //*------------------------------------------------------------------------------------------*
004900220310
005000220310      /Free
005100220310
005200220310       // Get handle
005300220310       GIHDL = ZFNDSTR_getHandle();
005400220310
005500220310       // Set search elements
005600220310       Clear ZFNDSTRIRF;
005700220310       XIHDL = GIHDL;
005800220310       XIDIR = '/tmp';
005900220310       XISTMF = '*'; // '*' means all stream files in the directory
006000220310       Write ZFNDSTRIRF;
006100220310
006200220310       // Resolve generic search elements
006300220310       ZFNDSTR_resolveGenericSearchElements(
006400220310         GIHDL // --> Handle
006500220310       );
006600220310
006700220310       // Get number of search elements (Only if neccessary)
006800220310       GIELE = ZFNDSTR_getNumberOfSearchElements(
006900220310         GIHDL // --> Handle
007000220310       );
007100220310
007200220310       // Do search
007300220310       searchArguments(1).operator = SRCHOPTS_OPERATOR_CONTAINS;
007400220310       searchArguments(1).string = 'element';
007500220310       searchArguments(1).fromColumn = 1;
007600220310       searchArguments(1).toColumn = 512;
007700220310       searchArguments(1).case = SRCHOPTS_CASE_IGNORE;
007800220310       searchArguments(1).regex = SRCHOPTS_ARG_STRING;
007900220310
008000220310       ZFNDSTR_search(
008100220311         GIHDL :              // --> Handle
008200220311         *ON :                // --> Show all records
008300220311         SRCHOPTS_MATCH_ALL : // --> Match all search arguments
008400220311         1 :                  // --> Number of search arguments
008500220311         searchArguments      // --> search arguments
008600220310       );
008700220310
008800220310       // The search results reside in file ZFNDSTRO. They have to be read and processed
008900220310       // ...
009000220310       // ...
009100220310       // ...
009200220310
009300220310       // Clear files
009400220310       ZFNDSTR_clear(
009500220310         GIHDL // --> Handle
009600220310       );
009700220310
009800220310       // Finish
009900220310       *INLR = *On;
010000220310
010100220310      /End-Free
