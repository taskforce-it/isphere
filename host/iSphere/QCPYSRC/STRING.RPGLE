**free
/if not defined(STRING)
/define STRING

dcl-c MAX_STRING_LENGTH 1024;
dcl-s string_t varchar(MAX_STRING_LENGTH) template;

dcl-pr startsWith ind extproc('STRING_startsWith');
  i_prefix       like(string_t) const;
  i_string       like(string_t) const;
end-pr;

dcl-pr mid like(string_t) extproc('STRING_mid');
  i_string       like(string_t) const;
  i_start        int(10) const;
  i_length       int(10) options(*nopass) const;
end-pr;

dcl-pr endsWith ind extproc('STRING_endsWith');
  i_suffix       like(string_t) const;
  i_string       like(string_t) const;
end-pr;

dcl-pr contains ind extproc('STRING_contains');
  i_subString    like(string_t) const;
  i_string       like(string_t) const;
end-pr;

dcl-pr matches ind extproc('STRING_matches');
  i_pattern      like(string_t) const;
  i_string       like(string_t) const;
  i_ignoreCase   ind options(*nopass);
end-pr;

dcl-pr lcase like(string_t) extproc('STRING_lcase');
  i_string       like(string_t) const;
end-pr;

dcl-pr ucase like(string_t) extproc('STRING_ucase');
  i_string       like(string_t) const;
end-pr;

dcl-pr kill extproc('STRING_kill');
  i_text         varchar(128) value;
end-pr;

/endif
