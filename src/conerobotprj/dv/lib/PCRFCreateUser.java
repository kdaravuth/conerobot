package conerobotprj.dv.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import java.io.StringWriter;
import java.nio.file.Files;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.comverse.rht.core.PcrfSdbSPAHttpClient;

public class PCRFCreateUser {
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

	public static String pcrfOrgName = null;
	public static String pcrfDomain = null;

	public void callPCRFCreateUser(String pcrfUserName, String accountName, String org_qname, String profile_qname,
			String dom, String bbUserid, String macAddress) throws Exception {

		
		// SearchType - 1: ExternalID; 2: pcrf userid
		LOGGER.log(Level.INFO, "Processing: " + pcrfUserName + "::" + accountName);

		String response = null;
		String pcrfSdbEndpoint = null;

		// System.out.println("Sending Provisioning Request to PCRF...");
		pcrfSdbEndpoint = "http://10.1.35.150:32000/api";

		if (pcrfSdbEndpoint == null || pcrfSdbEndpoint.trim().length() == 0)
			throw new Exception("PCRF_SDB_PROVISIONING_URL_NOT_FOUND");

		try {

			// org.apache.http.impl.client.CloseableHttpClient closeableHttpClient =
			// HttpClientBuilder.create().setDefaultRequestConfig(requestConfig).build();
			HttpClient closeableHttpClient = new DefaultHttpClient();

			HttpPost post = new HttpPost(pcrfSdbEndpoint);

			StringEntity entity = new StringEntity(getSdbCreateUserRequest(pcrfUserName, accountName, org_qname,
					profile_qname, dom, bbUserid, macAddress), "UTF-8");
			entity.setContentType("text/xml");
			post.setEntity((HttpEntity) entity);

			String request = EntityUtils.toString(post.getEntity());
			// System.out.println(" Http URL: " + pcrfSdbEndpoint);
			// System.out.println("REQUEST XML:\n" + request);
			LOGGER.log(Level.INFO, "REQUEST XML: " + request);

			HttpResponse httpResponse = closeableHttpClient.execute((HttpUriRequest) post);
			HttpEntity responseEntity = httpResponse.getEntity();
			response = EntityUtils.toString(responseEntity);

			// LOGGER.log(Level.INFO,"response: "+ response.toString());
			int respCode = ((org.apache.http.HttpResponse) httpResponse).getStatusLine().getStatusCode();
			// System.out.println(" PCRF HTTP Status" + HttpStatus.getStatusText(respCode));
			// System.out.println("RESPONE XML: " +response);
			// LOGGER.log(Level.INFO,"respCode: "+ respCode);
			LOGGER.log(Level.INFO, "RESPONSE XML: " + response);
			// System.out.println("respCode: "+ respCode);

			// Writing to file for further use
			String path = "src/input/PCRFCreateUserResponse.xml";
			try (FileWriter writer = new FileWriter(path); BufferedWriter bw = new BufferedWriter(writer)) {

				bw.write(response);
				// System.out.println("respCode: "+ respCode);

			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
			// System.out.println("respCode: "+ respCode);

			File xmlresponse = new File("src/input/PCRFCreateUserResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName("user");
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			String result = "";

			for (int i = 0; i < childnodes.getLength(); i++) {

				Node child = childnodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (childnodes.item(i).getTextContent().trim() != "") {

						if (childnodes.item(i).getNodeName().equals("name")
								|| childnodes.item(i).getNodeName().equals("login-name")
								|| childnodes.item(i).getNodeName().equals("account-name")
								|| childnodes.item(i).getNodeName().equals("creation-date")
								|| childnodes.item(i).getNodeName().equals("last-modified")
								|| childnodes.item(i).getNodeName().equals("modified-by")) {
							result = result + ";" + childnodes.item(i).getNodeName() + "::"
									+ childnodes.item(i).getTextContent().trim();
						}

						if (childnodes.item(i).getNodeName().equals("profile-set")) {
							NodeList profileset = childnodes.item(i).getChildNodes();

							for (int j = 0; j < profileset.getLength(); j++) {
								if (profileset.item(j).getNodeName().equals("name")) {
									result = result + ";Bandwidth-" + profileset.item(j).getNodeName() + "::"
											+ profileset.item(j).getTextContent().trim();
								}

								NodeList accesscontrolprofile = profileset.item(j).getChildNodes();
								for (int k = 0; k < accesscontrolprofile.getLength(); k++) {
									if (accesscontrolprofile.item(k).getNodeName().equals("access-attribute")) {
										result = result + ";MAC Address::"
												+ accesscontrolprofile.item(k).getLastChild().getTextContent().trim();
									}
								}

							}

						} // End Profile Set
							// Start retrieving org.

						if (childnodes.item(i).getNodeName().equals("organization")) {
							result = result + ";organization-"
									+ childnodes.item(i).getChildNodes().item(1).getNodeName() + "::"
									+ childnodes.item(i).getChildNodes().item(1).getTextContent().trim();
						}

					}
				}
			}

			if (respCode != 200) {
				// System.out.println("PCRF URL Call Exception:");
				response = "FaiLure:" + response;
				LOGGER.log(Level.SEVERE, "RESULT FAIL: " + pcrfUserName + "::" + accountName);
			} else if (checkForValidResponse(response).booleanValue()) {
				// System.out.println(" PCRF SDB Provisioning was successful");
				// response = "SucCess:" + response;
				LOGGER.log(Level.INFO,
						"RESULT SUCCESS: " + pcrfUserName + "::" + accountName + " - OUTPUT: " + result);// +
																											// result);
			} else {
				// System.out.println(" PCRF SDB Provisioning Failed");
				response = "FaiLure:" + response;
				LOGGER.log(Level.SEVERE, "RESULT FAIL: " +pcrfUserName + "::" + accountName);
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE,
					"RESULT FAIL: " + pcrfUserName + "::" + accountName + " - OUTPUT: Users Creation Failed");

		}

	}

// Call create user

	public String getSdbCreateUserRequest(String pcrfUserName, String accountName, String org_qname,
			String profile_qname, String dom, String bbUserid, String macAddress) throws Exception {
		String result = "";
		String pcrfSdbUsername = "c1Prov";
		String pcrfSdbPassword = "c1Prov";

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element rootElement = doc.createElement("request");
		doc.appendChild(rootElement);
		rootElement.setAttribute("principal", pcrfSdbUsername);
		rootElement.setAttribute("credentials", pcrfSdbPassword);
		rootElement.setAttribute("version", "3.9");
		Element targetElement = doc.createElement("target");
		targetElement.setAttribute("name", "UserAPI");
		targetElement.setAttribute("operation", "createUser");
		Element paramElement = doc.createElement("parameter");
		Element userElement = doc.createElement("user");
		Element loginName = doc.createElement("login-name");
		loginName.setTextContent(bbUserid);
		Element domain = doc.createElement("domain");
		Element domName = doc.createElement("name");
		domName.setTextContent(dom);
		domain.appendChild(domName);
		Element organization = doc.createElement("organization");
		Element organization_qname = doc.createElement("qualified-name");
		organization_qname.setTextContent(org_qname);
		organization.appendChild(organization_qname);
		Element name = doc.createElement("name");
		name.setTextContent(pcrfUserName);
		Element password = doc.createElement("password");
		Element pwd_value = doc.createElement("value");
		pwd_value.setTextContent(pcrfUserName);
		password.appendChild(pwd_value);
		Element profile_set = doc.createElement("profile-set");
		Element profile_set_qname = doc.createElement("name");
		profile_set_qname.setTextContent(profile_qname);
		profile_set.appendChild(profile_set_qname);
		if (macAddress != null) {
			Element access_ctrl_profile = doc.createElement("access-control-profile");
			Element access_ctrl_prof_id = doc.createElement("id");
			access_ctrl_prof_id.setTextContent("14");
			Element access_ctrl_profile_name = doc.createElement("name");
			access_ctrl_profile_name.setTextContent("MAC_Control");
			Element service = doc.createElement("service");
			Element service_name = doc.createElement("name");
			service_name.setTextContent("AccessControl");
			service.appendChild(service_name);
			Element access_attribute = doc.createElement("access-attribute");
			Element access = doc.createElement("access");
			access.setTextContent("true");
			Element mac_address = doc.createElement("value");
			mac_address.setTextContent(macAddress);
			access_attribute.appendChild(access);
			access_attribute.appendChild(mac_address);
			access_ctrl_profile.appendChild(access_ctrl_prof_id);
			access_ctrl_profile.appendChild(access_ctrl_profile_name);
			access_ctrl_profile.appendChild(service);
			access_ctrl_profile.appendChild(access_attribute);
			profile_set.appendChild(access_ctrl_profile);
		}
		Element account = doc.createElement("account");
		Element accName = doc.createElement("name");
		accName.setTextContent(accountName);
		account.appendChild(accName);
		userElement.appendChild(name);
		userElement.appendChild(password);
		userElement.appendChild(profile_set);
		userElement.appendChild(account);
		userElement.appendChild(loginName);
		userElement.appendChild(domain);
		userElement.appendChild(organization);
		paramElement.appendChild(userElement);
		targetElement.appendChild(paramElement);
		rootElement.appendChild(targetElement);
		result = doc2String(doc);
		return result;
	}

	// End call Account Contract Renew
	private Boolean checkForValidResponse(String resp) throws Exception {
		if (resp.contains("<error"))
			return Boolean.valueOf(false);
		return Boolean.valueOf(true);
	}

	private static void getSubsDetails(String bb_user_id, String subsType) throws Exception {
		// System.out.println("subsType: "+ subsType);
		if (subsType.equalsIgnoreCase("ADSL")) {
			pcrfOrgName = "/BTCL_FIXED";
			pcrfDomain = "btcbroadband.co.bw";
		} else if (subsType.equalsIgnoreCase("WDSL")) {
			pcrfOrgName = "/BTCL_FIXED";
			pcrfDomain = "btcbroadband.co.bw";
		} else if (subsType.equalsIgnoreCase("MOBILE")) {
			pcrfOrgName = "/BTCL_MOBILE";
			pcrfDomain = "btc.co.bw";
		} else {
			LOGGER.log(Level.SEVERE, "RESULT FAIL: " + bb_user_id + "::" + subsType
					+ " - OUTPUT: INVALID_ACCOUNT_TYPE: ALLOWED ACCOUNT TYPE MOBILE,ADSL,WDSL");
			throw new Exception("...");

		}
	}

	public static String doc2String(Document doc) throws TransformerFactoryConfigurationError, TransformerException {
		String result = "";
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		DOMSource domSource = new DOMSource(doc);
		transformer.transform(domSource, streamResult);
		result = stringWriter.toString();
		return result;
	}

}
