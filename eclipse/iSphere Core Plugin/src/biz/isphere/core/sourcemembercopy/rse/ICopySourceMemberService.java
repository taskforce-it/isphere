package biz.isphere.core.sourcemembercopy.rse;

public interface ICopySourceMemberService {

    public boolean copySourceMember(String fromConnectionName, String fromLibraryName, String fromFileName, String fromMemberName,
        String toConnectionName, String toLibraryName, String toFileName, String toMemberName);

}
