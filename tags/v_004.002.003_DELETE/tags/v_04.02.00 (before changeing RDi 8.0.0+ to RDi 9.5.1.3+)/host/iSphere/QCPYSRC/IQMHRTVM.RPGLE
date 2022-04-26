      /if not defined(iqmhrtvm)
      /define iqmhrtvm
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

     d IQMHRTVM_ALL...
     d                 c                   -1

     d IQMHRTVM_FIRST...
     d                 c                   '*FIRST'
     d IQMHRTVM_NEXT...
     d                 c                   '*NEXT'
     d IQMHRTVM_MSGID...
     d                 c                   '*MSGID'

      // Procedure prototype for procedure 'IQMHRTVM'
     d IQMHRTVM...
     d                 pr                  extpgm('IQMHRTVM')
     d  gi_msgInf                 32767a         options(*varsize)              <-- Message informat
     d  gi_lenMsgInf                 10i 0 const                                --> Length of messag
     d  gi_format                     8a   const                                --> Format name
     d  gi_msgId                      7a   const                                --> Message identifi
     d  gi_qMsgF                           const likeds(qObj_t)                 --> Qualified messag
     d  gi_rtnFmtCtl                 10a   const                                --> Return format co
     d  gio_errCode               32767a         options(*varsize)              <-> Error code
     d  gi_rtvOpt                    10a   const options(*nopass)               --> Retrieve option
     d  gi_numMsgRtn                 10i 0 const options(*nopass)               --> Number of messag
     d  gi_ccsidText                 10i 0 const options(*nopass)               --> CCSID to convert

     d iqmhrtvm_header_t...
     d                 ds                  qualified based(pTemplate)
     D  bytRtn                 1      4i 0
     d  bytAvl                 5      8i 0
     d  numMsgRtn              9     12i 0
     d  firstMsgId            13     19a
     d  lastMsgId             20     26a
     d  reserved              27     28a
     d  offsFirstMsgD         29     32i 0

     d IQMHRTVM_HEADER_MIN_SIZE...
     d                 c                   8
     d IQMHRTVM_MIN_SIZE_NUM_MSG_RTN...
     d                 c                   12
     d IQMHRTVM_MIN_SIZE_FIRST_MSG_ID...
     d                 c                   19
     d IQMHRTVM_MIN_SIZE_LAST_MSG_ID...
     d                 c                   26
     d IQMHRTVM_MIN_SIZE_OFFSET_FIRST_MSG_DESC...
     d                 c                   32

      /endif
