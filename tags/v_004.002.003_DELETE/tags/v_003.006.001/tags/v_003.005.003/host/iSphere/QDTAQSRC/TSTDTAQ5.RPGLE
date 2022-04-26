      //* --------------------------------------------------------------------- */
      //*                                                                       */
      //* Copyright (c) 2012-2016 iSphere Project Owners                        */
      //* All rights reserved. This program and the accompanying materials      */
      //* are made available under the terms of the Common Public License v1.0  */
      //* which accompanies this distribution, and is available at              */
      //* http://www.eclipse.org/legal/cpl-v10.html                             */
      //*                                                                       */
      //* --------------------------------------------------------------------- */
      //*                                                                       */
      //* iSphere Test Data Queue - Complex Keys                                */
      //*                                                                       */
      //* --------------------------------------------------------------------- */
      //* STRPREPRC Compile Options:                                            */
      //*   >>PRE-COMPILER<<                                                    */
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(QTEMP/&OB) +                         */
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
      //*       >>COMPILE<<                                                     */
      //*         >>PARM<<  TEXT('Creates: iSphere Test Data Queue - Cplx Msg');*/
      //*         >>PARM<<  DBGVIEW(*LIST);                                     */
      //*         >>PARM<<  TGTRLS(&TR);                                        */
      //*       >>END-COMPILE<<                                                 */
      //*       >>EXECUTE<<                                                     */
      //*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
      //*   >>END-PRE-COMPILER<<                                                */
      //* --------------------------------------------------------------------- */
     h debug
     h dftactgrp(*no) actgrp(*caller)
     h option(*srcstmt: *nodebugio)
     h main(MAIN)

      // Send Data Queue (QSNDDTAQ) API
     d QSNDDTAQ...
     d                 pr                  extpgm('QSNDDTAQ')
     d  i_dtaQ                       10a   const
     d  i_lib                        10a   const
     d  i_length                      5P 0 const
     d  i_data                    65535a   const  options(*varsize)
     d  i_lenKey                      3P 0 const  options(*nopass)              |OptGrp 1
     d  i_key                     65535a   const  options(*nopass: *varsize)    |
     d  i_asyncReq                   10a   const  options(*nopass)              |OptGrp 2
     d  i_dataOfJrnE                 10a   const  options(*nopass)              |OptGrp 3

      // Execute Command (QCMDEXC) API
     d QCMDEXC...
     d                 pr                  extpgm('QCMDEXC')
     d  i_cmd                     32702A   const  options(*varsize)
     d  i_length                     15P 5 const
     d  i_IGCprcCtrl                  3a   const  options(*nopass)

     d QCMDEXC_IGC     c                   'IGC'

     d main...
     d                 pr                  extpgm('TSTDTAQ1B')
     d  i_lib                        10a   const
     d  i_dtaQ                       10a   const

     p main...
     p                 b
     d                 pi
     d  i_lib                        10a   const
     d  i_dtaQ                       10a   const

     d cmd             s            512a   varying
     d key             s              7a   varying
     d data            s           1024a   varying
      /free

         monitor;
            cmd = 'DLTDTAQ DTAQ(' + %trim(i_lib) + '/' + %trim(i_dtaQ) + ')';
            QCMDEXC(cmd: %len(cmd));
         on-error;
         endmon;

         cmd = 'CRTDTAQ DTAQ(' + %trim(i_lib) + '/' + %trim(i_dtaQ) + ') +
                  TYPE(*STD) MAXLEN(800) KEYLEN(7) SEQ(*KEYED) SENDERID(*YES) +
                  AUTORCL(*YES) SIZE(32 16) +
                  TEXT(''iSphere Test Data Queue - Complex Keys'')';
         QCMDEXC(cmd: %len(cmd));

         key = 'K:' + x'0000000001';
         data = '*PDFWTR   0101NC_EMAIL  HOWARDP   597277CLMFRM    '  +
                x'000000000001'                                       +
                x'4040404040404040'                                   +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                '/home/raddatz/wsdl2rpg'                              +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'40404040404040404040404040404040404040404040404040' +
                x'404040404040'                                       +
                'RADDATZ   ';


         QSNDDTAQ(i_dtaQ: i_lib: %len(data): data: %len(key): key);

      /end-free
     p                 e

