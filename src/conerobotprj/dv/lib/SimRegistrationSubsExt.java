/**
 * Construct the Soap message for Subscriber Retrieve
 */
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

/**
 * @author khfighter
 *
 */
public class SimRegistrationSubsExt {

	// private static final Logger LOGGER = Logger.getLogger(
	// SubscriberRetrieveConstruct.class.getName());
	private static Logger LOGGER = null;
	public String serviceInternalId;
	public String serviceInternalIdResets;
	public String parentAccountInternalId;

	static {

		try {

			InputStream configFile = SimRegistrationSubsExt.class.getResourceAsStream("/config/logger.cfg");
			LogManager.getLogManager().readConfiguration(configFile);
			LOGGER = Logger.getLogger(SimRegistrationSubsExt.class.getName());
		} catch (IOException e) {
			e.getMessage();
		}

	}

	public static String realm = "";
	public static String username = "";
	public static String token = "";
	public static String SubExtendedData = "";

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

	private static void createSoapEnvelope(SOAPMessage soapMessage, String MSISDN) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement c1soapBodySubscriberRetrieve = c1soapBody.addChildElement("SubscriberRetrieve", myNamespace);
		SOAPElement c1soapBodyInput = c1soapBodySubscriberRetrieve.addChildElement("input", myNamespace);
		SOAPElement c1soapBodyuserIdName = c1soapBodyInput.addChildElement("userIdName");
		c1soapBodyuserIdName.addTextNode(username);
		SOAPElement c1soapBodysecurityToken = c1soapBodyInput.addChildElement("securityToken");
		c1soapBodysecurityToken.addTextNode(token);
		SOAPElement c1soapBodysubscriberId = c1soapBodyInput.addChildElement("subscriberId");
		SOAPElement c1soapBodysubscriberId2 = c1soapBodysubscriberId.addChildElement("subscriberId");
		SOAPElement c1soapBodysubscriberId2value = c1soapBodysubscriberId2.addChildElement("value");
		c1soapBodysubscriberId2value.addTextNode(MSISDN);
		SOAPElement c1soapBodysubscriberExternalIdType = c1soapBodysubscriberId
				.addChildElement("subscriberExternalIdType");
		SOAPElement c1soapBodysubscriberExternalIdTypeValue = c1soapBodysubscriberExternalIdType
				.addChildElement("value");
		c1soapBodysubscriberExternalIdTypeValue.addTextNode("1");
		SOAPElement c1soapBodyinfo = c1soapBodyInput.addChildElement("info");

		SOAPElement c1soapuseBillingDB = c1soapBodyinfo.addChildElement("useBillingDB");
		c1soapuseBillingDB.addAttribute(setQname, "true");
		c1soapuseBillingDB.addAttribute(changedQname, "true");
		SOAPElement c1soapuseBillingDBValue = c1soapuseBillingDB.addChildElement("value");
		c1soapuseBillingDBValue.addTextNode("true");

		SOAPElement c1soapusesubscriberData = c1soapBodyinfo.addChildElement("subscriberData");
		c1soapusesubscriberData.addAttribute(setQname, "true");
		c1soapusesubscriberData.addAttribute(changedQname, "true");
		SOAPElement c1soapusesubscriberDataValue = c1soapusesubscriberData.addChildElement("value");
		c1soapusesubscriberDataValue.addTextNode("true");

	}

	// Create SOAP request
	private static SOAPMessage createSOAPRequest(String soapAction, String MSISDN) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSoapEnvelope(soapMessage, MSISDN);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "Request SOAP Message - ExtendedData -->" + message);
		return soapMessage;
	}

// Call Subscriber retrieve soap:
	public void callSubscriberRetrieveService(String MSISDN) {

		try {

			// String soapEndpointUrl =
			// "http://10.128.202.137:8001/services/SubscriberService";
			// String soapAction =
			// "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			// http://10.128.202.137:8001: prod
			// http://10.1.38.11:8001: diot 1
			// 10.1.38.21: diot2
			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Calling SubscriberRetrieve...");
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, MSISDN), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubscriberRetrieveResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.FINEST, "Simplified Results --> " + MSISDN);

			File xmlresponse = new File("src/input/SubscriberRetrieveResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName(firstChild.getNodeName());
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			if (childnodes.item(0).getNodeName().equals("S:Body")) {

				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.SEVERE, i + "::" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				SubExtendedData = doc.getElementsByTagName("extendedData").item(0).getChildNodes().item(0)
						.getTextContent();
			//	System.out.println("Extended Data: " + SubExtendedData);

				NodeList outputSubInfoList = doc.getElementsByTagName("subscriber");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				String tempSubInfo = "";

				for (int i = 0; i < childnodesSubInfo.getLength(); i++) {

					Node child = childnodesSubInfo.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName());

					if (child.getNodeName() == "serviceInternalId") {
						serviceInternalId = childnodesSubInfo.item(i).getTextContent().trim();
						// LOGGER.log(Level.INFO, "service internal: " + serviceInternalId);
					}

					if (child.getNodeName() == "serviceInternalIdResets") {
						serviceInternalIdResets = childnodesSubInfo.item(i).getTextContent().trim();
					}

					if (child.getNodeName() == "parentAccountInternalId") {
						parentAccountInternalId = childnodesSubInfo.item(i).getTextContent().trim();
					}

					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfo.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfo.item(i).getNodeName() + "="
									+ childnodesSubInfo.item(i).getTextContent().trim() + " ; ";

						}
					}
				}

				LOGGER.log(Level.FINEST, tempSubInfo);

			}

			LOGGER.log(Level.FINEST, "<-- End Simplified result: " + MSISDN);
			LOGGER.log(Level.FINEST, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT::FAIL SUB EXT : "+ e.toString());
		}

	}

}
