/*******************************************************************************
 * Copyright (c) 2012-2018 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.resourcemanagement.useraction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import biz.isphere.core.resourcemanagement.InvalidRepositoryVersionException;
import biz.isphere.core.resourcemanagement.filter.RSEProfile;
import biz.isphere.core.resourcemanagement.useraction.DuplicateUserActionException;
import biz.isphere.core.resourcemanagement.useraction.InvalidDomainTypeException;
import biz.isphere.core.resourcemanagement.useraction.MissingNamedTypesException;
import biz.isphere.core.resourcemanagement.useraction.RSEDomain;
import biz.isphere.core.resourcemanagement.useraction.RSEUserAction;
import biz.isphere.core.resourcemanagement.useraction.UserActionXmlComparator;
import biz.isphere.rse.resourcemanagement.AbstractXmlHelper;
import biz.isphere.rse.resourcemanagement.XMLPrettyPrintWriter;
import biz.isphere.rse.resourcemanagement.namedtype.RSENamedTypeHelper;

public class XMLUserActionHelper extends AbstractXmlHelper {

    private static final String CURRENT_VERSION = "1.1.0";
    private static final String MIN_VERSION = "1.1.0";

    private static final String DOMAINS = "domains";
    private static final String DOMAIN = "domain";
    private static final String DOMAIN_NAME = "name";
    private static final String DOMAIN_TYPE = "type";

    private static final String USER_ACTIONS = "userActions";
    private static final String USER_ACTION = "userAction";
    private static final String ORDER = "order";
    private static final String LABEL = "label";
    private static final String ORIGINAL_NAME = "originalName";
    private static final String COMMAND_STRING = "commandString";
    private static final String RUN_ENVIRONMENT = "runEnvironment";
    private static final String PROMPT_FIRST = "promptFirst";
    private static final String REFRESH_AFTER = "refreshAfter";
    private static final String SHOW_ACTION = "showAction";
    private static final String SINGLE_SELECTION = "singleSelection";
    private static final String INVOKE_ONCE = "invokeOnce";
    private static final String VENDOR = "vendor";
    private static final String COMMENT = "comment";
    private static final String FILE_TYPES = "fileTypes";

    public static void saveUserActionsToXML(File toFile, boolean singleDomain, RSEUserAction[] userActions) throws Exception {

        Arrays.sort(userActions, new UserActionXmlComparator());

        XMLPrettyPrintWriter streamWriter = createXMLStreamWriter(toFile);

        streamWriter.writeStartDocument();

        startContainer(streamWriter, CURRENT_VERSION);

        streamWriter.writeStartElement(DOMAINS);

        Map<RSEDomain, List<RSEUserAction>> _pools = new TreeMap<RSEDomain, List<RSEUserAction>>();
        for (int idx = 0; idx < userActions.length; idx++) {
            RSEDomain _domain = userActions[idx].getDomain();
            List<RSEUserAction> _userActions = (List<RSEUserAction>)_pools.get(_domain);
            if (_userActions == null) {
                _userActions = new LinkedList<RSEUserAction>();
                _pools.put(_domain, _userActions);
            }
            _userActions.add(userActions[idx]);
        }

        for (Map.Entry<RSEDomain, List<RSEUserAction>> entry : _pools.entrySet()) {

            streamWriter.writeStartElement(DOMAIN);

            createNode(streamWriter, DOMAIN_TYPE, integerToXml(entry.getKey().getDomainType()));
            createNode(streamWriter, DOMAIN_NAME, entry.getKey().getName());

            RSEUserAction[] _userActions = new RSEUserAction[entry.getValue().size()];
            entry.getValue().toArray(_userActions);
            createUserActions(streamWriter, _userActions);

            streamWriter.writeEndElement();

        }

        streamWriter.writeEndElement();

        endContainer(streamWriter);

        streamWriter.writeEndDocument();

        streamWriter.flush();
        streamWriter.close();

    }

    private static void createUserActions(XMLPrettyPrintWriter streamWriter, RSEUserAction[] userActions) throws XMLStreamException {

        streamWriter.writeStartElement(USER_ACTIONS);

        for (int idx1 = 0; idx1 < userActions.length; idx1++) {

            streamWriter.writeStartElement(USER_ACTION);

            createNode(streamWriter, ORDER, integerToXml(userActions[idx1].getOrder()));
            createNode(streamWriter, LABEL, userActions[idx1].getLabel());
            createNode(streamWriter, ORIGINAL_NAME, userActions[idx1].getOriginalName());
            createNode(streamWriter, COMMAND_STRING, userActions[idx1].getCommandString());
            createNode(streamWriter, RUN_ENVIRONMENT, userActions[idx1].getRunEnvironment());
            createNode(streamWriter, PROMPT_FIRST, userActions[idx1].isPromptFirst());
            createNode(streamWriter, REFRESH_AFTER, userActions[idx1].isRefreshAfter());
            createNode(streamWriter, SHOW_ACTION, userActions[idx1].isShowAction());
            createNode(streamWriter, SINGLE_SELECTION, userActions[idx1].isSingleSelection());
            createNode(streamWriter, INVOKE_ONCE, userActions[idx1].isInvokeOnce());
            createNode(streamWriter, VENDOR, userActions[idx1].getVendor());
            createNode(streamWriter, COMMENT, userActions[idx1].getComment());
            createNode(streamWriter, FILE_TYPES, arrayToXml(userActions[idx1].getFileTypes()));

            streamWriter.writeEndElement();

        }

        streamWriter.writeEndElement();

    }

    public static RSEUserAction[] restoreUserActionsFromXML(File fromFile, boolean singleDomain, RSEProfile profile, RSEDomain domain)
        throws Exception {

        Set<String> keys = new HashSet<String>();

        ArrayList<RSEUserAction> items = new ArrayList<RSEUserAction>();

        XMLEventReader eventReader = null;

        Set<String> missingNamedTypes = new LinkedHashSet<String>();

        try {

            eventReader = createXMLEventReader(fromFile);

            RSEDomain _domain = null;
            RSEUserAction userAction = null;
            StringBuilder elementData = new StringBuilder();

            boolean isValidated = false;
            String versionNumber = null;

            while (eventReader.hasNext()) {

                XMLEvent event = eventReader.nextEvent();

                if (event.isStartElement()) {
                    // Element: Container
                    if (isContainerStartElement(event)) {
                        versionNumber = getVersionNumber(event);
                        isValidated = validateVersionNumber(event, MIN_VERSION);
                    } else
                    // Element: Domain
                    if (event.asStartElement().getName().getLocalPart().equals(DOMAIN)) {
                        _domain = new RSEDomain(profile);
                    } else if (event.asStartElement().getName().getLocalPart().equals(DOMAIN_NAME)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(DOMAIN_TYPE)) {
                        startElementCharacters(elementData, event);
                    } else
                    // Element: Named type
                    // Element: User action
                    if (event.asStartElement().getName().getLocalPart().equals(USER_ACTION)) {
                        userAction = new RSEUserAction();
                        userAction.setDomain(_domain);
                    } else if (event.asStartElement().getName().getLocalPart().equals(ORDER)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(LABEL)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(ORIGINAL_NAME)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(COMMAND_STRING)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(RUN_ENVIRONMENT)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(PROMPT_FIRST)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(REFRESH_AFTER)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(SHOW_ACTION)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(SINGLE_SELECTION)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(INVOKE_ONCE)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(VENDOR)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(COMMENT)) {
                        startElementCharacters(elementData, event);
                    } else if (event.asStartElement().getName().getLocalPart().equals(FILE_TYPES)) {
                        startElementCharacters(elementData, event);
                    } else {
                        clearElementCharacters(elementData);
                    }
                } else if (event.isEndElement()) {
                    if (!isValidated) {
                        throw new InvalidRepositoryVersionException(versionNumber);
                    }
                    if (event.asEndElement().getName().getLocalPart().equals(DOMAIN_NAME)) {
                        // The domain name in the repository is only for humans.
                        // It is the translated NLS name.
                    } else if (event.asEndElement().getName().getLocalPart().equals(DOMAIN_TYPE)) {
                        int domainIndex = xmlToInteger(elementData.toString());
                        _domain.setDomainType(domainIndex);
                        _domain.setName(RSEUserActionHelper.mapDomainName(profile.getName(), domainIndex));
                    } else if (event.asEndElement().getName().getLocalPart().equals(ORDER)) {
                        userAction.setOrder(xmlToInteger(elementData.toString()));
                    } else if (event.asEndElement().getName().getLocalPart().equals(LABEL)) {
                        userAction.setLabel(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(ORIGINAL_NAME)) {
                        userAction.setOriginalName(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(COMMAND_STRING)) {
                        userAction.setCommandString(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(RUN_ENVIRONMENT)) {
                        userAction.setRunEnvironment(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(PROMPT_FIRST)) {
                        userAction.setPromptFirst(xmlToBoolean(elementData.toString(), false));
                    } else if (event.asEndElement().getName().getLocalPart().equals(REFRESH_AFTER)) {
                        userAction.setRefreshAfter(xmlToBoolean(elementData.toString(), false));
                    } else if (event.asEndElement().getName().getLocalPart().equals(SHOW_ACTION)) {
                        userAction.setShowAction(xmlToBoolean(elementData.toString(), true));
                    } else if (event.asEndElement().getName().getLocalPart().equals(SINGLE_SELECTION)) {
                        userAction.setSingleSelection(xmlToBoolean(elementData.toString(), false));
                    } else if (event.asEndElement().getName().getLocalPart().equals(INVOKE_ONCE)) {
                        userAction.setInvokeOnce(xmlToBoolean(elementData.toString(), false));
                    } else if (event.asEndElement().getName().getLocalPart().equals(VENDOR)) {
                        userAction.setVendor(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(COMMENT)) {
                        userAction.setComment(elementData.toString());
                    } else if (event.asEndElement().getName().getLocalPart().equals(FILE_TYPES)) {
                        userAction.setFileTypes(xmlToArray(elementData.toString()));
                    } else if (event.asEndElement().getName().getLocalPart().equals(USER_ACTION)) {

                        if (singleDomain && userAction.getDomain().getDomainType() != domain.getDomainType()) {
                            throw new InvalidDomainTypeException(RSEUserActionHelper.mapDomainName(userAction.getDomain()));
                        }

                        String userActionKey = userAction.getKey();
                        if (keys.contains(userActionKey)) {
                            throw new DuplicateUserActionException();
                        }

                        for (String fileType : userAction.getFileTypes()) {
                            if (!RSENamedTypeHelper.hasNamedType(userAction.getDomain(), fileType)) {
                                missingNamedTypes.add(fileType);
                            }
                        }

                        items.add(userAction);
                        keys.add(userActionKey);
                    }
                    clearElementCharacters(elementData);
                } else {
                    collectElementCharacters(elementData, event);
                }

            }

        } finally {
            if (eventReader != null) {
                eventReader.close();
            }
        }

        if (!missingNamedTypes.isEmpty()) {
            throw new MissingNamedTypesException(missingNamedTypes.toArray(new String[missingNamedTypes.size()]));
        }

        RSEUserAction[] userActions = new RSEUserAction[items.size()];
        items.toArray(userActions);

        return userActions;
    }
}
