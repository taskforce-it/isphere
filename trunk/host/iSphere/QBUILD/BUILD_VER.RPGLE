000100150519      //*------------------------------------------------------------------------------------------*
000200150519      //*                                                                                          *
000300150519      //* Copyright (c) 2012-2014 iSphere Project Owners.                                          *
000400150519      //* All rights reserved. This program and the accompanying materials                         *
000500150519      //* are made available under the terms of the Common Public License v1.0                     *
000600150519      //* which accompanies this distribution, and is available at                                 *
000700150519      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
000800150519      //*                                                                                          *
000900150519      //* Contributors:                                                                            *
001000150519      //*     Task Force IT-Consulting GmbH - initial API and implementation                       *
001100150519      //*                                                                                          *
001200150519      //*------------------------------------------------------------------------------------------*
001300150519      //*                                                                                          *
001400150519      //* Build iSphere Library - Set version number                                               *
001500150519      //*                                                                                          *
001600150519      //*------------------------------------------------------------------------------------------*
001700150519      //* STRPREPRC Compile Options:                                                               *
001800150519      //*   >>PRE-COMPILER<<                                                                       *
001900150519      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
002000150519      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
002100150519      //*       >>COMPILE<<                                                                        *
002200150519      //*         >>PARM<<   DFTACTGRP(*NO);                                                       *
002300150519      //*         >>PARM<<   ACTGRP(*CALLER);                                                      *
002400150519      //*         >>PARM<<   TRUNCNBR(*NO);                                                        *
002500150519      //*         >>PARM<<   DBGVIEW(*LIST);                                                       *
002600150519      //*       >>END-COMPILE<<                                                                    *
002700150519      //*       >>EXECUTE<<                                                                        *
002800150519      //*   >>END-PRE-COMPILER<<                                                                   *
002900150519      //*------------------------------------------------------------------------------------------*
003000150520     h bnddir('QC2LE')
003100150519      /copy qcpysrc,h_spec
003200150519      /define VERSION_INFO
003300150519      /copy qcpysrc,copyright
003400150519
003500150519     D splitted_t      ds           144    qualified based(pTemplate)
003600150519     D  value                        16a   varying dim(8)
003700150519
003800150519      //*------------------------------------------------------------------------------------------*
003900150519
004000150519     D pgm_entry_point...
004100150519     D                 pr
004200150519     D                                     extpgm('BUILD_VER')
004300150519     D  gi_prdlib                    10a   const
004400150519
004500150519     D main...
004600150519     D                 pr
004700150520     D                                     extproc('main')
004800150519     D  i_prdlib                     10a   const
004900150519     D  i_server_ver                128a   const varying
005000150519     D  i_client_ver                128a   const varying
005100150519
005200150519     D splitt...
005300150519     D                 pr                  likeds(splitted_t)
005400150520     D                                     extproc('splitt')
005500150519     D  i_string                    128a   const varying
005600150519
005700150519     D format...
005800150519     D                 pr            16a   varying
005900150520     D                                     extproc('format')
006000150519     D  i_string                     16a   const varying
006100150519
006200150519     D gi_dataArea     s             21a   varying
006300150519     D iSphere         ds           256    dtaara(*var: gi_dataArea) inz
006400150519     D                                     qualified
006500150519     D  server_id              1      7a   inz('Server:')
006600150519     D  server_ver             8     13a
006700150519     D  client_id             15     21a   inz('Client:')
006800150519     D  client_ver            22     27a
006900150519
007000150519      //*------------------------------------------------------------------------------------------*
007100150519      //*  Program Entry Point
007200150519      //*------------------------------------------------------------------------------------------*
007300150519     D pgm_entry_point...
007400150519     D                 pi
007500150519     D  gi_prdlib                    10a   const
007600150519      /free
007700150519
007800150519         *inlr = *on;
007900150519         main(gi_prdlib: ISPHERE_SERVER_VERSION: ISPHERE_MIN_CLIENT_VERSION);
008000150519
008100150519      /end-free
008200150519
008300150519      //*------------------------------------------------------------------------------------------*
008400150519      //*  Main
008500150519      //*------------------------------------------------------------------------------------------*
008600150519     P main...
008700150519     P                 b
008800150519     D                 pi
008900150519     D  i_prdlib                     10a   const
009000150519     D  i_server_ver                128a   const varying
009100150519     D  i_client_ver                128a   const varying
009200150519
009300150519     D i               s             10i 0
009400150519     D splitted        ds                  likeds(splitted_t) inz
009500150519     D server_version  s             16a   varying inz
009600150519     D client_version  s             16a   varying inz
009700150519      /free
009800150519
009900150519         splitted = splitt(i_server_ver);
010000150519
010100150519         i = 1;
010200150519         dow (i <= %elem(splitted.value) and splitted.value(i) <> '');
010300150519            server_version += format(splitted.value(i));
010400150519            i = i + 1;
010500150519         enddo;
010600150519
010700150519         splitted = splitt(i_client_ver);
010800150519
010900150519         i = 1;
011000150519         dow (i <= %elem(splitted.value) and splitted.value(i) <> '');
011100150519            client_version += format(splitted.value(i));
011200150519            i = i + 1;
011300150519         enddo;
011400150519
011500150519         gi_dataArea = %trim(i_prdlib) + '/' + 'ISPHERE';
011600150519         in *lock iSphere;
011700150519         reset iSphere;
011800150519         iSphere.server_ver = server_version;
011900150519         iSphere.client_ver = client_version;
012000150519         out iSphere;
012100150519
012200150519      /end-free
012300150519     P                 e
012400150519
012500150519      //*------------------------------------------------------------------------------------------*
012600150519      //*  Splitts a given string delimited by dots (.) into pieces.
012700150519      //*------------------------------------------------------------------------------------------*
012800150519     P splitt...
012900150519     P                 b
013000150519     D                 pi                  likeds(splitted_t)
013100150519     D  i_string                    128a   const varying
013200150519
013300150519     D i               s             10i 0
013400150519     D pToken          s               *
013500150519     D splitted        ds                  likeds(splitted_t) inz
013600150519
013700150519     D strtok          PR              *          extproc('strtok')
013800150519     D  i_string                       *   value  options(*string)
013900150519     D  i_delimiters                   *   value  options(*string)
014000150519      /free
014100150519
014200150519         i = 0;
014300150519         pToken = strtok(i_string: '.');
014400150519         dow (pToken <> *NULL and i < %elem(splitted.value));
014500150519            i = i + 1;
014600150519            splitted.value(i) = %str(pToken);
014700150519            pToken = strtok(*null: '.');
014800150519         enddo;
014900150519
015000150519         return splitted;
015100150519
015200150519      /end-free
015300150519     P                 e
015400150519
015500150519      //*------------------------------------------------------------------------------------------*
015600150519      //*  Formats a given string (representing an integer value)
015700150519      //*  to a 2-digit string with leading zeros.
015800150519      //*------------------------------------------------------------------------------------------*
015900150519     P format...
016000150519     P                 b
016100150519     D                 pi            16a   varying
016200150519     D  i_string                     16a   const varying
016300150519
016400150519     D formatted       s             16a   varying
016500150519      /free
016600150519
016700150519         formatted = %trim(i_string);
016800150519         dow (%len(formatted) < 2);
016900150519            formatted = '0' + formatted;
017000150519         enddo;
017100150519
017200150519         return formatted;
017300150519
017400150519      /end-free
017500150519     P                 e
017600150519
