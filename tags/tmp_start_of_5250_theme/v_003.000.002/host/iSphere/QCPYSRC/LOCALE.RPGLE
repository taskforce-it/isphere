      /IF NOT DEFINED(LOCALE)
      /DEFINE LOCALE

     d nl_langinfo...
     d                 PR              *   extproc('nl_langinfo')
     d  i_item                       10I 0 value

     d CODESET         C                    1
     d D_T_FMT         C                    2
     d D_FMT           C                    3
     d T_FMT           C                    4
     d T_FMT_AMPM      C                    5
     d AM_STR          C                    6
     d PM_STR          C                    7
     d ABDAY_1         C                    8
     d ABDAY_2         C                    9
     d ABDAY_3         C                   10
     d ABDAY_4         C                   11
     d ABDAY_5         C                   12
     d ABDAY_6         C                   13
     d ABDAY_7         C                   14
     d DAY_1           C                   15
     d DAY_2           C                   16
     d DAY_3           C                   17
     d DAY_4           C                   18
     d DAY_5           C                   19
     d DAY_6           C                   20
     d DAY_7           C                   21
     d ABMON_1         C                   22
     d ABMON_2         C                   23
     d ABMON_3         C                   24
     d ABMON_4         C                   25
     d ABMON_5         C                   26
     d ABMON_6         C                   27
     d ABMON_7         C                   28
     d ABMON_8         C                   29
     d ABMON_9         C                   30
     d ABMON_10        C                   31
     d ABMON_11        C                   32
     d ABMON_12        C                   33
     d MON_1           C                   34
     d MON_2           C                   35
     d MON_3           C                   36
     d MON_4           C                   37
     d MON_5           C                   38
     d MON_6           C                   39
     d MON_7           C                   40
     d MON_8           C                   41
     d MON_9           C                   42
     d MON_10          C                   43
     d MON_11          C                   44
     d MON_12          C                   45
     d RADIXCHAR       C                   46
     d THOUSEP         C                   47
     d YESEXPR         C                   48
     d NOEXPR          C                   49
     d CRNCYSTR        C                   50
     d ERA             C                   51
     d ERA_D_FMT       C                   52
     d ERA_D_T_FMT     C                   53
     d ERA_T_FMT       C                   54
     d ALT_DIGITS      C                   55
     d YESSTR          C                   56
     d NOSTR           C                   57
     d NL_NUM_ITEMS    C                   58

      /ENDIF
