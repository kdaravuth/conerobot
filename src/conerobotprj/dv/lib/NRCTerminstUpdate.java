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
public class NRCTerminstUpdate {

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
	public static void createSENRCUpdate(SOAPMessage soapMessage, String NRCTermInst, String Rate, String Annotation)
			throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");
		QName fetchQname = new QName("fetch");

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement NrcTermInstanceUpdate = c1soapBody.addChildElement("NrcTermInstanceUpdate", myNamespace);

		SOAPElement Input = NrcTermInstanceUpdate.addChildElement("input", myNamespace);
		SOAPElement SecurityToken = Input.addChildElement("securityToken");
		SecurityToken.addTextNode(token);

		SOAPElement Usr = Input.addChildElement("userIdName");
		Usr.addTextNode(username);

		SOAPElement realm = Input.addChildElement("realm");
		realm.addTextNode("SAPI");

		SOAPElement ServerIdLocator = Input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");
		SOAPElement ratingServerId = ServerIdLocator.addChildElement("ratingServerId");
		ratingServerId.addTextNode("9");
		
		SOAPElement nrcTermInstance = Input.addChildElement("nrcTermInstance");
		
		SOAPElement nrcTermInstId = nrcTermInstance.addChildElement("nrcTermInstId");
		nrcTermInstId.addAttribute(setQname, "false");
		nrcTermInstId.addAttribute(changedQname, "true");
		SOAPElement nrcTermInstIdValue = nrcTermInstId.addChildElement("value");
		nrcTermInstIdValue.addTextNode(NRCTermInst);
		
		//rate
		SOAPElement rate = nrcTermInstance.addChildElement("rate");
		rate.addAttribute(setQname, "true");
		rate.addAttribute(changedQname, "true");
		SOAPElement rateValue = rate.addChildElement("value");
		rateValue.addTextNode(Rate);
		
		//annotation
		SOAPElement annotation = nrcTermInstance.addChildElement("annotation");
		annotation.addAttribute(setQname, "true");
		annotation.addAttribute(changedQname, "true");
		SOAPElement annotationValue = annotation.addChildElement("value");
		annotationValue.addTextNode(Annotation);
		
		//currencyCode
		SOAPElement currencyCode = nrcTermInstance.addChildElement("currencyCode");
		currencyCode.addAttribute(setQname, "true");
		currencyCode.addAttribute(changedQname, "true");
		SOAPElement currencyCodeValue = currencyCode.addChildElement("value");
		currencyCodeValue.addTextNode("129");
		
	}
	// End create offer add envelope

	// Create SOAP request
	private static SOAPMessage createSRNRCUpdate(String soapAction, String NRCTermInst, String Rate, String Annotation)
			throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSENRCUpdate(soapMessage, NRCTermInst, Rate, Annotation);

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
	public static void callingNRCUpdate(String NRCTermInst, String Rate, String Annotation) throws FileNotFoundException, IOException {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/NrcTermInstanceService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/NrcTermInstanceService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "NRC TERM INST QUERY");
			SOAPMessage soapResponse = soapConnection.call(createSRNRCUpdate(soapAction, NRCTermInst, Rate, Annotation),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response:  " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/NRCTermInstUpdateRESPONSE.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/NRCTermInstUpdateRESPONSE.xml");
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
									"RESULT NRCUPDATE FAIL: " + NRCTermInst + "::" + "::" + childnodes.item(i).getNodeName() + "::"
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

				LOGGER.log(Level.INFO, "RESULT NRCUPDATE SUCCESS: " + NRCTermInst+"::" +tempSubInfo);

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + NRCTermInst);
			LOGGER.log(Level.INFO, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT NRCUPDATE FAIL: " + e.getCause().toString());
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request

}
