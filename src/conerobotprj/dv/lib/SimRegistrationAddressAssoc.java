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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
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
public class SimRegistrationAddressAssoc {
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
	public static String subscriberAddressAssocId = "";

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
	// Create SOAP for Primary offer Swapping
	public static void createSEAddressAssoc(SOAPMessage soapMessage, String soapAction, String AddressID,
			String ServiceInternalID, String ServiceInternalIDResets, String AccountNo)
			throws SOAPException {

		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		ft.setTimeZone(TimeZone.getTimeZone("GMT"));

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberAddressAssocCreate = c1soapBody.addChildElement("SubscriberAddressAssocCreate",
				myNamespace);
		SOAPElement input = SubscriberAddressAssocCreate.addChildElement("input", myNamespace);
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");

		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement ratingServerId = ServerIdLocator.addChildElement("ratingServerId");
		ratingServerId.addTextNode("9");

		SOAPElement realmv = input.addChildElement("realm");
		realmv.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userid = input.addChildElement("userIdName");
		userid.addTextNode("BulkSimRegistration");
		SOAPElement displayUser = input.addChildElement("displayUser");
		displayUser.addTextNode("BulkSimRegistration");

		SOAPElement subscriberAddressAssoc = input.addChildElement("subscriberAddressAssoc");
		SOAPElement attribs = subscriberAddressAssoc.addChildElement("attribs");
		attribs.addTextNode("0");

		SOAPElement addressCategoryId = subscriberAddressAssoc.addChildElement("addressCategoryId");
		addressCategoryId.addAttribute(setQname, "true");
		addressCategoryId.addAttribute(changedQname, "true");
		SOAPElement addressCategoryIdValue = addressCategoryId.addChildElement("value");
		addressCategoryIdValue.addTextNode("1");

		SOAPElement addressId = subscriberAddressAssoc.addChildElement("addressId");
		addressId.addAttribute(setQname, "true");
		addressId.addAttribute(changedQname, "true");
		SOAPElement addressIdValue = addressId.addChildElement("value");
		addressIdValue.addTextNode(AddressID);

		SOAPElement serviceInternalId = subscriberAddressAssoc.addChildElement("serviceInternalId");
		serviceInternalId.addAttribute(setQname, "true");
		serviceInternalId.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(ServiceInternalID);

		SOAPElement serviceInternalIdResets = subscriberAddressAssoc.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(setQname, "true");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(ServiceInternalIDResets);

		SOAPElement accountInternalId = subscriberAddressAssoc.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "true");
		accountInternalId.addAttribute(changedQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(AccountNo);

		SOAPElement activeDt = subscriberAddressAssoc.addChildElement("activeDt");
		activeDt.addAttribute(setQname, "true");
		activeDt.addAttribute(changedQname, "true");
		SOAPElement activeDtValue = activeDt.addChildElement("value");
		activeDtValue.addTextNode(ft.format(date));

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRAddressAssoc(String soapAction, String AddressID, String ServiceInternalID,
			String ServiceInternalIDResets, String AccountNo) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAddressAssoc(soapMessage, soapAction, AddressID, ServiceInternalID, ServiceInternalIDResets, AccountNo);

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

	// Call primary offer swap
	public void callAddressAssoc(String AddressID, String ServiceInternalID, String ServiceInternalIDResets,
			String AccountNo) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberAddressAssocService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberAddressAssocService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Creating Local Address...");
			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.FINEST, "ASSOCIATING ADDRESS --> " + AddressID + " TO " + ServiceInternalID + "-"
					+ ServiceInternalIDResets);

			SOAPMessage soapResponse = soapConnection.call(createSRAddressAssoc(soapAction, AddressID,
					ServiceInternalID, ServiceInternalIDResets, AccountNo), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AddressAssocResponse.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/AddressAssocResponse.xml");
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

							LOGGER.log(Level.SEVERE, "RESULT::FAIL ADDRESS ASSOC CREATION UPDATE : "  + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				subscriberAddressAssocId = doc.getElementsByTagName("subscriberAddressAssocId").item(0).getChildNodes()
						.item(0).getTextContent();

				// addressId
				NodeList addList = doc.getElementsByTagName("addressId");
				Element eladdList = (Element) addList.item(0);
				AddressID = eladdList.getChildNodes().item(0).getTextContent();

				NodeList outputSubInfoList = doc.getElementsByTagName("output");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				String tempSubInfo = "";

				for (int i = 0; i < childnodesSubInfo.getLength(); i++) {

					Node child = childnodesSubInfo.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName() + " Node Type: "
					// + childnodesSubInfo.item(i).getTextContent().trim());

					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfo.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfo.item(i).getNodeName() + "="
									+ childnodesSubInfo.item(i).getTextContent().trim() + " ; ";
						}
					}
				}
				LOGGER.log(Level.FINEST,
						"RESULT SUCCESS: " + AddressID + " TO " + ServiceInternalID + "-" + ServiceInternalIDResets);
			}

			LOGGER.log(Level.FINEST, "<-- End Processing MSISDN " + AddressID + " TO " + ServiceInternalID + "-"
					+ ServiceInternalIDResets);
			LOGGER.log(Level.FINEST, "--------------------------------------");
		} catch (Exception e) {

			// LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.SEVERE,
					"RESULT::FAIL ADDRESS ASSOC CREATION UPDATE : " + AddressID + " TO " + ServiceInternalID + "-" + ServiceInternalIDResets);
			
			LOGGER.log(Level.FINEST, "--------------------------------------");
		}

	}

}
