**free
/if not defined(qlgcnvcs)
/define qlgcnvcs
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2023 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------

// Convert Case (QLGCNVCS, QlgConvertCase) API
dcl-pr QlgConvertCase extproc('QlgConvertCase');
  i_reqCtrlBlk   char(65535) options(*varsize) const;
  i_inData       char(65535) options(*varsize) const;
  o_outData      char(65535) options(*varsize) const;
  i_length       int(10: 0) const;
  io_ErrCode     char(65535) options(*nopass: *varsize);
end-pr;

dcl-c CVTCASE_TYPE_ccsid    1;
dcl-c CVTCASE_TYPE_table    2;
dcl-c CVTCASE_TYPE_userDef  3;
dcl-c CVTCASE_toUpper       0;
dcl-c CVTCASE_toLower       1;
dcl-c CVTCASE_CCSID_Job     0;

// Format of CCSID Request Control Block
dcl-ds reqCtrlBlk_t qualified template;
  type       int(10: 0);
  ccsid      int(10: 0);
  case       int(10: 0);
  reserved   char(10);
end-ds;

/endif
