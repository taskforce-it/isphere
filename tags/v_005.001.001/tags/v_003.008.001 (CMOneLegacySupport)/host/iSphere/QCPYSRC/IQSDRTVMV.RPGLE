      /if not defined(iqsdrtvmv)
      /define iqsdrtvmv
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2018 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,types

     d IQSDRTVMV_ALL...
     d                 c                   '*ALL'

      // Procedure prototype for procedure 'IQSDRTVMV'
     d IQSDRTVMV...
     d                 pr                  extpgm('IQSDRTVMV')
     d  go_rcvVar                 32767a         options(*varsize)              <-- Module views
     d  gi_lenRcvVar                 10i 0 const                                --> Length of rcvVar
     d  gi_format                     8a   const                                --> Format name
     d  gi_qObj                            const likeds(qObj_t)                 --> Qualified object
     d  gi_objType                   10a   const                                --> Object type
     d  gi_module                    10a   const options(*nopass)               --> Module (*ALL)
     d  gio_errCode               32767a         options(*nopass:*omit:*varsize)<-> Error code

     d iqsdrtvmv_header_t...
     d                 ds                  qualified based(pTemplate)
     D  bytRtn                 1      4i 0
     d  bytAvl                 5      8i 0
     d  numViewsRtn            9     12i 0
     d  lenViewE              13     16i 0
     d  offsFirstView         17     20i 0

     d IQSDRTVMV_HEADER_MIN_SIZE...
     d                 c                   8
     d IQSDRTVMV_MIN_SIZE_NUM_VIEWS_RTN...
     d                 c                   12
     d IQSDRTVMV_MIN_SIZE_LEN_VIEW_ENTRY...
     d                 c                   16
     d IQSDRTVMV_MIN_SIZE_FIRST_VIEW...
     d                 c                   20

      /endif
