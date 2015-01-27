      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,types

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
     d  bytRtn                       10i 0
     d  bytAvl                       10i 0
     d  numMsgRtn                    10i 0
     d  firstMsgId                    7a
     d  lastMsgId                     7a
     d  reserved                      2a
     d  offsFirstMsgD                10i 0

