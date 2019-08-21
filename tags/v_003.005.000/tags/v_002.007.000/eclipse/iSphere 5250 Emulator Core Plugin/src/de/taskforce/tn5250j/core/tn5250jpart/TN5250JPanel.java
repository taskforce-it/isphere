// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2, or (at your option)
// any later version.
// 
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with this software; see the file COPYING.  If not, write to
// the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
// Boston, MA 02111-1307 USA

package de.taskforce.tn5250j.core.tn5250jpart;

import java.util.*;

import org.eclipse.swt.widgets.Shell;
import org.jasypt.exceptions.EncryptionOperationNotPossibleException;
import org.jasypt.util.text.BasicTextEncryptor;
import org.tn5250j.Session5250;
import org.tn5250j.SessionPanel;
import org.tn5250j.TN5250jConstants;
import org.tn5250j.tools.LangTool;
import org.tn5250j.gui.TN5250jSecurityAccessDialog;
import org.tn5250j.event.ScreenListener;
import org.tn5250j.framework.common.*;

import de.taskforce.tn5250j.core.TN5250JCorePlugin;
import de.taskforce.tn5250j.core.session.Session;

public abstract class TN5250JPanel implements TN5250jConstants, ScreenListener {
	
	private static final long serialVersionUID = 1L;
	private TN5250JInfo tn5250jInfo;
	private Session session;
	private Shell shell;
	private SessionManager manager;
	private Session5250 s;
	private TN5250JGUI gui;
	
	public TN5250JPanel(TN5250JInfo tn5250jInfo, Session session, Shell shell) {
		this.tn5250jInfo = tn5250jInfo;
		this.session = session;
		this.shell = shell;
		try {
			jbInit();
		} catch (Exception e) {
		}
	}
	
	private void jbInit() throws Exception {

		if (isSpecified("-L"))
			LangTool.init(parseLocale(getParameter("-L")));
		else
			LangTool.init();

		try {
			System.getProperty(".java.policy");
		} catch (SecurityException e) {
			e.printStackTrace();
			TN5250jSecurityAccessDialog.showErrorMessage(e);
			return;
		}

		Properties sesProps = new Properties();

		sesProps.put(SESSION_HOST, getParameter("host"));

	//	if (isSpecified("-e"))
	//		sesProps.put(SESSION_TN_ENHANCED, "1");
	
		if (isSpecified("-p")) {
			sesProps.put(SESSION_HOST_PORT, getParameter("-p"));
		}
		if (isSpecified("-cp"))
			sesProps.put(SESSION_CODE_PAGE, getParameter("-cp"));

		if (isSpecified("-gui"))
			sesProps.put(SESSION_USE_GUI, "1");

		if (isSpecified("-132"))
			sesProps.put(SESSION_SCREEN_SIZE, SCREEN_SIZE_27X132_STR);
		else
			sesProps.put(SESSION_SCREEN_SIZE, SCREEN_SIZE_24X80_STR);

		if (isSpecified("-sph")) {
			sesProps.put(SESSION_PROXY_HOST, getParameter("-sph"));
		}

		if (isSpecified("-spp"))
			sesProps.put(SESSION_PROXY_PORT, getParameter("-spp"));

		if (isSpecified("-dn"))
			sesProps.put(SESSION_DEVICE_NAME, getParameter("-dn"));
		
		loadSystemProperty("SESSION_CONNECT_USER");
		loadSystemProperty("SESSION_CONNECT_PASSWORD");
		loadSystemProperty("SESSION_CONNECT_PROGRAM");
		loadSystemProperty("SESSION_CONNECT_LIBRARY");
		loadSystemProperty("SESSION_CONNECT_MENU");

		manager = SessionManager.instance();
		s = manager.openSession(sesProps, "", "TN5250J");

		gui = getTN5250JGUI(tn5250jInfo, s);

	}

	private void loadSystemProperty(String param) {
		if (isSpecified(param))
			System.getProperties().put(param, getParameter(param));
	}

