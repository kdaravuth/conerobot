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

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.telnet.TelnetClient;
import java.io.InputStream;
import java.io.PrintStream;

/**
 * @author khfighter
 *
 */
public class HuaweiTelnetListSub {
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
	
	 private TelnetClient telnet = new TelnetClient();
	    private InputStream in;
	    private PrintStream out;
	    private String prompt = "%";

	    public HuaweiTelnetListSub(String server, String user, String password) {
	        try {
	            // Connect to the specified server
	            telnet.connect(server, 7776);

	            // Get input and output stream references
	            in = telnet.getInputStream();
	            out = new PrintStream(telnet.getOutputStream());
	            
	            LOGGER.log(Level.INFO, "were are here"+ out.toString());
	            out.flush();

	            // Log the user on
	          //  readUntil("login: ");
	           // write(user);
	            //readUntil("Password: ");
	            //write(password);

	            // Advance to a prompt
	            //readUntil(prompt + " ");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public void su(String password) {
	        try {
	            write("su");
	            readUntil("Password: ");
	            write(password);
	            prompt = "#";
	            readUntil(prompt + " ");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public String readUntil(String pattern) {
	        try {
	            char lastChar = pattern.charAt(pattern.length() - 1);
	            StringBuffer sb = new StringBuffer();
	            boolean found = false;
	            char ch = (char) in.read();
	            while (true) {
	                System.out.print(ch);
	                sb.append(ch);
	                if (ch == lastChar) {
	                    if (sb.toString().endsWith(pattern)) {
	                        return sb.toString();
	                    }
	                }
	                ch = (char) in.read();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public void write(String value) {
	        try {
	            out.println(value);
	            out.flush();
	            System.out.println(value);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    public String sendCommand(String command) {
	        try {
	            write(command);
	            return readUntil(prompt + " ");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    public void disconnect() {
	        try {
	            telnet.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	
	
	public static void TelnetQueryCustomer() {

		 try {
			 LOGGER.log(Level.INFO, "checking status...");
			 
			   HuaweiTelnetListSub telnet = new HuaweiTelnetListSub(
	                    "10.134.4.103", "userId", "Password");
	            System.out.println("Got Connection...");
	            
	            telnet.sendCommand("LGI:HLRSN=1,OPNAME=\"Account\",PWD=\"Password_12\";\nLST SUB: ISDN=\"26773156284\", DETAIL=TRUE;");
	           	            
	            telnet.sendCommand("LST SUB: ISDN=\"26773156284\", DETAIL=TRUE; \n");
	            	                 	           	            
	            telnet.disconnect();
	            System.out.println("DONE");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}


	}


