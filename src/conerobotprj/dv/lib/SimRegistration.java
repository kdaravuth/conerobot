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
public class SimRegistration {

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
	public static void createSEAccountInfoUpdate(SOAPMessage soapMessage, String AccountNo, String MSISDN, String Fname,
			String Lname, String DoB, String Gender, String ID, String IDType, String Nationality, String PostalCode,
			String Add1, String Add2, String Add3, String Add4, String City, String CountryCode, String DayPhone,
			String EveningPhone) throws SOAPException {

		// Get subscr_no and account no from MSISDN - using subscriber Retrieve
		SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
		src.retreiveCred(new File("src/config/soapconnection.cfg"));
		src.callSubscriberRetrieveService(MSISDN);
		// END: Get subscr_no and subsc no reset from MSISDN - using subscriber Retrieve

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement SubscriberUpdate = c1soapBody.addChildElement("SubscriberUpdate", myNamespace);
		SOAPElement input = SubscriberUpdate.addChildElement("input", myNamespace);

		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode("sapi");
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);

		SOAPElement subscriber = input.addChildElement("subscriber");

		SOAPElement serviceInternalId = subscriber.addChildElement("serviceInternalId");
		serviceInternalId.addAttribute(setQname, "true");
		serviceInternalId.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdValue = serviceInternalId.addChildElement("value");
		serviceInternalIdValue.addTextNode(src.serviceInternalId);

		SOAPElement serviceInternalIdResets = subscriber.addChildElement("serviceInternalIdResets");
		serviceInternalIdResets.addAttribute(setQname, "true");
		serviceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement serviceInternalIdResetsValue = serviceInternalIdResets.addChildElement("value");
		serviceInternalIdResetsValue.addTextNode(src.serviceInternalIdResets);

		SOAPElement serviceLname = subscriber.addChildElement("serviceLname");
		SOAPElement serviceLnameValue = serviceLname.addChildElement("value");
		serviceLnameValue.addTextNode(Lname);

		SOAPElement serviceFname = subscriber.addChildElement("serviceFname");
		SOAPElement serviceFnameValue = serviceFname.addChildElement("value");
		serviceFnameValue.addTextNode(Fname);

		SOAPElement birthday = subscriber.addChildElement("birthday");
		SOAPElement birthdayValue = birthday.addChildElement("value");
		birthdayValue.addTextNode(DoB + "T00:00:00.0");

		// ID,// IDType
		// Updating account id and id type
		SimRegistrationIDonAccount sa = new SimRegistrationIDonAccount();
		sa.retreiveCred(new File("src/config/soapconnection.cfg"));
		sa.callingAccountInfoUpdate(AccountNo, ID, IDType);
		// End Account Update

		// Nationality and Gender

		/// Start replacing old ED with new ED
		SimRegistrationSubsExt sse = new SimRegistrationSubsExt();
		sse.retreiveCred(new File("src/config/soapconnection.cfg"));
		sse.callSubscriberRetrieveService(MSISDN);

		String line;
		String ed;
		Integer start;
		Integer end;
		String DOBOrig, IDTYPEOrig;
		String DOBReplaced, IDTYPEReplaced;
		String EDUpdated = "";
		String EDClean;

		////////////// GET Extended Data and replace//////////////////////

		ed = sse.SubExtendedData;
		start = ed.indexOf("<Parameter name=\"subs_gender\">");
		end = ed.indexOf("</Parameter>", start);

		DOBOrig = ed.substring(start, end + 12);
		DOBReplaced = "<Parameter name=\"subs_gender\"><StringValue>" + Gender + "</StringValue></Parameter>";
		EDUpdated = ed.replace(DOBOrig, DOBReplaced);

		// LOGGER.log(Level.INFO, "1==> " + EDUpdated);

		start = ed.indexOf("<Parameter name=\"Nationality\">");
		if (start == -1) {

			start = ed.indexOf(
					"<Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Nationality\" xsi:nil=\"true\">");
			end = ed.indexOf("</Parameter>", start);
			IDTYPEOrig = ed.substring(start, end + 12);
			IDTYPEReplaced = "<Parameter xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" name=\"Nationality\" xsi:nil=\"true\"><IntegerValue>"
					+ Nationality + "</IntegerValue></Parameter>";

			EDUpdated = EDUpdated.replace(IDTYPEOrig, IDTYPEReplaced);
			// LOGGER.log(Level.INFO, "2==> " + EDUpdated);

		} else {

			end = ed.indexOf("</Parameter>", start);
			IDTYPEOrig = ed.substring(start, end + 12);
			IDTYPEReplaced = "<Parameter name=\"Nationality\"><IntegerValue>" + Nationality
					+ "</IntegerValue></Parameter>";

			EDUpdated = EDUpdated.replace(IDTYPEOrig, IDTYPEReplaced);
			// LOGGER.log(Level.INFO, "2==> " + EDUpdated);

		}
		
		
		SOAPElement extendedData = subscriber.addChildElement("extendedData");
		extendedData.addAttribute(setQname, "true");
		extendedData.addAttribute(changedQname, "true");
		SOAPElement extendedDataValue = extendedData.addChildElement("value");
		String EDD = EDUpdated;
		
