      /if not defined(types)
      /define types
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     d qObj_t          ds                  qualified based(pTemplate)
     d  name                         10a
     d  lib                          10a

     d qJob_t          ds                  qualified based(pTemplate)
     d  name                         10a
     d  user                         10a
     d  nbr                           6a

     d errCode_t       ds                  qualified based(pTemplate)
     d  bytPrv                       10i 0
     d  bytAvl                       10i 0
     d  excId                         7a
     d  reserved                      1a
     d  excData                     256a

      /endif
