      /if not defined(iqsdrtvvt)
      /define iqsdrtvvt
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

      // Procedure prototype for procedure 'IQSDRTVVT'
     d IQSDRTVVT...
     d                 pr                  extpgm('IQSDRTVVT')
     d  go_rcvVar                 32767a         options(*varsize)              <-- Text lines
     d  gi_lenRcvVar                 10i 0 const                                --> Length of rcvVar
     d  gi_viewId                    10i 0 const                                --> View id
     d  gi_startLine                 10i 0 const                                --> Start line
     d  gi_numLines                  10i 0 const                                --> Number of lines
     d  gi_lineLength                10i 0 const                                --> Lenght of line
     d  gio_errCode               32767a         options(*nopass: *varsize)     <-> Error code

     d iqsdrtvvt_header_t...
     d                 ds                  qualified based(pTemplate)
     D  bytRtn                 1      4i 0
     d  bytAvl                 5      8i 0
     d  numLinesRtn            9     12i 0
     d  numLinesAvl           13     16i 0
     d  lineLength            17     20i 0
     d  firstLine             21     24i 0
     d  lastLine              25     28i 0
     d  lenLineE              29     32i 0
     d  offsFirstLine         33     36i 0

     d IQSDRTVVT_HEADER_MIN_SIZE...
     d                 c                   8
     d IQSDRTVVT_MIN_SIZE_NUM_LINES_RTN...
     d                 c                   12
     d IQSDRTVVT_MIN_SIZE_NUM_LINES_AVL...
     d                 c                   16
     d IQSDRTVVT_MIN_SIZE_LINE_LENGTH...
     d                 c                   20
     d IQSDRTVVT_MIN_SIZE_FIRST_LINE...
     d                 c                   24
     d IQSDRTVVT_MIN_SIZE_LAST_LINE...
     d                 c                   28
     d IQSDRTVVT_MIN_SIZE_OFFS_LEN_LINE_ENTRY...
     d                 c                   32
     d IQSDRTVVT_MIN_SIZE_OFFS_FIRST_LINE...
     d                 c                   36

      /endif