	protected static Locale parseLocale(String localString) {
		int x = 0;
		String[] s = { "", "", "" };
		StringTokenizer tokenizer = new StringTokenizer(localString, "_");
		while (tokenizer.hasMoreTokens()) {
			s[x++] = tokenizer.nextToken();
		}
		return new Locale(s[0], s[1], s[2]);
	}

	private boolean isSpecified(String parameter) {
		if (parameter.equals("-L")) {
			return false;
		} 
	//	else if (parameter.equals("-e")) {
	//		if (session.getEnhancedMode().equals("Y")) {
	//			return true;
	//		}
	//		else {
	//			return false;
	//		}
	//	} 
		else if (parameter.equals("-p")) {
			return true;
		} 
		else if (parameter.equals("-cp")) {
			return true;
		} 
		else if (parameter.equals("-gui")) {
			return false;
		} 
		else if (parameter.equals("-132")) {
			if (session.getScreenSize().equals("132")) {
				return true;
			}
			else {
				return false;
			}
		} 
		else if (parameter.equals("-sph")) {
			return false;
		} 
		else if (parameter.equals("-spp")) {
			return false;
		} 
		else if (parameter.equals("-dn")) {
			if (session.getDevice().equals("")) {
				return false;
			}
			else {
				return true;
			}
		} 
		else if (parameter.equals("SESSION_CONNECT_USER")) {
			return true;
		} 
		else if (parameter.equals("SESSION_CONNECT_PASSWORD")) {
			return true;
		} 
		else if (parameter.equals("SESSION_CONNECT_PROGRAM")) {
			return true;
		} 
		else if (parameter.equals("SESSION_CONNECT_LIBRARY")) {
			return true;
		} 
		else if (parameter.equals("SESSION_CONNECT_MENU")) {
			return true;
		} 
		else {
			return false;
		}
	}

	private String getParameter(String parameter) {
		if (parameter.equals("-L")) {
			return "";
		} 
		else if (parameter.equals("host")) {
			return getHost();
		} 
		else if (parameter.equals("-p")) {
			return session.getPort();
		} 
		else if (parameter.equals("-cp")) {
			return session.getCodePage();
		} 
		else if (parameter.equals("-sph")) {
			return "";
		} 
		else if (parameter.equals("-spp")) {
			return "";
		} 
		else if (parameter.equals("-dn")) {
			return session.getDevice();
		} 
		else if (parameter.equals("SESSION_CONNECT_USER")) {
			return session.getUser();
		} 
		else if (parameter.equals("SESSION_CONNECT_PASSWORD")) {
			if (session.getPassword().equals("")) {
				return "";
			}
			else {
				BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
				textEncryptor.setPassword(TN5250JCorePlugin.BASIC);
				String decryptedPassword;
				try {
					decryptedPassword = textEncryptor.decrypt(session.getPassword());
				}
				catch (EncryptionOperationNotPossibleException exeption) {
					decryptedPassword = "";
				}
				return decryptedPassword;
			}
		} 
		else if (parameter.equals("SESSION_CONNECT_PROGRAM")) {
			return session.getProgram();
		} 
		else if (parameter.equals("SESSION_CONNECT_LIBRARY")) {
			return session.getLibrary();
		} 
		else if (parameter.equals("SESSION_CONNECT_MENU")) {
			return session.getMenu();
		} 
		else {
			return "";
		}
	}

	public Session5250 getSession5250() {
		return s;
	}

	public SessionPanel getSessionGUI() {
		return gui;
	}
	
	public void addScreenListener() {
		gui.getScreen().addScreenListener(this);
	}
	
	public void removeScreenListener() {
		gui.getScreen().removeScreenListener(this);
	}

	public void onScreenChanged(int arg0, int arg1, int arg2, int arg3, int arg4) {
	}

	public void onScreenSizeChanged(int arg0, int arg1) {
		
	}

	public TN5250JGUI getTN5250JGUI(TN5250JInfo tn5250jInfo, Session5250 session5250) {
		return null;
	}
	 
	public TN5250JInfo getTN5250JInfo() {
		return tn5250jInfo;
	}
	
	public Session getSession() {
		return session;
	}
	
	public Shell getShell() {
		return shell;
	}
	
	public String getHost() {
		return "";
	}

}
