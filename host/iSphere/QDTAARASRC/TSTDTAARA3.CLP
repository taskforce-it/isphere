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
     /*         >>PARM<<  TEXT('Creates: +                                    */
     /*                         iSphere Test Data Area - Logical Data');      */
     /*         >>PARM<<  OPTION(*LSTDBG);                                    */
     /*       >>END-COMPILE<<                                                 */
     /*       >>EXECUTE<<                                                     */
     /*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
     /*   >>END-PRE-COMPILER<<                                                */
     /* --------------------------------------------------------------------- */
             PGM        PARM(&LIB &OBJ)

             DCL        VAR(&LIB  ) TYPE(*CHAR) LEN(10)
             DCL        VAR(&OBJ  ) TYPE(*CHAR) LEN(10)

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

             CRTDTAARA  DTAARA(&LIB/&OBJ) TYPE(*LGL) +
                          TEXT('iSphere Test Data Area - Logical Data')

             CHGDTAARA  DTAARA(&LIB/&OBJ (*ALL)) VALUE('1')

             RETURN

             /* --------------------------------------------------- */
             /*   Error Handler                                     */
             /* --------------------------------------------------- */
 ERRHDLR:    CALL       PGM(QMHMOVPM) PARM(&ERRMSGKEY &ERRMSGTYPE &ERRNUMTYPE +
                          &ERRSTKE &ERRSTKCNT &ERRCODE    )
             MONMSG     MSGID(CPF0000)

             CALL       PGM(QMHRSNEM) PARM(&ERRMSGKEY &ERRCODE    )

             ENDPGM
