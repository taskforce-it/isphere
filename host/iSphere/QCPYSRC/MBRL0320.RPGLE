      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2024 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'MBRL0320' for procedure 'QUSLMBR'.
      * MBRL0320 generates an MBRD0200 description.
     dMBRL0320         DS                  Based(MBRL0320Ptr)
     d MBRLMbr                       10A                                        <-- Member name used
     d MBRLRsrvd1                     2A                                        <-- Reserved
     d MBRLOffMbrInf                 10I 0                                      <-- Offset to member
     d MBRLRsrvd2                    16A                                        <-- Reserved
     dMBRL0320Ptr      S               *                                        Pointer to generic h
