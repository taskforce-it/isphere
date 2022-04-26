     /* --------------------------------------------------------------------- */
     /*                                                                       */
     /* Copyright (c) 2012-2016 iSphere Project Owners                        */
     /* All rights reserved. This program and the accompanying materials      */
     /* are made available under the terms of the Common Public License v1.0  */
     /* which accompanies this distribution, and is available at              */
     /* http://www.eclipse.org/legal/cpl-v10.html                             */
     /*                                                                       */
     /* --------------------------------------------------------------------- */
     /*                                                                       */
     /* iSphere Test Data Queue - Sequention FIFO                             */
     /*                                                                       */
     /* --------------------------------------------------------------------- */
     /* STRPREPRC Compile Options:                                            */
     /*   >>PRE-COMPILER<<                                                    */
     /*     >>CRTCMD<<    CRTCLPGM   PGM(QTEMP/&OB) +                         */
     /*                              SRCFILE(&SL/&SF) SRCMBR(&SM);            */
     /*       >>COMPILE<<                                                     */
     /*         >>PARM<<  TEXT('Creates: iSphere Test Data Queue - FIFO');    */
     /*         >>PARM<<  OPTION(*LSTDBG);                                    */
     /*         >>PARM<<  TGTRLS(&TR);                                        */
     /*       >>END-COMPILE<<                                                 */
     /*       >>EXECUTE<<                                                     */
     /*       >>CMD<<     CALL PGM(QTEMP/&OB) PARM(&LI &OB);                  */
     /*   >>END-PRE-COMPILER<<                                                */
     /* --------------------------------------------------------------------- */
             PGM        PARM(&LIB &OBJ)

             DCL        VAR(&LIB     ) TYPE(*CHAR) LEN(10)
             DCL        VAR(&OBJ     ) TYPE(*CHAR) LEN(10)

             DCL        VAR(&LENGTH  ) TYPE(*DEC ) LEN(5  0) VALUE(500)
             DCL        VAR(&DATA    ) TYPE(*CHAR) LEN(500 )
             DCL        VAR(&COUNT   ) TYPE(*DEC ) LEN(5  0)
             DCL        VAR(&COUNT_A ) TYPE(*CHAR) LEN(5)

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

             DLTDTAQ    DTAQ(&LIB/&OBJ)
             MONMSG     MSGID(CPF2105) EXEC(DO)
                RMVMSG     PGMQ(*SAME (*)) CLEAR(*ALL)
             ENDDO

             CRTDTAQ    DTAQ(&LIB/&OBJ) TYPE(*STD) MAXLEN(&LENGTH) +
                          SEQ(*FIFO) SENDERID(*YES) +
                          AUTORCL(*YES) SIZE(32 16) +
                          TEXT('iSphere Test Data Queue - Sequention FIFO')
PGM_STYLE:
             CHGVAR     VAR(&LENGTH) VALUE(34)
             CHGVAR     VAR(&DATA) VALUE('Some Elements of Programming Style')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(37)
             CHGVAR     VAR(&DATA) VALUE('Write clearly -- don''t be too clever.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(39)
             CHGVAR     VAR(&DATA) VALUE('Say what you mean, simply and directly.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(39)
             CHGVAR     VAR(&DATA) VALUE('Use variable names that mean something.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(41)
             CHGVAR     VAR(&DATA) VALUE('Use statement labels that mean something.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(50)
             CHGVAR     VAR(&DATA) VALUE('Format a program to help the reader understand it.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(40)
             CHGVAR     VAR(&DATA) VALUE('Make it right before you make it faster.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(40)
             CHGVAR     VAR(&DATA) VALUE('Make it clear before you make it faster.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(44)
             CHGVAR     VAR(&DATA) VALUE('Make it fail-safe before you make it faster.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(33)
             CHGVAR     VAR(&DATA) VALUE('Keep it simple to make it faster.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(40)
             CHGVAR     VAR(&DATA) VALUE('Keep it right when you make it faster.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(40)
             CHGVAR     VAR(&DATA) VALUE('Don''t sacrifice clarity +
                                          for small gains in efficiency.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(35)
             CHGVAR     VAR(&DATA) VALUE('Don''t patch bad code -- rewrite it.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(37)
             CHGVAR     VAR(&DATA) VALUE('Don''t comment bad code -- rewrite it.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             CHGVAR     VAR(&LENGTH) VALUE(46)
             CHGVAR     VAR(&DATA) VALUE('Make sure your code does "nothing" gracefully.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

WORLD:
             CHGVAR     VAR(&LENGTH) VALUE(32)
             CHGVAR     VAR(&COUNT) VALUE(&COUNT + 1)
             CHGVAR     VAR(&COUNT_A) VALUE(&COUNT)
             CHGVAR     VAR(&DATA) VALUE(&COUNT_A *CAT ' - Hello World')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

             IF         COND(&COUNT *LT 5) THEN(DO)
                GOTO       CMDLBL(WORLD)
             ENDDO

             /* Very long message to demonstrate the 259-character limitation */
             /* of the Windows org.eclipse.jface.viewers.TableViewer          */
             CHGVAR     VAR(&LENGTH) VALUE(295)
             CHGVAR     VAR(&DATA) VALUE('Lorem ipsum dolor sit amet, consetetur sadipscing +
                                          elitr, sed diam nonumy eirmod tempor invidunt ut +
                                          labore et dolore magna aliquyam erat, sed diam +
                                          voluptua. At vero eos et accusam et justo duo +
                                          dolores et ea rebum. Stet clita kasd gubergren, no +
                                          sea takimata sanctus est Lorem ipsum dolor sit +
                                          amet.')
             CALL       PGM(QSNDDTAQ) PARM(&OBJ &LIB &LENGTH &DATA)

EXIT:
             RETURN

             /* --------------------------------------------------- */
             /*   Error Handler                                     */
             /* --------------------------------------------------- */
 ERRHDLR:    CALL       PGM(QMHMOVPM) PARM(&ERRMSGKEY &ERRMSGTYPE &ERRNUMTYPE +
                          &ERRSTKE &ERRSTKCNT &ERRCODE    )
             MONMSG     MSGID(CPF0000)

             CALL       PGM(QMHRSNEM) PARM(&ERRMSGKEY &ERRCODE    )

             ENDPGM
