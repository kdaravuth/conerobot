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
import java.util.Hashtable;
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

import com.workpoint.client.ClientContext;
import com.workpoint.client.Job;
import com.workpoint.common.data.JobNodeData;
import com.workpoint.common.data.TableDataList;

/**
 * @author khfighter
 *
 */
public class orderRequeue {
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

	public static void reset_job(String soid, String wpID) {

		if (wpID.equalsIgnoreCase("2")) {
			Hashtable<String, String> hashParam = new Hashtable();
			hashParam.put("client.connect", "XML");
			hashParam.put("client.connect.URL", "http://10.128.203.19:8051/wp/wpClientServlet");
			ClientContext context = ClientContext.createContext(hashParam, true);

			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				List<Job> L = Job.getList(context, " PROCI_REF='" + soid + "' ", "", null);
				if (L != null && !L.isEmpty()) {
					Job job = L.get(0);
					job = Job.queryByID(context, job.getJobID(), true);
					job.reset();
					LOGGER.log(Level.INFO, "RESULT SUCCESS: Requeuing Service Order " + soid);
				} else {
					LOGGER.log(Level.SEVERE, "RESULT FAIL : " + soid + " cannot be found");
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
			}

		} else {

			ClientContext context = ClientContext.createContext();
			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				List<Job> L = Job.getList(context, " PROCI_REF='" + soid + "' ", "", null);
				if (L != null && !L.isEmpty()) {
					Job job = L.get(0);
					job = Job.queryByID(context, job.getJobID(), true);
					job.reset();
					LOGGER.log(Level.INFO, "RESULT SUCCESS: Requeuing Service Order " + soid);
				} else {
					LOGGER.log(Level.SEVERE, "RESULT FAIL : " + soid + " cannot be found");
				}
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, e.getStackTrace().toString());
			}
		}
	}

}
