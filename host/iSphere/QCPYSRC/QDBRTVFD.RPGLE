      /if not defined(QDBRTVFD)
      /define QDBRTVFD
      *
      *  Retrieve Database File Description (QDBRTVFD) API
     d QDBRTVFD...
     d                 pr                  extpgm('QDBRTVFD')
     d  o_rcvVar                  65535a         options(*varsize)
     d  i_lenRcvVar                  10i 0 const
     d  o_retFileName                20A
     d  i_format                      8a   const
     d  i_qFile                      20A   const
     d  i_rcdFormat                  10a   const
     d  i_override                    1a   const
     d  i_system                     10a   const
     d  i_formatType                 10a   const
     d  io_errCode                65535a          options(*varsize )
      *
      * ---------------------------------------------------
      *  FILD0100:
      * ---------------------------------------------------
      *
      *  File Definition Header (Qdb_Qdbfh)
     d Qdb_Qdbfh_t     ds                  qualified               based(pDummy)
     d  Qdbfyret               1      4I 0
     d  Qdbfyavl               5      8I 0
     d  Qdbfhflg               9     10U 0
     d  Reserved_7            11     14A
     d  Qdbflbnum             15     16I 0
     d  Qdbfknum              17     18I 0
     d  Qdbfkmxl              19     20I 0
     d  Qdbfkflg              21     21A
     d  Qdbfkfdm              22     22A
     d  Reserved_10           23     30A
     d  Qdbfhaut              31     40A
     d  Qdbfhupl              41     41A
     d  Qdbfhmxm              42     43I 0
     d  Qdbfwtfi              44     45I 0
     d  Qdbfhfrt              46     47I 0
     d  Qdbfhmnum             48     49I 0
     d  Qdbfapsize            50     51I 0
     d  Reserved_11           52     58A
     d  Qdbfbrwt              59     60I 0
     d  Qaaf                  61     61A
     d  Qdbffmtnum            62     63I 0
     d  Qdbfhfl2              64     65A
     d  Qdbfvrm               66     67I 0
     d  Qaaf2                 68     69A
     d  Qdbfhcrt              70     82A
     d  Reserved_18           83     84A
     d  Qdbfhtxt              85    134A
     d  Reserved_19          135    147A
     d  Qdbfsrcf             148    157A
     d  Qdbfsrcm             158    167A
     d  Qdbfsrcl             168    177A
     d  Qdbfkrcv             178    178A
     d  Reserved_20          179    201A
     d  Qdbftcid             202    203I 0
     d  Qdbfasp              204    205I 0
     d  Qdbfnbit             206    206A
     d  Qdbfmxfnum           207    208I 0
     d  Reserved_22          209    284A
     d  Qdbfodic             285    288I 0
     d  Reserved_23          289    302A
     d  Qdbffigl             303    304I 0
     d  Qdbfmxrl             305    306I 0
     d  Reserved_24          307    314A
     d  Qdbfgkct             315    316I 0
     d  Qdbfos               317    320I 0
     d  Reserved_25          321    328A
     d  Qdbfocs              329    332I 0
     d  Reserved_26          333    336A
     d  Qdbfpact             337    338A
     d  Qdbfhrls             339    344A
     d  Reserved_27          345    364A
     d  Qdbpfof              365    368I 0
     d  Qdblfof              369    372I 0
     d  Qdbfnlsb             373    373A
     d  Qdbflang             374    376A
     d  Qdbfcnty             377    378A
     d  Qdbfjorn             379    382I 0
     d  Qdbfevid             383    386I 0
     d  Reserved_28          387    400A
      *
      *  File Scope Array (Qdb_Qdbfb)
     d Qdb_Qdbfb_t     ds                  qualified               based(pDummy)
     d  Reserved_48            1     48A
     d  Qdbfbf                49     58A
     d  Qdbfbfl               59     68A
     d  Qdbft                 69     78A
     d  Reserved_49           79    115A
     d  Qdbfbgky             116    117I 0
     d  Reserved_50          118    119A
     d  Qdbfblky             120    121I 0
     d  Reserved_51          122    123A
     d  Qdbffogl             124    125I 0
     d  Reserved_52          126    128A
     d  Qdbfsoon             129    130I 0
     d  Qdbfsoof             131    134I 0
     d  Qdbfksof             135    138I 0
     d  Qdbfkyct             139    140I 0
     d  Qdbfgenf             141    142I 0
     d  Qdbfodis             143    146I 0
     d  Reserved_53          146    160A
      *
      *  Key Specification Array (Qdb_Qdbfk)
     d Qdb_Qdbfk_t     ds                  qualified               based(pDummy)
     d  Qdbfkfld               1     10A
     d  Reserved_59           11     13A
     d  Qdbfksq               14     14A
     d  Reserved_61           15     32A
      *
      * ---------------------------------------------------
      *  FILD0200
      * ---------------------------------------------------
      *
      *  Format Definition Header (Qdb_Qddfmt)
     d Qdb_Qddfmt_t    ds                  qualified               based(pDummy)
     d  bytRet                 1      4I 0
     d  bytAvl                 5      8I 0
     d  Reserved_62            9     32A
     d  Qddfmtf               33     33A
     d  Qddfxlto              34     37I 0
     d  Qddfrcao              38     41I 0
     d  Qddfdico              42     45I 0
     d  Qddfrcid              46     47I 0
     d  Qddfsrcd              48     49I 0
     d  Qddfrtcd              50     51I 0
     d  Qddfrlcd              52     53I 0
     d  Reserved_64           54     60A
     d  Qddftflgs             61     61A
     d  Qddflgs               62     62U 0
     d  Reserved_67           63     66A
     d  Qddfrlen              67     70I 0
     d  Qddfname              71     80A
     d  Qddfseq               81     93A
     d  Qddftext              94    143A
     d  Qddffldnum           144    145I 0
     d  Qddf_Identity_Off...
     d                       146    149I 0
      * Qddffldx                    CHAR(*)   Start of field definition array (Qdb_Qddffld)
     d  Qddffldx             257    512A
      *
      *  Field Header (Qdb_Qddffld)
     d Qdb_Qddffld_t   ds                  qualified               based(pDummy)
     d  Qddfdefl               1      4I 0
     d  Qddffldi               5     34A
     d  Qddfflde              35     64A
     d  Qddfftyp              65     66A
     d  Qddffiob              67     67A
     d  Qddffobo              68     71I 0
     d  Qddffibo              72     75I 0
     d  Qddffldb              76     77I 0
     d  Qddffldd              78     79I 0
     d  Qddffldp              80     81I 0
      *  ...
     d  Qddffldst2            86     86U 0
      *  ...
     d  Qddflalc              92     93U 0
     d  Qddfdttf              94     94A
     d  Qddfdtts              95     95A
     d  Qddfcsid              96     97U 0
      *  ...
     d  Qddfdftd             195    198U 0
      *  ...
     d  Qddftxtd             209    212I 0
     d  Reserved_102         213    214A
     d  Qddfrefd             215    218I 0
     d  Qddfedtl             219    220I 0
     d  Qddfedtd             221    224I 0
     d  Reserved_76          225    226I 0
     d  Qddfchd              227    230I 0
      *  ...
     d  Qddfxals             237    240I 0
      *
      *  Reference Information (Qdb_Qddfrefi)
     d Qdb_Qddfrefi_t  ds                  qualified               based(pDummy)
     d  Qddfrcde               1      1A
     d  Qddfrfil               2     11A
     d  Qddfrlib              12     21A
     d  Qddfrfmt              22     31A
     d  Qddfrfld              32     61A
     d  Reserved_78           62     80A
      *
      *  Edit Code/Edit Word Information (Qdb_Qddfedcw)
     d Qdb_Qddfedcw_t  ds                  qualified               based(pDummy)
     d  Qddfecdi               1      2A
     d  Qddfecde               1      1A
     d  Qddfecdx               2      2A
     d  Reserved_79            3     16A
     d  Qddfewd               17     80A
      *
      *  Field Text (Qdb_Qddfftxt)
     d Qdb_Qddfftxt_t  ds                  qualified               based(pDummy)
     d  Qddfftxt               1     50A
      *
      *  Alias name structure (Qdb_Qddfalis)
     d Qdb_Qddfalis_t  ds                  qualified               based(pDummy)
     d  Qddfalsl               1      2I 0
     d  Reserved_85            3     16A
     d  Qddfalsn              17    274A
      *
      *  Default Value Description Information (Qdb_Qddfdft)
     d Qdb_Qddfdft_t   ds                  qualified               based(pDummy)
     d  Qddfdftl               1      2I 0
     d  Qddfdfta               3      3U 0
     d  Reserved_87            4     16A
     d  Qddfdftv              17  32740A
      *
     d QDDFDFTA_GRAPHIC...
     d                 c                   128                                  DBCS/graphic d
     d QDDFDFTA_HEX...
     d                 c                   32                                   Hex default.
     d QDDFDFTA_NULL...
     d                 c                   4                                    Null default.
     d QDDFDFTA_DATE_TIME...
     d                 c                   2                                    Current date/t
     d QDDFDFTA_DFT_KEYWORD...
     d                 c                   1                                    DFT/DFTVAL spe
      *
      *  Column Heading Information (Qdb_Qddfcolh)
     d Qdb_Qddfcolh_t  ds                  qualified               based(pDummy)
     d  Qddfch1                1     20A
     d  Qddfch2               21     40A
     d  Qddfch3               41     60A
      *
      /endif
