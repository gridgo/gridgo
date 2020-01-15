package io.gridgo.framework.support.impl;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import io.gridgo.framework.support.Registry;
import io.gridgo.framework.support.exceptions.RegistryException;

public class XmlRegistry implements Registry {

    private Document document;

    private XPath xPath = XPathFactory.newInstance().newXPath();

    public XmlRegistry(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        var builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        var builder = builderFactory.newDocumentBuilder();
        this.document = builder.parse(is);
    }

    @Override
    public Object lookup(String name) {
        try {
            var node = (Node) xPath.compile(name).evaluate(document, XPathConstants.NODE);
            if (node == null)
                return null;
            return node.getTextContent();
        } catch (XPathExpressionException e) {
            throw new RegistryException("Cannot lookup key: " + name, e);
        }
    }

    @Override
    public Registry register(String name, Object answer) {
        return this;
    }

    public static XmlRegistry ofFile(String file) {
        try (var stream = new FileInputStream(file)) {
            return ofStream(stream);
        } catch (IOException e) {
            throw new RegistryException(e);
        }
    }

    public static XmlRegistry ofResource(String resource) {
        var classloader = Thread.currentThread().getContextClassLoader();
        try (var stream = classloader.getResourceAsStream(resource)) {
            return ofStream(stream);
        } catch (IOException e) {
            throw new RegistryException(e);
        }
    }

    public static XmlRegistry ofStream(InputStream stream) {
        try {
            return new XmlRegistry(stream);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            throw new RegistryException(e);
        }
    }
}
