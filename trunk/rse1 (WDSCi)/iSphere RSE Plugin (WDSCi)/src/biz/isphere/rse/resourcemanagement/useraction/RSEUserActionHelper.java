/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.useraction;

import java.util.ArrayList;
import java.util.Vector;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.core.resourcemanagement.useraction.RSEDomain;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.rse.resourcemanagement.AbstractSystemHelper;

import com.ibm.etools.iseries.core.ui.uda.UDActionSubsystemNFS;
import com.ibm.etools.systems.as400filesubsys.FileSubSystem;
import com.ibm.etools.systems.core.ui.uda.SystemUDActionElement;
import com.ibm.etools.systems.core.ui.uda.SystemUDActionManager;
import com.ibm.etools.systems.model.SystemConnection;
import com.ibm.etools.systems.model.SystemProfile;
import com.ibm.etools.systems.model.SystemStartHere;
import com.ibm.etools.systems.subsystems.SubSystem;
import com.ibm.etools.systems.subsystems.SubSystemFactory;

@SuppressWarnings("restriction")
public class RSEUserActionHelper extends AbstractSystemHelper {

    private static final String UA_ATTR_RUNENV = "RunEnv";
    private static final String UA_RUNENV_NORMAL = "normal";
    private static final String UA_RUNENV_BATCH = "batch";
    private static final String UA_RUNENV_INTERACTIVE = "interactive";

    public static RSEDomain[] getDomains(RSEProfile rseProfile) {

        ArrayList<RSEDomain> rseDomains = new ArrayList<RSEDomain>();

        SystemProfile systemProfile = getSystemProfile(rseProfile.getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);

            if (userActionManager != null) {
                String[] domainNames = userActionManager.getActionSubSystem().getDomainNames();
                for (String domainName : domainNames) {
                    int domainIndex = userActionManager.getActionSubSystem().mapDomainName(domainName);
                    rseDomains.add(produceDomain(rseProfile, domainIndex, domainName));
                }
            }
        }

