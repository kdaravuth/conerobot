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
public class SubscriberPointingShadowBal {

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
	// Create SOAP for Subscriber information update
	public static void createSEPointingShadow(SOAPMessage soapMessage, String SubscrNo, String SubscrNoResets, String MSISDN, String ShadowBalID, String RealBalID, String BalAmnt, String vnextResetDate) throws SOAPException {


		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdateBalanceInstance = c1soapBody.addChildElement("SubscriberUpdateBalanceInstance", myNamespace);
		SOAPElement input = SubscriberUpdateBalanceInstance.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement subscriberId = input.addChildElement("subscriberId");

		SOAPElement serviceInternalId = subscriberId.addChildElement("serviceInternalId");
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(SubscrNo);

		SOAPElement serviceInternalIdResets = subscriberId.addChildElement("serviceInternalIdResets");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(SubscrNoResets);

		SOAPElement subscriberIdInner = subscriberId.addChildElement("subscriberId");
		subscriberIdInner.addAttribute(setQname, "true");
		subscriberIdInner.addAttribute(changedQname, "true");
		SOAPElement subscriberIdInnerValue = subscriberIdInner.addChildElement("value");
		subscriberIdInnerValue.addTextNode(MSISDN);
		
		SOAPElement subscriberExternalIdType = subscriberId.addChildElement("subscriberExternalIdType");
		subscriberExternalIdType.addAttribute(setQname, "true");
		subscriberExternalIdType.addAttribute(changedQname, "true");
		SOAPElement subscriberExternalIdTypeValue = subscriberExternalIdType.addChildElement("value");
		subscriberExternalIdTypeValue.addTextNode("1");
		
		SOAPElement newBalances = input.addChildElement("newBalances");
		
		SOAPElement balanceId = newBalances.addChildElement("balanceId");
		balanceId.addAttribute(setQname, "true");
		balanceId.addAttribute(changedQname, "true");
		SOAPElement balanceIdValue = balanceId.addChildElement("value");
		balanceIdValue.addTextNode(ShadowBalID);
		
		SOAPElement targetBalId = newBalances.addChildElement("targetBalId");
		targetBalId.addAttribute(setQname, "true");
		targetBalId.addAttribute(changedQname, "true");
		SOAPElement targetBalIdValue = targetBalId.addChildElement("value");
		targetBalIdValue.addTextNode(RealBalID);
		
		SOAPElement maxLimit  = newBalances.addChildElement("maxLimit");
		maxLimit.addAttribute(setQname, "true");
		maxLimit.addAttribute(changedQname, "true");
		SOAPElement maxLimitValue = maxLimit.addChildElement("value");
		maxLimitValue.addTextNode(BalAmnt);
		
		SOAPElement availableBalance  = newBalances.addChildElement("availableBalance");
		availableBalance.addAttribute(setQname, "true");
		availableBalance.addAttribute(changedQname, "true");
		SOAPElement availableBalanceValue = availableBalance.addChildElement("value");
		availableBalanceValue.addTextNode(BalAmnt);
		
		SOAPElement totalBalance  = newBalances.addChildElement("totalBalance");
		totalBalance.addAttribute(setQname, "true");
		totalBalance.addAttribute(changedQname, "true");
		SOAPElement totalBalanceValue = totalBalance.addChildElement("value");
		totalBalanceValue.addTextNode(BalAmnt);
		
		SOAPElement resetPeriod  = newBalances.addChildElement("resetPeriod");
		resetPeriod.addAttribute(setQname, "true");
		resetPeriod.addAttribute(changedQname, "true");
		SOAPElement resetPeriodValue = resetPeriod.addChildElement("value");
		resetPeriodValue.addTextNode("6");
		
		SOAPElement cyclicBillingDay  = newBalances.addChildElement("cyclicBillingDay");
		cyclicBillingDay.addAttribute(setQname, "true");
		cyclicBillingDay.addAttribute(changedQname, "true");
		SOAPElement cyclicBillingDayValue = cyclicBillingDay.addChildElement("value");
		cyclicBillingDayValue.addTextNode("1");
		
		SOAPElement nextResetDate = newBalances.addChildElement("nextResetDate");
		nextResetDate.addAttribute(setQname, "true");
		nextResetDate.addAttribute(changedQname, "true");
		SOAPElement nextResetDateValue = nextResetDate.addChildElement("value");
		nextResetDateValue.addTextNode(vnextResetDate);
		//nextResetDate
		
		
	}// End create Extended Data add envelope
		// Create SOAP request

	private static SOAPMessage createSOPointingShadow(String soapAction, String SubscrNo, String SubscrNoResets, String MSISDN, String ShadowBalID, String RealBalID, String BalAmnt, String vnextResetDate) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEPointingShadow(soapMessage, SubscrNo, SubscrNoResets, MSISDN, ShadowBalID, RealBalID, BalAmnt, vnextResetDate);

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
	public static void callingPointingShadow( String SubscrNo, String SubscrNoResets, String MSISDN, String ShadowBalID, String RealBalID, String BalAmnt, String vnextResetDate) {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Inner SUB Information Updates..");
			SOAPMessage soapResponse = soapConnection
					.call(createSOPointingShadow(soapAction, SubscrNo, SubscrNoResets, MSISDN, ShadowBalID, RealBalID, BalAmnt, vnextResetDate), soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/PointingShadowResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Start Processing --> SubscrNo:" + SubscrNo + ";SubscrNoResets:"+SubscrNoResets);

			File xmlresponse = new File("src/input/PointingShadowResponse.xml");
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
									"RESULT SHADOWPOINTING::"+SubscrNo +";"+SubscrNoResets +"::FAIL SUB INFO UPDATE : " + "::" + childnodes.item(i).getNodeName() + "::"
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

				LOGGER.log(Level.INFO, "RESULT SHADOWPOINTING::"+SubscrNo +";"+SubscrNoResets +"::SUCCESS SUB INFO UPDATE::" + tempSubInfo);
				// LOGGER.log(Level.INFO, extendedData);

			}

			LOGGER.log(Level.INFO, "<-- End Processing: SubscrNo:" + SubscrNo + ";SubscrNoResets:"+SubscrNoResets);
			LOGGER.log(Level.FINEST, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());

		}
	}// End calling soap
}
