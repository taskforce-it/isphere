      // ===============================================================
      //  Usage information
      //
      //  Include a file information data structure for DB files:
      //
      //    /define fileInfDS_DB
      //    /copy INFDS
      //
      //  Include a file information data structure for display files:
      //
      //    /define fileInfDS_dspF
      //    /copy INFDS
      //
      //  Include a file information data structure for printer files:
      //
      //    /define fileInfDS_prtF
      //    /copy INFDS
      //
      //
      //  Overview of the data structure:
      //
      //    infDS_name.*
      //    infDS_name.openFB.*
      //    infDS_name.openFB.qFile.*
      //    infDS_name.IO.*
      //    infDS_name.deviceFB.*
      //
      // ===============================================================
      /if defined(fileInfDS_DB)
      /if defined(infDS_DB_t)
      /eof
      /endif
      /define infDS_DB_t
      /endif

      /if defined(fileInfDS_prtF)
      /if defined(infDS_prtF_t)
      /eof
      /endif
      /define infDS_prtF_t
      /endif

      /if defined(fileInfDS_dspF)
      /if defined(infDS_dspF_t)
      /eof
      /endif
      /define infDS_dspF_t
      /endif

      // ===============================================================
      //  File information data structure
      // ===============================================================
      /if defined(fileInfDS_DB)
     D infDS_DB_t      DS                  qualified               based(pDummy)
      /elseif defined(fileInfDS_PRTF)
     D infDS_prtF_t    DS                  qualified               based(pDummy)
      /elseif defined(fileInfDS_DSPF)
     D infDS_dspF_t    DS                  qualified               based(pDummy)
      /endif
      // Reserved 01
     D  reserved_01            1      8A
      // Open indication 1=open
     D  isOpen                 9      9A
      // End of file 1=EOF
     D  isEOF                 10     10A
      // Status
     D  status                11     15S 0
      // Operation code, first 5 pos.
     D  opCode                16     20A
      // IO-Type:
      //   F=The last operation was specified for a file name
      //   R=The last operation was specified for a record
      //   I=The last operation was an implicit file operation.
     D  IO_type               21     21A
      // Routine of operation code
     D  routine               22     29A
      // RPG source listing line nbr
     D  srcSeq                30     37A
      // Record format
     D  rcdFormat             38     45A
      // System message number
     D  sysMsgNbr             46     52A
      // Reserved 02
     D  reserved_02           53     66A

      // Valid after post:
      //    Screen size (product of the number of rows
      //    and the number of columns on the device screen).
     D  screenSize            67     70S 0
      //    The display's keyboard type.
      //       00 = alphanumeric or katakana
      //       10 = ideographic
     D  kbdType               71     72S 0
      //    The display type.
      //       00 = alphanumeric or katakana
      //       10 = ideographic
      //       20 = DBCS
     D  displayType           73     74S 0
      //    Always set to 00.
     D  mode                  75     76S 0
      // Reserved_03
     D  reserved_03           77     80A

      //  -----------------------------------------
      //    Open Feedback            81 - 240
      //  -----------------------------------------
     D  openFB                             likeds(openFeedback_t)

      //  -----------------------------------------
      //    Input/Output Feedback      241 - 366
      //  -----------------------------------------
     D  IO_FB                              likeds(IO_feedback_t )

      //  -----------------------------------------
      //    Device Specific Feedback   367 -
      //  -----------------------------------------
      /if defined(fileInfDS_DB)
     D  deviceFB...
     D                                     likeds(deviceFeedback_DB_t)
      /elseif defined(fileInfDS_PRTF)
     D  deviceFB...
     D                                     likeds(deviceFeedback_PrtF_t)
      /elseif defined(fileInfDS_DSPF)
     D  deviceFB...
     D                                     likeds(deviceFeedback_DspF_t)
      /endif

      /IF NOT DEFINED(openFeedback_t)
      /define openFeedback_t
      //  -----------------------------------------
      //    Open Feedback
      //  -----------------------------------------
     D openFeedback_t...
     D                 DS           160    qualified               based(pDummy)
      // DS=Display DB=File SP=Spooled File
     D  ODP_type               1      2A
      // Qualified file name
     D  qFile                              likeds(infds_qFile_t)
      // File name
     D  file                   3     12A
      // Library
     D  lib                   13     22A
      // Spool file name
     D  spoolFile             23     32A
      //
     D  spoolLib              33     42A
      //
     D  spoolNbr              43     44I 0
      //
     D  maxRcdLength          45     46I 0
      // Key length
     D  maxKeyLength          47     48I 0
      // Member
     D  mbr                   49     58A
      // Reserved
     D  reserved_01           59     62I 0
      // Reserved
     D  reserved_02           63     66I 0
      // File type
      //     1  =  Display
      //     2  =  Printer
      //     4  =  Diskette
      //     5  =  Tape
      //     9  =  Save
      //    10  =  DDM
      //    11  =  ICF
      //    20  =  Inline data
      //    21  =  Database
     D  fileType              67     68I 0
      // Reserved
     D  reserved_03           69     71A
      // Number of lines on a display screen or       (Display, printer)
      // number of lines on a printed page.
     D  rows                  72     73I 0
      // Length of the null field byte map.           (Database)
     D  lenNullFldMap         72     73I 0
      // Number of positions on a display screen or   (Display, printer)
      // number of characters on a printed line.
     D  columns               74     75I 0
      // Length of the null key field byte map.       (Database)
     D  lenKeyFldMap          74     75I 0
      // Number of records in the member at open
      // at open time.
     D numRcdsAtOpen          76     79I 0
      // Access type
     D accessType             80     81A
      // Duplicate key
     D isDupKey               82     82A
      // Source file
     D isSrcFile              83     83A
      // Reserved
     D reserved_04            84     93A
      // Reserved
     D reserved_05            94    103A
      // Offset to volume label fields of open
      // feedback area.
     D ofsVolLblFld          104    105I 0
      // Max rcds in blk
     D maxBlkRcds            106    107I 0
      // Overflow line
     D overflow              108    109I 0
      // Blk increment
     D blkInc                110    111I 0
      // Reserved
     D reserved_06           112    115A
      // Miscellaneous flags
     D flags1                116    116A
      // Requester name
     D requester             117    126A
      // Open count
     D openCount             127    128I 0
      // Reserved
     D reserved_07           129    130I 0
      // Num based mbrs
     D numBasedMbrs          131    132I 0
      // Miscellaneous flags
     D flags2                133    133A
      // Open identifier
     D openID                134    135A
      // Max rcd fmt length
     D maxRcdFmtLen          136    137I 0
      // Database CCSID
     D CCSID                 138    139I 0
      // Miscellaneous flags
     D flags3                140    140A
      // Reserved
     D reserved_08           141    146A
      // Num devs defined
     D numDevices            147    148I 0
      // Device name definition list
     D devices               149    160A

     D infds_qFile_t...
     D                 DS                  qualified               based(pDummy)
     D  name                   1     10A
     D  lib                   11     20A

      /endif
      /IF NOT DEFINED(IO_feedback_t)
      /define IO_feedback_t
      //  -----------------------------------------
      //    Input/Output Feedback
      //  -----------------------------------------
     D IO_feedback_t...
     D                 DS           126    qualified               based(pDummy)
      // Offset to file dependant feedback
     D ofsFileDepFB            1      2I 0
      // Write count
     D writeCount              3      6I 0
      // Read count
     D readCount               7     10I 0
      // Write/read count
     D writeReadCount         11     14I 0
      // Other I/O count
     D otherCount             15     18I 0
      // Reserved
     D reserved_01            19     19A
      // Current operation
      //   hex 01 =  Read or read block or read from invited devices
      //   hex 02 =  Read direct
      //   hex 03 =  Read by key
      //   hex 05 =  Write or write block
      //   hex 06 =  Write-read
      //   hex 07 =  Update
      //   hex 08 =  Delete
      //   hex 09 =  Force-end-of-data
      //   hex 0A =  Force-end-of-volume
      //   hex 0D =  Release record lock
      //   hex 0E =  Change end-of-data
      //   hex 0F =  Put deleted record
      //   hex 11 =  Release device
      //   hex 12 =  Acquire device
     D operation              20     20A
      // Rcd format name
     D IO_rcdFmt              21     30A
      // Device class
     D deviceClass            31     32A
      // Pgm device name
     D IO_pgmDevice           33     42A
      // Rcd len of I/O
     D IO_rcdLength           43     46I 0
      // Reserved
     D reserved_02            47    126A

      /endif
      /IF NOT DEFINED(deviceFeedback_DB_t)
      /define deviceFeedback_DB_t
      //  -----------------------------------------
      //    Device Specific Feedback of
      //    Database Files.
      //  -----------------------------------------
     D deviceFeedback_DB_t...
     D                 DS                  qualified               based(pDummy)
      // Size of feedback information
     D  size                   1      4I 0
      // Join logical file bits
     D  JFILE_bits             5      8I 0
      // Offset from the beginning of the I/O feedback area
      // for database files to the null key field byte map.
     D  ofsNullKeyFldMap...
     D                         9     10I 0
      // Number of locked records
     D  numLckRcds            11     12I 0
      // Maximum number of fields
     D  maxNumFlds            13     14I 0
      // Offset to the field-mapping error-bit map.
     D  ofsFldMapErrBitMap...
     D                        15     18I 0
      // Current file position indication.
     D                        19     19A
      // Current record deleted indication
     D                        20     20A
      // Number of key fields
     D  nbrOfKeyFlds          21     22I 0
      // Reserved
     D  resreved_1            23     26A
      // Key length
     D  keyLength             27     28I 0
      // Data member number
     D  dtaMbrNum             29     30I 0
      // Relative record number in data member
     D  rcdNbr                31     34U 0
      // Key value                      *
      // Null key field byte map        *

      /endif
      /IF NOT DEFINED(deviceFeedback_PrtF_t)
      /define deviceFeedback_PrtF_t
      //  -----------------------------------------
      //    Device Specific Feedback of
      //    Printer Files.
      //  -----------------------------------------
     D deviceFeedback_PrtF_t...
     D                 DS                  qualified               based(pDummy)
      // Current line number in a page
     D  curLine                1      2I 0
      // Current page count
     D  curPage                3      6I 0
      // Spooled file bits
     D  splF_bits              7      7A
      // Reserved
     D  reserved_1             8     34A
      // Major return code
     D  majorRtnCode          35     36A
      // Minor return code
     D  minorRtnCode          37     38A

      /endif
      /IF NOT DEFINED(deviceFeedback_DspF_t)
      /define deviceFeedback_DspF_t
      //  -----------------------------------------
      //    Device Specific Feedback of
      //    Display Files.
      //  -----------------------------------------
     D deviceFeedback_DspF_t...
     D                 DS                  qualified               based(pDummy)
      // Flag bits
     D  flag_bits              1      2A
      // AID byte
     D  aid_byte               3      3A
      // Cursor location (line and position)
     D  cursorPos              4      5U 0
      // Actual data length
     D  dataLength             6      9I 0
      //
     D  rrn                   10     11I 0
      // Lowest subfile
     D  rrn_lowest            12     13I 0
      // Total number of records in a subfile
     D  numRcds               14     15I 0
      // Cursor location (line and position) within active window
     D  cursorPosWdw          16     17U 0
      // Reserved
     D  reserved_1            18     34A
      // Major return code
     D  majorRtnCode          35     36A
      // Minor return code
     D  minorRtnCode          37     38A
      // Systems Network Architecture (SNA) sense return code              (ICF only)
     D  snaCode               39     46A
      // Safe indicator                                                    (ICF only)
     D  saveInd               47     47A
      // Reserved
     D  reserved_2            48     48A
      // Request Write (RQSWRT) command from remote system/application     (ICF only)
     D  rqsWrt                49     49A
      // Record format name received from the remote system                (ICF only)
     D  rcdFormat             50     59A
      // Reserved
     D  reserved_3            60     63A
      // Mode name                                                         (ICF only)
     D  mode                  64     71A
      // Reserved
     D  reserved_4            72     80A

      /endif
