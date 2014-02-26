000100140225     /*-----------------------------------------------------------------------*/
000200140225     /*                                                                       */
000300140225     /* Copyright (c) 2012-2013 Task Force IT-Consulting GmbH, Waltrop and ot.*/
000400140225     /* All rights reserved. This program and the accompanying materials      */
000500140225     /* are made available under the terms of the Common Public License v1.0  */
000600140225     /* which accompanies this distribution, and is available at              */
000700140225     /* http://www.eclipse.org/legal/cpl-v10.html                             */
000800140225     /*                                                                       */
000900140225     /* Contributors:                                                         */
001000140225     /*     Task Force IT-Consulting GmbH - initial API and implementation    */
001100140225     /*                                                                       */
001200140225     /*-----------------------------------------------------------------------*/
001300140225     /*                                                                       */
001400140225     /* iSphere Binding Directory                                             */
001500140225     /*                                                                       */
001600140225     /*-----------------------------------------------------------------------*/
001700140225     /* STRPREPRC Compile Options:                                            */
001800140225     /*   >>PRE-COMPILER<<                                                    */
001900140225     /*     >>CRTCMD<<    CRTCLPGM   PGM(QTEMP/&OB) +                         */
002000140225     /*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
002100140225     /*       >>COMPILE<<                                                     */
002200140225     /*         >>PARM<<  TEXT('Creates: iSphere Binding Directory');         */
002300140225     /*       >>END-COMPILE<<                                                 */
002400140225     /*       >>EXECUTE<<                                                     */
002500140225     /*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
002600140225     /*   >>END-PRE-COMPILER<<                                                */
002700140225     /*-----------------------------------------------------------------------*/
002800140225             PGM        PARM(&LIB &OBJ)
002900140225
003000140225             DCL        VAR(&LIB ) TYPE(*CHAR) LEN(10)
003100140225             DCL        VAR(&OBJ ) TYPE(*CHAR) LEN(10)
003200140225
003300140225             /* ------------------------- Error handler ------------------------- */
003400140225             DCL        VAR(&ERRMSGKEY ) TYPE(*CHAR) LEN( 4)
003500140225             DCL        VAR(&ERRMSGTYPE) TYPE(*CHAR) LEN(10) VALUE('*DIAG')
003600140225             DCL        VAR(&ERRNUMTYPE) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
003700140225             DCL        VAR(&ERRSTKE   ) TYPE(*CHAR) LEN(10) VALUE('*PGMBDY')
003800140225             DCL        VAR(&ERRSTKCNT ) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
003900140225             DCL        VAR(&ERRCODE   ) TYPE(*CHAR) LEN( 8) VALUE(x'0000000000000000')
004000140225
004100140225             MONMSG     MSGID(CPF0000) EXEC(GOTO ERRHDLR)
004200140225             /* ----------------------------------------------------------------- */
004300140225
004400140225             DLTBNDDIR BNDDIR(&LIB/&OBJ)
004500140225             MONMSG     MSGID(CPF2105) EXEC(DO)
004600140225                RMVMSG PGMQ(*SAME (*)) CLEAR(*ALL)
004700140225             ENDDO
004800140225
004900140225             CRTBNDDIR BNDDIR(&LIB/&OBJ) TEXT('iSphere Binding Directory')
005000140225
005100140225             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((FNDSTR  *SRVPGM *IMMED))
005200140225             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((ISTBDIR *SRVPGM *IMMED))
005300140225             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((NBRRNG  *SRVPGM *IMMED))
005400140225             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((SPLF    *SRVPGM *IMMED))
005500140225             ADDBNDDIRE BNDDIR(&LIB/&OBJ) OBJ((XFNDSTR *SRVPGM *IMMED))
005600140225
005700140225             RETURN
005800140225
005900140225             /* --------------------------------------------------- */
006000140225             /*   Error Handler                                     */
006100140225             /* --------------------------------------------------- */
006200140225 ERRHDLR:    CALL       PGM(QMHMOVPM) PARM(&ERRMSGKEY  +
006300140225                                           &ERRMSGTYPE +
006400140225                                           &ERRNUMTYPE +
006500140225                                           &ERRSTKE    +
006600140225                                           &ERRSTKCNT  +
006700140225                                           &ERRCODE    )
006800140225             MONMSG     MSGID(CPF0000)
006900140225
007000140225             CALL       PGM(QMHRSNEM) PARM(&ERRMSGKEY  +
007100140225                                           &ERRCODE    )
007200140225
007300140225             ENDPGM
