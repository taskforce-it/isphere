      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // Data structure for format 'ERRC0100' for error code
     dERRC0100         DS
     d ERRCBytePrv                   10I 0 Inz(272)                             --> Bytes provided
     d ERRCByteAvl                   10I 0                                      <-- Bytes available
     d ERRCExcId                      7A                                        <-- Exeption-Id.
     d ERRCRsrvd                      1A                                        <-- Reserved
     d ERRCExcDta                   256A                                        <-- Exeption data
