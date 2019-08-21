     /* --------------------------------------------------------------------- */
     /*                                                                       */
     /* Copyright (c) 2012-2014 iSphere Project Owners                        */
     /* All rights reserved. This program and the accompanying materials      */
     /* are made available under the terms of the Common Public License v1.0  */
     /* which accompanies this distribution, and is available at              */
     /* http://www.eclipse.org/legal/cpl-v10.html                             */
     /*                                                                       */
     /* --------------------------------------------------------------------- */
     /*                                                                       */
     /* iSphere Version                                                       */
     /*                                                                       */
     /* --------------------------------------------------------------------- */
     /* STRPREPRC Compile Options:                                            */
     /*   >>PRE-COMPILER<<                                                    */
     /*     >>CRTCMD<<    CRTCLPGM   PGM(QTEMP/&OB) +                         */
     /*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
     /*       >>COMPILE<<                                                     */
     /*         >>PARM<<  TEXT('Creates: iSphere Test Data Area - All Types'); */
     /*         >>PARM<<  OPTION(*LSTDBG);                                    */
     /*       >>END-COMPILE<<                                                 */
     /*       >>EXECUTE<<                                                     */
     /*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
     /*   >>END-PRE-COMPILER<<                                                */
     /* --------------------------------------------------------------------- */
             PGM        PARM(&LIB &OBJ)

             DCL        VAR(&LIB  ) TYPE(*CHAR) LEN(10)
             DCL        VAR(&OBJ  ) TYPE(*CHAR) LEN(10)

             DCL        VAR(&POS  ) TYPE(*DEC ) LEN(10 0)
             DCL        VAR(&LEN  ) TYPE(*DEC ) LEN(10 0) VALUE(4)
             DCL        VAR(&INT  ) TYPE(*DEC ) LEN(10 0)
             DCL        VAR(&INT_C) TYPE(*CHAR) LEN( 4)
             DCL        VAR(&CNT  ) TYPE(*DEC ) LEN(10 0)
             DCL        VAR(&CNT_C) TYPE(*CHAR) LEN(10)

             /* ------------------------- Error handler ------------------------- */
             DCL        VAR(&ERRMSGKEY ) TYPE(*CHAR) LEN( 4)
             DCL        VAR(&ERRMSGTYPE) TYPE(*CHAR) LEN(10) VALUE('*DIAG')
             DCL        VAR(&ERRNUMTYPE) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
             DCL        VAR(&ERRSTKE   ) TYPE(*CHAR) LEN(10) VALUE('*PGMBDY')
             DCL        VAR(&ERRSTKCNT ) TYPE(*CHAR) LEN( 4) VALUE(x'00000001')
             DCL        VAR(&ERRCODE   ) TYPE(*CHAR) LEN( 8) +
                          VALUE(x'0000000000000000')

             MONMSG     MSGID(CPF0000) EXEC(GOTO ERRHDLR)
             /* ----------------------------------------------------------------- */

             DLTDTAARA  DTAARA(&LIB/&OBJ)
             MONMSG     MSGID(CPF2105) EXEC(DO)
                RMVMSG     PGMQ(*SAME (*)) CLEAR(*ALL)
             ENDDO

             CRTDTAARA  DTAARA(&LIB/&OBJ) TYPE(*CHAR) LEN(2000) +
                          TEXT('iSphere Test Data Area - All Types')

             CHGDTAARA  DTAARA(&LIB/&OBJ (  1  12)) VALUE('Text:')
             CHGDTAARA  DTAARA(&LIB/&OBJ ( 51  50)) +
                          VALUE('The quick brown fox jumps over the lazy dog.')

             /* Decimal value: 64424509455 */
             CHGDTAARA  DTAARA(&LIB/&OBJ (101  12)) VALUE('Long:')
             CHGDTAARA  DTAARA(&LIB/&OBJ (114   8)) VALUE(x'0000000F0000000F')

             /* Decimal value: 983055      */
             CHGDTAARA  DTAARA(&LIB/&OBJ (151  12)) VALUE('Integer:')
             CHGDTAARA  DTAARA(&LIB/&OBJ (164   4)) VALUE(x'000F000F')

             /* Decimal value: 3855        */
             CHGDTAARA  DTAARA(&LIB/&OBJ (201  12)) VALUE('Short:')
             CHGDTAARA  DTAARA(&LIB/&OBJ (214   2)) VALUE(x'0F0F')

             /* Decimal value: 55          */
             CHGDTAARA  DTAARA(&LIB/&OBJ (251  12)) VALUE('Tiny:')
             CHGDTAARA  DTAARA(&LIB/&OBJ (264   1)) VALUE(x'37')

             CHGDTAARA  DTAARA(&LIB/&OBJ (301  12)) VALUE('Boolean:')
             CHGDTAARA  DTAARA(&LIB/&OBJ (314   1)) VALUE('1')

             CHGDTAARA  DTAARA(&LIB/&OBJ (351  12)) VALUE('Dec. (6,2):')
             CHGDTAARA  DTAARA(&LIB/&OBJ (364   6)) VALUE('123412')

             /* Integer values             */
             CHGDTAARA  DTAARA(&LIB/&OBJ (401  25)) VALUE('List of Integer Values:')
             CHGVAR     VAR(&POS) VALUE(451)
             CHGVAR     VAR(&INT) VALUE(0)
             CHGVAR     VAR(&CNT) VALUE(0)
LOOP:
             CHGVAR     VAR(%BIN(&INT_C)) VALUE(&INT)
             CHGDTAARA  DTAARA(&LIB/&OBJ (&POS &LEN)) VALUE(&INT_C)

             CHGVAR     VAR(&INT) VALUE(&INT + 1)
             CHGVAR     VAR(&CNT) VALUE(&CNT + 1)
             CHGVAR     VAR(&POS) VALUE(&POS + &LEN)

             IF         COND(&CNT < 256) THEN(DO)
               GOTO       CMDLBL(LOOP)
             ENDDO

             CHGVAR     VAR(&CNT_C) VALUE(&CNT)
             CHGDTAARA  DTAARA(&LIB/&OBJ (426 10)) VALUE(&CNT_C)

             RETURN

             /* --------------------------------------------------- */
             /*   Error Handler                                     */
             /* --------------------------------------------------- */
 ERRHDLR:    CALL       PGM(QMHMOVPM) PARM(&ERRMSGKEY &ERRMSGTYPE &ERRNUMTYPE +
                          &ERRSTKE &ERRSTKCNT &ERRCODE    )
             MONMSG     MSGID(CPF0000)

             CALL       PGM(QMHRSNEM) PARM(&ERRMSGKEY &ERRCODE    )

             ENDPGM
