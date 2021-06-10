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

public class DirectoryServiceRcterminstRateOverride {
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
	public static int errorRateOverride = 0;


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
	public static void createSERTIRO(SOAPMessage soapMessage, String InstanceID, String ActiveDT, String Rate)
			throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement RcTermInstanceRateOverrideCreate = c1soapBody.addChildElement("RcTermInstanceRateOverrideCreate",
				myNamespace);
		SOAPElement input = RcTermInstanceRateOverrideCreate.addChildElement("input", myNamespace);

		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement rcTermInstanceRateOverride = input.addChildElement("rcTermInstanceRateOverride");

		SOAPElement attribs = rcTermInstanceRateOverride.addChildElement("attribs");
		attribs.addTextNode("0");

		SOAPElement activeDt = rcTermInstanceRateOverride.addChildElement("activeDt");
		activeDt.addAttribute(setQname, "true");
		activeDt.addAttribute(changedQname, "true");
		SOAPElement activeDtValue = activeDt.addChildElement("value");
		activeDtValue.addTextNode(ActiveDT);

		SOAPElement rcTermInstId = rcTermInstanceRateOverride.addChildElement("rcTermInstId");
		rcTermInstId.addAttribute(setQname, "true");
		rcTermInstId.addAttribute(changedQname, "true");
		SOAPElement rcTermInstIdValue = rcTermInstId.addChildElement("value");
		rcTermInstIdValue.addTextNode(InstanceID);

		SOAPElement overrideRate = rcTermInstanceRateOverride.addChildElement("overrideRate");
		overrideRate.addAttribute(setQname, "true");
		overrideRate.addAttribute(changedQname, "true");
		SOAPElement overrideRateValue = overrideRate.addChildElement("value");
		overrideRateValue.addTextNode(Rate);

		SOAPElement currencyCode = rcTermInstanceRateOverride.addChildElement("currencyCode");
		currencyCode.addAttribute(setQname, "true");
		currencyCode.addAttribute(changedQname, "true");
		SOAPElement currencyCodeValue = currencyCode.addChildElement("value");
		currencyCodeValue.addTextNode("129");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRRTIRO(String soapAction, String InstanceID, String ActiveDT, String Rate)
			throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSERTIRO(soapMessage, InstanceID, ActiveDT, Rate);

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

	public void callRTIRO(String InstanceID, String ActiveDT, String Rate) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/RcTermInstanceRateOverrideService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/RcTermInstanceRateOverrideService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Start Overriding instance: " + InstanceID);
			SOAPMessage soapResponse = soapConnection.call(createSRRTIRO(soapAction, InstanceID, ActiveDT, Rate),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/rcTermInstanceRateOverrideResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.FINEST, "Overriding RC Term Inst:  --> " + InstanceID);

			File xmlresponse = new File("src/input/rcTermInstanceRateOverrideResponse.xml");
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
							errorRateOverride = 1;

							LOGGER.log(Level.SEVERE, "RESULT FAIL: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.FINEST, "RESULT SUCCESS: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.FINEST, "<-- End Processing InstanceID " + InstanceID);
			LOGGER.log(Level.FINEST, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
