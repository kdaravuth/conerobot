/**
 * 
 */
package conerobot.dv.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import conerobotprj.dv.lib.ExtendedDataUpdate;
import conerobotprj.dv.lib.NrcAddConstruct;
import conerobotprj.dv.lib.OfferAddConstruct;
import conerobotprj.dv.lib.PaymentReversalConstruct;
import conerobotprj.dv.lib.PostpaidOBAdjust;
import conerobotprj.dv.lib.SubscriberRetrieveConstruct;

/**
 * @author khfighter
 *
 */
public class conerobotCalls {

	/**
	 * @param args
	 */
	private static Logger LOGGER = null;
	static {

		try {

			InputStream configFile = SubscriberRetrieveConstruct.class.getResourceAsStream("/config/logger.cfg");
			LogManager.getLogManager().readConfiguration(configFile);
			LOGGER = Logger.getLogger(SubscriberRetrieveConstruct.class.getName());
		} catch (IOException e) {
			e.getMessage();
		}

	}

	// private static final Logger LOGGER = Logger.getLogger(
	// SubscriberRetrieveConstruct.class.getName());
	public static void main(String[] args) throws Exception {

		// TODO Auto-generated method stub
		try {
			if (args.length == 2) {
				// System.out.print("args[0]: "+ args[0] + " args[1]: " + args[1]);

				if (args[0].equals("SubscriberRetrieve") == true) {

					// Retrieving sapi credential
					SubscriberRetrieveConstruct src = new SubscriberRetrieveConstruct();
					src.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Running SubscriberRetrieve function");

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					// List <String> lines = new ArrayList<String>();
					String line;

					while ((line = bfrdr.readLine()) != null) {
						LOGGER.log(Level.INFO, "--------------------------------------");
						LOGGER.log(Level.INFO, "Retrieving info of Subs: " + line.split(",")[0]);
						src.callSubscriberRetrieveService(line.split(",")[0]);

					}
				} else if (args[0].equals("callPaymentReversalService") == true) {

					// Retrieving credential
					PaymentReversalConstruct prs = new PaymentReversalConstruct();
					prs.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Running PaymentReversal function");

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						LOGGER.log(Level.INFO, "--------------------------------------");
						LOGGER.log(Level.INFO, "Processing Tracking id: " + line.split(",")[0]);
						prs.callPaymentReversalService(line.split(",")[0], line.split(",")[1], line.split(",")[2]);
					}
				} else if (args[0].equals("OfferAdd") == true) {

					// Retrieving credential
					OfferAddConstruct oac = new OfferAddConstruct();
					oac.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Running Offer Adding function");
					// oac.callingOfferAdd("26773040775", "89", "true");

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						LOGGER.log(Level.INFO, "Processing MSISDN: " + line.split(",")[0]);
						oac.callingOfferAdd(line.split(",")[0], line.split(",")[1], line.split(",")[2]);

					}
				}

				else if (args[0].equals("NRCAdd") == true) {

					// Retrieving credential
					NrcAddConstruct nac = new NrcAddConstruct();
					nac.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Running NRC Adding function");
					// oac.callingOfferAdd("26773040775", "89", "true");

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						LOGGER.log(Level.INFO, "Processing MSISDN: " + line.split(",")[0]);
						// nac.callingNRCAdd(MSISDN, NRCtermID, NRCCategory, NRCRate, comments);
						nac.callingNRCAdd(line.split(",")[0], line.split(",")[1], line.split(",")[2],
								line.split(",")[3], line.split(",")[4]);

					}
				}
				// Calling ExtendedData Update
				else if (args[0].equals("ExtendedDataUpdate") == true) {

					ExtendedDataUpdate edu = new ExtendedDataUpdate();
					edu.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.FINEST, "Starting Extended Data Update");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						LOGGER.log(Level.INFO, "MSISDN: " + line.split("::")[0]);
						LOGGER.log(Level.FINEST, "Data: " + line.split("::")[1]);
						// callingExtendedDataUpdate(msisdn, extendeddata)
						edu.callingExtendedDataUpdate(line.split("::")[0], line.split("::")[1]);

					}

				}
				// End Next Function

				// Calling Postpaid Balance adjustment
				else if (args[0].equals("CallingPostpaidOBAdjust") == true) {

					PostpaidOBAdjust poa = new PostpaidOBAdjust();
					poa.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Starting Postpaid Outstanding Adjustment");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						poa.CallingPostpaidOBAdjust(line.split("::")[0], line.split("::")[1], line.split("::")[2]);

					}

				}
				// End Next Function

			} else {
				LOGGER.log(Level.SEVERE,
						"Usage: Arg[0] Arg[1] Arg[2] - Example: conerobotCalls SubscriberRetrieve batch file");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Please ensure the file format which is provided is correct " + e.getStackTrace());
		}

	}

}
