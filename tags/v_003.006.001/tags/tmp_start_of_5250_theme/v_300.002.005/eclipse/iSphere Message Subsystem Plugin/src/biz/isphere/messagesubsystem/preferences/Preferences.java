/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.messagesubsystem.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;

import sun.security.action.GetBooleanAction;

import biz.isphere.messagesubsystem.ISphereMessageSubsystemBasePlugin;
import biz.isphere.messagesubsystem.Messages;

/**
 * Class to manage access to the preferences of the plug-in.
 * <p>
 * Eclipse stores the preferences as <i>diffs</i> to their default values in
 * directory
 * <code>[workspace]\.metadata\.plugins\org.eclipse.core.runtime\.settings\</code>.
 * 
 * @author Thomas Raddatz
 */
public final class Preferences {

    private static int REPLY_BEFORE_MESSAGE_TEXT_VALUE = 1;
    private static int REPLY_AFTER_MESSAGE_TEXT_VALUE = 2;

    /**
     * The instance of this Singleton class.
     */
    private static Preferences instance;

    /**
     * Global preferences of the plugin.
     */
    private IPreferenceStore preferenceStore;

    private ReplyPositions[] replyPositions;

    /*
     * Preferences keys:
     */

    private static final String DOMAIN = ISphereMessageSubsystemBasePlugin.PLUGIN_ID + "."; //$NON-NLS-1$
    private static final String POSITION_MESSAGE_REPLY_FIELD = DOMAIN + "POSITION_MESSAGE_REPLY_FIELD"; //$NON-NLS-1$

    /**
     * Private constructor to ensure the Singleton pattern.
     */
    private Preferences() {
        super();

        this.replyPositions = new ReplyPositions[2];
        this.replyPositions[0] = ReplyPositions.REPLY_AFTER_MESSAGE_TEXT;
        this.replyPositions[1] = ReplyPositions.REPLY_BEFORE_MESSAGE_TEXT;
    }

    /**
     * Thread-safe method that returns the instance of this Singleton class.
     */
    public synchronized static Preferences getInstance() {
        if (instance == null) {
            instance = new Preferences();
            instance.preferenceStore = ISphereMessageSubsystemBasePlugin.getDefault().getPreferenceStore();
        }
        return instance;
    }

    /**
     * Thread-safe method that disposes the instance of this Singleton class.
     * <p>
     * This method is intended to be call from
     * {@link org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)}
     * to free the reference to itself.
     */
    public static void dispose() {
        if (instance != null) {
            instance = null;
        }
    }

    /*
     * Preferences: GETTER
     */

    // TODO: remove debug code
    public boolean isDebugEnabled() {
        return preferenceStore.getBoolean("MESSAGE_MONOTOR_DEBUG"); //$NON-NLS-1$
    }
    
    public boolean isReplyFieldBeforeMessageText() {

        int value = preferenceStore.getInt(POSITION_MESSAGE_REPLY_FIELD);
        if (value == REPLY_BEFORE_MESSAGE_TEXT_VALUE) {
            return true;
        }

        return false;
    }

    public String getReplyFieldPosition() {

        int value = preferenceStore.getInt(POSITION_MESSAGE_REPLY_FIELD);

        for (ReplyPositions replyPosition : replyPositions) {
            if (replyPosition.value() == value) {
                return replyPosition.label();
            }
        }

        return getDefaultReplyFieldPosition();
    }

    public String[] getReplyFieldPositions() {

        Set<String> labels = new HashSet<String>();

        labels.add(getDefaultReplyFieldPosition());

        labels.add(ReplyPositions.REPLY_AFTER_MESSAGE_TEXT.label());
        labels.add(ReplyPositions.REPLY_BEFORE_MESSAGE_TEXT.label());

        return labels.toArray(new String[labels.size()]);
    }

    /*
     * Preferences: SETTER
     */

    public void setReplyFieldPosition(String label) {

        for (ReplyPositions replyPosition : replyPositions) {
            if (replyPosition.label().equals(label)) {
                preferenceStore.setValue(POSITION_MESSAGE_REPLY_FIELD, replyPosition.value());
            }
        }
    }

    // TODO: remove debug code
    public void setDebugEnabled(boolean enabled) {
        
        preferenceStore.setValue("MESSAGE_MONOTOR_DEBUG", enabled); //$NON-NLS-1$
    }
    
    /*
     * Preferences: Default Initializer
     */

    public void initializeDefaultPreferences() {

        // TODO: remove debug code
        preferenceStore.setDefault("MESSAGE_MONOTOR_DEBUG", getDefaultDebugEnabled()); //$NON-NLS-1$
        
        preferenceStore.setDefault(POSITION_MESSAGE_REPLY_FIELD, getDefaultReplyFieldPositionInternally().value());
    }

    /*
     * Preferences: Default Values
     */

    public String getDefaultReplyFieldPosition() {
        return getDefaultReplyFieldPositionInternally().label();
    }

    // TODO: remove debug code
    public boolean getDefaultDebugEnabled() {
        return false;
    }
    
    private ReplyPositions getDefaultReplyFieldPositionInternally() {
        return ReplyPositions.getDefault();
    }

    /*
     * Helpers
     */

    private enum ReplyPositions {

        REPLY_BEFORE_MESSAGE_TEXT (REPLY_BEFORE_MESSAGE_TEXT_VALUE, Messages.Reply_Positions_beforeMessageText),
        REPLY_AFTER_MESSAGE_TEXT (REPLY_AFTER_MESSAGE_TEXT_VALUE, Messages.Reply_Positions_afterMessageText);

        private int _value;
        private String _label;

        ReplyPositions(int value, String label) {
            this._value = value;
            this._label = label;
        }

        public static ReplyPositions getDefault() {
            return REPLY_AFTER_MESSAGE_TEXT;
        }

        public int value() {
            return _value;
        }

        public String label() {
            return _label;
        }

        @Override
        public String toString() {
            return label() + ":" + value(); //$NON-NLS-1$
        }
    }
}