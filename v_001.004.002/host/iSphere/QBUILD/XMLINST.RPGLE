000100140307      *---------------------------------------------------------------------------------------
000101140307      * CRTBNDRPG PGM(QTEMP/XMLINST) SRCFILE(ISPHERE/QBUILD) SRCMBR(*PGM)
000103140307      *---------------------------------------------------------------------------------------
000104140307      * Parse / Install from xml text into source members and objects.
000105140307      * Copyright (C) 2001    Craig Rutledge    <craig_rutledge@mohawkind.com>
000106140307      * Martin Rowe    <Martin@dbg400.net>          scripting cmd prompts
000107140307      * David George   <webmaster@400times.co.uk>   intellectual input
000108140307      * Thomas Raddatz <thomas.raddatz@tools400.de> execute SQL statements
000109140307      * Thomas Raddatz <thomas.raddatz@tools400.de> conditions
000110140307      * Thomas Raddatz <thomas.raddatz@tools400.de> release
000111140307      * Thomas Raddatz <thomas.raddatz@tools400.de> retrieve object description
000112140307      *
000113140307      * Use xml tags in text to trigger:
000114140307      * 1. Parse text into source members (create srcfile & member if required).
000115140307      * 2. Compile source into objects.
000116140307      * 3. Send installation progress user messages.
000117140307      * 4. Execute qcmdexc as required.
000118140307      * 5. Execute SQL statements as required.
000119140307      *
000120140307      * This program is free software, you can redistribute it and/or modify it
000121140307      * under the terms of the GNU General Public License as published by
000122140307      * the Free Software Foundation.  See GNU General Public License for details
000123140307      *---------------------------------------------------------------------------------------
000124140307     H DFTACTGRP(*NO) ACTGRP(*CALLER) BNDDIR('QC2LE')
000125140307      *---------------------------------------------------------------------------------------
000126140307     Fxmlinput  if   f  112        disk    usropn                               uploaded text
000127140307     Fqxxxsrc   o    f  112        disk    usropn                               parsed out
000128140307      *--------------------------------------------------------------------
000129140307      * create parm prototypes
000130140307      *--------------------------------------------------------------------
000131140307      *  Tokenize String
000132140307     D strtok          PR              *          extproc('strtok')
000133140307     D  i_string                       *   value  options(*string)
000134140307     D  i_token                        *   value  options(*string)
000135140307      * retrieve member description
000136140307     D qusrmbrd        PR                  ExtPgm('QUSRMBRD')                   MEMBER DESCRIPTION
000137140307     D  o_rcvVar                  32767a          options(*varsize)             RECEIVER
000138140307     D  i_lenRcvVar                  10i 0 const                                LENGTH OF RECVR
000139140307     D  i_format                      8    const                                UPLOAD TYPE
000140140307     D  i_qFile                      20    const                                FILE   LIB
000141140307     D  i_mbr                        10    const                                MEMBER NAME
000142140307     D  i_ovverride                   1    const                                PROCESS OVERIDE
000143140307     D  io_errCode                32767a          options(*varsize)             ERROR CODE
000144140307      * retrieve object description
000145140307     D QUSROBJD...
000146140307     D                 PR                         extpgm('QUSROBJD')
000147140307     D  o_rcvVar                  65535A          options(*varsize)
000148140307     D  i_lenRcvVar                  10I 0 const
000149140307     D  i_format                      8A   const
000150140307     D  i_qObj                       20A   const
000151140307     D  i_type                       10A   const
000152140307     D  io_errCode                65535A          options(*nopass: *varsize)    | OptGrp 1
000153140307      * send program message
000154140307     D qmhsndpm        PR                  ExtPgm('QMHSNDPM')                   SEND MESSAGES
000155140307     D  i_msgID                       7a   const                                ID
000156140307     D  i_qMsgF                      20a   const                                FILE
000157140307     D  i_msgText                 32767a   const  options(*varsize)             TEXT
000158140307     D  i_lenMsgText                 10i 0 const                                LENGTH
000159140307     D  i_msgType                    10a   const                                TYPE
000160140307     D  i_callStackE                 10a   const                                STACK ENTRY
000161140307     D  i_callStackC                 10i 0 const                                STACK COUNTER
000162140307     D  i_msgKey                      4a   const                                KEY
000163140307     D  io_errCode                32767a          options(*varsize)             ERROR CODE
000164140307      * execute cl command
000165140307     D  qcmdexc        PR                  ExtPgm('QCMDEXC')
000166140307     D  i_cmd                        50a   Const
000167140307     D  i_lenCmd                     15p 5 Const
000168140307      *  Allocate Environment Handle
000169140307     D SQLAllocEnv...
000170140307     D                 PR                         extproc('SQLAllocEnv')
000171140307     D                                     like(SQLRETURN_t )
000172140307     D  o_phenv                        *   value
000173140307      *  Allocate Connection Handle
000174140307     D SQLAllocConnect...
000175140307     D                 PR                         extproc('SQLAllocConnect')
000176140307     D                                     like(SQLRETURN_t )
000177140307     D  i_henv                             value  like(SQLHENV_t   )
000178140307     D  o_phdbc                        *   value
000179140307      *  Connect to a Data Source
000180140307     D SQLConnect...
000181140307     D                 PR                         extproc('SQLConnect')
000182140307     D                                     like(SQLRETURN_t )
000183140307     D  i_hdbc                             value  like(SQLHDBC_t   )
000184140307     D  i_szDSN                        *   value  options(*string)
000185140307     D  i_cbDSN                            value  like(SQLSMALLINT_t)
000186140307     D  i_szUID                        *   value  options(*string)
000187140307     D  i_cbUID                            value  like(SQLSMALLINT_t)
000188140307     D  i_szAuthStr                    *   value  options(*string)
000189140307     D  i_cbAuthStr                        value  like(SQLSMALLINT_t)
000190140307      *  Set Connection Option
000191140307     D SQLSetConnectOption...
000192140307     D                 PR                         extproc('SQLSetConnectOption')
000193140307     D                                     like(SQLRETURN_t )
000194140307     D  i_hdbc                             value  like(SQLHDBC_t   )
000195140307     D  i_fOption                          value  like(SQLSMALLINT_t)
000196140307     D  i_vParam                           value  like(SQLPOINTER_t )
000197140307      *  Allocate a Statement Handle
000198140307     D SQLAllocStmt...
000199140307     D                 PR                         extproc('SQLAllocStmt')
000200140307     D                                     like(SQLRETURN_t )
000201140307     D  i_hdbc                             value  like(SQLHDBC_t   )
000202140307     D  o_phstmt                       *   value
000203140307      *  Execute a Statement Directly
000204140307     D SQLExecDirect...
000205140307     D                 PR                         extproc('SQLExecDirect')
000206140307     D                                     like(SQLRETURN_t )
000207140307     D  i_hstmt                            value  like(SQLHSTMT_t  )
000208140307     D  i_szSqlStr                     *   value  options(*string)
000209140307     D  i_cbSqlStr                         value  like(SQLINTEGER_t)
000210140307      *  Free (or Reset) a Statement Handle
000211140307     D SQLFreeStmt...
000212140307     D                 PR                         extproc('SQLFreeStmt')
000213140307     D                                     like(SQLRETURN_t )
000214140307     D  i_hstmt                            value  like(SQLHSTMT_t  )
000215140307     D  i_fOption                          value  like(SQLSMALLINT_t)
000216140307      *  Disconnect from a Data Source
000217140307     D SQLDisconnect...
000218140307     D                 PR                         extproc('SQLDisconnect')
000219140307     D                                     like(SQLRETURN_t )
000220140307     D  i_hdbc                             value  like(SQLHDBC_t   )
000221140307      *  Free Connection Handle
000222140307     D SQLFreeConnect...
000223140307     D                 PR                         extproc('SQLFreeConnect')
000224140307     D                                     like(SQLRETURN_t )
000225140307     D  i_hdbc                             value  like(SQLHDBC_t   )
000226140307      *  Free Environment Handle
000227140307     D SQLFreeEnv...
000228140307     D                 PR                         extproc('SQLFreeEnv')
000229140307     D                                     like(SQLRETURN_t )
000230140307     D  i_henv                             value  like(SQLHENV_t   )
000231140307      *--------------------------------------------------------------------
000232140307     D long_t          S             10I 0                    based(pDummy)
000233140307     D short_t         S              5I 0                    based(pDummy)
000234140307      *
000235140307     D SQLINTEGER_t    S                   like(long_t      ) based(pDummy)
000236140307     D SQLSMALLINT_t   S                   like(short_t     ) based(pDummy)
000237140307      *
000238140307     D PTR_t           S               *                      based(pDummy)
000239140307     D SQLPOINTER_t    S                   like(PTR_t       ) based(pDummy)
000240140307     D HENV_t          S                   like(long_t      ) based(pDummy)
000241140307     D HDBC_t          S                   like(long_t      ) based(pDummy)
000242140307     D HSTMT_t         S                   like(long_t      ) based(pDummy)
000243140307     D RETCODE_t       S                   like(SQLINTEGER_t) based(pDummy)
000244140307      *
000245140307     D SQLHENV_t       S                   like(HENV_t      ) based(pDummy)
000246140307     D SQLHDBC_t       S                   like(HDBC_t      ) based(pDummy)
000247140307     D SQLHSTMT_t      S                   like(HSTMT_t     ) based(pDummy)
000248140307     D SQLRETURN_t     S                   like(RETCODE_t   ) based(pDummy)
000249140307      *
000250140307     D retCode         S                   like(SQLRETURN_t) inz
000251140307     D hdbc            S                   like(SQLHDBC_t  ) inz
000252140307     D hstmt           S                   like(SQLHSTMT_t ) inz
000253140307     D henv            S                   like(SQLHENV_t  ) inz
000254140307     D sqlInit         S              1A                     inz(*off)
000255140307     D cOptVal         S             10I 0                   inz
000256140307      *
000257140307     D SQL_NTS         C                   const( -3)
000258140307     D SQL_COMMIT_NONE...
000259140307     D                 C                   const(  1)
000260140307     D SQL_ATTR_COMMIT...
000261140307     D                 C                   const(  0)
000262140307     D SQL_DROP        C                   const(  1)
000263140307      *--------------------------------------------------------------------
000264140307      * Read element
000265140307      *--------------------------------------------------------------------
000266140307     D readElement     PR                        like(bldexc)
000267140307     D  i_element                     9A   value varying
000268140307      *--------------------------------------------------------------------
000269140307      * Define function prototype to double quotes in a string
000270140307      *--------------------------------------------------------------------
000271140307     D dblQuotes       PR          2048A         varying
000272140307     D  i_string                   2048A   value varying
000273140307      *--------------------------------------------------------------------
000274140307      * Define function prototype to get attribute data from a string
000275140307      *--------------------------------------------------------------------
000276140307     D getAttrData...
000277140307     D                 PR          2000A          varying                       like(string_t)
000278140307     D  i_attr                       10a   value
000279140307     D  i_string                   2000A   value  varying                       like(string_t)
000280140307     D  i_default                  2000A   value  varying                       like(string_t)
000281140307      *--------------------------------------------------------------------
000282140307      * Define function prototype to check for a condition
000283140307      *--------------------------------------------------------------------
000284140307     D isCondition...
000285140307     D                 PR              n
000286140307     D  i_condList                 2000A   value
000287140307      *---------------------------------------------------------------------------------------
000288140307     D loadConditions...
000289140307     D                 PR                  dim(32) like(condition)
000290140307     D  i_conditions                128A   value
000291140307      *--------------------------------------------------------------------
000292140307     D isRelease...
000293140307     D                 PR              n
000294140307     D  i_release                    10A   value
000295140307      *--------------------------------------------------------------------
000296140307     D setTargetRelease...
000297140307     D                 PR          2000A          varying
000298140307     D  i_keyword                    10A   value  varying
000299140307     D  i_string                   2000A   value  varying
000300140307     D  i_value                      10A   value  varying
000301140307      *--------------------------------------------------------------------
000302140307     D setVariable...
000303140307     D                 PR
000304140307     D  i_fldName                    10A   value  varying
000305140307     D  i_value                      64A   value  varying
000306140307      *---------------------------------------------------------------------------------------------
000307140307     D getVarName...
000308140307     D                 PR            11a          varying
000309140307     D  i_bldexc                           value  like(bldexc)
000310140307     D  i_pos                        10i 0 value
000311140307      *---------------------------------------------------------------------------------------------
000312140307     D getVarValue...
000313140307     D                 PR            64a          varying
000314140307     D  i_name                       11a   value  varying
000315140307      *---------------------------------------------------------------------------------------------
000316140307     D uCase...
000317140307     D                 PR          2000A          varying
000318140307     D  i_string                   2000A   value  varying
000319140307      *--------------------------------------------------------------------
000320140307     D vrcvar          s            145
000321140307     D qm_msgid        s              7
000322140307     D qm_msgtxt       s            500
000323140307     D qm_msgq         s             10
000324140307     D qm_msgtyp       s             10
000325140307     D mbrname         s             10
000326140307     D mbrtype         s             10
000327140307     D mbrtext         s             50
000328140307     D srcfile         s             10
000329140307     D srclen          s              5
000330140307     D srclenN         s              5  0   inz(0)
000331140307     D srccssid        s              5
000332140307     D bldexc          s           2000
000333140307     D write_flag      s              1n   inz(*off)
000334140307     D srcSeqno        s              6s 2 inz(0)
000335140307     D aa              s              5u 0 inz(0)
000336140307     D ll              s              5u 0 inz(0)
000337140307     D qs              c                   ''''
000338140307     D qd              c                   '"'
000339140307     D errFLag         s              1    inz(*off)
000340140307     D cnd_msg         s             42
000341140307     D cnd_values      s            110
000342140307     D cnd_rtnVal      s             10
000343140307     D cnd_array       s                   dim(32) inz like(condition)
000344140307     D cnd_ptr         s             10i 0 inz
000345140307     D condition       s             10a
000346140307     D condList        s           2000a
000347140307     D release         s             10a
000348140307     D dltsplf         s             10a
000349140307     D objd_obj        s             10a
000350140307     D objd_lib        s             10a
000351140307     D objd_objtype    s             10a
000352140307     D objd_rtnlib     s             10a
000353140307     D objd0100        ds
000354140307     D  od_bytRet              1      4i 0
000355140307     D  od_bytAvl              5      8i 0
000356140307     D  od_name                9     18a
000357140307     D  od_lib                19     28a
000358140307     D  od_type               29     38a
000359140307     D  od_rtnLib             39     48a
000360140307     D fld_array       ds
000361140307     D  fld_x                        10i 0 inz
000362140307     D  fld_name                     11a   inz dim(64) varying
000363140307     D  fld_value                    64a   inz dim(64) varying
000364140307     D var_name        s             11a   inz varying
000365140307     D var_value       s             64a   inz varying
000366140307      * Error return code parm for APIs.
000367140307     D errCode         DS
000368140307     D  errCode_bytPrv...
000369140307     D                               10i 0 inz(%size(errCode))
000370140307     D  errCode_bytAvl...
000371140307     D                               10i 0 inz(0)
000372140307     D  errCode_excID...
000373140307     D                                7a   inz
000374140307     D  errCode_reserved...
000375140307     D                                1a   inz
000376140307     D  errCode_escData...
000377140307     D                              256a   inz
000378140307      * Optional parameters
000379140307     D OvrSrcFile      s                   like(i_OvrSrcFile) inz
000380140307     D isPreDefCond    s               N   inz(*off)
000381140307     D TgtRls          s                   like(i_TgtRls    ) inz
000382140307      *
000383140307      * Program status data structure.
000384140307     D sds            sds
000385140307     D  sds_pgmName                  10A
000386140307     D  sds_status                    5S 0
000387140307     D  sds_prvStat                   5S 0
000388140307     D  sds_lastSeq                   8A
000389140307     D  sds_lastSubR                  8A
000390140307     D  sds_numParm                   3S 0
000391140307     D  sds_msgID                     7A
000392140307     D  sds_MiInstr                   4A
000393140307     D  sds_wrkArea                  30A
000394140307     D  sds_lib                      10A
000395140307     D  sds_msgText                  80A
000396140307      *--------------------------------------------------------------------
000397140307     Ixmlinput  ns
000398140307     I                                 13   21  xmltag1
000399140307     I                                 18   27  xmltag2
000400140307     I                                 13  112  xmlcode
000401140307      *--------------------------------------------------------------------
000402140307     C     *entry        plist
000403140307     C                   parm                    ParseSrcMbr      10            source member
000404140307     C                   parm                    ParseSrcFile     10            source file
000405140307     C                   parm                    ParseSrcLib      10            source lib
000406140307      *  optional parameters:
000407140307     C                   parm                    i_OvrSrcFile     10            override to src
000408140307     C                   parm                    i_Conditions    128            pre-def conditions
000409140307     C                   parm                    i_TgtRls         10            target release
000410140307      *
000411140307      *  get optional parameter: Overrride to Source File
000412140307     C                   if        %parms() >= 4
000413140307     C                   eval      OvrSrcFile = i_OvrSrcFile
000414140307     C                   else
000415140307     C                   eval      OvrSrcFile = ''
000416140307     C                   endif
000417140307      *
000418140307      *  get optional parameter: Conditions
000419140307     C                   if        %parms() >= 5
000420140307     C                   if        i_Conditions <> ''
000421140307     C                   eval      cnd_array = loadConditions(i_Conditions)
000422140307     C                   eval      isPreDefCond = *on
000423140307     C                   else
000424140307     C                   eval      isPreDefCond = *off
000425140307     C                   endif
000426140307     C                   endif
000427140307      *
000428140307      *  get optional parameter: Target Release
000429140307     C                   if        %parms() >= 6
000430140307     C                   eval      TgtRls = i_TgtRls
000431140307     C                   else
000432140307     C                   eval      TgtRls = ''
000433140307     C                   endif
000434140307      *
000435140307     C                   exsr      srValidate                                   make sure exist
000436140307      *
000437140307      * Set user selected library *first for remainder of program
000438140307     C                   eval      bldexc = 'RMVLIBLE LIB('+
000439140307     C                             %trimr(ParseSrcLib) + ')'
000440140307     C                   callp(e)  qcmdexc(bldexc:%len(%trimr(bldexc)))
000441140307      *
000442140307     C                   eval      bldexc = 'ADDLIBLE LIB('+
000443140307     C                             %trimr(ParseSrcLib) + ') POSITION(*FIRST)'
000444140307     C                   callp(e)  qcmdexc(bldexc:%len(%trimr(bldexc)))
000445140307      *
000446140307      *       CPF2103 - Library &1 already exists in library list.
000447140307     C                   if        %error and sds_msgID <> 'CPF2103'
000448140307     C                   eval      qm_msgtxt = '0000 ' + sds_msgText
000449140307     C                   exsr      srKill
000450140307     C                   endif
000451140307      *
000452140307      * Set variable &TOLIB
000453140307     C                   callp     setVariable('tolib': ParseSrcLib)
000454140307      *
000455140307      * Override Input file to uploaded text file
000456140307     C                   eval      bldexc = 'OVRDBF FILE(XMLINPUT) TOFILE(' +
000457140307     C                             %trimr(ParseSrcLib)+'/'+
000458140307     C                             %trimr(ParseSrcFile) + ') MBR(' +
000459140307     C                             %trimr(ParseSrcMbr) + ')'
000460140307     C                   callp     qcmdexc(bldexc:%len(%trimr(bldexc)))
000461140307     C                   open      xmlinput
000462140307     C                   read      xmlinput
000463140307     C                   dow       not %eof
000464140307      * write records to outfile if flag is on
000465140307     C                   select
000466140307     C                   when      write_flag = *on
000467140307     C                   if        xmltag2<>'</copysrc>'
000468140307     C                   eval      srcSeqno=srcSeqno+1
000469140307     C                   except    write_one
000470140307     C                   else
000471140307     C                   eval      write_flag=*off
000472140307     C                   close     qxxxsrc
000473140307     C                   endif
000474140307      *
000475140307      * Extract values based on xml tags.
000476140307     C                   when      xmltag1 = 'mbrname ='
000477140307     C                   eval      mbrname = %subst(xmlcode:13:10)
000478140307     C                   when      xmltag1 = 'mbrtype ='
000479140307     C                   eval      mbrtype =%subst(xmlcode:13:10)
000480140307     C                   when      xmltag1 = 'mbrtext ='
000481140307     C                   eval      mbrtext =dblQuotes(%subst(xmlcode:13:50))
000482140307     C                   when      xmltag1 = 'srcfile ='
000483140307     C                   if        OvrSrcFile <> ''
000484140307     C                   eval      srcfile =OvrSrcFile
000485140307     C                   else
000486140307     C                   eval      srcfile =%subst(xmlcode:13:10)
000487140307     C                   endif
000488140307     C                   when      xmltag1 = 'srclen  ='
000489140307     C                   eval      srclen  =%subst(xmlcode:13:5)
000490140307     C                   when      xmltag1 = 'srccssid='
000491140307     C                   eval      srccssid=%subst(xmlcode:13:5)
000492140307      *--------------------------------------------------------------------
000493140307      * Start of data to copy.  Create source files/mbrs as required.
000494140307      *--------------------------------------------------------------------
000495140307     C                   when      xmltag1='<copysrc>'
000496140307     C                   move      srclen        srclenN
000497140307      * crtsrcpf
000498140307     C                   eval      bldexc = 'CRTSRCPF FILE(' +
000499140307     C                             %trimr(ParseSrcLib)+'/'+
000500140307     C                             %trimr(srcfile) + ') RCDLEN(' +
000501140307     C                             srclen + ') CCSID(' +
000502140307     C                             srccssid + ')'
000503140307     C                   callp(e)  qcmdexc(bldexc:%len(%trimr(bldexc)))
000504140307      * addpfm
000505140307     C                   eval      bldexc = 'ADDPFM   FILE(' +
000506140307     C                             %trimr(ParseSrcLib)+'/'+
000507140307     C                             %trimr(srcfile) + ') MBR(' +
000508140307     C                             %trimr(mbrname) + ') SRCTYPE(' +
000509140307     C                             %trimr(mbrtype) + ') TEXT(' +
000510140307     C                             qs+%trimr(mbrtext)+qs + ')'
000511140307     C                   callp(e)  qcmdexc(bldexc:%len(%trimr(bldexc)))
000512140307     C                   if        %error
000513140307      * chgpfm
000514140307     C                   eval      bldexc = 'CHGPFM   FILE(' +
000515140307     C                             %trimr(ParseSrcLib)+'/'+
000516140307     C                             %trimr(srcfile) + ') MBR(' +
000517140307     C                             %trimr(mbrname) + ') TEXT(' +
000518140307     C                             qs+%trimr(mbrtext)+qs + ')'
000519140307     C                   callp     qcmdexc(bldexc:%len(%trimr(bldexc)))
000520140307      * clr mbr
000521140307     C                   eval      bldexc = 'CLRPFM   FILE(' +
000522140307     C                             %trimr(ParseSrcLib)+'/'+
000523140307     C                             %trimr(srcfile) + ') MBR(' +
000524140307     C                             %trimr(mbrname) + ')'
000525140307     C                   callp     qcmdexc(bldexc:%len(%trimr(bldexc)))
000526140307     C                   endif
000527140307      * ovr to outfile mbr
000528140307     C                   eval      bldexc = 'OVRDBF QXXXSRC ' +
000529140307     C                             %trimr(ParseSrcLib) + '/' +
000530140307     C                             %trimr(srcfile) + ' ' +
000531140307     C                             mbrname
000532140307     C                   callp     qcmdexc(bldexc:%len(%trimr(bldexc)))
000533140307     C                   clear                   srcSeqno
000534140307     C                   open      qxxxsrc
000535140307     C                   eval      write_flag = *on
000536140307      *--------------------------------------------------------------------
000537140307      * Compile statement.  Read next record and execute it.
000538140307      * The subroutine TolibToken will replace &tolib with the
000539140307      * library the user has selected at run time.
000540140307      *--------------------------------------------------------------------
000541140307     C                   when      xmltag1 = '<compile '
000542140307     C                   eval      condList= getAttrData('condition':xmlcode:'')
000543140307     C                   read      xmlinput
000544140307     C                   eval      bldexc = readElement('compile')
000545140307     C                   if        isCondition(condList)
000546140307     C                   exsr      TolibToken
000547140307     C                   exsr      RplVars
000548140307     C                   exsr      SetTgtRls
000549140307     C                   callp     qcmdexc(bldexc:%len(%trimr(bldexc)))
000550140307     C                   endif
000551140307      *--------------------------------------------------------------------
000552140307      * qcmdexc statement. Build statement from each record between start
000553140307      * and stop tags.  When stop tag is found, execute statement.
000554140307      *--------------------------------------------------------------------
000555140307     C                   when      xmltag1 = '<qcmdexc '
000556140307     C                   eval      condList = getAttrData('condition'
000557140307     C                                                    :xmlcode:'')
000558140307     C                   eval      release  = getAttrData('release'
000559140307     C                                                    :xmlcode:'*CURRENT')
000560140307     C                   eval      dltsplf  = getAttrData('dltsplf'
000561140307     C                                                    :xmlcode:'*NONE')
000562140307     C                   read      xmlinput
000563140307     C                   eval      bldexc = readElement('qcmdexc')
000564140307     C                   if        isCondition(condList) and isRelease(release)
000565140307     C                   exsr      TolibToken
000566140307     C                   exsr      RplVars
000567140307     C                   exsr      SetTgtRls
000568140307     C                   if        %subst(bldexc: 1: 3) = 'DLT'
000569140307     C                   callp (e) qcmdexc(bldexc:%len(%trimr(bldexc)))
000570140307     C                   else
000571140307     C                   callp (e) qcmdexc(bldexc:%len(%trimr(bldexc)))
000572140307     C                   if        %error()
000573140307     C                   eval      qm_msgtxt = '0000 Failed to execute: '+bldexc
000574140307     C                   exsr      srKill
000575140307     C                   endif
000576140307     C                   if        dltsplf <> '' and dltsplf <> '*NONE'
000577140307     C                   eval      bldexc = 'DLTSPLF FILE(' + dltsplf +
000578140307     C                                      ') JOB(*) SPLNBR(*LAST)'
000579140307     C                   callp (e) qcmdexc(bldexc:%len(%trimr(bldexc)))
000580140307     C                   endif
000581140307     C                   endif
000582140307     C                   endif
000583140307      *--------------------------------------------------------------------
000584140307      * SQL statement. Build statement from each record between start
000585140307      * and stop tags.  When stop tag is found, execute SQL statement.
000586140307      *--------------------------------------------------------------------
000587140307     C                   when      xmltag1 = '<execsql '
000588140307     C                   eval      condList = getAttrData('condition'
000589140307     C                                                    :xmlcode:'')
000590140307     C                   read      xmlinput
000591140307     C                   eval      bldexc = readElement('execsql')
000592140307     C                   if        isCondition(condList)
000593140307     C                   exsr      TolibToken
000594140307     C                   exsr      RplVars
000595140307     C                   exsr      srSQLExecute
000596140307     C                   endif
000597140307      *--------------------------------------------------------------------
000598140307      * Send messages to user as program executes
000599140307      * Extract message ID, Message Type, from <sendmsg>
000600140307      * read a record and get the single line of message text.
000601140307      *--------------------------------------------------------------------
000602140307     C                   when      xmltag1 = '<sendmsg '
000603140307     C                   eval      qm_msgid = %subst(xmlcode:22:7)
000604140307     C                   eval      qm_msgtyp = %subst(xmlcode:46:10)
000605140307     C                   read      xmlinput
000606140307     C                   eval      qm_msgq   = '*EXT   '
000607140307     C                   eval      qm_msgtxt = xmlcode
000608140307     C                   exsr      srSndMessage
000609140307      *--------------------------------------------------------------------
000610140307      * rtvobjd statement. retrieve object description of the specified
000611140307      * object. Store attributes in specified fields.
000612140307      *--------------------------------------------------------------------
000613140307     C                   when      xmltag1 = '<rtvobjd '
000614140307     C                   eval      condList = getAttrData('condition'
000615140307     C                                                    :xmlcode:'')
000616140307     C                   eval      bldexc = readElement('rtvobjd')
000617140307     C                   eval      objd_obj    =getAttrData('obj'    :bldexc:'')
000618140307     C                   eval      objd_lib    =getAttrData('lib'    :bldexc:'')
000619140307     C                   eval      objd_objtype=getAttrData('objtype':bldexc:'')
000620140307     C                   eval      objd_rtnlib =getAttrData('rtnlib' :bldexc:'')
000621140307     C                   if        isCondition(condList)
000622140307     C                   exsr      srRtvObjD
000623140307     C                   endif
000624140307      *--------------------------------------------------------------------
000625140307      * chgvar statement.
000626140307      *--------------------------------------------------------------------
000627140307     C                   when      xmltag1 = '<chgvar  '
000628140307     C                   eval      condList = getAttrData('condition'
000629140307     C                                                    :xmlcode:'')
000630140307     C                   eval      bldexc = readElement('chgvar')
000631140307     C                   eval      var_name  = getAttrData('var'    :bldexc:'')
000632140307     C                   eval      var_value = getAttrData('value'  :bldexc:'')
000633140307     C                   if        isCondition(condList)
000634140307     C                   exsr      srChgVar
000635140307     C                   endif
000636140307      *--------------------------------------------------------------------
000637140307      * Query condition
000638140307      *--------------------------------------------------------------------
000639140307     C                   when      xmltag1 = '<qrycond '
000640140307     C                   eval      bldexc = readElement('qrycond')
000641140307     C                   eval      cnd_msg    = getAttrData('msg'   : bldexc:'')
000642140307     C                   eval      cnd_values = getAttrData('values': bldexc:'')
000643140307     C                   exsr      srQueryCond
000644140307     C                   endsl
000645140307      *
000646140307     C                   read      xmlinput
000647140307     C                   enddo
000648140307      *
000649140307     C                   exsr      srSQLEnd
000650140307     C                   eval      *inlr=*on
000651140307     C                   return
000652140307      *--------------------------------------------------------------------
000653140307      * Replace &tolib (no matter how many times it is in the string)
000654140307      * with whatever library the user has selected at run time.
000655140307      *--------------------------------------------------------------------
000656140307     C     TolibToken    begsr
000657140307     C                   eval      aa=%scan('&tolib':bldexc)
000658140307     C                   dow       aa>0
000659140307     C                   eval      bldexc=%replace(%trimr(ParseSrcLib):bldexc:
000660140307     C                             aa:6)
000661140307     C                   eval      aa=%scan('&tolib':bldexc)
000662140307     C                   enddo
000663140307      *  user has selected to override source, reset SRCFILE parm in bldexcs.
000664140307     C                   if        OvrSrcFile <> ''                             xmlpreview override
000665140307     C                   eval      aa=%scan('SRCFILE(':bldexc)
000666140307     C                   if        aa>0
000667140307     C                   eval      aa=%scan('/':bldexc:aa)
000668140307     C                   if        aa>0
000669140307     C                   eval      ll=%scan(')':bldexc:aa)
000670140307     C                   eval      bldexc=%replace(
000671140307     C                               %trimr(OvrSrcFile):bldexc:aa+1:ll-(aa+1))
000672140307     C                   endif
000673140307     C                   endif
000674140307     C                   endif
000675140307     C                   endsr
000676140307      *--------------------------------------------------------------------
000677140307      * Replace variable names with their associated values.
000678140307      *--------------------------------------------------------------------
000679140307     C     RplVars       begsr
000680140307     C                   eval      aa=%scan('&':bldexc)
000681140307     C                   dow       aa>0
000682140307     C                   eval      var_name=getVarName(bldexc:aa)
000683140307     C                   eval      var_value=getVarValue(var_name)
000684140307     C                   eval      bldexc=%replace(var_value:bldexc:aa
000685140307     C                                             :%len(var_name))
000686140307     C                   eval      aa=%scan('&':bldexc)
000687140307     C                   enddo
000688140307     C                   endsr
000689140307      *--------------------------------------------------------------------
000690140307      * Sets the target release according to the optional parameter
000691140307      * i_TgtRls.
000692140307      *--------------------------------------------------------------------
000693140307     C     SetTgtRls     begsr
000694140307     C                   if        TgtRls = ''
000695140307     C                   leavesr
000696140307     C                   endif
000697140307     C                   eval      bldexc =
000698140307     C                                setTargetRelease('TGTRLS': bldexc: TgtRls)
000699140307     C                   endsr
000700140307      *--------------------------------------------------------------------
000701140307      * Check of file, lib, member exist.
000702140307      *--------------------------------------------------------------------
000703140307     C     srValidate    begsr
000704140307     C                   callp     QUSRMBRD(
000705140307     C                             vrcvar:
000706140307     C                             145:
000707140307     C                             'MBRD0100':
000708140307     C                             ParseSrcFile + ParseSrcLib:
000709140307     C                             ParseSrcMbr:
000710140307     C                             '0':
000711140307     C                             errCode)
000712140307      *   --------------------------------------------------------------------------------
000713140307      *   If error occurred on call, send appropriate message back to user.
000714140307      *   ---------------------------------------------------------------------------------
000715140307     C                   if        errCode_bytAvl <> 0                          error occurred
000716140307     C                   select
000717140307      * lib not found
000718140307     C                   when      errCode_excID = 'CPF9810'
000719140307     C                   eval      qm_msgtxt = '0000 Library ' +
000720140307     C                             %trimr(ParseSrcLib) + ' was not found.'
000721140307      * src file not found
000722140307     C                   when      errCode_excID = 'CPF9812'
000723140307     C                   eval      qm_msgtxt = '0000 Source file ' +
000724140307     C                             %trimr(ParseSrcFile)+' was not found in ' +
000725140307     C                             %trimr(ParseSrcLib) + '.'
000726140307      * member not found
000727140307     C                   when      errCode_excID = 'CPF9815'
000728140307     C                   eval      qm_msgtxt = '0000 Member ' +
000729140307     C                             %trimr(ParseSrcMbr)+' was not found in ' +
000730140307     C                             %trimr(ParseSrcLib)+'/'+ %trimr(ParseSrcFile)
000731140307      * unexpected
000732140307     C                   other
000733140307     C                   eval      qm_msgtxt = '0000 Unexpected message ' +
000734140307     C                             errCode_excID + ' received. '
000735140307     C                   endsl
000736140307      * send message
000737140307     C                   exsr      srKill
000738140307     C                   eval      *inlr=*on
000739140307     C                   return
000740140307     C                   endif
000741140307     C                   endsr
000742140307      *--------------------------------------------------------------------
000743140307      * kill application
000744140307      *--------------------------------------------------------------------
000745140307     C     srKill        begsr
000746140307     C                   eval      qm_msgid  = 'CPD0006'
000747140307     C                   eval      qm_msgtyp = '*DIAG'
000748140307     C                   eval      qm_msgq   = '*CTLBDY'
000749140307     C                   exsr      srSndMessage
000750140307     C                   eval      qm_msgtxt = *blanks
000751140307     C                   eval      qm_msgid  = 'CPF0002'
000752140307     C                   eval      qm_msgtyp = '*ESCAPE'
000753140307     C                   exsr      srSndMessage
000754140307     C                   endsr
000755140307      *--------------------------------------------------------------------
000756140307      * call send program message api
000757140307      *--------------------------------------------------------------------
000758140307     C     srSndMessage  begsr
000759140307     C                   callp     QMHSNDPM(
000760140307     C                             qm_msgid:
000761140307     C                             'QCPFMSG   *LIBL     ':
000762140307     C                             qm_msgtxt:
000763140307     C                             %size(qm_msgtxt):
000764140307     C                             qm_msgtyp:
000765140307     C                             qm_msgq:
000766140307     C                             1:
000767140307     C                             '    ':
000768140307     C                             errCode)
000769140307     C                   endsr
000770140307      *--------------------------------------------------------------------
000771140307      * retrieve object description and store attributes
000772140307      *--------------------------------------------------------------------
000773140307     C     srRtvObjD     begsr
000774140307     C                   callp     QUSROBJD(objd0100
000775140307     C                                      : %size(objd0100)
000776140307     C                                      : 'OBJD0100'
000777140307     C                                      : objd_obj + objd_lib
000778140307     C                                      : objd_objtype
000779140307     C                                      : errCode)
000780140307     C                   if        errCode_bytAvl <> 0                          error occurred
000781140307     C                   eval      qm_msgtxt = '0000 Failed to retrieve +
000782140307     C                             object description of object ' +
000783140307     C                             %trim(objd_lib) + '/' + %trim(objd_obj) +
000784140307     C                             ' type ' + %trim(objd_objtype) + '.'
000785140307      * send message
000786140307     C                   exsr      srKill
000787140307     C                   else
000788140307     C                   callp     setVariable(objd_rtnlib: od_rtnlib)
000789140307     C                   endif
000790140307     C                   endsr
000791140307      *--------------------------------------------------------------------
000792140307      * change variable
000793140307      *--------------------------------------------------------------------
000794140307     C     srChgVar      begsr
000795140307     C                   callp     setVariable(var_name: var_value)
000796140307     C                   endsr
000797140307      *--------------------------------------------------------------------
000798140307      * execute SQL statement.
000799140307      *--------------------------------------------------------------------
000800140307     C     srSQLExecute  begsr
000801140307     C                   exsr      srSQLInit
000802140307     C                   Eval      retCode = SQLExecDirect(hstmt    :
000803140307     C                                                     bldexc   :
000804140307     C                                                     SQL_NTS  )
000805140307     C                   endsr
000806140307      *--------------------------------------------------------------------
000807140307      * query condition.
000808140307      *--------------------------------------------------------------------
000809140307     C     srQueryCond   begsr
000810140307     C                   if        isPreDefCond = *off
000811140307     C                   eval      cnd_values = uCase(cnd_values)
000812140307     C                   dou       (%scan(cnd_rtnVal: cnd_values) > 0) and
000813140307     C                             (cnd_rtnval <> ''                 )
000814140307     C                   eval      cnd_rtnval = ' '
000815140307     C     cnd_msg       dsply                   cnd_rtnval
000816140307     C                   eval      cnd_rtnVal = uCase(cnd_rtnVal)
000817140307     C                   enddo
000818140307     C                   eval      cnd_ptr = cnd_ptr + 1
000819140307     C                   eval      cnd_array(cnd_ptr) = cnd_rtnVal
000820140307     C                   endif
000821140307     C                   endsr
000822140307      *--------------------------------------------------------------------
000823140307      * initialize SQL.
000824140307      *--------------------------------------------------------------------
000825140307     C     srSQLInit     begsr
000826140307      *
000827140307     C                   if        sqlInit = *off
000828140307     C                   Eval      retCode = SQLAllocEnv(%addr(henv))
000829140307     C                   Eval      retCode = SQLAllocConnect(henv: %addr(hdbc))
000830140307     C                   Eval      retCode = SQLConnect(hdbc      :
000831140307     C                                                  '*LOCAL'  :
000832140307     C                                                  SQL_NTS   :
000833140307     C                                                  *null     :
000834140307     C                                                  SQL_NTS   :
000835140307     C                                                  *null     :
000836140307     C                                                  SQL_NTS   )
000837140307     C                   Eval      cOptVal = SQL_COMMIT_NONE
000838140307     C                   Eval      retCode = SQLSetConnectOption(hdbc          :
000839140307     C                                                          SQL_ATTR_COMMIT:
000840140307     C                                                          %addr(cOptVal) )
000841140307     C                   Eval      retCode = SQLAllocStmt(hdbc       :
000842140307     C                                                  %addr(hstmt) )
000843140307     C                   eval      sqlInit = *on
000844140307     C                   endif
000845140307     C                   endsr
000846140307      *--------------------------------------------------------------------
000847140307      * end SQL.
000848140307      *--------------------------------------------------------------------
000849140307     C     srSQLEnd      begsr
000850140307     C                   if        sqlInit = *on
000851140307     C                   Eval      retCode = SQLFreeStmt(hstmt    :
000852140307     C                                                   SQL_DROP )
000853140307     C                   Eval      retCode = SQLDisconnect(hdbc)
000854140307     C                   Eval      retCode = SQLFreeConnect(hdbc)
000855140307     C                   Eval      retCode = SQLFreeEnv(henv)
000856140307     C                   eval      sqlInit = *off
000857140307     C                   endif
000858140307     C                   endsr
000859140307      *--------------------------------------------------------------------
000860140307      * error handler
000861140307      *--------------------------------------------------------------------
000862140307     C     *pssr         begsr
000863140307     C                   if        errFLag = *off
000864140307     C                   eval      errFlag = *on
000865140307     C                   exsr      srSQLEnd
000866140307     C                   endif
000867140307     C                   endsr     '*CANCL'
000868140307     Oqxxxsrc   e            write_one
000869140307     O                       srcSeqno             6
000870140307     O                                           12 '000000'
000871140307     O                       xmlcode            112
000872140307      *--------------------------------------------------------------------
000873140307      * Read element
000874140307      *--------------------------------------------------------------------
000875140307     P readElement...
000876140307     P                 B
000877140307      *
000878140307     D readElement     PI                        like(bldexc)
000879140307     D  i_element                     9A   value varying
000880140307      *
000881140307     D i               S             10I 0 inz
000882140307     D value           S                   like(bldexc ) inz
000883140307     D endTag          S                   like(xmlTag2) inz
000884140307      *
000885140307     C                   eval      i = 1
000886140307     C                   eval      endTag = '</' + i_element + '>'
000887140307     C                   dow       xmltag2 <> endTag
000888140307     C                   eval      %subst(value: i: %len(xmlcode)) = xmlcode
000889140307     C                   eval      i = i + %len(xmlcode)
000890140307     C                   read      xmlinput
000891140307     C                   enddo
000892140307      *
000893140307     C                   Return    value
000894140307      *
000895140307     P readElement...
000896140307     P                 E
000897140307      *--------------------------------------------------------------------
000898140307      * Double quotes
000899140307      *--------------------------------------------------------------------
000900140307     P dblQuotes...
000901140307     P                 B
000902140307      *
000903140307     D dblQuotes       PI          2048A         varying
000904140307     D  i_string                   2048A   value varying
000905140307      *
000906140307     D x               S             10I 0         inz
000907140307     D string          S           2048A   varying inz
000908140307      *
000909140307     C                   For       x = 1 to %len(i_string)
000910140307     C                   Eval      string = string + %subst(i_string: x:1)
000911140307     C                   If        %subst(i_string: x : 1) = qs
000912140307     C                   Eval      string = string + qs
000913140307     C                   Endif
000914140307     C                   Endfor
000915140307      *
000916140307     C                   Return    string
000917140307      *
000918140307     P dblQuotes...
000919140307     P                 E
000920140307      *---------------------------------------------------------------------------------------------
000921140307      *  Get Attribute Data from String
000922140307      *---------------------------------------------------------------------------------------------
000923140307     P getAttrData...
000924140307     P                 B
000925140307      *
000926140307     D getAttrData...
000927140307     D                 PI          2000A          varying                       like(string_t)
000928140307     D  i_attr                       10a   value
000929140307     D  i_string                   2000A   value  varying                       like(string_t)
000930140307     D  i_default                  2000A   value  varying                       like(string_t)
000931140307      *
000932140307      *  return value
000933140307     D attrData        S           2000A   varying inz                          like(string_t)
000934140307      *
000935140307      *  local fields
000936140307     D attr            S             20a   varying inz
000937140307     D lenAttr         S             10i 0 inz
000938140307     D aa              S             10i 0 inz
000939140307     D cc              S             10i 0 inz
000940140307      *
000941140307     D Qd              C                   const('"')
000942140307      *-------------------------------------------------------------------*
000943140307      *
000944140307      *  find the keyword
000945140307     C                   eval      attr = %trim(i_attr) + '=' + Qd
000946140307      *
000947140307     C                   eval      aa   = %scan(attr: i_string)
000948140307     C                   if        aa = 0
000949140307     C                   return    i_default
000950140307     C                   endif
000951140307      *
000952140307     C                   eval      aa   = aa + %len(attr)
000953140307     C                   eval      cc   = %scan(Qd: i_string: aa+1)
000954140307      *
000955140307      *  return the data between the double-quotes
000956140307     C                   if        (cc-aa) > 0
000957140307     C                   eval      attrData = %subst(i_string: aa: cc-aa)
000958140307     C                   endif
000959140307      *
000960140307     C                   Return    attrData
000961140307      *
000962140307     P getAttrData...
000963140307     P                 E
000964140307      *---------------------------------------------------------------------------------------------
000965140307      *  Load condition array
000966140307      *---------------------------------------------------------------------------------------------
000967140307     P loadConditions...
000968140307     P                 B
000969140307      *
000970140307     D loadConditions...
000971140307     D                 PI                  dim(32) like(condition)
000972140307     D  i_conditions                128A   value
000973140307      *
000974140307      *  return value
000975140307     D cnd_array       s                   dim(32) inz like(condition)
000976140307     D pValue          s               *   inz
000977140307     D x               s             10i 0 inz
000978140307      *-------------------------------------------------------------------*
000979140307     C                   eval      i_conditions = uCase(i_conditions)
000980140307      *
000981140307     C                   eval      pValue = strtok(i_conditions: ';')
000982140307     C                   dow       pValue <> *null
000983140307     C                   eval      x = x + 1
000984140307     C                   eval      cnd_array(x) = %str(pValue)
000985140307     C                   eval      pValue = strtok(*null: ';')
000986140307     C                   enddo
000987140307      *
000988140307     C                   Return    cnd_array
000989140307     P loadConditions...
000990140307     P                 E
000991140307      *---------------------------------------------------------------------------------------------
000992140307      *  Check for a condition
000993140307      *---------------------------------------------------------------------------------------------
000994140307     P isCondition...
000995140307     P                 B
000996140307      *
000997140307     D isCondition...
000998140307     D                 PI              n
000999140307     D  i_condList                 2000A   value
001000140307      *
001001140307      *  return value
001002140307     D isCondition     S               n   inz
001003140307     D pValue          S               *   inz
001004140307     D aCondition      S             10a   inz
001005140307      *-------------------------------------------------------------------*
001006140307     C                   eval      i_condList = uCase(i_condList)
001007140307      *
001008140307     C                   if        (i_condList = '*NONE')  or
001009140307     C                             (i_condList = ''     )
001010140307     C                   eval      *in01 = *on
001011140307     C                   else
001012140307      *
001013140307     C                   eval      pValue = strtok(i_condList: ' ')
001014140307     C                   dow       pValue <> *null
001015140307     C                   eval      aCondition = %str(pValue)
001016140307     C     aCondition    lookup    cnd_array                              01
001017140307     C                   if        not *in01
001018140307     C                   leave
001019140307     C                   endif
001020140307     C                   eval      pValue = strtok(*null     : ' ')
001021140307     C                   enddo
001022140307      *
001023140307     C                   endif
001024140307      *
001025140307     C                   if        *in01
001026140307     C                   eval      isCondition = *on
001027140307     C                   else
001028140307     C                   eval      isCondition = *off
001029140307     C                   endif
001030140307      *
001031140307     C                   Return    isCondition
001032140307     P isCondition...
001033140307     P                 E
001034140307      *---------------------------------------------------------------------------------------------
001035140307      *  Check the release level
001036140307      *---------------------------------------------------------------------------------------------
001037140307     P isRelease...
001038140307     P                 B
001039140307      *
001040140307     D isRelease...
001041140307     D                 PI              n
001042140307     D  i_release                    10A   value
001043140307      *
001044140307      *  return value
001045140307     D isRelease       S               n   inz(*off)
001046140307      *
001047140307      *  QSZRTVPR API
001048140307     D QSZRTVPR...
001049140307     D                 PR                  extpgm('QSZRTVPR')
001050140307     D  o_rcvVar                  65535A          options(*varsize)
001051140307     D  i_lenRcvVar                  10I 0 const
001052140307     D  i_format                      8A   const
001053140307     D  o_prodInf                 65535A   const  options(*varsize)
001054140307     D  io_errCode                65535A          options(*varsize)
001055140307      *
001056140307     D prdi0100        DS                  qualified
001057140307     D  productID              1      7A   inz('*OPSYS')
001058140307     D  releaseLvl             8     13A   inz('*CUR'  )
001059140307     D  productOpt            14     17A   inz('0000'  )
001060140307     D  loadID                18     27A   inz('*CODE' )
001061140307      *  Shortened!:
001062140307     D prdr0100        DS                  qualified
001063140307     D  bytRet                 1      4I 0
001064140307     D  bytAvl                 5      8I 0
001065140307     D  reserved_1             9     12I 0
001066140307     D  productID             13     19A
001067140307     D  releaseLvl            20     25A
001068140307      *  API error code
001069140307     D errCode         DS
001070140307     D  bytPrv                       10i 0 inz(0)
001071140307     D  bytAvl                       10i 0 inz(0)
001072140307      *  Release
001073140307     D release         DS
001074140307     D  r_operator             1      3a
001075140307     D  r_space                4      4a
001076140307     D  r_level                5     10a
001077140307      *-------------------------------------------------------------------*
001078140307     C                   If        i_release = '*CURRENT'
001079140307     C                   Eval      isRelease = *on
001080140307     C                   Else
001081140307     C                   Eval      release = i_release
001082140307     C                   Callp     QSZRTVPR(prdr0100: %size(prdr0100):
001083140307     C                                      'PRDR0100': prdi0100: errCode)
001084140307     C                   Select
001085140307     C                   When      r_operator = '*LT' and
001086140307     C                             prdr0100.releaseLvl < i_release
001087140307     C                   Eval      isRelease = *on
001088140307     C                   When      r_operator = '*EQ' and
001089140307     C                             prdr0100.releaseLvl = i_release
001090140307     C                   Eval      isRelease = *on
001091140307     C                   When      r_operator = '*GT' and
001092140307     C                             prdr0100.releaseLvl > i_release
001093140307     C                   Eval      isRelease = *on
001094140307     C                   When      r_operator = '*LE' and
001095140307     C                             prdr0100.releaseLvl <= i_release
001096140307     C                   Eval      isRelease = *on
001097140307     C                   When      r_operator = '*GE' and
001098140307     C                             prdr0100.releaseLvl >= i_release
001099140307     C                   Eval      isRelease = *on
001100140307     C                   Other
001101140307     C                   Eval      isRelease = *off
001102140307     C                   Endsl
001103140307     C                   Endif
001104140307      *
001105140307     C                   Return    isRelease
001106140307     P isRelease...
001107140307     P                 E
001108140307      *---------------------------------------------------------------------------------------------
001109140307      *  Replaces the value of a given keyword.
001110140307      *---------------------------------------------------------------------------------------------
001111140307     P setTargetRelease...
001112140307     P                 B
001113140307      *
001114140307     D setTargetRelease...
001115140307     D                 PI          2000A          varying
001116140307     D  i_keyword                    10A   value  varying
001117140307     D  i_string                   2000A   value  varying
001118140307     D  i_value                      10A   value  varying
001119140307      *
001120140307      *  return value
001121140307     D string          S           2000A   varying inz
001122140307      *
001123140307      *  helper fields
001124140307     D x               S             10I 0 inz
001125140307     D l               S             10I 0 inz
001126140307     D tmpKeyword      S                   like(i_keyword) inz
001127140307     D tmpString       S                   like(i_string ) inz
001128140307     D tmpCmd          S             10A   inz
001129140307      *-------------------------------------------------------------------*
001130140307      /FREE
001131140307         tmpKeyword = uCase(%trimR(i_keyword));
001132140307         tmpString  = uCase(%trimR(i_string ));
001133140307         x = %scan(' ': tmpString);
001134140307         if x = 0;
001135140307            return i_string;
001136140307         endif;
001137140307         tmpCmd = %subst(tmpString: 1: x);
001138140307         if tmpCmd <> 'CRTRPGMOD' and
001139140307            tmpCmd <> 'CRTRPGPGM' and
001140140307            tmpCmd <> 'CRTCLMOD'  and
001141140307            tmpCmd <> 'CRTCLPGM'  and
001142140307            tmpCmd <> 'CRTCBLMOD' and
001143140307            tmpCmd <> 'CRTCBLPGM' and
001144140307            tmpCmd <> 'CRTCMOD'   and
001145140307            tmpCmd <> 'CRTCPPMOD' and
001146140307            tmpCmd <> 'CRTBNDC'   and
001147140307            tmpCmd <> 'CRTBNDCBL' and
001148140307            tmpCmd <> 'CRTBNDCL'  and
001149140307            tmpCmd <> 'CRTBNDCPP' and
001150140307            tmpCmd <> 'CRTBNDDIR' and
001151140307            tmpCmd <> 'CRTBNDRPG' and
001152140307            tmpCmd <> 'CRTPGM'    and
001153140307            tmpCmd <> 'CRTSRVPGM' ;
001154140307            return i_string;
001155140307         endif;
001156140307         x = %scan(tmpKeyword + '(': tmpString);
001157140307         if x = 0;
001158140307            return %trimR(i_string) +
001159140307                   ' ' + tmpKeyword + '(' + %trimR(i_value) + ')';
001160140307         endif;
001161140307         l = %scan(')': tmpString: x + %len(i_keyword) + 1);
001162140307         if l = 0;
001163140307            return i_string;
001164140307         endif;
001165140307         l = l - x + 1;
001166140307         string = %replace(tmpKeyword + '(' + %trimR(i_value) + ')'
001167140307                           : i_string: x: l);
001168140307         Return string;
001169140307      /END-FREE
001170140307      *
001171140307     P setTargetRelease...
001172140307     P                 E
001173140307      *---------------------------------------------------------------------------------------------
001174140307      *  Store field value.
001175140307      *---------------------------------------------------------------------------------------------
001176140307     P setVariable...
001177140307     P                 B
001178140307      *
001179140307     D setVariable...
001180140307     D                 PI
001181140307     D  i_fldName                    10A   value  varying
001182140307     D  i_value                      64A   value  varying
001183140307     D i               s             10I 0 inz
001184140307      *-------------------------------------------------------------------*
001185140307      /FREE
001186140307         if (i_fldName = '');
001187140307            return;
001188140307         endif;
001189140307         for i = 1 to fld_x;
001190140307            if (uCase('&' + %trim(i_fldName)) = uCase(fld_name(fld_x)));
001191140307               fld_value(fld_x) = i_value;
001192140307               leave;
001193140307            endif;
001194140307         endfor;
001195140307         if (fld_x >= %elem(fld_name));
001196140307            return;
001197140307         endif;
001198140307         fld_x = fld_x + 1;
001199140307         fld_name(fld_x)  = uCase('&' + %trim(i_fldName));
001200140307         fld_value(fld_x) = i_value;
001201140307      /END-FREE
001202140307      *
001203140307     P setVariable...
001204140307     P                 E
001205140307      *---------------------------------------------------------------------------------------------
001206140307      *  get variable name from statement.
001207140307      *---------------------------------------------------------------------------------------------
001208140307     P getVarName...
001209140307     P                 B
001210140307      *
001211140307     D getVarName...
001212140307     D                 PI            11a          varying
001213140307     D  i_bldexc                           value  like(bldexc)
001214140307     D  i_pos                        10i 0 value
001215140307     D name            s             11a   varying inz
001216140307     D CHARS           c                   '&ABCDEFGHIJKLMNOPQRSTUVWXYZ+
001217140307     D                                      1234567890_'
001218140307      *-------------------------------------------------------------------*
001219140307      /FREE
001220140307         dow (i_pos<=%len(i_bldexc) and
001221140307              %check(CHARS:%subst(i_bldexc:i_pos:1))=0);
001222140307            name = name+%subst(i_bldexc:i_pos:1);
001223140307            i_pos=i_pos+1;
001224140307         enddo;
001225140307         return name;
001226140307      /END-FREE
001227140307      *
001228140307     P getVarName...
001229140307     P                 E
001230140307      *---------------------------------------------------------------------------------------------
001231140307      *  get variable value.
001232140307      *---------------------------------------------------------------------------------------------
001233140307     P getVarValue...
001234140307     P                 B
001235140307      *
001236140307     D getVarValue...
001237140307     D                 PI            64a          varying
001238140307     D  i_name                       11a   value  varying
001239140307     D i               s             10i 0 inz
001240140307     D value           s             64a   varying inz
001241140307      *-------------------------------------------------------------------*
001242140307      /FREE
001243140307         for i=1 to fld_x;
001244140307            if (ucase(fld_name(i))=uCase(i_name));
001245140307               value=%trimR(fld_value(i));
001246140307               leave;
001247140307            endif;
001248140307         endfor;
001249140307         return value;
001250140307      /END-FREE
001251140307      *
001252140307     P getVarValue...
001253140307     P                 E
001254140307      *---------------------------------------------------------------------------------------------
001255140307      *  convert to upper case
001256140307      *---------------------------------------------------------------------------------------------
001257140307     P uCase...
001258140307     P                 B
001259140307      *
001260140307     D uCase...
001261140307     D                 PI          2000A          varying
001262140307     D  i_string                   2000A   value  varying
001263140307     D uCase           s           2000A   inz varying
001264140307     D LC              C                   const('abcdefghijklmnopqrstuvwxyz')
001265140307     D UC              C                   CONST('ABCDEFGHIJKLMNOPQRSTUVWXYZ')
001266140307      *-------------------------------------------------------------------*
001267140307      /FREE
001268140307         uCase = %xlate(LC:UC:i_string);
001269140307         return uCase;
001270140307      /END-FREE
001271140307      *
001272140307     P uCase...
001273140307     P                 E
001274140307
