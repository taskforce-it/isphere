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

package de.taskforce.tn5250j.rse.subsystems;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.rse.core.subsystems.AbstractConnectorService;
import org.eclipse.rse.core.model.IHost;

public class TN5250JConnectorService extends AbstractConnectorService {

	private boolean connected = false;

	public TN5250JConnectorService(IHost host) {
		super("connectorservice.devr.name", "connectorservice.devr.desc", host, 0);
	}

	public boolean isConnected() {
		return connected;
	}
	
	protected void internalConnect(IProgressMonitor monitor) throws Exception {
		connected=true;
	}

	public void internalDisconnect(IProgressMonitor monitor) throws Exception {
		connected=false;
	}

	public boolean supportsRemoteServerLaunching() {
		return false;
	}

	public boolean supportsServerLaunchProperties() {
		return false;
	}

	public void acquireCredentials(boolean arg0) throws OperationCanceledException {
	}

	public void clearCredentials() {
	}

	public void clearPassword(boolean arg0, boolean arg1) {
	}

	public String getUserId() {
		return null;
	}

	public boolean hasPassword(boolean arg0) {
		return false;
	}

	public boolean inheritsCredentials() {
		return false;
	}

	public boolean isSuppressed() {
		return false;
	}

	public void removePassword() {
	}

	public void removeUserId() {
	}

	public boolean requiresPassword() {
		return false;
	}

	public boolean requiresUserId() {
		return false;
	}

	public void savePassword() {
	}

	public void saveUserId() {
	}

	public void setPassword(String arg0, String arg1, boolean arg2, boolean arg3) {
	}

	public void setSuppressed(boolean arg0) {
	}

	public void setUserId(String arg0) {
	}

	public boolean sharesCredentials() {
		return false;
	}

	public boolean supportsPassword() {
		return false;
	}

	public boolean supportsUserId() {
		return false;
	}
	
}
