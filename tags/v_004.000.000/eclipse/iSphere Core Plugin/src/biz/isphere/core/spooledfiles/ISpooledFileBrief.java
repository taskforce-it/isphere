/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import com.ibm.as400.access.SpooledFile;

/**
 * This class briefly describes an IBM i spooled file by its identifying
 * attributes.
 */
public interface ISpooledFileBrief {

    /**
     * Returns the name of the connection.
     * 
     * @return The connection name.
     */
    public String getConnectionName();

    /**
     * Returns the name of the spooled file.
     * 
     * @return The name of the spooled file.
     * @see {@link SpooledFile#getName()}
     */
    public String getFile();

    /**
     * Returns the number of the spooled file.
     * 
     * @return The number of the spooled file.
     * @see {@link SpooledFile#getNumber()}
     */
    public int getFileNumber();

    /**
     * Returns the name of the job that created the spooled file.
     * 
     * @return The job name.
     * @see {@link SpooledFile#getJobName()}
     */
    public String getJobName();

    /**
     * Returns the ID of the user that created the spooled file.
     * 
     * @return The user ID.
     * @see {@link SpooledFile#getJobUser()}
     */
    public String getJobUser();

    /**
     * Returns the number of the job that created the spooled file.
     * 
     * @return The job number.
     * @see {@link SpooledFile#getJobNumber()}
     */
    public String getJobNumber();

    /**
     * Returns the name of the system where the spooled file was created.
     * <p>
     * This attribute can be null, but must be specified when using
     * <i>creation</i> date or <i>creation time</i>.
     * 
     * @return The name of the system where the spooled file was created.
     * @see {@link SpooledFile#getJobSystem()}
     */
    public String getJobSystem();

    /**
     * Returns the date of the spooled file creation. The date is encoded in the
     * CYYMMDD format.
     * <p>
     * This attribute can be null, but must be specified when using <i>job
     * system</i> or <i>creation time</i>.
     * 
     * @return The date (CYYMMDD) of the spooled file creation.
     * @see {@link SpooledFile#getCreateDate()}
     */
    public String getCreationDate();

    /**
     * Returns the time of spooled file creation. The time is encoded in the
     * HHMMSS format.
     * <p>
     * This attribute can be null, but must be specified when using <i>job
     * system</i> or <i>creation date</i>.
     * 
     * @return The time (HHMMSS) of the spooled file creation.
     * @see {@link SpooledFile#getCreateTime()}
     */
    public String getCreationTime();
}
