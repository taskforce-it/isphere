      /if not defined(iqsdregdv)
      /define iqsdregdv
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2018 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      /copy qcpysrc,types

      // Procedure prototype for procedure 'IQSDREGDV'
     d IQSDREGDV...
     d                 pr                  extpgm('IQSDREGDV')
     d  go_viewId                    10i 0                                      <-- View id
     d  go_viewLines                 10i 0                                      <-- View lines
     d  go_rtnLib                    10a                                        <-- Returned library
     d  go_viewTmstmp                13a                                        <-- View timestamp
     d  gi_qObj                            const likeds(qObj_t)                 --> Qualified object
     d  gi_objType                   10a   const                                --> Object type
     d  gi_module                    10a   const                                --> Module
     d  gi_viewNumber                10i 0 const                                --> View number
     d  gio_errCode               32767a         options(*nopass:*omit:*varsize)<-> Error code

      /endif
