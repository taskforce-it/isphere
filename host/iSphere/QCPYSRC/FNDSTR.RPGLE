      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Find String in Source File, Prototypes and Type Definitions                              *
      //*                                                                                          *
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
     d handle                        10S 0 Const                                --> Handle
     d showRecords                     N   Const                                --> Show records
     d matchOpt                      10A   Const                                --> Match option
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(SRCHOPTS_ARGUMENTS_SIZE)

     dFNDSTR_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle

      /copy qcpysrc,srchopts_t
