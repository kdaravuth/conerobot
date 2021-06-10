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
public class AccountNoteAdd {
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
	public static void createSEnoteAdd(SOAPMessage soapMessage, String AccountNo, String NoteType,  String NoteContent) throws SOAPException {

		//Type: 1 userID - 2 MacAddress
		SOAPPart soapPart = soapMessage.getSOAPPart();

		String myNamespace = "com";
		String myNamespaceURI = "http://www.comverse.com";
		QName changedQname = new QName("changed");
		QName setQname = new QName("set");

		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

		SOAPBody c1soapBody = envelope.getBody();
		SOAPElement CrmCaseAddCrmNote = c1soapBody
				.addChildElement("AccountAddCrmNote", myNamespace);
		SOAPElement input = CrmCaseAddCrmNote.addChildElement("input",myNamespace );
		SOAPElement productVersion = input.addChildElement("productVersion");
		productVersion.addTextNode("CCBS3.0");
		SOAPElement realm = input.addChildElement("realm");
		realm.addTextNode( "sapi");
		SOAPElement userIdName = input.addChildElement("userIdName");
		userIdName.addTextNode(username);
		SOAPElement securityToken = input.addChildElement("securityToken");
		securityToken.addTextNode(token);
		
		SOAPElement accountId = input.addChildElement("accountId");
		SOAPElement accountInternalId = accountId.addChildElement("accountInternalId");
		accountInternalId.addAttribute(setQname, "false");
		accountInternalId.addAttribute(changedQname, "true");
		SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
		accountInternalIdValue.addTextNode(AccountNo);
		
		SOAPElement note = input.addChildElement("note");
		
		
		SOAPElement noteTypeLkp = note.addChildElement("noteTypeLkp");
		noteTypeLkp.addAttribute(setQname, "true");
		noteTypeLkp.addAttribute(changedQname, "true");
		SOAPElement noteTypeLkpValue = noteTypeLkp.addChildElement("value");
		noteTypeLkpValue.addTextNode(NoteType);
		
		SOAPElement notes = note.addChildElement("notes");
		notes.addAttribute(setQname, "true");
		notes.addAttribute(changedQname, "true");
		SOAPElement notesValue = notes.addChildElement("value");
		notesValue.addTextNode(NoteContent);
		
		SOAPElement relatedTblName = note.addChildElement("relatedTblName");
		relatedTblName.addAttribute(setQname, "true");
		relatedTblName.addAttribute(changedQname, "true");
		SOAPElement relatedTblNameValue = relatedTblName.addChildElement("value");
		relatedTblNameValue.addTextNode("interaction");
		
		SOAPElement userCreated = note.addChildElement("userCreated");
		userCreated.addAttribute(setQname, "true");
		userCreated.addAttribute(changedQname, "true");
		SOAPElement userCreatedValue = userCreated.addChildElement("value");
		userCreatedValue.addTextNode("System");
		
		SOAPElement userUpdated = note.addChildElement("userUpdated");
		userUpdated.addAttribute(setQname, "true");
		userUpdated.addAttribute(changedQname, "true");
		SOAPElement userUpdatedValue = userUpdated.addChildElement("value");
		userUpdatedValue.addTextNode("System");
		
		
		
		
	}// End create Extended Data add envelope

	// Create SOAP request
	public static SOAPMessage createSRnoteAdd(String soapAction, String AccountNo, String NoteType,  String NoteContent) throws Exception {

		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();

		createSEnoteAdd(soapMessage,  AccountNo,  NoteType,   NoteContent);

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
	public void callnoteAdd(String AccountNo, String NoteType,  String NoteContent) {

		try {

			/*
			 * String soapEndpointUrl =
			 * "http://10.128.202.137:8001/services/SubscriberService"; String soapAction =
			 * "http://10.128.202.137:8001/services/SubscriberService.wsdl";
			 * http://10.128.202.137:8001: prod http://10.1.38.11:8001: diot 1 10.1.38.21:
			 * diot2
			 */
			LOGGER.log(Level.INFO, "<-- Start Processing Account No=" + AccountNo + ";NoteType="+ NoteType +";NoteContent"+NoteContent );

			String soapEndpointUrl = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/CrmAccountService";
			String soapAction = (new BufferedReader(new FileReader("src/config/sapi.cfg")).readLine())
					+ "/services/CrmAccountService.wsdl";

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();

			SOAPMessage soapResponse = soapConnection
					.call(createSRnoteAdd(soapAction,  AccountNo, NoteType,  NoteContent), soapEndpointUrl);

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			soapResponse.writeTo(stream);
			String message = new String(stream.toByteArray());

			// Print the SOAP Response
			LOGGER.log(Level.FINEST, "Response SOAP -->" + message);
			// System.out.println();

			// Writing to file for further use
			soapResponse.writeTo(new FileOutputStream(new File("src/input/AccountAddCrmNoteResponse.xml")));
			soapConnection.close();

			File xmlresponse = new File("src/input/AccountAddCrmNoteResponse.xml");
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
				LOGGER.log(Level.INFO, "RESULT SUCCESS: Account No=" + AccountNo + ";NoteType="+ NoteType +";NoteContent"+NoteContent);
		      
			}
			
			LOGGER.log(Level.INFO, "<-- End Processing Account No=" + AccountNo + ";NoteType="+ NoteType +";NoteContent"+NoteContent );
			LOGGER.log(Level.INFO, "--------------------------------------");
		} catch (Exception e) {

			// LOGGER.log(Level.SEVERE, e.toString());
			LOGGER.log(Level.INFO, "RESULT FAIL: Account No=" + AccountNo + ";NoteType="+ NoteType +";NoteContent"+NoteContent);
			LOGGER.log(Level.INFO, "<-- End Processing Account No=" + AccountNo + ";NoteType="+ NoteType +";NoteContent"+NoteContent);
			LOGGER.log(Level.INFO, "--------------------------------------");
		}

	}

}
