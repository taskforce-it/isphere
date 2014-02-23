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
      //* Wrapper (FNDSTR - getHandle)                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W001001'
 
     dW001001          PR
     d                               10S 0                                      <-- Handle
 
      // Procedure interface for procedure 'W001001'
 
     dW001001          PI
     d LPHDL                         10S 0                                      <-- Handle
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'FNDSTR'
 
     d/Copy QCPYSRC,FNDSTR
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       LPHDL = FNDSTR_getHandle();
 
       // Leave procedure
       Return;
 
      /End-Free
