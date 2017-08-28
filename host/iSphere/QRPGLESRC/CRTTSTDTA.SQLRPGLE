      //*------------------------------------------------------------------------------------------*
      //* Create test data: TYPES_SQL and TYPES_DDS                                                *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2017 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Create test data: TYPES_SQL and TYPES_DDS                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* Prerequisites:                                                                           *
      //*  - Compile table TYPES_SQL                                                               *
      //*  - Compile table TYPES_DDS                                                               *
      //*  - Create a journal                                                                      *
      //*  - Start journaling for TYPES_SQL and TYPE_DDS                                           *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<< CRTSQLRPGI   OBJ(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                   *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  OBJTYPE(*PGM);                                                         *
      //*         >>PARM<<  COMMIT(*NONE);                                                         *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*         >>PARM<<  CLOSQLCSR(*ENDMOD);                                                    *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  RPGPPOPT(*LVL2);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');                          *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     h Main(main) dftactgrp(*no) actgrp(*new)
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

     d main...
     d                 pr                                                       <-- Handle
     d                                     extpgm('CRTTSTDTA')

     d clCommand...
     d                 pr             7a   varying
     d                                     extproc('clCommand')
     d  i_cmd                       512a   varying

     d getTime...
     d                 pr            20a   varying
     d                                     extproc('getTime')
     d  i_timestamp                    z

      // system -- Execute a Command
      //    Return value       Meaning
      //    ----------------   --------------------------------------
      //    0                  the command is successful
      //     -1                 for a NULL pointer
      //    1                  the command fails
     d system          pr            10i 0 extproc('system')
     d  i_clCommand                    *   value options(*string)

     d EXCP_MSGID      s              7a   import('_EXCP_MSGID')

      //*==========================================================================================*
      //* Main procedure                                                                           *
      //*==========================================================================================*

     p main...
     p                 b
     d                 pi                                                       <-- Handle

     d count           s             10i 0
     d cmd             s            512a   varying
     d start_time      s               Z
     d end_time        s               Z
      /free

       start_time = %timestamp();

       // Clear table
       exec sql
          DELETE FROM TYPES_SQL;

       // Insert some test data
       for count = 1 to 10;

       exec sql
       INSERT INTO TYPES_SQL(
          S_SMALLINT,
          S_INTEGER,
          S_BIGINT,
          S_FLOAT,
          S_DECFLOAT,
          S_REAL,
          S_DOUBLE,
          S_NUMERIC,
          S_DECIMAL,
          S_CHAR273,
          S_CHAR37,
          S_VARCHAR,
          S_BINARY,
          S_VARBIN,
          S_DATE,
          S_TIME,
          S_TMSTMP,
          S_GRAPHIC,
          S_VARGRA,
          S_DTLINK,
          S_CLOB,
          S_BLOB,
          S_NULLABLE)
       VALUES (
          2,
          :count,
          8,
          99,
          999,
          44,
          4444,
          666.66,
          777.77,
          '<Char-273-öäüÖÄÜß-->',
          '<Char-37-öäüÖÄÜß--->',
          '<Varchar 100-bytes>',
          CAST(x'89E2978885998540404040404040404040404040' AS BINARY(20)),
          CAST(x'89E2978885998540404040404040404040404040' AS VARBINARY(20)),
          DATE(NOW()),
          TIME(NOW()),
          '1970-01-01-23.59.59.999999',
          '<Graphic 20-bytes-->',
          '<Vargraphic 20byte>',
          DLVALUE('https://www.ixquick.com/deu'),
          CLOB('The quick brown fox jumps over the lazy dog.'),
          BLOB('The quick brown fox jumps over the lazy dog.'),
          NULL);

          endfor;

       // Do some updates
       exec sql
          UPDATE TYPES_SQL
             SET S_DTLINK = DLVALUE('https://www.google.de/en')
             WHERE S_INTEGER IN (2, 4, 6, 8, 10);

       // Clear table
       exec sql
          DELETE FROM TYPES_DDS;

       // Insert some test data
       for count = 1 to 10;

       exec sql
       INSERT INTO TYPES_DDS(
          D_BIN1,
          D_BIN2,
          D_BIN4,
          D_BIN8,
          D_REAL,
          D_DOUBLE,
          D_ZONED,
          D_PACKED,
          D_CHAR273,
          D_CHAR37,
          D_VARCHAR,
          D_BINARY,
          D_VARBIN,
          D_DATE_ISO,
          D_TIME_ISO,
          D_TMSTMP,
          D_DATE_EUR,
          D_DATE_JUL,
          D_TIME_HMS,
          D_GRAPHIC,
          D_VARGRA,
          D_BOOL,
          D_NULLABLE)
       VALUES (
          1,
          2,
          :count,
          8,
          44,
          4444,
          666.66,
          777.77,
          '<Char-273-öäüÖÄÜß-->',
          '<Char-37-öäüÖÄÜß--->',
          '<Varchar 100-bytes>',
          CAST(x'89E2978885998540404040404040404040404040' AS BINARY(20)),
          CAST(x'89E2978885998540404040404040404040404040' AS VARBINARY(100)),
          DATE(NOW()),
          TIME(NOW()),
          '1970-01-01-23.59.59.999999',
          '01.01.1970',
          '70.001',
          '23:59:59',
          '<Graphic 20-bytes-->',
          '<Vargraphic 20byte>',
          '1',
          NULL
          );

       endfor;

       // Do some updates
       exec sql
          UPDATE TYPES_DDS
             SET D_VARCHAR = 'The quick brown fox jumps over the lazy dog.'
             WHERE D_BIN4 IN (2, 4, 6, 8, 10);

       // Create DSPJRN output files
       end_time = %timestamp();

       cmd = 'DSPJRN JRN(ISPHEREDVP/JRN) +
                FILE((ISPHEREDVP/TYPES_DDS) (ISPHEREDVP/TYPES_SQL)) +
                FROMTIME(' + getTime(start_time) + ') +
                TOTIME(' + getTime(end_time) + ') +
                ENTTYP(*RCD) +
                OUTPUT(*OUTFILE) OUTFILFMT(*TYPE1) OUTFILE(ISPHEREDVP/TYPES_1) +
                ENTDTALEN(5000)';
       clCommand(cmd);

       cmd = 'DSPJRN JRN(ISPHEREDVP/JRN) +
                FILE((ISPHEREDVP/TYPES_DDS) (ISPHEREDVP/TYPES_SQL)) +
                FROMTIME(' + getTime(start_time) + ') +
                TOTIME(' + getTime(end_time) + ') +
                ENTTYP(*RCD) +
                OUTPUT(*OUTFILE) OUTFILFMT(*TYPE2) OUTFILE(ISPHEREDVP/TYPES_2) +
                ENTDTALEN(5000)';
       clCommand(cmd);

       cmd = 'DSPJRN JRN(ISPHEREDVP/JRN) +
                FILE((ISPHEREDVP/TYPES_DDS) (ISPHEREDVP/TYPES_SQL)) +
                FROMTIME(' + getTime(start_time) + ') +
                TOTIME(' + getTime(end_time) + ') +
                ENTTYP(*RCD) +
                OUTPUT(*OUTFILE) OUTFILFMT(*TYPE3) OUTFILE(ISPHEREDVP/TYPES_3) +
                ENTDTALEN(5000)';
       clCommand(cmd);

       cmd = 'DSPJRN JRN(ISPHEREDVP/JRN) +
                FILE((ISPHEREDVP/TYPES_DDS) (ISPHEREDVP/TYPES_SQL)) +
                FROMTIME(' + getTime(start_time) + ') +
                TOTIME(' + getTime(end_time) + ') +
                ENTTYP(*RCD) +
                OUTPUT(*OUTFILE) OUTFILFMT(*TYPE4) OUTFILE(ISPHEREDVP/TYPES_4) +
                ENTDTALEN(5000)';
       clCommand(cmd);

       cmd = 'DSPJRN JRN(ISPHEREDVP/JRN) +
                FILE((ISPHEREDVP/TYPES_DDS) (ISPHEREDVP/TYPES_SQL)) +
                FROMTIME(' + getTime(start_time) + ') +
                TOTIME(' + getTime(end_time) + ') +
                ENTTYP(*RCD) +
                OUTPUT(*OUTFILE) OUTFILFMT(*TYPE5) OUTFILE(ISPHEREDVP/TYPES_5) +
                ENTDTALEN(5000)';
       clCommand(cmd);


      /end-free
     p                 e

      //*==========================================================================================*
      //* Returns the date and time string formatted for the DSPJRN command.                       *
      //*==========================================================================================*

     p getTime...
     p                 b
     d                 pi            20a   varying
     d  i_timestamp                    z

     d date            s             10a   varying
     d time            s             10a   varying
     d tDateTime       s             20a   varying
     d dateTime        s             20a   varying
     d char            s              1a
     d i               s             10i 0
      /free

         date = %char(%date(i_timestamp): *jobrun);
         time = %char(%time(i_timestamp): *jobrun);

         tDateTime = date + ' ' + time;

         for i = 1 to %len(tDateTime);
            char = %subst(tDateTime: i: 1);
            if (%check(' 0123456789': char) = 0);
               dateTime = dateTime + char;
            endif;
         endfor;

         return dateTime;

      /end-free
     p                 e

      //*==========================================================================================*
      //* Executes a CL command                                                                    *
      //*==========================================================================================*

     p clCommand...
     p                 b
     d                 pi             7a   varying
     d  i_cmd                       512a   varying

     d rc              s             10i 0
      /free

         rc = system(i_cmd);
         if (rc = 0);
            return '';
         endif;

         return EXCP_MSGID;

      /end-free
     p                 e

