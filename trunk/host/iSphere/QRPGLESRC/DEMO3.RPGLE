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
      //* Demo of iSphere Spooled Files API                                                        *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
 
     hBNDDIR('SPLF')
 
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototypes for modul 'SPLF'
 
     d/Copy QCPYSRC,SPLF
 
      // Global field declarations
 
     dGIHDL            S             10S 0                                      Handle
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
        // Prepare search criteria
        SPLF_prepare();
 
        // Set search criteria "User"
        SPLF_setUser(
         'CMONE' // --> User
        );
 
        // Build results
        GIHDL = SPLF_build();
 
        // Clear files
        SPLF_clear(
         GIHDL // --> Handle
        );
 
        // Finish
        *INLR = *On;
 
      /End-Free
