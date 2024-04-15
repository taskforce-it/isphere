**free
// ------------------------------------------------------------------------------------------
//  Synchronize Source Members API
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2024 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------
//    >>PRE-COMPILER<<
//      >>CRTCMD<<  CRTRPGMOD    MODULE(&LI/&OB) +
//                               SRCFILE(&SL/&SF) SRCMBR(&SM);
//      >>COMPILE<<
//        >>PARM<< OPTION(*EVENTF);
//        >>PARM<< TRUNCNBR(*NO);
//        >>PARM<< DBGVIEW(*LIST);
//        >>PARM<< TGTRLS(&TR);
//      >>END-COMPILE<<
//      >>EXECUTE<<
//    >>END-PRE-COMPILER<<
// ------------------------------------------------------------------------------------------
ctl-opt NoMain;
/include qcpysrc,h_spec
/include qcpysrc,copyright

// Own Prototypes
/include QCPYSRC,STRING

// Prototypes
/include QCPYSRC,TYPES
/include QCPYSRC,QMHSNDPM
/include QCPYSRC,ERRC0100
/include QCPYSRC,QLGCNVCS

dcl-pr kill extproc(*dclcase);
  i_text         varchar(128) value;
end-pr;

// ==========================================================================================
//  Tests if the specified string starts with the specified prefix.
// ==========================================================================================

dcl-proc startsWith export;
  dcl-pi *n ind;
    i_prefix       like(string_t) const;
    i_string       like(string_t) const;
  end-pi;

  // ------------------------------------------------------------------------------------------

  if (%len(i_string) = 0  or
      %len(i_string) < %len(i_prefix));
     return *off;
  endif;

  if (%len(i_prefix) = 0);
    return *on;
  endif;

  if (%subst(i_string: 1: %len(i_prefix)) = i_prefix);
     return *on;
  endif;

  return *off;

end-proc;

// ==========================================================================================
//  Returns the sub string that starts at a given position.
//  Works the same as Java String.substring(start, end), but uses parameters
//  'start' and 'length'.
//  See:
//    - Java unit test: biz.isphere.junit.TestString.testMid()
//    - iRPGUnit test: STRINGRU.testMid()
// ==========================================================================================

dcl-proc mid export;
  dcl-pi *n like(string_t);
    i_string       like(string_t) const;
    i_start        int(10) const;
    i_length       int(10) options(*nopass) const;
  end-pi;

  // ------------------------------------------------------------------------------------------

  dcl-s rtnString like(string_t);
  dcl-s length int(10);

  // ------------------------------------------------------------------------------------------

  if (%parms() >= %parmnum(i_length));
    length = i_length;
  else;
    length = %len(i_string) - i_start + 1;
  endif;

  if (i_start > %len(i_string) + 1 or i_start < 1);
    kill('Start position is out of range: ' + %char(i_start));
  endif;

  if (i_start + length - 1 > %len(i_string));
  //  length = %len(i_string) - i_start + 1;
    kill('Length is out of range: ' + %char(i_length));
  endif;

  if (length < 0);
    kill('Length is out of range: ' + %char(i_length));
  endif;

  if (length = 0);
    return '';
  endif;

  rtnString = %subst(i_string: i_start: length);

  return rtnString;

end-proc;

// ==========================================================================================
//  Tests if the specified string ends with the specified suffix.
// ==========================================================================================

dcl-proc endsWith export;
  dcl-pi *n ind;
    i_suffix       like(string_t) const;
    i_string       like(string_t) const;
  end-pi;

  // ------------------------------------------------------------------------------------------

  if (%len(i_string) = 0  or
      %len(i_string) < %len(i_suffix));
     return *off;
  endif;

  if (%len(i_suffix) = 0);
    return *on;
  endif;

  if (%subst(i_string: %len(i_string) - %len(i_suffix) + 1) = i_suffix);
     return *on;
  endif;

  return *off;

end-proc;

