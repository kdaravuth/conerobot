package conerobotprj.dv.lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

public class SubscriberAdjBalance {
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
	public static String realm = "";
	public static String username = "";
	public static String token = "";

	// read credential
	public void retreiveCred(File filepath) {

		try {

			BufferedReader bfcred = new BufferedReader(new FileReader(filepath));
			List<String> lines = new ArrayList<String>();
			String line;

			while ((line = bfcred.readLine()) != null) {
				lines.add(line);
			}
			realm = lines.get(0);
			LOGGER.log(Level.FINEST, "Retrieving realm...");

			username = lines.get(1);
			LOGGER.log(Level.FINEST, "Retrieving username...");

			token = lines.get(2);
			LOGGER.log(Level.FINEST, "Retrieving token...");

			bfcred.close();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Retrieving credential fails " + e);
		}
	}

	// End sapi credential retrieval

	// Create SOAP for Inventory Load
	public static void createSESubsAdjBalance(SOAPMessage soapMessage, String MSISDN, String AMOUNT, String Annotation, String BalanceId, String Day) throws SOAPException {

		/*SubscriberRetrieveConstruct sr = new SubscriberRetrieveConstruct();
		sr.retreiveCred(new File("src/config/soapconnection.cfg"));
		sr.callSubscriberRetrieveService(MSISDN);*/

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberAdjustBalanceInstance = c1soapBody.addChildElement("SubscriberAdjustBalanceInstance",
				myNamespace);
		SOAPElement input = SubscriberAdjustBalanceInstance.addChildElement("input", myNamespace);

		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement subscriberId = input.addChildElement("subscriberId");
		
		SOAPElement subscriberIdInner = subscriberId.addChildElement("subscriberId");
		subscriberIdInner.addAttribute(setQname, "true");
		subscriberIdInner.addAttribute(changedQname, "true");
		SOAPElement subscriberIdInnerValue = subscriberIdInner.addChildElement("value");
		subscriberIdInnerValue.addTextNode(MSISDN);
		
		SOAPElement subscriberExternalIdType = subscriberId.addChildElement("subscriberExternalIdType");
		subscriberExternalIdType.addAttribute(setQname, "true");
		subscriberExternalIdType.addAttribute(changedQname, "true");
		SOAPElement subscriberExternalIdTypeValue = subscriberExternalIdType.addChildElement("value");
		subscriberExternalIdTypeValue.addTextNode("1");
		
		SOAPElement balanceId = input.addChildElement("balanceId");
		balanceId.addTextNode(BalanceId);
		
		SOAPElement valueDelta = input.addChildElement("valueDelta");
		valueDelta.addTextNode(AMOUNT);
		
		SOAPElement dateDelta = input.addChildElement("dateDelta");
		dateDelta.addTextNode(Day);
		
		SOAPElement mtrComment = input.addChildElement("mtrComment");
		mtrComment.addTextNode(Annotation);
		
		
		
	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRSubsAdjBalance(String soapAction, String MSISDN, String AMOUNT, String Annotation, String BalanceId, String Day) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSESubsAdjBalance(soapMessage,  MSISDN,  AMOUNT,  Annotation,  BalanceId,  Day);

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

	public void callSubsAdjBalance(String MSISDN, String AMOUNT, String Annotation, String BalanceId, String Day) {

		try {

		
			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Start Adjusting : " + MSISDN);
			SOAPMessage soapResponse = soapConnection.call(createSRSubsAdjBalance(soapAction,   MSISDN,  AMOUNT,  Annotation,  BalanceId,  Day),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubsAdjBalance.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Adjusting balance  --> " + MSISDN);

			File xmlresponse = new File("src/input/SubsAdjBalance.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName(firstChild.getNodeName());
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			// LOGGER.log( Level.INFO, "Childnodes "+ childnodes.item(0).getNodeName());
			if (childnodes.item(0).getNodeName().equals("S:Body")) {

				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.SEVERE, "RESULT FAIL: "+MSISDN+" " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.INFO, "RESULT SUCCESS: "+MSISDN+" " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN: " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.INFO, "RESULT FAIL " + MSISDN);
			LOGGER.log(Level.INFO, "<-- End Processing MSISDN: " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

	// End call Account Contract Renew

}
