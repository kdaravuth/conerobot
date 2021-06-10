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
public class InvoiceDetailsAdj {
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
			String vamount, String vannotation, String vorigBillRefNo, String vorigBillRefResets, String TransCode, String vbillInvoiceRow) throws SOAPException {

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement InvoiceDetailAdjust = c1soapBody.addChildElement("InvoiceDetailAdjust", myNamespace);
		SOAPElement input = InvoiceDetailAdjust.addChildElement("input", myNamespace);
		SOAPElement productVersion = input.addChildElement("productVersion");
		productVersion.addTextNode("CCBS3.0");
		SOAPElement realme = input.addChildElement("realm");
		realme.addTextNode(realm);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode("RCS");
		
		SOAPElement ServerIdLocator = input.addChildElement("ServerIdLocator");
		
		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");
		SOAPElement ratingServerId = ServerIdLocator.addChildElement("ratingServerId");
		ratingServerId.addTextNode("9");
				
		SOAPElement invoiceDetail = input.addChildElement("invoiceDetail");
		SOAPElement attribs = invoiceDetail.addChildElement("attribs");
		attribs.addTextNode("0");
		
		SOAPElement billRefNo = invoiceDetail.addChildElement("billRefNo");
		billRefNo.addAttribute(setQname, "true");
		billRefNo.addAttribute(changedQname, "value");
		SOAPElement billRefNoValue = billRefNo.addChildElement("value");
		billRefNoValue.addTextNode(vorigBillRefNo);
		
		SOAPElement billRefResets = invoiceDetail.addChildElement("billRefResets");
		billRefResets.addAttribute(setQname, "true");
		billRefResets.addAttribute(changedQname, "true");
		SOAPElement billRefResetsValue = billRefResets.addChildElement("value");
		billRefResetsValue.addTextNode(vorigBillRefResets);
		
		SOAPElement billInvoiceRow = invoiceDetail.addChildElement("billInvoiceRow");
		billInvoiceRow.addAttribute(setQname, "true");
		billInvoiceRow.addAttribute(changedQname, "true");
		SOAPElement billInvoiceRowValue = billInvoiceRow.addChildElement("value");
		billInvoiceRowValue.addTextNode(vbillInvoiceRow);
		
		
		SOAPElement transCode = input.addChildElement("transCode");
		transCode.addTextNode(TransCode);
		
		SOAPElement adjReasonCode = input.addChildElement("adjReasonCode");
		adjReasonCode.addTextNode(vadjReasonCode);
		
		SOAPElement requestStatus = input.addChildElement("requestStatus");
		requestStatus.addTextNode("1");
		
		SOAPElement annotation = input.addChildElement("annotation");
		annotation.addTextNode(vannotation);
		
		SOAPElement primaryUnitsType = input.addChildElement("primaryUnitsType");
		primaryUnitsType.addTextNode("403");
		
		SOAPElement primaryUnits = input.addChildElement("primaryUnits");
		primaryUnits.addTextNode(vamount);
		
		SOAPElement amt = input.addChildElement("amt");
		amt.addTextNode(vamount);
			

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRPostBillAdj(String soapAction, String AccountNo, String vadjReasonCode,
			String vamount, String vannotation, String vorigBillRefNo, String vorigBillRefResets,  String TransCode, String vbillInvoiceRow ) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEPostbillAdjustment(soapMessage, AccountNo, vadjReasonCode, vamount, vannotation, vorigBillRefNo,
				vorigBillRefResets,    TransCode,  vbillInvoiceRow);

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
			String vorigBillRefNo, String vorigBillRefResets,  String TransCode, String vbillInvoiceRow) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/InvoiceDetailService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/InvoiceDetailService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Calling adjustment on invoice details");
			SOAPMessage soapResponse = soapConnection.call(createSRPostBillAdj(soapAction, AccountNo, vadjReasonCode,
					vamount, vannotation, vorigBillRefNo, vorigBillRefResets,  TransCode, vbillInvoiceRow), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/InvoiceDetailsAdjResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + AccountNo + " ; Bill_ref_no: " + vorigBillRefNo);

			File xmlresponse = new File("src/input/InvoiceDetailsAdjResponse.xml");
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

							LOGGER.log(Level.SEVERE, "RESULT FAIL Invoice Details ADJ: " + i + "::" + childnodes.item(i).getNodeName()
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
									"RESULT SUCCESS Invoice Details ADJ: " + i + "::"
											+ childnodes.item(i).getNodeName() + "::"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}

			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + AccountNo + " ; Bill_ref_no: " + vorigBillRefNo + " ; row: "+ vbillInvoiceRow);
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE,  "RESULT FAIL: " + e.toString());
		}

	}

	// End call

}
