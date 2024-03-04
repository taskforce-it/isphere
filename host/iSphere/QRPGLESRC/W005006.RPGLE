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
//  Wrapper (SYNCMBR - retrieve member attributes)
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
//          >>PARM<<  DBGVIEW(*LIST);
//          >>PARM<<  OPTIMIZE(*NONE);
//          >>PARM<<  OPTION(*EVENTF);
//          >>PARM<<  TGTRLS(&TR);
//        >>END-COMPILE<<
//        >>EXECUTE<<
//    >>END-PRE-COMPILER<<
// ------------------------------------------------------------------------------------------
ctl-opt bnddir('ISPHERE') main(W005006);
// ------------------------------------------------------------------------------------------

// Other External Prototypes
/include qcpysrc,syncmbr

// Own Prototype
dcl-pr W005006 extpgm('W005006');
  i_library      char(10) const;
  i_file         char(10) const;
  i_member       char(10) const;
  o_mbrAttrs     likeds(mbrAttrs_t);
end-pr;

// ------------------------------------------------------------------------------------------

dcl-proc W005006;
  dcl-pi *n;
    i_library      char(10) const;
    i_file         char(10) const;
    i_member       char(10) const;
    o_mbrAttrs     likeds(mbrAttrs_t);
  end-pi;

  // ----------------------------------------------------------------------------------------

  // Call service program
  o_mbrAttrs = SYNCMBR_retrieveMemberAttributes(i_library: i_file: i_member);

end-proc;

