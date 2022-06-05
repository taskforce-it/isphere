      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      * Procedure prototype for procedure 'QUSCRTUS'
     dQUSCRTUS         PR                  ExtPgm('QUSCRTUS')
     d                               20A   Const                                --> Qualified user s
     d                               10A   Const                                --> Extended attribu
     d                               10I 0 Const                                --> Initial size
     d                                1A   Const                                --> Initial value
     d                               10A   Const                                --> Public authority
     d                               50A   Const                                --> Text description
     d                               10A   Const Options(*NoPass)               --> Replace
     d                            32767A   Options(*VarSize : *NoPass)          <-> Error code
     d                               10A   Const Options(*NoPass)               --> Domain
