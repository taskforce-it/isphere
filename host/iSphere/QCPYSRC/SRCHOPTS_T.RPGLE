      /if not defined(srchopts_t)
      /define srchopts_t

     d searchArgument_t...
     d                 DS                  Qualified Based(pTemplate)
     d  operator                     10I 0
     d  string                      256A   Varying
     d  fromColumn                   10I 0
     d  toColumn                     10I 0
     d  case                         10A
     d  regex                        10A

     d SRCHOPTS_OPERATOR_CONTAINS...
     d                 C                   1
     d SRCHOPTS_OPERATOR_CONTAINS_NOT...
     d                 C                   -1

     d SRCHOPTS_MATCH_LINE...
     d                 C                   '*LINE'
     d SRCHOPTS_MATCH_ALL...
     d                 C                   '*ALL'
     d SRCHOPTS_MATCH_ANY...
     d                 C                   '*ANY'
     d SRCHOPTS_MATCH_MSGID...
     d                 C                   '*MSGID'

     d SRCHOPTS_CASE_MATCH...
     d                 C                   '*MATCH'
     d SRCHOPTS_CASE_IGNORE...
     d                 C                   '*IGNORE'

     d SRCHOPTS_ARG_STRING...
     d                 C                   '*STRING'
     d SRCHOPTS_ARG_REGEX...
     d                 C                   '*REGEX'

     d SRCHOPTS_ARGUMENTS_SIZE...
     d                 C                   16

      /endif
