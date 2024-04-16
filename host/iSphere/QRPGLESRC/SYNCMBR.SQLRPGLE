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
//
//  Synchronize Source Members API
//
// ------------------------------------------------------------------------------------------
//  Notes:
//   Mode is one of:
//     MODE_LEFT    Process members of the left side of the comparison editor.
//     MODE_RIGHT   Process members of the right side of the comparison editor.
// ------------------------------------------------------------------------------------------
//  Execution time:
//    Development IBM i: 60916 Members in ~4 Minutes is about 40ms/member.
// ------------------------------------------------------------------------------------------
//  STRPREPRC Compile Options:
//    >>PRE-COMPILER<<
//      >>CRTCMD<< CRTSQLRPGI   OBJ(&LI/&OB) SRCFILE(&SL/&SF) SRCMBR(&SM);
//        >>COMPILE<<
//          >>PARM<<  OBJTYPE(*MODULE);
//          >>PARM<<  COMMIT(*NONE);
//          >>PARM<<  TGTRLS(&TR);
//          >>PARM<<  CLOSQLCSR(*ENDMOD);
//          >>PARM<<  DBGVIEW(*NONE);
//          >>PARM<<  RPGPPOPT(*LVL2);
//          >>PARM<<  OPTION(*EVENTF);
//          >>PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');
//          --PARM<<  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');
//        >>END-COMPILE<<
//        >>EXECUTE<<
//    >>END-PRE-COMPILER<<
// ------------------------------------------------------------------------------------------
ctl-opt NoMain;
/include qcpysrc,h_spec
/include qcpysrc,copyright

// ------------------------------------------------------------------------------------------

/include QCPYSRC,TYPES       // Common type definitions
/include QCPYSRC,STRING      // Procedure prototypes for modul 'STRING'
/include QCPYSRC,SYNCMBR     // Procedure prototypes for modul 'SYNCMBR'
/include QCPYSRC,NBRRNG      // Procedure prototypes for modul 'NBRRNG'
/include QCPYSRC,CRC32       // Procedure prototypes for modul 'CRC32'
/include QCPYSRC,QUSCRTUS    // Procedure prototype for procedure 'QUSRMBRD'
/include QCPYSRC,QUSDLTUS    // Procedure prototype for procedure 'QUSDLTUS'
/include QCPYSRC,QUSPTRUS    // Procedure prototype for procedure 'QUSPTRUS'
/include QCPYSRC,QUSRMBRD    // Procedure prototype for procedure 'QUSRMBRD'
/include QCPYSRC,QUSLMBR     // Procedure prototype for procedure 'QUSLMBR'
/include QCPYSRC,QUSRMBRD    // Procedure prototype for procedure 'QUSRMBRD'
/include QCPYSRC,GHDR0300    // Data structure for format 'GHDR0300' for generic header
/include QCPYSRC,MBRL0320    // Data structure for format 'MBRL0320' for procedure 'QUSLMBR'
/include QCPYSRC,MBRD0200    // Data structure for format 'MBRD0200' for procedure 'QUSRMBRD'
/include QCPYSRC,QUSLOBJ     // Procedure prototype for procedure 'QUSLOBJ'
/include QCPYSRC,OBJL0200    // Data structure for format 'OBJL0200' for procedure 'QUSLOBJ'
/include QCPYSRC,QDBRTVFD    // Retrieve Database File Description (QDBRTVFD) API
/include QCPYSRC,IFSIO_H     // IFC C Header
/include QCPYSRC,REGEX       // Regular expression functions
/include QCPYSRC,ICONV       // Character translation functions
/include QCPYSRC,QUSRJOBI    // Procedure prototype for procedure 'QUSRJOBI'
/include QCPYSRC,LOCALE      // Locale functions
/include QCPYSRC,QMHSNDPM    // Send Program Message (QMHSNDPM) API
/include QCPYSRC,ERRC0100    // Format ERRC0100

// Global type definitions

dcl-s pattern_t varchar(1024) template;

dcl-ds hIconv_t qualified template;
  isInit     ind;
  job_ccsid  int(10);
  nl_ccsid   int(10);
  iconv      likeds(iconv_t);
end-ds;

dcl-ds hRegex_t qualified template;
  isInit     ind;
  regex      likeds(regex_t);
end-ds;

// Internal prototypes for lower/upper case names

dcl-pr buildPath varchar(256) extproc(*dclcase);
  i_library      varchar(10) Options(*trim) const;
  i_file         varchar(10) Options(*trim) const;
  i_member       varchar(10) Options(*trim) const;
end-pr;

dcl-pr calculateChecksum like(crc32_checkSum_t) extproc(*dclcase);
  i_path         varchar(256) Options(*trim) const;
end-pr;

dcl-pr crtUsrSpc likeds(errCode_t) extproc(*dclcase);
  i_qUsrSpc      likeds(qObj_t) const;
end-pr;

dcl-pr cvtApiTimestampToRpg timestamp extproc(*dclcase);
  i_apiTimestamp char(13) const;
end-pr;

dcl-pr dltUsrSpc extproc(*dclcase);
  i_qUsrSpc      likeds(qObj_t) const;
end-pr;

dcl-pr isCanceled ind extproc(*dclcase);
  i_handle         int(10) const;
end-pr;

dcl-pr updateStatusResolvedFiles extproc(*dclcase);
  i_handle         int(10) const;
  i_count          int(10) const;
end-pr;

dcl-pr updateStatusResolvedMembers extproc(*dclcase);
  i_handle         int(10) const;
  i_count          int(10) const;
end-pr;

