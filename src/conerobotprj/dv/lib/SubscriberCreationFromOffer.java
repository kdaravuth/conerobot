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

public class SubscriberCreationFromOffer {
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

	// Create SOAP for Inventory Load
	public static void createSESCFO(SOAPMessage soapMessage, String ExternalID, String ExternalIDType, String Company,
			String Fname, String lname, String AcctNo, String address1, String address2, String offerID)
			throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberCreateFromOffer = c1soapBody.addChildElement("SubscriberCreateFromOffer", myNamespace);
		SOAPElement input = SubscriberCreateFromOffer.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement newSubscriber = input.addChildElement("newSubscriber");
		SOAPElement attribs = newSubscriber.addChildElement("attribs");
		attribs.addTextNode("0");

		SOAPElement subscriberId = newSubscriber.addChildElement("subscriberId");
		subscriberId.addAttribute(setQname, "true");
		subscriberId.addAttribute(changedQname, "true");
		SOAPElement subscriberIdValue = subscriberId.addChildElement("value");
		subscriberIdValue.addTextNode(ExternalID);

		SOAPElement subscriberExternalIdType = newSubscriber.addChildElement("subscriberExternalIdType");
		subscriberExternalIdType.addAttribute(setQname, "true");
		subscriberExternalIdType.addAttribute(changedQname, "true");
		SOAPElement subscriberExternalIdTypeValue = subscriberExternalIdType.addChildElement("value");
		subscriberExternalIdTypeValue.addTextNode(ExternalIDType);

		SOAPElement serviceCompany = newSubscriber.addChildElement("serviceCompany");
		serviceCompany.addAttribute(setQname, "true");
		serviceCompany.addAttribute(changedQname, "true");
		SOAPElement serviceCompanyValue = serviceCompany.addChildElement("value");
		serviceCompanyValue.addTextNode(Company);

		SOAPElement serviceFname = newSubscriber.addChildElement("serviceFname");
		serviceFname.addAttribute(setQname, "true");
		serviceFname.addAttribute(changedQname, "true");
		SOAPElement serviceFnameValue = serviceFname.addChildElement("value");
		serviceFnameValue.addTextNode(Fname);

		SOAPElement serviceLname = newSubscriber.addChildElement("serviceLname");
		serviceLname.addAttribute(setQname, "true");
		serviceLname.addAttribute(changedQname, "true");
		SOAPElement serviceLnameValue = serviceLname.addChildElement("value");
		serviceLnameValue.addTextNode(lname);

		SOAPElement parentAccountInternalId = newSubscriber.addChildElement("parentAccountInternalId");
		parentAccountInternalId.addAttribute(setQname, "true");
		parentAccountInternalId.addAttribute(changedQname, "true");
		SOAPElement parentAccountInternalIdValue = parentAccountInternalId.addChildElement("value");
		parentAccountInternalIdValue.addTextNode(AcctNo);

		SOAPElement extendedData = newSubscriber.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");
		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		extendedDataValue.addTextNode(
				"<![CDATA[<ExtendedData><Parameter name=\"Nationality\"><IntegerValue>72</IntegerValue></Parameter><Parameter name=\"Customer Profession\"><StringValue>Government</StringValue></Parameter><Parameter name=\"notif_number\"><StringValue>26773000000</StringValue></Parameter></ExtendedData>]]>");

		SOAPElement primaryOfferId = input.addChildElement("primaryOfferId");

		SOAPElement offerId = primaryOfferId.addChildElement("offerId");
		offerId.addAttribute(setQname, "true");
		offerId.addAttribute(changedQname, "true");
		SOAPElement offerIdValue = offerId.addChildElement("value");
		offerIdValue.addTextNode(offerID);

		SOAPElement externalId = primaryOfferId.addChildElement("externalId");
		externalId.addAttribute(setQname, "true");
		externalId.addAttribute(changedQname, "true");
		SOAPElement externalIdValue = externalId.addTextNode("value");
		externalIdValue.addTextNode(ExternalID);
		// Add extended data for offer
		SOAPElement offerInstances = input.addChildElement("offerInstances");

		SOAPElement offeridInst = offerInstances.addChildElement("offerId");
		offeridInst.addAttribute(setQname, "true");
		offeridInst.addAttribute(changedQname, "true");
		SOAPElement offerValue = offeridInst.addChildElement("value");
		offerValue.addTextNode(offerID);

