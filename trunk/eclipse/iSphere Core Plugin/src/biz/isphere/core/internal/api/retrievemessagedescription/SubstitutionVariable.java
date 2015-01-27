package biz.isphere.core.internal.api.retrievemessagedescription;

import java.io.UnsupportedEncodingException;

import biz.isphere.core.internal.api.APIFormat;

import com.ibm.as400.access.AS400;

public class SubstitutionVariable extends APIFormat {

    private static final String LENGTH_OF_REPLACEMENT_DATA = "lengthOfReplacementData";
    private static final String FIELD_SIZE_OR_DECIMAL_POSITIONS = "fieldSizeOrDecimalPositions";
    private static final String SUBSTItUTION_VARIABLE_TYPE = "substitutionVariableType";
    
    public SubstitutionVariable(AS400 system, byte[] bytes) throws UnsupportedEncodingException {
        super(system, "SubstitutionVariable");

        createStructure();

        setBytes(bytes);
    }

    public void setOffset(int offset) {
        super.setOffset(offset);
    }

    public int getLength() {
        return getInt4Value(LENGTH_OF_REPLACEMENT_DATA);
    }
    
    public int getDecimalPositions() {
        return getInt4Value(FIELD_SIZE_OR_DECIMAL_POSITIONS);
    }
    
    public String getType() throws UnsupportedEncodingException {
        return getCharValue(SUBSTItUTION_VARIABLE_TYPE);
    }
    
    private void createStructure() {

        addInt4Field(LENGTH_OF_REPLACEMENT_DATA, 0);
        addInt4Field(FIELD_SIZE_OR_DECIMAL_POSITIONS, 4);
        addCharField(SUBSTItUTION_VARIABLE_TYPE, 8, 10);
    }
}
