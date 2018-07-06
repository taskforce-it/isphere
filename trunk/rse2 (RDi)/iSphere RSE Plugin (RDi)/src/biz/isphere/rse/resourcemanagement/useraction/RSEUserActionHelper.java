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
        SystemUDActionManager userActionManager = getUserActionManager(systemProfile);

        String[] domainNames = userActionManager.getActionSubSystem().getDomainNames();
        for (String domainName : domainNames) {
            int domainIndex = userActionManager.getActionSubSystem().mapDomainName(domainName);
            rseDomains.add(produceDomain(rseProfile, domainIndex, domainName));
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
            systemUserAction.getVendor(), systemUserAction.getOriginalName(), systemUserAction);

        return rseUserAction;
    }

    public static void createUserAction(RSEDomain rseDomain, String label, String commandString, boolean isPromptFirst, boolean isRefreshAfter,
        boolean isShowAction, boolean isSingleSelection, boolean isInvokeOnce, String comment, String[] fileTypes, boolean isIBM, String vendor) {

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement userAction = userActionManager.addAction(systemProfile, label, rseDomain.getDomainType());
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

    public static void updateUserAction(RSEDomain rseDomain, String label) {

        ISystemProfile systemProfile = getSystemProfile(rseDomain.getProfile().getName());
        if (systemProfile != null) {
            SystemUDActionManager userActionManager = getUserActionManager(systemProfile);
            if (userActionManager != null) {
                SystemUDActionElement[] userActions = userActionManager.getActions(new Vector(), systemProfile, rseDomain.getDomainType());
                for (SystemUDActionElement userAction : userActions) {
                    if (userAction.getLabel().equals(label)) {

                        saveUserActions(userActionManager, systemProfile);
                    }
                }
            }
        }
    }

    private static void saveUserActions(SystemUDActionManager userActionManager, ISystemProfile systemProfile) {

        userActionManager.setChanged(systemProfile);
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
