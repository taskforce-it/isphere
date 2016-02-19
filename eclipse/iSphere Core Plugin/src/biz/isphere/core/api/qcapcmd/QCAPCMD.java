package biz.isphere.core.api.qcapcmd;

import biz.isphere.core.ISpherePlugin;
import biz.isphere.core.internal.api.APIErrorCode;
import biz.isphere.core.internal.api.APIProgramCallDocument;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.ProgramParameter;

public class QCAPCMD extends APIProgramCallDocument {

    private String commandReturned;
    private int lengthReturned;

    public QCAPCMD(AS400 system) {
        super(system, "QCAPCMD", "QSYS");
    }

    public static boolean checkCLStatement(AS400 system, String command) {

        try {
            QCAPCMD qcapcmd = new QCAPCMD(system);
            return qcapcmd.execute(command, CPOP0100.checkCLStatement(system));
        } catch (Throwable e) {
            return false;
        }
    }

    public boolean checkCLCommand(String command) {

        try {
            return execute(command, CPOP0100.checkCLStatement(getSystem()));
        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public boolean execute(String command, CPOP0100 cpop0100) {

        try {

            if (!execute(createParameterList(cpop0100, command))) {
                return false;
            }

            lengthReturned = getIntConverter().toInt(getParameterList()[7].getOutputData());
            commandReturned = getCharConverter().byteArrayToString(getParameterList()[5].getOutputData()).substring(0, lengthReturned);

            return true;

        } catch (Throwable e) {
            ISpherePlugin.logError(e.getLocalizedMessage(), e);
            return false;
        }
    }

    public String getErrorMessage() {
        return super.getErrorMessage();
    }

    public String getChangedCommand() {
        return commandReturned;
    }

    public int getLengthReturned() {
        return lengthReturned;
    }

    /**
     * Produces the parameter list for calling the QCAPCMD API.
     */
    protected ProgramParameter[] createParameterList(CPOP0100 cpop0100, String command) throws Exception {

        int returnLength = Math.min(command.length() * 4, 32702);

        ProgramParameter[] parameterList = new ProgramParameter[9];
        parameterList[0] = produceStringParameter(command, command.length());
        parameterList[1] = produceIntegerParameter(command.length());
        parameterList[2] = produceByteParameter(cpop0100.getBytes());
        parameterList[3] = produceIntegerParameter(cpop0100.getLength());
        parameterList[4] = produceStringParameter(cpop0100.getName(), 8);
        parameterList[5] = produceStringParameter("", returnLength);
        parameterList[6] = produceIntegerParameter(returnLength);
        parameterList[7] = produceIntegerParameter(0);
        parameterList[8] = produceByteParameter(new APIErrorCode().getBytes());

        return parameterList;
    }
}
