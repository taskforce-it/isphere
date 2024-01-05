**free
/if not defined(QUSLOBJ)
/define QUSLOBJ

//  List Objects (QUSLOBJ) API
dcl-pr quslobj
       extpgm('QUSLOBJ');
  i_qUsrSpc       char(20) const;
  i_format        char(8) const;
  i_qObj          char(20) const;
  i_objType       char(10) const;
  io_errcode      char(65535) options(*nopass: *varsize);                // | optGrp 1
  i_authCtrl      char(65535) const  options(*nopass: *varsize);         // | optGrp 2
  i_slcCtrl       char(65535) const  options(*nopass: *varsize);         // |
  i_auxStgPool    char(65535) const  options(*nopass: *varsize);         // | optGrp 3
end-pr;

//  Input Parameter Section
dcl-ds quslobj_inpparm_t qualified template;
  qUsrSpc         char(20);
  format          char(8);
  objSlc          char(10);
  libSlc          char(10);
  objTypeSlc      char(10);
  reserved_1      char(2);
  errBytPrv       int(10);
  lenAuthCtrl     int(10);
  callLvl         int(10);
  ofsObjAuth      int(10);
  numObjAuth      int(10);
  ofsLibAuth      int(10);
  numLibAuth      int(10);
  lenSlcCtrl      int(10);
  slcOmtSts       int(10);
  ofsSts          int(10);
  numSts          int(10);
  lenAuxStgPool   int(10);
  auxStgPoolName  char(10);
  auxStgPoolType  char(10);
end-ds;
//  Object authorities   ARRAY(*) of CHAR(10)
//  Library authorities  ARRAY(*) of CHAR(10)
//  Statuses             ARRAY(*) of CHAR(1)

//  Authority Control Format
dcl-ds quslobj_authCtrl_t qualified template;
  length          char(10);
  callLvl         int(10);
  ofsObjAuth      int(10);
  numObjAuth      int(10);
  ofsLibAuth      int(10);
  numLibAuth      int(10);
  reserved_1      int(10);
end-ds;
//  Object authorities   ARRAY(*) of CHAR(10)
//  Library authorities  ARRAY(*) of CHAR(10)

//  Selection Control Format
dcl-ds quslobj_slcCtrl_t qualified template;
  length          int(10);
  slcOmtSts       int(10);
  ofsSts          int(10);
  numSts          int(10);
  reserved_1      int(10);
end-ds;

/endif
