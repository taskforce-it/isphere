      /if not defined(qgetfilea)
      /define qgetfilea
      //*------------------------------------------------------------------------------------------*
      //*                                                                                          *
      //* Copyright (c) 2012-2015 iSphere Project Owners                                           *
      //* All rights reserved. This program and the accompanying materials                         *
      //* are made available under the terms of the Common Public License v1.0                     *
      //* which accompanies this distribution, and is available at                                 *
      //* http://www.eclipse.org/legal/cpl-v10.html                                                *
      //*                                                                                          *
      //*------------------------------------------------------------------------------------------*

      // Qp0lGetAttr()--Get Attributes
     d Qp0lGetAttr...
     d                 pr            10i 0 extproc('Qp0lGetAttr')
     d  i_path                             const  likeds(Qlg_Path_Name_t)
     d  i_attrArray                        const  like(Qlg_attrArray_t  )
     d  i_pBuffer                      *   value
     d  i_bytPrv                     10u 0 value
     d  o_sizeNeeded                 10u 0
     d  o_bytRet                     10u 0
     d  i_followLnk                  10u 0 value

     d Qlg_Path_Name_t...
     d                 ds                  qualified align         based(pDummy)
     d   CCSID                       10i 0
     d   Country_ID                   2a
     d   Language_ID                  3a
     d   Reserved                     3a
     d   Path_Type                   10u 0
     d   Path_Length                 10i 0
     d   Path_Name_Delimiter...
     d                                2a
     d   Reserved2                   10a
     d   Path_Name                  256a

     d QLG_PATH_JOB_CCSID...
     d                 c                   const(0)
     d QLG_PATH_JOB_COUNTRYID...
     d                 c                   const(X'0000')
     d QLG_PATH_JOB_LANGUAGEID...
     d                 c                   const(X'000000')

      // The Path_Name is a character string and the delimiter is one byte long.
     d QLG_CHAR_SINGLE...
     d                 c                   const(0)

      // The Path_Name is a pointer and the delimiter is one byte long.
     d QLG_PTR_SINGLE...
     d                 c                   const(1)

      // The Path_Name is a character string and the delimiter is two bytes long.
     d QLG_CHAR_DOUBLE...
     d                 c                   const(2)

      // The Path_Name is a pointer and the delimiter is two bytes long.
     d QLG_PTR_DOUBLE...
     d                 c                   const(3)

     d Qlg_attrArray_t...
     d                 ds                  qualified               based(pDummy)
     d  numE                         10i 0
     d  attr                         10i 0 dim(64)

     d Qlg_rtnAttr_t...
     d                 ds                  qualified               based(pDummy)
     d  ofsNextAttr                  10i 0
     d  ID                           10i 0
     d  size                         10i 0
     d  reserved                      4a

     d QP0L_ATTR_OBJTYPE...
     d                 c                   const(0)                             CHAR(10)
     d QP0L_ATTR_DATA_SIZE...
     d                 c                   const(1)                             UINT(4)
     d QP0L_ATTR_ALLOC_SIZE...
     d                 c                   const(2)                             UINT(4)
     d QP0L_ATTR_EXTENDED_ATTR_SIZE...
     d                 c                   const(3)                             UINT(4)
     d QP0L_ATTR_CREATE_TIME...
     d                 c                   const(4)                             UINT(4)
     d QP0L_ATTR_ACCESS_TIME...
     d                 c                   const(5)                             UINT(4)
     d QP0L_ATTR_CHANGE_TIME...
     d                 c                   const(6)                             UINT(4)
     d QP0L_ATTR_MODIFY_TIME...
     d                 c                   const(7)                             UINT(4)
     d QP0L_ATTR_STG_FREE...
     d                 c                   const(8)                             CHAR(1)
     d QP0L_ATTR_CHECKED_OUT...
     d                 c                   const(9)                             chkOut Fmt
     d QP0L_ATTR_LOCAL_REMOTE...
     d                 c                   const(10)                            CHAR(1)

     d QP0L_DONOT_FOLLOW_SYMLNK...
     d                 c                   const(x'00')
     d QP0L_FOLLOW_SYMLNK...
     d                 c                   const(x'01')

     d QP0L_NOT_CHECKED_OUT...
     d                 c                   const(x'00')
     d QP0L_CHECKED_OUT...
     d                 c                   const(x'01')

     d QP0L_LOCAL_OBJ...
     d                 c                   const(x'01')
     d QP0L_REMOTE_OBJ...
     d                 c                   const(x'02')

      /endif
