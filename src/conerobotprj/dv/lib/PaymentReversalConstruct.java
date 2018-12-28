/**
 * Payment Reversal Constructor
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
public class PaymentReversalConstruct {

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
	// Create Payment reversal soap envelope
	private static void createSEPaymentReversal(SOAPMessage soapMessage, String trackingID, String trackingIDServ,
			String billingServ) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement c1soapBodyPaymentDelete = c1soapBody.addChildElement("PaymentDelete", myNamespace);
		SOAPElement c1soapBodyInput = c1soapBodyPaymentDelete.addChildElement("input", myNamespace);

		SOAPElement c1soapBodyuserIdName = c1soapBodyInput.addChildElement("userIdName");
		c1soapBodyuserIdName.addTextNode(username);
		SOAPElement c1soapBodysecurityToken = c1soapBodyInput.addChildElement("securityToken");
		c1soapBodysecurityToken.addTextNode(token);
		SOAPElement c1soapBodyrealm = c1soapBodyInput.addChildElement("realm");
		c1soapBodyrealm.addTextNode("SAPI");

		SOAPElement c1soapBodyServerIdLocator = c1soapBodyInput.addChildElement("ServerIdLocator");
		SOAPElement c1soapBodybillingServerId = c1soapBodyServerIdLocator.addChildElement("billingServerId");
		c1soapBodybillingServerId.addTextNode(billingServ);

		SOAPElement c1soapBodyproductVersion = c1soapBodyInput.addChildElement("productVersion");
		c1soapBodyproductVersion.addTextNode("CCBS3.0");

		SOAPElement c1soapBodypayment = c1soapBodyInput.addChildElement("payment");

		SOAPElement c1soapBodypaymenttrackingId = c1soapBodypayment.addChildElement("trackingId");
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");
		c1soapBodypaymenttrackingId.addAttribute(changedQname, "true");
		c1soapBodypaymenttrackingId.addAttribute(setQname, "true");
		SOAPElement c1soapBodypaymenttrackingIdvalue = c1soapBodypaymenttrackingId.addChildElement("value");
		c1soapBodypaymenttrackingIdvalue.addTextNode(trackingID);

		SOAPElement c1soapBodypaymenttrackingIdServ = c1soapBodypayment.addChildElement("trackingIdServ");
		c1soapBodypaymenttrackingIdServ.addAttribute(changedQname, "true");
		c1soapBodypaymenttrackingIdServ.addAttribute(setQname, "true");
		SOAPElement c1soapBodypaymenttrackingIdServvalue = c1soapBodypaymenttrackingIdServ.addChildElement("value");
		c1soapBodypaymenttrackingIdServvalue.addTextNode(trackingIDServ);

	}

	// End create payment reversal soap envelope
	// Create Soap request
	// Create SOAP request
	public static SOAPMessage createSRPaymentReversal(String soapAction, String trackingID, String trackingIDServ,
			String billingServ) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEPaymentReversal(soapMessage, trackingID, trackingIDServ, billingServ);

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

	// Call Payment reversal request

	public void callPaymentReversalService(String trackingID, String trackingIDServ, String billingServ) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/PaymentService";
			String soapAction = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/PaymentService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Payment Reversal Function...");
			SOAPMessage soapResponse = soapConnection.call(
					createSRPaymentReversal(soapAction, trackingID, trackingIDServ, billingServ), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP..." + message);
			System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src\\input\\PaymentReversalResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Reversal Payment Details --> ");

			File xmlresponse = new File("src\\input\\PaymentReversalResponse.xml");
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

							LOGGER.log(Level.SEVERE, i + "::" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.INFO, i + "::" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Payment Reversal Details");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call payment reversal request

}
