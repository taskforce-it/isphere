      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners.                                          *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //* Contributors:                                                                            *
      //*     Task Force IT-Consulting GmbH - initial API and implementation                       *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Build iSphere Library - Set version number                                               *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<   DFTACTGRP(*NO);                                                       *
      //*         >>PARM<<   ACTGRP(*CALLER);                                                      *
      //*         >>PARM<<   TRUNCNBR(*NO);                                                        *
      //*         >>PARM<<   DBGVIEW(*LIST);                                                       *
      //*         >>PARM<<   TGTRLS(&TR);                                                          *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*
     h bnddir('QC2LE')
      /copy qcpysrc,h_spec
      /define VERSION_INFO
      /copy qcpysrc,copyright

     D splitted_t      ds           144    qualified based(pTemplate)
     D  value                        16a   varying dim(8)

      //*------------------------------------------------------------------------------------------*

     D pgm_entry_point...
     D                 pr
     D                                     extpgm('BUILD_VER')
     D  gi_prdlib                    10a   const

     D main...
     D                 pr
     D                                     extproc('main')
     D  i_prdlib                     10a   const
     D  i_server_ver                128a   const varying
     D  i_client_ver                128a   const varying
     D  i_build_date                128a   const varying

     D splitt...
     D                 pr                  likeds(splitted_t)
     D                                     extproc('splitt')
     D  i_string                    128a   const varying

     D format...
     D                 pr            16a   varying
     D                                     extproc('format')
     D  i_string                     16a   const varying

     D gi_dataArea     s             21a   varying
     D iSphere         ds           256    dtaara(*var: gi_dataArea) inz
     D                                     qualified
     D  server_id              1      7a   inz('Server:')
     D  server_ver             8     13a
     D  client_id             15     21a   inz('Client:')
     D  client_ver            22     27a
     D  build_date_id         29     39a   inz('Build-date:')
     D  build_date            40     49a

      //*------------------------------------------------------------------------------------------*
      //*  Program Entry Point
      //*------------------------------------------------------------------------------------------*
     D pgm_entry_point...
     D                 pi
     D  gi_prdlib                    10a   const
      /free

         *inlr = *on;
         main(gi_prdlib
              : ISPHERE_SERVER_VERSION
              : ISPHERE_MIN_CLIENT_VERSION
              : ISPHERE_DATE);

      /end-free

      //*------------------------------------------------------------------------------------------*
      //*  Main
      //*------------------------------------------------------------------------------------------*
     P main...
     P                 b
     D                 pi
     D  i_prdlib                     10a   const
     D  i_server_ver                128a   const varying
     D  i_client_ver                128a   const varying
     D  i_build_date                128a   const varying

     D i               s             10i 0
     D splitted        ds                  likeds(splitted_t) inz
     D server_version  s             16a   varying inz
     D client_version  s             16a   varying inz
      /free

         splitted = splitt(i_server_ver);

         i = 1;
         dow (i <= %elem(splitted.value) and splitted.value(i) <> '');
            server_version += format(splitted.value(i));
            i = i + 1;
         enddo;

         splitted = splitt(i_client_ver);

         i = 1;
         dow (i <= %elem(splitted.value) and splitted.value(i) <> '');
            client_version += format(splitted.value(i));
            i = i + 1;
         enddo;

         gi_dataArea = %trim(i_prdlib) + '/' + 'ISPHERE';
         in *lock iSphere;
         reset iSphere;
         iSphere.server_ver = server_version;
         iSphere.client_ver = client_version;
         iSphere.build_date = i_build_date;
         out iSphere;

      /end-free
     P                 e

      //*------------------------------------------------------------------------------------------*
      //*  Splitts a given string delimited by dots (.) into pieces.
      //*------------------------------------------------------------------------------------------*
     P splitt...
     P                 b
     D                 pi                  likeds(splitted_t)
     D  i_string                    128a   const varying

     D i               s             10i 0
     D pToken          s               *
     D splitted        ds                  likeds(splitted_t) inz

     D strtok          PR              *          extproc('strtok')
     D  i_string                       *   value  options(*string)
     D  i_delimiters                   *   value  options(*string)
      /free

         i = 0;
         pToken = strtok(i_string: '.');
         dow (pToken <> *NULL and i < %elem(splitted.value));
            i = i + 1;
            splitted.value(i) = %str(pToken);
            pToken = strtok(*null: '.');
         enddo;

         return splitted;

      /end-free
     P                 e

      //*------------------------------------------------------------------------------------------*
      //*  Formats a given string (representing an integer value)
      //*  to a 2-digit string with leading zeros.
      //*------------------------------------------------------------------------------------------*
     P format...
     P                 b
     D                 pi            16a   varying
     D  i_string                     16a   const varying

     D formatted       s             16a   varying
      /free

         formatted = %trim(i_string);
         dow (%len(formatted) < 2);
            formatted = '0' + formatted;
         enddo;

         return formatted;

      /end-free
     P                 e

