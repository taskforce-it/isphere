/*-----------------------------------------------------------------------*/
/*                                                                       */
/* Copyright (c) 2012-2014 iSphere Project Owners                        */
/* All rights reserved. This program and the accompanying materials      */
/* are made available under the terms of the Common Public License v1.0  */
/* which accompanies this distribution, and is available at              */
/* http://www.eclipse.org/legal/cpl-v10.html                             */
/*                                                                       */
/*-----------------------------------------------------------------------*/
/*                                                                       */
/* Build iSphere Library - Command processor (CPP)                       */
/*                                                                       */
/*-----------------------------------------------------------------------*/

/* Register error handler */
Signal on Error;

/* Start */
PARSE ARG,
DUMMY1 "(" PRDLIB ")",
DUMMY2 "(" DEVLIB ")",
DUMMY3 "(" TMPLIB ")",
DUMMY4 "(" PREPRC ")",
DUMMY5 "(" WHOAMI ")",
DUMMY6 "(" ISPHERE")",
DUMMY7 "(" TGTRLS ")",
DUMMY8 "(" OWNER  ")";

/* Change current library to iSphere development library */
"CHGCURLIB ",
"CURLIB(&DEVLIB)";

IF PREPRC = "*YES" THEN;
DO;

   /* The creation of the preprocessor has to run in a separate REXX procedure due to */
   /* inexplicable errors which occur when the creation runs in this REXX procedure */
   CALL BUILD2 DEVLIB TGTRLS;

END;

IF WHOAMI = "*YES" THEN;
DO;

   /* The creation of the WHOAMI utility has to run in a separate REXX procedure due to */
   /* inexplicable errors which occur when the creation runs in this REXX procedure */
   CALL BUILD3 DEVLIB TGTRLS;

END;

IF ISPHERE = "*YES" THEN;
DO;

   /* Create help panel group of BUILD command */
   "CRTPNLGRP ",
   "PNLGRP(&DEVLIB/BUILD_HLP) ",
   "SRCFILE(QBUILD) ",
   "SRCMBR(BUILD_HLP) ";

   /* Create Main Builder */
   "CRTBNDCL ",
   "PGM(&DEVLIB/BUILD_CRT) ",
   "SRCFILE(QBUILD) ",
   "SRCMBR(BUILD_CRT) ",
   "DBGVIEW(*LIST)";

   /* Create iSphere */
   "CALL ",
   "PGM(BUILD_CRT) ",
   "PARM(&PRDLIB &DEVLIB &TMPLIB &TGTRLS &OWNER)";

   "SNDPGMMSG ",
   "MSGID(CPF9897) ",
   "MSGF(QCPFMSG) ",
   "MSGDTA('*** Successfully created the iSphere library ***') ",
   "TOPGMQ(*PRV (*CTLBDY)) ",
   "MSGTYPE(*INFO)";
END;

/* Remove current library */
"CHGCURLIB ",
"CURLIB(*CRTDFT)";

/* Normal end */
EXIT;

/* Error handler */
ERROR:

"SNDPGMMSG ",
"MSGID(CPF9898) ",
"MSGF(QCPFMSG) ",
"MSGDTA('ERROR: Failed to create the iSphere library. Check the job log for details') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*ESCAPE)";

EXIT;

