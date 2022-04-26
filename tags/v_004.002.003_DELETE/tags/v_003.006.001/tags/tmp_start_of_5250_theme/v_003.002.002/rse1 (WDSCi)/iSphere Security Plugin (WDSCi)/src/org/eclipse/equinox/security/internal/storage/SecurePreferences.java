/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package org.eclipse.equinox.security.internal.storage;

import java.io.IOException;
import java.util.UUID;

import org.eclipse.equinox.security.ISphereSecurityRSEPlugin;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jface.preference.IPreferenceStore;


public class SecurePreferences implements ISecurePreferences {

    private IPreferenceStore preferencesStore;
    private JavaEncryption encryption;

    public SecurePreferences() {
        preferencesStore = ISphereSecurityRSEPlugin.getDefault().getPreferenceStore();
    }

    public String absolutePath() {
        // TODO Auto-generated method stub
        return null;
    }

    public String[] childrenNames() {
        // TODO Auto-generated method stub
        return null;
    }

    public void clear() {
        // TODO Auto-generated method stub
    }

    public void flush() throws IOException {

    }

    public String get(String key, String def) throws StorageException {

        if (!hasKey(getKeyValue(key))) {
            return def;
        }

        String value = preferencesStore.getString(getKeyValue(key));

        if (value != null) {
            if (isEncrypted(getKeyEncrypted(key))) {
                value = decrypt(value);
            }
            return value;
        }

        return def;
    }

    public boolean getBoolean(String key, boolean def) throws StorageException {
        // TODO Auto-generated method stub
        return false;
    }

    public byte[] getByteArray(String key, byte[] def) throws StorageException {
        // TODO Auto-generated method stub
        return null;
    }

    public double getDouble(String key, double def) throws StorageException {
        // TODO Auto-generated method stub
        return 0;
    }

    public float getFloat(String key, float def) throws StorageException {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getInt(String key, int def) throws StorageException {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getLong(String key, long def) throws StorageException {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isEncrypted(String key) throws StorageException {

        return preferencesStore.getBoolean(key);
    }

    public String[] keys() {
        // TODO Auto-generated method stub
        return null;
    }

    public String name() {
        // TODO Auto-generated method stub
        return null;
    }

    public ISecurePreferences node(String pathName) {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean nodeExists(String pathName) {
        // TODO Auto-generated method stub
        return false;
    }

    public ISecurePreferences parent() {
        // TODO Auto-generated method stub
        return null;
    }

    public void put(String key, String value, boolean encrypt) throws StorageException {

        preferencesStore.setValue(getKeyEncrypted(key), encrypt);

        if (encrypt) {
            preferencesStore.setValue(getKeyValue(key), encrypt(value));
        } else {
            preferencesStore.setValue(key, value);
        }
    }

    public void putBoolean(String key, boolean value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void putByteArray(String key, byte[] value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void putDouble(String key, double value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void putFloat(String key, float value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void putInt(String key, int value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void putLong(String key, long value, boolean encrypt) throws StorageException {
        // TODO Auto-generated method stub

    }

    public void remove(String key) {
        // TODO Auto-generated method stub

    }

    public void removeNode() {
        // TODO Auto-generated method stub

    }

    private boolean hasKey(String key) {
        return preferencesStore.contains(key);
    }

    private String decrypt(String value) throws StorageException {

        try {
            return getEncryption().decrypt(value);
        } catch (Throwable e) {
            throw new StorageException(StorageException.DECRYPTION_ERROR, e);
        }
    }

    private String encrypt(String value) throws StorageException {

        try {
            return getEncryption().encrypt(value);
        } catch (Throwable e) {
            throw new StorageException(StorageException.ENCRYPTION_ERROR, e);
        }
    }

    private String getKeyValue(String key) {
        return key + ".value";
    }

    private String getKeyEncrypted(String key) {
        return key + ".encrypted";
    }

    private JavaEncryption getEncryption() {

        if (encryption == null) {
            encryption = new JavaEncryption(getEncryptionKey());
        }

        return encryption;
    }

    private byte[] getEncryptionKey() {

        String key = preferencesStore.getString("org.eclipse.equinox.internal.security.storage.key");
        if (key == null || key.trim().length() == 0) {
            key = UUID.randomUUID().toString().replaceAll("-", "");
            preferencesStore.setValue("org.eclipse.equinox.internal.security.storage.key", key);
        }

        byte[] ba = new byte[key.length() / 2];
        int s = 0;
        for (int i = 0; i < ba.length; i++) {
            ba[i] = Integer.valueOf(key.substring(s, s + 2), 16).byteValue();
            s = s + 2;
        }

        return ba;
    }
}
