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
            $notifier_url = str_replace( ' ' , '%20' , '@VERSION_MANIFEST@' );
            $manifestFileContents = file_get_contents( $notifier_url );
            $manifest = parseJarManifest($manifestFileContents);
            $current_version = $manifest['Bundle-Version'];
            $current_version = '@VERSION_NUMBER@';
        ?>
      
        <table width="100%" border="0">
        <tr><td align="left" >
        <a href="https://sourceforge.net/projects/isphere/"><img src="assets/isphere.gif" alt="iSphere Plug-in" width="122" height="79" border="0" style="padding-right: 10px;"/></a>
        </td>
        <td align="left" width="100%" >
        <h1>iSphere Plug-in</h1>
        <p>Hi, this is the home of the iSphere plug-in for RDi and WDSCi.</p>
        <p>Visit the <a target="_help" href="http://isphere.sourceforge.net/help/">iSphere help</a> site to find out more about the plug-in.</p>
        </td>
        <td valign="bottom" align="right" nowrap>
        <b>Version: <?php echo $current_version; ?></b>
        </td>
        </tr>
        </table>
        
        <h2 class="release">Introduction</h2>
        <div class="section">
        iSphere is an open source plug-in for WDSCi 7.0 and RDi 8.0+. It delivers high quality extensions 
        for WDSC, RDP and RDi to further improve developer productivity. The current version is <?php echo $current_version; ?>.
		<p/>
		IBM's current Eclipse based Integrated Development Environment (IDE) is a huge step beyond SEU, but 
		it still lacks features available only on the green screen. That is where the iSphere Project comes 
		into play, filling in those gaps.
		<p/>
		iSphere features are driven from our ideas and needs, but everybody is encouraged to contribute 
		suggestions and manpower to improve the power of iSphere.
		<p/>
		<table  border="0">
		<tr><td valign="top">
			<table border="0" style="border-spacing: 0px 0px; ">
			<tr><td valign="top"><h2>The iSphere Project Owners</h2></td></tr>
			<tr><td nowrap><a target="_owner" href="http://www.taskforce-it.de/"><img class="noborder" src="./assets/task-force.jpg" ></a><br>Task Force IT-Consulting GmbH, Frank Hildebrandt</td>
				<td width="60px"></td>
			    <td nowrap><a target="_owner" href="http://www.tools400.de/"><img class="noborder" src="./assets/tools400.png" ></a><br>Tools/400, Thomas Raddatz</td></tr>
	        </table>
		</td>
		<td width="60px"></td>
		<td valign="top">
			<table border="0" style="border-spacing: 0px 0px; ">
			<tr><td valign="top"><h2>Contributors</h2></td></tr>
			<tr><td height="15px"></td></tr>
			<tr><td nowrap>Peter Colpaert</td><td>-</td><td nowrap>Dutch Translation</td></tr>
			<tr><td height="20px"></td></tr>
	        <tr><td nowrap>Nicola Brion</td><td>-</td><td nowrap>Italian Translation</td></tr>
			<tr><td height="20px"></td></tr>
	        <tr><td nowrap>Sam Lennon</td><td>-</td><td nowrap>Documentation</td></tr>
			<tr><td height="20px"></td></tr>
	        <tr><td nowrap>Buck Calabro</td><td>-</td><td nowrap>Documentation</td></tr>
	        </table>
		</td></tr>
        </table>
		</div>
        <p/>
        
        <h2 class="release">Features</h2>
        <div class="section">
        Click to enlarge:
        <table class="nomargin">
        <tr><td><a href="./assets/isphere_screenshot_1.png"><img class="noborder" src="./assets/isphere_screenshot_1_preview.png"></a><br>Objects, spooled files, TODOs</td>
            <td><a href="./assets/isphere_screenshot_2.png"><img class="noborder" src="./assets/isphere_screenshot_2_preview.png"></a><br>Extended search</td>
            <td><a href="./assets/isphere_screenshot_3.png"><img class="noborder" src="./assets/isphere_screenshot_3_preview.png"></a><br>Message file editor</td>
            <td><a href="./assets/isphere_screenshot_4.png"><img class="noborder" src="./assets/isphere_screenshot_4_preview.png"></a><br>Message file compare</td>
            <td><a href="./assets/isphere_screenshot_5.png"><img class="noborder" src="./assets/isphere_screenshot_5_preview.png"></a><br>Data area editor/monitor</td>
            <td><a href="./assets/isphere_screenshot_6.png"><img class="noborder" src="./assets/isphere_screenshot_6_preview.png"></a><br>Job log explorer</td>
        </tr>
        </table>
        <ul>
        <li>Message File Editor for editing message descriptions within a message file.</li>
        <li>Binding Directory Editor for editing binding directory entries within a binding directory.</li>
        <li>Compare/Merge Editor for comparing/merging source physical file members.</li>
        <li>An extremely fast source file search. (Up to 60 times faster than the original RSE Search.)</li>
        <li>An extremely fast message file search.</li>
        <li>A spooled file subsystem that can open spooled files in Text, HTML and PDF format.</li>
        <li>LPEX Task Tags for marking positions in your source like TODO, FIXME, etc.</li>
        <li>Decorators (object descriptions) for objects in the RSE tree view</li>
        <li>RSE Filter Management to export/import some or all filters to/from a repository</li>
        <li>Data Area Editor</li>
        <li>User Space Editor</li>
        <li>Data Area Monitor/Viewer</li>
        <li>User Space Monitor/Viewer</li>
        <li>Data Queue Monitor/Viewer</li>
        <li>Message Subsystem/Message Monitor</li>
        </ul>
        </div>     
        <p/>
        
        <h2 class="release">Installation</h2>
        <div class="section">
        The easiest way to install the iSphere plug-in is using the official update sites:
        <p/>
        <table class="nomargin">
        <tr><td><img class="noborder" src="./assets/updatesite.png"></td><td><a href="http://isphere.sourceforge.net/eclipse/rdi8.0/">IBM Rational Developer for i - RDi 8.0+</a></td></tr>
        <tr><td><img class="noborder" src="./assets/updatesite.png"></td><td><a href="http://isphere.sourceforge.net/eclipse/wdsci7.0/">Websphere Development Studio Client for iSeries - WDSCi 7.0</a></td></tr>
        </table>
        <p/>
        Refer to the iSphere <a target="_help" href="http://isphere.sourceforge.net/help/">help</a> page for detailed installation instructions.
        <p/>
        The iSphere beta versions are available here:
        <p/>
        <table class="nomargin">
        <tr><td><img class="noborder" src="./assets/updatesite_beta.png"></td><td><a href="http://isphere.sourceforge.net/beta-version/eclipse/rdi8.0/">IBM Rational Developer for i - RDi 8.0+ (beta)</a></td></tr>
        <tr><td><img class="noborder" src="./assets/updatesite_beta.png"></td><td><a href="http://isphere.sourceforge.net/beta-version/eclipse/wdsci7.0/">Websphere Development Studio Client for iSeries - WDSCi 7.0 (beta)</a></td></tr>
        </table>
        <p/>
        Refer to the iSphere <a target="_help" href="http://isphere.sourceforge.net/beta-version/help/">help (beta version)</a> page for detailed installation instructions.
        </div>
        
        <h2 class="release">Help</h2>
        <div class="section">
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
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article5" href="http://www.itjungle.com/fhg/fhg061615-story01.html">iSphere Plug-in Expands RSE/RDi Toolset by Susan Gantner (June 25, 2015)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article6" href="http://www.itjungle.com/fhg/fhg070715-story01.html">Looking For Stuff With iSphere by Susan Gantner (July 7, 2015)</a></td></tr>
		<tr><td width="0px"><img class="noborder" align="center" src="./assets/newspaper.png"><td><a target="_article7" href="http://www.itjungle.com/fhg/fhg092915-story03.html">More iSphere Goodies by Susan Gantner (September 29, 2015)</a></td></tr>
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
        <tr><td class="copyright" align="left" width="50%">Version: <?php echo $current_version; ?> - Copyright: @YEAR@, iSphere project owners</td><td class="copyright" align="right" width="50%">Updated: @TODAY@</td></tr>
        </table>
        <br>
    </body>
</html>