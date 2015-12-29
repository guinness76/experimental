package org.experimental;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxTester {

    Map map = new HashMap<String, String>();

    public static void main(String[] args) {

        String xml = "<?xml version='1.0' encoding='UTF-8'?>" +
                "<cas:serviceResponse >" +
                "<cas:authenticationSuccess>" +
                "<cas:user>mgross@inductiveautomation.com</cas:user>" +
                "<cas:attributes>" +
                "<cas:forumID>6065</cas:forumID>" +
                "<cas:forum_username>mgross</cas:forum_username>" +
                "<cas:globalID>55</cas:globalID>" +
                "<cas:crmID>43497</cas:crmID>" +
                "<cas:isIntegrator>False</cas:isIntegrator>" +
                "</cas:attributes>" +
                "</cas:authenticationSuccess>" +
                "</cas:serviceResponse>";

        SaxTester tester = new SaxTester();
        Map newMap = tester.extractCustomAttributes(xml);
        for (Object keyObj : newMap.keySet()) {
            Object valObj = newMap.get(keyObj);
            System.out.println(keyObj.toString() + "=" + valObj.toString());

        }

    }

    protected Map<String, Object> extractCustomAttributes(final String xml) {


        SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        SAXParser saxParser = null;

        try {
            saxParser = factory.newSAXParser();
            ServiceHandler handler = new ServiceHandler();
            InputSource is = new InputSource(new StringReader(xml));
            saxParser.parse(is, handler);
        } catch (Exception e) {
            System.out.println("Could not parse response from global login server:" + e.getMessage());
        }

        return map;
    }

    private class ServiceHandler extends DefaultHandler {

        private String currentField;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            String element = qName;

        }

        @Override
        public void characters(char[] ch, int start, int end) {
            currentField = new String(ch, start, end);
            currentField = currentField.trim();
        }

        @Override
        public void endElement(String uri, String localName,
                               String qName) throws SAXException {

            String element = qName;

            if (currentField != null && currentField.length() > 0) {
                map.put(qName, currentField);
                currentField = "";
            }

        }

    }

}
