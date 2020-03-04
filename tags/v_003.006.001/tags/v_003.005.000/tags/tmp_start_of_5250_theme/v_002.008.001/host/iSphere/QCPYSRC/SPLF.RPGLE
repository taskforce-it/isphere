      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     dSPLF_prepare...
     d                 PR

     dSPLF_setJob...
     d                 PR
     d                               10A   Const                                --> Job name
     d                               10A   Const                                --> User name
     d                                6A   Const                                --> Job number

     dSPLF_setUser...
     d                 PR
     d                               10A   Const                                --> User

     dSPLF_setFormType...
     d                 PR
     d                               10A   Const                                --> Form type

     dSPLF_setOutputQueue...
     d                 PR
     d                               10A   Const                                --> Output queue
     d                               10A   Const                                --> Output queue lib

     dSPLF_setUserData...
     d                 PR
     d                               10A   Const                                --> User data

     dSPLF_setDateTime...
     d                 PR
     d                               10D   DatFmt(*ISO) Const                   --> Start date
     d                                8T   TimFmt(*ISO) Const                   --> Start time
     d                               10D   DatFmt(*ISO) Const                   --> End date
     d                                8T   TimFmt(*ISO) Const                   --> End time

     dSPLF_build...
     d                 PR            10S 0                                      <-- Handle

     dSPLF_clear...
     d                 PR
     d                               10S 0 Const                                --> Handle
