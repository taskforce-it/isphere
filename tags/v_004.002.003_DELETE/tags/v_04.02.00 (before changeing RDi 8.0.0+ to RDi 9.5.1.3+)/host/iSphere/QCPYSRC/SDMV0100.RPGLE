      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2016 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     d SDMV_VIEW_TYPE_LISTING...
     d                 c                   '*LISTING'
     d SDMV_VIEW_TYPE_TEXT...
     d                 c                   '*TEXT'
     d SDMV_VIEW_TYPE_STATEMENT...
     d                 c                   '*STATEMENT'

      // Data structure for format 'SDMV0100' for procedure 'IQSDRTVMV'
     d sdmv0100_t      DS                  Qualified based(pTemplate)
     d  offsNextView           1      4i 0
     d  module                 5     14a
     d  viewType              15     24a
     d  compilerId            25     44a
     d  mainInd               45     54a
     d  viewTmstmp            55     67a
     d  viewDesc              68    117a
     d  reserved_1           118    120a
     d  viewNumber           121    124i 0
     d  numOfViews           125    128i 0

