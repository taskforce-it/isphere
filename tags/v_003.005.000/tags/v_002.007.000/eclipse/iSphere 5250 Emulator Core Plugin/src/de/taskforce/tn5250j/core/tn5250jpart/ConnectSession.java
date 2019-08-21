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

public class ConnectSession extends Thread {
	
	private TN5250JPanel tn5250j;

	public ConnectSession(TN5250JPanel tn5250j) {
		this.tn5250j = tn5250j;
	}

	public void run() {
		try {
			Thread.sleep(500);
		} 
		catch (InterruptedException e) {
		}
		tn5250j.getSession5250().connect();
	}

}