		SOAPElement offerExt = offerInstances.addChildElement("extendedData");
		offerExt.addAttribute(setQname, "true");
		offerExt.addAttribute(changedQname, "true");
		SOAPElement offerExtValue = offerExt.addChildElement("value");
		offerExtValue.addTextNode(
				"<![CDATA[<ExtendedData><Parameter name=\"original_external_id\"><StringValue>2673123123</StringValue></Parameter><Parameter name=\"annotation\"><StringValue>0</StringValue></Parameter><Parameter\r\n"
						+ "name=\"Fluency\"><IntegerValue>1</IntegerValue></Parameter><Parameter name=\"SYSPRO_INVENTORY_COUNT\"><IntegerValue>1</IntegerValue></Parameter>]]>");

//end Add extended data for offer

		SOAPElement subscriberExternalIdList = input.addChildElement("subscriberExternalIdList");

		SOAPElement serviceExternalId = subscriberExternalIdList.addChildElement("serviceExternalId");
		serviceExternalId.addAttribute(setQname, "true");
		serviceExternalId.addAttribute(changedQname, "true");
		SOAPElement serviceExternalIdValue = serviceExternalId.addChildElement("value");
		serviceExternalIdValue.addTextNode(ExternalID);

		SOAPElement serviceExternalIdType = subscriberExternalIdList.addChildElement("serviceExternalIdType");
		serviceExternalIdType.addAttribute(setQname, "true");
		serviceExternalIdType.addAttribute(changedQname, "true");
		SOAPElement serviceExternalIdTypeValue = serviceExternalIdType.addChildElement("value");
		serviceExternalIdTypeValue.addTextNode(ExternalIDType);

		SOAPElement address = input.addChildElement("address");

		SOAPElement addressv1 = address.addChildElement("address1");
		addressv1.addAttribute(setQname, "true");
		addressv1.addAttribute(changedQname, "true");
		SOAPElement address1Value = addressv1.addChildElement("value");
		address1Value.addTextNode(address1);

		SOAPElement addressv2 = address.addChildElement("address2");
		addressv2.addAttribute(setQname, "true");
		addressv2.addAttribute(changedQname, "true");
		SOAPElement address2Value = addressv2.addChildElement("value");
		address2Value.addTextNode(address2);

		SOAPElement addressTypeId = address.addChildElement("addressTypeId");
		addressTypeId.addAttribute(setQname, "true");
		addressTypeId.addAttribute(changedQname, "true");
		SOAPElement addressTypeIdValue = addressTypeId.addChildElement("value");
		addressTypeIdValue.addTextNode("1");

		SOAPElement effectiveDate = input.addChildElement("effectiveDate");
		effectiveDate.addTextNode("2019-06-01T00:00:00.0");

		SOAPElement waiveActivation = input.addChildElement("waiveActivation");
		waiveActivation.addTextNode("true");

		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("true");

		SOAPElement generateWorkflow = input.addChildElement("generateWorkflow");
		generateWorkflow.addTextNode("false");
	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRSCFO(String soapAction, String ExternalID, String ExternalIDType, String Company,
			String Fname, String lname, String AcctNo, String address1, String address2, String offerID)
			throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSESCFO(soapMessage, ExternalID, ExternalIDType, Company, Fname, lname, AcctNo, address1, address2,
				offerID);

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

	public void callSCFO(String ExternalID, String ExternalIDType, String Company, String Fname, String lname,
			String AcctNo, String address1, String address2, String offerID) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Start Creating Subscribers...");
			SOAPMessage soapResponse = soapConnection.call(createSRSCFO(soapAction, ExternalID, ExternalIDType, Company,
					Fname, lname, AcctNo, address1, address2, offerID), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SubscriberCreateFromOfferResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Loading --> " + ExternalID);

			File xmlresponse = new File("src/input/SubscriberCreateFromOfferResponse.xml");
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

							LOGGER.log(Level.SEVERE, "RESULT FAIL: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {
				for (int i = 0; i < childnodes.getLength(); i++) {

					Node child = childnodes.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						if (childnodes.item(i).getTextContent().trim() != "") {

							LOGGER.log(Level.INFO, "RESULT SUCCESS: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Processing externald id " + ExternalID);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
