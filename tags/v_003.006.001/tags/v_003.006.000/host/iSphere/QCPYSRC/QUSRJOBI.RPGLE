000100191104      /if not defined(QUSRJOBI)
000101191104      /define QUSRJOBI
000102191104      *
000103191104      *  Retrieve Job Information (QUSRJOBI) API
000104191104     d QUSRJOBI...
000105191104     d                 pr                  extpgm('QUSRJOBI')
000106191104     d  o_rcvVar                  65535a          options(*varsize)
000107191104     d  i_rcvVarLen                  10i 0 const
000108191104     d  i_format                      8a   const
000109191104     d  i_qJob                       26A   const
000110191104     d  i_intJobID                   16a   const
000111191104     d  io_errCode                65535a          options(*nopass: *varsize)    OptGrp 1
000112191104     d  i_resPrfStat                  1a   const  options(*nopass)              OptGrp 2
000113191104      *
000114191104      *  This format primarily returns job attribute types of information.
000115191104     d jobi0400_t      ds                  qualified           based(pDummy)
000116191104     d  bytRet                       10i 0
000117191104     d  bytAvl                       10i 0
000118191104     d  job                          10a
000119191104     d  user                         10a
000120191104     d  nbr                           6a
000121191104     d  intJobID                     16a
000122191104     d  status                       10a
000123191104     d  type                          1a
000124191104     d  subType                       1a
000125191104     d  dateTimeEnt                  13A
000126191104     d  dateTimeAct                  13A
000127191104     d  jobAcctCode                  15A
000128191104     d  jobD                         10a
000129191104     d  jobDLib                      10a
000130191104     d  unitOfWorkID                 24A
000131191104     d  mode                          8a
000132191104     d  inqMsgRply                   10a
000133191104     d  logCLPgms                    10a
000134191104     d  brkMsgHdl                    10a
000135191104     d  stsMsgHdl                    10a
000136191104     d  devRcvrAct                   13A
000137191104     d  ddmCnvHdl                    10a
000138191104     d  dateSep                       1a
000139191104     d  dateFmt                       4a
000140191104     d  prtText                      30A
000141191104     d  sbmJob                       10a
000142191104     d  sbmUser                      10a
000143191104     d  sbmNbr                        6a
000144191104     d  sbmMsgQ                      10a
000145191104     d  sbmMsgQLib                   10a
000146191104     d  timeSep                       1a
000147191104     d  ccsid                        10i 0
000148191104     d  dateTimeScd                   8a
000149191104     d  prtKeyFmt                    10a
000150191104     d  sortSeq                      10a
000151191104     d  sortSeqLib                   10a
000152191104     d  langID                        3a
000153191104     d  countryID                     2a
000154191104     d  complSts                      1a
000155191104     d  signedOnJob                   1a
000156191104     d  jobSws                        8a
000157191104     d  msgQFullAct                  10a
000158191104     d  reserved_01                   1a
000159191104     d  msgQMaxSize                  10i 0
000160191104     d  dftCcsid                     10i 0
000161191104     d  rtgData                      80A
000162191104     d  decFmt                        1a
000163191104     d  chrIDCtrl                    10a
000164191104     d  serverType                   30A
000165191104     d  alwMltThrds                   1a
000166191104     d  jobLogPnd                     1a
000167191104     d  reserved_02                   1a
000168191104     d  jobEndRsn                    10i 0
000169191104     d  jobTypeEnhncd                10i 0
000170191104     d  dateTimeEnd                  13A
000171191104     d  reserved_03                   1a
000172191104     d  splFActn                     10a
000173191104     d  ofsAspGrpInf                 10i 0
000174191104     d  numEAspGrpInf                10i 0
000175191104     d  lenAspGrpInfE                10i 0
000176191104     d  timeZoneDscNm                10a
000177191104     d  jobLogOutput                 10a
000178191104      *
000179191104      /endif
