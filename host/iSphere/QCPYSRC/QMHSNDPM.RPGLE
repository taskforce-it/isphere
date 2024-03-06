**FREE
/if not defined(qmhsndpm)
/define qmhsndpm
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2024 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------

/include qcpysrc,types

// Send Program Message (QMHSNDPM) API
dcl-pr QMHSNDPM extpgm('QMHSNDPM');
  i_msgID                      char(7) const;
  i_qMsgF                      likeds(qObj_t) const;
  i_msgData                    char(32767) const options(*varsize );
  i_length                     int(10) const;
  i_msgType                    char(10) const;
  i_callStackEntry             char(4096) const options(*varsize);
  i_callStackCounter           int(10) const;
  o_msgKey                     char(4);
  io_errcode                   char(32767) options(*varsize );
  i_lengthStackEntry           int(10) const options(*nopass  );    // | optGrp 1
  i_callStackEntryQualifier    char(20) const options(*nopass  );   // |
  i_wait                       int(10) const options(*nopass  );    // |
  i_callStackEntryDataType     char(10) const options(*nopass  );   // | optGrp 2
  i_ccsid                      int(10) const options(*nopass  );    // |
end-pr;

// Message type
dcl-c QMH_MT_CMD    '*CMD';
dcl-c QMH_MT_COMP   '*COMP';
dcl-c QMH_MT_DIAG   '*DIAG';
dcl-c QMH_MT_ESCAPE '*ESCAPE';
dcl-c QMH_MT_INFO   '*INFO';
dcl-c QMH_MT_INQ    '*INQ';
dcl-c QMH_MT_NOTIFY '*NOTIFY';
dcl-c QMH_MT_RQS    '*RQS';
dcl-c QMH_MT_STATUS '*STATUS';

// Call stack entry
dcl-c QMH_CSE_SAME    '*';
dcl-c QMH_CSE_PGMBDY  '*PGMBDY';
dcl-c QMH_CSE_CTLBDY  '*CTLBDY';
dcl-c QMH_CSE_PGMNAME '*PGMNAME';
dcl-c QMH_CSE_EXT     '*EXT';

// Call stack entry qualification
dcl-c QMH_CSEQ_NONE   '*NONE';
dcl-c QMH_CSEQ_PGMBDY '*PGMBDY';

// Call stack entry data type
dcl-c QMH_CSEDT_CHAR '*CHAR';
dcl-c QMH_CSEDT_PTR  '*PTR';

/endif
