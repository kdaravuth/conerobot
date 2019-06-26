/**
 * @author khfighter
 * Extended Data Update
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
public class ExtendedDataUpdate {

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

	// Create NRC Add soap envelope
	public static void createSEExtendedDataUpdate(SOAPMessage soapMessage, String MSISDN, String ExtData)
			throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// Get subscr_no and account no from MSISDN - using subscriber Retrieve
		SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
		src.retreiveCred(new File("src/config/soapconnection.cfg"));
		src.callSubscriberRetrieveService(MSISDN);
		// END: Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdate = c1soapBody.addChildElement("SubscriberUpdate", myNamespace);
		SOAPElement input = SubscriberUpdate.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement subscriber = input.addChildElement("subscriber");
		SOAPElement serviceInternalId = subscriber.addChildElement("serviceInternalId");
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(src.serviceInternalId);

		SOAPElement serviceInternalIdResets = subscriber.addChildElement("serviceInternalIdResets");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(src.serviceInternalIdResets);

		SOAPElement extendedData = subscriber.addChildElement("extendedData");
		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		extendedDataValue.addTextNode(ExtData);
		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("1");

	}// End create Extended Data add envelope

	// Create SOAP request
	private static SOAPMessage createSOAPRequest(String soapAction, String MSISDN, String ExtData) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEExtendedDataUpdate(soapMessage, MSISDN, ExtData);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);
		soapMessage.saveChanges();

		// write request soap message to stream then to show in the log

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "Request SOAP Message -->" + message);

		return soapMessage;
	}// End create SOAP Request

	// Calling SOAP then get SOAP response
	public static void callingExtendedDataUpdate(String MSISDN, String ExtData) {
		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Calling Subscriber Extended Data Update...");
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, MSISDN, ExtData),
					soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubscriberExtendedDataUpdate.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.FINEST, "Simplified Results --> " + MSISDN);

			File xmlresponse = new File("src/input/SubscriberExtendedDataUpdate.xml");
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

							LOGGER.log(Level.SEVERE, "RESULT::FAIL: " + "::" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				NodeList outputSubInfoList = doc.getElementsByTagName("subscriber");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				String tempSubInfo = "";

				for (int i = 0; i < childnodesSubInfo.getLength(); i++) {

					Node child = childnodesSubInfo.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName() + " Node Type: " +
					// child.getNodeType());

					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfo.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfo.item(i).getNodeName() + "="
									+ childnodesSubInfo.item(i).getTextContent().trim() + " ; ";

						}
					}
				}
				// For Order identifier

				NodeList outputSubInfoListOID = doc.getElementsByTagName("orderIdentifier");
				Element elSubInfoOID = (Element) outputSubInfoListOID.item(0);
				NodeList childnodesSubInfoOID = elSubInfoOID.getChildNodes();

				for (int i = 0; i < childnodesSubInfoOID.getLength(); i++) {

					Node childOID = childnodesSubInfoOID.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName());

					if (childOID.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfoOID.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfoOID.item(i).getNodeName() + "="
									+ childnodesSubInfoOID.item(i).getTextContent().trim() + " ; ";

						}
					}
				}
				// end order identifier
				// For Service Order
				// For Order identifier

				NodeList outputSubInfoListSO = doc.getElementsByTagName("serviceOrderId");
				Element elSubInfoSO = (Element) outputSubInfoListSO.item(0);
				NodeList childnodesSubInfoSO = elSubInfoSO.getChildNodes();

				for (int i = 0; i < childnodesSubInfoSO.getLength(); i++) {

					Node childSO = childnodesSubInfoSO.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + childSO.getNodeName() + " Node type: "
					// + childSO.getNodeType());

					if (childSO.getNodeType() == Node.ELEMENT_NODE) {
						// LOGGER.log(Level.INFO, "childnodesSubInfoSO : " +
						// childnodesSubInfoSO.item(i));

						if (childnodesSubInfoSO.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfoSO.item(i).getNodeName() + "="
									+ childnodesSubInfoSO.item(i).getTextContent().trim() + " ; ";

						}
					}
				}

				// end service order

				LOGGER.log(Level.INFO, "RESULT::SUCCESS: " + tempSubInfo);

			}

			LOGGER.log(Level.FINEST, "<-- End Simplified result: " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());

		}
	}// End calling soap
}
