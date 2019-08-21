000100180629      /if not defined(QUSRMBRD)
000101180629      /define QUSRMBRD
000102180629      *
000103180629      *  Retrieve Member Description (QUSRMBRD) API
000104180629     d QUSRMBRD...
000105180629     d                 pr                         extpgm('QUSRMBRD')
000106180629     d  o_rcvVar                  65535a          options(*varsize)
000107180629     d  i_lenRcvVar                  10i 0 const
000108180629     d  i_format                      8a   const
000109180629     d  i_qFile                      20A   const
000110180629     d  i_mbr                        10a   const
000111180629     d  i_ovrPrc                      1a   const
000112180629     d  io_errCode                65535a          options(*nopass: *varsize)    | OptGrp 1
000113180629     d  i_findMbrPrc                  1a   const  options(*nopass)              | OptGrp 2
000114180629      *
000115180629      *  MBRD0100 Format
000116180629     d mbrd0100_t...
000117180629     d                 ds                  qualified               based(pDummy)
000118180629     d  bytRet                       10i 0
000119180629     d  bytAvl                       10i 0
000120180629     d  qFile                        20a
000121180629     d  mbr                          10a
000122180629     d  attr                         10a
000123180629     d  srcType                      10a
000124180629     d  crtDatTim                    13A
000125180629     d  srcChgDatTim                 13A
000126180629     d  text                         50A
000127180629     d  isSrcFile                      N
000128180629      *
000129180629      *  MBRD0200 Format
000130180629     d mbrd0200_t...
000131180629     d                 ds                  qualified               based(pDummy)
000132180629     d  bytRet                       10i 0
000133180629     d  bytAvl                       10i 0
000134180629     d  qFile                        20a
000135180629     d  mbr                          10a
000136180629     d  attr                         10a
000137180629     d  srcType                      10a
000138180629     d  crtDatTim                    13A
000139180629     d  srcChgDatTim                 13A
000140180629     d  text                         50A
000141180629     d  isSrcFile                      N
000142180629     d  isRemoteFile                   N
000143180629     d  isLogicalFile                  N
000144180629     d  isODPShareAlw                  N
000145180629     d  reserved_1                    2a
000146180629     d  numTotRcds                   10i 0
000147180629     d  numDltRcds                   10i 0
000148180629     d  dtaSpcSize                   10i 0
000149180629     d  accPathSize                  10i 0
000150180629     d  numBasedMbr                  10i 0
000151180629     d  chgDatTim                    13A
000152180629     d  savDatTim                    13A
000153180629     d  rstDatTim                    13A
000154180629     d  expDate                       7a
000155180629     d  reserved_2                    6a
000156180629     d  numDaysUsed                  10i 0
000157180629     d  dateLastUsed                  7a
000158180629     d  useResetDate                  7a
000159180629     d  reserved_3                    2a
000160180629     d  dtaSpcSizeMlt                10i 0
000161180629     d  accPathSizeMlt...
000162180629     d                               10i 0
000163180629     d  textCcsid                    10i 0
000164180629     d  ofsAddInf                    10i 0
000165180629     d  lenAddInf                    10i 0
000166180629     d  numTotRcdsU                  10u 0
000167180629     d  numDltRcdsU                  10u 0
000168180629     d  reserved_4                    6a
000169180629      *
000170180629      /endif
