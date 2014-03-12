      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     dFNDSTR_getHandle...
     d                 PR            10S 0                                      <-- Handle

     dFNDSTR_resolveGenericSearchElements...
     d                 PR
     d                               10S 0 Const                                --> Handle

     dFNDSTR_getNumberOfSearchElements...
     d                 PR             6S 0                                      <-- Number of elem.
     d                               10S 0 Const                                --> Handle

     dFNDSTR_search...
     d                 PR
     d                               10S 0 Const                                --> Handle
     d                               40A   Const                                --> String
     d                                3S 0 Const                                --> From column
     d                                3S 0 Const                                --> To column
     d                               10A   Const                                --> Case

     dFNDSTR_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle
