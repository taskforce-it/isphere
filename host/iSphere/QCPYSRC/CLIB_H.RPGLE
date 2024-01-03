      /if not defined(clib_h)
      /define clib_h
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // localtime_r() -- Correct Local Time
      //    It is the same as localtime() except that it passes
      //    in the place to store the returned structure result.
     d localtime_r...
     d                 pr              *   extproc('localtime_r')
     d  time                         10i 0 const
     d  tm                                 const  likeds(tm_t)

      // strftime -- Convert to Formatted Time
      //    strftime() returns the number of bytes placed into the
      //    array pointed to by 'dest'.
     d strftime...
     d                 pr            10i 0 extproc('strftime')
     d  dest                           *   value
     d  maxsize                      10i 0 value
     d  format                         *   value options(*string)
     d  timeptr                            const  likeds(tm_t)
      //
     d tm_t            ds                  qualified  based(pDummy)
     d  tm_sec                       10i 0
     d  tm_min                       10i 0
     d  tm_hour                      10i 0
     d  tm_mday                      10i 0
     d  tm_mon                       10i 0
     d  tm_year                      10i 0
     d  tm_wday                      10i 0
     d  tm_yday                      10i 0
     d  tm_isdst                     10i 0

      /endif
