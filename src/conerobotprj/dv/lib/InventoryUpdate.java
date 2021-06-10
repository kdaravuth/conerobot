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

import org.jdom.CDATA;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class InventoryUpdate {
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
	public static void createSEInventoryUpdate(SOAPMessage soapMessage, String inventoryType, String InventoryId, String inventoryIdResets, String BBuserID, String MacAddress) throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement InvElementUpdate = c1soapBody.addChildElement("InvElementUpdate", myNamespace);
		SOAPElement input = InvElementUpdate.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement invElement = input.addChildElement("invElement");
		
		SOAPElement inventoryTypeId = invElement.addChildElement("inventoryTypeId");
		inventoryTypeId.addAttribute(setQname, "true");
		inventoryTypeId.addAttribute(changedQname, "true");
		SOAPElement inventoryTypeIdValue = inventoryTypeId.addChildElement("value");
		inventoryTypeIdValue.addTextNode(inventoryType);
		
		SOAPElement inventoryId = invElement.addChildElement("inventoryId");
		inventoryId.addAttribute(setQname, "true");
		inventoryId.addAttribute(changedQname, "true");
		SOAPElement inventoryIdValue = inventoryId.addChildElement("value");
		inventoryIdValue.addTextNode(InventoryId);
		
		//inventoryIdResets
		SOAPElement InventoryIdResets = invElement.addChildElement("inventoryIdResets");
		InventoryIdResets.addAttribute(setQname, "true");
		InventoryIdResets.addAttribute(changedQname, "true");
		SOAPElement inventoryIdResetsValue = InventoryIdResets.addChildElement("value");
		inventoryIdResetsValue.addTextNode(inventoryIdResets);
		
		//extendedData
		SOAPElement extendedData = invElement.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");
		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		
		if (!BBuserID.equalsIgnoreCase("na") && !MacAddress.equalsIgnoreCase("na")) {
		String cdata = "<ExtendedData><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"broadband_username\"><StringValue>"+BBuserID+"</StringValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"mac_address\"><StringValue>"+ MacAddress +"</StringValue></Parameter></ExtendedData>";
		LOGGER.log(Level.INFO, "cdata: " + cdata);
		extendedDataValue.addTextNode(CDATA.normalizeString(cdata));
		}
		else if (BBuserID.equalsIgnoreCase("na")){
			String cdata = "<ExtendedData><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"broadband_username\"><StringValue /></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"mac_address\"><StringValue>"+ MacAddress +"</StringValue></Parameter></ExtendedData>";
			LOGGER.log(Level.INFO, "cdata: " + cdata);
			extendedDataValue.addTextNode(CDATA.normalizeString(cdata));
			
		}else if (MacAddress.equalsIgnoreCase("na")) {
			String cdata = "<ExtendedData><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"broadband_username\"><StringValue>"+BBuserID+"</StringValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"mac_address\"><StringValue/></Parameter></ExtendedData>";
			LOGGER.log(Level.INFO, "cdata: " + cdata);
			extendedDataValue.addTextNode(CDATA.normalizeString(cdata));
			
			
		}
			}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRInventoryUpdate(String soapAction, String inventoryType, String InventoryId, String inventoryIdResets, String BBuserID, String MacAddress) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEInventoryUpdate(soapMessage, inventoryType, InventoryId, inventoryIdResets, BBuserID, MacAddress);

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

	public void callInventoryUpdate(String inventoryType, String InventoryId, String inventoryIdResets, String BBuserID, String MacAddress) {

		try {

		
			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/InvElementService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/InvElementService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Start Loading the inventory");
			SOAPMessage soapResponse = soapConnection
					.call(createSRInventoryUpdate(soapAction, inventoryType, InventoryId, inventoryIdResets, BBuserID,  MacAddress), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/InvElementUpdateResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Updating --> InventoryId:" + InventoryId +";inventoryIdResets:" + inventoryIdResets);

			File xmlresponse = new File("src/input/InvElementUpdateResponse.xml");
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

							LOGGER.log(Level.SEVERE, "DETAILS FAIL: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
				LOGGER.log(Level.INFO, "RESULT FAIL: InventoryId:" + InventoryId +";inventoryIdResets:" + inventoryIdResets);
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.INFO, "DETAILS SUCCESS: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
				LOGGER.log(Level.INFO, "RESULT SUCCESS: InventoryId:" + InventoryId +";inventoryIdResets:" + inventoryIdResets);

			}

			LOGGER.log(Level.INFO, "<-- End Processing InventoryId:" + InventoryId +";inventoryIdResets:" + inventoryIdResets);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {
			LOGGER.log(Level.INFO, "RESULT FAIL: InventoryId:" + InventoryId +";inventoryIdResets:" + inventoryIdResets);
			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
