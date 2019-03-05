package com.sph.healthtrac.common;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFunctions {

    public static Document XMLfromString(String xml){

        Document doc;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is);

        } catch (ParserConfigurationException e) {

            System.out.println("XML parse error: " + e.getMessage());
            return null;

        } catch (SAXException e) {

            System.out.println("Wrong XML file structure: " + e.getMessage());
            return null;

        } catch (IOException e) {

            System.out.println("I/O exeption: " + e.getMessage());
            return null;
        }

        return doc;

    }

    /** Returns element value
     * @param elem element (it is XML tag)
     * @return Element value otherwise empty String
     */

    public static String getElementValue( Node elem ) {

        Node kid;

        if( elem != null){

            if (elem.hasChildNodes()){

                for( kid = elem.getFirstChild(); kid != null; kid = kid.getNextSibling() ){

                    if( kid.getNodeType() == Node.TEXT_NODE  ){

                        return kid.getNodeValue();
                    } else if(kid.getNodeType() == Node.CDATA_SECTION_NODE){
                        CharacterData cd = (CharacterData) kid;
                        return cd.getData();
                    }
                }
            }
        }

        return "";
    }

	 /*
	 public static String getXML(){
			String line = null;

			try {

				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://p-xr.com/xml");

				HttpResponse httpResponse = httpClient.execute(httpPost);
				HttpEntity httpEntity = httpResponse.getEntity();
				line = EntityUtils.toString(httpEntity);

			} catch (UnsupportedEncodingException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			} catch (MalformedURLException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			} catch (IOException e) {
				line = "<results status=\"error\"><msg>Can't connect to server</msg></results>";
			}

			return line;

	}

	public static int numResults(Document doc){
		Node results = doc.getDocumentElement();
		int res = -1;

		try{
			res = Integer.valueOf(results.getAttributes().getNamedItem("count").getNodeValue());
		}catch(Exception e ){
			res = -1;
		}

		return res;
	}
	*/

    public static String getValue(Element item, String str) {

        NodeList n = item.getElementsByTagName(str);

        return XMLFunctions.getElementValue(n.item(0));
    }

    public static Boolean tagExists(Element item, String str) {

        NodeList n = item.getElementsByTagName(str);

        return n.item(0) != null;
    }
}
