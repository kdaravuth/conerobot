/**
 * Bulk Add SO
 */
package conerobotprj.dv.lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
public class OfferAddConstruct {

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
			LOGGER.log(Level.INFO, "Retrieving realm...");

			username = lines.get(1);
			LOGGER.log(Level.INFO, "Retrieving username...");

			token = lines.get(2);
			LOGGER.log(Level.INFO, "Retrieving token...");

			bfcred.close();

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Retrieving credential fails " + e);
		}
	}

	// End sapi credential retrieval

	// Create offer Add soap envelope
	public static void createSEOfferAdd(SOAPMessage soapMessage, String MSISDN, String OfferID, String isWorkflow)
			throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve
		SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
		src.retreiveCred(new File("src/config/soapconnection.cfg"));
		src.callSubscriberRetrieveService(MSISDN);

		// LOGGER.log(Level.INFO, "serviceInternalId" + src.serviceInternalId);
		// LOGGER.log(Level.INFO, "serviceInternalIdResets" +
		// src.serviceInternalIdResets);

		// END: Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SAOI = c1soapBody.addChildElement("SubscriberAddOfferInstance", myNamespace);

		SOAPElement SAOIInput = SAOI.addChildElement("input", myNamespace);
		SOAPElement SecurityToken = SAOIInput.addChildElement("securityToken");
		SecurityToken.addTextNode(token);

		SOAPElement Usr = SAOIInput.addChildElement("userIdName");
		Usr.addTextNode(username);

		SOAPElement realm = SAOIInput.addChildElement("realm");
		realm.addTextNode("SAPI");

		SOAPElement subscriberId = SAOIInput.addChildElement("subscriberId");

		SOAPElement serviceInternalId = subscriberId.addChildElement("serviceInternalId");
		serviceInternalId.addAttribute(changedQname, "true");
		serviceInternalId.addAttribute(setQname, "true");

		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(src.serviceInternalId);

		SOAPElement serviceInternalIdResets = subscriberId.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		serviceInternalIdResets.addAttribute(setQname, "true");

		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(src.serviceInternalIdResets);

		SOAPElement InnersubscriberId = subscriberId.addChildElement("subscriberId");
		InnersubscriberId.addAttribute(changedQname, "true");
		InnersubscriberId.addAttribute(setQname, "true");

		SOAPElement InnersubscriberIdValue = InnersubscriberId.addChildElement("value");
		InnersubscriberIdValue.addTextNode(MSISDN);

		SOAPElement subscriberExternalIdType = subscriberId.addChildElement("subscriberExternalIdType");
		subscriberExternalIdType.addAttribute(changedQname, "true");
		subscriberExternalIdType.addAttribute(setQname, "true");

		SOAPElement subscriberExternalIdTypeValue = subscriberExternalIdType.addChildElement("value");
		subscriberExternalIdTypeValue.addTextNode("1");

		SOAPElement newSupplementaryOfferIdList = SAOIInput.addChildElement("newSupplementaryOfferIdList");
		SOAPElement offerId = newSupplementaryOfferIdList.addChildElement("offerId");
		offerId.addAttribute(changedQname, "true");
		offerId.addAttribute(setQname, "true");

		SOAPElement offerIdValue = offerId.addChildElement("value");
		offerIdValue.addTextNode(OfferID);

		SOAPElement orderAutoCommit = SAOIInput.addChildElement("orderAutoCommit");
		orderAutoCommit.addTextNode("true");

		SOAPElement generateWorkflow = SAOIInput.addChildElement("generateWorkflow");
		generateWorkflow.addTextNode(isWorkflow);

	}
	// End create offer add envelope

	// Create SOAP request
	private static SOAPMessage createSOAPRequest(String soapAction, String MSISDN, String OfferID, String isWorkflow)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEOfferAdd(soapMessage, MSISDN, OfferID, isWorkflow);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "<------Request SOAP Message -->" + message);

		return soapMessage;
	}

	// END: Create SOAP request
	public static void callingOfferAdd(String MSISDN, String OfferID, String isWorkflow)
			throws FileNotFoundException, IOException {

		try {

			// String soapEndpointUrl =
			// "http://10.128.202.137:8001/services/SubscriberService";
			// String soapAction =
			// "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			// http://10.128.202.137:8001: prod
			// http://10.1.38.11:8001: diot 1
			// 10.1.38.21: diot2
			String soapEndpointUrl = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Offer Add...");
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, MSISDN, OfferID, isWorkflow),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src\\input\\SubscriberAddOfferInstanceResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Simplified Results --> " + MSISDN);

			File xmlresponse = new File("src\\input\\SubscriberAddOfferInstanceResponse.xml");
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

							LOGGER.log(Level.SEVERE, "FAIL: " + "::" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				NodeList outputSubInfoList = doc.getElementsByTagName("output");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				String tempSubInfo = "";

				for (int i = 0; i < childnodesSubInfo.getLength(); i++) {

					Node child = childnodesSubInfo.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName());

					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfo.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfo.item(i).getNodeName() + "="
									+ childnodesSubInfo.item(i).getTextContent().trim() + " ; ";

						}
					}
				}

				LOGGER.log(Level.INFO, "SUCCESS: " + tempSubInfo);

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request

}
