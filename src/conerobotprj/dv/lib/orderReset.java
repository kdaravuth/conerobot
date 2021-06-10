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
import java.util.Iterator;
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
public class orderReset {
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

	// 17609129043
	public static void reset_failed_node(String soid, String ActivityInstID, String wpID) {

		if (wpID.equalsIgnoreCase("2")) {
			Hashtable<String, String> hashParam = new Hashtable();
			hashParam.put("client.connect", "XML");
			hashParam.put("client.connect.URL", "http://10.128.203.19:8051/wp/wpClientServlet");
			ClientContext context = ClientContext.createContext(hashParam, true);
			boolean ifnodefound = false;
			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				LOGGER.log(Level.INFO, "START PROCESS: ServiceOrder=" + soid + ";ActivityInstID=" + ActivityInstID);
				List<Job> L = Job.getList(context, " PROCI_REF='" + soid + "' ", "", null);
				if (L != null && !L.isEmpty()) {
					Job job = L.get(0);
					// System.out.println("Job: " + job.getJobID());
					job = Job.queryByID(context, job.getJobID(), true);
					TableDataList list = job.getNodeList();
					for (Iterator<JobNodeData> it = list.iterator(); it.hasNext();) {
						JobNodeData node = it.next();
						// node.getNodeTypeID() == 2 &&
						// node.getCompletionCode() == 60 &&
						// System.out.println("Job: " + node.getActivityInstID());

						// Fixing gettting null activity
						if (node.getActivityInstID() != null
								&& node.getActivity().getActivityInstID().toString().equalsIgnoreCase(ActivityInstID)) {
							// System.out.println(job.getJobID() + " | " + node.getName() + " | " +
							// node.getActivity().getActivityInstID() + " | " + node.getCompletionCode() +
							// "|" + node.getNodeIteration());
							job.resetFromNode(node);
							LOGGER.log(Level.INFO, "RESULT PASS: ServiceOrder=" + soid + ";ActivityInstID="
									+ ActivityInstID + " is processed");
							ifnodefound = true;
							break;
						}
					}
					if (!ifnodefound) {
						LOGGER.log(Level.SEVERE, "RESULT FAIL: ServiceOrder=" + soid + ";ActivityInstID="
								+ ActivityInstID + " is not found");
					}
				} else {
					LOGGER.log(Level.SEVERE,
							"RESULT FAIL: ServiceOrder=" + soid + ";ServiceOrder=" + soid + " is not found");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			ClientContext context = ClientContext.createContext();
			boolean ifnodefound = false;
			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				LOGGER.log(Level.INFO, "START PROCESS: ServiceOrder=" + soid + ";ActivityInstID=" + ActivityInstID);
				List<Job> L = Job.getList(context, " PROCI_REF='" + soid + "' ", "", null);
				if (L != null && !L.isEmpty()) {
					Job job = L.get(0);
					// System.out.println("Job: " + job.getJobID());
					job = Job.queryByID(context, job.getJobID(), true);
					TableDataList list = job.getNodeList();
					for (Iterator<JobNodeData> it = list.iterator(); it.hasNext();) {
						JobNodeData node = it.next();
						// node.getNodeTypeID() == 2 &&
						// node.getCompletionCode() == 60 &&
						// System.out.println("Job: " + node.getActivityInstID());

						// Fixing gettting null activity
						if (node.getActivityInstID() != null
								&& node.getActivity().getActivityInstID().toString().equalsIgnoreCase(ActivityInstID)) {
							// System.out.println(job.getJobID() + " | " + node.getName() + " | " +
							// node.getActivity().getActivityInstID() + " | " + node.getCompletionCode() +
							// "|" + node.getNodeIteration());
							job.resetFromNode(node);
							LOGGER.log(Level.INFO, "RESULT PASS: ServiceOrder=" + soid + ";ActivityInstID="
									+ ActivityInstID + " is processed");
							ifnodefound = true;
							break;
						}
					}
					if (!ifnodefound) {
						LOGGER.log(Level.SEVERE, "RESULT FAIL: ServiceOrder=" + soid + ";ActivityInstID="
								+ ActivityInstID + " is not found");
					}
				} else {
					LOGGER.log(Level.SEVERE,
							"RESULT FAIL: ServiceOrder=" + soid + ";ServiceOrder=" + soid + " is not found");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