		System.out.println(EDD);
		extendedDataValue.addTextNode(CDATA.normalizeString(EDD));
				
		///////////////////////////////////////////////////////////////////

		/// END Nationality and Gender

		// Postal code, Add1, Add2, Add3, Add4, CountryCode
		SimRegistrationAddressCreation sac = new SimRegistrationAddressCreation();
		SimRegistrationAddressAssoc saa = new SimRegistrationAddressAssoc();
		SimRegistrationAddressAssocEnabled sae = new SimRegistrationAddressAssocEnabled();
		sac.retreiveCred(new File("src/config/soapconnection.cfg"));
		saa.retreiveCred(new File("src/config/soapconnection.cfg"));
		sae.retreiveCred(new File("src/config/soapconnection.cfg"));
		sac.callAddressCreation(Add1, Add2, Add3, Add4, City);
		saa.callAddressAssoc(sac.AddressID, src.serviceInternalId, src.serviceInternalIdResets, AccountNo);
		sae.callAddressAssoc(saa.subscriberAddressAssocId);

		// Day phone
		SOAPElement servicePhone = subscriber.addChildElement("servicePhone");
		servicePhone.addAttribute(setQname, "true");
		servicePhone.addAttribute(changedQname, "true");
		SOAPElement servicePhoneValue = servicePhone.addChildElement("value");
		servicePhoneValue.addTextNode(DayPhone);

		// Evening Phone
		SOAPElement servicePhone2 = subscriber.addChildElement("servicePhone2");
		servicePhone2.addAttribute(setQname, "true");
		servicePhone2.addAttribute(changedQname, "true");
		SOAPElement servicePhone2Value = servicePhone2.addChildElement("value");
		servicePhone2Value.addTextNode(EveningPhone);

		SOAPElement autoCommitOrder = input.addChildElement("autoCommitOrder");
		autoCommitOrder.addTextNode("1");

	}// End create Extended Data add envelope
		// Create SOAP request

	private static SOAPMessage createSOAPRequest(String soapAction, String AccountNo, String MSISDN, String Fname,
			String Lname, String DoB, String Gender, String ID, String IDType, String Nationality, String PostalCode,
			String Add1, String Add2, String Add3, String Add4, String City, String CountryCode, String DayPhone,
			String EveningPhone) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEAccountInfoUpdate(soapMessage, AccountNo, MSISDN, Fname, Lname, DoB, Gender, ID, IDType, Nationality,
				PostalCode, Add1, Add2, Add3, Add4, City, CountryCode, DayPhone, EveningPhone);

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
	public static void callingSubInfoUpdate(String AccountNo, String MSISDN, String Fname, String Lname, String DoB,
			String Gender, String ID, String IDType, String Nationality, String PostalCode, String Add1, String Add2,
			String Add3, String Add4, String City, String CountryCode, String DayPhone, String EveningPhone) {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/SubscriberService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Inner SUB Information Updates..");
			SOAPMessage soapResponse = soapConnection.call(
					createSOAPRequest(soapAction, AccountNo, MSISDN, Fname, Lname, DoB, Gender, ID, IDType, Nationality,
							PostalCode, Add1, Add2, Add3, Add4, City, CountryCode, DayPhone, EveningPhone),
					soapEndpointUrl);

			// Writing soap response to stream so that it's easily to print out

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/SimRegUpdate.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "Simplified Results --> " + MSISDN);

			File xmlresponse = new File("src/input/SimRegUpdate.xml");
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
									"RESULT::FAIL SIMREGISTRATIN UPDATE : " + "::" + childnodes.item(i).getNodeName() + "::"
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

				LOGGER.log(Level.INFO, "RESULT::PASS SIMREGISTRATIN UPDATE : " + tempSubInfo);
				// LOGGER.log(Level.INFO, extendedData);

			}

			LOGGER.log(Level.INFO, "<-- End Simplified result: " + MSISDN);
			LOGGER.log(Level.FINEST, "--------------------------------------");

		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT::FAIL SIMREGISTRATIN UPDATE : " + e.getCause().toString());

		}
	}// End calling soap
	
	
}
