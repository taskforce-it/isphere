/*******************************************************************************
 * Copyright (c) 2012-2017 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     iSphere Project Owners - Maintenance and enhancements
 *******************************************************************************/

package biz.isphere.messagesubsystem;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

    private static final String BUNDLE_NAME = "biz.isphere.messagesubsystem.messages"; //$NON-NLS-1$

    public static final String EMPTY = ""; //$NON-NLS-1$

    public static String E_R_R_O_R;

    public static String Message_ID_colon;

    public static String From_job_colon;

    public static String From_job_number_colon;

    public static String From_program_colon;

    public static String Message_queue_colon;

    public static String Library_colon;

    public static String From_user_colon;

    public static String Severity_threshold_colon;

    public static String Message_text_contains_colon;

    public static String Message_type_colon;

    public static String Message_Type_Text_Any;

    public static String Message_Type_Text_Completion;

    public static String Message_Type_Text_Diagnostic;

    public static String Message_Type_Text_Informational;

    public static String Message_Type_Text_Inquiry;

    public static String Message_Type_Text_Senders_copy;

    public static String Message_Type_Text_Request;

    public static String Message_Type_Text_Request_with_prompting;

    public static String Message_Type_Text_Notify;

    public static String Message_Type_Escape;

    public static String Message_Type_Reply_Not_Validity_Checked;

    public static String Message_Type_Reply_Validity_Checked;

    public static String Message_Type_Reply_Message_Default_Used;

    public static String Message_Type_Reply_System_Default_Used;

    public static String Message_Reply_From_System_Reply_List;

    public static String iSeries_Message;

    public static String Severity_colon;

    public static String Date_sent_colon;

    public static String From_colon;

    public static String Message_colon;

    public static String Reply_colon;

    public static String Reply;

    public static String ISeries_Message_Reply_Error;

    public static String ISeries_Message_Email_Error;

    public static String Message_Queue_Monitoring_Error;

    public static String Monitor_message_queue;

    public static String Monitor_message_queue_tooltip;

    public static String Remove_informational_messages_after_notification;

    public static String Remove_informational_messages_after_notification_tooltip;

    public static String Inquiry_message_notification_colon;

    public static String Inquiry_message_notification_tooltip;

    public static String Informational_message_notification_colon;

    public static String Informational_message_notification_tooltip;

    public static String Notification_type_Dialog;

    public static String Notification_type_Email;

    public static String Notification_type_Beep;

    public static String Email_address_colon;

    public static String Email_address_tooltip;

    public static String Email_from_colon;

    public static String Email_from_tooltip;

    public static String Email_host_colon;

    public static String Email_host_tooltip;

    public static String Email_port_colon;

    public static String Email_port_tooltip;

    public static String Email_send_test_message;

    public static String Email_send_test_message_tooltip;

    public static String Email_Notification_Error_Message;

    public static String Email_Notification_Properties_Error_message;

    public static String ISeries_Message_Monitor_Test;

    public static String Notification_test_message;

    public static String Notification_test_message_sent_to;

    public static String Notification_test_message_failed;

    public static String Delete_Message_Error;

    public static String From;

    public static String Message_ID;

    public static String Severity;

    public static String Message_type;

    public static String Date_sent;

    public static String From_job;

    public static String From_job_number;

    public static String From_program;

    public static String Reply_status;

    public static String SmtpLogin_credentials;

    public static String SmtpLogin_credentials_tooltip;

    public static String SmtpUser_colon;

    public static String SmtpUser_tooltip;

    public static String SmtpPassword_colon;

    public static String SmtpPassword_tooltip;

    public static String Filter;

    public static String Monitor;

    public static String OK_To_All_LABEL;

    public static String RESET_LABEL;

    public static String Collect_informational_messages_on_startup;

    public static String Collect_informational_messages_on_startup_tooltip;

    public static String Queued_Messages_title;

    public static String Previous_request_is_still_pending;

    public static String One_or_more_messages_could_not_be_removed;

    public static String Please_enter_a_value;

    public static String Message_queue_is_missing;

    public static String Message_queue_library_is_invalid_or_missing;

    public static String From_user_is_missing;

    public static String Message_ID_is_missing;

    public static String Severity_is_missing;

    public static String From_job_is_missing;

    public static String From_job_number_is_missing;

    public static String From_program_is_missing;

    public static String Message_type_is_missing;

    public static String Message_queue_A_not_found_in_library_B;

    public static String Delivery_mode_colon;

    public static String Message_text_colon;

    public static String Recipient_type_colon;

    public static String Recipients_colon;

    public static String Recipient;

    public static String Message_text_is_missing;

    public static String Recipients_are_missing;

    public static String Invalid_recipient;

    public static String Send_Message;

    public static String Forward_Message;

    public static String A_must_be_the_only_item_in_the_list;

    public static String A_cannot_be_used_if_B_is_specified_for_the_C_parameter;

    public static String Recipient_type;

    public static String Reply_Positions_beforeMessageText;

    public static String Reply_Positions_afterMessageText;

    public static String Reply_Field_Position_colon;

    public static String Reply_Field_Position_tooltip;

    public static String Reply_message_queue_name;

    public static String Reply_message_queue_library;

    public static String Invalid_message_queue_library_name;

    public static String Invalid_message_queue_name;

    public static String Column_Date;

    public static String Column_Time;

    public static String Column_Message_ID;

    public static String Column_Message_Type;

    public static String Column_Message_Text;

    public static String Informational_messages_are_removed_on_closing_the_dialog;

    public static String Message_Reply_Status_Accepts_Send;

    public static String Message_Reply_Status_Accepts_Not_Send;

    public static String Message_Reply_Status_Not_Accept;

    public static String Label_Full_generic_string;

    public static String Special_values_are_not_allowed;

    public static String User_profile_A_does_not_exist;

    public static String User_profile_A_has_already_been_added_to_the_list;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }

    public static String getLabel(String label) {
        return label + ":"; //$NON-NLS-1$
    }
}
