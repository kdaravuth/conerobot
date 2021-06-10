/**
 * Postbill adjustment function
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
public class PostBillAdj2 {
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
	// Create SOAP for Bill Adjustment
	public static void createSEPostbillAdjustment(SOAPMessage soapMessage, String AccountNo, String vadjReasonCode,
			String vamount, String vannotation, String vorigBillRefNo, String vorigBillRefResets, String origBillSequenceNumber, 
			String origRcTermInstId, String origSplitRowNum, String serviceInternalId, String serviceInternalIdResets, String TransCode, String transTargetType) throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement AdjustmentCreate = c1soapBody.addChildElement("AdjustmentCreate", myNamespace);
		SOAPElement input = AdjustmentCreate.addChildElement("input", myNamespace);
		SOAPElement productVersion = input.addChildElement("productVersion");
		productVersion.addTextNode("CCBS3.0");
		SOAPElement realme = input.addChildElement("realm");
		realme.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode("RCS");
		SOAPElement adjustment = input.addChildElement("adjustment");
		SOAPElement attribs = adjustment.addChildElement("attribs");
		attribs.addTextNode("0");

		SOAPElement accountInternalId = adjustment.addChildElement("accountInternalId");
		accountInternalId.addAttribute(changedQname, "false");
		accountInternalId.addAttribute(setQname, "false");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(AccountNo);
		
		SOAPElement vserviceInternalId = adjustment.addChildElement("serviceInternalId");
		vserviceInternalId.addAttribute(setQname, "true");
		vserviceInternalId.addAttribute(changedQname, "true");
		SOAPElement vserviceInternalIdValue = vserviceInternalId.addChildElement("value");
		vserviceInternalIdValue.addTextNode(serviceInternalId);
		
		SOAPElement vserviceInternalIdResets = adjustment.addChildElement("serviceInternalIdResets");
		vserviceInternalIdResets.addAttribute(setQname, "true");
		vserviceInternalIdResets.addAttribute(changedQname, "true");
		SOAPElement vserviceInternalIdResetsValue = vserviceInternalIdResets.addChildElement("value");
		vserviceInternalIdResetsValue.addTextNode(serviceInternalIdResets);

		SOAPElement adjReasonCode = adjustment.addChildElement("adjReasonCode");
		adjReasonCode.addAttribute(setQname, "true");
		adjReasonCode.addAttribute(changedQname, "true");
		SOAPElement adjReasonCodeValue = adjReasonCode.addChildElement("value");
		adjReasonCodeValue.addTextNode(vadjReasonCode);

		SOAPElement amount = adjustment.addChildElement("amount");
		amount.addAttribute(setQname, "true");
		amount.addAttribute(changedQname, "true");
		SOAPElement amountValue = amount.addChildElement("value");
		amountValue.addTextNode(vamount);

		SOAPElement annotation = adjustment.addChildElement("annotation");
		annotation.addAttribute(setQname, "true");
		annotation.addAttribute(changedQname, "true");
		SOAPElement annotationValue = annotation.addChildElement("value");
		annotationValue.addTextNode(vannotation);

		
		SOAPElement origBillRefNo = adjustment.addChildElement("origBillRefNo");
		origBillRefNo.addAttribute(setQname, "true");
		origBillRefNo.addAttribute(changedQname, "true");
		SOAPElement origBillRefNoValue = origBillRefNo.addChildElement("value");
		origBillRefNoValue.addTextNode(vorigBillRefNo);

		SOAPElement origBillRefResets = adjustment.addChildElement("origBillRefResets");
		origBillRefResets.addAttribute(setQname, "true");
		origBillRefResets.addAttribute(changedQname, "true");
		SOAPElement origBillRefResetsValue = origBillRefResets.addChildElement("value");
		origBillRefResetsValue.addTextNode(vorigBillRefResets);

		SOAPElement origType = adjustment.addChildElement("origType");
		origType.addAttribute(setQname, "true");
		origType.addAttribute(changedQname, "true");
		SOAPElement origTypeValue = origType.addChildElement("value");
		origTypeValue.addTextNode("0");

		SOAPElement requestStatus = adjustment.addChildElement("requestStatus");
		requestStatus.addAttribute(changedQname, "true");
		requestStatus.addAttribute(setQname, "true");
		SOAPElement requestStatusValue = requestStatus.addChildElement("value");
		requestStatusValue.addTextNode("1");

		SOAPElement primaryUnitType = adjustment.addChildElement("primaryUnitType");
		primaryUnitType.addAttribute(setQname, "true");
		primaryUnitType.addAttribute(changedQname, "true");
		SOAPElement primaryUnitTypeValue = primaryUnitType.addChildElement("value");
		primaryUnitTypeValue.addTextNode("403");

		SOAPElement primaryUnits = adjustment.addChildElement("primaryUnits");
		primaryUnits.addAttribute(setQname, "true");
		primaryUnits.addAttribute(changedQname, "true");
		SOAPElement primaryUnitsValue = primaryUnits.addChildElement("value");
		primaryUnitsValue.addTextNode(vamount);

		SOAPElement transCode = adjustment.addChildElement("transCode");
		transCode.addAttribute(setQname, "true");
		transCode.addAttribute(changedQname, "true");
		SOAPElement transCodeValue = transCode.addChildElement("value");
		transCodeValue.addTextNode(TransCode);
		
		//transTargetType
		/*SOAPElement vtransTargetType = adjustment.addChildElement("transTargetType");
		vtransTargetType.addAttribute(setQname, "true");
		vtransTargetType.addAttribute(changedQname, "true");
		SOAPElement vtransTargetTypeValue = vtransTargetType.addChildElement("value");
		vtransTargetTypeValue.addTextNode(transTargetType);*/
		
		//billingLevel
		
		/*SOAPElement vbillingLevel = adjustment.addChildElement("billingLevel");
		vbillingLevel.addAttribute(setQname, "true");
		vbillingLevel.addAttribute(changedQname, "true");
		SOAPElement vbillingLevelValue = vbillingLevel.addChildElement("value");
		vbillingLevelValue.addTextNode("1");*/
		
		if (!origSplitRowNum.equals("NA") && !origRcTermInstId.equals("NA") && !origBillSequenceNumber.equals("NA") ) {
			
			SOAPElement vorigSplitRowNum = adjustment.addChildElement("origSplitRowNum");
			SOAPElement vorigSplitRowNumValue = vorigSplitRowNum.addChildElement("value");
			vorigSplitRowNumValue.addTextNode(origSplitRowNum);
			
			SOAPElement vorigRcTermInstId = adjustment.addChildElement("origRcTermInstId");
			SOAPElement vorigRcTermInstIdValue = vorigRcTermInstId.addChildElement("value");
			vorigRcTermInstIdValue.addTextNode(origRcTermInstId);
			
			SOAPElement vorigBillSequenceNumber = adjustment.addChildElement("origBillSequenceNumber");
			SOAPElement vorigBillSequenceNumberValue = vorigBillSequenceNumber.addChildElement("value");
			vorigBillSequenceNumberValue.addTextNode(origBillSequenceNumber);
			
			
		}
			SOAPElement openItemId = adjustment.addChildElement("openItemId");
			openItemId.addAttribute(setQname, "true");
			openItemId.addAttribute(changedQname, "true");
			SOAPElement openItemIdValue = openItemId.addChildElement("value");
			openItemIdValue.addTextNode("1");

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRPostBillAdj(String soapAction, String AccountNo, String vadjReasonCode,
			String vamount, String vannotation, String vorigBillRefNo, String vorigBillRefResets, String origBillSequenceNumber, String origRcTermInstId, String origSplitRowNum,String serviceInternalId, String serviceInternalIdResets, String TransCode, String transTargetType) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEPostbillAdjustment(soapMessage, AccountNo, vadjReasonCode, vamount, vannotation, vorigBillRefNo,
				vorigBillRefResets,  origBillSequenceNumber,  origRcTermInstId,  origSplitRowNum, serviceInternalId, serviceInternalIdResets, TransCode, transTargetType);

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

	public void callPostBillAdj(String AccountNo, String vadjReasonCode, String vamount, String vannotation,
			String vorigBillRefNo, String vorigBillRefResets, String origBillSequenceNumber, String origRcTermInstId, String origSplitRowNum, String serviceInternalId, String serviceInternalIdResets, String TransCode, String transTargetType) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AdjustmentService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/AdjustmentService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling Post Bill Adjustment Function");
			SOAPMessage soapResponse = soapConnection.call(createSRPostBillAdj(soapAction, AccountNo, vadjReasonCode,
					vamount, vannotation, vorigBillRefNo, vorigBillRefResets, origBillSequenceNumber, origRcTermInstId, origSplitRowNum, serviceInternalId,serviceInternalIdResets,TransCode, transTargetType), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/PostBillAdjResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + AccountNo + " ; Bill_ref_no: " + vorigBillRefNo);

			File xmlresponse = new File("src/input/PostBillAdjResponse.xml");
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

							LOGGER.log(Level.INFO,
									"RESULT SUCCESS Post Bill Adjustment: " + i + "::"
											+ childnodes.item(i).getNodeName() + "::"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + AccountNo + " ; Bill_ref_no: " + vorigBillRefNo + " ; rc_term_inst_id: " + origRcTermInstId +"::"+ origBillSequenceNumber);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE,  "RESULT FAIL: " + e.toString());
		}

	}

	// End call

}
