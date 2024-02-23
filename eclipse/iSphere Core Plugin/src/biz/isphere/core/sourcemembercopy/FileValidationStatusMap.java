/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import java.util.HashMap;
import java.util.Map;

public class FileValidationStatusMap {

    private Map<String, FileValidationStatus> items;

    public FileValidationStatusMap() {
        this.items = new HashMap<String, FileValidationStatus>();
    }

    public FileValidationStatus addItem(String libraryName, String fileName) {
        FileValidationStatus item = new FileValidationStatus(libraryName, fileName);
        this.items.put(item.getKey(), item);
        return item;
    }

    public FileValidationStatus getFileValidationStatus(String libraryName, String fileName) {
        return items.get(produceKey(libraryName, fileName));
    }

    public FileValidationStatus[] getItems() {
        return items.values().toArray(new FileValidationStatus[items.values().size()]);
    }

    public class FileValidationStatus {

        private String libraryName;
        private String fileName;
        private Boolean isFileError;
        private MemberValidationError errorId;
        private String errorMessage;
        private Object userData;

        public FileValidationStatus(String libraryName, String fileName) {
            this.libraryName = libraryName;
            this.fileName = fileName;

            initialize();
        }

        private void initialize() {
            this.isFileError = null;
            this.errorId = MemberValidationError.ERROR_NONE;
            this.errorMessage = null;
            this.userData = null;
        }

        public void clearFileError() {
            this.isFileError = false;
            this.errorId = MemberValidationError.ERROR_NONE;
            this.errorMessage = null;
        }

        public void setFileError(MemberValidationError errorId, String errorMessage, Object userData) {
            this.isFileError = true;
            this.errorId = errorId;
            this.errorMessage = errorMessage;
            this.userData = userData;
        }

        public boolean isFileErrorNull() {
            if (isFileError == null) {
                return true;
            }
            return false;
        }

        public boolean isFileError() {
            if (isFileError == null) {
                return false;
            }
            return isFileError;
        }

        public MemberValidationError getErrorId() {
            return errorId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public Object getUserData() {
            return userData;
        }

        public String getKey() {
            return produceKey(libraryName, fileName);
        }
    }

    private static String produceKey(String libraryName, String fileName) {
        return String.format("%s/%s", libraryName, fileName);
    }
}
