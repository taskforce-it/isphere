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
      //* Wrapper (XFNDSTR - getHandle)                                                            *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'W002001'
 
     dW002001          PR
     d                               10S 0                                      <-- Handle
 
      // Procedure interface for procedure 'W002001'
 
     dW002001          PI
     d LPHDL                         10S 0                                      <-- Handle
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure Prototypes for Modul 'XFNDSTR'
 
     d/Copy QCPYSRC,XFNDSTR
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Call service program
       LPHDL = XFNDSTR_getHandle();
 
       // Leave procedure
       Return;
 
      /End-Free
