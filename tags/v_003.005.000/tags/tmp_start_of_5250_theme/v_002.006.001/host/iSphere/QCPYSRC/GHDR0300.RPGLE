      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'GHDR0300' for generic header
     dGHDR0300         DS                  Based(GHDR0300Ptr)
     d GHDRUsrAra                    64A                                        <-- User area
     d GHDRSizeGenHdr                10I 0                                      <-- Size of generic
     d GHDRSctRelLvl                  4A                                        <-- Structure's rele
     d GHDRFmt                        8A                                        <-- Format name
     d GHDRAPIUsed                   10A                                        <-- API used
     d GHDRCrtTime                   13A                                        <-- Date and time cr
     d GHDRInfSts                     1A                                        <-- Information stat
     d GHDRSizeUsrSpc                10I 0                                      <-- Size of user spa
     d GHDROffInpSct                 10I 0                                      <-- Offset to input
     d GHDRSizeInpSct                10I 0                                      <-- Size of input pa
     d GHDROffHdrSct                 10I 0                                      <-- Offset to header
     d GHDRSizeHdrSct                10I 0                                      <-- Size of header s
     d GHDROffLstSct                 10I 0                                      <-- Offset to list d
     d GHDRSizeLstSct                10I 0                                      <-- Size of list dat
     d GHDRNbrEtry                   10I 0                                      <-- Number of list e
     d GHDRSizeEtry                  10I 0                                      <-- Size of each ent
     d GHDRCCSID                     10I 0                                      <-- CCSID of data in
     d GHDRCntryId                    2A                                        <-- Country ID
     d GHDRLngId                      3A                                        <-- Language ID
     d GHDRSubLstInd                  1A                                        <-- Subsetted list i
     d GHDRRsrvd1                    42A                                        <-- Reserved
     d GHDRAPIEtyPtr                256A                                        <-- API entry point
     d GHDRRsrvd2                   128A                                        <-- Reserved
     dGHDR0300Ptr      S               *                                        Pointer to generic h
