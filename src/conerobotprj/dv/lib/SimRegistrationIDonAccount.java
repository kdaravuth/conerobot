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
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author khfighter
 *
 */
public class SimRegistrationIDonAccount {

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

	// Create SOAP for Account information update
	public static void createSEAccountInfoUpdate(SOAPMessage soapMessage, String AccountNo, String id, String IDType)
			throws SOAPException {

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
		accountInternalIdValue.addTextNode(AccountNo);

		SOAPElement accountExternalIdType = account.addChildElement("accountExternalIdType");
		accountExternalIdType.addAttribute(setQname, "false");
		accountExternalIdType.addAttribute(changedQname, "true");
		SOAPElement accountExternalIdTypeValue = accountExternalIdType.addChildElement("value");
		accountExternalIdTypeValue.addTextNode("0");

		SOAPElement ssn = account.addChildElement("ssn");
		ssn.addAttribute(setQname, "true");
		ssn.addAttribute(changedQname, "true");
		SOAPElement ssnValue = ssn.addChildElement("value");
		ssnValue.addTextNode(id);

		// ID TYPE
		// Get Existing Account Extended Data and Extend it
		AccountSubsInfoAcctInfoGet aiaig = new AccountSubsInfoAcctInfoGet();
		aiaig.retreiveCred(new File("src/config/soapconnection.cfg"));
		String line;
		String ed;
		Integer start;
		Integer end;
		String IDTYPEOrig;
		String IDTYPEReplaced;
		String EDUpdated = "";
		/// Start replacing old ED with new ED
		aiaig.callingAccountInfoRetrieve(AccountNo);
		ed = aiaig.extendedData;
		EDUpdated = ed;

		start = ed.indexOf("<Parameter name=\"id_type\">");
		if (start == -1) {

			start = ed.indexOf(
					"<Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"id_type\" xsi:nil=\"true\">");
			end = ed.indexOf("</Parameter>", start);
			IDTYPEOrig = ed.substring(start, end + 12);
			IDTYPEReplaced = "<Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"id_type\" xsi:nil=\"true\"><StringValue>"
					+ IDType + "</StringValue></Parameter>";

			EDUpdated = EDUpdated.replace(IDTYPEOrig, IDTYPEReplaced);
			// LOGGER.log(Level.INFO, "2-1==> " + EDUpdated);

		} else {

			end = ed.indexOf("</Parameter>", start);
			IDTYPEOrig = ed.substring(start, end + 12);
			IDTYPEReplaced = "<Parameter name=\"id_type\"><StringValue>" + IDType + "</StringValue></Parameter>";

			EDUpdated = EDUpdated.replace(IDTYPEOrig, IDTYPEReplaced);
			// LOGGER.log(Level.INFO, "2==> " + EDUpdated);

		}

		/////////////////////////////////////

		String EDD = EDUpdated;
		SOAPElement extendedData = account.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");
		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		
		extendedDataValue.addTextNode(CDATA.normalizeString(EDD));
		

	}// End create Extended Data add envelope
		// Create SOAP request

	private static SOAPMessage createSOAPRequest(String soapAction, String AccountNo, String id, String IDType)
			throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAccountInfoUpdate(soapMessage, AccountNo, id, IDType);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);
		soapMessage.saveChanges();

		// write request soap message to stream then to show in the log

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.INFO, "Request SOAP Message -->" + message);

		return soapMessage;
	}// End create SOAP Request

	// Calling SOAP then get SOAP response
	public static void callingAccountInfoUpdate(String AccountNo, String id, String IDType) {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Inner Account Information Updates..");
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, AccountNo, id, IDType),
					soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AccountInfoUpdate.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Simplified Results --> " + AccountNo);

			File xmlresponse = new File("src/input/AccountInfoUpdate.xml");
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
									"RESULT::FAIL ACCOUNT INFO UPDATE " + "::" + childnodes.item(i).getNodeName() + "::"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				String tempSubInfo = "";

				// For Extend

				NodeList outputSubInfoListSO = doc.getElementsByTagName("output");
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

				LOGGER.log(Level.INFO, "RESULT::SUCCESS ACCOUNT INFO UPDATE::" + tempSubInfo);
				// LOGGER.log(Level.INFO, extendedData);

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + AccountNo);
			LOGGER.log(Level.FINEST, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT::FAIL ACCOUNT INFO UPDATE " + e.getCause().toString());

		}
	}// End calling soap

}
