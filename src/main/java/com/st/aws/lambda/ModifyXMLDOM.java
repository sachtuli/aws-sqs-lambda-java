package com.st.aws.lambda;

import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ModifyXMLDOM {
    private static final String XML_FILENAME = "sample_message.xml";
    private static final CloseableHttpClient httpClient = HttpClients.createDefault();
    private static final String url = "bin/5fad1416-be50-4f81-bbe3-dd1cc1db002c";
    private static final String username = "admin";
    private static final String password = "admin";

    static ModifyXMLDOM obj = new ModifyXMLDOM();

    private InputStream getFileFromResourceAsStream() {

        // The class loader that loaded the class
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(ModifyXMLDOM.XML_FILENAME);

        // the stream holding the file content
        if (inputStream == null) {
            throw new IllegalArgumentException("file not found! " + ModifyXMLDOM.XML_FILENAME);
        } else {
            return inputStream;
        }

    }

    private static void printInputStream(InputStream is) {

        try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean XmlProcessor(@NotNull String relationshipId, String geoList) {
        boolean result = true;
        System.out.println("getResourceAsStream : " + XML_FILENAME);
        try (InputStream is = obj.getFileFromResourceAsStream()) {
            DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
            DocumentBuilder b = f.newDocumentBuilder();
            Document doc = b.parse(is);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//parameters/parameter[key='message']/value/entry[key='geoList" +
                    "']/value");
            NodeList nodes = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            System.out.println(nodes.item(0).getTextContent());
            nodes.item(0).setTextContent(geoList);

            XPathExpression expr1 = xpath.compile("//parameters/parameter[key='message']/value/entry[key" +
                    "='relationshipId']/value");
            NodeList nodes1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
            System.out.println(nodes1.item(0).getTextContent());
            nodes1.item(0).setTextContent(relationshipId);

            // writing the output to console or file
            boolean res = writeXml(doc);
            if (res) {
                System.out.println("Process completed successfully");
            }
        } catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException |
                TransformerException | AuthenticationException | URISyntaxException e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    private static boolean writeXml(Document doc) throws TransformerException, IOException, AuthenticationException,
            URISyntaxException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(writer);
        StreamResult result1 = new StreamResult(new File("final_message.xml"));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(source, result);
        transformer.transform(source, result1);
        String xmlString = writer.getBuffer().toString();
        System.out.println(xmlString);
        if (xmlString.isEmpty()) {
            System.out.println("Invalid XML file ");
            return false;
        }
        System.out.println("XML file updated successfully");
        sendPost(xmlString);
        return true;
    }

    private static void sendPost(String xml) throws IOException, URISyntaxException {
        String authToken = username + password;
        String authorizationHeader = "Basic" + authToken;
        URI uri = new URIBuilder().setScheme("https")
                .setHost("mockbin.org").setPath(url).setParameter("foo", "bar").setParameter("foo",
                        "baz").build();
        HttpPost post = new HttpPost(uri);
        System.out.println(post.getURI());
        post.setHeader("Authorization", authorizationHeader);
        // add request parameter, form parameters
        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("xml", xml));
        post.setEntity(new UrlEncodedFormEntity(urlParameters, "UTF-8"));

        try (CloseableHttpResponse response = httpClient.execute(post)) {
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + post.getEntity());
            System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
            System.out.println(EntityUtils.toString(response.getEntity()));
        } finally {
            httpClient.close();
        }

    }
}
