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
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
public class imagedocumentid {

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
	public static String fileurl = "";
	public static String maxDocumentID = "";
	public static float fileSize;

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

	// Create offer Add soap envelope
	public static void createSEImageDocumentIDFind(SOAPMessage soapMessage, String VbillRefNo, String VbillRefNoResets)
			throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement CustomerDocumentURLAssemble = c1soapBody.addChildElement("InvoiceDocumentRetrieveAll", myNamespace);

		SOAPElement SAOIInput = CustomerDocumentURLAssemble.addChildElement("input", myNamespace);
		SOAPElement SecurityToken = SAOIInput.addChildElement("securityToken");
		SecurityToken.addTextNode(token);

		SOAPElement Usr = SAOIInput.addChildElement("userIdName");
		Usr.addTextNode(username);

		SOAPElement realm = SAOIInput.addChildElement("realm");
		realm.addTextNode("SAPI");

		SOAPElement ServerIdLocator = SAOIInput.addChildElement("ServerIdLocator");

		SOAPElement billingServerId = ServerIdLocator.addChildElement("billingServerId");
		billingServerId.addTextNode("43");
		SOAPElement ratingServerId = ServerIdLocator.addChildElement("ratingServerId");
		ratingServerId.addTextNode("9");

		SOAPElement billRefNo = SAOIInput.addChildElement("billRefNo");
		billRefNo.addTextNode(VbillRefNo);

		SOAPElement billRefResets = SAOIInput.addChildElement("billRefResets");
		billRefResets.addTextNode(VbillRefNoResets);

	}
	// End create offer add envelope

	// Create SOAP request
	private static SOAPMessage createSOAPRequestImageDocumentIDFind(String soapAction, String VbillRefNo,
			String VbillRefNoResets) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEImageDocumentIDFind(soapMessage, VbillRefNo, VbillRefNoResets);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", soapAction);

		soapMessage.saveChanges();

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		soapMessage.writeTo(stream);
		String message = new String(stream.toByteArray(), "utf-8");

		/* Print the request message, just for debugging purposes */
		LOGGER.log(Level.FINEST, "<------Request SOAP Message -->" + message);

		return soapMessage;
	}

	// END: Create SOAP request
	public static void callingImageDocumentIDFind(String VbillRefNo, String VbillRefNoResets)
			throws FileNotFoundException, IOException {

		try {

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/CustomerDocumentService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/CustomerDocumentService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server
			LOGGER.log(Level.FINEST, "billimagefind started...");
			SOAPMessage soapResponse = soapConnection.call(
					createSOAPRequestImageDocumentIDFind(soapAction, VbillRefNo, VbillRefNoResets), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/DocumentImageURLFindResponse.xml")));
			soapConnection.close();

			// Read Subscriber retrieve response from temp xml file
			LOGGER.log(Level.FINEST, "Simplified Results --> " + VbillRefNo);

			File xmlresponse = new File("src/input/DocumentImageURLFindResponse.xml");
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
									"RESULT ORDER BILL IMAGE FIND FAIL: " + VbillRefNo + "::"
											+ childnodes.item(i).getNodeName() + "==>"
											+ childnodes.item(i).getTextContent().trim());
						}
					}
				}
			} else {

				List<Integer> tempDocumentID = new ArrayList<Integer>();

				NodeList outputSubInfoList = doc.getElementsByTagName("regularPDFDocument");
				for (int j = 0; j < outputSubInfoList.getLength(); j++) {
					Element elSubInfo = (Element) outputSubInfoList.item(j);
					NodeList childnodesSubInfo = elSubInfo.getElementsByTagName("documentId");
					NodeList filenameNodes = elSubInfo.getElementsByTagName("filename");

					// Normally one bill ref no will have two records for dummy.iml and
					// btc_invoice.iml
					if (!filenameNodes.item(0).getTextContent().equalsIgnoreCase("dummy.iml")) {

						tempDocumentID.add(Integer.parseInt(childnodesSubInfo.item(0).getTextContent()));
						// LOGGER.log(Level.INFO, "Max Document ID" + tempDocumentID);
					}

				}

				maxDocumentID = findMax(tempDocumentID).toString();
				imageurlfind ilf = new imageurlfind();
				ilf.retreiveCred(new File("src/config/soapconnection.cfg"));
				ilf.callingImageURLFind(maxDocumentID);
				fileSize = findFilesize(ilf.fileurl.toString());
				fileurl = ilf.fileurl;
				LOGGER.log(Level.FINEST, maxDocumentID + "::" + ilf.fileurl + "::" + fileSize);

			}

			LOGGER.log(Level.FINEST, "<-- End Simplified result: " + VbillRefNo);
			LOGGER.log(Level.FINEST, "--------------------------------------");
		} catch (Exception e) {

			LOGGER.log(Level.SEVERE,
					"RESULT ORDER BILL IMAGE FIND FAIL: " + VbillRefNo + "::" + e.getCause().toString());
			e.printStackTrace();
		}

	}
	// Call SOAP Request

	// END: Call SOAP Request
	// Find max number in array
	private static Integer findMax(List<Integer> list) {
		Collections.sort(list, Collections.reverseOrder());
		return Integer.parseInt(list.get(0).toString());
	}

	private static float findFilesize(String Vurl) throws MalformedURLException {
		float size = 0;
		// get the url of web page
		URL url = new URL(Vurl);

		// create a connection
		HttpURLConnection conn;
		try {
			// open stream to get size of page
			conn = (HttpURLConnection) url.openConnection();

			// set request method.
			conn.setRequestMethod("HEAD");

			// get the input stream of process
			conn.getInputStream();

			// store size of file
			size = (float) (conn.getContentLength()/1024.00);

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Reading file fail.");
		}

		return size;

	}
}