// ==========================================================================================
//  Tests if the specified string contains a given sub-string.
// ==========================================================================================

dcl-proc contains export;
  dcl-pi *n ind;
    i_subString    like(string_t) const;
    i_string       like(string_t) const;
  end-pi;

  // ------------------------------------------------------------------------------------------

  if (%len(i_string) = 0  or
      %len(i_string) < %len(i_subString));
     return *off;
  endif;

  if (%len(i_subString) = 0);
    return *on;
  endif;

  if (%scan(i_subString: i_string) > 0);
     return *on;
  endif;

  return *off;

end-proc;

// ==========================================================================================
//  Performs a generic, case-sensitive comparison of strings.
//  Wildcard characters:
//   *    group of characters
//   ?    single character
// ==========================================================================================
//  Parameters:
//   i_pattern     - Mask. Optionally containing wildcard characters.
//   i_string      - The string to be compared with i_mask.
//   i_ignoreCase  - Specifies whether or not that case matters.
//
//  Returns:
//   isEqual       - Returns *on when the strings are equal, else *off.
// ==========================================================================================

dcl-proc matches export;
  dcl-pi *n ind;
    i_pattern      like(string_t) const;
    i_string       like(string_t) const;
    i_ignoreCase   ind options(*nopass);
  end-pi;

  dcl-s string like(i_string);
  dcl-s pattern like(i_pattern);
  dcl-s ignoreCase like(i_ignoreCase);

  // ------------------------------------------------------------------------------------------

  if (%parms() >= %parmnum(i_ignoreCase));
    ignoreCase = i_ignoreCase;
  else;
    ignoreCase = *off;
  endif;

  if (ignoreCase);
    pattern = lcase(i_pattern);
    string = lcase(i_string);
    return matchesInternally(%trimr(pattern): %trimr(string));
  endif;

  // Call generic string compare procedure
  return matchesInternally(%trimr(i_pattern): %trimr(i_string));

end-proc;

// ==========================================================================================
//  Compare strings using wildcard characters. (Compare generic)
// ==========================================================================================
dcl-proc matchesInternally;
  dcl-pi *n ind;
    i_pattern      like(string_t) const;
    i_string       like(string_t) const;
    i_wildcard     char(2) options(*nopass) const;
  end-pi;

  // Fields for optional parameters
  dcl-ds wildcard qualified;
    group   char(1) inz;
    single  char(1) inz;
  end-ds;

  // Local fields
  dcl-s string like(i_string);
  dcl-s pattern like(i_pattern);

  // ------------------------------------------------------------------------------------------

  // Process optional parameter values
  if (%parms() >= %parmnum(i_wildcard));
    wildcard = i_wildcard;
  else;
    wildcard.group  = '*'; // Ignore group of characters
    wildcard.single = '?'; // Ignore single character
  endif;

  // Get prepared
  string = i_string;
  pattern = i_pattern;

  // Do the comparison...
  if (%len(pattern) = %len(string) and pattern = string);
    return *on; // Exact match. Also covers empty string and empty pattern.
  elseif (%len(string) = 0);
    if (%check(wildcard.group: pattern) = 0);
      return *on; // Match. Pattern is all of '*'.
    else;
      return *off; // No match, because pattern specifies specific characters.
    endif;
  elseif (%len(pattern) = 0);
    return *off; // No match, because string is not empty.
  elseif (pattern = wildcard.group);
    return *on; // Match. Pattern is '*'.
  elseif (startsWith(wildcard.single: pattern));
    // Proceed with next character of pattern and string.
    return matchesInternally(mid(pattern: 2): mid(string: 2): wildcard);
  elseif (endsWith(wildcard.single: pattern));
    // Proceed with previous character of pattern and string.
    return matchesInternally(%subst(pattern: 1: %len(pattern) - 1)
                        : %subst(string: 1: %len(string) - 1)
                        : wildcard);
  elseif (startsWith(wildcard.group: pattern));
    // Test next character of pattern.
    if (matchesInternally(mid(pattern: 2): string: wildcard));
      return *on; // Match. Next character of pattern matches string.
    else;
      // Test next character of string.
      return matchesInternally(pattern: mid(string: 2): wildcard);
    endif;
  elseif (endsWith(wildcard.group: pattern));
    // Test second-last character of pattern.
    if (matchesInternally(%subst(pattern: 1: %len(pattern) - 1): string: wildcard));
      return *on; // Match. Second last character of pattern matches string.
    else;
      // Test second last character of string.
      return matchesInternally(pattern: %subst(string: 1: %len(string) - 1): wildcard);
    endif;
  elseif (%subst(pattern: 1: 1) = %subst(string: 1: 1));
    // Match. Proceed with next character of pattern and string.
    return matchesInternally(mid(pattern: 2): mid(string: 2): wildcard);
  endif;

  return *off;

