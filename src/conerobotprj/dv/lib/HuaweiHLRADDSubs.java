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

public class HuaweiHLRADDSubs {
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
	public static void createSOAPHuaweiHLRADDSubs(SOAPMessage soapMessage, String PhoneNumber, String IMSI,
			String KIVALUE, String TPLID) throws SOAPException {

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
		BizCode.addTextNode("ADDSUB");

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

		SOAPElement ParaKI = ParaList.addChildElement("Para");
		SOAPElement NameKI = ParaKI.addChildElement("Name");
		NameKI.addTextNode("KIVALUE");
		SOAPElement ValueKI = ParaKI.addChildElement("Value");
		ValueKI.addTextNode(KIVALUE);

		SOAPElement ParaHLR = ParaList.addChildElement("Para");
		SOAPElement NameHLR = ParaHLR.addChildElement("Name");
		NameHLR.addTextNode("HLRSN");
		SOAPElement ValueHLR = ParaHLR.addChildElement("Value");
		ValueHLR.addTextNode("1");

		SOAPElement ParaTPLID = ParaList.addChildElement("Para");
		SOAPElement NameTPLID = ParaTPLID.addChildElement("Name");
		NameTPLID.addTextNode("TPLID");
		SOAPElement ValueTPLID = ParaTPLID.addChildElement("Value");
		ValueTPLID.addTextNode(TPLID);

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRHuaweiHLRADDSubs(String soapAction, String PhoneNumber, String IMSI,
			String KIVALUE, String TPLID) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSOAPHuaweiHLRADDSubs(soapMessage, PhoneNumber, IMSI, KIVALUE, TPLID);

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

	public void callHuaweiHLRQueryADDSubs(String PhoneNumber, String IMSI, String KIVALUE, String TPLID) {

		try {

			String soapEndpointUrl = "http://10.134.8.184:8965/provision";
			String soapAction = "http://10.134.8.184:8965/provision";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Huawei HLR Query");
			SOAPMessage soapResponse = soapConnection
					.call(createSRHuaweiHLRADDSubs(soapAction, PhoneNumber, IMSI, KIVALUE, TPLID), soapEndpointUrl);

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
				LOGGER.log(Level.INFO, "<-- RESULT: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";TPLID#::" + TPLID
						+ ";Provisioning result::" + resultlist.item(0).getTextContent());
			} else {
				LOGGER.log(Level.SEVERE,
						"RESULT FAIL: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";TPLID#::" + TPLID+ ";Provisioning result::" + resultlist.item(0).getTextContent());
			}

			LOGGER.log(Level.INFO, "<-- End Processing MSISDN " + PhoneNumber + ";IMSI::" + IMSI + ";TPLID#::" + TPLID);

			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, "RESULT FAIL: MSISDN::" + PhoneNumber + ";IMSI::" + IMSI + ";TPLID#::" + TPLID+ ";Provisioning result::" + RetDesc);
		}

	}

	// End call Account Contract Renew

}
