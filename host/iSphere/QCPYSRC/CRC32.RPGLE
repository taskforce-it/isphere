**free
/if not defined(CRC32)
/define CRC32

dcl-s crc32_checkSum_t uns(10) template;

dcl-pr crc32_init uns(10) extproc('CRC32_init');
end-pr;

dcl-pr crc32_add uns(10) extproc('CRC32_add');
  i_crc     like(crc32_checkSum_t) value;
  i_buf     pointer value;
  i_len     uns(10) value;
end-pr;

dcl-pr crc32_finish uns(10) extproc('CRC32_finish');
  i_crc     like(crc32_checkSum_t) value;
end-pr;

dcl-pr crc32_string uns(10) extproc('CRC32_string');
  i_pString pointer options(*string) value;
end-pr;

/endif
