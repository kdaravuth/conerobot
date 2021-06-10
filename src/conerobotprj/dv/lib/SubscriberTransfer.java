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

public class SubscriberTransfer {
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
	public static void createSESubsTransfer(SOAPMessage soapMessage, String MSISDN, String TargetAccount,
			String Comments, String ExternalIDTYPE) throws SOAPException {

		SubscriberRetrieveEXTTYPE sr = new SubscriberRetrieveEXTTYPE();
		sr.retreiveCred(new File("src/config/soapconnection.cfg"));
		sr.callSubscriberRetrieveService(MSISDN, ExternalIDTYPE);

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberAdjustBalanceInstance = c1soapBody.addChildElement("SubscriberTransfer", myNamespace);
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
		
		SOAPElement serviceInternalId = subscriberId.addChildElement("serviceInternalId");
		serviceInternalId.addAttribute(setQname, "true");
		serviceInternalId.addAttribute(changedQname, "true");
		
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(sr.serviceInternalId);
		
		SOAPElement serviceInternalIdResets = subscriberId.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(setQname, "true");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(sr.serviceInternalIdResets);		
		

		SOAPElement subscriberIdInner = subscriberId.addChildElement("subscriberId");
		subscriberIdInner.addAttribute(setQname, "true");
		subscriberIdInner.addAttribute(changedQname, "true");
		SOAPElement subscriberIdInnerValue = subscriberIdInner.addChildElement("value");
		subscriberIdInnerValue.addTextNode(MSISDN);

		SOAPElement subscriberExternalIdType = subscriberId.addChildElement("subscriberExternalIdType");
		subscriberExternalIdType.addAttribute(setQname, "true");
		subscriberExternalIdType.addAttribute(changedQname, "true");
		SOAPElement subscriberExternalIdTypeValue = subscriberExternalIdType.addChildElement("value");
		subscriberExternalIdTypeValue.addTextNode(ExternalIDTYPE);

		SOAPElement transferToAccountId = input.addChildElement("transferToAccountId");

		SOAPElement accountInternalId = transferToAccountId.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "true");
		accountInternalId.addAttribute(changedQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(TargetAccount);

		SOAPElement accountExternalId = transferToAccountId.addChildElement("accountExternalId");
		accountExternalId.addAttribute(setQname, "true");
		accountExternalId.addAttribute(changedQname, "true");
		SOAPElement accountExternalIdValue = accountExternalId.addChildElement("value");
		accountExternalIdValue.addTextNode(TargetAccount);

		SOAPElement accountExternalIdType = transferToAccountId.addChildElement("accountExternalIdType");
		accountExternalIdType.addAttribute(setQname, "true");
		accountExternalIdType.addAttribute(changedQname, "true");
		SOAPElement accountExternalIdTypeValue = accountExternalIdType.addChildElement("value");
		accountExternalIdTypeValue.addTextNode("1");

		SOAPElement statusReasonId = input.addChildElement("statusReasonId");
		statusReasonId.addTextNode("1");

		SOAPElement comment = input.addChildElement("comment");
		comment.addTextNode(Comments);

		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("1");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRSubsTransfer(String soapAction, String MSISDN, String TargetAccount,
			String Comments, String ExternalIDTYPE) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSESubsTransfer(soapMessage, MSISDN, TargetAccount, Comments, ExternalIDTYPE);

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

	public void callSubsTransfer(String MSISDN, String TargetAccount, String Comments, String ExternalIDTYPE) {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Start Transferring MSISN : " + MSISDN);
			SOAPMessage soapResponse = soapConnection
					.call(createSRSubsTransfer(soapAction, MSISDN, TargetAccount, Comments, ExternalIDTYPE), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubsTransfer.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Start Transfering Subs  --> " + MSISDN);

			File xmlresponse = new File("src/input/SubsTransfer.xml");
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

							LOGGER.log(Level.SEVERE,
									"RESULT FAIL: " + MSISDN + " " + i + "::" + childnodes.item(i).getNodeName() + "::"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.INFO,
									"RESULT SUCCESS: " + MSISDN + " " + i + "::" + childnodes.item(i).getNodeName()
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



}
