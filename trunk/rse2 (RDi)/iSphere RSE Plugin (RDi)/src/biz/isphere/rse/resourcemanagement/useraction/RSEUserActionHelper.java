/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.useraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

import org.eclipse.rse.core.model.ISystemProfile;
import org.eclipse.rse.core.model.SystemStartHere;
import org.eclipse.rse.core.subsystems.ISubSystemConfiguration;
import org.eclipse.rse.internal.useractions.ui.uda.SystemUDActionElement;
import org.eclipse.rse.internal.useractions.ui.uda.SystemUDActionManager;
import org.eclipse.rse.internal.useractions.ui.uda.SystemUDActionSubsystem;

import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.core.resourcemanagement.useraction.RSEDomain;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.rse.resourcemanagement.AbstractSystemHelper;

import com.ibm.etools.iseries.rse.ui.uda.QSYSUDActionSubsystemAdapter;

@SuppressWarnings("restriction")
public class RSEUserActionHelper extends AbstractSystemHelper {

    public static RSEDomain[] getDomains(RSEProfile rseProfile) {

        ArrayList<RSEDomain> rseDomains = new ArrayList<RSEDomain>();

        ISystemProfile systemProfile = getSystemProfile(rseProfile.getName());
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

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    RSEUserAction rseUserAction = produceUserAction(rseDomain, userAction);
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

        RSEUserAction rseUserAction = new RSEUserAction(domain, systemUserAction.getLabel(), systemUserAction.getCommand(),
            systemUserAction.getPrompt(), systemUserAction.getRefresh(), systemUserAction.getShow(), systemUserAction.getSingleSelection(),
            systemUserAction.getCollect(), systemUserAction.getComment(), systemUserAction.getFileTypes(), systemUserAction.isIBM(),
            systemUserAction.getVendor(), systemUserAction.getOriginalName(), systemUserAction.getOrder(), systemUserAction);

        return rseUserAction;
    }

    public static void createUserAction(RSEDomain rseDomain, String label, String commandString, boolean isPromptFirst, boolean isRefreshAfter,
        boolean isShowAction, boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes, boolean isIBM, String vendor,
        int order) {

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {

                int newOrder = getNextOrderNumber(userActionManager, rseDomain);

                SystemUDActionElement userAction = userActionManager.addAction(systemProfile, label, rseDomain.getDomainType());

                // Do not set 'order' to avoid duplicate
                // order numbers
                userAction.setOrder(newOrder);
                userAction.setCommand(commandString);
                userAction.setPrompt(isPromptFirst);
                userAction.setRefresh(isRefreshAfter);
                userAction.setShow(isShowAction);
                userAction.setSingleSelection(isSingleSelection);
                userAction.setCollect(isInvokeOnce);
                userAction.setComment(comment);
                userAction.setFileTypes(fileTypes);
                // must be called before setVendor()
                userAction.setIBM(isIBM);
                userAction.setVendor(vendor);

                moveUserActionTo(userActionManager, userAction, order);

                saveUserActions(userActionManager, systemProfile);
            }
        }
    }

    private static void moveUserActionTo(SystemUDActionManager userActionManager, SystemUDActionElement userAction, int order) {

        SystemUDActionElement[] actions = userActionManager.getActions(new Vector<Object>(), userAction.getProfile(), userAction.getDomain());
        if (actions == null || actions.length == 0) {
            return;
        }

        Arrays.sort(actions, new Comparator<SystemUDActionElement>() {
            public int compare(SystemUDActionElement arg0, SystemUDActionElement arg1) {

                if (arg0 == null && arg1 == null) {
                    return 0;
                } else if (arg0 == null) {
                    return -1;
                } else if (arg1 == null) {
                    return 1;
                }

                if (arg0.getOrder() > arg1.getOrder()) {
                    return 1;
                } else if (arg0.getOrder() < arg1.getOrder()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        int i = actions.length - 1;
        while (i >= 0 && userAction.getOrder() > order) {
            userActionManager.moveElementUp(userAction);
        }
    }

    private static int getNextOrderNumber(SystemUDActionManager userActionManager, RSEDomain rseDomain) {

        int lastOrderNumber = -1;

        RSEUserAction[] actions = getUserActions(rseDomain);
        if (actions.length == 0) {
            return 0;
        }

        for (RSEUserAction action : actions) {
            lastOrderNumber = Math.max(lastOrderNumber, action.getOrder());
        }

        lastOrderNumber++;

        return lastOrderNumber;
    }

    public static void deleteUserAction(RSEDomain rseDomain, String label) {

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
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

    public static void updateUserAction(RSEDomain rseDomain, String label, String commandString, boolean isPromptFirst, boolean isRefreshAfter,
        boolean isShowAction, boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes, boolean isIBM, String vendor,
        int order) {

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    if (userAction.getLabel().equals(label)) {

                        // Do not update 'order' to avoid duplicate
                        // order numbers
                        // userAction.setOrder(order);
                        userAction.setCommand(commandString);
                        userAction.setPrompt(isPromptFirst);
                        userAction.setRefresh(isRefreshAfter);
                        userAction.setShow(isShowAction);
                        userAction.setSingleSelection(isSingleSelection);
                        userAction.setCollect(isInvokeOnce);
                        userAction.setComment(comment);
                        userAction.setFileTypes(fileTypes);
                        // must be called before setVendor()
                        userAction.setIBM(isIBM);
                        userAction.setVendor(vendor);

                        saveUserActions(userActionManager, systemProfile);
                    }
                }
            }
        }
    }

    public static boolean hasUserActionManager(RSEProfile rseProfile) {

        ISystemProfile systemProfile = (ISystemProfile)rseProfile.getOrigin();

        if (getUserActionManager(systemProfile) != null) {
            return true;
        }

        return false;
    }

    private static void saveUserActions(SystemUDActionManager userActionManager, ISystemProfile systemProfile) {

        userActionManager.saveUserData(systemProfile);
    }

    private static ISystemProfile getSystemProfile(String name) {
        return SystemStartHere.getSystemRegistry().getSystemProfile(name);
    }

    private static SystemUDActionManager getUserActionManager(ISystemProfile systemProfile) {

        ISubSystemConfiguration subSystemFactory = getSubSystemConfiguration();
        SystemUDActionSubsystem udactionSubSystem = new QSYSUDActionSubsystemAdapter().getSystemUDActionSubsystem(subSystemFactory);
        udactionSubSystem.setSubSystemFactory(subSystemFactory);
        SystemUDActionManager userActionManager = udactionSubSystem.getUDActionManager();
        userActionManager.setCurrentProfile(systemProfile);

        return userActionManager;
    }

}
