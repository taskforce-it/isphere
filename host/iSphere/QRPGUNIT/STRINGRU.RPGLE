**free
// ==========================================================================
//  iRPGUnit Test: matches()
// ==========================================================================
// >>PRE-COMPILER<<
//   >>CRTCMD<<  RUCRTRPG TSTPGM(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);
//   >>IMPORTANT<<
//     >>PARM<<  BNDSRVPGM(*LIBL/STRING);
//     >>PARM<<  COPTION(*EVENTF);
//   >>END-IMPORTANT<<
//   >>EXECUTE<<
// >>END-PRE-COMPILER<<
// ==========================================================================
ctl-opt nomain option(*SrcStmt: *NoDebugIo);

/include qcpysrc,string
/include qinclude,testcase

dcl-pr testStartsWith extproc(*dclcase) end-pr;
dcl-pr testMid extproc(*dclcase) end-pr;
dcl-pr testEndsWith extproc(*dclcase) end-pr;
dcl-pr testContains extproc(*dclcase) end-pr;
dcl-pr testMatches extproc(*dclcase) end-pr;
dcl-pr testMatchesSpecial extproc(*dclcase) end-pr;
dcl-pr testNotMatched extproc(*dclcase) end-pr;
dcl-pr testLCase extproc(*dclcase) end-pr;
dcl-pr testUCase extproc(*dclcase) end-pr;

dcl-proc testStartsWith export;
  dcl-pi *n end-pi;

  assert(startsWith('': 'DEMO01'): '''DEMO01'' starts with an empty prefix.');
  assert(not startsWith('Prefix': ''): 'An empty string does not start with ''Prefix''');
  assert(not startsWith('DEMO01A': 'DEMO01'): '''DEMO01'' does not start with ''DEMO01A''');
  assert(startsWith('DEMO01': 'DEMO01A'): '''DEMO01A'' does start with ''DEMO01''');

end-proc;

dcl-proc testMid export;
  dcl-pi *n end-pi;

  dcl-s value like(string_t);
  dcl-ds msgInfo likeds(msgInfo_t) inz;

  value = 'DEMO01';

  assert('DEMO01' = mid(value: 1: 6): 'mid() must return ''DEMO01''');
  assert('DEMO0' = mid(value: 1: 5): 'mid() must return ''DEMO0''');
  assert('D' = mid(value: 1: 1): 'mid() must return ''D''');

  assert('EMO01' = mid(value: 2: 5): 'mid() must return ''EMO01''');

  assert('' = mid(value: 7: 0): 'mid() must return an empty string');

  assert('' = mid(value: 1: 0): 'mid() must return an empty string');

  monitor;
    assert('EMO01' = mid(value: 2: 80): 'mid() must return ''EMO01''');
    fail('Must fail, because length exceeds maximum of 5');
  on-error;
    msgInfo = getMonitoredMessage();
    aEqual( 'CPF9897': msgInfo.Id);
    aEqual( 'Length is out of range: 80': msgInfo.txt);
  endmon;

  monitor;
    mid('DEMO01': 0: 10);
    fail('Must fail, because start position is lower than 1');
  on-error;
    msgInfo = getMonitoredMessage();
    aEqual( 'CPF9897': msgInfo.Id);
    aEqual( 'Start position is out of range: 0': msgInfo.txt);
  endmon;

  monitor;
    mid('DEMO01': 8: 1);
    fail('Must fail, because start position is greater than string length + 1');
  on-error;
    msgInfo = getMonitoredMessage();
    aEqual( 'CPF9897': msgInfo.Id);
    aEqual( 'Start position is out of range: 8': msgInfo.txt);
  endmon;

  monitor;
    mid(value: 7: 1);
    fail('Must fail, because mid() returns an empty string at position 7');
  on-error;
    msgInfo = getMonitoredMessage();
    aEqual( 'CPF9897': msgInfo.Id);
    aEqual( 'Length is out of range: 1': msgInfo.txt);
  endmon;

