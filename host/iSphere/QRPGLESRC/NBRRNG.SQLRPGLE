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
      //* Number Range                                                                             *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<< CRTSQLRPGI   OBJ(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);                   *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  OBJTYPE(*MODULE);                                                      *
      //*         >>PARM<<  COMMIT(*NONE);                                                         *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*         >>PARM<<  CLOSQLCSR(*ENDMOD);                                                    *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM--  COMPILEOPT('OPTIMIZE(*FULL)');                                         *
      //*         >>PARM<<  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');                          *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*

     hNoMain
      /copy qcpysrc,h_spec
      /copy qcpysrc,copyright

      //*------------------------------------------------------------------------------------------*

     fNBRRNG01  UF A E           K Disk    Rename(NBRRNG : NBRRNGRF01)
     f                                     UsrOpn

      //*------------------------------------------------------------------------------------------*

      // Procedure prototypes for modul 'NBRRNG'

     d/Copy QCPYSRC,NBRRNG

      //*==========================================================================================*
      //* Get Number                                                                               *
      //*==========================================================================================*

     pNBRRNG_getNumber...
     p                 B                   Export
     dNBRRNG_getNumber...
     d                 PI            10S 0                                      <-- Number
     d LPRNG                         10A   Const                                --> Number range

      //*------------------------------------------------------------------------------------------*

      /Free

       // Open file
       Open NBRRNG01;

       // Get handle
       DoU Not %Error;
         Chain(E) (LPRNG) NBRRNGRF01;
         If %Error;
           // ...
         EndIf;
       EndDo;
       If Not %Found;
         Clear *ALL NBRRNGRF01;
         NRRNG = LPRNG;
         NRNBR = 1;
         Write NBRRNGRF01;
       Else;
         NRNBR = NRNBR + 1;
         Update NBRRNGRF01;
       EndIf;

       // Close file
       Close NBRRNG01;

       // Leave procedure
       Return NRNBR;

      /End-Free

       //*------------------------------------------------------------------------------------------

     pNBRRNG_getNumber...
     p                 E
