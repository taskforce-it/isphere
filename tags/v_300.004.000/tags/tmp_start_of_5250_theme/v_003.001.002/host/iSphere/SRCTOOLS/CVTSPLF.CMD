     /* **************************************************************************** */
     /* *** Command name:?CVTSPLF                              Date:?2005-08-09  *** */
     /* ***          CPP:?CVTSPLFC                                               *** */
     /* **************************************************************************** */
     /* ***  Copyright - Free RPG/i5 Tools                                       *** */
     /* ***     E-Mail - Utilities@FreeRPGTools.com                              *** */
     /* ***    Purpose - Convert Spooled File                                    *** */
     /* ***                                                                      *** */
     /* ***    Maintenance History: (Most recent is listed first)                *** */
     /* ***                                                                      *** */
     /* ***    Project  Date of      Pgmr                                        *** */
     /* ***     Id.   Last Change Initials Description of Changes            *** */
     /* ***   xxxxxxx  2006-12-05   FRT       Initial Creation                  *** */
     /* ***                                                                     *** */
     /* **************************************************************************** */
     /* ***?                          Creation Keywords                          *** */
     /* *** Keyword      Keyword Data                                          *** */
     /* *** *NONE         No Keywords used for this program                     *** */
     /* ***                                                                     *** */
     /* **************************************************************************** */
     /* *** STRPREPRC Compile Options:                                           *** */
     /* ***   >>PRE-COMPILER<<                                                   *** */
     /* ***     >>CRTCMD<<    CRTCMD     CMD(&LI/&OB) +                          *** */
     /* ***                   SRCFILE(&SL/&SF) SRCMBR(&SM);                      *** */
     /* ***     >>COMPILE<<                                                      *** */
     /* ***       >>PARM<<  PGM(*LIBL/CVTSPLFC);                                 *** */
     /* ***       >>PARM<<  MODE(*ALL);                                          *** */
     /* ***       >>PARM<<  ALLOW(*ALL);                                         *** */
     /* ***       >>PARM<<  ALWLMTUSR(*NO);                                      *** */
     /* ***     >>END-COMPILE<<                                                  *** */
     /* ***     >>EXECUTE<<                                                      *** */
     /* ***   >>END-PRE-COMPILER<<                                               *** */
     /* **************************************************************************** */
             CMD        PROMPT('Convert Spooled File')

             PARM       KWD(FROMFILE) TYPE(*NAME) LEN(10) MIN(1) PROMPT('From spooled file +
                          name')

             PARM       KWD(TOSTREAM) TYPE(*PNAME) LEN(128) MIN(1) PROMPT('To stream file name')

             PARM       KWD(TODIR) TYPE(*PNAME) LEN(256) MIN(1) PROMPT('To directory')

             PARM       KWD(JOB) TYPE(JOB) DFT(*) SNGVAL((*)) PROMPT('Job name')
 JOB:        QUAL       TYPE(*NAME) LEN(10) MIN(1)
             QUAL       TYPE(*NAME) LEN(10) MIN(1) PROMPT('User')
             QUAL       TYPE(*CHAR) LEN(6) RANGE(000000 999999) MIN(1) PROMPT('Number')

             PARM       KWD(SPLNBR) TYPE(*DEC) LEN(4) DFT(*ONLY) RANGE(1 9999) SPCVAL((*LAST +
                          -2) (*ONLY -3)) PROMPT('Spooled file number')

             PARM       KWD(TOFMT) TYPE(*CHAR) LEN(5) RSTD(*YES) DFT(*TEXT) VALUES(*TEXT *HTML +
                          *PDF) PROMPT('Stream file format')

             PARM       KWD(STOPT) TYPE(*CHAR) LEN(8) RSTD(*YES) DFT(*NONE) VALUES(*NONE *ADD +
                          *REPLACE) PROMPT('Stream file option')

             PARM       KWD(STCODPAG) TYPE(*DEC) LEN(5 0) DFT(*PCASCII) RANGE(1 32767) +
                          SPCVAL((*PCASCII -1) (*STREAM -2)) PMTCTL(*PMTRQS) PROMPT('Stream +
                          file code page')

             PARM       KWD(TITLE) TYPE(*CHAR) LEN(50) RSTD(*NO) DFT(*NONE) SPCVAL((*NONE) +
                          (*STREAMILE)) PMTCTL(HTML) PROMPT('Title for HTML or PDF')

             PARM       KWD(BOOKMARK) TYPE(*CHAR) LEN(7) RSTD(*YES) DFT(*PAGNBR) +
                          VALUES(*PAGNBR *POS *KEY) PMTCTL(PDF) PROMPT('Type of PDF bookmarks')

             PARM       KWD(BMARKPOS) TYPE(BLST1) PMTCTL(POS) PROMPT('PDF bookmark string +
                          position')
 BLST1:      ELEM       TYPE(*DEC) LEN(3 0) DFT(1) RANGE(1 300) PROMPT('Line number')
             ELEM       TYPE(*DEC) LEN(3 0) DFT(1) RANGE(1 378) PROMPT('Character position')
             ELEM       TYPE(*DEC) LEN(3 0) DFT(1) RANGE(1 378) PROMPT('Length')

             PARM       KWD(BMARKKEY) TYPE(BLST2) PMTCTL(KEY) PROMPT('PDF bookmark string key')
 BLST2:      ELEM       TYPE(*CHAR) LEN(378) DFT(' ') VARY(*YES *INT2) PROMPT('Key string')
             ELEM       TYPE(*DEC) LEN(3 0) DFT(1) RANGE(1 999) PROMPT('Occurrence')
             ELEM       TYPE(*DEC) LEN(3 0) DFT(0) RANGE(-378 378) PROMPT('Offset')
             ELEM       TYPE(*DEC) LEN(3 0) DFT(1) RANGE(1 378) PROMPT('Length')

 HTML:       PMTCTL     CTL(TOFMT) COND((*EQ *HTML) (*EQ *PDF)) NBRTRUE(*EQ 1) LGLREL(*OR)

 PDF:        PMTCTL     CTL(TOFMT) COND((*EQ *PDF)) NBRTRUE(*EQ 1)

 POS:        PMTCTL     CTL(BOOKMARK) COND((*EQ *POS)) NBRTRUE(*EQ 1)

 KEY:        PMTCTL     CTL(BOOKMARK) COND((*EQ *KEY)) NBRTRUE(*EQ 1)

