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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author khfighter
 *
 */
public class BalanceLimitUpdate {
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
	// Create SOAP for Primary offer Swapping
	public static void createSEBalanceLimitUpdate(SOAPMessage soapMessage, String MSISDN, String Amounttbupdated,
			String UpdateFlag) throws SOAPException {

		// UpdateFlag: 1: only balance update, 2: only limit update, 3: both balance and
		// limit update
		// Get subscr_no and account no from MSISDN - using subscriber Retrieve
		SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
		src.retreiveCred(new File("src/config/soapconnection.cfg"));
		src.callSubscriberRetrieveService(MSISDN);
		// END: Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve
		// LOGGER.log(Level.INFO, src.serviceInternalId);
		// SOAP Envelope

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdateBalanceInstance = c1soapBody.addChildElement("SubscriberUpdateBalanceInstance",
				myNamespace);
		SOAPElement input = SubscriberUpdateBalanceInstance.addChildElement("input", myNamespace);
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement realmv = input.addChildElement("realm");
		realmv.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userid = input.addChildElement("userIdName");
		userid.addTextNode(username);

		SOAPElement subscriberId = input.addChildElement("subscriberId");

		SOAPElement serviceInternalId = subscriberId.addChildElement("serviceInternalId");
		serviceInternalId.addAttribute(setQname, "true");
		serviceInternalId.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(src.serviceInternalId);

		SOAPElement serviceInternalIdResets = subscriberId.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(setQname, "true");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(src.serviceInternalIdResets);

		SOAPElement newBalances = input.addChildElement("newBalances");
		SOAPElement attribs = newBalances.addChildElement("attribs");
		attribs.addTextNode("0");

		SOAPElement balanceId = newBalances.addChildElement("balanceId");
		balanceId.addAttribute(setQname, "true");
		balanceId.addAttribute(changedQname, "true");
		SOAPElement balanceIdValue = balanceId.addChildElement("value");
		balanceIdValue.addTextNode("2");

		// UpdateFlag: 1: only balance update, 2: only limit update, 3: both balance and
		// limit update
		if (UpdateFlag == "1") {
			// availableBalance
			SOAPElement availableBalance = newBalances.addChildElement("availableBalance");
			availableBalance.addAttribute(setQname, "true");
			availableBalance.addAttribute(changedQname, "true");
			SOAPElement availableBalanceValue = availableBalance.addChildElement("value");
			availableBalanceValue.addTextNode(Amounttbupdated);
			// totalBalance
			SOAPElement totalBalance = newBalances.addChildElement("totalBalance");
			totalBalance.addAttribute(setQname, "true");
			totalBalance.addAttribute(changedQname, "true");
			SOAPElement totalBalanceValue = totalBalance.addChildElement("value");
			totalBalanceValue.addTextNode(Amounttbupdated);
		} else if (UpdateFlag == "2") {
			// maxLimit
			SOAPElement maxLimit = newBalances.addChildElement("maxLimit");
			maxLimit.addAttribute(setQname, "true");
			maxLimit.addAttribute(changedQname, "true");
			SOAPElement maxLimitValue = maxLimit.addChildElement("value");
			maxLimitValue.addTextNode(Amounttbupdated);
		} else {

			SOAPElement availableBalance = newBalances.addChildElement("availableBalance");
			availableBalance.addAttribute(setQname, "true");
			availableBalance.addAttribute(changedQname, "true");
			SOAPElement availableBalanceValue = availableBalance.addChildElement("value");
			availableBalanceValue.addTextNode(Amounttbupdated);

			SOAPElement totalBalance = newBalances.addChildElement("totalBalance");
			totalBalance.addAttribute(setQname, "true");
			totalBalance.addAttribute(changedQname, "true");
			SOAPElement totalBalanceValue = totalBalance.addChildElement("value");
			totalBalanceValue.addTextNode(Amounttbupdated);

			SOAPElement maxLimit = newBalances.addChildElement("maxLimit");
			maxLimit.addAttribute(setQname, "true");
			maxLimit.addAttribute(changedQname, "true");
			SOAPElement maxLimitValue = maxLimit.addChildElement("value");
			maxLimitValue.addTextNode(Amounttbupdated);

		}

		SOAPElement mtrComment = input.addChildElement("mtrComment");
		mtrComment.addTextNode("Setting credit limit for subscriber: " + MSISDN);

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRBalanceLimitUpdate(String soapAction, String MSISDN, String Amounttbupdated,
			String UpdateFlag) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEBalanceLimitUpdate(soapMessage, MSISDN, Amounttbupdated, UpdateFlag);

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

	// Call primary offer swap
	public void callBalanceLimitUpdate(String MSISDN, String Amounttbupdated, String UpdateFlag) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling BALANCE/CREDIT LIMIT UPDATE...");
			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + MSISDN);

			SOAPMessage soapResponse = soapConnection
					.call(createSRBalanceLimitUpdate(soapAction, MSISDN, Amounttbupdated, UpdateFlag), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/BalanceLimitUpdateResponse.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/BalanceLimitUpdateResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName(firstChild.getNodeName());
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			// LOGGER.log(Level.INFO, "Childnodes " + childnodes.item(0).getNodeName());

			if (childnodes.item(0).getNodeName().equals("S:Body")) {

				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.SEVERE,
									"RESULT FAIL CREDIT LIMIT/BALANCE UPDATE: " + i + "::"
											+ childnodes.item(i).getNodeName() + "::"
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
					// LOGGER.log(Level.INFO, "Node Name: " + child.getNodeName() + " Node Type: "
					// + childnodesSubInfo.item(i).getTextContent().trim());

					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodesSubInfo.item(i).getTextContent().trim() != "") {
							tempSubInfo += childnodesSubInfo.item(i).getNodeName() + "="
									+ childnodesSubInfo.item(i).getTextContent().trim() + " ; ";
						}
					}
				}
				LOGGER.log(Level.INFO, "RESULT SUCCESS CREDIT LIMIT/BALANCE UPDATE: " + MSISDN);
			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			// LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.INFO, "RESULT FAIL CREDIT LIMIT/BALANCE UPDATE: " + MSISDN);
			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + MSISDN);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

}
