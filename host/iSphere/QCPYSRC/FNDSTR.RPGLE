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
     d matchAll                        N   Const                                --> Match all finds
     d numArguments                  10I 0 Const                                --> Number of args.
     d arguments                           Const Likeds(searchArgument_t)       --> Search argument
     d                                     Options(*varsize)
     d                                     Dim(FNDSTR_ARGUMENTS_SIZE)

     dFNDSTR_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle

     d FNDSTR_OPERATOR_CONTAINS...
     d                 C                   1
     d FNDSTR_OPERATOR_CONTAINS_NOT...
     d                 C                   -1

     d FNDSTR_ARGUMENTS_SIZE...
     d                 C                   16

     d searchArgument_t...
     d                 DS                  Qualified Based(pTemplate)
     d  operator                     10I 0
     d  string                       40A   Varying
     d  fromColumn                   10I 0
     d  toColumn                     10I 0
     d  case                         10A
