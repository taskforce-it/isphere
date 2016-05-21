package biz.isphere.rse.sourcemembercopy;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;

import biz.isphere.core.sourcemembercopy.rse.ICopySourceMemberService;
import biz.isphere.rse.ibmi.contributions.extension.point.XRDiContributions;

import com.ibm.etools.iseries.rse.ui.resources.QSYSEditableRemoteSourceFileMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;
import com.ibm.etools.iseries.subsystems.qsys.objects.QSYSRemoteSourceMember;

public class CopySourceMemberService implements ICopySourceMemberService {

    private XRDiContributions rdiContributions;

    public CopySourceMemberService() {
        rdiContributions = new XRDiContributions();
    }

    public boolean copySourceMember(String fromConnectionName, String fromLibraryName, String fromFileName, String fromMemberName,
        String toConnectionName, String toLibraryName, String toFileName, String toMemberName) {

        try {

            IBMiConnection fromConnection = IBMiConnection.getConnection(fromConnectionName);
            if (fromConnection == null) {
                setError("Could not get 'from' connection");
                return false;
            }

            IBMiConnection toConnection = IBMiConnection.getConnection(toConnectionName);
            if (toConnection == null) {
                setError("Could not get 'from' connection");
                return false;
            }

            IQSYSMember fromMember = fromConnection.getMember(fromLibraryName, fromFileName, fromMemberName, null);
            if (fromMember == null) {
                setError("Member not found");
                return false;
            }

            if (!(fromMember instanceof QSYSRemoteSourceMember)) {
                setError("Member is not a source member");
                return false;
            }

            QSYSEditableRemoteSourceFileMember downloadMember = new QSYSEditableRemoteSourceFileMember((QSYSRemoteSourceMember)fromMember);
            if (!downloadMember.download(null, true)) {
                setError("Failed to download member");
                return false;
            }

            IFile downloadedLocalResource = downloadMember.getLocalResource();
            if (downloadedLocalResource == null) {
                setError("Failed to create local member resource");
                return false;
            }

            if (!ensureMember(toConnection, toLibraryName, toFileName, toMemberName, fromMember.getDescription(), fromMember.getType())) {
                return false;
            }

            if (!uploadMember(toConnection, toLibraryName, toFileName, toMemberName, downloadedLocalResource)) {
                return false;
            }

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean ensureMember(IBMiConnection connection, String libraryName, String fileName, String memberName, String description,
        String sourceType) {

        if (!rdiContributions.checkFile(connection.getConnectionName(), libraryName, fileName)) {
            setError("File not found.");
            return false;
        }

        if (!rdiContributions.checkMember(connection.getConnectionName(), libraryName, fileName, memberName)) {
            String command = "ADDPFM FILE(" + libraryName + "/" + fileName + ") MBR(" + memberName + ") TEXT('" + description + "') SRCTYPE("
                + sourceType + ")";
            String message = rdiContributions.executeCommand(connection.getConnectionName(), command, null);
            if (message != null) {
                setError("Could not create member.");
                return false;
            }
        }

        return true;
    }

    private boolean uploadMember(IBMiConnection connection, String toLibraryName, String toFileName, String toMemberName, IFile localResource)
        throws Exception {

        IQSYSMember uploadMember = connection.getMember(toLibraryName, toFileName, toMemberName, null);
        if (uploadMember == null) {
            setError("Member is not a source member");
            return false;
        }

        if (!(uploadMember instanceof QSYSRemoteSourceMember)) {
            setError("Member is not a source member");
            return false;
        }

        QSYSEditableRemoteSourceFileMember editableMember = new QSYSEditableRemoteSourceFileMember((QSYSRemoteSourceMember)uploadMember);

        BufferedReader br = null;

        try {

            br = new BufferedReader(new InputStreamReader(localResource.getContents()));
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line.substring(24)); // strip sequence number and date
            }

            editableMember.setContents(lines.toArray(new String[lines.size()]), true, null);

        } finally {
            if (br != null) {
                br.close();
            }
        }

        return true;
    }

    private void setError(String message) {

    }
}
