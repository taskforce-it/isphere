      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'RTVM0300' for procedure 'QMHRTVM'
     dRTVM0300         DS
     d RTVMByteRtn                   10I 0                                      <-- Bytes returned
     d RTVMByteAvl                   10I 0                                      <-- Bytes available
     d RTVMMsgSvrty                  10I 0                                      <-- Message severity
     d RTVMAltIdx                    10I 0                                      <-- Alert index
     d RTVMAltOpt                     9A                                        <-- Alert option
     d RTVMLogInd                     1A                                        <-- Log indicator
     d RTVMMsgId                      7A                                        <-- Message ID
     d RTVMRsrvd                      3A                                        <-- Reserved
     d RTVMNSVF                      10I 0                                      <-- Number of SVF
     d RTVMCCSID1                    10I 0                                      <-- CCSID CSI of tex
     d RTVMCCSID2                    10I 0                                      <-- CCSID CSI of rep
     d RTVMCCSID3                    10I 0                                      <-- CCSID of text re
     d RTVMOffDR                     10I 0                                      <-- Offset of defaul
     d RTVMLenDRRtn                  10I 0                                      <-- Length of defaul
     d RTVMLenDRAvl                  10I 0                                      <-- Length of defaul
     d RTVMOffMsg                    10I 0                                      <-- Offset of messag
     d RTVMLenMsgRtn                 10I 0                                      <-- Length of messag
     d RTVMLenMsgAvl                 10I 0                                      <-- Length of messag
     d RTVMOffMsgH                   10I 0                                      <-- Offset of messag
     d RTVMLenMsgHRtn                10I 0                                      <-- Length of messag
     d RTVMLenMsgHAvl                10I 0                                      <-- Length of messag
     d RTVMOffSVF                    10I 0                                      <-- Offset of SVF
     d RTVMLenSVFRtn                 10I 0                                      <-- Length of SVF re
     d RTVMLenSVFAvl                 10I 0                                      <-- Length of SVF av
     d RTVMLenSVFElm                 10I 0                                      <-- Length of SVF el
     d RTVMVarAra                  4096A                                        <-- Varriable area
      *
      * Datenstruktur für Format 'RTVM0300' für Prozedur 'QMHRTVM' (Substitution Variable Format)
      *
     dRTVM0300SVF      DS                  Based(RTVM0300SVFPtr)
     d RTVMSVFLenRplDta...
     d                               10I 0                                      <-- Length of replac
     d RTVMSVFFldSizDecPos...
     d                               10I 0                                      <-- Field size or de
     d RTVMSVFSVType                 10A                                        <-- Substitution var
      *
     dRTVM0300SVFPtr   S               *                                        Pointer