dcl-pr updateStatusMembersFound extproc(*dclcase);
  i_handle         int(10) const;
  i_count          int(10) const;
end-pr;

dcl-pr retrieveJobCcsid int(10);
end-pr;

dcl-pr initIconv likeds(hIconv_t) extproc(*dclcase);
end-pr;

dcl-pr freeIconv extproc(*dclcase);
  io_hIconv        likeds(hIconv_t);
end-pr;

dcl-pr cvtCcsid like(string_t) extproc(*dclcase);
  i_string         like(string_t) value;
end-pr;

dcl-pr initRegex likeds(hRegex_t) extproc(*dclcase);
  i_pattern        like(string_t) const;
end-pr;

dcl-pr freeRegex extproc(*dclcase);
  io_hRegex        likeds(hRegex_t);
end-pr;

dcl-pr matchRegex ind extproc(*dclcase);
  i_string         like(string_t) const;
  io_hRegex        likeds(hRegex_t);
end-pr;

dcl-pr kill extproc(*dclcase);
  i_text         varchar(128) value;
end-pr;

// Other External Prototypes

// Convert Hex to Character (CVTHC)
dcl-pr cvthc extproc('cvthc');
  o_pHexResult   pointer value;
  i_pCharInp     pointer value;
  i_numNibbles   int(10) value;
end-pr;

dcl-pr errno pointer extproc('__errno');
end-pr;

// strerror -- Set Pointer to Runtime Error Message
dcl-pr strerror pointer extproc('strerror');
  i_errno        int(10) value;
end-pr;

// Global values

dcl-ds g_hIconv likeds(hIconv_t) inz;

// ==========================================================================================
//  Get Handle
// ==========================================================================================

dcl-proc SYNCMBR_getHandle export;
  dcl-pi *n zoned(10: 0);
  end-pi;

  // Return value
  dcl-s handle zoned(10: 0);

  // ----------------------------------------------------------------------------------------

  // Set handle
  handle = NBRRNG_getNumber('HANDLE');

  // Write file 'Status'
  Exec SQL
    INSERT
      INTO
    SYNCMBRS
      (
       XSHDL,
       XSCNTFILE,
       XSCNTMBR,
       XSCNTFND,
       XSCNL
      )
    VALUES
      (
       :handle,
       0,
       0,
       0,
       '*NO'
      );

  // Initialize character translation
  if (g_hIconv.isInit and g_hIconv.job_ccsid <> retrieveJobCcsid());
    freeIconv(g_hiconv);
  endif;

  if (not g_hIconv.isInit);
    g_hIconv = initIconv();
  endif;

  // Leave procedure
  return handle;

end-proc;

// ==========================================================================================
//  Resolves Generic Files.
// ==========================================================================================

