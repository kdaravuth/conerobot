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

public class AccountContractRenew {
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

	// Create SOAP for Contract Renewal -- Customer_DOB
	public static void createSEAccountContractRenew(SOAPMessage soapMessage, String MSISDN, String ExpiryDate)
			throws SOAPException {

		// Get subscr_no and account no from MSISDN - using subscriber Retrieve
		SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
		src.retreiveCred(new File("src/config/soapconnection.cfg"));
		src.callSubscriberRetrieveService(MSISDN);
		// END: Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve

		// Get Account Extended Data

		AccountInfoGet aig = new AccountInfoGet();
		aig.retreiveCred(new File("src/config/soapconnection.cfg"));
		aig.callingAccountInfoRetrieve(src.parentAccountInternalId);

		RobotUtilities ru = new RobotUtilities();

		// Use method to convert XML string content to XML Document object
		Document extendedDataDoc = ru.convertStringToXMLDocument(aig.extendedData);
		Element e = extendedDataDoc.getDocumentElement();
		NodeList dl = e.getChildNodes();

		for (int j = 0; j < dl.getLength(); j++) {
// Customer_DOB //ExpiryDates
			if (dl.item(j).getAttributes().getNamedItem("name").getNodeValue().equals("ExpiryDates")) {
				if (dl.item(j).getNodeType() == Node.ELEMENT_NODE) {
					// LOGGER.log(Level.INFO, dl.item(j).getChildNodes().item(0).getTextContent());
					dl.item(j).getChildNodes().item(0).setTextContent(ExpiryDate);
				}
			}

		}

		// LOGGER.log(Level.FINEST, ru.ConvertXMLDocumentToString(extendedDataDoc));

		// End Get Account Extended Data
		// SOAP Envelope

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdate = c1soapBody.addChildElement("AccountBaseUpdate", myNamespace);
		SOAPElement input = SubscriberUpdate.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement account = input.addChildElement("account");
		SOAPElement attribs = account.addChildElement("attribs");
		attribs.addTextNode("0");
		SOAPElement accountInternalId = account.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "false");
		accountInternalId.addAttribute(changedQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(src.parentAccountInternalId);

		SOAPElement extendedData = account.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");

		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		extendedDataValue.addTextNode("<![CDATA[" + ru.ConvertXMLDocumentToString(extendedDataDoc) + "]]>");

		SOAPElement extObject = input.addChildElement("extObject");
		SOAPElement extObjectattribs = extObject.addChildElement("attribs");
		extObjectattribs.addTextNode("0");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRAccountContractRenew(String soapAction, String MSISDN, String ExpiryDate)
			throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAccountContractRenew(soapMessage, MSISDN, ExpiryDate);

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

	public void callAccountContractRenew(String MSISDN, String ExpiryDate) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
					+ "/services/AccountService";
			String soapAction = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
					+ "/services/AccountService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Account Contract Renew...");
			SOAPMessage soapResponse = soapConnection.call(createSRAccountContractRenew(soapAction, MSISDN, ExpiryDate),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AccountContractRenewResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + MSISDN);

			File xmlresponse = new File("src/input/AccountContractRenewResponse.xml");
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

							LOGGER.log(Level.INFO,
									"RESULT SUCCESS RENEWING CONTRACT: " + i + "::" + childnodes.item(i).getNodeName()
											+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
