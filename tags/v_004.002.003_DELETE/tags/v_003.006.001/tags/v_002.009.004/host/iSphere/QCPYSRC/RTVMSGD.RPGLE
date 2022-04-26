      /if not defined(RTVMSGD)
      /define RTVMSGD
      *-------------------------------------------------------------------------------------------*
      *                                                                                           *
      * Copyright (c) 2014 Task Force IT-Consulting GmbH, Waltrop (Germany)                       *
      * This software and the accompanying materials are made available                           *
      * under the terms of the GNU General Public License version 2.0 (GPLv2)                     *
      * which accompanies this distribution and is available at                                   *
      * http://www.gnu.org/licenses/gpl-2.0.html                                                  *
      *                                                                                           *
      *-------------------------------------------------------------------------------------------*

     d rtvMsgText      pr           250a   Varying                              <-- Rückkehrwert
     D                                     extproc('RTVMSGD_+
     D                                     rtvMsgText+
     D                                     ')
     d  i_msgID                       7a   Const                                --> Nachrichten-Id.
     d  i_msgF                       10a   Const                                --> Nachrichtendatei
     d  i_lib                        10a   Const                                --> Bibliothek Nachr
     d  i_data                      256a   Const Varying                        --> Nachrichtendaten

     d rtvMsgHelp      pr          3000a   Varying                              <-- Rückkehrwert
     D                                     extproc('RTVMSGD_+
     D                                     rtvMsgHelp+
     D                                     ')
     d  i_msgID                       7a   Const                                --> Nachrichten-Id.
     d  i_msgF                       10a   Const                                --> Nachrichtendatei
     d  i_lib                        10a   Const                                --> Bibliothek Nachr
     d  i_data                      256a   Const Varying                        --> Nachrichtendaten
      /endif