dcl-proc SYNCMBR_resolveGenericFiles export;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Local field declarations
  dcl-ds SYNCMBRWRF extname('SYNCMBRW') qualified inz end-ds;
  dcl-ds qUsrSpc likeds(qObj_t) inz;
  dcl-s pUsrSpc pointer;
  dcl-ds qFile likeds(qObj_t) inz;
  dcl-s member char(10);
  dcl-s rightLib char(10);
  dcl-s doGenericStringCompare ind;
  dcl-s mask varchar(10);
  dcl-s counter int(10);
  dcl-ds OBJL0200 likeds(objl0200_t) based(pOBJL0200);
  dcl-ds ffd likeds(Qdb_Qdbfh_t) inz;
  dcl-ds qFileRet likeds(qObj_t) inz;
  dcl-s isSourceFile ind;
  dcl-ds errCode likeds(errCode_t) inz(*likeds);

  dcl-s countFilesResolved int(10);

  dcl-ds lastUpdateFilesResolved qualified inz;
    time    timestamp;
    count   int(10);
  end-ds;

  // ----------------------------------------------------------------------------------------

  // Declare cursor
  Exec SQL
     DECLARE
        GENERIC_FILES
     CURSOR FOR
        SELECT
           *
        FROM
           SYNCMBRW
        WHERE
           XWHDL = :i_handle AND
            ((TRIM(XWLEFTFILE) LIKE '*%' OR TRIM(XWLEFTFILE) LIKE '%*' OR
              TRIM(XWLEFTFILE) LIKE '%*%' OR XWLEFTFILE = '*') or
             (TRIM(XWRGHTFILE) LIKE '*%' OR TRIM(XWRGHTFILE) LIKE '%*' OR
              TRIM(XWRGHTFILE) LIKE '%*%' OR XWRGHTFILE = '*'))
        FOR UPDATE;

  // Open cursor
  Exec SQL
     OPEN
        GENERIC_FILES;

  clear lastUpdateFilesResolved;
  lastUpdateFilesResolved.time = %timestamp();

  dow (not isCanceled(i_handle));

    // Fetch from cursor
    Exec SQL
       FETCH NEXT FROM
          GENERIC_FILES
       INTO
          :SYNCMBRWRF;

    // Leave condition
    if (SQLCOD = 100 or SQLCOD < 0);
      leave;
    endif;

    // Check if process has to be canceled
    if (isCanceled(i_handle));
      leave;
    endif;

    // Delete generic input element
    Exec SQL
       DELETE FROM
          SYNCMBRW
       WHERE
          CURRENT OF
             GENERIC_FILES;

    // Update each 1500 milliseconds
    countFilesResolved += 1;
    if (%diff(%timestamp(): lastUpdateFilesResolved.time: *mseconds) > 1500);

      // Update file 'Status': Count Files
      updateStatusResolvedFiles(i_handle: countFilesResolved);
      lastUpdateFilesResolved.count = countFilesResolved;

      if (isCanceled(i_handle));
        leave;
      endif;

    endif;

    // Create user space
    qUsrSpc.name = 'SYNCMBR';
    qUsrSpc.lib = 'QTEMP';
    errCode = crtUsrSpc(qUsrSpc);
    if (errCode.bytAvl = 0);

      // Retrieve pointer to user space
      Reset errCode;
      QUSPTRUS(qUsrSpc: pUsrSpc: errCode);

      if (errCode.bytAvl = 0);

        // List files
        select;
        when (i_mode = MODE_LEFT);
          qFile.name = SYNCMBRWRF.XWLEFTFILE;
          qFile.lib = SYNCMBRWRF.XWLEFTLIB;
          member = SYNCMBRWRF.XWMBR;
          rightLib = SYNCMBRWRF.XWRGHTLIB;
        when (i_mode = MODE_RIGHT);
          qFile.name = SYNCMBRWRF.XWRGHTFILE;
          qFile.lib = SYNCMBRWRF.XWRGHTLIB;
          member = SYNCMBRWRF.XWMBR;
          rightLib = SYNCMBRWRF.XWRGHTLIB;
        other;
          // TODO: incorrect mode
        endsl;

        if (contains('*': qFile.name));
          mask = ucase(%trimr(qFile.name));
          doGenericStringCompare = *on;
          qFile.name = '*ALL';
        else;
          doGenericStringCompare = *off;
        endif;

        Reset errCode;
        QUSLOBJ(
         qUsrSpc :      // --> Qual. user space
         'OBJL0200' :   // --> Format name
         qFile :        // --> Qual. file
         '*FILE':       // --> Object type
         errCode        // <-> Error code
        );

        if (errCode.bytAvl = 0);

          // Set pointer of generic header
          GHDR0300Ptr = pUsrSpc;

          for counter = 1 To GHDRNbrEtry;

            // Set pointer of list area
            pOBJL0200 =
              pUsrSpc +                       // Addr. user space
              GHDROffLstSct +                 // Offset to list area
              ((counter - 1) * GHDRSizeEtry); // Offset to record

            // Ignore all non-PF files.
            if (objl0200.extObjAttr <> 'PF');
              iter;
            endif;

            // Ignore all non-source files.
            reset errCode;
            qFile.name = objl0200.obj;
            qFile.lib = objl0200.lib;
            QDBRTVFD(ffd: %size(ffd): qFileRet: 'FILD0100'
                     : qFile: '*FIRST': '0': '*LCL': '*EXT': errCode);

            isSourceFile = %bitand(ffd.Qdbfhflg: QDBFHFSU) = QDBFHFSU;
            if (not isSourceFile);
              iter;
            endif;

            // Compare current file with selection mask.
            if (doGenericStringCompare);
              if (not matches(mask: %trimr(OBJL0200.OBJ)));
                iter;
              endif;
            endif;

            // Append record to work file.
            select;
            when (i_mode = MODE_LEFT);
              Exec SQL
                INSERT INTO
                  SYNCMBRW
                    (
                    XWHDL,
                    XWMBR,
                    XWLEFTLIB,
                    XWLEFTFILE,
                    XWLEFTTYPE,
                    XWLEFTLCHG,
                    XWLEFTCRC
                    )
                VALUES
                  (
                  :i_handle,
                  :member,                -- Member Name
                  :objl0200.lib,          -- Left Member Source File Library
                  :objl0200.obj,          -- Left Member Source File
                  :SRCTYPE_NULL,          -- Left Member Source Type
                  :LAST_CHANGED_NULL,     -- Left Member Last Changed
                  :CHECKSUM_NULL          -- Left Member Checksum
                  );

            when (i_mode = MODE_RIGHT);
              Exec SQL
                INSERT INTO
                  SYNCMBRW
                    (
                    XWHDL,
                    XWMBR,
                    XWRGHTLIB,
                    XWRGHTFILE,
                    XWRGHTTYPE,
                    XWRGHTLCHG,
                    XWRGHTCRC
                    )
                VALUES
                  (
                  :i_handle,
                  :member,                -- Member Name
                  :objl0200.lib,          -- Right Member Source File Library
                  :objl0200.obj,          -- Right Member Source File
                  :SRCTYPE_NULL,          -- Right Member Source Type
                  :LAST_CHANGED_NULL,     -- Right Member Last Changed
                  :CHECKSUM_NULL          -- Right Member Checksum
                  );

            other;
              // TODO: incorrect mode
            endsl;

          endfor;

        endif;


      endif;

      // Delete user space
      dltUsrSpc(qUsrSpc);

    endif;

  enddo;

on-exit;

  // Report "Done" to plug-in
  updateStatusResolvedFiles(i_handle: -1);

  // Close cursor
  Exec SQL
     CLOSE
        GENERIC_FILES;

end-proc;

// ==========================================================================================
//  Resolves Generic Members.
// ==========================================================================================

