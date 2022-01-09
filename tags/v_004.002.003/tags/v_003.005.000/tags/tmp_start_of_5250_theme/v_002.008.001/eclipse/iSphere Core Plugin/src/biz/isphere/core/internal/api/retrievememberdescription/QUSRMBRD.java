package biz.isphere.core.internal.api.retrievememberdescription;

import java.beans.PropertyVetoException;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

public class QUSRMBRD extends APIProgramCallDocument {

    private static final String OVERRIDES_ARE_NOT_PROCESSED = "0";
    private static final String OVERRIDES_ARE_PROCESSED = "1";
    
    private String file;
    private String library;
    private String member;
    private String overrideProcessing;

    public QUSRMBRD(AS400 system) throws PropertyVetoException {
        super(system, "QUSRMBRD", "QSYS");
        
        this.overrideProcessing = OVERRIDES_ARE_NOT_PROCESSED;
    }

    /**
     * Sets the name of the file, library and member.
     * 
     * @param name - name of the file
     * @param library - name of the library that contains the file
     * @param member - name of the member
     */
    public void setFile(String name, String library, String member) {

        this.file = name;
        this.library = library;
        this.member = member;
    }

    public boolean execute(MBRD0100 mbrd0100) {

        try {

            if (!execute(createParameterList(mbrd0100))) {
                return false;
            }

            mbrd0100.setBytes(getParameterList()[0].getOutputData());

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    /**
     * Produces the parameter list for calling the QUSRMBRD API.
     */
    protected ProgramParameter[] createParameterList(MBRD0100 mbrd0100) throws Exception {

        ProgramParameter[] parameterList = new ProgramParameter[7];
        parameterList[0] = new ProgramParameter(mbrd0100.getLength()); // Receiver
        parameterList[1] = produceIntegerParameter(mbrd0100.getLength()); // Length
        parameterList[2] = produceStringParameter(mbrd0100.getName(), 8); // Format
        parameterList[3] = produceQualifiedObjectName(file, library); // File
        parameterList[4] = produceStringParameter(member, 10); // Member
        parameterList[5] = produceStringParameter(overrideProcessing, 1);
        parameterList[6] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }

}
