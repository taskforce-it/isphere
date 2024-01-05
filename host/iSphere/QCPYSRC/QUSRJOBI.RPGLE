      /if not defined(QUSRJOBI)
      /define QUSRJOBI
      *
      *  Retrieve Job Information (QUSRJOBI) API
     d QUSRJOBI...
     d                 pr                  extpgm('QUSRJOBI')
     d  o_rcvVar                  65535a          options(*varsize)
     d  i_rcvVarLen                  10i 0 const
     d  i_format                      8a   const
     d  i_qJob                       26A   const
     d  i_intJobID                   16a   const
     d  io_errCode                65535a          options(*nopass: *varsize)    OptGrp 1
     d  i_resPrfStat                  1a   const  options(*nopass)              OptGrp 2
      *
      *  This format primarily returns job attribute types of information.
     d jobi0400_t      ds                  qualified           based(pDummy)
     d  bytRet                       10i 0
     d  bytAvl                       10i 0
     d  job                          10a
     d  user                         10a
     d  nbr                           6a
     d  intJobID                     16a
     d  status                       10a
     d  type                          1a
     d  subType                       1a
     d  dateTimeEnt                  13A
     d  dateTimeAct                  13A
     d  jobAcctCode                  15A
     d  jobD                         10a
     d  jobDLib                      10a
     d  unitOfWorkID                 24A
     d  mode                          8a
     d  inqMsgRply                   10a
     d  logCLPgms                    10a
     d  brkMsgHdl                    10a
     d  stsMsgHdl                    10a
     d  devRcvrAct                   13A
     d  ddmCnvHdl                    10a
     d  dateSep                       1a
     d  dateFmt                       4a
     d  prtText                      30A
     d  sbmJob                       10a
     d  sbmUser                      10a
     d  sbmNbr                        6a
     d  sbmMsgQ                      10a
     d  sbmMsgQLib                   10a
     d  timeSep                       1a
     d  ccsid                        10i 0
     d  dateTimeScd                   8a
     d  prtKeyFmt                    10a
     d  sortSeq                      10a
     d  sortSeqLib                   10a
     d  langID                        3a
     d  countryID                     2a
     d  complSts                      1a
     d  signedOnJob                   1a
     d  jobSws                        8a
     d  msgQFullAct                  10a
     d  reserved_01                   1a
     d  msgQMaxSize                  10i 0
     d  dftCcsid                     10i 0
     d  rtgData                      80A
     d  decFmt                        1a
     d  chrIDCtrl                    10a
     d  serverType                   30A
     d  alwMltThrds                   1a
     d  jobLogPnd                     1a
     d  reserved_02                   1a
     d  jobEndRsn                    10i 0
     d  jobTypeEnhncd                10i 0
     d  dateTimeEnd                  13A
     d  reserved_03                   1a
     d  splFActn                     10a
     d  ofsAspGrpInf                 10i 0
     d  numEAspGrpInf                10i 0
     d  lenAspGrpInfE                10i 0
     d  timeZoneDscNm                10a
     d  jobLogOutput                 10a
      *
      /endif
