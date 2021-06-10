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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author khfighter
 *
 */
public class AccountCreation {
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
	public static void createSEAccountCreation(SOAPMessage soapMessage, String Fname, String Lname,
			String Cname, String AccountType, String PODetails, String NotificationNumber, String OrigAccount, String MarketCode ) throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement AccountCreate = c1soapBody
				.addChildElement("AccountCreate", myNamespace);
		SOAPElement input = AccountCreate.addChildElement("input", myNamespace);
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");

		SOAPElement realmv = input.addChildElement("realm");
		realmv.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userid = input.addChildElement("userIdName");
		userid.addTextNode(username);

		SOAPElement account = input.addChildElement("account");
		SOAPElement attribs = account.addChildElement("attribs");
		attribs.addTextNode("0");
		
		SOAPElement accountType = account.addChildElement("accountType");
		accountType.addAttribute(setQname, "true");
		accountType.addAttribute(changedQname, "true");
		SOAPElement value = accountType.addChildElement("value");
		value.addTextNode(AccountType);
		
		SOAPElement billFname = account.addChildElement("billFname");
		billFname.addAttribute(setQname, "true");
		billFname.addAttribute(changedQname, "true");
		SOAPElement fvalue = billFname.addChildElement("value");
		fvalue.addTextNode(Fname);

		SOAPElement billLname = account.addChildElement("billLname");
		billLname.addAttribute(setQname, "true");
		billLname.addAttribute(changedQname, "true");
		SOAPElement Lvalue = billLname.addChildElement("value");
		Lvalue.addTextNode(Lname);
		
		SOAPElement billCompany = account.addChildElement("billCompany");
		billCompany.addAttribute(setQname, "true");
		billCompany.addAttribute(changedQname, "true");
		SOAPElement Cvalue = billCompany.addChildElement("value");
		Cvalue.addTextNode(Cname);
		
		
		SOAPElement custCompanyName = account.addChildElement("custCompanyName");
		custCompanyName.addAttribute(setQname, "true");
		custCompanyName.addAttribute(changedQname, "true");
		SOAPElement CustCvalue = custCompanyName.addChildElement("value");
		CustCvalue.addTextNode(Cname);
		
		
		SOAPElement lastName = account.addChildElement("lastName");
		lastName.addAttribute(setQname, "true");
		lastName.addAttribute(changedQname, "true");
		SOAPElement CustLvalue = lastName.addChildElement("value");
		CustLvalue.addTextNode(Lname);
		
		SOAPElement firstName = account.addChildElement("firstName");
		firstName.addAttribute(setQname, "true");
		firstName.addAttribute(changedQname, "true");
		SOAPElement CustFvalue = firstName.addChildElement("value");
		CustFvalue.addTextNode(Fname);
		
		
		SOAPElement mktCode = account.addChildElement("mktCode");
		mktCode.addAttribute(setQname, "true");
		mktCode.addAttribute(changedQname, "true");
		SOAPElement mktCodevalue = mktCode.addChildElement("value");
		mktCodevalue.addTextNode(MarketCode);
		
		///purchaseOrder
		
		SOAPElement purchaseOrder = account.addChildElement("purchaseOrder");
		purchaseOrder.addAttribute(setQname, "true");
		purchaseOrder.addAttribute(changedQname, "true");
		SOAPElement purchaseOrderValue = purchaseOrder.addChildElement("value");
		purchaseOrderValue.addTextNode(PODetails);
		
		String cdata = "<ExtendedData><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"sim_register_dealer\" xsi:nil=\"true\"/><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Work Permit No\" xsi:nil=\"true\"/><Parameter name=\"registration_complete\"><BooleanValue>false</BooleanValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Telephone Number\" xsi:nil=\"true\"/><Parameter name=\"coll_number\"><StringValue>"+ NotificationNumber+"</StringValue></Parameter><Parameter name=\"Legacy Account ID\"><StringValue>"+OrigAccount+"</StringValue></Parameter><Parameter name=\"Work Permit Date of Expiry\"><DateValue>2020-01-1T00:00:00.0</DateValue></Parameter><Parameter name=\"ExpiryDates\"><DateValue>2025-02-2T00:00:00.0</DateValue></Parameter><Parameter name=\"Customer_DOB\"><DateValue>1980-01-01T00:00:00.0</DateValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"AccountBoolean\" xsi:nil=\"true\"/><Parameter name=\"ITC_SCORE\"><StringValue>4</StringValue></Parameter><Parameter name=\"id_type\"><StringValue>anonymous_id</StringValue></Parameter><Parameter name=\"ITC_REFRESH\"><BooleanValue>false</BooleanValue></Parameter><Parameter name=\"ConeOblicoreFlag\"><StringValue>Null</StringValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"AccountNumber\" xsi:nil=\"true\"/><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Account Manager\" xsi:nil=\"true\"/><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Residence Permit No\" xsi:nil=\"true\"/><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"AccountString\" xsi:nil=\"true\"/><Parameter name=\"Personal ID\"><StringValue>233352263</StringValue></Parameter><Parameter name=\"Name\"><StringValue>ACCOUNT</StringValue></Parameter><Parameter name=\"Marital Status\"><StringValue>Married</StringValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"AccountCurrency\" xsi:nil=\"true\"/><Parameter name=\"Residence Permit Date of Expiry\"><DateValue>2020-01-1T00:00:00.0</DateValue></Parameter><Parameter name=\"BillingType\"><StringValue>Prepaid</StringValue></Parameter><Parameter name=\"Email Address\"><StringValue>hello@gmail.com</StringValue></Parameter><Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"AccountDate\" xsi:nil=\"true\"/></ExtendedData>";
			
		SOAPElement extendedData = account.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");
		SOAPElement extendedDatavalue = extendedData.addChildElement("value");
		extendedDatavalue.addTextNode(CDATA.normalizeString(cdata));
		

		SOAPElement hierarchyAccountInternalId = account.addChildElement("parentAccountInternalId");
		hierarchyAccountInternalId.addAttribute(setQname, "true");
		hierarchyAccountInternalId.addAttribute(changedQname, "true");
		SOAPElement hierarchyAccountInternalIdValue = hierarchyAccountInternalId.addChildElement("value");
		hierarchyAccountInternalIdValue.addTextNode(OrigAccount);


		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder"); // <autoCommitOrder>?</autoCommitOrder>
		autoCommitOrder.addTextNode("true");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRAccountCreation(String soapAction, String Fname, String Lname,
			String Cname, String AccountType, String PODetails, String NotificationNumber, String OrigAccount, String MarketCode) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAccountCreation(soapMessage,  Fname,  Lname,
				 Cname,  AccountType,  PODetails,  NotificationNumber, OrigAccount, MarketCode);

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
	public void callAccountCreation(String Fname, String Lname,
			String Cname, String AccountType, String PODetails, String NotificationNumber, String OrigAccount, String MarketCode) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AccountService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Creating Accounts..." + Fname + " " + Lname + " " + Cname);
			

			SOAPMessage soapResponse = soapConnection
					.call(createSRAccountCreation(soapAction,  Fname,  Lname,
							 Cname,  AccountType, PODetails,  NotificationNumber,  OrigAccount,  MarketCode), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AccountCreationResponse.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/AccountCreationResponse.xml");
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
				LOGGER.log(Level.INFO, "RESULT SUCCESS: " + tempSubInfo);
			}

			LOGGER.log(Level.INFO, "<-- End Processing  " + Fname + " " + Lname + " " + Cname);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			// LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.INFO, "RESULT FAIL: "  + Fname + " " + Lname + " " + Cname);
			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + Fname + " " + Lname + " " + Cname);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

}
