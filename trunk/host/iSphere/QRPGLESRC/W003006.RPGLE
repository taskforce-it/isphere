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
      //* Wrapper (SPLF - setFormType)                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W003006'
 
     dW003006          PR
     d                               10A   Const                                --> Form type
 
      // Procedure interface for procedure 'W003006'
 
     dW003006          PI
     d LPFRMTYP                      10A   Const                                --> Form type
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'SPLF'
 
     d/Copy QCPYSRC,SPLF
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       SPLF_setFormType(
        LPFRMTYP // --> Form type
       );
 
       // Leave procedure
       Return;
 
      /End-Free
