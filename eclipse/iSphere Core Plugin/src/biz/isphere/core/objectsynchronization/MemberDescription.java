/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.objectsynchronization;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * This class defines the attributes of a comparable member description used by
 * the iSphere Synchronize Members editor.
 * <p>
 * Please notice, that the member text is an attribute of the object, but it is
 * <b>not</b> included in the comparison.
 */
public class MemberDescription implements Serializable, Comparable<MemberDescription> {

    private static final String CRLF = "\n";
    private static final String TAB = "\t";

    private static final long serialVersionUID = 6390106167717880849L;

    private String connectionName;
    private String fileName;
    private String libraryName;
    private String memberName;
    private String sourceType;
    private Timestamp lastChangedDate;
    private Long checksum;

    public MemberDescription() {
    }

    /**
     * Textual description of the member. This attribute is not used when
     * comparing two member descriptions. Hence it is not honored in
     * <code>hashCode()</code> neither in <code>equals()</code>.
     */
    private String text;

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String hostName) {
        this.fileName = hostName;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public Timestamp getLastChangedDate() {
        return lastChangedDate;
    }

    public void setLastChangedDate(Timestamp lastChangedDate) {
        this.lastChangedDate = lastChangedDate;
    }

    public Long getChecksum() {
        return checksum;
    }

    public void setChecksum(Long checksum) {
        this.checksum = checksum;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQualifiedMemberName() {
        return getLibraryName() + "/" + getFileName() + "." + getMemberName();
    }

    /**
     * Compares this member description with another member description. The
     * <i>text</i> attribute is intentionally not compared.
     */
    public int compareTo(MemberDescription other) {

        if (other == null) {
            return 1;
        }

        int rc = compareTo(libraryName, other.getLibraryName());
        if (rc == 0) {
            rc = compareTo(fileName, other.getFileName());
            if (rc == 0) {
                rc = compareTo(memberName, other.getMemberName());
                if (rc == 0) {
                    rc = compareTo(sourceType, other.getSourceType());
                    if (rc == 0) {
                        rc = compareTo(lastChangedDate, other.getLastChangedDate());
                        if (rc == 0) {
                            rc = compareTo(checksum, other.getChecksum());
                        }
                    }
                }
            }
        }

        return rc;
    }

    private int compareTo(String me, String other) {
        if (me == null && other == null) {
            return 0;
        } else if (me == null && other != null) {
            return -1;
        } else if (me != null && other == null) {
            return 1;
        } else {
            return me.compareTo(other);
        }
    }

    private int compareTo(Timestamp me, Timestamp other) {
        if (me == null && other == null) {
            return 0;
        } else if (me == null && other != null) {
            return -1;
        } else if (me != null && other == null) {
            return 1;
        } else {
            return me.compareTo(other);
        }
    }

    private int compareTo(Long me, Long other) {
        if (me == null && other == null) {
            return 0;
        } else if (me == null && other != null) {
            return -1;
        } else if (me != null && other == null) {
            return 1;
        } else {
            return me.compareTo(other);
        }
    }

    /**
     * Produces the hash code of this member description. The <i>text</i>
     * attribute is intentionally not included.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((memberName == null) ? 0 : memberName.hashCode());
        result = prime * result + ((checksum == null) ? 0 : checksum.hashCode());
        result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
        result = prime * result + ((lastChangedDate == null) ? 0 : lastChangedDate.hashCode());
        result = prime * result + ((libraryName == null) ? 0 : libraryName.hashCode());
        result = prime * result + ((sourceType == null) ? 0 : sourceType.hashCode());
        return result;
    }

    /**
     * Tests whether this member description equals another member description.
     * The <i>text</i> attribute is intentionally not compared.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MemberDescription other = (MemberDescription)obj;
        if (memberName == null) {
            if (other.memberName != null) return false;
        } else if (!memberName.equals(other.memberName)) return false;
        if (checksum == null) {
            if (other.checksum != null) return false;
        } else if (!checksum.equals(other.checksum)) return false;
        if (fileName == null) {
            if (other.fileName != null) return false;
        } else if (!fileName.equals(other.fileName)) return false;
        if (lastChangedDate == null) {
            if (other.lastChangedDate != null) return false;
        } else if (!lastChangedDate.equals(other.lastChangedDate)) return false;
        if (libraryName == null) {
            if (other.libraryName != null) return false;
        } else if (!libraryName.equals(other.libraryName)) return false;
        if (sourceType == null) {
            if (other.sourceType != null) return false;
        } else if (!sourceType.equals(other.sourceType)) return false;
        return true;
    }
}