dcl-proc SYNCMBR_resolveGenericMembers export;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
    i_memberFilter char(1024) const;
    i_isRegEx      ind const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Local field declarations
  dcl-ds SYNCMBRWRF extname('SYNCMBRW') qualified inz end-ds;
  dcl-ds qUsrSpc likeds(qObj_t) inz;
  dcl-s pUsrSpc pointer;
  dcl-ds qFile likeds(qObj_t) inz;
  dcl-s member char(10);
  dcl-s counter int(10);
  dcl-s checkSum like(crc32_checkSum_t);
  dcl-s lastChanged timestamp;
  dcl-s doGenericStringCompare ind;
  dcl-s mask varchar(10);
  dcl-ds errCode likeds(errCode_t) inz(*likeds);

  dcl-c FILTER_DELIMITER_CHARS ' ,;';

  dcl-s filterItem varchar(21); // = name.type

  dcl-s numFilters int(10);
  dcl-ds nameTypeFilter qualified dim(50) inz;
    isNegated  ind;
    name       varchar(10);
    srcType    varchar(10);
  end-ds;

  dcl-s parts varchar(10) dim(2);

  dcl-s pattern like(pattern_t);
  dcl-ds hRegex likeds(hRegex_t) inz;

  dcl-s i int(10);
  dcl-s isNegated ind;
  dcl-s isFound ind;
  dcl-s fullMemberName varchar(21);
  dcl-s memberNameMask varchar(21);

  dcl-s countMembersResolved int(10);

  dcl-ds lastUpdateMembersResolved qualified inz;
    time    timestamp;
    count   int(10);
  end-ds;

  dcl-s countMembersFound int(10);

  dcl-ds lastUpdateFound qualified inz;
    time    timestamp;
    count   int(10);
  end-ds;

  // ----------------------------------------------------------------------------------------

  if (i_isRegEx);
    if (%len(i_memberFilter) >= 2);
      pattern = %trimr(i_memberFilter);
      hRegex = initRegex(pattern);
    else;
      pattern = '';
      clear hRegex;
      hRegex.isInit = *off;
    endif;
  else;
    clear hRegex;
    hRegex.isInit = *off;
    numFilters = 0;
    isNegated = *off;
    for-each filterItem in %split(i_memberFilter: FILTER_DELIMITER_CHARS);

      // Starting with a pipe (|) all following
      // entries are negated.
      if (startsWith('|': filterItem));
        if (%len(filterItem) >= 2);
          filterItem = %subst(filterItem: 2);
        else;
          filterItem = '';
        endif;
        isNegated = *on;
      endif;

      if (filterItem = '');
        filterItem = '*.*';
      endif;

      // First idea was to induvidially compare name and source type.
      // There the name and source type were stored separately.
      if (%len(%trim(filterItem)) > 0);
        parts = %split(filterItem: '.'); // = name.type
        if (parts(1) <> '' or parts(2) <> '' );
          numFilters += 1;
          nameTypeFilter(numFilters).isNegated = isNegated;
          if (parts(1) <> '');
            nameTypeFilter(numFilters).name = %trim(ucase(parts(1)));
          else;
            nameTypeFilter(numFilters).name = '*';
          endif;
          if (parts(2) <> '');
            nameTypeFilter(numFilters).srcType = %trim(ucase(parts(2)));
          else;
            nameTypeFilter(numFilters).srcType = '*';
          endif;
        endif;
      endif;
    endfor;
  endif;

  // Declare cursor
  Exec SQL
     DECLARE
        GENERIC_MEMBERS
     CURSOR FOR
        SELECT
           *
        FROM
           SYNCMBRW
        WHERE
           XWHDL = :i_handle AND
           (TRIM(XWMBR) LIKE '*%' OR TRIM(XWMBR) LIKE '%*' OR TRIM(XWMBR) LIKE '%*%' OR XWMBR = '*')
        FOR UPDATE;

  // Open cursor
  Exec SQL
     OPEN
        GENERIC_MEMBERS;

  clear lastUpdateMembersResolved;
  lastUpdateMembersResolved.time = %timestamp();

  clear lastUpdateFound;
  lastUpdateFound.time = %timestamp();

  dow (not isCanceled(i_handle));

    // Fetch from cursor
    Exec SQL
       FETCH NEXT FROM
          GENERIC_MEMBERS
       INTO
          :SYNCMBRWRF;

    // Leave condition
    if (SQLCOD = 100 or SQLCOD < 0);
      leave;
    endif;

    // Delete generic input element
    Exec SQL
       DELETE FROM
          SYNCMBRW
       WHERE
          CURRENT OF
             GENERIC_MEMBERS;

    // Update each 1500 milliseconds
    countMembersResolved += 1;
    if (%diff(%timestamp(): lastUpdateMembersResolved.time: *mseconds) > 1500);

      // Update file 'Status': Count Resolved Members
      updateStatusResolvedMembers(i_handle: countMembersResolved);
      lastUpdateMembersResolved.count = countMembersResolved;

      if (isCanceled(i_handle));
        leave;
      endif;

    endif;

    // Create user space
    qUsrSpc.name = 'SYNCMBR';
    qUsrSpc.lib = 'QTEMP';
    errCode = crtUsrSpc(qUsrSpc);
    if (errCode.bytAvl = 0);

      // Retrieve pointer to user space
      Reset errCode;
      QUSPTRUS(qUsrSpc: pUsrSpc: errCode);

      if (errCode.bytAvl = 0);

        // List members
        select;
        when (i_mode = MODE_LEFT);
          qFile.name = SYNCMBRWRF.XWLEFTFILE;
          qFile.lib = SYNCMBRWRF.XWLEFTLIB;
          member = SYNCMBRWRF.XWMBR;
        when (i_mode = MODE_RIGHT);
          qFile.name = SYNCMBRWRF.XWRGHTFILE;
          qFile.lib = SYNCMBRWRF.XWRGHTLIB;
          member = SYNCMBRWRF.XWMBR;
        other;
          // TODO: incorrect mode
        endsl;

        if (contains('*': member));
          mask = ucase(%trimr(member));
          doGenericStringCompare = *on;
          member = '*ALL';
        else;
          doGenericStringCompare = *off;
        endif;

        Reset errCode;
        QUSLMBR(
         qUsrSpc :      // --> Qual. user space
         'MBRL0320' :   // --> Format name
         qFile :        // --> Qual. file
         member :       // --> Member name
         '0' :          // --> Override process
         errCode        // <-> Error code
        );

        if (errCode.bytAvl = 0);

          // Set pointer of generic header
          GHDR0300Ptr = pUsrSpc;

          for counter = 1 To GHDRNbrEtry;

            // Set pointer of list area
            MBRL0320Ptr =
              pUsrSpc +                       // Addr. user space
              GHDROffLstSct +                 // Offset to list area
              ((counter - 1) * GHDRSizeEtry); // Offset to record

            // Set pointer of information area
            MBRD0200APtr = pUsrSpc + MBRLOffMbrInf;

            // Compare current member with selection mask.
            if (doGenericStringCompare);
              if (not matches(mask: %trimr(MBRDMbr)));
                iter;
              endif;
            endif;

            // Test filters: member name and source type
            // The code for comparing name and source type separately
            // looked pretty ugly and complicated. Therefore name and
            // source type are compared together and without any
            // optimization regarding to '*.*'.
            // Let's check performance before any optimization.
            fullMemberName = %trim(MBRDMbr) + '.' + %trim(MBRDSrcTyp);

            // Check non-negated patterns:
            isFound = *off;
            if (i_isRegEx);
              // Compare using regular expression
              isFound = matchRegex(fullMemberName: hRegex);
            else;
              // String compare
              for i = 1 to numFilters;
                if (nameTypeFilter(i).isNegated);
                  iter;
                endif;

                memberNameMask = nameTypeFilter(i).name + '.' + nameTypeFilter(i).srcType;
                isFound = matches(memberNameMask: fullMemberName);
                if (isFound);
                  leave;
                endif;
              endfor;

              if (not isFound);
                iter;
              endif;

              // Check negated patterns:
              for i = 1 to numFilters;
                if (not nameTypeFilter(i).isNegated);
                  iter;
                endif;

                memberNameMask = nameTypeFilter(i).name + '.' + nameTypeFilter(i).srcType;
                isFound = not (matches(memberNameMask: fullMemberName));
                if (isFound);
                  leave;
                endif;
              endfor;
            endif;

            if (not isFound);
              iter;
            endif;

            // Update each 1500 milliseconds
            countMembersFound += 1;
            if (%diff(%timestamp(): lastUpdateFound.time: *mseconds) > 1500);

              // Update file 'Status': Count Members Found
              updateStatusMembersFound(i_handle: countMembersFound);
              lastUpdateFound.count = countMembersFound;

              if (isCanceled(i_handle));
                leave;
              endif;

            endif;

            // Append record to work file.
            checkSum = calculateChecksum(buildPath(MBRDFileLib: MBRDFile: MBRDMbr));
            lastChanged = cvtApiTimestampToRpg(MBRDChgDate);

            select;
            when (i_mode = MODE_LEFT);
              Exec SQL
                INSERT INTO
                  SYNCMBRW
                    (
                    XWHDL,
                    XWMBR,
                    XWLEFTLIB,
                    XWLEFTFILE,
                    XWLEFTTYPE,
                    XWLEFTLCHG,
                    XWLEFTCRC,
                    XWLEFTTEXT
                    )
                VALUES
                  (
                  :i_handle,
                  :MBRDMbr,      -- Member Name
                  :MBRDFileLib,  -- Left Member Source File Library
                  :MBRDFile,     -- Left Member Source File
                  :MBRDSrcTyp,   -- Left Member Source Type
                  :lastChanged,  -- Left Member Last Changed
                  :checkSum,     -- Left Member Checksum
                  :MBRDText      -- Left Member Text
                  );

            when (i_mode = MODE_RIGHT);
              Exec SQL
                INSERT INTO
                  SYNCMBRW
                    (
                    XWHDL,
                    XWMBR,
                    XWRGHTLIB,
                    XWRGHTFILE,
                    XWRGHTTYPE,
                    XWRGHTLCHG,
                    XWRGHTCRC,
                    XWRGHTTEXT
                    )
                VALUES
                  (
                  :i_handle,
                  :MBRDMbr,      -- Member Name
                  :MBRDFileLib,  -- Right Member Source File Library
                  :MBRDFile,     -- Right Member Source File
                  :MBRDSrcTyp,   -- Right Member Source Type
                  :lastChanged,  -- Right Member Last Changed
                  :checkSum,     -- Right Member Chcksum
                  :MBRDText      -- Right Member Text
                  );

            other;
              // TODO: incorrect mode
            endsl;

          endfor;

        endif;

      endif;

      // Delete user space
      dltUsrSpc(qUsrSpc);

    endif;

  enddo;

  if (lastUpdateFound.count <> countMembersFound);
    updateStatusMembersFound(i_handle: countMembersFound);
    lastUpdateFound.count = countMembersFound;
  endif;

