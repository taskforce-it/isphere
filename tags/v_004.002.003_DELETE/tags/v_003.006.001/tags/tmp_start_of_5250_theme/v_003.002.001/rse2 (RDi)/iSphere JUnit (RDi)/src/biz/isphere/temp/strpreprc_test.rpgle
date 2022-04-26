      * =================================================================== *
      *                                                                     *
      *  Kurzdokumentation :                                                *
      *                                                                     *
      *  Bereich . . . : STRPREPRC                                          *
      *  Objekt  . . . : STRPREPRC4                                         *
      *  Beschreibung  : Tool: STRPREPRC  (Starten Pre-Proz.  ) EVF-Modul   *
      *  Author  . . . : Thomas Raddatz   <thomas.raddatz@tools400.de>      *
      *  Datum . . . . : 15.05.2015                                         *
      *                                                                     *
      * =================================================================== *
      *                                                                     *
      *  This software is free software, you can redistribute it and/or     *
      *  modify it under the terms of the GNU General Public License (GPL)  *
      *  as published by the Free Software Foundation.                      *
      *                                                                     *
      *  See GNU General Public License for details.                        *
      *          http://www.opensource.org                                  *
      *          http://www.opensource.org/licenses/gpl-license.html        *
      *                                                                     *
      *=====================================================================*
      *  History:                                                           *
      *                                                                     *
      *  Datum       Name          Änderung                                 *
      *  ----------  ------------  ---------------------------------------  *
      *                                                                     *
      * =================================================================== *
     D* >>PRE-COMPILER<<                                                    *
      *   >>WIDTH<<  70;                                                    *
      *                                                                     *
      *   >>CRTCMD<< CRTRPGMOD    SRCFILE(&SL/&SF) +                        *
      *                           SRCMBR(&SM) +                             *
      *                           MODULE(&LI/&OB);                          *
      *                                                                     *
      *   >>LINK<<                                                          *
      *     >>PARM<< OPTION(*EVENTF);                                       *
      *   >>END-LINK<<                                                      *
      *                                                                     *
      *   >>COMPILE<<                                                       *
      *     >>PARM<< TRUNCNBR(*NO);                                         *
      *     >>PARM<< DBGVIEW(*LIST);                                        *
      *   >>END-COMPILE<<                                                   *
      *                                                                     *
      *   >>CMD<<       CRTPF FILE(QTEMP/FOO) RCDLEN(112);                  *
      *   >>CMD<<       CRTPF FILE(QTEMP/BAA) +                             *
      *                       RCDLEN(112);                                  *
      *   >>EXECUTE<<                                                       *
      *   >>CMD<<       DLTF FILE(QTEMP/FOO);                               *
      *   >>CMD<<       DLTF FILE(QTEMP/BAA);                               *
      *                                                                     *
      * >>END-PRE-COMPILER<<                                                *
      * =================================================================== *
      /COPY QSTRPREPRC,H_SPEC
     H NOMAIN BNDDIR('QC2LE')
      *
     FSTRPRE2   IF   E           K DISK    usropn
     F                                     prefix(P2)
      *
      *  Returns cTrue for an ignored message ID, else cFalse.
     D isIgnoredMsgID...
     D                 pr              n
     D                                     extproc('isIgnoredMsgID')
     D  i_jobLogEntry                      const likeds(JobLog_jobLogEntry_t)
      *
      *  Returns the statement number of the line that has the error.
     D getLineNumber...
     D                 pr            20i 0
     D                                     extproc('getLineNumber')
     D  i_jobLogEntry                      const likeds(JobLog_jobLogEntry_t)
      *
      *  Macro to prepare the _NUM_Descr_T, used in the cpynv and edit functions.
     D NUM_Descr_T...
     D                 PR            10I 0
     D  i_type                        3I 0 const
     D  i_length                      3I 0 const
     D  i_fractional                  3I 0 const
      *
      *  Copy Numeric Value (CPYNV)
     D cpynv...
     D                 PR              *          extproc('cpynv')
     D  i_rcvDesc                          value  like(NUM_Descr_T )
     D  i_pRcvVar                      *   value
     D  i_srcDesc                          value  like(NUM_Descr_T )
     D  i_pSrcVar                      *   value
      *
      *  These values must be specified on the Scalar Type for CPYNV,
      *  CVTEFN, and EDIT instructions.                              QSYSINC/MIH.MICPTCOM
     D T_SIGNED        C                   x'00'
     D T_FLOAT         C                   x'01'
     D T_ZONED         C                   x'02'
     D T_PACKED        C                   x'03'
     D T_CHAR          C                   x'04'
     D T_ONLYNS        C                   x'06'
     D T_ONLYN         C                   x'07'
     D T_EITHER        C                   x'08'
     D T_OPEN          C                   x'09'
     D T_UNSIGNED      C                   x'0A'
      *
      * -------------------------------------------------------------
      *  Verwendete Dateien
      * -------------------------------------------------------------
      *
      * -------------------------------------------------------------
      *  Globale Referenzfelder
      * -------------------------------------------------------------
      *
      * -------------------------------------------------------------
      *  Globale Konstanten
      * -------------------------------------------------------------
      *
      * -------------------------------------------------------------
      *  Globale Variablen
      * -------------------------------------------------------------
     D g_status        ds                  qualified
     D  haveEventFile                  n   inz(cFalse)
     D  haveJobLog                     n   inz(cFalse)
     D  msgKey                        4a   inz(*ALLx'FF')
      *
     D g_LDA           ds                  dtaara(*LDA) qualified
     D  library                      10a
     D  object                       10a
      *
      * -------------------------------------------------------------
      *  Prototypen
      * -------------------------------------------------------------
      /copy QSTRPREPRC,PROTO_H                     Tool: STRPREPRC - Prototypes
      /copy QSTRPREPRC,QMHRTVM                     Retrieve Message (QMHRTVM) API
      /copy QEVENTF,PEVENTF                        EVFEVENT API - Public Interface
      /copy QJOBLOG,PJOBLOG                        EVFEVENT API - Job Log
      *
      * =============================================================
      *  Prepares for writing the event file.
      * =============================================================
     P prepareEventFile...
     P                 b                   export
     D                 pi

     D jobLogEntry     ds                  likeds(JobLog_jobLogEntry_t) inz
      /free

         if (JobLog_getNewest('*': jobLogEntry));
            g_status.haveJobLog = cTrue;
            g_status.msgKey = jobLogEntry.msgKey;
         else;
            g_status.haveJobLog = cFalse;
            g_status.msgKey = *ALLx'FF';
         endif;

      /end-free
     P                 e
      *
      * =============================================================
      *  Opens the event file.
      * =============================================================
     P openEventFile...
     P                 b                   export
     D                 pi              n
     D  i_obj                        10a   const
     D  i_lib                        10a   const

     D msg             ds                  likeds(msg_t)
     D rtnLib          s                   like(i_lib)
      /free

         Eventf_open(i_lib: i_obj: rtnLib);
         g_status.haveEventFile = cTrue;

         Eventf_writeProcessor();

         monitor;
            in g_LDA;
            if (rtnLib <> '');
               g_LDA.library = rtnLib;
               g_LDA.object = i_obj;
            else;
               g_LDA.library = '';
               g_LDA.object = '';
            endif;
            out g_LDA;
         on-error;
         endmon;

         if (rtnLib <> '');
            return cTrue;
         endif;

         return cFalse;

      /end-free
     P                 e
      *
      * =============================================================
      *  Copies the job log messages to the event file.
      * =============================================================
     P writeEventFile...
     P                 b                   export
     D                 pi
     D  i_srcFile                    10a   const
     D  i_srcLib                     10a   const
     D  i_srcMbr                     10a   const
     D  i_lineNumber                 10i 0 const
     D  i_threshold                  10i 0 const options(*nopass)
     D  i_ovrMsgSev                  10i 0 const options(*nopass)

     D p_ovrMsgSev     c                   6

     D threshold       s                   like(i_threshold)
     D ovrMsgSev       s                   like(i_ovrMsgSev)
     D path            s                   like(evf_path_t)
     D numLines        s             10i 0 inz
     D SOURCE_ID       c                   1

     D hJobLog         s                   like(JobLog_handle_t) inz
     D jobLogEntry     ds                  likeds(JobLog_jobLogEntry_t) inz
     D isFirstMsg      s               n   inz(cTrue)
     D lineNumber      s                   like(i_lineNumber)
      /free

        if (not g_status.haveEventFile);
           return;
        endif;

        if (not g_status.haveJobLog);
           return;
        endif;

        if (%parms() >= p_ovrMsgSev);
           ovrMsgSev = i_ovrMsgSev;
           threshold = i_threshold;
        else;
           ovrMsgSev = -1;
           threshold = -1;
        endif;

        path = f_cvtQSYSObjNameToPath(i_srcFile: i_srcLib: i_srcMbr: '*FILE');
        Eventf_writeFileID(SOURCE_ID: path: EVF_NAME_TYPE_PATH);

        hJobLog = JobLog_open(*omit: JOBLOG_OPTION_MSG +
                                     JOBLOG_OPTION_REPLACE_VARS +
                                     JOBLOG_OPTION_SENDER
                                   : JOBLOG_NEXT
                                   : g_status.msgKey);

        dow (JobLog_getEntry(hJobLog: jobLogEntry));

           if (isFirstMsg);
              isFirstMsg = cFalse;
              iter;
           endif;

           if (isIgnoredMsgID(jobLogEntry));
              iter;
           endif;

           lineNumber = getLineNumber(jobLogEntry);
           if (lineNumber = -1);
              lineNumber = i_lineNumber;
           endif;

           if (ovrMsgSev <> -1 and jobLogEntry.msgSeverity >= threshold);
              jobLogEntry.msgSeverity = ovrMsgSev;
           endif;

           Eventf_writeError(SOURCE_ID
                             : lineNumber
                             : jobLogEntry.msgID
                             : jobLogEntry.msgSeverity
                             : jobLogEntry.msgText);
        enddo;

        JobLog_close(hJobLog);

        Eventf_writeFileEnd(SOURCE_ID: numLines);

      /end-free
     P                 e
      *
      * =============================================================
      *  Closes the event file.
      * =============================================================
     P closeEventFile...
     P                 b                   export
     D                 pi
      /free

        if (%open(STRPRE2));
           close STRPRE2;
        endif;

        if (g_status.haveEventFile);
           Eventf_close();
           g_status.haveEventFile = cFalse;
        endif;

      /end-free
     P                 e
      *
      * =============================================================
      *  Returns cTrue for an ignored message ID, else cFalse.
      * =============================================================
     P isIgnoredMsgID...
     P                 b
     D                 pi              n
     D  i_jobLogEntry                      const likeds(JobLog_jobLogEntry_t)
      *
     D key             ds                  likerec(FSTRPRE2: *KEY)
      /free

         if (not %open(STRPRE2));
            open STRPRE2;
         endif;

         key.P2MSGFILE = i_jobLogEntry.msgFile;
         key.P2MSGLIB = i_jobLogEntry.msgFLibUsed;
         key.P2MSGID = i_jobLogEntry.msgID;
         chain %kds(key) FSTRPRE2;

         if (not %found());
            key.P2MSGLIB = i_jobLogEntry.msgFLibSpcfd;
            chain %kds(key) FSTRPRE2;
         endif;

         if (%found() and P2FMT = -1);
            return cTrue;
         endif;

         return cFalse;

      /end-free
     P                 e
      *
      * =============================================================
      *  Returns the statement number of the line that has the error.
      * =============================================================
     P getLineNumber...
     P                 b
     D                 pi            20i 0
     D  i_jobLogEntry                      const likeds(JobLog_jobLogEntry_t)
      *
     D key             ds                  likerec(FSTRPRE2: *KEY)
      *
     D qMsgF           ds                  likeds(qObj_t) inz
     D errCode         ds                  likeds(errCode_t) inz
     D rtvm0300_buffer...
     D                 s           8192a
     D rtvm0300        ds                  likeds(rtvm0300_t)
     D                                     based(pRtvm0300)
     D varFmt          ds                  likeds(qmhrtvm_substVarFmt_t)
     D                                     based(pVarFmt)
     D x               s             10i 0
     D offset          s             10i 0
     D length          s             10i 0
      *
     D tmpVal          s            128a
     D rtnVal          s             20i 0
      /free

         if (not %open(STRPRE2));
            open STRPRE2;
         endif;

         key.P2MSGFILE = i_jobLogEntry.msgFile;
         key.P2MSGLIB = i_jobLogEntry.msgFLibUsed;
         key.P2MSGID = i_jobLogEntry.msgID;
         chain %kds(key) FSTRPRE2;

         if (not %found());
            key.P2MSGLIB = i_jobLogEntry.msgFLibSpcfd;
            chain %kds(key) FSTRPRE2;
         endif;

         if (not %found() or P2FMT <= 0);
            return -1;
         endif;

         pRtvm0300 = %addr(rtvm0300_buffer);
         qMsgF.name = i_jobLogEntry.msgFile;
         qMsgF.lib = i_jobLogEntry.msgFLibUsed;
         errCode = f_newApiErrCode(cTrue);
         QMHRTVM(rtvm0300: %size(rtvm0300): 'RTVM0300'
                 : i_jobLogEntry.msgID: qMsgF: '': 0: '*NO': '*NO': errCode);

         if (f_isApiError(errCode));
            return -1;
         endif;

         if (rtvm0300.bytRet <= 0);
            return -1;
         endif;

         if (rtvm0300.lenVarFmtR <= 0);
            return -1;
         endif;

         if (rtvm0300.numVarFmt < P2FMT);
            return -1;
         endif;

         for x = 1 to P2FMT;
            if (x = 1);
               pVarFmt = %addr(rtvm0300) + rtvm0300.ofsVarFmt;
            else;
               offset += varFmt.length;
               pVarFmt += rtvm0300.lenVarFmtE;
               length = varFmt.length;
            endif;
         endfor;

         rtnVal = -1;
         tmpVal = %subst(i_jobLogEntry.rplcData: offset + 1: length);

         select;
         when (varFmt.type = '*BIN');
            cpynv(NUM_Descr_T(T_UNSIGNED: 8: 0)
                  : %addr(rtnVal)
                  : NUM_Descr_T(T_SIGNED: varFmt.length: varFmt.decPos)
                  : %addr(tmpVal));

         when (varFmt.type = '*UBIN');
            cpynv(NUM_Descr_T(T_UNSIGNED: 8: 0)
                  : %addr(rtnVal)
                  : NUM_Descr_T(T_UNSIGNED: varFmt.length: varFmt.decPos)
                  : %addr(tmpVal));

         when (varFmt.type = '*DEC');
            cpynv(NUM_Descr_T(T_UNSIGNED: 8: 0)
                  : %addr(rtnVal)
                  : NUM_Descr_T(T_PACKED: varFmt.length: varFmt.decPos)
                  : %addr(tmpVal));

         endsl;

         return rtnVal;

      /end-free
     P                 e
      *
      * =============================================================
      *  NUM_Descr_T
      *
      *  The following macro is used to prepare the numeric descriptor
      *  (_NUM_Descr_T), used in the cpynv and edit functions. Can also
      *  be used to set the type and length fields of _DPA_Template_T.
      *  The valid values for the parameters are:
      *     type         length             fractional
      *     -----------  -----------------  -------------------------
      *     _T_Signed    2 or 4             0
      *     _T_Float     4 or 8             0
      *     _T_Zoned     1 <= length <= 31  0 <= fractional <= length
      *     _T_Packed    1 <= length <= 31  0 <= fractional <= length
      *     _T_Unsigned  2 or 4             0
      * =============================================================
     P NUM_Descr_T...
     P                 b
     D                 pi            10i 0
     D  i_type                        3i 0 const
     D  i_length                      3i 0 const
     D  i_fractional                  3i 0 const
      *
     D                 DS
     D NUM_Descr                     10i 0 inz
     D  NUM_Descr_24                  3i 0 overlay(NUM_Descr: 1)
     D  NUM_Descr_16                  3i 0 overlay(NUM_Descr: *NEXT)
     D  NUM_Descr_8                   3i 0 overlay(NUM_Descr: *NEXT)
     D  NUM_Descr_0                   3i 0 overlay(NUM_Descr: *NEXT)
      /free

         NUM_Descr_0  = 0;
         NUM_Descr_8  = i_length;
         NUM_Descr_16 = i_fractional;
         NUM_Descr_24 = i_type;

         return NUM_Descr;

      /end-free
     P                 e
      *