        return rseDomains.toArray(new RSEDomain[rseDomains.size()]);
    }

    public static RSEUserAction[] getUserActions(RSEProfile rseProfile) {

        ArrayList<RSEUserAction> allUserActions = new ArrayList<RSEUserAction>();

        RSEDomain[] domains = getDomains(rseProfile);
        for (int idx1 = 0; idx1 < domains.length; idx1++) {
            RSEUserAction[] userActions = getUserActions(domains[idx1]);
            for (int idx2 = 0; idx2 < userActions.length; idx2++) {
                allUserActions.add(userActions[idx2]);
            }
        }

        RSEUserAction[] _userActions = new RSEUserAction[allUserActions.size()];
        allUserActions.toArray(_userActions);

        return _userActions;
    }

    public static RSEUserAction[] getUserActions(RSEDomain rseDomain) {

        ArrayList<RSEUserAction> rseUserActions = new ArrayList<RSEUserAction>();

        SystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    RSEUserAction rseUserAction = produceUserAction(rseDomain, userAction);
                    // Generate order number from list size for WDSCi
                    rseUserAction.setOrder(rseUserActions.size());
                    rseUserActions.add(rseUserAction);
                }
            }
        }

        return rseUserActions.toArray(new RSEUserAction[rseUserActions.size()]);
    }

    public static RSEUserAction getUserAction(RSEDomain rseDomain, String label) {

        RSEUserAction[] rseUserActions = getUserActions(rseDomain);
        for (RSEUserAction rseUserAction : rseUserActions) {
            if (rseUserAction.getLabel().equals(label)) {
                return rseUserAction;
            }
        }

        return null;
    }

    private static RSEDomain produceDomain(RSEProfile rseProfile, int domain, String name) {
        return new RSEDomain(rseProfile, domain, name);
    }

    private static RSEUserAction produceUserAction(RSEDomain domain, SystemUDActionElement systemUserAction) {

        RSEUserAction rseUserAction = new RSEUserAction(domain, systemUserAction.getLabel(), systemUserAction.getCommand(), systemUserAction
            .getAttribute(UA_ATTR_RUNENV, UA_RUNENV_NORMAL), systemUserAction.getPrompt(), systemUserAction.getRefresh(), systemUserAction.getShow(),
            systemUserAction.getSingleSelection(), systemUserAction.getCollect(), systemUserAction.getComment(), systemUserAction.getFileTypes(),
            systemUserAction.getVendor(), systemUserAction.getOriginalName(), 0, systemUserAction);

        return rseUserAction;
    }

    public static void createUserAction(RSEDomain rseDomain, String label, String commandString, String runEnvironment, boolean isPromptFirst,
        boolean isRefreshAfter, boolean isShowAction, boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes,
        String vendor, int order) {

        SystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {

                SystemUDActionElement userAction = userActionManager.addAction(systemProfile, label, rseDomain.getDomainType());
                // userAction.setOrder(getNextOrderNumber(userActionManager,
                // rseDomain));

                setUserActionAttributes(commandString, runEnvironment, isPromptFirst, isRefreshAfter, isShowAction, isSingleSelection, isInvokeOnce,
                    comment, fileTypes, vendor, userAction);
                userAction.setVendor(vendor);

                // moveUserActionTo(userActionManager, userAction, order);

                saveUserActions(userActionManager, systemProfile);
            }
        }
    }

    public static void deleteUserAction(RSEDomain rseDomain, String label) {

        SystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    if (userAction.getLabel().equals(label)) {
                        userActionManager.delete(systemProfile, userAction);
                        saveUserActions(userActionManager, systemProfile);
                    }
                }
            }
        }
    }

    public static void updateUserAction(RSEDomain rseDomain, String label, String commandString, String runEnvironment, boolean isPromptFirst,
        boolean isRefreshAfter, boolean isShowAction, boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes,
        String vendor, int order) {

        SystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    if (userAction.getLabel().equals(label)) {

                        setUserActionAttributes(commandString, runEnvironment, isPromptFirst, isRefreshAfter, isShowAction, isSingleSelection, isInvokeOnce, comment,
                            fileTypes, vendor, userAction);

                        // moveUserActionTo(userActionManager, userAction,
                        // order);

                        saveUserActions(userActionManager, systemProfile);
                    }
                }
            }
        }
    }

    public static boolean hasUserActionManager(RSEProfile rseProfile) {

        SystemProfile systemProfile = (SystemProfile)rseProfile.getOrigin();

        if (getUserActionManager(systemProfile) != null) {
            return true;
        }

        return false;
    }

    private static void setUserActionAttributes(String commandString, String runEnvironment, boolean isPromptFirst, boolean isRefreshAfter, boolean isShowAction,
        boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes, String vendor, SystemUDActionElement userAction) {

        userAction.setCommand(commandString);
        userAction.setAttribute(UA_ATTR_RUNENV, runEnvironment);
        userAction.setPrompt(isPromptFirst);
        userAction.setRefresh(isRefreshAfter);
        userAction.setShow(isShowAction);
        userAction.setSingleSelection(isSingleSelection);
        userAction.setCollect(isInvokeOnce);
        userAction.setComment(comment);
        userAction.setFileTypes(fileTypes);
    }

    private static void saveUserActions(SystemUDActionManager userActionManager, SystemProfile systemProfile) {

        userActionManager.saveUserData(systemProfile);
    }

    private static SystemProfile getSystemProfile(String name) {
        return SystemStartHere.getSystemRegistry().getSystemProfile(name);
    }

    private static SystemUDActionManager getUserActionManager(SystemProfile systemProfile) {

        for (SystemConnection connection : systemProfile.getConnections()) {
            if ("iSeries".equals(connection.getSystemType())) {
                for (SubSystem subSystem : connection.getSubSystems()) {
                    if (subSystem instanceof FileSubSystem) {
                        return subSystem.getUDActionSubsystem().getUDActionManager();
                    }
                }
            }
        }

        SubSystemFactory subSystemFactory = getSubSystemConfiguration();
        UDActionSubsystemNFS actionSubsystemNFS = new UDActionSubsystemNFS();
        actionSubsystemNFS.setSubSystemFactory(subSystemFactory);
        SystemUDActionManager userActionManager = actionSubsystemNFS.getUDActionManager();
        userActionManager.setCurrentProfile(systemProfile);

        return userActionManager;
    }

}
