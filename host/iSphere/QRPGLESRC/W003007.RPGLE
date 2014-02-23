      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and others.               *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //* Contributors:                                                                            *
      //*     Task Force IT-Consulting GmbH - initial API and implementation                       *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Wrapper (SPLF - setOutputQueue)                                                          *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W003007'
 
     dW003007          PR
     d                               10A   Const                                --> Output queue
     d                               10A   Const                                --> Output queue lib
 
      // Procedure interface for procedure 'W003007'
 
     dW003007          PI
     d LPOUTQ                        10A   Const                                --> Output queue
     d LPOUTQLIB                     10A   Const                                --> Output queue lib
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'SPLF'
 
     d/Copy QCPYSRC,SPLF
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       SPLF_setOutputQueue(
        LPOUTQ :  // --> Output queue
        LPOUTQLIB // --> Output queue lib
       );
 
       // Leave procedure
       Return;
 
      /End-Free
