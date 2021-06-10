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
import java.util.function.Supplier;
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

import com.comverse.api.framework.business.client.WorkpointPropertiesClient;
import com.comverse.api.workflow.client.WorkpointClient;
import com.comverse.ccbs.csm.utilities.WorkpointClientContextManager;
import com.workpoint.client.ClientContext;
import com.workpoint.client.Job;
import com.workpoint.client.WorkItemEntry;
import com.workpoint.common.data.ActivityInstData;
import com.workpoint.common.data.JobNodeData;
import com.workpoint.common.data.TableDataList;
import com.workpoint.common.data.table.ActivityInstTable;
import com.workpoint.common.data.table.ActivityInstUserDataTable;
import com.workpoint.common.data.table.ActivityUserDataTable;
import com.workpoint.common.data.table.UserData;
import com.workpoint.common.script.SymbolData;
import com.workpoint.common.script.SymbolTable;
import com.workpoint.common.util.TableID;

/**
 * @author khfighter
 *
 */
public class AddPCRF2C1Bypass {
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

	public static void addPCRFc1ByPass(String prociID, int actiID, String varname, String varvalue,
			String nodeNameToReset, String wpID) {
		
		if (wpID.equalsIgnoreCase("2")) {
			Hashtable<String, String> hashParam = new Hashtable();
			hashParam.put("client.connect", "XML");
			hashParam.put("client.connect.URL", "http://10.128.202.19:8051/wp/wpClientServlet");
			ClientContext context = ClientContext.createContext(hashParam, true);
			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				List<Job> L = Job.getList(context, " PROCI_ID='" + prociID + "' ", "", null);
				Job j = L.get(0);

				j.readDetail(true);
				LOGGER.log(Level.INFO, "Processing - JOBID:" + prociID + ";ACTIID:" + actiID);
				// Adding Bypassing parameter
				TableID actiIDtbl = new TableID(actiID, "WPDS");
				j.setActivityBulkUserData(actiIDtbl, varname, varvalue);
				j.save();

				// Get nodelist to reset
				Job job = Job.queryByID(context, j.getJobID(), true);
				TableDataList list = job.getNodeList();

				for (Iterator<JobNodeData> it = list.iterator(); it.hasNext();) {
					JobNodeData node = it.next();
					if (node.getName().equals(nodeNameToReset)) {
						LOGGER.log(Level.INFO, "NODE LIST: " + node.getName() + " " + node.getJobNodeID());
						// j.resetFromNode(node);
						// j.save();
						job.completeWorkForActivity(node.getActivityInstID());
						LOGGER.log(Level.INFO, "RESULT PASS Processing - JOBID:" + prociID + ";ACTIID:" + actiID);
						break;
					}
				}

			} catch (Exception e) {
				LOGGER.log(Level.INFO, "RESULT FAIL Processing - JOBID:" + prociID + ";ACTIID:" + actiID + "; Erro:"
						+ e.getCause() + e.getLocalizedMessage() + e.getStackTrace().getClass());
			}
		} else {

			ClientContext context = ClientContext.createContext();
			try {
				context.open("WPDS", "wfadmin", "Wfadmin123!");
				List<Job> L = Job.getList(context, " PROCI_ID='" + prociID + "' ", "", null);
				Job j = L.get(0);

				j.readDetail(true);
				LOGGER.log(Level.INFO, "Processing - JOBID:" + prociID + ";ACTIID:" + actiID);
				// Adding Bypassing parameter
				TableID actiIDtbl = new TableID(actiID, "WPDS");
				j.setActivityBulkUserData(actiIDtbl, varname, varvalue);
				j.save();

				// Get nodelist to reset
				Job job = Job.queryByID(context, j.getJobID(), true);
				TableDataList list = job.getNodeList();

				for (Iterator<JobNodeData> it = list.iterator(); it.hasNext();) {
					JobNodeData node = it.next();
					if (node.getName().equals(nodeNameToReset)) {
						LOGGER.log(Level.INFO, "NODE LIST: " + node.getName() + " " + node.getJobNodeID());
						// j.resetFromNode(node);
						// j.save();
						job.completeWorkForActivity(node.getActivityInstID());
						LOGGER.log(Level.INFO, "RESULT PASS Processing - JOBID:" + prociID + ";ACTIID:" + actiID);
						break;
					}
				}

			} catch (Exception e) {
				LOGGER.log(Level.INFO, "RESULT FAIL Processing - JOBID:" + prociID + ";ACTIID:" + actiID + "; Erro:"
						+ e.getCause() + e.getLocalizedMessage() + e.getStackTrace().getClass());
			}
		}

		
	}
}
