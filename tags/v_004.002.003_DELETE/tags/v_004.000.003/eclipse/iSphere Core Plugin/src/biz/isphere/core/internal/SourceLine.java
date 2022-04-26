/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.math.BigDecimal;

public class SourceLine {

    private BigDecimal sourceSequence;
    private BigDecimal sourceDate;
    private String sourceData;

    public SourceLine(BigDecimal sourceSequence, BigDecimal sourceDate, String sourceData) {
        this.sourceSequence = sourceSequence;
        this.sourceDate = sourceDate;
        this.sourceData = sourceData;
    }

    public BigDecimal getSourceSequence() {
        return sourceSequence;
    }

    public BigDecimal getSourceDate() {
        return sourceDate;
    }

    public String getSourceData() {
        return sourceData;
    }

    @Override
    public String toString() {

        StringBuilder buffer = new StringBuilder();
        buffer.append(sourceSequence);
        buffer.append(", "); //$NON-NLS-1$
        buffer.append(sourceDate);
        buffer.append(": "); //$NON-NLS-1$
        buffer.append(sourceData);

        return buffer.toString();
    }
}
