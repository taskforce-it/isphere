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
      //* Wrapper (SPLF - setUser)                                                                 *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W003002'
 
     dW003002          PR
     d                               10A   Const                                --> User
 
      // Procedure interface for procedure 'W003002'
 
     dW003002          PI
     d LPUSR                         10A   Const                                --> User
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'SPLF'
 
     d/Copy QCPYSRC,SPLF
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       SPLF_setUser(
        LPUSR // --> User
       );
 
       // Leave procedure
       Return;
 
      /End-Free