on-exit;

  updateStatusResolvedMembers(i_handle: -1);

  if (hRegex.isInit);
    freeRegex(hRegex);
  endif;

  // Close cursor
  Exec SQL
     CLOSE
        GENERIC_MEMBERS;

end-proc;

// ==========================================================================================
//  Returns The Total Number Of Compare Members. (equals members found)
// ==========================================================================================

dcl-proc SYNCMBR_getNumberOfCompareElements export;
  dcl-pi *n zoned(6: 0);
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
  end-pi;

  // Local field declarations
  dcl-s numElems zoned(6: 0);

  // ----------------------------------------------------------------------------------------

  // Initialize 'Number of elements'
  numElems = 0;

  // Get number of elements
  select;
  when (i_mode = MODE_LEFT);
    Exec SQL
      SELECT
        XSCNTFND
      INTO
        :numElems
      FROM
        SYNCMBRS
      WHERE
        XSHDL = :i_handle;

  when (i_mode = MODE_RIGHT);
    Exec SQL
      SELECT
        XSCNTFND
      INTO
        :numElems
      FROM
        SYNCMBRS
      WHERE
        XSHDL = :i_handle;

  other;
    // TODO: incorrect mode
  endsl;

   return numElems;

end-proc;

// ==========================================================================================
//  Clear synchronisation data.
// ==========================================================================================

