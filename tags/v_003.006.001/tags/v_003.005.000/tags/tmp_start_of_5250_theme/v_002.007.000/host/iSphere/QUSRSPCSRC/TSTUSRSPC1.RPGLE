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
      //         >>PARM<<  TEXT('Creates: iSphere Test User Space - All Types');
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

     D usrSpcDta       ds                  qualified
     D  text                   1     12a   inz('Text:')
     D  textValue             51    100a   inz('The quick brown fox jumps +
     D                                          over the lazy dog.')
     D  long                 101    112a   inz('Long:')
     D  textLong             114    121i 0 inz(x'0000000F0000000F')
     D  integer              151    162a   inz('Integer:')
     D  textInteger          164    167i 0 inz(x'000F000F')
     D  short                201    212a   inz('Short:')
     D  textShort            214    215i 0 inz(x'0F0F')
     D  tiny                 251    262a   inz('Tiny:')
     D  textTiny             264    264i 0 inz(x'37')
     D  boolean              301    312a   inz('Boolean:')
     D  textBoolean          314    314n   inz('1')
     D  decimal              351    362a   inz('Boolean:')
     D  textDecimal          364    369s 2 inz(1234.12)
     D  listInt              401    425a   inz('List on int Values:')

     D numIntValues    s             10s 0 based(pNumIntValues)

     D usrSpc          s           4096a   based(pUsrSpc)
     D intValue        s             10i 0 based(pIntValue)
     D count           s             10s 0
     D offset          s             10i 0
      /free

         // Create user space
         qUsrSpc.name = i_obj;
         qUsrSpc.lib = i_lib;
         clear errCode;
         QUSCRTUS(qUsrSpc: 'TEST': %size(usrSpc): x'00': '*USE'
                  : 'iSphere Test User Space - All Types': '*YES': errCode);

         // Set user space values
         QUSPTRUS(qusrSpc: pUsrSpc: errCode);
         usrSpc = usrSpcDta;

         // Set list of integer value
         count = 0;
         offset = 450;
         dow (count < 256);
            pIntValue = pUsrSpc + offset;
            intValue = count;
            offset += %size(intValue);
            count += 1;
         enddo;

         pNumIntValues = pUsrSpc + %size(usrSpcDta);
         numIntValues = count;

         *inlr = *ON;

      /end-free

