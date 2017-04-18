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
/* Build iSphere Library - Create STRPREPRC                              */
/*                                                                       */
/*-----------------------------------------------------------------------*/

/* Register error handler */
Signal on Error;

/* Start */
ARG DEVLIB TGTRLS;

/* Create XML-Installer */
"CRTBNDRPG ",
"PGM(&DEVLIB/XMLINST) ",
"SRCFILE(QBUILD) ",
"SRCMBR(XMLINST)";

/* Create the Tools/400 Runtime service program */
/* Create sample programs: NO   */
/* C-Compiler available:   C_NO */
"CALL ",
"PGM(XMLINST) ",
"PARM('BASICS1' 'QBUILD' &DEVLIB '' '&CRTTSTPGM=NO;&C_COMP=C_NO;' &TGTRLS)";

/* Create the Job Log API */
/* Create sample programs: NO   */
"CALL ",
"PGM(XMLINST) ",
"PARM('JOBLOG' 'QBUILD' &DEVLIB '' '&CRTTSTPGM=NO;' &TGTRLS)";

/* Create the Event File API */
/* Create sample programs: NO   */
"CALL ",
"PGM(XMLINST) ",
"PARM('EVENTF' 'QBUILD' &DEVLIB '' '&CRTTSTPGM=NO;' &TGTRLS)";

/* Create Preprocessor */
"CALL ",
"PGM(XMLINST) ",
"PARM('STRPREPRC' 'QBUILD' &DEVLIB '' '&LANG=ENG;&TAGSREQ=NO;' &TGTRLS)";

"SNDPGMMSG ",
"MSGID(CPF9897) ",
"MSGF(QCPFMSG) ",
"MSGDTA('*** Successfully compiled the STRPREPRC utility ***') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*INFO)";

/* Normal end */
EXIT;

/* Error handler */
ERROR:

"SNDPGMMSG ",
"MSGID(CPF9898) ",
"MSGF(QCPFMSG) ",
"MSGDTA('ERROR: Failed to create the STRPREPRC utility. Check the job log for details') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*ESCAPE)";

EXIT;