dcl-proc SYNCMBR_clear export;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Clear file 'Status'
  Exec SQL
    DELETE FROM
      SYNCMBRS
    WHERE
      XSHDL = :i_handle;

  // Clear file 'Work'
  Exec SQL
    DELETE FROM
      SYNCMBRW
    WHERE
      XWHDL = :i_handle;

end-proc;

// ==========================================================================================
//  Retrieve the attributes of a given member.
// ==========================================================================================

dcl-proc SYNCMBR_retrieveMemberAttributes export;
  dcl-pi *n likeds(mbrAttrs_t);
    i_library      char(10) Options(*trim) const;
    i_file         char(10) Options(*trim) const;
    i_member       char(10) Options(*trim) const;
  end-pi;

  // Return value
  dcl-ds mbrAttrs likeds(mbrAttrs_t);

  dcl-ds qFile likeds(qObj_t) inz;
  dcl-ds errCode likeds(errCode_t) inz(*likeds);
  dcl-ds mbrd0100 likeds(mbrd0100_t) inz;
  // ----------------------------------------------------------------------------------------

  qFile.name = i_file;
  qFile.lib = i_library;

  Reset errCode;
    QUSRMBRD(
      mbrd0100 :        // --> Receiver variable
      %size(mbrd0100):  // --> Size of receiver variable
      'MBRD0100' :      // --> Format name
      qFile :           // --> Qual. file
      i_member :        //  --> Member name
      '0' :             // --> Override process
      errCode           // <-> Error code
      );

  clear mbrAttrs;
  mbrAttrs.file = i_file;
  mbrAttrs.library = i_library;
  mbrAttrs.member = i_member;
  mbrAttrs.srcType = mbrd0100.srcType;
  mbrAttrs.text = mbrd0100.text;
  mbrAttrs.lastChanged = cvtApiTimestampToRpg(mbrd0100.srcChgDatTim);
  mbrAttrs.checkSum = calculateChecksum(buildPath(i_library: i_file: i_member));

  return mbrAttrs;

end-proc;

// ==========================================================================================
//  Builds a path from a given library, file and member.
// ==========================================================================================

dcl-proc buildPath;
  dcl-pi *n varchar(256);
    i_library      varchar(10) Options(*trim) const;
    i_file         varchar(10) Options(*trim) const;
    i_member       varchar(10) Options(*trim) const;
  end-pi;

  // Return value
  dcl-s path varchar(256);

  // ----------------------------------------------------------------------------------------

  path = '/qsys.lib/' + i_library + '.lib/' + i_file + '.file/' + i_member + '.mbr';

  return path;

end-proc;

// ==========================================================================================
//  Calculates the checksum of the member identified by a given path.
//  The line last changed date and the sequence numbers are not compared, because
//  the file is read from its IFS path. This way the date and sequence number columns
//  are dropped automatically.
// ==========================================================================================

dcl-proc calculateChecksum;
  dcl-pi *n like(crc32_checkSum_t);
    i_path         varchar(256) Options(*trim) const;
  end-pi;

  // Return value
  dcl-s checkSum like(crc32_checkSum_t); //   inz(x'FFFFFFFF');

  // Local field declarations
  dcl-s hFile int(10) inz(-1);
  dcl-s opnFlags int(10);
  dcl-s pBuffer pointer;
  dcl-s buffer char(256) based(pBuffer); // only for debugging
  dcl-s size int(10);
  // dcl-s checkSumHex char(8);
  dcl-s totalSize int(10);
  dcl-s errNbr int(10);
  dcl-s pErrNbr pointer;
  dcl-s errText char(80);

  dcl-c BUFFER_SIZE 16384;

  // ----------------------------------------------------------------------------------------

  opnFlags = %bitor(O_SHARE_RDWR: O_RDONLY);

  hFile = open(%trimr(i_path): opnFlags);
  if (hFile <> -1);

    pBuffer = %alloc(BUFFER_SIZE);
    totalSize = 0;

    //checkSum = x'FFFFFFFF'; // Initialize result buffer
    checkSum = crc32_init();
    dou (size <= 0);
      size = read(hFile: pBuffer: BUFFER_SIZE);
      if (size > 0);
        checkSum = crc32_add(checkSum: pBuffer: size);
        totalSize = totalSize + size;
      endif;
    enddo;

    //checkSum = %bitnot(checkSum); // Finalize calculaion
    checkSum = crc32_finish(checkSum); // Finalize calculaion

    // Not yet sure, how to display the checksum. Numeric or hex value?
    // cvthc(%addr(checkSumHex): %addr(checkSum): %size(checkSum) * 2);

  else;
    pErrNbr = %addr(errNbr);
    pErrNbr = errno();
    errText = %str(strerror(errNbr): %size(errText));
    checkSum = CHECKSUM_NULL;
  endif;

  return checkSum;

