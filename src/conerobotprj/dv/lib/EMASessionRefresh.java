package conerobotprj.dv.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
import org.w3c.dom.NodeList;

public class EMASessionRefresh {
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
	public static String SequenceId = "";
	public static String TransactionId = "";
	public static String SessionId = "";

	// Create SOAP
	public static void createSOAPGetEMASession(SOAPMessage soapMessage, String useridpassword) throws SOAPException {

		// SOAP Envelope

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespaceCai3 = "cai3";
		String myNamespaceURICai3 = "http://schemas.ericsson.com/cai3g1.2/";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespaceCai3, myNamespaceURICai3);

		SOAPBody emasoapBody = envelope.getBody();
		SOAPElement Login = emasoapBody.addChildElement("Login", myNamespaceCai3);

		SOAPElement userId = Login.addChildElement("userId", myNamespaceCai3);
		userId.addTextNode(useridpassword);

		SOAPElement pwd = Login.addChildElement("pwd", myNamespaceCai3);
		pwd.addTextNode(useridpassword);

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRGetEMASession(String soapAction, String useridpassword) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSOAPGetEMASession(soapMessage, useridpassword);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "Request SOAP Message -->" + message);
		return soapMessage;
	}
	// End create soap request

	public void callGetEMASession(String useridpassword) {

		try {

			String soapEndpointUrl = "http://10.11.0.50:8998/cai3g1_2/SessionControl/";
			String soapAction = "http://10.11.0.50:8998/cai3g1_2/SessionControl/?wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Opening session to EMA");
			SOAPMessage soapResponse = soapConnection.call(createSRGetEMASession(soapAction, useridpassword),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/NewSessionResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			// LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + imsi);

			File xmlresponse = new File("src/input/NewSessionResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			// doc.getDocumentElement().normalize();
			// Node firstChild = doc.getFirstChild(); // get first child to list through
			// other elements

			NodeList vsession = doc.getElementsByTagName("sessionId");
			Element sessionv = (Element) vsession.item(0);
			SessionId = sessionv.getTextContent();

			NodeList vsequence = doc.getElementsByTagName("baseSequenceId");
			Element sequencev = (Element) vsequence.item(0);
			SequenceId = sequencev.getTextContent();

			TransactionId = SequenceId;

			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
