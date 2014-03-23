      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Data structure for format 'SPLF0300' for procedure 'QUSLSPL'
     dSPLF0300         DS                  Based(SPLF0300Ptr)
     d SPLFJobName                   10A                                        <-- Job name
     d SPLFJobUsr                    10A                                        <-- User name
     d SPLFJobNbr                     6A                                        <-- Job number
     d SPLFSplF                      10A                                        <-- Spooled file
     d SPLFSplFNbr                   10I 0                                      <-- Spooled file nbr
     d SPLFSts                       10I 0                                      <-- Status
     d SPLFCrtDate                    7A                                        <-- Creation date
     d SPLFCrtTime                    6A                                        <-- Creation time
     d SPLFScd                        1A                                        <-- Schedule
     d SPLFSys                       10A                                        <-- System name
     d SPLFUsrDta                    10A                                        <-- User data
     d SPLFFormType                  10A                                        <-- Form type
     d SPLFOutQ                      10A                                        <-- Output queue
     d SPLFOutQLib                   10A                                        <-- Output queue lib
     d SPLFASPNbr                    10I 0                                      <-- ASP Number
     d SPLFSize                      10I 0                                      <-- Size
     d SPLFSizeMult                  10I 0                                      <-- Size multiplier
     d SPLFPages                     10I 0                                      <-- Pages
     d SPLFCopies                    10I 0                                      <-- Copies
     d SPLFOutPty                     1A                                        <-- Output priority
     d SPLFRsrvd                      3A                                        <-- Reserved
     d SPLFIPPJobId                  10I 0                                      <-- IPP job id
     dSPLF0300Ptr      S               *                                        Pointer to generic h
