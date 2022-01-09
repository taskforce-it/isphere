      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Procedure prototype for procedure 'QUSLSPL'
     dQUSLSPL          PR                  ExtPgm('QUSLSPL')
     d                               20A   Const                                --> User space
     d                                8A   Const                                --> Format name
     d                               10A   Const                                --> User name
     d                               20A   Const                                --> output queue
     d                               10A   Const                                --> Form type
     d                               10A   Const                                --> User data
     d                            32767A   Options(*VarSize : *NoPass)          <-> Error code
     d                               26A   Const Options(*NoPass)               --> Job
     d                               10I 0 Dim(50) Const Options(*NoPass)       --> Fields
     d                               10I 0 Const Options(*NoPass)               --> Number of fields
     d                               10I 0 Const Options(*NoPass)               --> ASP Number
     d                                8A   Const Options(*NoPass)               --> Job system name
     d                                7A   Const Options(*NoPass)               --> Start date
     d                                6A   Const Options(*NoPass)               --> Start time
     d                                7A   Const Options(*NoPass)               --> End date
     d                                6A   Const Options(*NoPass)               --> End time
     d                               10A   Const Options(*NoPass)               --> ASP Device
