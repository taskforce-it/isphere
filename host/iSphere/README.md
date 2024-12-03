# iSphere

This folder is an IBM *i Project* representing the iSphere host library.

## Building the Library

Before you can build the library you have to clone the iSphere project
to your PC and import the *iSphere* folder as an IBM *i Project* into
your *IBM Rational Developer for i*. Alternatively, you can download
the project as a zip file from the main project page.

Afterwards you need to push the source code to your IBM i into library
`ISPHEREDVP` and create and execute the `BUILD` command.

Use the following commands for compiling the `BUILD` command:

```commandLine
CRTPNLGRP PNLGRP(ISPHEREDVP/BUILD_HLP) SRCFILE(ISPHEREDVP/QBUILD)
  SRCMBR(BUILD_HLP)

CRTCMD CMD(ISPHEREDVP/BUILD) PGM(*REXX) SRCFILE(ISPHEREDVP/QBUILD)
  SRCMBR(BUILD) REXSRCFILE(QBUILD) REXSRCMBR(BUILD1)
  REXCMDENV(*COMMAND) HLPPNLGRP(BUILD_HLP) HLPID(BUILD)
```

Build the `ISPHERE` library like this:

```commandLine
ADDLIBLE LIB(ISPHEREDVP)
BUILD
```

No parameters are required. All parameters have been set to their
correct default values.

---

A compiled version of the library is available in the `/Server` directory of
the *local update site* zip file that you can download from the iSphere
[Local Update Sites](https://rdi-open-source.github.io/isphere/files/) page.

---