end-proc;

dcl-proc testEndsWith export;
  dcl-pi *n end-pi;

  assert(endsWith('': 'DEMO01'): '''DEMO01'' ends with an empty suffix.');
  assert(not endsWith('Suffix': ''): 'An empty string does not end with ''Suffix''');
  assert(not endsWith('DEMO01A': 'DEMO01'): '''DEMO01'' does not end with ''DEMO01A''');
  assert(endsWith('01': 'DEMO01'): '''DEMO01'' does end with ''01''');

end-proc;

dcl-proc testContains export;
  dcl-pi *n end-pi;

  assert(contains('': 'DEMO01'): 'DEMO01 contains an empty sub-string.');
  assert(not contains('MidStr': ''): 'An empty string does not contains ''MidStr''');
  assert(not contains('DEMO01A': 'DEMO01'): '''DEMO01'' is too short for ''DEMO01A''');
  assert(contains('EMO': 'DEMO01'): '''DEMO01'' does contain ''EMO''');
  assert(contains('01': 'DEMO01'): '''DEMO01'' does end with ''01''');
  assert(contains('DEM': 'DEMO01'): '''DEMO01'' does start with ''DEM''');
  assert(not contains('FOO': 'DEMO01'): '''DEMO01'' does not contain ''FOO''');

end-proc;

dcl-proc testMatches export;
  dcl-pi *n end-pi;

  assert(matches('*'     : 'DEMO01A')  = *on: '''DEMO01A'' must match ''*''');
  assert(matches('DEMO*' : 'DEMO01A')  = *on: '''DEMO01A'' must match ''DEMO*''');
  assert(matches('DEMO*' : 'DEMOXX')   = *on: '''DEMOXX'' must match ''DEMO*''');
  assert(matches('*01'   : 'XXMO01')   = *on: '''XXMO01'' must match ''*01''');
  assert(matches('DE*01' : 'DEXO01')   = *on: '''DEXO01'' must match ''DE*01''');
  assert(matches('*EMO*' : 'XEMO0X')   = *on: '''XEMO0X'' must match ''*EMO*''');
  assert(matches('*E*O*' : 'XEXOXX')   = *on: '''XEXOXX'' must match ''*E*O*''');
  assert(matches('?E*O??': 'XEXO0X')   = *on: '''XEXO0X'' must match ''?E*O??''');
  assert(matches('*E?O*' : 'DEXO01A')  = *on: '''DEXO01A'' must match ''*E?O*''');
  assert(matches('*E?O*' : 'DDEXO01A') = *on: '''DDXMO01A'' must match ''*E?O*''');

end-proc;

dcl-proc testMatchesSpecial export;
  dcl-pi *n end-pi;

  // Empty string.
  assert(matches('***': '') = *on : 'Empty string must match pattern of wildcard characters.');
  assert(matches('???': '') = *off: 'Empty string must not match a 3-character pattern.');

  // Empty Pattern.
  assert(matches('': '')       = *on : 'Empty string must match an empty pattern.');
  assert(matches('': 'DEMO01') = *off: 'A given string must not match an empty pattern.');

end-proc;

dcl-proc testNotMatched export;
  dcl-pi *n end-pi;

  assert(matches('DEMO*': 'XEMO01') = *off: '''XEMO01'' must not match ''DEMO*''');
  assert(matches('*02'  : 'DEMO0X') = *off: '''DEMO0X'' must not match ''*02''');
  assert(matches('DE?01': 'DXMO01') = *off: '''DXMO01'' must not match ''DE?01''');

end-proc;

dcl-proc testLCase export;
  dcl-pi *n end-pi;

  assert('d?mo*' = lcase('D?MO*'): 'D?MO* must become d?mo*');

end-proc;

dcl-proc testUCase export;
  dcl-pi *n end-pi;

  assert('D?MO*' = ucase('d?mo*'): 'd?mo* must become D?MO*');

end-proc;

