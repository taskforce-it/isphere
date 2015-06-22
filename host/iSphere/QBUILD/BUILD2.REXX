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
"PARM('BASICS1' 'QBUILD' &DEVLIB '' 'NO;C_NO' &TGTRLS)";

/* Create the Job Log API */
/* Create sample programs: NO   */
"CALL ",
"PGM(XMLINST) ",
"PARM('JOBLOG' 'QBUILD' &DEVLIB '' 'NO' &TGTRLS)";

/* Create the Event File API */
/* Create sample programs: NO   */
"CALL ",
"PGM(XMLINST) ",
"PARM('EVENTF' 'QBUILD' &DEVLIB '' 'NO' &TGTRLS)";

/* Create Preprocessor */
"CALL ",
"PGM(XMLINST) ",
"PARM('STRPREPRC' 'QBUILD' &DEVLIB '' 'ENG;NO' &TGTRLS)";

"SNDPGMMSG ",
"MSGID(CPF9897) ",
"MSGF(QCPFMSG) ",
"MSGDTA('*** Successfully compiled the STRPREPRC utility ***') ",
"TOPGMQ(*PRV (*CTLBDY)) ",
"MSGTYPE(*INFO)";

/* End */
EXIT;
