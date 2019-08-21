      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2014 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Wrapper (SPLF - setDateTime)                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
      //*         >>PARM<<  BNDDIR(ISPHERE);                                                       *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
      //*         >>PARM<<  TGTRLS(V5R4M0);                                                        *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

      // Procedure prototyp for procedure 'W003009'

     dW003009          PR
     d                                8S 0 Const                                --> Start date
     d                                6S 0 Const                                --> Start time
     d                                8S 0 Const                                --> End date
     d                                6S 0 Const                                --> End time

      // Procedure interface for procedure 'W003009'

     dW003009          PI
     d LPSTRDAT                       8S 0 Const                                --> Start date
     d LPSTRTIM                       6S 0 Const                                --> Start time
     d LPENDDAT                       8S 0 Const                                --> End date
     d LPENDTIM                       6S 0 Const                                --> End time

      //*------------------------------------------------------------------------------------------*

      // Procedure Prototypes for Modul 'SPLF'

     d/Copy QCPYSRC,SPLF

      // Locale Field Declarations

     dLISTRDAT         S              8S 0                                      Start date
     dLISTRTIM         S              6S 0                                      Start time
     dLIENDDAT         S              8S 0                                      End date
     dLIENDTIM         S              6S 0                                      End time

      //*------------------------------------------------------------------------------------------*

      /Free

       // Call service program
       SPLF_setDateTime(
        %Date(LPSTRDAT : *ISO) : // --> Start date
        %Time(LPSTRTIM : *ISO) : // --> Start time
        %Date(LPENDDAT : *ISO) : // --> End date
        %Time(LPENDTIM : *ISO)   // --> End time
       );

       // Leave procedure
       Return;

      /End-Free
