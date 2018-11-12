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
      //* 5250 Emulator - Handler                                                                  *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*
      //* STRPREPRC Compile Options:                                                               *
      //*   >>PRE-COMPILER<<                                                                       *
      //*     >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +                                              *
      //*                              SRCFILE(&SL/&SF) SRCMBR(&SM);                               *
      //*       >>COMPILE<<                                                                        *
      //*         >>PARM<<  DFTACTGRP(*NO);                                                        *
      //*         >>PARM<<  ACTGRP(ISPHERE);                                                       *
      //*         >>PARM<<  BNDDIR(ISPHERE QC2LE);                                                 *
      //*         >>PARM<<  DBGVIEW(*NONE);                                                        *
      //*         >>PARM<<  OPTIMIZE(*FULL);                                                       *
      //*         >>PARM<<  OPTION(*EVENTF);                                                       *
      //*         >>PARM<<  TGTRLS(&TR);                                                           *
      //*       >>END-COMPILE<<                                                                    *
      //*       >>EXECUTE<<                                                                        *
      //*   >>END-PRE-COMPILER<<                                                                   *
      //*------------------------------------------------------------------------------------------*
 
      // Procedure prototyp for procedure 'HANDLER'
 
     dHANDLER          PR
     d                               10A                                        --> Programm
     d                                 *                                        <-> Parameter 1
     d                                 *                                        <-> Parameter 2
     d                                 *                                        <-> Parameter 3
     d                                 *                                        <-> Parameter 4
     d                                 *                                        <-> Parameter 5
 
      // Procedure interface for procedure 'HANDLER'
 
     dHANDLER          PI
     d p_PGM                         10A                                        --> Programm
     d p_PRM1                          *                                        <-> Parameter 1
     d p_PRM2                          *                                        <-> Parameter 2
     d p_PRM3                          *                                        <-> Parameter 3
     d p_PRM4                          *                                        <-> Parameter 4
     d p_PRM5                          *                                        <-> Parameter 5
 
      //*------------------------------------------------------------------------------------------*
 
      // System command
 
     dSYSTEM           PR            10I 0 ExtProc('system')
     d                                 *   Value
     d                                     Options(*String)
 
      // Procedure prototyp for procedure 'TARGET'
 
     dTARGET           PR                  ExtPgm(g_PGM)
     d                                 *                                        <-> Parameter 1
     d                                 *                                        <-> Parameter 2
     d                                 *                                        <-> Parameter 3
     d                                 *                                        <-> Parameter 4
     d                                 *                                        <-> Parameter 5
 
      // Program status data structure
 
     dg_PSDS          SDS           429
     d g_LIB                  81     90A
 
      // Program
 
     dg_PGM            S             21A
 
      //*------------------------------------------------------------------------------------------*
 
      /Free
 
       // Create override
       SYSTEM(
         'OVRDSPF ' +
         'FILE(' + %Trim(p_PGM) + ') ' +
         'TOFILE(' + %Trim(g_LIB) + '/' +
                     %Trim(p_PGM) + ') ' +
         'OVRSCOPE(*JOB)'
       );
 
       // Call programm
       g_PGM = %Trim(g_LIB) + '/' + %Trim(p_PGM);
       TARGET(p_PRM1 : p_PRM2 : p_PRM3 : p_PRM4 : p_PRM5);
 
       // Delete override
       SYSTEM(
         'DLTOVR ' +
         'FILE(' + %Trim(p_PGM) + ') ' +
         'LVL(*JOB)'
       );
 
       // Leave procedure
       *INLR = *On;
 
      /End-Free
