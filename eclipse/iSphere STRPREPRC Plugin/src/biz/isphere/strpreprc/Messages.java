package biz.isphere.strpreprc;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.strpreprc.messages"; //$NON-NLS-1$

    public static final String EMPTY = ""; //$NON-NLS-1$

    public static String E_R_R_O_R;

    public static String Command_A_is_invalid_or_could_not_be_found_The_original_error_message_is_B;

    public static String Could_not_get_the_active_editor;

    public static String Could_not_get_AS400_object_for_connection_A;

    public static String Menu_Edit_header;

    public static String Menu_Add_pre_compile_command;

    public static String Menu_Add_post_compile_command;

    public static String Menu_Remove_header;

    public static String Button_Prompt;

    public static String Browse;

    public static String Directory_must_not_be_empty;

    public static String The_specified_directory_does_not_exist;

    public static String Could_not_save_A_files_to_directory_B;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
