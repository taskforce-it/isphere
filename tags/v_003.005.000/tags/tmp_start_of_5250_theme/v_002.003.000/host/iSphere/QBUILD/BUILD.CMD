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
     /* This command has to be created by the executing the following command */
     /* ===================================================================== */
     /*                                                                       */
     /* CRTCMD                                                                */
     /* CMD(ISPHEREDVP/BUILD)                                                 */
     /* PGM(*REXX)                                                            */
     /* SRCFILE(ISPHEREDVP/QBUILD)                                            */
     /* SRCMBR(BUILD)                                                         */
     /* REXSRCFILE(QBUILD)                                                    */
     /* REXSRCMBR(BUILD1)                                                     */
     /* REXCMDENV(*COMMAND)                                                   */
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

             PARM       KWD(PREPRC) TYPE(*CHAR) LEN(10) RSTD(*YES) DFT(*YES) +
                          SPCVAL((*YES) (*NO)) PROMPT('Create preprocessor')

             PARM       KWD(ISPHERE) TYPE(*CHAR) LEN(10) RSTD(*YES) DFT(*YES) +
                          SPCVAL((*YES) (*NO)) PROMPT('Create iSphere')

             PARM       KWD(TGTRLS) TYPE(*CHAR) LEN(10) DFT(V5R3M0) PROMPT('Target +
                          release')

