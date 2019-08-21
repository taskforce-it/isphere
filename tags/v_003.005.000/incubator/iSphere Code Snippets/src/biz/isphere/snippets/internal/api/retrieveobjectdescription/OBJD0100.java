/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.snippets.internal.api.retrieveobjectdescription;

import java.util.Date;

import com.ibm.as400.access.AS400Message;

/**
 * Format OBJD0100 of the "Retrieve Object Description (QUSROBJD)" API.
 */
public class OBJD0100 {

    AS400Message errorMessage;
    private int bytesReturned;
    private int bytesAvailable;
    private String objectName;
    private String LibraryName;
    private String objectType;
    private String libraryReturned;
    private int auxiliaryStoragePoolASPNumber;
    private String objectOwner;
    private String objectDomain;
    private Date creationTime;
    private Date lastChangedTime;

    public OBJD0100() {
        this.bytesReturned = 0;
        this.bytesAvailable = 0;
        this.errorMessage = null;
    }

    public int getBytesReturned() {
        return bytesReturned;
    }

    public void setBytesReturned(int bytesReturned) {
        this.bytesReturned = bytesReturned;
    }

    public int getBytesAvailable() {
        return bytesAvailable;
    }

    public void setBytesAvailable(int bytesAvailable) {
        this.bytesAvailable = bytesAvailable;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getLibraryName() {
        return LibraryName;
    }

    public void setLibraryName(String libraryName) {
        LibraryName = libraryName;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getLibraryReturned() {
        return libraryReturned;
    }

    public void setLibraryReturned(String libraryReturned) {
        this.libraryReturned = libraryReturned;
    }

    public int getAuxiliaryStoragePoolASPNumber() {
        return auxiliaryStoragePoolASPNumber;
    }

    public void setAuxiliaryStoragePoolASPNumber(int auxiliaryStoragePoolASPNumber) {
        this.auxiliaryStoragePoolASPNumber = auxiliaryStoragePoolASPNumber;
    }

    public String getObjectOwner() {
        return objectOwner;
    }

    public void setObjectOwner(String objectOwner) {
        this.objectOwner = objectOwner;
    }

    public String getObjectDomain() {
        return objectDomain;
    }

    public void setObjectDomain(String objectDomain) {
        this.objectDomain = objectDomain;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getLastChangedTime() {
        return lastChangedTime;
    }

    public void setLastChangedTime(Date lastChangedTime) {
        this.lastChangedTime = lastChangedTime;
    }

    public AS400Message getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMEssage(AS400Message message) {
        this.errorMessage = message;
    }
}
