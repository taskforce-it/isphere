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
     /* iSphere Build                                                         */
     /*                                                                       */
     /*-----------------------------------------------------------------------*/
     /*                                                                       */
     /* This command has to be created by the executing the following commands*/
     /* ===================================================================== */
     /*                                                                       */
     /* CRTPNLGRP                                                             */
     /* PNLGRP(ISPHEREDVP/BUILD_HLP)                                          */
     /* SRCFILE(ISPHEREDVP/QBUILD)                                            */
     /* SRCMBR(BUILD_HLP)                                                     */
     /*                                                                       */
     /* CRTCMD                                                                */
     /* CMD(ISPHEREDVP/BUILD)                                                 */
     /* PGM(*REXX)                                                            */
     /* SRCFILE(ISPHEREDVP/QBUILD)                                            */
     /* SRCMBR(BUILD)                                                         */
     /* REXSRCFILE(QBUILD)                                                    */
     /* REXSRCMBR(BUILD1)                                                     */
     /* REXCMDENV(*COMMAND)                                                   */
     /* HLPPNLGRP(BUILD_HLP)                                                  */
     /* HLPID(BUILD)                                                          */
     /*                                                                       */
     /*-----------------------------------------------------------------------*/
     /* STRPREPRC Compile Options:                                            */
     /*   >>PRE-COMPILER<<                                                    */
     /*     >>CRTCMD<<    CRTCMD   CMD(&LI/&OB) +                             */
     /*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
     /*       >>COMPILE<<                                                     */
     /*         >>PARM<<   PGM(*REXX);                                        */
     /*         >>PARM<<   REXSRCFILE(QBUILD);                                */
     /*         >>PARM<<   REXSRCMBR(BUILD1);                                 */
     /*         >>PARM<<   REXCMDENV(*COMMAND);                               */
     /*         >>PARM<<   HLPPNLGRP(&LI/BUILD_HLP);                          */
     /*         >>PARM<<   HLPID(BUILD);                                      */
     /*       >>END-COMPILE<<                                                 */
     /*       >>EXECUTE<<                                                     */
     /*   >>END-PRE-COMPILER<<                                                */
     /*-----------------------------------------------------------------------*/

             CMD        PROMPT('iSphere Build')

             PARM       KWD(PRDLIB) TYPE(*NAME) LEN(10) DFT(ISPHERE) +
                          PROMPT('Production library')

             PARM       KWD(DEVLIB) TYPE(*NAME) LEN(10) DFT(ISPHEREDVP) +
                          PROMPT('Development library')

             PARM       KWD(TMPLIB) TYPE(*NAME) LEN(10) DFT(QTEMP) +
                          PROMPT('Temporary library')

             PARM       KWD(PREPRC) TYPE(*CHAR) LEN(10) DFT(*NO) +
                          RSTD(*YES) SPCVAL((*YES) (*NO)) +
                          PROMPT('Create preprocessor')

             PARM       KWD(WHOAMI) TYPE(*CHAR) LEN(10) DFT(*NO) +
                          RSTD(*YES) SPCVAL((*YES) (*NO)) +
                          PROMPT('Create WHOAMI utility')

             PARM       KWD(ISPHERE) TYPE(*CHAR) LEN(10) DFT(*YES) +
                          RSTD(*YES) SPCVAL((*YES) (*NO)) +
                          PROMPT('Create iSphere')

             PARM       KWD(TGTRLS) TYPE(*CHAR) LEN(10) DFT(V7R1M0) +
                          SPCVAL((*CURRENT)) +
                          PROMPT('Target release')

             PARM       KWD(OWNER) TYPE(*NAME) LEN(10) DFT(QPGMR) +
                          SPCVAL(*SAME) +
                          PROMPT('Object owner')

