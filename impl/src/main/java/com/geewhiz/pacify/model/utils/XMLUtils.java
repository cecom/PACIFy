/*-
 * ========================LICENSE_START=================================
 * com.geewhiz.pacify.impl
 * %%
 * Copyright (C) 2011 - 2017 Sven Oppermann
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package com.geewhiz.pacify.model.utils;

import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

import com.geewhiz.pacify.defect.DefectRuntimeException;
import com.geewhiz.pacify.model.PMarker;

public class XMLUtils {

    private static final String SLASH                         = "/";
    private static final String XPATH_ELEMENTS_WITH_NO_CHILDS = "//*[not(*) and not (self::Property)]";

    private Logger              logger                        = LogManager.getLogger();

    private PMarker             pMarker;
    private Document            document;
    private XPath               xpathSearch;

    public XMLUtils(PMarker pMarker) {
        this.pMarker = pMarker;

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        xpathSearch = XPathFactory.newInstance().newXPath();

        try {
            document = documentBuilderFactory.newDocumentBuilder().parse(pMarker.getFile());
        } catch (Exception e) {
            logger.debug(e);
            throw new DefectRuntimeException("Error while processing xml document [" + pMarker.getFile().getAbsolutePath() + "]");
        }

    }

    public void deleteExistingElement(String xpath) {
        try {
            String parentXPath = getParentXPath(xpath);

            Node parentNode = (Node) xpathSearch.evaluate(parentXPath, document, XPathConstants.NODE);
            Node childNode = (Node) xpathSearch.evaluate(xpath, document, XPathConstants.NODE);

            parentNode.removeChild(childNode);
        } catch (Exception e) {
            logger.debug(e);
            throw new DefectRuntimeException("Could not find xPath [" + xpath + "] in file [" + pMarker.getFile() + "]");
        }
    }

    public void removeEntriesWithoutChilds() {
        while (removeEntries()) {
            // do until we don't find any more.
        }
    }

    public void writeDocument() {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            document.setXmlStandalone(true);
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(pMarker.getFile());

            transformer.transform(source, result);
        } catch (Exception e) {
            logger.debug(e);
            throw new DefectRuntimeException("Error while writing xml document.");
        }
    }

    private String getParentXPath(String xpath) {
        if (StringUtils.isEmpty(xpath) || xpath.lastIndexOf(SLASH) <= 0) {
            return null;
        }
        return xpath.substring(0, xpath.lastIndexOf(SLASH));
    }

    private boolean removeEntries() {
        NodeList result;
        try {
            result = (NodeList) xpathSearch.evaluate(XPATH_ELEMENTS_WITH_NO_CHILDS, document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            logger.debug(e);
            throw new DefectRuntimeException("Could not find elements which dont have a child.");
        }

        for (int i = 0; i < result.getLength(); i++) {
            Node nodeToDelete = result.item(i);
            Node parentNode = nodeToDelete.getParentNode();
            parentNode.removeChild(nodeToDelete);
        }
        return result.getLength() > 0;
    }

}
