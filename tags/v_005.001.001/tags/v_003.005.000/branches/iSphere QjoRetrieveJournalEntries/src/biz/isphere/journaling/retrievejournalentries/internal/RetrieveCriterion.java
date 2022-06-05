package biz.isphere.journaling.retrievejournalentries.internal;

import com.ibm.as400.access.AS400DataType;

/**
 * Class, representing a selection criterion.
 * 
 * @author Thomas Raddatz
 */
public class RetrieveCriterion {

    RetrieveKey key;

    AS400DataType dataType;

    Object value;

    public RetrieveCriterion(RetrieveKey aKey, AS400DataType aDataType, Object aValue) {
        key = aKey;
        dataType = aDataType;
        value = aValue;
    }

    public RetrieveKey getKey() {
        return key;
    }

    public AS400DataType getDataType() {
        return dataType;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return key + "=" + value;
    }
}
