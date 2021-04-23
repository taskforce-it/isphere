/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.journalexplorer.core.externalapi;

/**
 * This interface specifies an object that is attached to a journal. Supported
 * objects type are: *FILE, *DTAARA and *DTAQ.
 */
public interface IJournaledObject {

    /**
     * Returns the library where the object is stored.
     * 
     * @return library name
     */
    public String getLibrary();

    /**
     * Returns the name of the object.
     * 
     * @return object name
     */
    public String getName();

    /**
     * Returns the name of the physical file member for which entries are
     * retrieved.
     * <p>
     * Special values:
     * <ul>
     * <li>*FIRST - Entries for the database physical file and the first member
     * in the file are retrieved.</li>
     * <li>*ALL - Entries for the database physical file and all the currently
     * existing members of the file are retrieved.</li>
     * </ul>
     * 
     * @return member name
     */
    public String getMember();

    /**
     * Returns the object type. Must be one of *FILE, *DTAARA or *DTAQ.
     * 
     * @return object type
     */
    public String getObjectType();

    /**
     * Returns the qualified name of the object, e.g.: LIBRARY/OBJECT (TYPE)
     * <p>
     * The qualified object name is used for labels and tooltips.
     * 
     * @return qualified object name
     */
    public String getQualifiedName();

}
