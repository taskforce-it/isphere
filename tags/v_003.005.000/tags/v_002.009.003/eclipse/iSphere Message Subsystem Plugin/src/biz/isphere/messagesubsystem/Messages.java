/*******************************************************************************
 * Copyright (c) 2012-2015 iSphere Project Owners
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

    public static String iSeries_Message;

    public static String Severity_colon;

    public static String Date_sent_colon;

    public static String From_colon;

    public static String Message_colon;

    public static String Reply_colon;

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

    public static String Collect_informational_messages_on_startup;

    public static String Collect_informational_messages_on_startup_tooltip;
    
    public static String Queued_Messages_title;

    public static String Previous_request_is_still_pending;

    public static String One_or_more_messages_could_not_be_removed;

    public static String Please_enter_a_value;
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
