package com.seblong.okr.utils;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @description 解析xml字符串
 */
public class XmlUtil {
    public static Document parseXmlString(String xmlStr){

        try{
            InputSource is = new InputSource(new StringReader(xmlStr));
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder=factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            return doc;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String,Object> getXmlBodyContext(String bodyXml){

        Map<String,Object> dataMap = new HashMap<String,Object>();

        Document doc = parseXmlString(bodyXml);
        if(null != doc){
            NodeList rootNode = doc.getElementsByTagName("xml");
            if(rootNode != null){

                Node root = rootNode.item(0);
                NodeList nodes = root.getChildNodes();
                for(int i = 0;i < nodes.getLength(); i++){
                    Node node = nodes.item(i);
                    dataMap.put(node.getNodeName(), node.getTextContent());
                }
            }
        }
        return dataMap;
    }

}