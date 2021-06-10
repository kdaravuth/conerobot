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
public class NCAFixed {
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
	public static void createSENCAFixed(SOAPMessage soapMessage, String ModeFlag, String PrimaryOfferId,
			String ParentAccount, String Fname, String Lname, String CompanyName, String ID, String IDType,
			String IDExpiry, String NotifNumber, String Address1, String Address2, String Address3, String Address4,
			String city, String state, String country, String FIXEDEXTERNALID, String ExternalIDType) throws SOAPException {
//ModeFlag: 1 Prepaid else Postpaid 
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
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement realmv = input.addChildElement("realm");
		realmv.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userid = input.addChildElement("userIdName");
		userid.addTextNode(username);

		if (ParentAccount.equals("0")) {
			// in case of new account creation
			SOAPElement account = input.addChildElement("account");
			// bill address
			SOAPElement billAddress1 = account.addChildElement("billAddress1");
			billAddress1.addAttribute(setQname, "true");
			billAddress1.addAttribute(changedQname, "true");
			SOAPElement billAddress1Value = billAddress1.addChildElement("value");
			billAddress1Value.addTextNode(Address1);

			SOAPElement billAddress2 = account.addChildElement("billAddress2");
			billAddress2.addAttribute(setQname, "true");
			billAddress2.addAttribute(changedQname, "true");
			SOAPElement billAddress2Value = billAddress2.addChildElement("value");
			billAddress2Value.addTextNode(Address2);

			SOAPElement billAddress3 = account.addChildElement("billAddress3");
			billAddress3.addAttribute(setQname, "true");
			billAddress3.addAttribute(changedQname, "true");
			SOAPElement billAddress3Value = billAddress3.addChildElement("value");
			billAddress3Value.addTextNode(Address3);

			SOAPElement billAddress4 = account.addChildElement("billAddress4");
			billAddress4.addAttribute(setQname, "true");
			billAddress4.addAttribute(changedQname, "true");
			SOAPElement billAddress4Value = billAddress4.addChildElement("value");
			billAddress4Value.addTextNode(Address4);

			// cust address
			SOAPElement custAddress1 = account.addChildElement("custAddress1");
			custAddress1.addAttribute(setQname, "true");
			custAddress1.addAttribute(changedQname, "true");
			SOAPElement custAddress1Value = custAddress1.addChildElement("value");
			custAddress1Value.addTextNode(Address1);

			SOAPElement custAddress2 = account.addChildElement("custAddress2");
			custAddress2.addAttribute(setQname, "true");
			custAddress2.addAttribute(changedQname, "true");
			SOAPElement custAddress2Value = custAddress2.addChildElement("value");
			custAddress2Value.addTextNode(Address2);

			SOAPElement custAddress3 = account.addChildElement("custAddress3");
			custAddress3.addAttribute(setQname, "true");
			custAddress3.addAttribute(changedQname, "true");
			SOAPElement custAddress3Value = custAddress3.addChildElement("value");
			custAddress3Value.addTextNode(Address3);

			SOAPElement custAddress4 = account.addChildElement("custAddress4");
			custAddress4.addAttribute(setQname, "true");
			custAddress4.addAttribute(changedQname, "true");
			SOAPElement custAddress4Value = custAddress4.addChildElement("value");
			custAddress4Value.addTextNode(Address4);

			SOAPElement billFnameAcct = account.addChildElement("billFname");
			billFnameAcct.addAttribute(setQname, "true");
			billFnameAcct.addAttribute(changedQname, "true");
			SOAPElement billFnameAcctValue = billFnameAcct.addChildElement("value");
			billFnameAcctValue.addTextNode(Fname);

			SOAPElement billLnameAcct = account.addChildElement("billLname");
			billLnameAcct.addAttribute(setQname, "true");
			billLnameAcct.addAttribute(changedQname, "true");
			SOAPElement billLnameAcctValue = billLnameAcct.addChildElement("value");
			billLnameAcctValue.addTextNode(Lname);

			// billCompany

			SOAPElement billCompanyAcct = account.addChildElement("billCompany");
			billCompanyAcct.addAttribute(setQname, "true");
			billCompanyAcct.addAttribute(changedQname, "true");
			SOAPElement billCompanyAcctValue = billCompanyAcct.addChildElement("value");
			billCompanyAcctValue.addTextNode(CompanyName);

			SOAPElement ssn = account.addChildElement("ssn");
			ssn.addAttribute(setQname, "true");
			ssn.addAttribute(changedQname, "true");
			ssn.addTextNode(ID);

			SOAPElement mktCode = account.addChildElement("mktCode");
			mktCode.addAttribute(setQname, "true");
			mktCode.addAttribute(changedQname, "true");
			SOAPElement mktCodevalue = mktCode.addChildElement("value");
			mktCodevalue.addTextNode("3");

			SOAPElement accountType = account.addChildElement("accountType");
			accountType.addAttribute(setQname, "true");
			accountType.addAttribute(changedQname, "true");
			SOAPElement accountTypeValue = accountType.addChildElement("value");
			// if modeflag = 1 then account type non-prepaid statement else billed.

			if (ModeFlag.equals("1")) {

				accountTypeValue.addTextNode("2");

			} else {
				accountTypeValue.addTextNode("1");
			}

			SOAPElement extendedData = account.addChildElement("extendedData");
			extendedData.addAttribute(setQname, "true");
			extendedData.addAttribute(changedQname, "true");
			SOAPElement extendedDataValue = extendedData.addChildElement("value");
			extendedDataValue.addTextNode("<![CDATA[<ExtendedData><Parameter name=\"id_type\"><StringValue>" + IDType
					+ "</StringValue></Parameter><Parameter name=\"coll_number\"><StringValue>" + NotifNumber
					+ "</StringValue></Parameter></ExtendedData>]]>");
		}

		SOAPElement newSubscriber = input.addChildElement("newSubscriber");

		if (!ParentAccount.equals("0")) {
			// Existing Account No
			SOAPElement parentAccountInternalId = newSubscriber.addChildElement("parentAccountInternalId");
			parentAccountInternalId.addAttribute(setQname, "true");
			parentAccountInternalId.addAttribute(changedQname, "true");
			SOAPElement parentAccountInternalIdValue = parentAccountInternalId.addChildElement("value");
			parentAccountInternalIdValue.addTextNode(ParentAccount);
		}

		SOAPElement serviceCompany = newSubscriber.addChildElement("serviceCompany");
		serviceCompany.addAttribute(setQname, "true");
		serviceCompany.addAttribute(changedQname, "true");
		SOAPElement serviceCompanyValue = serviceCompany.addChildElement("value");
		serviceCompanyValue.addTextNode(CompanyName);

		SOAPElement serviceFname = newSubscriber.addChildElement("serviceFname");
		serviceFname.addAttribute(setQname, "true");
		serviceFname.addAttribute(changedQname, "true");
		SOAPElement serviceFnameValue = serviceFname.addChildElement("value");
		serviceFnameValue.addTextNode(Fname);

		SOAPElement serviceLname = newSubscriber.addChildElement("serviceLname");
		serviceLname.addAttribute(setQname, "true");
		serviceLname.addAttribute(changedQname, "true");
		SOAPElement serviceLnameValue = serviceLname.addChildElement("value");
		serviceLnameValue.addTextNode(Lname);

		SOAPElement extendedDataSub = newSubscriber.addChildElement("extendedData");
		extendedDataSub.addAttribute(setQname, "true");
		extendedDataSub.addAttribute(changedQname, "true");
		SOAPElement extendedDataSubValue = extendedDataSub.addChildElement("value");
		extendedDataSubValue.addTextNode(
				"<![CDATA[<ExtendedData><Parameter name=\"Nationality\"><IntegerValue>72</IntegerValue></Parameter><Parameter name=\"Customer Profession\"><StringValue>Government</StringValue></Parameter><Parameter name=\"notif_number\"><StringValue>"
						+ NotifNumber + "</StringValue></Parameter></ExtendedData>]]>");

		SOAPElement primaryOfferId = input.addChildElement("primaryOfferId");
		SOAPElement offerId = primaryOfferId.addChildElement("offerId");
		offerId.addAttribute(setQname, "true");
		offerId.addAttribute(changedQname, "true");
		SOAPElement offerIdValue = offerId.addChildElement("value");
		offerIdValue.addTextNode(PrimaryOfferId);

		// FIXEDEXTERNALID
		SOAPElement subscriberExternalIdListFIXEDEXTERNALID = input.addChildElement("subscriberExternalIdList");
		SOAPElement serviceExternalIdFIXEDEXTERNALID = subscriberExternalIdListFIXEDEXTERNALID.addChildElement("serviceExternalId");
		serviceExternalIdFIXEDEXTERNALID.addAttribute(setQname, "true");
		serviceExternalIdFIXEDEXTERNALID.addAttribute(changedQname, "true");
		SOAPElement serviceExternalIdFIXEDEXTERNALIDValue = serviceExternalIdFIXEDEXTERNALID.addChildElement("value");
		serviceExternalIdFIXEDEXTERNALIDValue.addTextNode(FIXEDEXTERNALID);
		SOAPElement serviceExternalIdTypeFIXEDEXTERNALID = subscriberExternalIdListFIXEDEXTERNALID
				.addChildElement("serviceExternalIdType");
		serviceExternalIdTypeFIXEDEXTERNALID.addAttribute(setQname, "true");
		serviceExternalIdTypeFIXEDEXTERNALID.addAttribute(changedQname, "true");
		SOAPElement serviceExternalIdTypeFIXEDEXTERNALIDValue = serviceExternalIdTypeFIXEDEXTERNALID.addChildElement("value");
		serviceExternalIdTypeFIXEDEXTERNALIDValue.addTextNode(ExternalIDType);

		// Subscriber Address
		SOAPElement ServiceAddress = input.addChildElement("address");
		SOAPElement Serviceaddress1 = ServiceAddress.addChildElement("address1");
		Serviceaddress1.addAttribute(setQname, "true");
		Serviceaddress1.addAttribute(changedQname, "true");
		SOAPElement Serviceaddress1Value = Serviceaddress1.addChildElement("value");
		Serviceaddress1Value.addTextNode(Address1);

		SOAPElement Serviceaddress2 = ServiceAddress.addChildElement("address2");
		Serviceaddress2.addAttribute(setQname, "true");
		Serviceaddress2.addAttribute(changedQname, "true");
		SOAPElement Serviceaddress2Value = Serviceaddress2.addChildElement("value");
		Serviceaddress2Value.addTextNode(Address2);

		SOAPElement Serviceaddress3 = ServiceAddress.addChildElement("address3");
		Serviceaddress3.addAttribute(setQname, "true");
		Serviceaddress3.addAttribute(changedQname, "true");
		SOAPElement Serviceaddress3Value = Serviceaddress3.addChildElement("value");
		Serviceaddress3Value.addTextNode(Address3);

		SOAPElement Serviceaddress4 = ServiceAddress.addChildElement("address4");
		Serviceaddress4.addAttribute(setQname, "true");
		Serviceaddress4.addAttribute(changedQname, "true");
		SOAPElement Serviceaddress4Value = Serviceaddress4.addChildElement("value");
		Serviceaddress4Value.addTextNode(Address4);

		SOAPElement ServiceCity = ServiceAddress.addChildElement("city");
		ServiceCity.addAttribute(setQname, "true");
		ServiceCity.addAttribute(changedQname, "true");
		SOAPElement ServiceCityValue = ServiceCity.addChildElement("value");
		ServiceCityValue.addTextNode(city);

		SOAPElement Servicestate = ServiceAddress.addChildElement("state");
		Servicestate.addAttribute(setQname, "true");
		Servicestate.addAttribute(changedQname, "true");
		SOAPElement ServicestateValue = Servicestate.addChildElement("value");
		ServicestateValue.addTextNode(state);

		SOAPElement ServicecountryCode = ServiceAddress.addChildElement("countryCode");
		ServicecountryCode.addAttribute(setQname, "true");
		ServicecountryCode.addAttribute(changedQname, "true");
		SOAPElement ServicecountryCodeValue = ServicecountryCode.addChildElement("value");
		ServicecountryCodeValue.addTextNode(country);

		// addressTypeId
		SOAPElement ServiceaddressTypeId = ServiceAddress.addChildElement("addressTypeId");
		ServiceaddressTypeId.addAttribute(setQname, "true");
		ServiceaddressTypeId.addAttribute(changedQname, "true");
		SOAPElement ServiceaddressTypeIdValue = ServiceaddressTypeId.addChildElement("value");
		ServiceaddressTypeIdValue.addTextNode("1");

		SOAPElement waiveActivation = input.addChildElement("waiveActivation");
		waiveActivation.addTextNode("true");

		SOAPElement waiveTermination = input.addChildElement("waiveTermination");
		waiveTermination.addTextNode("true");

		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("true");

		SOAPElement generateWorkflow = input.addChildElement("generateWorkflow");
		generateWorkflow.addTextNode("true");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRNCAFixed(String soapAction, String ModeFlag, String PrimaryOfferId,
			String ParentAccount, String Fname, String Lname, String CompanyName, String ID, String IDType,
			String IDExpiry, String NotifNumber, String Address1, String Address2, String Address3, String Address4,
			String city, String state, String country, String FIXEDEXTERNALID, String ExternalIDType ) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSENCAFixed(soapMessage, ModeFlag, PrimaryOfferId, ParentAccount, Fname, Lname, CompanyName, ID, IDType,
				IDExpiry, NotifNumber, Address1, Address2, Address3, Address4, city, state, country, FIXEDEXTERNALID, ExternalIDType);

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

	// Call primary offer swap
	public void callNCAFixed(String ModeFlag, String PrimaryOfferId, String ParentAccount, String Fname, String Lname,
			String CompanyName, String ID, String IDType, String IDExpiry, String NotifNumber, String Address1,
			String Address2, String Address3, String Address4, String city, String state, String country, String FIXEDEXTERNALID,
			String ExternalIDType) {

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
			LOGGER.log(Level.INFO, "Creating Subscriber....");
			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING FIXEDEXTERNALID --> " + FIXEDEXTERNALID);

			SOAPMessage soapResponse = soapConnection.call(createSRNCAFixed(soapAction, ModeFlag, PrimaryOfferId,
					ParentAccount, Fname, Lname, CompanyName, ID, IDType, IDExpiry, NotifNumber, Address1, Address2,
					Address3, Address4, city, state, country, FIXEDEXTERNALID, ExternalIDType), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/NCAFixed.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/NCAFixed.xml");
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

							LOGGER.log(Level.SEVERE, "RESULT FAIL: " + i + "::" + childnodes.item(i).getNodeName()
									+ "::" + childnodes.item(i).getTextContent().trim());
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
				LOGGER.log(Level.INFO, "RESULT SUCCESS: " + FIXEDEXTERNALID);
			}

			LOGGER.log(Level.INFO, "<-- End Processing FIXEDEXTERNALID " + FIXEDEXTERNALID);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			// LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.INFO, "RESULT FAIL: " + FIXEDEXTERNALID);
			LOGGER.log(Level.INFO, "<-- End Processing FIXEDEXTERNALID " + FIXEDEXTERNALID);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

}
