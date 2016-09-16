      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'MBRL0330' for procedure 'QUSLMBR'
     dMBRL0330         DS                  Based(MBRL0330Ptr)
     d MBRLMbr                       10A                                        <-- Member name used
     d MBRLRsrvd1                     2A                                        <-- Reserved
     d MBRLOffMbrInf                 10I 0                                      <-- Offset to member
     d MBRLRsrvd2                    16A                                        <-- Reserved
     dMBRL0330Ptr      S               *                                        Pointer to generic h
