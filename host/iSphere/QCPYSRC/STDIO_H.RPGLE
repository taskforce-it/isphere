000100210426     /*-                                                                            +
000200210426      * Copyright (c) 2003-2006 Scott C. Klement                                    +
000300210426      * All rights reserved.                                                        +
000400210426      *                                                                             +
000500210426      * Redistribution and use in source and binary forms, with or without          +
000600210426      * modification, are permitted provided that the following conditions          +
000700210426      * are met:                                                                    +
000800210426      * 1. Redistributions of source code must retain the above copyright           +
000900210426      *    notice, this list of conditions and the following disclaimer.            +
001000210426      * 2. Redistributions in binary form must reproduce the above copyright        +
001100210426      *    notice, this list of conditions and the following disclaimer in the      +
001200210426      *    documentation and/or other materials provided with the distribution.     +
001300210426      *                                                                             +
001400210426      * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND      +
001500210426      * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE       +
001600210426      * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE  +
001700210426      * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE     +
001800210426      * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  +
001900210426      * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS     +
002000210426      * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)       +
002100210426      * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  +
002200210426      * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   +
002300210426      * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF      +
002400210426      * SUCH DAMAGE.                                                                +
002500210426      *                                                                             +
002600210426      */                                                                            +
002700210426
002800210426      /if defined(STDIO_H)
002900210426      /eof
003000210426      /endif
003100210426      /define STDIO_H
003200210426
003300210426      *-----------------------------------------------------------------
003400210426      * Data types
003500210426      *-----------------------------------------------------------------
003600210426     D pFILE           S               *   based(prototype_only)
003700210426
003800210426     D stdin           s                   like(pFILE)
003900210426     D                                     import('_C_IFS_stdin')
004000210426     D stdout          s                   like(pFILE)
004100210426     D                                     import('_C_IFS_stdout')
004200210426     D stderr          s                   like(pFILE)
004300210426     D                                     import('_C_IFS_stderr')
004400210426
004500210426      *-----------------------------------------------------------------
004600210426      * fopen():  Open File for buffered reading/writing
004700210426      *
004800210426      *   filename = (input) path to file in the IFS
004900210426      *       mode = (input) various open mode flags.  (see manual)
005000210426      *
005100210426      *  returns *NULL upon error, or a pointer to a FILE structure
005200210426      *-----------------------------------------------------------------
005300210426     D fopen           PR                  ExtProc('_C_IFS_fopen')
005400210426     D                                     like(pFILE)
005500210426     D   filename                      *   value options(*string)
005600210426     D   mode                          *   value options(*string)
005700210426
005800210426
005900210426      *-----------------------------------------------------------------
006000210426      * fgets(): read a string
006100210426      *
006200210426      *    string = (output) string read (null-terminated)
006300210426      *      size = (input) maximum size that can be stored in string
006400210426      *    stream = (input) FILE structure to read data from
006500210426      *
006600210426      * returns a pointer to the string read from the file
006700210426      *      or *NULL upon EOF or error.
006800210426      *-----------------------------------------------------------------
006900210426     D fgets           PR              *   ExtProc('_C_IFS_fgets')
007000210426     D   string                        *   value
007100210426     D   size                        10I 0 value
007200210426     D   stream                            like(pFILE) value
007300210426
007400210426      *-----------------------------------------------------------------
007500210426      * fputs(): Write string
007600210426      *
007700210426      *    string = (input) string to write to file
007800210426      *    stream = (input) FILE structure designating the file to
007900210426      *                write to.
008000210426      *
008100210426      *  returns a non-negative value if successful
008200210426      *       or -1 upon error
008300210426      *-----------------------------------------------------------------
008400210426     D fputs           PR            10I 0 ExtProc('_C_IFS_fputs')
008500210426     D   string                        *   value options(*string)
008600210426     D   stream                            like(pFILE) value
008700210426
008800210426      *-----------------------------------------------------------------
008900210426      * fread(): Read items
009000210426      *
009100210426      *    data = (input) data items to read
009200210426      *    size = (input) size of each data item
009300210426      *   count = (input) number of data items
009400210426      *  stream = (input) pointer to FILE structure to read from
009500210426      *
009600210426      * returns the number of full items read, a short count
009700210426      *         indicates an error.
009800210426      *-----------------------------------------------------------------
009900210426     D fread           PR            10U 0 ExtProc('_C_IFS_fread')
010000210426     D   data                          *   value
010100210426     D   size                        10U 0 value
010200210426     D   count                       10U 0 value
010300210426     D   stream                            like(pFILE) value
010400210426
010500210426      *-----------------------------------------------------------------
010600210426      * fwrite(): Write items
010700210426      *
010800210426      *    data = (input) data items to write
010900210426      *    size = (input) size of each data item
011000210426      *   count = (input) number of data items
011100210426      *  stream = (input) pointer to FILE structure to write to
011200210426      *
011300210426      * returns the number of full items written.  A short count
011400210426      *         indicates an error.
011500210426      *-----------------------------------------------------------------
011600210426     D fwrite          PR            10U 0 ExtProc('_C_IFS_fwrite')
011700210426     D   data                          *   value
011800210426     D   size                        10U 0 value
011900210426     D   count                       10U 0 value
012000210426     D   stream                            like(pFILE) value
012100210426
012200210426      *-----------------------------------------------------------------
012300210426      * fflush():  Flush a stream
012400210426      *
012500210426      *    stream = (input) pointer to FILE structure to flush
012600210426      *
012700210426      * returns 0 if successful, -1 otherwise
012800210426      *-----------------------------------------------------------------
012900210426     D fflush          PR            10U 0 ExtProc('_C_IFS_fflush')
013000210426     D   stream                            like(pFILE) value
013100210426
013200210426      *-----------------------------------------------------------------
013300210426      * fclose(): Close File
013400210426      *
013500210426      *    stream = (input) pointer to FILE structure to close
013600210426      *-----------------------------------------------------------------
013700210426     D fclose          PR            10I 0 ExtProc('_C_IFS_fclose')
013800210426     D   stream                            like(pFILE) value
013900210426
014000210426      *-----------------------------------------------------------------
014100210426      * fseek(): Reposition a file
014200210426      *
014300210426      *    stream = (input) pointer to FILE structure to reposition
014400210426      *    offset = (input) offset from "whence" in bytes
014500210426      *    whence = (input) position to begin offset at, can be
014600210426      *                SEEK_SET, SEEK_END or SEEK_CUR
014700210426      *
014800210426      * returns 0 if successful, -1 otherwise
014900210426      *-----------------------------------------------------------------
015000210426     D fseek           PR            10I 0 ExtProc('_C_IFS_fseek')
015100210426     D   stream                            like(pFILE) value
015200210426     D   offset                      10I 0 value
015300210426     D   whence                      10I 0 value
015400210426
015500210426      /if not defined(SEEK_WHENCE_VALUES)
015600210426     D SEEK_SET        C                   CONST(0)
015700210426     D SEEK_CUR        C                   CONST(1)
015800210426     D SEEK_END        C                   CONST(2)
015900210426      /define SEEK_WHENCE_VALUES
016000210426      /endif
016100210426
016200210426      *-----------------------------------------------------------------
016300210426      * ftell(): Get Current position
016400210426      *
016500210426      *    stream = (input) pointer to FILE structure
016600210426      *
016700210426      * returns the file position, or -1 upon error
016800210426      *-----------------------------------------------------------------
016900210426     D ftell           PR            10I 0 ExtProc('_C_IFS_ftell')
017000210426     D   stream                            like(pFILE) value
017100210426
017200210426      *-----------------------------------------------------------------
017300210426      * fdopen():  Upgrade a file descriptor to a buffered stream
017400210426      *
017500210426      *      fildes = (input) file descriptor to upgrade
017600210426      *        mode = (input) mode, equivalent to the mode specified
017700210426      *                   on the fopen() API, except that it must be
017800210426      *                   compatible with the flags that were used on
017900210426      *                   the open() API.
018000210426      *
018100210426      *  Returns a new pointer to a buffered stream I/O file
018200210426      *       or *NULL upon error.
018300210426      *-----------------------------------------------------------------
018400210426     D fdopen          pr              *   extproc('fdopen')
018500210426     D   fildes                      10I 0 value
018600210426     D   mode                          *   value options(*string)
