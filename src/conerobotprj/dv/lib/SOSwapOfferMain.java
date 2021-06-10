/**
 * Bulk Add SO
 */
package conerobotprj.dv.lib;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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
public class SOSwapOfferMain {

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
	// END: Create SOAP request
	public static void callingSOSwapOfferMain(String subscrNo, String subscrNoReset, String swappedInOffer, String swappedOutOffer)
			throws FileNotFoundException, IOException {

		try {
			
			//Calling add offer
			SOSwapOfferAdd SOADD = new SOSwapOfferAdd();
			SOADD.retreiveCred(new File("src/config/soapconnection.cfg"));
			//LOGGER.log(Level.INFO,"order id: " + SOADD.orderId );
			SOADD.callingSOSwapOfferAdd(subscrNo, subscrNoReset, swappedInOffer);
			if (SOADD.orderId != null) {
			//Calling Offer disconnect
			SOSwapOfferDisconnect SODIS = new SOSwapOfferDisconnect();
			SODIS.retreiveCred(new File("src/config/soapconnection.cfg"));
			SODIS.callingOfferDisconnect(subscrNo, subscrNoReset, swappedOutOffer, SOADD.orderId, SOADD.serviceOrderId);

			//Committing order
			
			SOSwapOrderCommitted com = new SOSwapOrderCommitted();
			com.retreiveCred(new File("src/config/soapconnection.cfg"));
			SOSwapOrderCommitted.callingCommitOrder(SOADD.orderId);
			}else {
				LOGGER.log(Level.SEVERE, "RESULT FAIL "+ subscrNo+ " [No Order Creation. Offer SWAP Fail]");
			}
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE, e.getCause().toString());
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request

}
