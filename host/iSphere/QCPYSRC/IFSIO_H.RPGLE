000100000000     /*-                                                                            +
000200000000      * Copyright (c) 2001-2021 Scott C. Klement                                    +
000300000000      * All rights reserved.                                                        +
000400000000      *                                                                             +
000500000000      * Redistribution and use in source and binary forms, with or without          +
000600000000      * modification, are permitted provided that the following conditions          +
000700000000      * are met:                                                                    +
000800000000      * 1. Redistributions of source code must retain the above copyright           +
000900000000      *    notice, this list of conditions and the following disclaimer.            +
001000000000      * 2. Redistributions in binary form must reproduce the above copyright        +
001100000000      *    notice, this list of conditions and the following disclaimer in the      +
001200000000      *    documentation and/or other materials provided with the distribution.     +
001300000000      *                                                                             +
001400000000      * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ''AS IS'' AND      +
001500000000      * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE       +
001600000000      * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE  +
001700000000      * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE     +
001800000000      * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL  +
001900000000      * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS     +
002000000000      * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)       +
002100000000      * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT  +
002200000000      * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY   +
002300000000      * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF      +
002400000000      * SUCH DAMAGE.                                                                +
002500000000      *                                                                             +
002600000000      */                                                                            +
002700000000
002800000000      *  This header file contains the constants, structures and prototypes
002900000000      *  for using the Integrated File System API
003000000000      *
003100000000      *  These APIs were designed originally for use in C programming.
003200000000      *  Therefore bear in mind:
003300000000      *   1) All strings must be null-terminated, and variable-length.
003400000000      *        [ options(*string) will fix this for you ]
003500000000      *   2) Errors are returned in the errno variable, and strings for
003600000000      *         them are available by calling strerror()
003700000000      *   3) In order to call errno or strerror, you must bind to the
003800000000      *         QC2LE binding directory.  See ERRNO_H for the definitions
003900000000      *         used to access errno.
004000000000      *
004100000000      *                                               SCK 03/24/1999
004200000000
004300000000      * CHG SCK 03/28/2001  Added options(*string) & /define directives
004400000000      *            now that we're running a post-V4R2 system.
004500000000
004600000000      *
004700000000      *  To use these in your source code, you need a D-spec like this:
004800000000      *  D/COPY source-lib/QRPGLESRC,IFSIO_H
004900000000
005000000000      *  IBM's "UNIX-type APIs" manual explains how to use all of these
005100000000      *      APIs in detail.  However, the manual assumes you're writing
005200000000      *      your programs in C.
005300000000
005400000000      * This header file includes (in order)
005500000000      *   1) Constants
005600000000      *   2) Structures
005700000000      *   3) Prototypes
005800000000
005900000000     D/if defined(IFSIO_H)
006000000000     D/eof
006100000000     D/endif
006200000000
006300000000     D/define IFSIO_H
006400000000
006500000000     D*                                         ascii code-page
006600000000     D CP_DOSASCII     C                   437
006700000000     D CP_ASCII        C                   819
006800000000
006900000000     D**********************************************************************
007000000000     D*  Flags for use in open()
007100000000     D*
007200000000     D* More than one can be used -- add them together.
007300000000     D**********************************************************************
007400000000     D*                                            Reading Only
007500000000     D O_RDONLY        C                   1
007600000000     D*                                            Writing Only
007700000000     D O_WRONLY        C                   2
007800000000     D*                                            Reading & Writing
007900000000     D O_RDWR          C                   4
008000000000     D*                                            Create File if not exist
008100000000     D O_CREAT         C                   8
008200000000     D*                                            Exclusively create
008300000000     D O_EXCL          C                   16
008400000000     D*                                            Assign a CCSID
008500000000     D O_CCSID         C                   32
008600000000     D*                                            Truncate File to 0 bytes
008700000000     D O_TRUNC         C                   64
008800000000     D*                                            Append to File
008900000000     D O_APPEND        C                   256
009000000000     D*                                            Synchronous write
009100000000     D O_SYNC          C                   1024
009200000000     D*                                            Sync write, data only
009300000000     D O_DSYNC         C                   2048
009400000000     D*                                            Sync read
009500000000     D O_RSYNC         C                   4096
009600000000     D*                                            No controlling terminal
009700000000     D O_NOCTTY        C                   32768
009800000000     D*                                            Share with readers only
009900000000     D O_SHARE_RDONLY  C                   65536
010000000000     D*                                            Share with writers only
010100000000     D O_SHARE_WRONLY  C                   131072
010200000000     D*                                            Share with read & write
010300000000     D O_SHARE_RDWR    C                   262144
010400000000     D*                                            Share with nobody.
010500000000     D O_SHARE_NONE    C                   524288
010600000000     D*                                            Assign a code page
010700000000     D O_CODEPAGE      C                   8388608
010800000000     D*                                            Open in text-mode
010900000000     D O_TEXTDATA      C                   16777216
011000000000     D*                                            Allow text translation
011100000000     D*                                            on newly created file.
011200000000     D* Note: O_TEXT_CREAT requires all of the following flags to work:
011300000000     D*           O_CREAT+O_TEXTDATA+(O_CODEPAGE or O_CCSID)
011400000000     D O_TEXT_CREAT    C                   33554432
011500000000     D*                                            Inherit mode from dir
011600000000     D O_INHERITMODE   C                   134217728
011700000000     D*                                            Large file access
011800000000     D*                                            (for >2GB files)
011900000000     D O_LARGEFILE     C                   536870912
012000000000
012100000000     D**********************************************************************
012200000000     D* Access mode flags for access()
012300000000     D*
012400000000     D*   F_OK = File Exists
012500000000     D*   R_OK = Read Access
012600000000     D*   W_OK = Write Access
012700000000     D*   X_OK = Execute or Search
012800000000     D**********************************************************************
012900000000     D F_OK            C                   0
013000000000     D R_OK            C                   4
013100000000     D W_OK            C                   2
013200000000     D X_OK            C                   1
013300000000
013400000000     D**********************************************************************
013500000000     D*      Mode Flags.
013600000000     D*         basically, the mode parm of open(), creat(), chmod(),etc
013700000000     D*         uses 9 least significant bits to determine the
013800000000     D*         file's mode. (peoples access rights to the file)
013900000000     D*
014000000000     D*           user:       owner    group    other
014100000000     D*           access:     R W X    R W X    R W X
014200000000     D*           bit:        8 7 6    5 4 3    2 1 0
014300000000     D*
014400000000     D* (This is accomplished by adding the flags below to get the mode)
014500000000     D**********************************************************************
014600000000     D*                                         owner authority
014700000000     D S_IRUSR         C                   256
014800000000     D S_IWUSR         C                   128
014900000000     D S_IXUSR         C                   64
015000000000     D S_IRWXU         C                   448
015100000000     D*                                         group authority
015200000000     D S_IRGRP         C                   32
015300000000     D S_IWGRP         C                   16
015400000000     D S_IXGRP         C                   8
015500000000     D S_IRWXG         C                   56
015600000000     D*                                         other people
015700000000     D S_IROTH         C                   4
015800000000     D S_IWOTH         C                   2
015900000000     D S_IXOTH         C                   1
016000000000     D S_IRWXO         C                   7
016100000000
016200000000     D**********************************************************************
016300000000     D* "whence" constants for use with lseek()
016400000000     D**********************************************************************
016500220310
016600220310      /if not defined(SEEK_WHENCE_VALUES)
016700220310     D SEEK_SET        C                   CONST(0)
016800220310     D SEEK_CUR        C                   CONST(1)
016900220310     D SEEK_END        C                   CONST(2)
017000220310      /define SEEK_WHENCE_VALUES
017100220310      /endif
017200000000
017300000000     D**********************************************************************
017400000000     D* File Information Structure (stat)
017500000000     D*
017600000000     D* struct stat {
017700000000     D*  mode_t         st_mode;       /* File mode                       */
017800000000     D*  ino_t          st_ino;        /* File serial number              */
017900000000     D*  nlink_t        st_nlink;      /* Number of links                 */
018000000000     D*  uid_t          st_uid;        /* User ID of the owner of file    */
018100000000     D*  gid_t          st_gid;        /* Group ID of the group of file   */
018200000000     D*  off_t          st_size;       /* For regular files, the file
018300000000     D*                                 * size in bytes                   */
018400000000     D*  time_t         st_atime;      /* Time of last access             */
018500000000     D*  time_t         st_mtime;      /* Time of last data modification  */
018600000000     D*  time_t         st_ctime;      /* Time of last file status change */
018700000000     D*  dev_t          st_dev;        /* ID of device containing file    */
018800000000     D*  size_t         st_blksize;    /* Size of a block of the file     */
018900000000     D*  unsigned long  st_allocsize;  /* Allocation size of the file     */
019000000000     D*  qp0l_objtype_t st_objtype;    /* AS/400 object type              */
019100000000     D*  unsigned short st_codepage;   /* Object data codepage            */
019200000000     D*  char           st_reserved1[66]; /* Reserved                     */
019300000000     D* };
019400000000     D*
019500000000     D p_statds        S               *
019600000000     D statds          DS                  BASED(p_statds)
019700000000     D  st_mode                      10U 0
019800000000     D  st_ino                       10U 0
019900000000     D  st_nlink                      5U 0
020000000000     D  st_pad                        2A
020100000000     D  st_uid                       10U 0
020200000000     D  st_gid                       10U 0
020300000000     D  st_size                      10I 0
020400000000     D  st_atime                     10I 0
020500000000     D  st_mtime                     10I 0
020600000000     D  st_ctime                     10I 0
020700000000     D  st_dev                       10U 0
020800000000     D  st_blksize                   10U 0
020900000000     D  st_alctize                   10U 0
021000000000     D  st_objtype                   12A
021100000000     D  st_codepag                    5U 0
021200000000     D  st_resv11                    62A
021300000000     D  st_ino_gen_id                10U 0
021400000000
021500000000
021600000000     D**********************************************************************
021700000000     D* Group Information Structure (group)
021800000000     D*
021900000000     D*  struct group {
022000000000     D*        char    *gr_name;        /* Group name.                      */
022100000000     D*        gid_t   gr_gid;          /* Group id.                        */
022200000000     D*        char    **gr_mem;        /* A null-terminated list of pointers
022300000000     D*                                    to the individual member names.  */
022400000000     D*  };
022500000000     D*
022600000000     D p_group         S               *
022700000000     D group           DS                  Based(p_group)
022800000000     D   gr_name                       *
022900000000     D   gr_gid                      10U 0
023000000000     D   gr_mem                        *   DIM(256)
023100000000
023200000000
023300000000     D**********************************************************************
023400000000     D*
023500000000     D* User Information Structure (passwd)
023600000000     D*
023700000000     D* (Don't let the name fool you, this structure does not contain
023800000000     D*  any password information.  Its named after the UNIX file that
023900000000     D*  contains all of the user info.  That file is "passwd")
024000000000     D*
024100000000     D*   struct passwd {
024200000000     D*        char    *pw_name;            /* User name.                   */
024300000000     D*        uid_t   pw_uid;              /* User ID number.              */
024400000000     D*        gid_t   pw_gid;              /* Group ID number.             */
024500000000     D*        char    *pw_dir;             /* Initial working directory.   */
024600000000     D*        char    *pw_shell;           /* Initial user program.        */
024700000000     D*   };
024800000000     D*
024900000000     D p_passwd        S               *
025000000000     D passwd          DS                  BASED(p_passwd)
025100000000     D  pw_name                        *
025200000000     D  pw_uid                       10U 0
025300000000     D  pw_gid                       10U 0
025400000000     D  pw_dir                         *
025500000000     D  pw_shell                       *
025600000000
025700000000
025800000000     D**********************************************************************
025900000000     D* File Time Structure (utimbuf)
026000000000     D*
026100000000     D* struct utimbuf {
026200000000     D*    time_t     actime;           /*  access time       */
026300000000     D*    time_t     modtime;          /*  modification time */
026400000000     D* };
026500000000     D*
026600000000     D p_utimbuf       S               *
026700000000     D utimbuf         DS                  based(p_utimbuf)
026800000000     D   actime                      10I 0
026900000000     D   modtime                     10I 0
027000000000
027100000000
027200000000     D**********************************************************************
027300000000     D*
027400000000     D* Directory Entry Structure (dirent)
027500000000     D*
027600000000     D* struct dirent {
027700000000     D*   char           d_reserved1[16];  /* Reserved                       */
027800000000     D*   unsigned int   d_reserved2;      /* Reserved                       */
027900000000     D*   ino_t          d_fileno;         /* The file number of the file    */
028000000000     D*   unsigned int   d_reclen;         /* Length of this directory entry
028100000000     D*                                     * in bytes                       */
028200000000     D*   int            d_reserved3;      /* Reserved                       */
028300000000     D*   char           d_reserved4[8];   /* Reserved                       */
028400000000     D*   qlg_nls_t      d_nlsinfo;        /* National Language Information
028500000000     D*                                     * about d_name                   */
028600000000     D*   unsigned int   d_namelen;        /* Length of the name, in bytes
028700000000     D*                                     * excluding NULL terminator      */
028800000000     D*   char           d_name[_QP0L_DIR_NAME]; /* Name...null terminated   */
028900000000     D*
029000000000     D* };
029100000000     D*
029200000000     D p_dirent        s               *
029300000000     D dirent          ds                  based(p_dirent)
029400000000     D   d_reserv1                   16A
029500000000     D   d_reserv2                   10U 0
029600000000     D   d_fileno                    10U 0
029700000000     D   d_reclen                    10U 0
029800000000     D   d_reserv3                   10I 0
029900000000     D   d_reserv4                    8A
030000000000     D   d_nlsinfo                   12A
030100000000     D     nls_ccsid                 10I 0 OVERLAY(d_nlsinfo:1)
030200000000     D     nls_cntry                  2A   OVERLAY(d_nlsinfo:5)
030300000000     D     nls_lang                   3A   OVERLAY(d_nlsinfo:7)
030400000000     D     nls_reserv                 3A   OVERLAY(d_nlsinfo:10)
030500000000     D   d_namelen                   10U 0
030600000000     D   d_name                     640A
030700000000
030800000000
030900000000
031000000000     D*--------------------------------------------------------------------
031100000000     D* Determine file accessibility
031200000000     D*
031300000000     D* int access(const char *path, int amode)
031400000000     D*
031500000000     D*--------------------------------------------------------------------
031600000000     D access          PR            10I 0 ExtProc('access')
031700000000     D   Path                          *   Value Options(*string)
031800000000     D   amode                       10I 0 Value
031900000000
032000000000     D*--------------------------------------------------------------------
032100000000     D* Change Directory
032200000000     D*
032300000000     D* int chdir(const char *path)
032400000000     D*--------------------------------------------------------------------
032500000000     D chdir           PR            10I 0 ExtProc('chdir')
032600000000     D   path                          *   Value Options(*string)
032700000000
032800000000     D*--------------------------------------------------------------------
032900000000     D* Change file authorizations
033000000000     D*
033100000000     D* int chmod(const char *path, mode_t mode)
033200000000     D*--------------------------------------------------------------------
033300000000     D chmod           PR            10I 0 ExtProc('chmod')
033400000000     D   path                          *   Value options(*string)
033500000000     D   mode                        10U 0 Value
033600000000
033700000000     D*--------------------------------------------------------------------
033800000000     D* Change Owner/Group of File
033900000000     D*
034000000000     D* int chown(const char *path, uid_t owner, gid_t group)
034100000000     D*--------------------------------------------------------------------
034200000000     D chown           PR            10I 0 ExtProc('chown')
034300000000     D   path                          *   Value options(*string)
034400000000     D   owner                       10U 0 Value
034500000000     D   group                       10U 0 Value
034600000000
034700000000     D*--------------------------------------------------------------------
034800000000     D* Close a file
034900000000     D*
035000000000     D* int close(int fildes)
035100000000     D*
035200000000     D* Note:  Because close() is already defined in socket_h, we use
035300000000     D*         "closef" here to prevent a conflict.  Now, in V4, we
035400000000     D*         can do the same thing with conditional compilation, so
035500000000     D*         "close" is also defined.
035600000000     D*--------------------------------------------------------------------
035700000000     D closef          PR            10I 0 ExtProc('close')
035800000000     D  handle                       10I 0 value
035900000000
036000000000     D/if not defined(CLOSE_PROTOTYPE)
036100000000     D close           PR            10I 0 ExtProc('close')
036200000000     D  handle                       10I 0 value
036300000000     D/define CLOSE_PROTOTYPE
036400000000     D/endif
036500000000
036600000000     D*--------------------------------------------------------------------
036700000000     D* Close a directory
036800000000     D*
036900000000     D* int closedir(DIR *dirp)
037000000000     D*--------------------------------------------------------------------
037100000000     D closedir        PR            10I 0 EXTPROC('closedir')
037200000000     D  dirhandle                      *   VALUE
037300000000
037400000000     D*--------------------------------------------------------------------
037500000000     D* Create or Rewrite File
037600000000     D*
037700000000     D* int creat(const char *path, mode_t mode)
037800000000     D*--------------------------------------------------------------------
037900000000     D creat           PR            10I 0 ExtProc('creat')
038000000000     D   path                          *   Value options(*string)
038100000000     D   mode                        10U 0 Value
038200000000
038300000000     D*--------------------------------------------------------------------
038400000000     D* Duplicate open file descriptor
038500000000     D*
038600000000     D* int dup(int fildes)
038700000000     D*--------------------------------------------------------------------
038800000000     D dup             PR            10I 0 ExtProc('dup')
038900000000     D   fildes                      10I 0 Value
039000000000
039100000000     D*--------------------------------------------------------------------
039200000000     D* Duplicate open file descriptor to another descriptor
039300000000     D*
039400000000     D* int dup2(int fildes, int fildes2)
039500000000     D*--------------------------------------------------------------------
039600000000     D dup2            PR            10I 0 ExtProc('dup2')
039700000000     D   fildes                      10I 0 Value
039800000000     D   fildes2                     10I 0 Value
039900000000
040000000000     D*--------------------------------------------------------------------
040100000000     D* Change file authorizations by descriptor
040200000000     D*
040300000000     D* int fchmod(int fildes, mode_t mode)
040400000000     D*--------------------------------------------------------------------
040500000000     D fchmod          PR            10I 0 ExtProc('fchmod')
040600000000     D   fildes                      10I 0 Value
040700000000     D   mode                        10U 0 Value
040800000000
040900000000     D*--------------------------------------------------------------------
041000000000     D* Change Owner and Group of File by Descriptor
041100000000     D*
041200000000     D* int fchown(int fildes, uid_t owner, gid_t group)
041300000000     D*--------------------------------------------------------------------
041400000000     D fchown          PR            10I 0 ExtProc('fchown')
041500000000     D   fildes                      10I 0 Value
041600000000     D   owner                       10U 0 Value
041700000000     D   group                       10U 0 Value
041800000000
041900000000     D*--------------------------------------------------------------------
042000000000     D* Perform File Control
042100000000     D*
042200000000     D* int fcntl(int fildes, int cmd, . . .)
042300000000     D*--------------------------------------------------------------------
042400000000     D/if not defined(FCNTL_PROTOTYPE)
042500000000     D fcntl           PR            10I 0 ExtProc('fcntl')
042600000000     D   fildes                      10I 0 Value
042700000000     D   cmd                         10I 0 Value
042800000000     D   arg                         10I 0 Value options(*nopass)
042900000000     D/define FCNTL_PROTOTYPE
043000000000     D/endif
043100000000
043200000000     D*--------------------------------------------------------------------
043300000000     D* Get configurable path name variables by descriptor
043400000000     D*
043500000000     D* long fpathconf(int fildes, int name)
043600000000     D*--------------------------------------------------------------------
043700000000     D fpathconf       PR            10I 0 ExtProc('fpathconf')
043800000000     D   fildes                      10I 0 Value
043900000000     D   name                        10I 0 Value
044000000000
044100000000     D*--------------------------------------------------------------------
044200000000     D* Get File Information by Descriptor
044300000000     D*
044400000000     D* int fstat(int fildes, struct stat *buf)
044500000000     D*--------------------------------------------------------------------
044600000000     D fstat           PR            10I 0 ExtProc('fstat')
044700000000     D   fildes                      10I 0 Value
044800000000     D   buf                           *   Value
044900000000
045000000000     D*--------------------------------------------------------------------
045100000000     D* Synchronize Changes to fIle
045200000000     D*
045300000000     D* int fsync(int fildes)
045400000000     D*--------------------------------------------------------------------
045500000000     D fsync           PR            10I 0 ExtProc('fsync')
045600000000     D   fildes                      10I 0 Value
045700000000
045800000000     D*--------------------------------------------------------------------
045900000000     D* Truncate file
046000000000     D*
046100000000     D* int ftruncate(int fildes, off_t length)
046200000000     D*--------------------------------------------------------------------
046300000000     D ftruncate       PR            10I 0 ExtProc('ftruncate')
046400000000     D   fildes                      10I 0 Value
046500000000     D   length                      10I 0 Value
046600000000
046700000000     D*--------------------------------------------------------------------
046800000000     D* Get current working directory
046900000000     D*
047000000000     D* char *getcwd(char *buf, size_t size)
047100000000     D*--------------------------------------------------------------------
047200000000     D getcwd          PR              *   ExtProc('getcwd')
047300000000     D   buf                           *   Value
047400000000     D   size                        10U 0 Value
047500000000
047600000000     D*--------------------------------------------------------------------
047700000000     D* Get effective group ID
047800000000     D*
047900000000     D* gid_t getegid(void)
048000000000     D*--------------------------------------------------------------------
048100000000     D getegid         PR            10U 0 ExtProc('getegid')
048200000000
048300000000     D*--------------------------------------------------------------------
048400000000     D* Get effective user ID
048500000000     D*
048600000000     D* uid_t geteuid(void)
048700000000     D*--------------------------------------------------------------------
048800000000     D geteuid         PR            10U 0 ExtProc('geteuid')
048900000000
049000000000     D*--------------------------------------------------------------------
049100000000     D* Get Real Group ID
049200000000     D*
049300000000     D* gid_t getgid(void)
049400000000     D*--------------------------------------------------------------------
049500000000     D getgid          PR            10U 0 ExtProc('getgid')
049600000000
049700000000     D*--------------------------------------------------------------------
049800000000     D* Get group information from group ID
049900000000     D*
050000000000     D* struct group *getgrgid(gid_t gid)
050100000000     D*--------------------------------------------------------------------
050200000000     D getgrid         PR              *   ExtProc('getgrgid')
050300000000     D   gid                         10U 0 VALUE
050400000000
050500000000     D*--------------------------------------------------------------------
050600000000     D* Get group info using group name
050700000000     D*
050800000000     D* struct group  *getgrnam(const char *name)
050900000000     D*--------------------------------------------------------------------
051000000000     D getgrnam        PR              *   ExtProc('getgrnam')
051100000000     D   name                          *   VALUE
051200000000
051300000000     D*--------------------------------------------------------------------
051400000000     D* Get group IDs
051500000000     D*
051600000000     D* int getgroups(int gidsetsize, gid_t grouplist[])
051700000000     D*--------------------------------------------------------------------
051800000000     D getgroups       PR              *   ExtProc('getgroups')
051900000000     D   gidsetsize                  10I 0 VALUE
052000000000     D   grouplist                     *
052100000000
052200000000     D*--------------------------------------------------------------------
052300000000     D* Get user information by user-name
052400000000     D*
052500000000     D* (Don't let the name mislead you, this does not return the password,
052600000000     D*  the user info database on unix systems is called "passwd",
052700000000     D*  therefore, getting the user info is called "getpw")
052800000000     D*
052900000000     D* struct passwd *getpwnam(const char *name)
053000000000     D*--------------------------------------------------------------------
053100000000     D getpwnam        PR              *   ExtProc('getpwnam')
053200000000     D   name                          *   Value
053300000000
053400000000     D*--------------------------------------------------------------------
053500000000     D* Get user information by user-id
053600000000     D*
053700000000     D* (Don't let the name mislead you, this does not return the password,
053800000000     D*  the user info database on unix systems is called "passwd",
053900000000     D*  therefore, getting the user info is called "getpw")
054000000000     D*
054100000000     D* struct passwd *getpwuid(uid_t uid)
054200000000     D*--------------------------------------------------------------------
054300000000     D getpwuid        PR              *   extproc('getpwuid')
054400000000     D   uid                         10U 0 Value
054500000000
054600000000     D*--------------------------------------------------------------------
054700000000     D* Get Real User-ID
054800000000     D*
054900000000     D* uid_t getuid(void)
055000000000     D*--------------------------------------------------------------------
055100000000     D getuid          PR            10U 0 ExtProc('getuid')
055200000000
055300000000     D*--------------------------------------------------------------------
055400000000     D* Perform I/O Control Request
055500000000     D*
055600000000     D* int ioctl(int fildes, unsigned long req, ...)
055700000000     D*--------------------------------------------------------------------
055800000000     D ioctl           PR            10I 0 ExtProc('ioctl')
055900000000     D   fildes                      10I 0 Value
056000000000     D   req                         10U 0 Value
056100000000     D   arg                           *   Value
056200000000
056300000000     D*--------------------------------------------------------------------
056400000000     D* Create Link to File
056500000000     D*
056600000000     D* int link(const char *existing, const char *new)
056700000000     D*--------------------------------------------------------------------
056800000000     D link            PR            10I 0 ExtProc('link')
056900000000     D   existing                      *   Value options(*string)
057000000000     D   new                           *   Value options(*string)
057100000000
057200000000     D*--------------------------------------------------------------------
057300000000     D* Set File Read/Write Offset
057400000000     D*
057500000000     D* off_t lseek(int fildes, off_t offset, int whence)
057600000000     D*--------------------------------------------------------------------
057700000000     D lseek           PR            10I 0 ExtProc('lseek')
057800000000     D   fildes                      10I 0 value
057900000000     D   offset                      10I 0 value
058000000000     D   whence                      10I 0 value
058100000000
058200000000     D*--------------------------------------------------------------------
058300000000     D* Get File or Link Information
058400000000     D*
058500000000     D* int lstat(const char *path, struct stat *buf)
058600000000     D*--------------------------------------------------------------------
058700000000     D lstat           PR            10I 0 ExtProc('lstat')
058800000000     D   path                          *   Value options(*string)
058900000000     D   buf                           *   Value
059000000000
059100000000     D*--------------------------------------------------------------------
059200000000     D* Make Directory
059300000000     D*
059400000000     D* int mkdir(const char *path, mode_t mode)
059500000000     D*--------------------------------------------------------------------
059600000000     D mkdir           PR            10I 0 ExtProc('mkdir')
059700000000     D   path                          *   Value options(*string)
059800000000     D   mode                        10U 0 Value
059900000000
060000000000     D*--------------------------------------------------------------------
060100000000     D* Open a File
060200000000     D*
060300000000     D* int open(const char *path, int oflag, . . .);
060400000000     D*--------------------------------------------------------------------
060500000000     D open            PR            10I 0 ExtProc('open')
060600000000     D  filename                       *   value options(*string)
060700000000     D  openflags                    10I 0 value
060800000000     D  mode                         10U 0 value options(*nopass)
060900000000     D  ccsid                        10U 0 value options(*nopass)
061000000000     D  txtcreatid                   10U 0 value options(*nopass)
061100000000
061200000000     D*--------------------------------------------------------------------
061300000000     D* Open a Directory
061400000000     D*
061500000000     D* DIR *opendir(const char *dirname)
061600000000     D*--------------------------------------------------------------------
061700000000     D opendir         PR              *   EXTPROC('opendir')
061800000000     D  dirname                        *   VALUE options(*string)
061900000000
062000000000     D*--------------------------------------------------------------------
062100000000     D* Get configurable path name variables
062200000000     D*
062300000000     D* long pathconf(const char *path, int name)
062400000000     D*--------------------------------------------------------------------
062500000000     D pathconf        PR            10I 0 ExtProc('pathconf')
062600000000     D   path                          *   Value options(*string)
062700000000     D   name                        10I 0 Value
062800000000
062900000000     D*--------------------------------------------------------------------
063000000000     D* Get path name of object from its file id
063100000000     D*
063200000000     D* char *Qp0lGetPathFromFileID(char *buf, size_t size,Qp0lFID_t fileid)
063300000000     D*--------------------------------------------------------------------
063400000000     D GetPathFID      PR              *   ExtProc('Qp0lGetPathFromFileID')
063500000000     D   buf                           *   Value
063600000000     D   size                        10U 0 Value
063700000000     D   fileid                      16A
063800000000
063900000000     D*--------------------------------------------------------------------
064000000000     D* Rename File or Directory, return error if a file/dir under the
064100000000     D*   "new" name already exists.
064200000000     D*
064300000000     D* int Qp0lRenameKeep(const char *old, const char *new)
064400000000     D*--------------------------------------------------------------------
064500000000     D Rename          PR            10I 0 ExtProc('Qp0lRenameKeep')
064600000000     D   old                           *   Value options(*string)
064700000000     D   new                           *   Value options(*string)
064800000000
064900000000     D*--------------------------------------------------------------------
065000000000     D* Rename File or Directory.  If another file/dir exists under the
065100000000     D*    "new" name, delete it first.
065200000000     D*
065300000000     D* int Qp0lRenameUnlink(const char *old, const char *new)
065400000000     D*--------------------------------------------------------------------
065500000000     D Replace         PR            10I 0 ExtProc('Qp0lRenameUnlink')
065600000000     D   old                           *   Value options(*string)
065700000000     D   new                           *   Value options(*string)
065800000000
065900000000     D*--------------------------------------------------------------------
066000000000     D* Read From a File
066100000000     D*
066200000000     D* ssize_t read(int handle, void *buffer, size_t bytes);
066300000000     D*--------------------------------------------------------------------
066400000000     D read            PR            10I 0 ExtProc('read')
066500000000     D  handle                       10i 0 value
066600000000     D  buffer                         *   value
066700000000     D  bytes                        10U 0 value
066800000000
066900000000     D*--------------------------------------------------------------------
067000000000     D* Read Directory Entry
067100000000     D*
067200000000     D* struct dirent *readdir(DIR *dirp)
067300000000     D*--------------------------------------------------------------------
067400000000     D readdir         PR              *   EXTPROC('readdir')
067500000000     D  dirp                           *   VALUE
067600000000
067700000000     D*--------------------------------------------------------------------
067800000000     D* Read Value of Symbolic Link
067900000000     D*
068000000000     D* int readlink(const char *path, char *buf, size_t bufsiz)
068100000000     D*--------------------------------------------------------------------
068200000000     D readlink        PR            10I 0 ExtProc('readlink')
068300000000     D   path                          *   value
068400000000     D   buf                           *   value
068500000000     D   bufsiz                      10U 0 value
068600000000
068700000000     D*--------------------------------------------------------------------
068800000000     D* Reset Directory Stream to Beginning
068900000000     D*
069000000000     D* void rewinddir(DIR *dirp)
069100000000     D*--------------------------------------------------------------------
069200000000     D rewinddir       PR                  ExtProc('rewinddir')
069300000000     D   dirp                          *   value
069400000000
069500000000
069600000000     D*--------------------------------------------------------------------
069700000000     D* Remove Directory
069800000000     D*
069900000000     D* int rmdir(const char *path)
070000000000     D*--------------------------------------------------------------------
070100000000     D rmdir           PR            10I 0 ExtProc('rmdir')
070200000000     D   path                          *   value options(*string)
070300000000
070400000000     D*--------------------------------------------------------------------
070500000000     D* Get File Information
070600000000     D*
070700000000     D* int stat(const char *path, struct stat *buf)
070800000000     D*--------------------------------------------------------------------
070900000000     D stat            PR            10I 0 ExtProc('stat')
071000000000     D   path                          *   value options(*string)
071100000000     D   buf                           *   value
071200000000
071300000000     D*--------------------------------------------------------------------
071400000000     D* Make Symbolic Link
071500000000     D*
071600000000     D* int symlink(const char *pname, const char *slink)
071700000000     D*--------------------------------------------------------------------
071800000000     D symlink         PR            10I 0 ExtProc('symlink')
071900000000     D   pname                         *   value options(*string)
072000000000     D   slink                         *   value options(*string)
072100000000
072200000000     D*--------------------------------------------------------------------
072300000000     D* Get system configuration variables
072400000000     D*
072500000000     D* long sysconf(int name)
072600000000     D*--------------------------------------------------------------------
072700000000     D sysconf         PR            10I 0 ExtProc('sysconf')
072800000000     D   name                        10I 0 Value
072900000000
073000000000     D*--------------------------------------------------------------------
073100000000     D* Set Authorization Mask for Job
073200000000     D*
073300000000     D* mode_t umask(mode_t cmask)
073400000000     D*--------------------------------------------------------------------
073500000000     D umask           PR            10U 0 ExtProc('umask')
073600000000     D   cmask                       10U 0 Value
073700000000
073800000000     D*--------------------------------------------------------------------
073900000000     D* Remove Link to File.  (Deletes Directory Entry for File, and if
074000000000     D*    this was the last link to the file data, the file itself is
074100000000     D*    also deleted)
074200000000     D*
074300000000     D* int unlink(const char *path)
074400000000     D*--------------------------------------------------------------------
074500000000     D unlink          PR            10I 0 ExtProc('unlink')
074600000000     D   path                          *   Value options(*string)
074700000000
074800000000     D*--------------------------------------------------------------------
074900000000     D* Set File Access & Modification Times
075000000000     D*
075100000000     D* int utime(const char *path, const struct utimbuf *times)
075200000000     D*--------------------------------------------------------------------
075300000000     D utime           PR            10I 0 ExtProc('utime')
075400000000     D   path                          *   value options(*string)
075500000000     D   times                         *   value
075600000000
075700000000     D*--------------------------------------------------------------------
075800000000     D* Write to a file
075900000000     D*
076000000000     D* ssize_t write(int fildes, const void *buf, size_t bytes)
076100000000     D*--------------------------------------------------------------------
076200000000     D write           PR            10I 0 ExtProc('write')
076300000000     D  handle                       10I 0 value
076400000000     D  buffer                         *   value
076500000000     D  bytes                        10U 0 value
