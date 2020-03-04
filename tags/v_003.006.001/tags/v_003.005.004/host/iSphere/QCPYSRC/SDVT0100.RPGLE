      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2016 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // Data structure for format 'SDVT0100' for procedure 'IQSDRTVVT'
     d sdvt0100_t      ds                  qualified based(pTemplate)
     d  offsNextLine                 10i 0
     d  lineLength                   10i 0
     d  offsLine                     10i 0
      // text char(*)

