/*-----------------------------------------------------------------------*/
/*                                                                       */
/* Copyright (c) 2012-2019 iSphere Project Owners                        */
/* All rights reserved. This program and the accompanying materials      */
/* are made available under the terms of the Common Public License v1.0  */
/* which accompanies this distribution, and is available at              */
/* http://www.eclipse.org/legal/cpl-v10.html                             */
/*                                                                       */
/*-----------------------------------------------------------------------*/
/*                                                                       */
/* Build iSphere Library - Create WHOAMI                                 */
/*                                                                       */
/*-----------------------------------------------------------------------*/

/* Register error handler */
Signal on Error;

/* Start */
ARG DEVLIB TGTRLS DLTUTLSRCF;

/* Create XML-Installer */
"CRTBNDRPG ",
"PGM(&DEVLIB/XMLINST) ",
"SRCFILE(QBUILD) ",
"SRCMBR(XMLINST)";

/* Create WHOAMI */
"CALL ",
"PGM(XMLINST) ",
"PARM('WHOAMI' 'QBUILD' &DEVLIB '' '' '*CURRENT')";

/* Need to fix variable DEVLIB after caling XMLINST. Why??? */
DEVLIB = STRIP(DEVLIB)

/* Delete sotrce files */
IF DLTUTLSRCF = "*YES" THEN;
DO;

   /* Delete source files of utility */
   "DLTF ",
   "FILE(&DEVLIB/QWHOAMI)";
END;

"SNDPGMMSG ",
"MSGID(CPF9897) ",
"MSGF(QCPFMSG) ",
"MSGDTA('*** Successfully compiled the WHOAMI utility ***') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*INFO)";

/* Normal end */
EXIT;

/* Error handler */
ERROR:

"SNDPGMMSG ",
"MSGID(CPF9898) ",
"MSGF(QCPFMSG) ",
"MSGDTA('ERROR: Failed to create the WHOAMI utility. Check the job log for details') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*ESCAPE)";

EXIT;

