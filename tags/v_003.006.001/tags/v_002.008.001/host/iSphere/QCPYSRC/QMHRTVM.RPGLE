      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Procedure prototype for procedure 'QMHRTVM'
     dQMHRTVM          PR                  ExtPgm('QMHRTVM')
     d                            32767A   Options(*VarSize)                    <-- Message informat
     d                               10I 0 Const                                --> Length of messag
     d                                8A   Const                                --> Format name
     d                                7A   Const                                --> Message identifi
     d                               20A   Const                                --> Qualified messag
     d                            32767A   Const Options(*VarSize)              --> Replacement data
     d                               10I 0 Const                                --> Length of replac
     d                               10A   Const                                --> Replace substitu
     d                               10A   Const                                --> Return format co
     d                            32767A   Options(*VarSize)                    <-> Error code
     d                               10A   Const Options(*NoPass)               --> Retrieve option
     d                               10I 0 Const Options(*NoPass)               --> CCSID to convert
     d                               10I 0 Const Options(*NoPass)               --> CCSID of replace
