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
public class ExemptedAccountAdding {

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
	public static void createSEAccountExempted(SOAPMessage soapMessage, String AccountNo) throws SOAPException {


		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdate = c1soapBody.addChildElement("TaxExemptionCreate", myNamespace);
		SOAPElement input = SubscriberUpdate.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement in = input.addChildElement("in");
		
		SOAPElement accountInternalId = in.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "true");
		accountInternalId.addAttribute(setQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode( AccountNo);
		
		SOAPElement cityCode = in.addChildElement("cityCode");
		cityCode.addAttribute(setQname, "true");
		cityCode.addAttribute(changedQname, "true");
		SOAPElement cityCodeValue = cityCode.addChildElement("value");
		cityCodeValue.addTextNode("0");
		
		SOAPElement countryCode = in.addChildElement("countryCode");
		countryCode.addAttribute(setQname, "true");
		countryCode.addAttribute(changedQname, "true");
		SOAPElement countryCodeValue = countryCode.addChildElement("value");
		countryCodeValue.addTextNode("72");
		
		SOAPElement countyCode = in.addChildElement("countyCode");
		countyCode.addAttribute(setQname, "true");
		countyCode.addAttribute(changedQname, "true");
		SOAPElement countyCodeValue = countyCode.addChildElement("value");
		countyCodeValue.addTextNode("0");
		
		SOAPElement exemptCity = in.addChildElement("exemptCity");
		exemptCity.addAttribute(setQname, "true");
		exemptCity.addAttribute(changedQname, "true");
		SOAPElement exemptCityValue = exemptCity.addChildElement("value");
		exemptCityValue.addTextNode("0");
		
		SOAPElement exemptCounty = in.addChildElement("exemptCounty");
		exemptCounty.addAttribute(setQname, "true");
		exemptCounty.addAttribute(changedQname, "true");
		SOAPElement exemptCountyValue = exemptCounty.addChildElement("value");
		exemptCountyValue.addTextNode("0");
		
		SOAPElement exemptFederal = in.addChildElement("exemptFederal");
		exemptFederal.addAttribute(setQname, "true");
		exemptFederal.addAttribute(changedQname, "true");
		SOAPElement exemptFederalValue = exemptFederal.addChildElement("value");
		exemptFederalValue.addTextNode("1");
		
		SOAPElement exemptOther = in.addChildElement("exemptOther");
		exemptOther.addAttribute(setQname, "true");
		exemptOther.addAttribute(changedQname, "true");
		SOAPElement exemptOtherValue = exemptOther.addChildElement("value");
		exemptOtherValue.addTextNode("1");
		
		SOAPElement exemptState = in.addChildElement("exemptState");
		exemptState.addAttribute(setQname, "true");
		exemptState.addAttribute(changedQname, "true");
		SOAPElement exemptStateValue = exemptState.addChildElement("value");
		exemptStateValue.addTextNode("0");
		
		SOAPElement franchiseCode = in.addChildElement("franchiseCode");
		franchiseCode.addAttribute(setQname, "true");
		franchiseCode.addAttribute(changedQname, "true");
		SOAPElement franchiseCodeValue = franchiseCode.addChildElement("value");
		franchiseCodeValue.addTextNode("2");
		
		SOAPElement stateCode = in.addChildElement("stateCode");
		stateCode.addAttribute(setQname, "true");
		stateCode.addAttribute(changedQname, "true");
		SOAPElement stateCodeValue = stateCode.addChildElement("value");
		stateCodeValue.addTextNode("0");
		
		SOAPElement taxPkgInstId = in.addChildElement("taxPkgInstId");
		taxPkgInstId.addAttribute(setQname, "true");
		taxPkgInstId.addAttribute(changedQname, "true");
		SOAPElement taxPkgInstIdValue = taxPkgInstId.addChildElement("value");
		taxPkgInstIdValue.addTextNode("1");
		
		SOAPElement taxStatus = in.addChildElement("taxStatus");
		taxStatus.addAttribute(setQname, "true");
		taxStatus.addAttribute(changedQname, "true");
		SOAPElement taxStatusValue = taxStatus.addChildElement("value");
		taxStatusValue.addTextNode("2");
		
		SOAPElement taxTypeCode = in.addChildElement("taxTypeCode");
		taxTypeCode.addAttribute(setQname, "true");
		taxTypeCode.addAttribute(changedQname, "true");
		SOAPElement taxTypeCodeValue = taxTypeCode.addChildElement("value");
		taxTypeCodeValue.addTextNode("2");

		
	}// End create Extended Data add envelope
		// Create SOAP request

	private static SOAPMessage createSOAccountExempted(String soapAction, String AccountNo) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAccountExempted(soapMessage, AccountNo);

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
	public static void callingAccountExempted(String AccountNo) {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/TaxExemptionService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/TaxExemptionService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "Inner SUB Information Updates..");
			SOAPMessage soapResponse = soapConnection
					.call(createSOAccountExempted(soapAction, AccountNo), soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubsTargetAccountUpdate.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Start Processing --> AccountNo:" + AccountNo );

			File xmlresponse = new File("src/input/SubsTargetAccountUpdate.xml");
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
									"RESULT ACCOUNTEXEMPTED::"+AccountNo +"::FAIL SUB INFO UPDATE : " + "::" + childnodes.item(i).getNodeName() + "::"
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

				LOGGER.log(Level.INFO, "RESULT ACCOUNTEXEMPTED::"+AccountNo +"::SUCCESS SUB INFO UPDATE::" + tempSubInfo);
				// LOGGER.log(Level.INFO, extendedData);

			}

			LOGGER.log(Level.INFO, "<-- End Processing: ACCOUNTEXEMPTED::"+AccountNo );
			LOGGER.log(Level.FINEST, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());

		}
	}// End calling soap
}
