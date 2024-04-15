**free
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2024 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------
//
//  Demo of iSphere Synchronize Source Member API
//
// ------------------------------------------------------------------------------------------
//  STRPREPRC Compile Options:
//    >>PRE-COMPILER<<
//      >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +
//                               SRCFILE(&SL/&SF) SRCMBR(&SM);
//        >>COMPILE<<
//          >>PARM<<  DFTACTGRP(*NO);
//          >>PARM<<  ACTGRP(ISPHERE);
//          >>PARM<<  DBGVIEW(*LIST);
//          >>PARM<<  OPTIMIZE(*NONE);
//          >>PARM<<  OPTION(*EVENTF);
//          >>PARM<<  TGTRLS(&TR);
//        >>END-COMPILE<<
//        >>EXECUTE<<
//    >>END-PRE-COMPILER<<
// ------------------------------------------------------------------------------------------

ctl-opt bnddir('ISPHERE') main(DEMO11);
/include qcpysrc,h_spec
// ------------------------------------------------------------------------------------------

// Work file
dcl-f SYNCMBRW disk(*ext) qualified keyed usage(*output) rename(SYNCMBRW: MBR) usropn;

// ------------------------------------------------------------------------------------------

/include QCPYSRC,SYNCMBR     // Procedure prototypes for modul 'FNDSTR'

// Own Prototype
dcl-pr DEMO11 extpgm('DEMO11');
  i_clearFiles   ind options(*nopass) const;
end-pr;

// Global type definitions

dcl-s handle_t zoned(10: 0) template;

// Global field declarations

dcl-s g_handle_left like(handle_t);
dcl-s g_handle_right like(handle_t);

// ------------------------------------------------------------------------------------------

dcl-proc DEMO11;
  dcl-pi *n;
    i_clearFiles   ind options(*nopass) const;
  end-pi;

  // Optional parameters
  dcl-s clearFiles like(i_clearFiles);

  // ----------------------------------------------------------------------------------------

  if (%parms() >= %parmnum(i_clearFiles));
    clearFiles = i_clearFiles;
  else;
    clearFiles = *on;
  endif;

  // Simulate comparing members stored on different systems:
  // Read members on system A
  g_handle_left = doCompare(MODE_LEFT: 'ISPHEREDVP': 'Q*SRC': '*');

  // Read members on system B
  g_handle_right = doCompare(MODE_RIGHT: 'ISPHERE': 'Q*SRC': '*');

  // Now both results must be merged to finalize the compare.

  // Cleanup
  if (clearFiles);
    SYNCMBR_clear(g_handle_left);
    SYNCMBR_clear(g_handle_right);
  endif;

end-proc;

// ==========================================================================================
//  Appends a compare element to the work file.
// ==========================================================================================

dcl-proc doCompare;
  dcl-pi *n like(handle_t);
    i_mode         char(10) const;
    i_library      char(10) const;
    i_file         char(10) const;
    i_member       char(10) const;
  end-pi;

  // Return value
  dcl-s handle like(handle_t);

  dcl-s numElems int(10);
  dcl-s msg char(52);

  // ----------------------------------------------------------------------------------------

  // Get handle
  handle = SYNCMBR_getHandle();

  // Build input data ...
  open SYNCMBRW;

  // ... add generic compare item
  if (i_mode = MODE_LEFT);
    appendLeftElement(handle: i_library: i_file: i_member);
  endif;

  // ... add generic compare item
  if (i_mode = MODE_RIGHT);
    appendRightElement(handle: i_library: i_file: i_member);
  endif;

  // ... finish setting start values
  close SYNCMBRW;

  // Resolve *generic* elements
  // SYNCMBR_resolveGenericCompareElements(handle: i_mode: '');

  // Resolve *generic* files
  SYNCMBR_resolveGenericFiles(handle: i_mode);

  // Resolve *generic* members
  SYNCMBR_resolveGenericMembers(handle: i_mode: 'DEMO.{1,6}\.(PRT|DSP)F': *on);
  // SYNCMBR_resolveGenericMembers(handle: i_mode: '<DEMO4.*': *on);

  // Get number of compare elements
  numElems = SYNCMBR_getNumberOfCompareElements(handle: i_mode);

  msg = 'Number of elements on system ' + %trim(i_mode) + ': ' + %char(numElems);
  dsply msg;

  return handle;

end-proc;

// ==========================================================================================
//  Appends a compare element to the work file.
// ==========================================================================================

dcl-proc appendLeftElement;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_library      varchar(10) Options(*trim) const;
    i_file         varchar(10) Options(*trim) const;
    i_member       varchar(10) Options(*trim) const;
  end-pi;

  // Locale field declarations
  dcl-ds data likerec(SYNCMBRW.MBR: *all);

  // ----------------------------------------------------------------------------------------

  clear data;
  data.XWHDL = i_handle;
  data.XWMBR = i_member;
  data.XWLEFTTYPE = '';
  data.XWLEFTLIB = i_library;
  data.XWLEFTFILE = i_file;
  data.XWLEFTLCHG = *loval;
  data.XWLEFTCRC = 0;

  write SYNCMBRW.MBR data;

end-proc;

// ==========================================================================================
//  Appends a compare element to the work file.
// ==========================================================================================

dcl-proc appendRightElement;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_library      varchar(10) Options(*trim) const;
    i_file         varchar(10) Options(*trim) const;
    i_member       varchar(10) Options(*trim) const;
  end-pi;

  // Locale field declarations
  dcl-ds data likerec(SYNCMBRW.MBR: *all);

  // ----------------------------------------------------------------------------------------

  clear data;
  data.XWHDL = i_handle;
  data.XWMBR = i_member;
  data.XWRGHTTYPE = '';
  data.XWRGHTLIB = i_library;
  data.XWRGHTFILE = i_file;
  data.XWRGHTLCHG = *loval;
  data.XWRGHTCRC = 0;

  write SYNCMBRW.MBR data;

end-proc;

