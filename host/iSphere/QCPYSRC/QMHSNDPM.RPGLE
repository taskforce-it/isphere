      /if not defined(qmhsndpm)
      /define qmhsndpm
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,types

      // Send Program Message (QMHSNDPM) API
     D QMHSNDPM        PR                        extpgm('QMHSNDPM')
     D   i_msgID                      7a   const
     D   i_qMsgF                           const likeds(qObj_t)
     D   i_msgData                32767a   const options(*varsize )
     D   i_length                    10i 0 const
     D   i_msgType                   10a   const
     D   i_callStkE               32767a   const options(*varsize )
     D   i_callStkC                  10i 0 const
     D   o_msgKey                     4a
     D   io_errCode               32767a         options(*varsize )

      /endif
