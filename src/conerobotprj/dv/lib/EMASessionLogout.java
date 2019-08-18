package conerobotprj.dv.lib;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

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

public class EMASessionLogout {
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

	// Create SOAP
	public static void createSOAPLogOutEMASession(SOAPMessage soapMessage, String session) throws SOAPException {

		// SOAP Envelope

		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespaceCai3 = "cai3";
		String myNamespaceURICai3 = "http://schemas.ericsson.com/cai3g1.2/";

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespaceCai3, myNamespaceURICai3);

		SOAPHeader emasoapHeader = envelope.getHeader();
		SOAPElement SessionIdHead = emasoapHeader.addChildElement("SessionId", myNamespaceCai3);
		SessionIdHead.addTextNode(session);
//<cai3:Logout>

		SOAPBody emasoapBody = envelope.getBody();
		SOAPElement emalogout = emasoapBody.addChildElement("Logout", myNamespaceCai3);
		SOAPElement SessionIdBody = emalogout.addChildElement("SessionId", myNamespaceCai3);
		SessionIdBody.addTextNode(session);

	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRLogOutEMASession(String soapAction, String session) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSOAPLogOutEMASession(soapMessage, session);

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

	public void callLogOutEMASession(String session) {

		try {

			String soapEndpointUrl = "http://10.11.0.50:8998/cai3g1_2/SessionControl/";
			String soapAction = "http://10.11.0.50:8998/cai3g1_2/SessionControl/?wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.INFO, "Closing session to EMA");
			SOAPMessage soapResponse = soapConnection.call(createSRLogOutEMASession(soapAction, session),
					soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.INFO, "Response SOAP..." + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/CloseSessionResponse.xml")));
			soapConnection.close();

			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.toString());
		}

	}

	// End call Account Contract Renew

}
