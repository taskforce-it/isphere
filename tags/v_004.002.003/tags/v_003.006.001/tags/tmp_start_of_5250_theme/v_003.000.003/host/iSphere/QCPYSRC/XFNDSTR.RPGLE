      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2016 iSphere Project Owners                                           *
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
     d matchOpt                      10A   Const                                --> Match option
     d firstLvlText                    N   Const                                --> Inc. 1.level txt
     d secLvlText                      N   Const                                --> Inc. 2.level txt
     d messageId                       N   Const                                --> Inc. message id
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     dXFNDSTR_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle

      /copy qcpysrc,srchopts_t
