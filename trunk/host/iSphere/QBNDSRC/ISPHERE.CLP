000100150519     /*-----------------------------------------------------------------------*/
000200150519     /*                                                                       */
000300150519     /* Copyright (c) 2012-2014 iSphere Project Owners                        */
000400150519     /* All rights reserved. This program and the accompanying materials      */
000500150519     /* are made available under the terms of the Common Public License v1.0  */
000600150519     /* which accompanies this distribution, and is available at              */
000700150519     /* http://www.eclipse.org/legal/cpl-v10.html                             */
000800150519     /*                                                                       */
000900150519     /*-----------------------------------------------------------------------*/
001000150519     /*                                                                       */
001100150519     /* iSphere Binding Directory                                             */
001200150519     /*                                                                       */
001300150519     /*-----------------------------------------------------------------------*/
001400150519     /* STRPREPRC Compile Options:                                            */
001500150519     /*   >>PRE-COMPILER<<                                                    */
001600150519     /*     >>CRTCMD<<    CRTCLPGM   PGM(QTEMP/&OB) +                         */
001700150519     /*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
001800150519     /*       >>COMPILE<<                                                     */
001900150519     /*         >>PARM<<  TEXT('Creates: iSphere Binding Directory');         */
002000150519     /*       >>END-COMPILE<<                                                 */
002100150519     /*       >>EXECUTE<<                                                     */
002200150519     /*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
002300150519     /*   >>END-PRE-COMPILER<<                                                */
002400150519     /*-----------------------------------------------------------------------*/
002500150519             PGM        PARM(&LIB &OBJ)
002600150519
002700150519             DCL        VAR(&LIB ) TYPE(*CHAR) LEN(10)
002800150519             DCL        VAR(&OBJ ) TYPE(*CHAR) LEN(10)
002900150519
003000150519             /* ------------------------- Error handler ------------------------- */
003100150519             DCL        VAR(&ERRMSGKEY ) TYPE(*CHAR) LEN( 4)
003200150519             DCL        VAR(&ERRMSGTYPE) TYPE(*CHAR) LEN(10) VALUE('*DIAG')
003300150519             DCL        VAR(&ERRNUMTYPE) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
003400150519             DCL        VAR(&ERRSTKE   ) TYPE(*CHAR) LEN(10) VALUE('*PGMBDY')
003500150519             DCL        VAR(&ERRSTKCNT ) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
003600150519             DCL        VAR(&ERRCODE   ) TYPE(*CHAR) LEN( 8) VALUE(x'0000000000000000')
003700150519
003800150519             MONMSG     MSGID(CPF0000) EXEC(GOTO ERRHDLR)
003900150519             /* ----------------------------------------------------------------- */
004000150519
004100150519             DLTBNDDIR BNDDIR(&LIB/&OBJ)
004200150519             MONMSG     MSGID(CPF2105) EXEC(DO)
004300150519                RMVMSG PGMQ(*SAME (*)) CLEAR(*ALL)
004400150519             ENDDO
004500150519
004600150519             CRTBNDDIR BNDDIR(&LIB/&OBJ) TEXT('iSphere Binding Directory')
004700150519
004800150520             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((FNDSTR  *SRVPGM)) /* +
004900150520                          *IMMED*/
005000150520             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((ISTBDIR *SRVPGM)) /* +
005100150520                          *IMMED */
005200150520             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((NBRRNG  *SRVPGM)) /* +
005300150520                          *IMMED */
005400150520             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((SPLF    *SRVPGM)) /* +
005500150520                          *IMMED */
005600150520             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((XFNDSTR *SRVPGM)) /* +
005700150520                          *IMMED */
005800150519
005900150519             RETURN
006000150519
006100150519             /* --------------------------------------------------- */
006200150519             /*   Error Handler                                     */
006300150519             /* --------------------------------------------------- */
006400150519 ERRHDLR:    CALL       PGM(QMHMOVPM) PARM(&ERRMSGKEY  +
006500150519                                           &ERRMSGTYPE +
006600150519                                           &ERRNUMTYPE +
006700150519                                           &ERRSTKE    +
006800150519                                           &ERRSTKCNT  +
006900150519                                           &ERRCODE    )
007000150519             MONMSG     MSGID(CPF0000)
007100150519
007200150519             CALL       PGM(QMHRSNEM) PARM(&ERRMSGKEY  +
007300150519                                           &ERRCODE    )
007400150519
007500150519             ENDPGM
