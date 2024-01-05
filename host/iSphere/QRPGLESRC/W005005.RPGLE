**free
// ------------------------------------------------------------------------------------------
//
//  Copyright (c) 2012-2024 iSphere Project Owners
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Common Public License v1.0
//  which accompanies this distribution, and is available at
//  http://www.eclipse.org/legal/cpl-v10.html
//
// ------------------------------------------------------------------------------------------
//
//  Wrapper (SYNCMBR - resolveGenericCompareElements)
//
// ------------------------------------------------------------------------------------------
//  STRPREPRC Compile Options:
//    >>PRE-COMPILER<<
//      >>CRTCMD<<    CRTBNDRPG  PGM(&LI/&OB) +
//                               SRCFILE(&SL/&SF) SRCMBR(&SM);
//        >>COMPILE<<
//          >>PARM<<  DFTACTGRP(*NO);
//          >>PARM<<  ACTGRP(ISPHERE);
//          >>PARM<<  BNDDIR(ISPHERE);
//          >>PARM<<  DBGVIEW(*NONE);
//          >>PARM<<  OPTIMIZE(*FULL);
//          >>PARM<<  OPTION(*EVENTF);
//          >>PARM<<  TGTRLS(&TR);
//        >>END-COMPILE<<
//        >>EXECUTE<<
//    >>END-PRE-COMPILER<<
// ------------------------------------------------------------------------------------------
ctl-opt bnddir('ISPHERE') main(W005005);
// ------------------------------------------------------------------------------------------

// Other External Prototypes
/include qcpysrc,syncmbr

// Own Prototype
dcl-pr W005005 extpgm('W005005');
  i_handle       zoned(10: 0) const;
  i_mode         char(10) const;
  i_memberFilter char(1024) const;
end-pr;

// ------------------------------------------------------------------------------------------

dcl-proc W005005;
  dcl-pi *n;
    i_handle       zoned(10: 0) const;
    i_mode         char(10) const;
    i_memberFilter char(1024) const;
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Call service program
  SYNCMBR_resolveGenericCompareElements(i_handle: i_mode: i_memberFilter);

end-proc;

