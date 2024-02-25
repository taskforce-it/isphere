**free
// ------------------------------------------------------------------------------------------
//
//  Demo of iSphere Synchronize Source Member API, Prototypes and Type Definitions
//
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2023 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------

dcl-c MODE_LEFT '*LEFT';
dcl-c MODE_RIGHT '*RIGHT';

dcl-c LAST_CHANGED_NULL z'0001-01-01-00.00.00.000000';
dcl-c CHECKSUM_NULL 0;
dcl-c SRCTYPE_NULL '';

dcl-ds mbrAttrs_t qualified template;
  file          char(10);
  library       char(10);
  member        char(10);
  srcType       char(10);
  text          char(50);
  lastChanged   timestamp;
  checkSum      int(20);
end-ds;

dcl-pr SYNCMBR_getHandle zoned(10: 0) extproc(*dclcase);
end-pr;

dcl-pr SYNCMBR_resolveGenericCompareElements extproc(*dclcase);
  i_handle       zoned(10: 0) const;
  i_mode         char(10) const;
  i_memberFilter char(1024) const;
end-pr;

dcl-pr SYNCMBR_getNumberOfCompareElements zoned(6: 0) extproc(*dclcase);
  i_handle       zoned(10: 0) const;
  i_mode         char(10) const;
end-pr;

dcl-pr SYNCMBR_clear extproc(*dclcase);
  i_handle       zoned(10: 0) const;
end-pr;

dcl-pr SYNCMBR_retrieveMemberAttributes likeds(mbrAttrs_t) extproc(*dclcase);
  i_library      char(10) Options(*trim) const;
  i_file         char(10) Options(*trim) const;
  i_member       char(10) Options(*trim) const;
end-pr;

