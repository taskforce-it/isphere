      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2016 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // Data structure for format 'RTVM0100' for procedure 'QMHRTVM'
     d RTVM0100_t      DS                  Qualified based(pTemplate)
     d  ByteRtn                      10I 0                                      <-- Bytes returned
     d  ByteAvl                      10I 0                                      <-- Bytes available
     d  LenMsgRtn                    10I 0                                      <-- Length of messag
     d  LenMsgAvl                    10I 0                                      <-- Length of messag
     d  LenMsgHRtn                   10I 0                                      <-- Length of messag
     d  LenMsgHAvl                   10I 0                                      <-- Length of messag
     d  Data                       4096A                                        <-- Varriable area

