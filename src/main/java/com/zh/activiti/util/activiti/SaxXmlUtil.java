package com.zh.activiti.util.activiti;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rebybyx on 2017/4/20.
 */
public class SaxXmlUtil extends DefaultHandler {

    private Map<String, String> retMap = null;

    public Map<String, String> getDiagramName(InputStream xmlStream) {
        SaxXmlUtil handler = new SaxXmlUtil();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(xmlStream, handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return handler.getRetMap();
    }

    public Map<String, String> getRetMap() {
        return retMap;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("process".equals(qName)) {
            retMap = new HashMap<>();
            String name = attributes.getValue("name");
            String nameArr[] = name.split("-");
            String diagramName = nameArr[0];
            String diagramCategory = "";
            if (nameArr.length > 1) {
                diagramCategory = nameArr[1];
            } else {
                diagramCategory = nameArr[0];
            }
            retMap.put("name", diagramName);
            retMap.put("category", diagramCategory);
            System.out.println(attributes.getValue("name"));
        }
    }

}
