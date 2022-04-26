      /if not defined(qmhrsnem)
      /define qmhrsnem
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,types

      // Resend Escape Message (QMHRSNEM) API
     D QMHRSNEM...
     D                 pr                  extpgm('QMHRSNEM')
     D  i_msgKey                      4a   const
     D  io_ErrCode                65535a          options(*varsize)
     D  i_callStkE                65535a   const  options(*nopass )             | optGrp 1
     D  i_lenStkE                    10i 0 const  options(*nopass )             |
     D  i_format                      8a   const  options(*nopass )             |
     D  i_frmCallStkE                16a   const  options(*nopass )             |
     D  i_frmCallStkC                10i 0 const  options(*nopass )             |

     D RSNM0100_t      ds                  qualified based(pTemplate)
     D  toStkCnt                     10i 0
     D  toStkEMod                    10a
     D  toStkEPgm                    10a
     D  lenStkEQual                  10i 0
     D  toStkE                       10a

      /endif
