/*******************************************************************************
 * Copyright (c) 2012-2016 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import com.ibm.as400.access.AS400;

public class ObjectLock {

    /**
     * Exclusive (*EXCL). The object is reserved for the exclusive use of the
     * requesting job; no other jobs can use the object. However, if the object
     * is already allocated to another job, your job cannot get exclusive use of
     * the object. This lock state is appropriate when a user does not want any
     * other user to have access to the object until the function being
     * performed is complete.
     */
    public static final String EXCL = "*EXCL";

    /**
     * Exclusive allow read (*EXCLRD). The object is allocated to the job that
     * requested it, but other jobs can read the object. This lock is
     * appropriate when a user wants to prevent other users from performing any
     * operation other than a read.
     */
    public static final String EXCLRD = "*EXCLRD";

    /**
     * Shared for update (*SHRUPD). The object can be shared either for update
     * or read with another job. That is, another user can request either a
     * shared-for-read lock state or a shared-for-update lock state for the same
     * object. This lock state is appropriate when a user intends to change an
     * object but wants to allow other users to read or change the same object.
     */
    public static final String SHRUPD = "*SHRUPD";

    /**
     * Shared no update (*SHRNUP). The object can be shared with another job if
     * the job requests either a shared-no-update lock state, or a
     * shared-for-read lock state. This lock state is appropriate when a user
     * does not intend to change an object but wants to ensure that no other
     * user changes the object.
     */
    public static final String SHRNUP = "*SHRNUP";

    /**
     * Shared for read (*SHRRD). The object can be shared with another job if
     * the user does not request exclusive use of the object. That is, another
     * user can request an exclusive-allow-read, shared-for-update,
     * shared-for-read, or shared-no-update lock state.
     */
    public static final String SHRRD = "*SHRRD";

    private RemoteObject remoteObject;
    private String lock;
    private String[] errorMessages;

    public ObjectLock(String connectionName, String library, String name, String type, String lock) {
        this(new RemoteObject(connectionName, name, library, type, null), lock);
    }

    public ObjectLock(RemoteObject remoteObject, String lock) {
        this.remoteObject = remoteObject;
        this.lock = lock;
    }

    public AS400 getSystem() {
        return remoteObject.getSystem();
    }

    public String getAllocateCommand(int lockWaitTime) {

        StringBuffer alcObjCmd = new StringBuffer();
        alcObjCmd.append("ALCOBJ OBJ(("); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getLibrary());
        alcObjCmd.append("/"); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getName());
        alcObjCmd.append(" "); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getObjectType());
        alcObjCmd.append(" "); //$NON-NLS-1$
        alcObjCmd.append(lock);

        if (lockWaitTime >= 0) {
            alcObjCmd.append(")) WAIT(");
            alcObjCmd.append(lockWaitTime);
            alcObjCmd.append(")"); //$NON-NLS-1$
        }

        return alcObjCmd.toString();
    }

    public String getDeallocateCommand() {

        StringBuffer alcObjCmd = new StringBuffer();
        alcObjCmd.append("DLCOBJ OBJ(("); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getLibrary());
        alcObjCmd.append("/"); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getName());
        alcObjCmd.append(" "); //$NON-NLS-1$
        alcObjCmd.append(remoteObject.getObjectType());
        alcObjCmd.append(" "); //$NON-NLS-1$
        alcObjCmd.append(lock);
        alcObjCmd.append("))"); //$NON-NLS-1$

        return alcObjCmd.toString();
    }

    public void setErrorMessages(String[] messages) {
        this.errorMessages = messages;
    }

    public String[] getErrorMessages() {
        return errorMessages;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(remoteObject.getLibrary());
        sb.append("/");
        sb.append(remoteObject.getName());
        sb.append(" ");
        sb.append(remoteObject.getObjectType());
        sb.append(" ");
        sb.append(lock);
        return sb.toString();
    }
}
