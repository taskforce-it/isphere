/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles;

import biz.isphere.core.Messages;

/**
 * Defines the attributes of a spooled file. Each attribute holds the following
 * information:
 * <ul>
 * <li>attribute name</li>
 * <li>column heading</li>
 * <li>column width</li>
 * </ul>
 * The items of this enumeration can be used for getting spooled file
 * attributes. Call {@link SpooledFile#getAttributeValue(SpooledFileAttributes)}
 * to retrieve a spooled file attribute.
 */
public enum SpooledFileAttributes {
    STATUS ("status", Messages.Status, 70),
    FILE ("file", Messages.File, 100),
    FILE_NUMBER ("fileNumber", Messages.File_number, 80),
    JOB_NAME ("jobName", Messages.Job_name, 100),
    JOB_USER ("jobUser", Messages.Job_user, 100),
    JOB_NUMBER ("jobNumber", Messages.Job_number, 100),
    JOB_SYSTEM ("jobSystem", Messages.Job_system, 80),
    CREATION_TIMESTAMP ("creationTimestamp", Messages.Creation_timestamp, 150),
    OUTPUT_QUEUE ("outputQueue", Messages.Output_queue, 160),
    OUTPUT_PRIORITY ("outputPriority", Messages.Output_priority, 80),
    USER_DATA ("userData", Messages.User_data, 80),
    FORM_TYPE ("formType", Messages.Form_type, 80),
    COPIES ("copies", Messages.Copies, 50),
    PAGES ("pages", Messages.Pages, 50),
    CURRENT_PAGE ("currentPage", Messages.Current_page, 50),
    CREATION_DATE ("creationDate", Messages.Creation_date, 80),
    CREATION_TIME ("creationTime", Messages.Creation_time, 80);

    public final String attributeName;
    public final String columnHeading;
    public final int columnWidth;

    SpooledFileAttributes(String attributeName, String columnHeading, int columnWidth) {
        this.attributeName = attributeName;
        this.columnHeading = columnHeading;
        this.columnWidth = columnWidth;
    }

    public static SpooledFileAttributes getByName(String attributeName) {

        for (SpooledFileAttributes column : SpooledFileAttributes.values()) {
            if (column.attributeName.equals(attributeName)) {
                return column;
            }
        }

        throw new RuntimeException("Column [" + attributeName + "] not found.");
    }
}
