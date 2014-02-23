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
      //* Wrapper (FNDSTR - resolveGenericSearchElements)                                          *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W001005'
 
     dW001005          PR
     d                               10S 0 Const                                --> Handle
 
      // Procedure interface for procedure 'W001005'
 
     dW001005          PI
     d LPHDL                         10S 0 Const                                --> Handle
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'FNDSTR'
 
     d/Copy QCPYSRC,FNDSTR
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       FNDSTR_resolveGenericSearchElements(
        LPHDL // --> Handle
       );
 
       // Leave procedure
       Return;
 
      /End-Free
