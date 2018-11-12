<html>
    <head>
        <meta http-equiv="cache-control" content="no-cache">
        <meta name="description" content="iSphere Plug-in Update Site">
        <meta name="author" content="iSphere Project Team">
        <meta name="keywords" content="iSphere, Plugin, RDP, RDI, WDSCI, Eclipse, AS400">
        <link href="assets/stylesheet.css" rel="stylesheet" type="text/css" />
        <title>iSphere Plug-in</title>
    </head>
    <body>
        <?php
            function parseJarManifest($manifestFileContents) {
               $manifest = array();	
               $lines = explode("\n", $manifestFileContents);
               foreach ($lines as $line) {
                  if (preg_match("/^([^:]+):\s*(.+)$/", $line, $m)) {
                     $manifest[$m[1]] = trim($m[2]);
                  }
               }
               return $manifest;
            }
            $manifestFileContents = file_get_contents('http://sourceforge.net/p/isphere/code/HEAD/tree/trunk/build/iSphere%20Notifier/MANIFEST.MF?format=raw');
            $manifest = parseJarManifest($manifestFileContents);
            $current_version = $manifest['Bundle-Version'];
        ?>
      
        <table width="100%" border="0">
        <tr><td align="left" >
        <a href="https://sourceforge.net/projects/isphere/"><img src="assets/isphere.gif" alt="iSphere Plug-in" width="122" height="79" border="0" style="padding-right: 10px;"/></a>
        </td>
        <td align="left" width="100%" >
        <h1>iSphere Plug-in</h1>
        <p>Hi, this is the support page of the iSphere plug-in for RDi and WDSCi.</p>
        <p>Visit the <a target="_help" href="http://isphere.sourceforge.net/help/">iSphere help</a> site to find out more about the plug-in.</p>
        </td>
        <td>
        
        <table>
        <tr>
        <td align="left">
        <img src="assets/isphere_support.png" alt="iSphere Plug-in" border="0" style="padding-right: 10px;"/>
        </td>
        </tr>
        <tr>
        <td valign="bottom" align="right" nowrap>
        <b>Version: <?php echo $current_version; ?></b>
        </td>
        </tr>
        </table>
        
        </td>
        </tr>
        </table>
        
        <h2 class="release">Getting Support</h2>
        <div class="section">
        When you are in doubt whether or not something is wrong with iSphere, please, start with the iSphere help and 
        check for remarks regarding your concerns or ask your questions at the 
        <a target="_wdsci-l" href="http://lists.midrange.com/mailman/listinfo/wdsci-l">WDSCI-L</a> mailing list. In
        case you are sure that you spotted a bug, add a bug report at the 
        <a target="_isphere-bugs" href="https://sourceforge.net/p/isphere/tickets/">iSphere bug tracker</a>.
        <p>
        <table>
        <tr><td>Visit the iSphere <a target="_help" href="http://isphere.sourceforge.net/help/">help</a> page.</td></tr>
        <tr><td>Ask your questions at the <a target="_wdsci-l" href="http://lists.midrange.com/mailman/listinfo/wdsci-l">WDSCI-L</a> mailing list at <a target="_wdsci-l" href="http://www.midrange.com">midrange.com</a>.</td></tr>
        <tr><td>For bug reports open a ticket at the <a href="https://sourceforge.net/p/isphere/tickets/">iSphere bug tracker.</a></td></tr>
        </table>
        </div>
           
		<h2 class="release">Articles About iSphere</h2>
        <div class="section">
		<table class="nomargin">
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"></td><td><a target="_article1" href="http://www.ibmsystemsmag.com/ibmi/developer/rpg/rse_goodies/">New Free RSE Goodies by Jon Paris and Susan Gantner (March 2013)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article2" href="http://www.ibmsystemsmag.com/ibmi/developer/rpg/isphere-details/?utm_campaign=ibm-enews&utm_medium=email&utm_source=ibmi-sep17-2014&utm_content=exclusive3-headline">A Closer Look at iSphere by Jon Paris and Susan Gantner (September 2014)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article3" href="http://www.itjungle.com/fhs/fhs092314-story02.html">iSphere : A Free and Functional Plugin by Alex Woodie (September 23, 2014)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article4" href="http://www.mcpressonline.com/dev-tools/techtip-rational-developer-for-i-rdi-and-isphere-the-missing-link.html">Rational Developer for i (RDi) and iSphere: The Missing Link by Frank Hildebrandt (November 7, 2014)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article5" href="http://www.itjungle.com/fhg/fhg061615-story01.html">iSphere Plug-in Expands RSE/RDi Toolset (June 25, 2015)</a></td></tr>
		</table>
		</div>
        <p/>
		
		<h2 class="release">Trademarks</h2>
        <div class="section">
		The following terms are trademarks of the IBM Corporation in the United States or other countries or both:
		<ul>
		<li>Websphere Development Studio Client for iSeries 7.0</li>
		<li>IBM Rational Developer for i 8.0+</li>
		</ul>
		</div>
        <p/>
		
        <br>
        <hr>
        <table border="0" class="copyright">
        <tr><td class="copyright" align="left" width="50%">Version: <?php echo $current_version; ?> - Copyright: 2016, iSphere project owners</td><td class="copyright" align="right" width="50%">Updated: @TODAY@</td></tr>
        </table>
        <br>
    </body>
</html>