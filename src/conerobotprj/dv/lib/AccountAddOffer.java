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
public class AccountAddOffer {

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
	public static String orderId = null;
	public static String serviceOrderId = null;
	public static String OfferInstId = null;
	public static Boolean checkifsuccess = false;

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
	public static void createSEOfferAdd(SOAPMessage soapMessage, String AccountNo, String OfferId)
			throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SAOI = c1soapBody.addChildElement("AccountAddOfferInstance", myNamespace);

		SOAPElement SAOIInput = SAOI.addChildElement("input", myNamespace);
		SOAPElement SecurityToken = SAOIInput.addChildElement("securityToken");
		SecurityToken.addTextNode(token);

		SOAPElement Usr = SAOIInput.addChildElement("userIdName");
		Usr.addTextNode(username);

		SOAPElement realm = SAOIInput.addChildElement("realm");
		realm.addTextNode("SAPI");

		SOAPElement accountId = SAOIInput.addChildElement("accountId");
		SOAPElement accountInternalId = accountId.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "true");
		accountInternalId.addAttribute(changedQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(AccountNo);
		
		SOAPElement offerId = SAOIInput.addChildElement("offerId");
		SOAPElement offerIdSub = offerId.addChildElement("offerId");
		offerIdSub.addAttribute(setQname, "true");
		offerIdSub.addAttribute(changedQname, "true");
		
		SOAPElement offerIdSubValue = offerIdSub.addChildElement("value");
		offerIdSubValue.addTextNode(OfferId);
		
		SOAPElement autoCommitOrder = SAOIInput.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("0");

		SOAPElement generateWorkflow = SAOIInput.addChildElement("generateWorkflow");
		generateWorkflow.addTextNode("0");

	}
	// End create offer add envelope

	// Create SOAP request
	private static SOAPMessage createSROfferAdd(String soapAction, String AccountNo, String OfferId)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEOfferAdd(soapMessage, AccountNo, OfferId);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.INFO, "<------Request SOAP Message -->" + message);

		return soapMessage;
	}

	// END: Create SOAP request
	public static void callingOfferAdd( String AccountNo, String OfferId)
			throws FileNotFoundException, IOException {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Account Add Offer");
			SOAPMessage soapResponse = soapConnection.call(createSROfferAdd(soapAction, AccountNo, OfferId),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response:  " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AccountOfferAddResponse.xml")));
			soapConnection.close();

		    File xmlresponse = new File("src/input/AccountOfferAddResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName(firstChild.getNodeName());
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			if (childnodes.item(0).getNodeName().equals("S:Body")) {
				checkifsuccess = false;

				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.SEVERE,
									"RESULT ADD OFFER FAIL " + AccountNo + "::" + "::" + childnodes.item(i).getNodeName() + "::"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
			
				checkifsuccess = true;
				NodeList outputSubInfoList = doc.getElementsByTagName("output");
				Element elSubInfo = (Element) outputSubInfoList.item(0);
				NodeList childnodesSubInfo = elSubInfo.getChildNodes();
				

				for (int i = 0; i < childnodesSubInfo.getLength(); i++) {

					Node child = childnodesSubInfo.item(i);
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName());

					if (child.getNodeType() == Node.ELEMENT_NODE) {
					
						if (child.getNodeName().equals("orderIdentifier")) {
							
							orderId = child.getChildNodes().item(2).getTextContent(); 
							
							LOGGER.log(Level.INFO, "OrderID: " + orderId );
						}
					
					if (child.getNodeName().equals("serviceOrderId")) {
							
							serviceOrderId = child.getChildNodes().item(1).getTextContent(); 
							
							LOGGER.log(Level.INFO, "Service Order ID: " + serviceOrderId );
						}
					
					if (child.getNodeName().equals("offerInstance")) {
						
						OfferInstId = child.getChildNodes().item(2).getTextContent(); 
						
						LOGGER.log(Level.INFO, "OfferInstId: " + OfferInstId );
					}
					
					
					}
				}

				LOGGER.log(Level.INFO, "RESULT ADD OFFER SUCCESS : " + AccountNo );

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + AccountNo);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT ADD OFFER FAIL: "+ e.getCause().toString());
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request

}
