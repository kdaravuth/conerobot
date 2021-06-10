/**
 * 
 */
package conerobotprj.dv.lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 * @author khfighter
 *
 */
public class TokenRetrieve {
	// Initialize logger
	private static Logger LOGGER = null;
	static {

		try {

			InputStream configFile = SubscriberRetrieveConstruct.class.getResourceAsStream("/config/logger.cfg");
			LogManager.getLogManager().readConfiguration(configFile);
			LOGGER = Logger.getLogger(PaymentReversalConstruct.class.getName());
		} catch (IOException e) {
			e.getMessage();
		}

	}
	// End initialize logger
	// Sapi Credential retrieval
	public static String realm = "SAPI";
	public static String username = "bulkprov";
	public static String password = "P@ssw0rd";

	// End sapi credential retrieval
	// Create SOAP for Primary offer Swapping
	public static void createSETokenRetrieve(SOAPMessage soapMessage, String realm, String username, String password) throws SOAPException {

		// SOAP Envelope
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "auth";
		String myNamespaceURI = "https://org.comverse.rtbd.sec/webservice/auth";
		
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement proxyLogin = c1soapBody.addChildElement("proxyLogin", myNamespace);
		
		SOAPElement String_1 = proxyLogin.addChildElement("String_1");
		String_1.addTextNode(username);
		
		SOAPElement String_2 = proxyLogin.addChildElement("String_2");
		String_2.addTextNode(password);
		
		SOAPElement String_3 = proxyLogin.addChildElement("String_3");
		String_3.addTextNode(realm);
		
		
	}// End create Extended Data add envelope

	
	// Create SOAP request
	public static SOAPMessage createSRTokenRetrieve(String soapAction, String realm, String username, String password) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSETokenRetrieve(soapMessage, realm, username, password);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		
		//LOGGER.log(Level.INFO, "Request SOAP Message -->" + soapMessage.getContentDescription());
		
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.INFO, "Request SOAP Message -->" + message);

		return soapMessage;
	}
	// End create soap request

	// Call primary offer swap
	public void callLRTokenRetrieve(String realm, String username, String password) {

		try {

	
			String soapEndpointUrl = "https://secserv.c1.btc.bw:8443/SAMLSignOnWS";
			String soapAction = "https://secserv.c1.btc.bw:8443/SAMLSignOnWS?wsdl";
			HttpsURLConnection httpsConnection = null;
			
		

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Retrieving Token");
			
			// Create SSL context and trust all certificates
			SSLContext sslContext = SSLContext.getInstance("SSL");
			TrustAllCertificates ta = new TrustAllCertificates() {
				
				@Override
				public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws CertificateException {
					// TODO Auto-generated method stub
					
				}
			};
			TrustManager[] trustAll = new TrustManager[] {ta};
			sslContext.init(null, trustAll, new java.security.SecureRandom());
			// Set trust all certificates context to HttpsURLConnection
			HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
			URL url = new URL(soapEndpointUrl);
			httpsConnection = (HttpsURLConnection) url.openConnection();
			// Trust all hosts
			httpsConnection.setHostnameVerifier(new TrustAllHosts());
			// Connect
			httpsConnection.connect();
		
			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			
			SOAPMessage soapResponse = soapConnection
					.call(createSRTokenRetrieve(soapAction, realm, username, password), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP -->" + soapResponse.getSOAPBody().getTextContent());
			LOGGER.log(Level.FINEST, "Response SOAP -->" + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/TokenRetrieveResponse.xml")));
			soapConnection.close();
			httpsConnection.disconnect();

			File xmlresponse = new File("src/input/TokenRetrieveResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName(firstChild.getNodeName());
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			// LOGGER.log(Level.INFO, "Childnodes " + childnodes.item(0).getNodeName());

			if (childnodes.item(0).getNodeName().equals("S:Body")) {

				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.SEVERE, "RESULT FAIL: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				NodeList outputSubInfoList = doc.getElementsByTagName("result");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				String tempSubInfo = "";

				LOGGER.log(Level.INFO, "RESULT SUCCESS: " + childnodesSubInfo.item(0));
			}

			//LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + SourceMSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.log(Level.SEVERE, e.getMessage());
			//LOGGER.log(Level.INFO, "RESULT FAIL: " + SourceMSISDN);
			//LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + SourceMSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

}