on-exit;

  if (hFile <> -1);
    closef(hFile);
  endif;

  if (pBuffer <> *null);
    dealloc(n) pBuffer;
  endif;

end-proc;

// ==========================================================================================
//  Converts a given 13-byte API timestamp value to a RPG timestamp.
//  The format of the API timestamp is:
//    Byte 1-7 :   cyymmdd
//    Byte 8-13:   hhmmss
// ==========================================================================================

dcl-proc cvtApiTimestampToRpg;
  dcl-pi *n timestamp;
    i_apiTimestamp char(13) const;
  end-pi;

  // Return value
  dcl-s rpgTimestamp timestamp;

  // Local field declarations
  dcl-ds apiTimestamp qualified inz;
    date  char(7);
    time  char(6);
  end-ds;

  // ----------------------------------------------------------------------------------------

  apiTimestamp = i_apiTimestamp;
  rpgTimestamp = %date(apiTimestamp.date: *CYMD0) + %time(apiTimestamp.time: *HMS0);

  return rpgTimestamp;

end-proc;

// ==========================================================================================
//  Create User Space.
// ==========================================================================================

dcl-proc crtUsrSpc;
  dcl-pi *n likeds(errCode_t);
    i_qUsrSpc      likeds(qObj_t) const;
  end-pi;

  // Return value
  dcl-ds errCode likeds(errCode_t) inz(*likeds);

  // ------------------------------------------------------------------------------------------

    Reset errCode;

    QUSCRTUS(
     i_qUsrSpc :    // --> Qual. user space
     *Blanks :      // --> Extended attr.
     16384 :        // --> Initial size
     '0' :          // --> Initial value
     '*ALL' :       // --> Public authority
     *Blanks :      // --> Text description
     '*YES' :       // --> Replace
     errCode        // <-> Error code
    );

    return errCode;

end-proc;

// ==========================================================================================
//  Delete User Space.
// ==========================================================================================

dcl-proc dltUsrSpc;
  dcl-pi *n;
    i_qUsrSpc      likeds(qObj_t) const;
  end-pi;

  // Local field declarations
  dcl-ds errCode likeds(errCode_t) inz(*likeds);

  // ----------------------------------------------------------------------------------------

  Reset errCode;
  QUSDLTUS(i_qUsrSpc: errCode);

end-proc;

// ==========================================================================================
//  Updates the number of resolved generic file entries.
// ==========================================================================================

dcl-proc updateStatusResolvedFiles;
  dcl-pi *n;
    i_handle         int(10) const;
    i_count          int(10) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Update file 'Status': resolved members
  Exec SQL
    UPDATE
      SYNCMBRS
    SET
      XSCNTFILE = :i_count
    WHERE
      XSHDL = :i_handle;

end-proc;

// ==========================================================================================
//  Updates the number of resolved generic member entries.
// ==========================================================================================

dcl-proc updateStatusResolvedMembers;
  dcl-pi *n;
    i_handle         int(10) const;
    i_count          int(10) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Update file 'Status': resolved members
  Exec SQL
    UPDATE
      SYNCMBRS
    SET
      XSCNTMBR = :i_count
    WHERE
      XSHDL = :i_handle;

end-proc;

// ==========================================================================================
//  Updates the number of total members found.
// ==========================================================================================

dcl-proc updateStatusMembersFound;
  dcl-pi *n;
    i_handle         int(10) const;
    i_count          int(10) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Update file 'Status': resolved members
  Exec SQL
    UPDATE
      SYNCMBRS
    SET
      XSCNTFND = :i_count
    WHERE
      XSHDL = :i_handle;

end-proc;

// ==========================================================================================
//  Check if process has to be canceled
// ==========================================================================================

dcl-proc isCanceled;
  dcl-pi *n ind;
    i_handle       int(10) const;
  end-pi;

  // Return value
  dcl-s isCanceled ind;

  // Local field declarations
  dcl-s cancel char(10);

  // ----------------------------------------------------------------------------------------

    cancel = '';

    Exec SQL
      SELECT
        XSCNL
      INTO
        :cancel
      FROM
         SYNCMBRS
      WHERE
        XSHDL = :i_handle;

    // Process has been canceled from RDi
    if (cancel = '*YES');
      isCanceled = *on;
    else;
      isCanceled = *off;
    endif;

    return isCanceled;

end-proc;

// ==========================================================================================
//  Retrieves the job ccsid.
// ==========================================================================================

dcl-proc retrieveJobCcsid;
  dcl-pi *n int(10);
  end-pi;

  // Return value
  dcl-s jobCcsid int(10);

  // Local field declarations
  dcl-ds qJob likeds(qJob_t) inz;
  dcl-ds jobi0400 likeds(jobi0400_t) inz;

  // ----------------------------------------------------------------------------------------

    clear qJob;
    qJob.name = '*';

    QUSRJOBI(jobi0400: %size(jobi0400): 'JOBI0400': qJob: '');

    jobCcsid = jobi0400.ccsid;

    return jobCcsid;

end-proc;

// ==========================================================================================
//  Initializes character translation.
// ==========================================================================================

