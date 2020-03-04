      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Debugger program stop handler (dummy for retrieving module listing)                      *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

     d DBGSTPHDLR...
     d                 pr                  extpgm('DBGSTPHDLR')
     d  i_qPgm                             const  likeds(qObj_t)
     d  i_pgmType                    10a   const
     d  i_module                     10a   const
     d  i_stopReason                       const  likeds(psh_stopReason_t)
     d  i_rcvVar                  32767a   const  options(*varsize)
     d  i_numE                       10i 0 const
     d  i_msgData                 32767a   const  options(*varsize)

