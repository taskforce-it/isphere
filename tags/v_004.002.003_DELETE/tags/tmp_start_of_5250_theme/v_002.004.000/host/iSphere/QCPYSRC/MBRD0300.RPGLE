      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'MBRD0300' for procedure 'QUSRMBRD'
     dMBRD0300         DS
     d MBRDByteRtn                   10I 0                                      <-- Bytes returned
     d MBRDByteAvl                   10I 0                                      <-- Bytes available
      * General information
     dMBRD0300A        DS                  Based(MBRD0300APtr)
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
     d MBRDRsrvd4                    14A                                        <-- Reserved
     d MBRDJoinMbr                    1A                                        <-- Join member
     d MBRDAccPthMnt                  1A                                        <-- Access path main
     d MBRDSQLType                   10A                                        <-- SQL file type
     d MBRDRsrvd5                     1A                                        <-- Reserved
     d MBRDAlwRead                    1A                                        <-- Allow read opera
     d MBRDAlwWrite                   1A                                        <-- Allow write oper
     d MBRDAlwUpdate                  1A                                        <-- Allow update ope
     d MBRDAlwDelete                  1A                                        <-- Allow delete ope
     d MBRDRsrvd6                     1A                                        <-- Reserved
     d MBRDRcdFrc                    10I 0                                      <-- Records to force
     d MBRDMaxDltRcd                 10I 0                                      <-- Maximum  percent
     d MBRDIniNbrRcd                 10I 0                                      <-- Initial number o
     d MBRDIncNbrRcd                 10I 0                                      <-- Increment number
     d MBRDMaxNbrInc                 10I 0                                      <-- Maximum number o
     d MBRDCurNbrInc                 10I 0                                      <-- Current number o
     d MBRDRcdCap                    10I 0                                      <-- Record capacity
     d MBRDRFSPgm                    10A                                        <-- Record format se
     d MBRDRFSPgmLib                 10A                                        <-- Record format se
     d MBRDNbrCstIdx                  5I 0                                      <-- Number of constr
     d MBRDOffCstIdx                 10I 0                                      <-- Offset to constr
     d MBRDRsrvd7                    46A                                        <-- Reserved
     dMBRD0300APtr     S               *
      * Record format and based-on file list
     dMBRD0300B        DS                  Based(MBRD0300BPtr)
     d MBRDBPFFile                   10A                                        <-- Based-on physica
     d MBRDBPFFileLib                10A                                        <-- Based-on physica
     d MBRDBPFMbr                    10A                                        <-- Based-on physica
     d MBRDBPFFmt                    10A                                        <-- Format name
     d MBRDBPFFmtNbr                 10I 0                                      <-- Logical file rec
     d MBRDBPFCurRcd                 10I 0                                      <-- Current number o
     d MBRDBPFDltRcd                 10I 0                                      <-- Number of delete
     d MBRDBPFSize                   10I 0                                      <-- Access path size
     d MBRDBPFSizeMlt                10I 0                                      <-- Access path size
     d MBRDBPFShared                  1A                                        <-- Access path shar
     d MBRDBPFValid                   1A                                        <-- Access path vali
     d MBRDBPFHeld                    1A                                        <-- Access path held
     d MBRDBPFOwnF                   10A                                        <-- Access path owne
     d MBRDBPFOwnFLib                10A                                        <-- Access path owne
     d MBRDBPFOwnMbr                 10A                                        <-- Access path owne
     d MBRDBPFRsrvd8                 19A                                        <-- Reserved
     dMBRD0300BPtr     S               *
      * Constraint indexes information
     dMBRD0300C        DS                  Based(MBRD0300CPtr)
     d MBRDCnsLib                    10A                                        <-- Constraint libra
     d MBRDCnsLngName                 5I 0                                      <-- Constraint name
     d MBRDCnsName                  258A                                        <-- Constraint name
     d MBRDCnsSize                   10I 0                                      <-- Access path size
     d MBRDCnsSizeMlt                10I 0                                      <-- Access path size
     d MBRDCnsShared                  1A                                        <-- Access path shar
     d MBRDCnsValid                   1A                                        <-- Access path vali
     d MBRDCnsHeld                    1A                                        <-- Access path held
     d                                4A
     d MBRDCnsNbrLGL                 10I 0                                      <-- Number of constr
     d                                4A
     d MBRDCnsNbrPHY                 10I 0                                      <-- Number of constr
     d MBRDCnsRsrvd9                 23A                                        <-- Reserved
     dMBRD0300CPtr     S               *
      * Additional Information
     dMBRD0300D        DS                  Based(MBRD0300DPtr)
     d                                4A
     d MBRDSPCAct                    10I 0                                      <-- Number of activa
     d                                4A
     d MBRDSPCDeAct                  10I 0                                      <-- Number of deacti
     d                                4A
     d MBRDSPCIns                    10I 0                                      <-- Number of insert
     d                                4A
     d MBRDSPCUpd                    10I 0                                      <-- Number of update
     d                                4A
     d MBRDSPCDlt                    10I 0                                      <-- Number of delete
     d                                4A
     d MBRDSPCReset                  10I 0                                      <-- Number of reset
     d                                4A
     d MBRDSPCCpy                    10I 0                                      <-- Number of copy o
     d                                4A
     d MBRDSPCRgz                    10I 0                                      <-- Number of reorga
     d                                4A
     d MBRDSPCBld                    10I 0                                      <-- Number of access
     d                                4A
     d MBRDSPCLRead                  10I 0                                      <-- Number of logica
     d                                4A
     d MBRDSPCPRead                  10I 0                                      <-- Number of physic
     d                                4A
     d MBRDSPCRjcKey                 10I 0                                      <-- Number of record
     d                                4A
     d MBRDSPCRjcNKey                10I 0                                      <-- Number of record
     d                                4A
     d MBRDSPCRjcGrp                 10I 0                                      <-- Number of record
     d MBRDSPCRsrvd10                80A                                        <-- Reserved
     d                                4A
     d MBRDIDXLAccPth                10I 0                                      <-- Number of logica
     d                                4A
     d MBRDIDXPAccPth                10I 0                                      <-- Number of physic
     d                                4A
     d MBRDIDXKey1                   10I 0                                      <-- Number of unique
     d                                4A
     d MBRDIDXKey2                   10I 0                                      <-- Number of unique
     d                                4A
     d MBRDIDXKey3                   10I 0                                      <-- Number of unique
     d                                4A
     d MBRDIDXKey4                   10I 0                                      <-- Number of unique
     d MBRDIDXRsrvd11                16A                                        <-- Reserved
     dMBRD0300DPtr     S               *
