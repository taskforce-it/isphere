      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2017 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // Data structure for format 'RVFD0100' for procedure 'IQDBRTVFD'
     d RVFD0100_t      DS                  Qualified based(pTemplate)
     D  name                   1     10a                                        <-- field name
     d  type                  11     11a                                        <-- data type
     d  use                   12     12a                                        <-- usage
     d  length                13     16i 0                                      <-- length
     d  decPos                17     20i 0                                      <-- #decimal pos.
     d  buffLen               21     24i 0                                      <-- buffer length
     d  alcLen                25     28i 0                                      <-- allocated Lenght
     d  ccsid                 29     32u 0                                      <-- CCSID
     d  isVarying             33     33a                                        <-- is varying field
     d  isBinary              34     34a                                        <-- is binary field
     d  reserved_1            35     36a
     d  datTimFmt             37     46a                                        <-- date/time format
     d  datTimSep             47     56a                                        <-- date/time sep.
     d  text                  57    106a                                        <-- column text

