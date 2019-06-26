/**
 * The function is to add NRC standalone to subscriber
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
public class NrcAddConstruct {

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

	// Create NRC Add soap envelope
	public static void createSENRCAdd(SOAPMessage soapMessage, String MSISDN, String NRCtermID, String NRCCategory,
			String NRCRate, String comments) throws SOAPException {

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
		SOAPElement NrcTermInstanceStandaloneCreate = c1soapBody.addChildElement("NrcTermInstanceStandaloneCreate",
				myNamespace);
		SOAPElement input = NrcTermInstanceStandaloneCreate.addChildElement("input", myNamespace);

		SOAPElement productVersion = input.addChildElement("productVersion");
		productVersion.addTextNode("CCBS3.0");
		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("SAPI");
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement unmaskValueSelected = input.addChildElement("unmaskValueSelected");
		unmaskValueSelected.addTextNode("true");
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);
		SOAPElement nrcTermInstance = input.addChildElement("nrcTermInstance");
		SOAPElement currencyCode = nrcTermInstance.addChildElement("currencyCode");
		currencyCode.addAttribute(changedQname, "true");
		currencyCode.addAttribute(setQname, "true");
		SOAPElement currencyCodevalue = currencyCode.addChildElement("value");
		currencyCodevalue.addTextNode("129"); // Pula currency
		SOAPElement nrcCategory = nrcTermInstance.addChildElement("nrcCategory");
		nrcCategory.addAttribute(changedQname, "true");
		nrcCategory.addAttribute(setQname, "true");
		SOAPElement nrcCategoryValue = nrcCategory.addChildElement("value");
		nrcCategoryValue.addTextNode(NRCCategory);
		SOAPElement nrcTermId = nrcTermInstance.addChildElement("nrcTermId");
		nrcTermId.addAttribute(setQname, "true");
		nrcTermId.addAttribute(changedQname, "true");
		SOAPElement nrcTermIdValue = nrcTermId.addChildElement("value");
		nrcTermIdValue.addTextNode(NRCtermID);
		SOAPElement annotation = nrcTermInstance.addChildElement("annotation");
		annotation.addAttribute(changedQname, "false");
		annotation.addAttribute(setQname, "true");
		SOAPElement annotationValue = annotation.addChildElement("value");
		annotationValue.addTextNode(comments);
		SOAPElement parentAccountInternalId = nrcTermInstance.addChildElement("parentAccountInternalId");
		parentAccountInternalId.addAttribute(setQname, "true");
		parentAccountInternalId.addAttribute(changedQname, "true");
		SOAPElement parentAccountInternalIdValue = parentAccountInternalId.addChildElement("value");
		parentAccountInternalIdValue.addTextNode(src.parentAccountInternalId);
		SOAPElement parentServiceInternalId = nrcTermInstance.addChildElement("parentServiceInternalId");
		parentServiceInternalId.addAttribute(setQname, "true");
		parentServiceInternalId.addAttribute(changedQname, "true");
		SOAPElement parentServiceInternalIdValue = parentServiceInternalId.addChildElement("value");
		parentServiceInternalIdValue.addTextNode(src.serviceInternalId);
		SOAPElement parentServiceInternalIdResets = nrcTermInstance.addChildElement("parentServiceInternalIdResets");
		parentServiceInternalIdResets.addAttribute(setQname, "true");
		parentServiceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement parentServiceInternalIdResetsValue = parentServiceInternalIdResets.addChildElement("value");
		parentServiceInternalIdResetsValue.addTextNode(src.serviceInternalIdResets);
		SOAPElement rate = nrcTermInstance.addChildElement("rate");
		SOAPElement rateValue = rate.addChildElement("value");
		rateValue.addTextNode(NRCRate);
	}// End create NRC add envelope

	// Create SOAP request
	private static SOAPMessage createSOAPRequest(String soapAction, String MSISDN, String NRCtermID, String NRCCategory,
			String NRCRate, String comments) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSENRCAdd(soapMessage, MSISDN, NRCtermID, NRCCategory, NRCRate, comments);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);
		soapMessage.saveChanges();

		// write request soap message to stream then to show in the log

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "<------Request SOAP Message -->" + message);

		return soapMessage;
	}// End create SOAP Request

	// Calling SOAP then get SOAP response
	public static void callingNRCAdd(String MSISDN, String NRCtermID, String NRCCategory, String NRCRate,
			String comments) {
		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/NrcTermInstanceService";
			String soapAction = (new BufferedReader(new FileReader("src\\Config\\sapi.cfg")).readLine())
					+ "/services/NrcTermInstanceService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling NRC Add...");
			SOAPMessage soapResponse = soapConnection.call(
					createSOAPRequest(soapAction, MSISDN, NRCtermID, NRCCategory, NRCRate, comments), soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src\\input\\NrcTermInstanceStandaloneCreate.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Simplified Results --> " + MSISDN);

			File xmlresponse = new File("src\\input\\NrcTermInstanceStandaloneCreate.xml");
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
}
