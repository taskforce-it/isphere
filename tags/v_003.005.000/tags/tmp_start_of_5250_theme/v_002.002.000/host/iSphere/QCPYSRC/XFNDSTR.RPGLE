      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     dXFNDSTR_getHandle...
     d                 PR            10S 0                                      <-- Handle

     dXFNDSTR_getNumberOfSearchElements...
     d                 PR             6S 0                                      <-- Number of elem.
     d                               10S 0 Const                                --> Handle

     dXFNDSTR_search...
     d                 PR
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchAll                        N   Const                                --> Match all finds
     d secLvlText                      N   Const                                --> Inc. 2.level txt
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     dXFNDSTR_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle

      /copy qcpysrc,srchopts_t
