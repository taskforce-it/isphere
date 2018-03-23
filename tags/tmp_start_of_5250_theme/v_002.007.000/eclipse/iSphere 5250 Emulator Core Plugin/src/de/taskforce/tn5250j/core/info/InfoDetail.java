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

package de.taskforce.tn5250j.core.info;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.tn5250j.TN5250JPlugin;

import de.taskforce.tn5250j.core.Messages;
import de.taskforce.tn5250j.core.TN5250JCorePlugin;

public class InfoDetail {

	public InfoDetail() {
	}
	
	public void createContents(Composite parent) {
		
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		
		Composite container = new Composite(sc, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		container.setLayout(gridLayout);
		
		for (int idx = 0; idx < TN5250JPlugin.getTN5250JInstallations().size(); idx++) {
			Label labelVersion = new Label(container, SWT.NONE);
			GridData gd_labelVersion = new GridData(SWT.CENTER, SWT.CENTER, true, false);
			labelVersion.setLayoutData(gd_labelVersion);
			labelVersion.setText((String)TN5250JPlugin.getTN5250JInstallations().get(idx));
		}
		
		final Label labelTN5250JImage = new Label(container, SWT.NONE);
		labelTN5250JImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelTN5250JImage.setImage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_TN5250JSPLASH));

		final Label labelPoweredBy = new Label(container, SWT.NONE);
		final GridData gd_labelPoweredBy = new GridData(SWT.CENTER, SWT.CENTER, true, false);
		labelPoweredBy.setLayoutData(gd_labelPoweredBy);
		labelPoweredBy.setText("powered by");
		
		final Label labelTaskForceImage = new Label(container, SWT.NONE);
		labelTaskForceImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelTaskForceImage.setImage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_TASKFORCE));
		
		final Composite compositeInternal = new Composite(container, SWT.NONE);
		compositeInternal.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeInternal.setLayout(new GridLayout());
		
		final Composite compositeAdress = new Composite(container, SWT.NONE);
		compositeAdress.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeAdress.setLayout(new GridLayout());

		final Label cA1 = new Label(compositeAdress, SWT.NONE);
		cA1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA1.setText("Task Force IT-Consulting GmbH");

		final Label cA2 = new Label(compositeAdress, SWT.NONE);
		cA2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA2.setText("Im Eickel 77");

		final Label cA3 = new Label(compositeAdress, SWT.NONE);
		cA3.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA3.setText("45731 Waltrop");

		final Label cA4 = new Label(compositeAdress, SWT.NONE);
		cA4.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cA4.setText("Deutschland / Germany");

		final Composite compositeNumbers = new Composite(container, SWT.NONE);
		compositeNumbers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeNumbers.setLayout(new GridLayout());

		final Label cN1 = new Label(compositeNumbers, SWT.NONE);
		cN1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cN1.setText(Messages.getString("Telefon") + ": +49 23 09-60 93 01");

		final Label cN2 = new Label(compositeNumbers, SWT.NONE);
		cN2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cN2.setText(Messages.getString("Telefax") + ": +49 23 09-40 97 68");

		final Composite compositeInternet = new Composite(container, SWT.NONE);
		compositeInternet.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		compositeInternet.setLayout(new GridLayout());

		final Label cI1 = new Label(compositeInternet, SWT.NONE);
		cI1.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cI1.setText(Messages.getString("E-Mail") + ": info@taskforce-it.de");

		final Label cI2 = new Label(compositeInternet, SWT.NONE);
		cI2.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		cI2.setText(Messages.getString("Internet") + ": www.taskforce-it.de");

		final Label labelSeparator1 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator1 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator1.setLayoutData(gd_labelSeparator1);
		
		final Label labelCMOneImage = new Label(container, SWT.NONE);
		labelCMOneImage.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, false));
		labelCMOneImage.setImage(TN5250JCorePlugin.getDefault().getImageRegistry().get(TN5250JCorePlugin.IMAGE_CMONE));

		final Label labelSeparator2 = new Label(container, SWT.SEPARATOR | SWT.HORIZONTAL);
		final GridData gd_labelSeparator2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		labelSeparator2.setLayoutData(gd_labelSeparator2);
		
		// Compute size
		Point point = container.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
	    // Set the child as the scrolled content of the ScrolledComposite
	    sc.setContent(container);

	    // Set the minimum size
	    sc.setMinSize(point.x, point.y);

	    // Expand both horizontally and vertically
	    sc.setExpandHorizontal(true);
	    sc.setExpandVertical(true);
		
	}
	
	public boolean processButtonPressed() {	
		return true;
	}

}
