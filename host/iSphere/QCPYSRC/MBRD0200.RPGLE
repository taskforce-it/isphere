      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2024 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'MBRD0200' for procedure 'QUSRMBRD'
     dMBRD0200         DS
     d MBRDByteRtn                   10I 0                                      <-- Bytes returned
     d MBRDByteAvl                   10I 0                                      <-- Bytes available
      * General information
     dMBRD0200A        DS                  Based(MBRD0200APtr)
     d                               10I 0                                      <-- Bytes returned
     d                               10I 0                                      <-- Bytes available
     d MBRDFile                      10A                                        <-- Database file na
     d MBRDFileLib                   10A                                        <-- Database file li
     d MBRDMbr                       10A                                        <-- Member name
     d MBRDFileAtr                   10A                                        <-- File attribute
     d MBRDSrcTyp                    10A                                        <-- Source type
     d MBRDCrtDate                   13A                                        <-- Creation date an
     d MBRDChgDate                   13A                                        <-- Last source chan
     d MBRDText                      50A                                        <-- Member text desc
     d MBRDSrcF                       1A                                        <-- Source file
     d MBRDRmtF                       1A                                        <-- Remote file
     d MBRDLFPF                       1A                                        <-- Logical file or
     d MBRDODPShrng                   1A                                        <-- ODP sharing
     d MBRDRsrvd1                     2A                                        <-- Reserved
     d MBRDNbrCurRcd                 10I 0                                      <-- Current number o
     d MBRDNbrDltRcd                 10I 0                                      <-- Number of delete
     d MBRDDtaSpcSize                10I 0                                      <-- Data space size
     d MBRDAccPthSize                10I 0                                      <-- Access path size
     d MBRDNbrBPF                    10I 0                                      <-- Number of based-
     d MBRDChgTime                   13A                                        <-- Change date and
     d MBRDSavTime                   13A                                        <-- Save date and ti
     d MBRDRstTime                   13A                                        <-- Restore date and
     d MBRDExpDate                    7A                                        <-- Expiration date
     d MBRDRsrvd2                     6A                                        <-- Reserved
     d MBRDNbrDayUsed                10I 0                                      <-- Number of days u
     d MBRDDateUsed                   7A                                        <-- Date last used
     d MBRDUseReset                   7A                                        <-- Use reset date
     d MBRDRsrvd3                     2A                                        <-- Reserved
     d MBRDDtaSpcMlt                 10I 0                                      <-- Data space size
     d MBRDAccSpcMlt                 10I 0                                      <-- Access path size
     d MBRDTxtCCSID                  10I 0                                      <-- Member text desc
     d MBRDOffAddFmt                 10I 0                                      <-- Offset to additi
     d MBRDLngAddFmt                 10I 0                                      <-- Length of additi
     d MBRDNbrRcdAll                 10U 0                                      <-- Current number o
     d MBRDNbrDltRcdU                10U 0                                      <-- Number of delete
     d MBRDRsrvd4                     6A                                        <-- Reserved
     dMBRD0200APtr     S               *

