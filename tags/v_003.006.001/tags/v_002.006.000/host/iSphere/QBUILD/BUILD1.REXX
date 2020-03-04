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

/* Start */
PARSE ARG,
DUMMY1 "(" PRDLIB ")",
DUMMY2 "(" DEVLIB ")",
DUMMY3 "(" TMPLIB ")",
DUMMY4 "(" PREPRC ")",
DUMMY5 "(" ISPHERE")",
DUMMY6 "(" TGTRLS ")",
DUMMY7 "(" OWNER  ")";

/* Change current library to iSphere development library */
"CHGCURLIB ",
"CURLIB(&DEVLIB)";

IF PREPRC = "*YES" THEN;
DO;

   /* The creation of the preprocessor has to run in a separate REXX procedure due to */
   /* inexplicable errors which occur when the creation runs in this REXX procedure */
   CALL BUILD2 DEVLIB TGTRLS;

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

END;

/* Remove current library */
"CHGCURLIB ",
"CURLIB(*CRTDFT)";

/* End */
EXIT;
