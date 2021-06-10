/**
 * 
 */
package conerobotprj.dv.lib;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.InputStream;
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

import com.comverse.api.crm.casemgmt.client.CrmTaskClient;
import com.comverse.api.crm.casemgmt.client.CrmUserClient;
import com.comverse.api.crm.casemgmt.data.CrmTaskIdentifier;
import com.comverse.api.crm.casemgmt.data.CrmUserBaseObject;
import com.comverse.api.crm.casemgmt.data.CrmUserBaseObjectFilter;
import com.comverse.api.crm.casemgmt.message.CrmTaskFindInputMessage;
import com.comverse.api.crm.casemgmt.message.CrmUserFindOutputMessage;
import com.comverse.api.framework.filter.Filter;
import com.comverse.api.framework.filter.FilterCriteria;
import com.comverse.api.framework.filter.StringEquals;
import com.comverse.api.framework.security.JAASClient;
import com.comverse.api.framework.base.CCBSConfiguration;
import com.comverse.api.framework.client.UserContext;

/**
 * @author khfighter
 *
 */
public class QueryCRMUserDetailsByUserName {
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
	public static void queryCRMUserDetailsByUserName(String UserName) {
		try {
			String USER_NAME = "bulkprov";
			String REALM = "sapi";
			String PASSWORD = "P@ssw0rd";
			
			/* String sapi = "java.naming.provider.url=t3://sapi21.c1.btc.bw:8001";
             String factory = "java.naming.factory.initial=weblogic.jndi.WLInitialContextFactory";
             List<String> props = new List<String>();
             props.add(factory);
             props.add(sapi);
             CCBSConfiguration.getConfiguration().setProperty("initial.context",
                             props);
			 */

			// Get a UserContext instance
			UserContext userContext = UserContext.getInstance();

			System.out.println("Testing ==> " + userContext.getDisplayUser());

			// Obtain a security token using the JAASClient
			JAASClient jaasClient = new JAASClient();

			// String securityToken = jaasClient.login(USER_NAME, REALM, PASSWORD);

			// System.out.println("Testing ==> " + securityToken);

			System.out.println("Successfully obtained security token.");

			int blockSize = 5;
			// Set UserContext attributes
			userContext.setUser(USER_NAME);
			userContext.setSecurityToken(
					"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");
			userContext.setBlockSize(blockSize);
			userContext.setResponseLogEnabled(true);
			userContext.setResponseLogLevel(5);

			CrmUserBaseObjectFilter user = new CrmUserBaseObjectFilter();
			CrmUserFindOutputMessage output;
			FilterCriteria FC = new FilterCriteria();
			StringEquals SE = new StringEquals();
			SE.setValue(UserName);
			FC.addFilter(SE);

			user.setAgentName(FC);

		
			CrmUserClient UC = new CrmUserClient();
			output = UC.crmUserFind(user);
			System.out.println("Count ==>" + output.getTotalCount());
			
		} catch (Exception e) {
			System.out.println(e.getLocalizedMessage() + e.getCause());
			e.printStackTrace();
			// System.out.println();
		}
	}

}
