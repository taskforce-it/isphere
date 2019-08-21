      /IF NOT DEFINED(ICONV)
      /DEFINE ICONV

     d QtqIconvOpen    PR                  extproc('QtqIconvOpen')
     d                                     like(iconv_t)
     d    tocode                           like(QtqCode_t) const
     d    fromcode                         like(QtqCode_t) const

     d iconv           PR            10I 0 extproc('iconv')
     d   cd                                like(iconv_t) value
     d   inbuf                         *
     d   inbytesleft                 10U 0
     d   outbuf                        *
     d   outbytesleft                10U 0

     d iconv_close     PR            10I 0 extproc('iconv_close')
     d   cd                                like(iconv_t) value

     d iconv_t         DS                  based(pTemplate)
     d   return_value                10I 0
     d   cd                          10I 0 dim(12)

     d QtqCode_t       DS                  based(pTemplate)
     D  CCSID                        10I 0
     D  ConvAlt                      10I 0
     D  SubsAlt                      10I 0
     D  ShiftAlt                     10I 0
     D  InpLenOp                     10I 0
     D  ErrorOpt                     10I 0
     D  Reserved                      8A

      /ENDIF
