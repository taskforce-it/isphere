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
//          --PARM<<  COMPILEOPT('OPTIMIZE(*FULL)');
//          >>PARM<<  COMPILEOPT('OPTIMIZE(*NONE) DBGVIEW(*LIST)');
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

// Global Type Definitions
dcl-ds syncmbrw_t extname('SYNCMBRW': *all) qualified end-ds;

dcl-ds updatedAttrs_t qualified template;
  exists        ind;
  srcType       char(10);
  checkSum      like(crc32_checkSum_t);
  lastChanged   timestamp;
end-ds;

// Internal prototypes for lower/upper case names

dcl-pr buildPath varchar(256) extproc('buildPath');
  i_library      varchar(10) Options(*trim) const;
  i_file         varchar(10) Options(*trim) const;
  i_member       varchar(10) Options(*trim) const;
end-pr;

dcl-pr calculateChecksum like(crc32_checkSum_t) extproc('calculateChecksum');
  i_path         varchar(256) Options(*trim) const;
end-pr;

dcl-pr crtUsrSpc likeds(errCode_t) extproc('crtUsrSpc');
  i_qUsrSpc      likeds(qObj_t) const;
end-pr;

dcl-pr cvtApiTimestampToRpg timestamp extproc('cvtApiTimestampToRpg');
  i_apiTimestamp char(13) const;
end-pr;

dcl-pr dltUsrSpc extproc('dltUsrSpc');
  i_qUsrSpc      likeds(qObj_t) const;
end-pr;

dcl-pr isCanceled ind extproc('isCanceled');
  i_handle         int(10) const;
end-pr;

dcl-pr resolveGenericFiles extproc('resolveGenericFiles');
  i_handle       zoned(10: 0) const;
  i_mode         char(10) const;
end-pr;

dcl-pr resolveGenericMembers extproc('resolveGenericMembers');
  i_handle       zoned(10: 0) const;
  i_mode         char(10) const;
  i_memberFilter char(1024) const;
end-pr;

dcl-pr updateStatus extproc('updateStatus');
  i_handle         int(10) const;
  i_status         int(10) const;
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
       XSCNT,
       XSCNL
      )
    VALUES
      (
       :handle,
       0,
       '*NO'
      );

  // Leave procedure
  return handle;

end-proc;

// ==========================================================================================
//  Resolve Generic Elements.
// ==========================================================================================

dcl-proc SYNCMBR_resolveGenericCompareElements export;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
    i_memberFilter char(1024) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  resolveGenericFiles(i_handle: i_mode);

  resolveGenericMembers(i_handle: i_mode: i_memberFilter);

  updateStatus(i_handle: -1);

end-proc;

// ==========================================================================================
//  Get Number Of Compare Elements
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
        COUNT(*)
      INTO
        :numElems
      FROM
        SYNCMBRW
      WHERE
        XWHDL = :i_handle AND XWLEFTLIB <> '';

  when (i_mode = MODE_RIGHT);
    Exec SQL
      SELECT
        COUNT(*)
      INTO
        :numElems
      FROM
        SYNCMBRW
      WHERE
        XWHDL = :i_handle AND XWRGHTLIB <> '';

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
//  Resolves Generic File Selection items. For example: 'Q*'.
// ==========================================================================================

dcl-proc resolveGenericFiles;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
  end-pi;

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
            ((TRIM(XWLEFTFILE) LIKE '*%' OR TRIM(XWLEFTFILE) LIKE '%*' OR XWLEFTFILE = '*') or
             (TRIM(XWRGHTFILE) LIKE '*%' OR TRIM(XWRGHTFILE) LIKE '%*' OR XWRGHTFILE = '*'))
        FOR UPDATE;

  // Open cursor
  Exec SQL
     OPEN
        GENERIC_FILES;

  dow (*on);

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

        if (startsWith('*': qFile.name));
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

  // Close cursor
  Exec SQL
     CLOSE
        GENERIC_FILES;

end-proc;

// ==========================================================================================
//  Resolves Generic Member Selection items. For example: 'DEMO*'.
//  Calculates and updates the CRC32 checksum of the selected items.
// ==========================================================================================

dcl-proc resolveGenericMembers;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
    i_memberFilter char(1024) const;
  end-pi;

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
  dcl-ds nameTypeFilter qualified dim(50);
    name    char(10);
    srcType char(10);
  end-ds;

  dcl-s parts char(10) dim(2);

  dcl-s regexFilter varchar(1024);

  dcl-s i int(10);
  dcl-s isFound ind;
  dcl-s cancel char(10);

  // ----------------------------------------------------------------------------------------

  if (startsWith('!': i_memberFilter));
    // TODO: implement regex filter
    regexFilter = i_memberFilter;
  else;
    numFilters = 0;
    for-each filterItem in %split(i_memberFilter: FILTER_DELIMITER_CHARS);
      if (%len(%trim(filterItem)) > 0);
        parts = %split(filterItem: '.'); // = name.type
        if (parts(1) <> '' or parts(2) <> '' );
          numFilters += 1;
          if (parts(1) <> '');
            nameTypeFilter(numFilters).name = ucase(parts(1));
          else;
            nameTypeFilter(numFilters).name = '*';
          endif;
          if (parts(2) <> '');
            nameTypeFilter(numFilters).srcType = ucase(parts(2));
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
           (TRIM(XWMBR) LIKE '*%' OR TRIM(XWMBR) LIKE '%*' OR XWMBR = '*')
        FOR UPDATE;

  // Open cursor
  Exec SQL
     OPEN
        GENERIC_MEMBERS;

  dow (*on);

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

        if (startsWith('*': member));
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


           if (%rem(counter: 100) = 0);

             // Update file 'Status'
             updateStatus(i_handle: counter);

             // Initialize 'Cancel'
             cancel = '';

             // Check if compare has to be canceled
             Exec SQL
                SELECT
                   XSCNL
                INTO
                   :cancel
                FROM
                   SYNCMBRS
                WHERE
                   XSHDL = :i_handle;

             // Compare has to be canceled
             if (cancel = '*YES');
               leave;
             endif;

           endif;

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
            isFound = *on;
            for i = 1 to numFilters;
              // Check name filter
              if (nameTypeFilter(i).name <> '*');
                if (not matches(nameTypeFilter(i).name: MBRDMbr));
                  isFound = *off;
                  leave;
                endif;
              endif;
              // Check source type filter
              if (nameTypeFilter(i).srcType <> '*');
                if (not matches(nameTypeFilter(i).srcType: MBRDSrcTyp));
                  isFound = *off;
                  leave;
                endif;
              endif;
            endfor;

            if (not isFound);
              iter;
            endif;

            // Append record to work file.
            checkSum = calculateChecksum(buildPath(MBRDFileLib: MBRDFile: MBRDMbr));
            lastChanged = cvtApiTimestampToRpg(MBRDChgTime);

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

    // Compare has to be canceled
    if (cancel = '*YES');
      leave;
    endif;

  enddo;

on-exit;

  // Close cursor
  Exec SQL
     CLOSE
        GENERIC_MEMBERS;

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

  opnFlags = %bitor(O_SHARE_RDONLY: O_RDONLY);

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
//  Updates the status field of the result table.
// ==========================================================================================

dcl-proc updateStatus;
  dcl-pi *n;
    i_handle         int(10) const;
    i_status         int(10) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Update file 'Status'
  Exec SQL
    UPDATE
      SYNCMBRS
    SET
      XSCNT = :i_status
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

