package conerobotprj.dv.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.jdom.CDATA;
import org.opensaml.XML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import weblogic.xml.babel.dtd.Cdata;

public class HuaweiHLRRemoveSub {
	// Initialize logger
	private static Logger LOGGER = null;
	private static String RetDesc = "";
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

	// Create SOAP
	public static void createSOAPHuaweiHLRRMVSubs(SOAPMessage soapMessage, String PhoneNumber, String IMSI) throws SOAPException {

		// SOAP Envelope
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespacebean = "bean";
		String myNamespaceURIbean3 = "http://huawei.com/mds/access/webservice/server/bean";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespacebean, myNamespaceURIbean3);

		SOAPHeader huaweiHeader = envelope.getHeader();

		SOAPBody huaweisoapBody = envelope.getBody();
		SOAPElement SendSyncReq = huaweisoapBody.addChildElement("SendSyncReq", myNamespacebean);

		SOAPElement SyncRequestMsg = SendSyncReq.addChildElement("SyncRequestMsg");
		SOAPElement RequestMessage = SyncRequestMsg.addChildElement("RequestMessage");

		SOAPElement MessageHeader = RequestMessage.addChildElement("MessageHeader");

		SOAPElement SysUser = MessageHeader.addChildElement("SysUser");
		SysUser.addTextNode("pror5c19");

		SOAPElement SysPassword = MessageHeader.addChildElement("SysPassword");
		SysPassword.addTextNode("Huawei12#$");

		SOAPElement MessageBody = RequestMessage.addChildElement("MessageBody");
		SOAPElement BizCode = MessageBody.addChildElement("BizCode");
		BizCode.addTextNode("RMVSUB");

		SOAPElement ParaList = MessageBody.addChildElement("ParaList");

		SOAPElement Para = ParaList.addChildElement("Para");
		SOAPElement Name = Para.addChildElement("Name");
		Name.addTextNode("MSISDN");
		SOAPElement Value = Para.addChildElement("Value");
		Value.addTextNode(PhoneNumber);

		SOAPElement ParaIMSI = ParaList.addChildElement("Para");
		SOAPElement NameIMSI = ParaIMSI.addChildElement("Name");
		NameIMSI.addTextNode("IMSI");
		SOAPElement ValueMISI = ParaIMSI.addChildElement("Value");
		ValueMISI.addTextNode(IMSI);

	
	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRHuaweiHLRRMVSubs(String soapAction, String PhoneNumber, String IMSI) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSOAPHuaweiHLRRMVSubs(soapMessage, PhoneNumber, IMSI);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.INFO, "Request SOAP Message -->" + CDATA.normalizeString(message));
		return soapMessage;
	}
	// End create soap request

	public void callHuaweiHLRQueryRMVSubs(String PhoneNumber, String IMSI) {

		try {

			String soapEndpointUrl = "http://10.134.8.184:8965/provision";
			String soapAction = "http://10.134.8.184:8965/provision";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Huawei HLR Query");
			SOAPMessage soapResponse = soapConnection
					.call(createSRHuaweiHLRRMVSubs(soapAction, PhoneNumber, IMSI), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/HuaweiAddSubResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			// LOGGER.log(Level.INFO, "PROCESSING MSISDN --> " + PhoneNumber);

			File xmlresponse = new File("src/input/HuaweiAddSubResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			NodeList resultlist = doc.getElementsByTagName("RetDesc");
			RetDesc = resultlist.item(0).getTextContent();

			if (resultlist.item(0).getTextContent().contains("succeeded")) {
				LOGGER.log(Level.INFO, "<-- RESULT: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";Provisioning result::" + resultlist.item(0).getTextContent());
			} else {
				LOGGER.log(Level.SEVERE,
						"RESULT FAIL: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";Provisioning result::" + resultlist.item(0).getTextContent());
			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + PhoneNumber + ";IMSI::" + IMSI );

			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT FAIL: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";RetDesc::" + RetDesc);
		}

	}

	// End call Account Contract Renew

}
