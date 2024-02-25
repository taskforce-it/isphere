/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.sql.Timestamp;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.data.PcmlException;
import com.ibm.as400.data.ProgramCallDocument;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.PcmlProgramCallDocument;

public class SYNCMBR_retrieveMemberAttributes {

    public MemberAttributes run(AS400 _as400, String library, String file, String member) {

        MemberAttributes memberAttributes = null;

        try {

            ProgramCallDocument pcml = new PcmlProgramCallDocument(_as400, "biz.isphere.core.objectsynchronization.SYNCMBR_retrieveMemberAttributes", //$NON-NLS-1$
                this.getClass().getClassLoader());

            pcml.setStringValue("SYNCMBR_retrieveMemberAttributes.library", library);
            pcml.setStringValue("SYNCMBR_retrieveMemberAttributes.file", file);
            pcml.setStringValue("SYNCMBR_retrieveMemberAttributes.member", member);

            boolean rc = pcml.callProgram("SYNCMBR_retrieveMemberAttributes");

            if (rc == false) {

                AS400Message[] msgs = pcml.getMessageList("SYNCMBR_retrieveMemberAttributes");
                for (int idx = 0; idx < msgs.length; idx++) {
                    ISpherePlugin.logError(msgs[idx].getID() + " - " + msgs[idx].getText(), null); //$NON-NLS-1$
                }
                ISpherePlugin.logError("*** Call to SYNCMBR_retrieveMemberAttributes failed. See messages above ***", null); //$NON-NLS-1$

                memberAttributes = null;

            } else {

                memberAttributes = new MemberAttributes();
                memberAttributes.setLibrary(pcml.getStringValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.library"));
                memberAttributes.setFile(pcml.getStringValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.file"));
                memberAttributes.setMember(pcml.getStringValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.member"));
                memberAttributes.setSrcType(pcml.getStringValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.srcType"));
                memberAttributes.setText(pcml.getStringValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.text"));
                memberAttributes.setLastChanged((Timestamp)pcml.getValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.lastChanged"));
                memberAttributes.setCheckSum((Long)pcml.getValue("SYNCMBR_retrieveMemberAttributes.mbrAttrs.checkSum"));

            }

        } catch (PcmlException e) {

            memberAttributes = null;
            ISpherePlugin.logError("*** Call to SYNCMBR_retrieveMemberAttributes failed. See messages above ***", e); //$NON-NLS-1$
        }

        return memberAttributes;

    }

    public class MemberAttributes {

        private String library;
        private String file;
        private String member;
        private String srcType;
        private String text;
        private Timestamp lastChanged;
        private long checkSum;

        public String getLibrary() {
            return library;
        }

        public void setLibrary(String library) {
            this.library = library;
        }

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public String getMember() {
            return member;
        }

        public void setMember(String member) {
            this.member = member;
        }

        public String getSrcType() {
            return srcType;
        }

        public void setSrcType(String srcType) {
            this.srcType = srcType;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Timestamp getLastChanged() {
            return lastChanged;
        }

        public void setLastChanged(Timestamp lastChanged) {
            this.lastChanged = lastChanged;
        }

        public long getCheckSum() {
            return checkSum;
        }

        public void setCheckSum(long checkSum) {
            this.checkSum = checkSum;
        }
    }
}