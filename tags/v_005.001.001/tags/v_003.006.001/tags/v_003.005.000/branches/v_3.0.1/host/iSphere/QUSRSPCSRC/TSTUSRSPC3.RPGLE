      // ---------------------------------------------------------------------
      //
      // Copyright (c) 2012-2014 iSphere Project Owners
      // All rights reserved. This program and the accompanying materials
      // are made available under the terms of the Common Public License v1.0
      // which accompanies this distribution, and is available at
      // http://www.eclipse.org/legal/cpl-v10.html
      //
      // ---------------------------------------------------------------------
      //
      // iSphere Version
      //
      // ---------------------------------------------------------------------
      // STRPREPRC Compile Options:
      //   >>PRE-COMPILER<<
      //     >>CRTCMD<<    CRTBNDRPG  PGM(QTEMP/&OB) +
      //                              SRCFILE(&SL/&SF) SRCMBR(&SM);
      //       >>COMPILE<<
      //         >>PARM<<  TEXT('Creates: iSphere Test User Space - 16 MB');
      //         >>PARM<<  DBGVIEW(*LIST);
      //         >>PARM<<  OPTION(*EVENTF);
      //       >>END-COMPILE<<
      //       >>EXECUTE<<
      //       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);
      //   >>END-PRE-COMPILER<<
      // ---------------------------------------------------------------------
     D qObj_t          ds                  qualified based(pDummy)
     D  name                         10a
     D  lib                          10a

     D errCode_t       ds                  qualified based(pDummy)
     D  bytPrv                       10i 0
     D  bytAvl                       10i 0

      //  Create User Space (QUSCRTUS) API
     D QUSCRTUS...
     D                 PR                  extpgm('QUSCRTUS')
     D  i_qUsrSpc                    20a   const
     D  i_extAttr                    10a   const
     D  i_initSize                   10i 0 const
     D  i_initValue                   1a   const
     D  i_pubAuth                    10a   const
     D  i_text                       50a   const
     D  i_replace                    10a   const options(*nopass)               | optGrp 1
     D  io_ErrCode                65535a         options(*nopass: *varsize)     |
     D  i_domain                     10a   const options(*nopass)               | optGrp 2
     D  i_tfrSizeReq                 10i 0 const options(*nopass)               | optGrp 3
     D  i_optSpcAlgn                  1a   const options(*nopass)               | optGrp 3

      //  Retrieve Pointer to User Space (QUSPTRUS) API
     D QUSPTRUS...
     D                 PR                  extpgm('QUSPTRUS')
     D  i_qUsrSpc                    20a   const
     D  o_pUsrSpc                      *
     D  io_ErrCode                65535a         options(*nopass: *varsize)     | optGrp 1

     D main            pr                  extpgm('TSTUSRSPC1')
     D  i_lib                        10a   const
     D  i_obj                        10a   const

     D main            pi
     D  i_lib                        10a   const
     D  i_obj                        10a   const

     D qusrSpc         ds                  likeds(qObj_t) inz
     D errCode         ds                  likeds(errCode_t) inz

     D spaceSize       s             10i 0
      /free

         // Create user space
         qUsrSpc.name = i_obj;
         qUsrSpc.lib = i_lib;
         clear errCode;
         spaceSize = 16 * 1024 * 1024;
         QUSCRTUS(qUsrSpc: 'TEST': %size(spaceSize): x'00': '*USE'
                  : 'iSphere Test User Space - All Types': '*YES': errCode);

         *inlr = *ON;

      /end-free

