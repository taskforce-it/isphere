/*******************************************************************************
 * Copyright (c) 2012-2014 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import java.util.ArrayList;
import java.util.List;

import biz.isphere.core.ISpherePlugin;

/**
 * The Object Lock Manager is used to manage the object locks needed in a
 * particular context.
 * <p>
 * An application context, which requires one or more object locks on the IBM i,
 * should use the Object Lock Manager to set and remove these object locks. This
 * way the object locks a managed at a single point of control. When the
 * application context ends, it must call {@link #dispose()} to remove all
 * object locks.
 */
public abstract class AbstractObjectLockManager {

    private List<ObjectLock> objectLocks;
    private String[] lastErrorMessages;
    private int lockWaitTime;

    public AbstractObjectLockManager() {
        this(-1); // use default lock wait of IBM i
    }

    public AbstractObjectLockManager(int waitSeconds) {
        lockWaitTime = waitSeconds;
        objectLocks = new ArrayList<ObjectLock>();
        lastErrorMessages = new String[] {};
    }

    /**
     * Sets an exclusive (*EXCL) lock. The object is reserved for the exclusive
     * use of the requesting job; no other jobs can use the object. However, if
     * the object is already allocated to another job, your job cannot get
     * exclusive use of the object. This lock state is appropriate when a user
     * does not want any other user to have access to the object until the
     * function being performed is complete.
     * 
     * @param remoteObject - object that is locked
     * @return object lock
     */
    public ObjectLock setExclusiveLock(RemoteObject remoteObject) {

        return addObjectLock(remoteObject, ObjectLock.EXCL);
    }

    /**
     * Sets an exclusive allow read (*EXCLRD) lock. The object is allocated to
     * the job that requested it, but other jobs can read the object. This lock
     * is appropriate when a user wants to prevent other users from performing
     * any operation other than a read.
     * 
     * @param remoteObject - object that is locked
     * @return object lock
     */
    public ObjectLock setExclusiveAllowReadLock(RemoteObject remoteObject) {

        return addObjectLock(remoteObject, ObjectLock.EXCLRD);
    }

    /**
     * Sets a shared for update (*SHRUPD) lock. The object can be shared either
     * for update or read with another job. That is, another user can request
     * either a shared-for-read lock state or a shared-for-update lock state for
     * the same object. This lock state is appropriate when a user intends to
     * change an object but wants to allow other users to read or change the
     * same object.
     * 
     * @param remoteObject - object that is locked
     * @return object lock
     */
    public ObjectLock setSharedForUpdateLock(RemoteObject remoteObject) {

        return addObjectLock(remoteObject, ObjectLock.SHRUPD);
    }

    /**
     * Sets a shared no update (*SHRNUP) lock. The object can be shared with
     * another job if the job requests either a shared-no-update lock state, or
     * a shared-for-read lock state. This lock state is appropriate when a user
     * does not intend to change an object but wants to ensure that no other
     * user changes the object.
     * 
     * @param remoteObject - object that is locked
     * @return object lock
     */
    public ObjectLock setShareNoUpdateLock(RemoteObject remoteObject) {

        return addObjectLock(remoteObject, ObjectLock.SHRNUP);
    }

    /**
     * Sets a shared for read (*SHRRD) lock. The object can be shared with
     * another job if the user does not request exclusive use of the object.
     * That is, another user can request an exclusive-allow-read,
     * shared-for-update, shared-for-read, or shared-no-update lock state.
     * 
     * @param remoteObject - object that is locked
     * @return object lock
     */
    public ObjectLock setSharedForReadLock(RemoteObject remoteObject) {

        return addObjectLock(remoteObject, ObjectLock.SHRRD);
    }

    public String[] getErrorMessages() {

        return lastErrorMessages;
    }

    /**
     * Removes a previously set object lock.
     * 
     * @param objectLock - that that is removed
     */
    public void removeObjectLock(ObjectLock objectLock) {

        String[] messages = deallocateObject(objectLock);
        if (messages.length == 0) {
            objectLocks.remove(objectLock);
        }
    }

    private ObjectLock addObjectLock(RemoteObject remoteObject, String lockType) {

        ObjectLock objectLock = new ObjectLock(remoteObject, lockType);
        lastErrorMessages = allocateObject(objectLock, lockWaitTime);
        if (lastErrorMessages.length == 0) {
            objectLocks.add(objectLock);
            return objectLock;
        }

        return null;
    }

    /**
     * Disposes the Object Lock Manager and removes all pending object lock.
     */
    public void dispose() {
        List<ObjectLock> removedObjectLocks = new ArrayList<ObjectLock>();

        for (ObjectLock objectLock : objectLocks) {
            if (deallocateObject(objectLock).length == 0) {
                removedObjectLocks.add(objectLock);
            }
        }
        objectLocks.removeAll(removedObjectLocks);
        for (ObjectLock objectLock : objectLocks) {
            ISpherePlugin.logError("Failed to remove object lock: " + objectLock.toString(), null);
        }
    }

    protected abstract String[] allocateObject(ObjectLock objectLock, int lockWaitTime);

    protected abstract String[] deallocateObject(ObjectLock objectLock);

}
