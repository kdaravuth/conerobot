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
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class EMAHSSDisconnection {
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
	public static void createSOAPHSSDisconnection(SOAPMessage soapMessage, String imsi) throws SOAPException {

		// Initialize session
		EMASessionRefresh session = new EMASessionRefresh();
		session.callGetEMASession("sogadm");
		SequenceId = session.SequenceId;
		SessionId = session.SessionId;
		TransactionId = session.TransactionId;

		// SOAP Envelope
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespaceCai3 = "cai3";
		String myNamespaceURICai3 = "http://schemas.ericsson.com/cai3g1.2/";
		String myNamespaceltes = "ltes";
		String myNamespaceURIltes = "http://schemas.ericsson.com/ema/UserProvisioning/LteSub/";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespaceCai3, myNamespaceURICai3);
		envelope.addNamespaceDeclaration(myNamespaceltes, myNamespaceURIltes);

		SOAPHeader emaHeader = envelope.getHeader();
		SOAPElement SequenceId = emaHeader.addChildElement("SequenceId", myNamespaceCai3);
		SequenceId.addTextNode(EMAHSSDisconnection.SequenceId);

		SOAPElement TransactionIdd = emaHeader.addChildElement("TransactionId", myNamespaceCai3);
		TransactionIdd.addTextNode(EMAHSSDisconnection.TransactionId);

		SOAPElement SessionIdd = emaHeader.addChildElement("SessionId", myNamespaceCai3);
		SessionIdd.addTextNode(EMAHSSDisconnection.SessionId);

		SOAPBody emasoapBody = envelope.getBody();
		SOAPElement Delete = emasoapBody.addChildElement("Delete", myNamespaceCai3);
		SOAPElement MOType = Delete.addChildElement("MOType", myNamespaceCai3);
		MOType.addTextNode("Subscription@http://schemas.ericsson.com/ema/UserProvisioning/LteSub/");

		SOAPElement MOId = Delete.addChildElement("MOId", myNamespaceCai3);
		SOAPElement MOIdImsi = MOId.addChildElement("imsi", myNamespaceltes);
		MOIdImsi.addTextNode(imsi);

		SOAPElement MOAttributes = Delete.addChildElement("MOAttributes", myNamespaceCai3);
		SOAPElement createSubscription = MOAttributes.addChildElement("deleteSubscription", myNamespaceltes);
		createSubscription.setAttribute("imsi", imsi);

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRHSSDisconnection(String soapAction, String imsi) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSOAPHSSDisconnection(soapMessage, imsi);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.INFO, "Request SOAP Message -->" + message);
		return soapMessage;
	}
	// End create soap request

	public void callHSSDisconnection(String imsi) {

		try {

			String soapEndpointUrl = "http://10.11.0.50:8998/cai3g1_2/Provisioning/";
			String soapAction = "http://10.11.0.50:8998/cai3g1_2/Provisioning/?wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Disconnection Command");
			SOAPMessage soapResponse = soapConnection.call(createSRHSSDisconnection(soapAction, imsi), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/HSSSubscriptionResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + imsi);

			File xmlresponse = new File("src/input/HSSSubscriptionResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			// doc.getDocumentElement().normalize();
			// Node firstChild = doc.getFirstChild(); // get first child to list through
			// other elements

			NodeList outputlist = doc.getElementsByTagName("S:Body");
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			for (int i = 0; i < childnodes.getLength(); i++) {

				Node child = childnodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (childnodes.item(i).getTextContent().trim() != "") {
						if (childnodes.item(i).getNodeName().equals("S:Fault")) {
							LOGGER.log(Level.INFO, imsi + "==> Result Fail: "
									+ childnodes.item(i).getTextContent().replaceAll("[\\n\\t ]", " "));
						} else {
							LOGGER.log(Level.INFO, imsi + "==> Result Success: "
									+ childnodes.item(i).getTextContent().replaceAll("[\\n\\t ]", " "));

						}
						// LOGGER.log(Level.INFO, childnodes.item(i).getNodeName(S:Fault));
						// LOGGER.log(Level.INFO, childnodes.item(i).getTextContent());
						// LOGGER.log(Level.INFO, "RESULT SUCCESS: " + i + "::" +
						// childnodes.item(i).getNodeName()
						// + "::" + childnodes.item(i).getTextContent().trim());
					}
				}
			}

			LOGGER.log(Level.INFO, "<-- End Processing IMSI " + imsi);
			EMASessionLogout logout = new EMASessionLogout();
			// Log Out
			logout.callLogOutEMASession(EMAHSSDisconnection.SessionId);

			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
