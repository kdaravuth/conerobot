/**
 * Payment Reversal Constructor
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
public class PaymentInsert {

	// Initialize logger
	private static Logger LOGGER = null;
	static {

		try {

			InputStream configFile = SubscriberRetrieveConstruct.class.getResourceAsStream("/config/logger.cfg");
			LogManager.getLogManager().readConfiguration(configFile);
			LOGGER = Logger.getLogger(PaymentInsert.class.getName());
		} catch (IOException e) {
			e.getMessage();
		}

	}
	// End initialize logger
	// Sapi Credential retrieval
	public static String realm = "";
	public static String username = "";
	public static String token = "";
	public static String trackingId = "";

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


	// Create SOAP for Contract Renewal
				public static void createSEMakepayment(SOAPMessage soapMessage, String AccountNo, String Amount, String Annotation) throws SOAPException {

					SOAPPart soapPart = soapMessage.getSOAPPart();

					String myNamespace = "com";
					String myNamespaceURI = "http://www.comverse.com";
					QName changedQname = new QName("changed");
					QName setQname = new QName("set");
					QName fetchQname = new QName("fetch");

					// SOAP Envelope
					SOAPEnvelope envelope = soapPart.getEnvelope();
					envelope.addNamespaceDeclaration(myNamespace, myNamespaceURI);

					SOAPBody c1soapBody = envelope.getBody();
					SOAPElement PaymentCreate = c1soapBody.addChildElement("PaymentCreate", myNamespace);
					SOAPElement input = PaymentCreate.addChildElement("input", myNamespace);

					SOAPElement realm = input.addChildElement("realm");
					realm.addTextNode("sapi");

					SOAPElement securityToken = input.addChildElement("securityToken");
					securityToken.addTextNode(token);
					SOAPElement userIdName = input.addChildElement("userIdName");
					userIdName.addTextNode("CSS");
					
					SOAPElement payment = input.addChildElement("payment");
					SOAPElement accountInternalId = payment.addChildElement("accountInternalId");
					accountInternalId.addAttribute(setQname, "true");
					accountInternalId.addAttribute(setQname, "true");
					SOAPElement accountInternalIdValue = accountInternalId.addChildElement("value");
					accountInternalIdValue.addTextNode(AccountNo);
					
					SOAPElement annotation = payment.addChildElement("annotation");
					annotation.addAttribute(setQname, "true");
					annotation.addAttribute(changedQname, "true");
					SOAPElement annotationValue = annotation.addChildElement("value");
					annotationValue.addTextNode(Annotation);
					
					SOAPElement payMethod = payment.addChildElement("payMethod");
					payMethod.addAttribute(setQname, "true");
					payMethod.addAttribute(changedQname, "true");
					SOAPElement payMethodValue = payMethod.addChildElement("value");
					payMethodValue.addTextNode("1");
					
					SOAPElement transAmount = payment.addChildElement("transAmount");
					transAmount.addAttribute(setQname, "true");
					transAmount.addAttribute(setQname, "true");
					SOAPElement transAmountValue = transAmount.addChildElement("value");
					transAmountValue.addTextNode(Amount);
					
					SOAPElement transSource = payment.addChildElement("transSource");
					transSource.addAttribute(setQname, "true");
					transSource.addAttribute(changedQname, "true");
					SOAPElement transSourceValue = transSource.addChildElement("value");
					transSourceValue.addTextNode("1");
					
					SOAPElement transType = payment.addChildElement("transType");
					transType.addAttribute(setQname, "true");
					transType.addAttribute(changedQname, "true");
					SOAPElement transTypeValue = transType.addChildElement("value");
					transTypeValue.addTextNode("1");
									

				}// End create Extended Data add envelope

				// Create SOAP request
				private static SOAPMessage createSOAPRequestMakepayment(String soapAction, String AccountNo, String Amount, String Annotation) throws Exception {

					MessageFactory messageFactory = MessageFactory.newInstance();
					SOAPMessage soapMessage = messageFactory.createMessage();

					createSEMakepayment(soapMessage,AccountNo,Amount, Annotation);

					MimeHeaders headers = soapMessage.getMimeHeaders();
					headers.addHeader("SOAPAction", soapAction);
					soapMessage.saveChanges();

					// write request soap message to stream then to show in the log

					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					soapMessage.writeTo(stream);
					String message = new String(stream.toByteArray(), "utf-8");

					/* Print the request message, just for debugging purposes */
					LOGGER.log(Level.FINEST, "Request SOAP Message -->" + message);

					return soapMessage;
				}// End create SOAP Request

				// Calling SOAP then get SOAP response
				public static boolean Makingpayment(String AccountNo, String Amount, String Annotation) {
					try {

						String soapEndpointUrl = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
								+ "/services/PaymentService";
						String soapAction = (new BufferedReader(new FileReader("src/Config/sapi.cfg")).readLine())
								+ "/services/PaymentService.wsdl";

						// Create SOAP Connection
						SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
						SOAPConnection soapConnection = soapConnectionFactory.createConnection();

						// Send SOAP Message to SOAP Server
						//LOGGER.log(Level.INFO, "Creating SOAP and Calling SOAP");
						SOAPMessage soapResponse = soapConnection.call(createSOAPRequestMakepayment(soapAction, AccountNo, Amount, Annotation),
								soapEndpointUrl);

						// Writing soap response to stream so that it's easily to print out

						ByteArrayOutputStream stream = new ByteArrayOutputStream();
						soapResponse.writeTo(stream);
						String message = new String(stream.toByteArray());

						// Print the SOAP Response
						LOGGER.log(Level.FINEST, "Receiving SOAP Response " + message);

						// Writing to file for further use
						soapResponse.writeTo(new FileOutputStream(new File("src/Logs/PaymentCreate.xml")));
						soapConnection.close();

						// Read Subscriber retrieve response from temp xml file
						LOGGER.log(Level.FINEST, "Simplified Results --> " + AccountNo);

						File xmlresponse = new File("src/Logs/PaymentCreate.xml");
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
												"RESULT::FAIL MAKE PAYMENT : " + "::" + childnodes.item(i).getNodeName() + "::"
														+ childnodes.item(i).getTextContent().trim());
										return false; // payment is unsuccessfully done
									}
								}
							}
						} else {

							// Count the findings
							NodeList totalcountlist = doc.getElementsByTagName("totalCount");
							Element etotalcountlist = (Element) totalcountlist.item(0);
							LOGGER.log(Level.FINEST, "=====> etotalcountlist: " + etotalcountlist.getTextContent());

							if (Integer.parseInt(etotalcountlist.getTextContent()) > 0) {
								// count is the payment record successfully done.
								// if >0 means payment is successful else
								//if payment = 0 then payment is fail
								NodeList outputSubInfoListSO = doc.getElementsByTagName("trackingId");
								Element elSubInfoSO = (Element) outputSubInfoListSO.item(0);
								NodeList childnodesSubInfoSO = elSubInfoSO.getChildNodes();

								for (int i = 0; i < childnodesSubInfoSO.getLength(); i++) {

									Node childSO = childnodesSubInfoSO.item(i);

									if (childSO.getNodeType() == Node.ELEMENT_NODE) {

										trackingId = childSO.getTextContent();
										LOGGER.log(Level.FINEST ,"RESULT::PASS MAKE PAYMENT :: " + trackingId +";"+ Annotation);
										return true;

									}
								}

								
							} else //Else to check the total record count 
							{

								return false;
							}

						}

						LOGGER.log(Level.FINEST, "<-- End Simplified result: " + AccountNo);
						LOGGER.log(Level.FINEST, "--------------------------------------");

					} catch (Exception e) {

						LOGGER.log(Level.SEVERE, e.getCause() + "::" + e.getMessage() + "::" + e.getStackTrace());
						return false; //payment is false

					}

					return false; //payment is false

				}// End calling soap


}
