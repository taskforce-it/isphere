**free
// ==========================================================================
//  iRPGUnit Test: crc32 checksum
// ==========================================================================
//  Online CRC32 calculator:
//    https://crccalc.com/
// ==========================================================================
// >>PRE-COMPILER<<
//   >>CRTCMD<<  RUCRTRPG TSTPGM(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);
//   >>IMPORTANT<<
//     >>PARM<<  BNDSRVPGM(*LIBL/CRC32);
//     >>PARM<<  COPTION(*EVENTF);
//   >>END-IMPORTANT<<
//   >>EXECUTE<<
// >>END-PRE-COMPILER<<
// ==========================================================================
ctl-opt nomain option(*SrcStmt: *NoDebugIo);

/include qcpysrc,crc32
/include qinclude,testcase

dcl-c TEST_BUFFER 'The quick brown fox jumps over the lazy dog.';
dcl-c TEST_BUFFER_CRC 2576481670;

dcl-pr testCrc32Chunks extproc(*dclcase);
end-pr;

dcl-pr testCrc32AllAtOnce extproc(*dclcase);
end-pr;

dcl-pr testCrc32String extproc(*dclcase);
end-pr;

// ==========================================================================
//  Calculate CRC32 of a 44-byte buffer in 5-byte chunks.
// ==========================================================================
dcl-proc testCrc32Chunks export;
  dcl-pi *n end-pi;

  dcl-s buffer char(256);
  dcl-s length int(10);
  dcl-s offset int(10);
  dcl-s chunkSize int(10);

  dcl-s expected like(crc32_checkSum_t);
  dcl-s actual like(crc32_checkSum_t);

  buffer = TEST_BUFFER;
  length = %len(%trimr(buffer));
  chunkSize = 5;

  expected = TEST_BUFFER_CRC;

  actual = crc32_init();

  offset = 0;
  dow (offset < length);
    if (length - offset < chunkSize);
      chunkSize = length - offset;
    endif;
    actual = crc32_add(actual: %addr(buffer) + offset: 1);
    offset += 1;
  enddo;

  actual = crc32_finish(actual);

  assert(expected = actual);

end-proc;

// ==========================================================================
//  Calculate CRC32 of a 44-byte buffer in one step.
// ==========================================================================
dcl-proc testCrc32AllAtOnce export;
  dcl-pi *n end-pi;

  dcl-s buffer char(256);
  dcl-s length int(10);

  dcl-s expected like(crc32_checkSum_t);
  dcl-s actual like(crc32_checkSum_t);

  buffer = TEST_BUFFER;
  length = %len(%trimr(buffer));

  expected = TEST_BUFFER_CRC;

  actual = crc32_init();
  actual = crc32_add(actual: %addr(buffer): length);
  actual = crc32_finish(actual);

  assert(expected = actual);

end-proc;

// ==========================================================================
//  Calculate CRC32 of a RPG string.
// ==========================================================================
dcl-proc testCrc32String export;
  dcl-pi *n end-pi;

  dcl-s string varchar(256);

  dcl-s expected like(crc32_checkSum_t);
  dcl-s actual like(crc32_checkSum_t);

  string = TEST_BUFFER;

  expected = TEST_BUFFER_CRC;

  actual = crc32_string(string);

  assert(expected = actual);

end-proc;

