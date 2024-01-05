**free
/if not defined(OBJL0200)
/define OBJL0200

//  OBJL0200 List Data Section
dcl-ds objl0200_t qualified template;
  obj             char(10);
  lib             char(10);
  type            char(10);
  infSts          char(1);
  extObjAttr      char(10);
  text            char(50);
  usrDefAttr      char(10);
  reserved_1      char(7);
end-ds;

/endif