dcl-proc initIconv;
  dcl-pi *n likeds(hIconv_t);
  end-pi;

  // Return value
  dcl-ds hIconv likeds(hIconv_t) inz;

  // Local field declarations
  dcl-ds from likeds(QtqCode_t) inz;
  dcl-ds to likeds(QtqCode_t) inz;

  dcl-c DIGITS const(%len(hIconv.nl_ccsid));
  dcl-c DECPOS const(%decpos(hIconv.nl_ccsid));

  // ----------------------------------------------------------------------------------------

    clear hIconv;
    hIconv.isInit = *off;

    hIconv.job_ccsid = retrieveJobCcsid();
    hIconv.nl_ccsid = %dec(%str(nl_langinfo(CODESET)): DIGITS: DECPOS);

    if (hIconv.job_ccsid = hIconv.nl_ccsid);
        return hIconv;
    endif;

    from.CCSID = hIconv.job_ccsid;
    from.ConvAlt = 0;    // Conversion alternative
    from.SubsAlt = 0;    // Substitution alternative
    from.ShiftAlt = 1;   // Shift-state alternative
    from.InpLenOp = 0;   // Input length option
    from.ErrorOpt = 1;   // Error option for mixed data
    from.Reserved = *ALLx'00';

    to.CCSID = hIconv.nl_ccsid;
    to.ConvAlt = 0;      // Conversion alternative
    to.SubsAlt = 0;      // Substitution alternative
    to.ShiftAlt = 1;     // Shift-state alternative
    to.InpLenOp = 0;     // Input length option
    to.ErrorOpt = 1;     // Error option for mixed data
    to.Reserved = *ALLx'00';

    hIconv.iconv = QtqIconvOpen(to: from);

    hIconv.isInit = *on;

    return hIconv;

end-proc;

// ==========================================================================================
//  Frees resources allcocated by the character translation service.
// ==========================================================================================

dcl-proc freeIconv;
  dcl-pi *n;
    io_hIconv        likeds(hIconv_t);
  end-pi;

  // ----------------------------------------------------------------------------------------

  if (not io_hIconv.isInit);
    return;
  endif;

  iconv_close(io_hIconv.iconv);

  clear io_hIconv;
  io_hIconv.isInit = *off;

end-proc;

// ==========================================================================================
//  Frees resources allcocated by the character translation service.
// ==========================================================================================

dcl-proc cvtCcsid;
  dcl-pi *n like(string_t);
    i_string         like(string_t) value;
  end-pi;

  // Return value
  dcl-s string like(string_t) inz;

  dcl-s rc int(10);
  dcl-s bufferSize uns(10);
  dcl-s inBytesLeft uns(10);
  dcl-s outBytesLeft uns(10);
  dcl-s outBuffer char(512);
  dcl-s outLength int(10);
  dcl-s pInBuf pointer;
  dcl-s pOutBuf pointer;

  // ----------------------------------------------------------------------------------------

  if (not g_hIconv.isInit);
    return i_string;
  endif;

  bufferSize = %size(outBuffer);
  inBytesLeft = %len(%trimR(i_string));
  outBytesLeft = bufferSize;
  pInBuf = %addr(i_string: *data);
  pOutBuf= %addr(outBuffer);
  rc = iconv(g_hIconv.iconv: pInBuf: inBytesLeft: pOutBuf: outBytesLeft);
  if (rc <> 0);
    kill('cvtCcsid: iconv() returned error code ' + %char(rc));
  endif;

  outLength = bufferSize - outBytesLeft;

  string = %subst(outBuffer: 1: outLength);

  return string;

end-proc;

// ==========================================================================================
//  Compiles a regular expression pattern.
// ==========================================================================================

dcl-proc initRegex;
  dcl-pi *n likeds(hRegex_t);
    i_pattern        like(string_t) const;
  end-pi;

  // Return value
  dcl-ds hRegex likeds(hRegex_t) inz;

  dcl-s rc int(10);
  dcl-s cflags int(10);
  dcl-s pattern like(i_pattern);

  // ----------------------------------------------------------------------------------------

  clear hRegex;

  pattern = cvtCcsid(i_pattern);

  cflags = REG_EXTENDED + REG_ICASE;
  rc = regcomp(hRegex.regex: pattern: cflags);
  if (rc <> 0);
    kill('initRegex: regcomp() returned error code ' + %char(rc));
  endif;

  hRegex.isInit = *on;

  return hRegex;

end-proc;

// ==========================================================================================
//  Frees all resources allocated by regular expressions.
// ==========================================================================================

dcl-proc freeRegex;
  dcl-pi *n;
    io_hRegex        likeds(hRegex_t);
  end-pi;

  // ----------------------------------------------------------------------------------------

  if (not io_hRegex.isInit);
    return;
  endif;

  regfree(io_hRegex.regex);

  clear io_hRegex;
  io_hRegex.isInit = *off;

end-proc;

// ==========================================================================================
//  Frees resources allcocated by the character translation service.
// ==========================================================================================

dcl-proc matchRegex;
  dcl-pi *n ind;
    i_string         like(string_t) const;
    io_hRegex        likeds(hRegex_t);
  end-pi;

  // Return value
  dcl-s isMatch ind;

  dcl-s rc int(10);
  dcl-s nmatch uns(10) inz(1);
  dcl-ds regmatch likeds(regmatch_t) inz;
  dcl-s error char(256);
  dcl-s string like(i_string);

  // ----------------------------------------------------------------------------------------

  string = cvtCcsid(i_string);
  rc = regexec(io_hRegex.regex: string: nmatch: regmatch: 0);

  select;
  when rc = 0;             // found!
    isMatch = *on;
  when rc = REG_NOMATCH;   // not found
    isMatch = *off;
  other;                   // error!
    regerror(rc: io_hRegex: %addr(error): %size(error));
    kill('matchRegex: regcomp() returned error code ' + %char(rc));
  endsl;

  return isMatch;

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