end-proc;

// ==========================================================================================
//  Converts a given string to lower case.
// ==========================================================================================
dcl-proc lcase export;
  dcl-pi *n like(string_t);
    i_string       like(string_t) const;
  end-pi;

  //Return value
  dcl-s string varchar(MAX_STRING_LENGTH);

  // Local fields
  dcl-s stringBuf char(4096);
  dcl-ds errCode likeds(errCode_t) inz;
  dcl-ds reqCtrlBlk likeds(reqCtrlBlk_t) inz;

  // ------------------------------------------------------------------------------------------

  if (i_string = '');
    return i_string;
  endif;

  reqCtrlBlk.type = CVTCASE_TYPE_ccsid;
  reqCtrlBlk.CCSID = CVTCASE_CCSID_Job;
  reqCtrlBlk.case = CVTCASE_toLower;
  reqCtrlBlk.reserved = *allx'00';
  clear errCode;

  QlgConvertCase(reqCtrlBlk: i_string: stringBuf: %len(i_string): errCode);
  string = %subst(stringBuf: 1: %len(i_string));

  return string;

end-proc;

// ==========================================================================================
//  Converts a given string to upper case.
// ==========================================================================================
dcl-proc ucase export;
  dcl-pi *n like(string_t);
    i_string       like(string_t) const;
  end-pi;

  //Return value
  dcl-s string varchar(MAX_STRING_LENGTH);

  // Local fields
  dcl-s stringBuf char(4096);
  dcl-ds errCode likeds(errCode_t) inz;
  dcl-ds reqCtrlBlk likeds(reqCtrlBlk_t) inz;

  // ------------------------------------------------------------------------------------------

  if (i_string = '');
    return i_string;
  endif;

  reqCtrlBlk.type     = CVTCASE_TYPE_ccsid;
  reqCtrlBlk.CCSID    = CVTCASE_CCSID_Job;
  reqCtrlBlk.case     = CVTCASE_toUpper;
  reqCtrlBlk.reserved = *allx'00';
  clear errCode;

  QlgConvertCase(reqCtrlBlk: i_string: stringBuf: %len(i_string): errCode);
  string = %subst(stringBuf: 1: %len(i_string));

  return string;

end-proc;

// ==========================================================================================
//  Kills the program by sending an *ESCAPE message to the calling program.
// ==========================================================================================
dcl-proc kill;
  dcl-pi *n;
    i_text         varchar(128) value;
  end-pi;

  // Local fields
  dcl-s msgKey char(4);

  dcl-ds qMsgF likeds(qObj_t);

  // ------------------------------------------------------------------------------------------

  clear qMsgF;
  qMsgF.name = 'QCPFMSG';
  qMsgF.lib  = '*LIBL';

  clear ERRC0100;

  QMHSNDPM('CPF9897': qMsgF: i_text: %len(i_text): QMH_MT_ESCAPE
           : QMH_CSE_CTLBDY: 1: msgKey: ERRC0100);

end-proc;

