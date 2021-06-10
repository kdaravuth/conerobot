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
public class SOSwapOfferDisconnect {

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

	// Create offer Add soap envelope
	public static void createSEOfferDisconnect(SOAPMessage soapMessage, String subscrNo, String  subscrNoReset, String OfferId, String OrderID, String ServiceOrderID)
			throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// Get offerInst to be disconnected
		SOSWAPSubscriberRetrieve SSR = new SOSWAPSubscriberRetrieve();
		SSR.retreiveCred(new File("src/config/soapconnection.cfg"));
		SSR.callSubscriberRetrieveService(subscrNo, subscrNoReset, OfferId);
		
		//LOGGER.log(Level.INFO, "offerinstance in disconnect class : " + SSR.OfferInstanceDisconnected);

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SAOI = c1soapBody.addChildElement("SubscriberDisconnectOfferInstance", myNamespace);

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
		serviceInternalIdValue.addTextNode(subscrNo);

		SOAPElement serviceInternalIdResets = subscriberId.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		serviceInternalIdResets.addAttribute(setQname, "true");

		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(subscrNoReset);

		SOAPElement offerInstanceIdsToDisconnect = SAOIInput.addChildElement("offerInstanceIdsToDisconnect");
		SOAPElement offerInstanceId = offerInstanceIdsToDisconnect.addChildElement("offerInstId");
		offerInstanceId.addAttribute(changedQname, "true");
		offerInstanceId.addAttribute(setQname, "true");

		SOAPElement offerIdValue = offerInstanceId.addChildElement("value");
		offerIdValue.addTextNode(SSR.OfferInstanceDisconnected);
		
		SOAPElement orderId = SAOIInput.addChildElement("orderId");
		SOAPElement innerorderId = orderId.addChildElement("orderId");
		innerorderId.addAttribute(setQname, "true");
		innerorderId.addAttribute(changedQname, "true");
		SOAPElement innerorderIdValue = innerorderId.addChildElement("value");
		innerorderIdValue.addTextNode(OrderID);
		
		SOAPElement orderNumber = orderId.addChildElement("orderNumber");
		orderNumber.addAttribute(setQname, "true");
		orderNumber.addAttribute(changedQname, "true");
		SOAPElement orderNumberValue = orderNumber.addChildElement("value");
		orderNumberValue.addTextNode(OrderID);
		
		SOAPElement serviceOrderId = SAOIInput.addChildElement("serviceOrderId");
		SOAPElement innerserviceOrderId = serviceOrderId.addChildElement("serviceOrderId");
		innerserviceOrderId.addAttribute(setQname, "true");
		innerserviceOrderId.addAttribute(changedQname, "true");
		SOAPElement innerserviceOrderIdValue = innerserviceOrderId.addChildElement("value");
		innerserviceOrderIdValue.addTextNode(ServiceOrderID);
			
		SOAPElement innerserviceOrderIdorderId = serviceOrderId.addChildElement("orderId");
		innerserviceOrderIdorderId.addAttribute(setQname, "true");
		innerserviceOrderIdorderId.addAttribute(changedQname, "true");
		SOAPElement innerserviceOrderIdorderIdValue = innerserviceOrderIdorderId.addChildElement("value");
		innerserviceOrderIdorderIdValue.addTextNode(OrderID);

		SOAPElement orderAutoCommit = SAOIInput.addChildElement("orderAutoCommit");
		orderAutoCommit.addTextNode("0");

		SOAPElement generateWorkflow = SAOIInput.addChildElement("generateWorkflow");
		generateWorkflow.addTextNode("0");

		SOAPElement disconnectReasonId = SAOIInput.addChildElement("disconnectReasonId");
		disconnectReasonId.addTextNode("1");
		
		SOAPElement waiveTermination = SAOIInput.addChildElement("waiveTermination");
		waiveTermination.addTextNode("1");
		
		SOAPElement waiveUnmetCommitment = SAOIInput.addChildElement("waiveUnmetCommitment");
		waiveUnmetCommitment.addTextNode("1");
		
	}
	// End create offer add envelope

	// Create SOAP request
	private static SOAPMessage createSOAPRequestOfferDisconnect(String soapAction, String subscrNo, String  subscrNoReset, String OfferId, String OrderID, String ServiceOrderID)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEOfferDisconnect(soapMessage, subscrNo, subscrNoReset, OfferId, OrderID,  ServiceOrderID);

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
	public static void callingOfferDisconnect(String subscrNo, String  subscrNoReset, String OfferId, String OrderID, String ServiceOrderID)
			throws FileNotFoundException, IOException {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Offer Disconnection...");
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequestOfferDisconnect(soapAction, subscrNo,  subscrNoReset, OfferId, OrderID, ServiceOrderID),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubscriberDisconnectOfferInstanceResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Simplified Results --> " + subscrNo);

			File xmlresponse = new File("src/input/SubscriberDisconnectOfferInstanceResponse.xml");
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

							LOGGER.log(Level.SEVERE,
									"RESULT FAIL " + subscrNo + "::" + "::" + childnodes.item(i).getNodeName() + "::"
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

				LOGGER.log(Level.INFO, "RESULT SUCCESS : " + subscrNo + "::" + tempSubInfo);

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + subscrNo);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request

}
