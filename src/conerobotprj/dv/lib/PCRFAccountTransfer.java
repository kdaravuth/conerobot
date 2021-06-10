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

public class PCRFAccountTransfer {
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
	public static String responseAccountTransfer = null;

	public boolean pcrfAccountTransfer(String AccountNo, String BBuserID) throws Exception {

		String response = null;
		String pcrfSdbEndpoint = null;

		pcrfSdbEndpoint = "http://10.1.35.150:32000/api";

		if (pcrfSdbEndpoint == null || pcrfSdbEndpoint.trim().length() == 0)
			throw new Exception("PCRF_SDB_PROVISIONING_URL_NOT_FOUND");

		try {
			HttpClient closeableHttpClient = new DefaultHttpClient();

			HttpPost post = new HttpPost(pcrfSdbEndpoint);

			StringEntity entity = new StringEntity(accountTransfer(AccountNo, BBuserID), "UTF-8");
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

			LOGGER.log(Level.INFO, "RESPONSE XML: " + response);

			// Writing to file for further use
			String path = "src/input/PCRFAccountTransferResponse.xml";
			try (FileWriter writer = new FileWriter(path); BufferedWriter bw = new BufferedWriter(writer)) {

				bw.write(response);
				// System.out.println("respCode: "+ respCode);

			} catch (IOException e) {
				System.out.println(e.getStackTrace());
			}
			// System.out.println("respCode: "+ respCode);

			File xmlresponse = new File("src/input/PCRFAccountTransferResponse.xml");
			DocumentBuilderFactory dbuilderfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder dbuilder = dbuilderfac.newDocumentBuilder();
			Document doc = dbuilder.parse(xmlresponse);

			doc.getDocumentElement().normalize();
			Node firstChild = doc.getFirstChild(); // get first child to list through other elements

			NodeList outputlist = doc.getElementsByTagName("response");
			Element el = (Element) outputlist.item(0);
			NodeList childnodes = el.getChildNodes();

			String result = "";

			for (int i = 0; i < childnodes.getLength(); i++) {

				Node child = childnodes.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					if (childnodes.item(i).getTextContent().trim() != "") {

						result = result + " ; " + childnodes.item(i).getNodeName() + "::"
								+ childnodes.item(i).getTextContent().trim();

					}
				}
			}

			if (respCode != 200) {
				// System.out.println("PCRF URL Call Exception:");
				response = "FaiLure:" + response;
				responseAccountTransfer = response;
				LOGGER.log(Level.SEVERE, "RESULT FAIL: " + AccountNo);
				return false;
			} else if (checkForValidResponse(response).booleanValue()) {
				// System.out.println(" PCRF SDB Provisioning was successful");
				// response = "SucCess:" + response;
				LOGGER.log(Level.INFO, "RESULT SUCCESS: " + AccountNo + " Created - OUTPUT: " + result);// + result);
				responseAccountTransfer = response;
				return true;
			} else {
				// System.out.println(" PCRF SDB Provisioning Failed");
				response = "FaiLure:" + response;
				responseAccountTransfer = response;
				LOGGER.log(Level.SEVERE, "RESULT FAIL: " + AccountNo);
				return false;
			}

		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "RESULT FAIL: " + AccountNo + " " + e.getMessage());
			responseAccountTransfer = e.getMessage();
			return false;

		}

	}

	// End call Account Contract Renew
	private Boolean checkForValidResponse(String resp) throws Exception {
		if (resp.contains("<error"))
			return Boolean.valueOf(false);
		return Boolean.valueOf(true);
	}

	private String accountTransfer(String AccountNo, String BBuserID) throws Exception {
		String result = "";
		String pcrfSdbUsername = "c1Prov";
		String pcrfSdbPassword = "c1Prov";
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.newDocument();
		Element request = doc.createElement("request");
		doc.appendChild(request);
		request.setAttribute("principal", pcrfSdbUsername);
		request.setAttribute("credentials", pcrfSdbPassword);
		request.setAttribute("version", "3.9");

		Element target = doc.createElement("target");
		target.setAttribute("name", "UserAPI");
		target.setAttribute("operation", "updateUser");

		Element parameter = doc.createElement("parameter");
		Element user = doc.createElement("user");
		Element loginname = doc.createElement("login-name");
		Element domain = doc.createElement("domain");
		Element domainname = doc.createElement("name");
		Element status = doc.createElement("status");
		Element value = doc.createElement("value");
		Element account = doc.createElement("account");
		Element accountname = doc.createElement("name");

		request.appendChild(target);
		target.appendChild(parameter);
		parameter.appendChild(user);
		user.appendChild(loginname);
		loginname.setTextContent(BBuserID);
		user.appendChild(domain);
		domain.appendChild(domainname);
		domainname.setTextContent("btcbroadband.co.bw");
		user.appendChild(account);
		account.appendChild(accountname);
		accountname.setTextContent(AccountNo);
		account.appendChild(status);
		status.appendChild(value);
		value.setTextContent("active");

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		StringWriter stringWriter = new StringWriter();
		StreamResult streamResult = new StreamResult(stringWriter);
		DOMSource domSource = new DOMSource(doc);
		transformer.transform(domSource, streamResult);
		result = stringWriter.toString();
		return result;
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
