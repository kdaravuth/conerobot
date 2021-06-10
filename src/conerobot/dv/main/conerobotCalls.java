/**
 * 
 */
package conerobot.dv.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

import conerobotprj.dv.lib.AccountAddOffer;
import conerobotprj.dv.lib.AccountContractRenew;
import conerobotprj.dv.lib.AccountCreation;
import conerobotprj.dv.lib.AccountInfoGet;
import conerobotprj.dv.lib.AccountInfoUpdate;
import conerobotprj.dv.lib.AccountNoteAdd;
import conerobotprj.dv.lib.AccountSubsInfoAcctInfoGet;
import conerobotprj.dv.lib.AccountSubsInfoAcctUpdate;
import conerobotprj.dv.lib.AccountSubsInfoSubsUpdate;
import conerobotprj.dv.lib.AddPCRF2C1Bypass;
import conerobotprj.dv.lib.BRASGetMacAddress;
import conerobotprj.dv.lib.BRASnoBandwidthSub;
import conerobotprj.dv.lib.BalanceLimitUpdate;
import conerobotprj.dv.lib.DirectoryServiceRcterminstRateOverride;
import conerobotprj.dv.lib.DirectoryServiceSubsCreation;
import conerobotprj.dv.lib.EMAHSSDisconnection;
import conerobotprj.dv.lib.EMAHSSSubscription;
import conerobotprj.dv.lib.EMASessionLogout;
import conerobotprj.dv.lib.EMASessionRefresh;
import conerobotprj.dv.lib.ExemptedAccountAdding;
import conerobotprj.dv.lib.ExtendedDataUpdate;
import conerobotprj.dv.lib.HuaweiAPNProv;
import conerobotprj.dv.lib.HuaweiHLRADDSubs;
import conerobotprj.dv.lib.HuaweiHLRQuery;
import conerobotprj.dv.lib.HuaweiHLRRemoveSub;
import conerobotprj.dv.lib.HuaweiTelnetListSub;
import conerobotprj.dv.lib.InventoryLoad;
import conerobotprj.dv.lib.InventoryUpdate;
import conerobotprj.dv.lib.InvoiceDetailsAdj;
import conerobotprj.dv.lib.LRAttach;
import conerobotprj.dv.lib.LRDisconnect;
import conerobotprj.dv.lib.NCAFixed;
import conerobotprj.dv.lib.NCAMobile;
import conerobotprj.dv.lib.NRCTerminstFind;
import conerobotprj.dv.lib.NRCTerminstUpdate;
import conerobotprj.dv.lib.NonVoucherRecharge;
import conerobotprj.dv.lib.NrcAddConstruct;
import conerobotprj.dv.lib.OfferAddConstruct;
import conerobotprj.dv.lib.OfferAddExtended;
import conerobotprj.dv.lib.OfferDisconnect;
import conerobotprj.dv.lib.OrderCancelCSM;
import conerobotprj.dv.lib.OrderCommitted;
import conerobotprj.dv.lib.OrderManualNodeCompletion;
import conerobotprj.dv.lib.PCRFAccountCheck;
import conerobotprj.dv.lib.PCRFAccountCreation;
import conerobotprj.dv.lib.PCRFAccountTransfer;
import conerobotprj.dv.lib.PCRFCheckSubscriber;
import conerobotprj.dv.lib.PCRFCreateUser;
import conerobotprj.dv.lib.PCRFGetUser;
import conerobotprj.dv.lib.PCRFTestingLabDeleteUser;
import conerobotprj.dv.lib.PaymentInsert;
import conerobotprj.dv.lib.PaymentReversalConstruct;
import conerobotprj.dv.lib.PostBillAdj;
import conerobotprj.dv.lib.PostpaidOBAdjust;
import conerobotprj.dv.lib.PrimaryOfferSwap;
import conerobotprj.dv.lib.QueryCRMUserDetailsByUserName;
import conerobotprj.dv.lib.RcterminstRateOverride;
import conerobotprj.dv.lib.Readbrasconvertbbuserid;
import conerobotprj.dv.lib.SOSWAPSubscriberRetrieve;
import conerobotprj.dv.lib.SOSwapOfferAdd;
import conerobotprj.dv.lib.SOSwapOfferMain;
import conerobotprj.dv.lib.SOSwapOrderCommitted;
import conerobotprj.dv.lib.SearchOrderWorkRef;
import conerobotprj.dv.lib.SimRegistration;
import conerobotprj.dv.lib.SimRegistrationAddressAssoc;
import conerobotprj.dv.lib.SimRegistrationAddressAssocEnabled;
import conerobotprj.dv.lib.SimRegistrationAddressCreation;
import conerobotprj.dv.lib.SimRegistrationAddressOnly;
import conerobotprj.dv.lib.SimRegistrationDOBOnly;
import conerobotprj.dv.lib.SimRegistrationGenderOnly;
import conerobotprj.dv.lib.SimRegistrationIDonAccount;
import conerobotprj.dv.lib.SimRegistrationLastnameFnameOnly;
import conerobotprj.dv.lib.SubsUpdateUsageOrder;
import conerobotprj.dv.lib.SubscriberAdjBalance;
import conerobotprj.dv.lib.SubscriberCreationFromOffer;
import conerobotprj.dv.lib.SubscriberInfoUpdate;
import conerobotprj.dv.lib.SubscriberPointingShadowBal;
import conerobotprj.dv.lib.SubscriberRetrieveConstruct;
import conerobotprj.dv.lib.SubscriberStateChange;
import conerobotprj.dv.lib.SubscriberTransfer;
import conerobotprj.dv.lib.SubscriberUpdateBalExpirationDate;
import conerobotprj.dv.lib.SubscriberUpdateTargetAccount;
import conerobotprj.dv.lib.TokenRetrieve;
import conerobotprj.dv.lib.Utility;
import conerobotprj.dv.lib.imagedocumentid;
import conerobotprj.dv.lib.imageurlfind;
import conerobotprj.dv.lib.noteAdd;
import conerobotprj.dv.lib.noteUpdate;
import conerobotprj.dv.lib.orderRequeue;
import conerobotprj.dv.lib.orderReset;
import conerobotprj.dv.lib.parentAccountUpdate;
import conerobotprj.dv.lib.parentAccountUpdateViaAccount;

import com.workpoint.client.ClientContext;
import com.workpoint.client.Job;
import com.workpoint.common.data.JobNodeData;
import com.workpoint.common.data.TableDataList;

/**
 * @author khfighter
 *
 */
public class conerobotCalls {

	public static void disableWarning() {
		System.err.close();
		System.setErr(System.out);
	}

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
			if (args.length >= 2) {
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
						LOGGER.log(Level.INFO, "Processing MSISDN: " + line.split("::")[0]);
						oac.callingOfferAdd(line.split("::")[0], line.split("::")[1], line.split("::")[2]);

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

				// Calling Account Infor Retrieve Function
				else if (args[0].equals("callingAccountInfoRetrieve") == true) {

					AccountInfoGet aig = new AccountInfoGet();
					aig.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Starting Account Info Retrieve");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						aig.callingAccountInfoRetrieve(line);

					}

				}
				// End Next Function

				// Calling Account Contract Renew
				else if (args[0].equals("AccountContractRenew") == true) {

					AccountContractRenew acr = new AccountContractRenew();
					acr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Starting Account Contract Renew");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						acr.callAccountContractRenew(line.split("::")[0], line.split("::")[1]);
						LOGGER.log(Level.INFO, "2nd Run for the non-completed info Account");
						acr.callAccountContractRenew(line.split("::")[0], line.split("::")[1]);

					}

				}
				// End Next Function

				// Calling Primary offer Swapping
				else if (args[0].equals("callPrimaryOfferSwapping") == true) {

					PrimaryOfferSwap pos = new PrimaryOfferSwap();
					pos.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Starting Primary offer swapping");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						pos.callPrimaryOfferSwapping(line.split("::")[0], line.split("::")[1]);

					}

				}
				// End Next Function

				// Calling Account Information Update
				else if (args[0].equals("callingAccountInfoUpdate") == true) {

					AccountInfoUpdate aiu = new AccountInfoUpdate();
					aiu.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Account Information Updating...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						aiu.callingAccountInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7], line.split("::")[8], line.split("::")[9]);
					}

				}
				// End Next Function

				// Calling SUB Information Update
				else if (args[0].equals("callingSubInfoUpdate") == true) {

					SubscriberInfoUpdate siu = new SubscriberInfoUpdate();
					siu.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Subscriber Information Updating...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						siu.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[6], line.split("::")[10]);
					}

				}
				// End Next Function

				// Calling Postbill adjustment Function
				else if (args[0].equals("PostBillAdj") == true) {

					PostBillAdj pba = new PostBillAdj();
					pba.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Postpaid Adjustment Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// pba.callPostBillAdj(AccountNo, vadjReasonCode, vamount, vannotation,
						// vorigBillRefNo, vorigBillRefResets);
						LOGGER.log(Level.INFO, "--------------------------------------");
						pba.callPostBillAdj(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5]);
					}

				}
				// End Next Function

				// Calling Inventory load
				else if (args[0].equals("invdload") == true) {

					InventoryLoad il = new InventoryLoad();
					il.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Inventory loading start...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// CallingPostpaidOBAdjust(String AccountNo, String Amount, String Annotation)
						LOGGER.log(Level.INFO, "--------------------------------------");
						il.callInventoryLoad(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

				}
				// End Next Function

				// Calling function to override the rate
				else if (args[0].equals("rctermrateoverride") == true) {

					RcterminstRateOverride il = new RcterminstRateOverride();
					il.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Rate Overriden Process starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// il.callRTIRO(InstanceID, ActiveDT, Rate);
						LOGGER.log(Level.INFO, "--------------------------------------");
						il.callRTIRO(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

				}
				// End Next Function

				// Calling function to override the rate
				else if (args[0].equals("subscriberCreate") == true) {

					SubscriberCreationFromOffer sc = new SubscriberCreationFromOffer();
					sc.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Subscriber Creation Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// sc.callSCFO(ExternalID, ExternalIDType, Company, Fname, lname,
						// AcctNo,address1, address2, offerID);
						LOGGER.log(Level.INFO, "--------------------------------------");
						sc.callSCFO(line.split("::")[0], line.split("::")[1], line.split("::")[2], line.split("::")[3],
								line.split("::")[4], line.split("::")[5], line.split("::")[6], line.split("::")[7],
								line.split("::")[8]);
					}

				}
				// End Next Function

				// Calling function to Subscriber the profile to HSS
				else if (args[0].equals("EMAHSSSubscription") == true) {

					EMAHSSSubscription emasub = new EMAHSSSubscription();

					LOGGER.log(Level.INFO, "Subscription to HSS Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// emasub.callHSSSubscription(imsi, MSISDN, KI, LTEProfile, Paymentmode);
						emasub.callHSSSubscription(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4]);
					}

				}
				// End Next Function

				// Calling function to disconnect subscriber's profile from HSS
				else if (args[0].equals("EMAHSSDisconnection") == true) {

					EMAHSSDisconnection emasub = new EMAHSSDisconnection();

					LOGGER.log(Level.INFO, "Disconnection from HSS Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");

						emasub.callHSSDisconnection(line.split("::")[0]);

					}

				}
				// End Next Function

				// Calling function to disconnect subscriber's profile from HSS
				else if (args[0].equals("RefreshSession") == true) {

					EMASessionRefresh emasession = new EMASessionRefresh();

					LOGGER.log(Level.INFO, "Session Refresh Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");

						emasession.callGetEMASession(line.split("::")[0]);
					}

				}
				// End Next Function

				// Calling function to disconnect subscriber's profile from HSS
				else if (args[0].equals("CloseSession") == true) {

					EMASessionLogout emasession = new EMASessionLogout();

					// LOGGER.log(Level.INFO, "Session Refresh Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");

						emasession.callLogOutEMASession(line.split("::")[0]);
					}

				}
				// End Next Function

				// Calling function to disconnect subscriber's profile from HSS
				else if (args[0].equals("SubsStateChange") == true) {

					SubscriberStateChange ssc = new SubscriberStateChange();
					ssc.retreiveCred(new File("src/config/soapconnection.cfg"));

					// LOGGER.log(Level.INFO, "Session Refresh Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");

						ssc.callSubsStateChange(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

				}
				// End Next Function

				// Calling function to UPDATE BALANCE/CREDIT LIMIT
				else if (args[0].equals("BalanceLimitUpdate") == true) {

					BalanceLimitUpdate blu = new BalanceLimitUpdate();
					blu.retreiveCred(new File("src/config/soapconnection.cfg"));

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// blu.callBalanceLimitUpdate(MSISDN, Amounttbupdated, UpdateFlag);
						blu.callBalanceLimitUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

				}
				// End Next Function
				// Calling function to upgrade from 3g to HLR
				else if (args[0].equals("LTEUpgrade") == true) {

					EMAHSSSubscription emasub = new EMAHSSSubscription();
					EMAHSSDisconnection emadis = new EMAHSSDisconnection();

					LOGGER.log(Level.INFO, "LTE upgrades...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// emadis.callHSSDisconnection(imsi);
						emadis.callHSSDisconnection(line.split("::")[0]);
						// emasub.callHSSSubscription(imsi, MSISDN, KI, LTEProfile, Paymentmode);
						emasub.callHSSSubscription(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4]);
					}

				}
				// End Next Function

				// Calling function to Redirect RC/NRC liability
				else if (args[0].equals("LRAttach") == true) {

					LRAttach LR = new LRAttach();
					LR.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Liability redirection starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// LR.callLRAttach(SourceMSISDN, TargetAccount, LRtplID);
						LR.callLRAttach(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

				}
				// End Next Function

				// Calling function to Redirect RC/NRC liability
				else if (args[0].equals("NCAMobile") == true) {

					NCAMobile mob = new NCAMobile();
					mob.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Subscriber Creation Bulk starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// mob.callNCAMobile(ModeFlag, PrimaryOfferId, ParentAccount, Fname, Lname,
						// CompanyName, ID, IDType, IDExpiry, NotifNumber, Address1,
						// Address2, Address3, Address4, city, state, country, MSISDN, ICCID, IMSI);
						mob.callNCAMobile(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7], line.split("::")[8], line.split("::")[9], line.split("::")[10],
								line.split("::")[11], line.split("::")[12], line.split("::")[13], line.split("::")[14],
								line.split("::")[15], line.split("::")[16], line.split("::")[17], line.split("::")[18],
								line.split("::")[19]);

					}

				}
				// End Next Function

				// Calling function to Redirect RC/NRC liability
				else if (args[0].equals("NVR") == true) {

					NonVoucherRecharge nvr = new NonVoucherRecharge();
					nvr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Non voucher Recharge starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// nvr.callNVR(MSISDN, Amount, Comment, Source);
						nvr.callNVR(line.split("::")[0], line.split("::")[1], line.split("::")[2], line.split("::")[3]);
					}

				}
				// End Next Function

				// Calling function to update parent account
				else if (args[0].equals("parentaccountupdate") == true) {

					parentAccountUpdate pau = new parentAccountUpdate();
					pau.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Parent Account Update Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// pau.callingAccountInfoUpdate(MSISDN, ParentAccount);
						pau.callingParentAccountUpdate(line.split("::")[0], line.split("::")[1]);
					}

				}
				// End Next Function

				// Calling Token Retrieval
				else if (args[0].equals("TokenRetrieve") == true) {

					TokenRetrieve tr = new TokenRetrieve();
					LOGGER.log(Level.INFO, "Token Retrieving...");
					tr.callLRTokenRetrieve("sapi", "bulkprov", "P@ssw0rd");
				}
				// End Next Function

				// Calling function to new account creation
				else if (args[0].equals("AccountCreation") == true) {

					AccountCreation ac = new AccountCreation();
					ac.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Account Creation Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// ac.callAccountCreation(Fname, Lname, Cname, AccountType, PODetails,
						// NotificationNumber, OrigAccount, MarketCode);

						ac.callAccountCreation(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7]);

					}

				}
				// End Next Function

				// Calling function SO Swap: OfferAdd
				else if (args[0].equals("SOSwapOfferAdd") == true) {

					SOSwapOfferAdd ssoa = new SOSwapOfferAdd();
					ssoa.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SO SWAP - Adding New Offer");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						ssoa.callingSOSwapOfferAdd(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
						LOGGER.log(Level.INFO,
								"Order ID: " + ssoa.orderId + " || Service Order ID: " + ssoa.serviceOrderId);
					}

				}
				// End Next Function

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("SOSwapSubscriberRetrieve") == true) {

					SOSWAPSubscriberRetrieve ssor = new SOSWAPSubscriberRetrieve();
					ssor.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SO SWAP - Subscriber Retrieve");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						ssor.callSubscriberRetrieveService(line.split("::")[0], line.split("::")[1],
								line.split("::")[2]);
						LOGGER.log(Level.INFO,
								"SO SWAP - Instance to be disconnected: " + ssor.OfferInstanceDisconnected);
					}

				}
				// End Next Function
				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("SOSwapOfferMain") == true) {

					SOSwapOfferMain SOSWAP = new SOSwapOfferMain();
					LOGGER.log(Level.INFO, "SO SWAP STARTS....");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						SOSWAP.callingSOSwapOfferMain(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3]);

					}

				}
				// End Next Function

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("CommittingOrder") == true) {

					SOSwapOrderCommitted ord = new SOSwapOrderCommitted();
					ord.retreiveCred(new File("src/config/soapconnection.cfg"));
					;
					LOGGER.log(Level.INFO, "Order committing STARTS....");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						ord.callingCommitOrder(line.split("::")[0]);

					}

				}
				// End Next Function

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("REQORDER") == true) {

					orderRequeue ord = new orderRequeue();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						ord.reset_job(line.split("::")[0], line.split("::")[1]);

					}

					LOGGER.log(Level.INFO, "REQ JOB is successfully completed");
				}
				// End Next Function
				// PCRFGetUser

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("PCRFGetUser") == true) {

					PCRFGetUser pcrf = new PCRFGetUser();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						pcrf.callPCRFGetUser(line.split("::")[0], line.split("::")[1]);
						Thread.sleep(1000);

					}

					LOGGER.log(Level.INFO, "PCRFGetUser JOB is successfully completed");
				}
				// End Next Function

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("NoteAdd") == true) {

					noteAdd na = new noteAdd();
					na.retreiveCred(new File("src/config/soapconnection.cfg"));
					;
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						// na.callnoteAdd(caseID, AccountNo, UserIDMacAddress, Type);
						if (!line.split("::")[2].equalsIgnoreCase("NA")) {
							na.callnoteAdd(line.split("::")[0], line.split("::")[1], line.split("::")[2], "1"); // userID
						}

						if (!line.split("::")[3].equalsIgnoreCase("NA")) {
							na.callnoteAdd(line.split("::")[0], line.split("::")[1], line.split("::")[3], "2"); // mac
																												// address
						}

					}

					LOGGER.log(Level.INFO, "NoteAdd JOB is successfully completed");
				}
				// End Next Function

				// Calling function node reset
				else if (args[0].equals("RESETNODE") == true) {

					orderReset ord = new orderReset();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						ord.reset_failed_node(line.split("::")[0], line.split("::")[1], line.split("::")[2]);

					}

					LOGGER.log(Level.INFO, "REQ JOB is successfully completed");
				}
				// End Next Function

				// Calling function SO Swap: SOSwapSubscriberRetrieve
				else if (args[0].equals("CancelOrder") == true) {

					OrderCancelCSM oc = new OrderCancelCSM();
					orderRequeue ord = new orderRequeue();
					oc.retreiveCred(new File("src/config/soapconnection.cfg"));
					;
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						oc.callingCancelOrder(line.split("::")[0]);
						ord.reset_job(line.split("::")[1], line.split("::")[2]);

					}

					LOGGER.log(Level.INFO, "ORDERCANCEL JOB is successfully completed");
				}
				// End Next Function

				// Calling PCRF Delete User
				else if (args[0].equals("PCRFDeleteUser") == true) {

					PCRFGetUser pcrf = new PCRFGetUser();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						pcrf.deleteUser(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "PCRFDeleteUser JOB is successfully completed");
				}
				// End Next Function

				// Calling PCRF Delete User
				else if (args[0].equals("PCRFTESTINGLABDeleteUser") == true) {

					PCRFTestingLabDeleteUser pcrf = new PCRFTestingLabDeleteUser();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						pcrf.deleteUser(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "PCRFTESTINGLABDeleteUser JOB is successfully completed");
				}
				// End Next Function

				else if (args[0].equals("PCRFTESTINGLABGetUser") == true) {

					PCRFTestingLabDeleteUser pcrf = new PCRFTestingLabDeleteUser();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						pcrf.callPCRFGetUser(line.split("::")[0], line.split("::")[1]);
						Thread.sleep(1000);

					}

					LOGGER.log(Level.INFO, "PCRFTESTINGLABGetUser JOB is successfully completed");
				}
				// End Next Function

				// Calling function: node update
				else if (args[0].equals("noteUpdate") == true) {

					noteUpdate nu = new noteUpdate();
					nu.retreiveCred(new File("src/config/soapconnection.cfg"));
					;
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						nu.callnoteUpdate(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "NoteUpdate JOB is successfully completed");
				}
				// End Next Function

				// Calling function: node update
				else if (args[0].equals("invUpdate") == true) {

					InventoryUpdate IU = new InventoryUpdate();
					IU.retreiveCred(new File("src/config/soapconnection.cfg"));
					;
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						// IU.callInventoryUpdate(inventoryType, InventoryId, inventoryIdResets,
						// BBuserID, MacAddress);
						IU.callInventoryUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4]);

					}

					LOGGER.log(Level.INFO, "Inventory Update JOB is successfully completed");
				}
				// End Next Function

				// Calling function: ShadowRedirectionCreditSet
				else if (args[0].equals("ShadowRedirectionCreditSet") == true) {

					OfferAddConstruct oac = new OfferAddConstruct();
					oac.retreiveCred(new File("src/config/soapconnection.cfg"));

					SubscriberUpdateTargetAccount suta = new SubscriberUpdateTargetAccount();
					suta.retreiveCred(new File("src/config/soapconnection.cfg"));

					SubscriberPointingShadowBal spsb = new SubscriberPointingShadowBal();
					spsb.retreiveCred(new File("src/config/soapconnection.cfg"));

					parentAccountUpdate pau = new parentAccountUpdate();
					pau.retreiveCred(new File("src/config/soapconnection.cfg"));

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						// oac.callingOfferAdd(MSISDN, OfferID, isWorkflow);
						// suta.callingSubTargetAccountUpdate(SubscrNo, SubscrNoResets,
						// TargetAccountNo);
						// spsb.callingPointingShadow(SubscrNo, SubscrNoResets, MSISDN, ShadowBalID,
						// RealBalID, BalAmnt, vnextResetDate);
						// Timeformat: 2019-05-20T11:40:49+02:00
						// MSISDN::OFFERID::SUBSCRNO::SUBSCRNORESETS::TARGETACCOUNTNO::SHADOWBALID::REALBALANCEID::SHADOWBALAMNTANDLIMIT::vnextResetDate
						oac.callingOfferAdd(line.split("::")[0], line.split("::")[1], "0");
						Thread.sleep(5000);
						// pau.callingParentAccountUpdate(line.split("::")[0], line.split("::")[4]);
						suta.callingSubTargetAccountUpdate(line.split("::")[2], line.split("::")[3],
								line.split("::")[4]);
						Thread.sleep(40000);
						spsb.callingPointingShadow(line.split("::")[2], line.split("::")[3], line.split("::")[0],
								line.split("::")[5], line.split("::")[6], line.split("::")[7], line.split("::")[8]);
					}

					LOGGER.log(Level.INFO, "ShadowRedirectionCreditSet JOB is successfully completed");
				}
				// End Next Function
				// Calling function: Usage Order Update to Real
				else if (args[0].equals("UpdateUsageOrder") == true) {

					SubsUpdateUsageOrder suo = new SubsUpdateUsageOrder();
					suo.retreiveCred(new File("src/config/soapconnection.cfg"));

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// suo.callingSubUsageOrderUpdate(SubscrNo, SubscrNoResets);
						suo.callingSubUsageOrderUpdate(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "ShadowRedirectionCreditSet JOB is successfully completed");
				}
				// End Next Function

				// Calling function: HuaweiHLRQuery
				else if (args[0].equals("callHuaweiHLRQuery") == true) {

					HuaweiHLRQuery ch = new HuaweiHLRQuery();

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						ch.callHuaweiHLRQuery(line);
					}

					LOGGER.log(Level.INFO, "callHuaweiHLRQuery JOB is successfully completed");
				}
				// End Next Function

				// Calling function: HuaweiHLRADDSUB
				else if (args[0].equals("callHuaweiHLRQueryADDSubs") == true) {

					HuaweiHLRADDSubs hha = new HuaweiHLRADDSubs();

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// hha.callHuaweiHLRQueryADDSubs(PhoneNumber, IMSI, KIVALUE, TPLID);
						hha.callHuaweiHLRQueryADDSubs(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3]);
					}

					LOGGER.log(Level.INFO, "callHuaweiHLRQueryADDSubs JOB is successfully completed");
				}
				// End Next Function

				// Calling function to override the rate
				else if (args[0].equals("LRDisconnect") == true) {

					LRDisconnect ld = new LRDisconnect();
					ld.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Liability Disconnection starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						ld.callLRDisconnect(line.split("::")[0], line.split("::")[1]);
						Thread.sleep(1000);
					}

					LOGGER.log(Level.INFO, "LRDisconnect JOB is successfully completed");

				}
				// End Next Function

				// Calling function to add account into tax exempted list
				else if (args[0].equals("TaxExempted") == true) {

					ExemptedAccountAdding eaa = new ExemptedAccountAdding();
					eaa.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Adding Account into Tax Exempted List..");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						eaa.callingAccountExempted(line);
					}

					LOGGER.log(Level.INFO, "TaxExempted JOB is successfully completed");

				}
				// End Next Function

				// Calling function to Account offer NRC Adding
				else if (args[0].equals("AccountOfferNRCAddRateAnnotationSet") == true) {

					AccountAddOffer aao = new AccountAddOffer();
					aao.retreiveCred(new File("src/config/soapconnection.cfg"));

					NRCTerminstFind nf = new NRCTerminstFind();
					nf.retreiveCred(new File("src/config/soapconnection.cfg"));

					NRCTerminstUpdate ntu = new NRCTerminstUpdate();
					ntu.retreiveCred(new File("src/config/soapconnection.cfg"));

					OrderCommitted oc = new OrderCommitted();
					oc.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Adding Account Offer");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// aao.callingOfferAdd(AccountNo, OfferId);
						// 2064659::6321::775::35.16::Bulk reversal payment for preactivated VISA Card
						// payemnts.
						// ACCOUNTNO::OFFERID::NRCOFFERMAP::NRCRATE::ANNOTATION

						aao.callingOfferAdd(line.split("::")[0], line.split("::")[1]);

						if (aao.checkifsuccess.equals(true)) {
							nf.callingNRCFind(aao.OfferInstId, line.split("::")[2]);
							ntu.callingNRCUpdate(nf.NRCTermInstId, line.split("::")[3], line.split("::")[4]);
							oc.callingCommitOrder(aao.orderId, "0");
						} else {
							LOGGER.log(Level.INFO, "RESULT ADD OFFER FAIL : " + line.split("::")[0]);
						}
						// Nullify all param in case the function calculating
						// Global param fails.
						aao.OfferInstId = null;
						nf.NRCTermInstId = null;
						aao.orderId = null;
						aao.checkifsuccess = null;

					}

					LOGGER.log(Level.INFO, "AccountOfferNRCAddRateAnnotationSet JOB is successfully completed");

				}
				// End Next Function
				// nrcFind

				// Calling function to Query NRC
				else if (args[0].equals("nrcFind") == true) {

					NRCTerminstFind nf = new NRCTerminstFind();
					nf.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "NRCTERMINST QUERYING");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// nf.callingNRCFind(OfferInstId, NRCTermID);
						nf.callingNRCFind(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "nrcFind JOB is successfully completed");

				}

				// End Next Function
				// NRCUpdate

				// Calling function to Query NRC
				else if (args[0].equals("NRCUpdate") == true) {

					NRCTerminstUpdate ntu = new NRCTerminstUpdate();
					ntu.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "NRCTERMINST Updating");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						// ntu.callingNRCUpdate(NRCTermInst, Rate, Annotation);
						ntu.callingNRCUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

					LOGGER.log(Level.INFO, "NRCTERMINST JOB is successfully completed");
				}
				// End Next Function

				// Calling function to Remove subscriber from huawei HLR
				else if (args[0].equals("HuaweiRMVSub") == true) {

					HuaweiHLRRemoveSub hrs = new HuaweiHLRRemoveSub();

					LOGGER.log(Level.INFO, "Removing Subscriber from Huawei HLR");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
//hrs.callHuaweiHLRQueryRMVSubs(PhoneNumber, IMSI);
						hrs.callHuaweiHLRQueryRMVSubs(line.split("::")[0], line.split("::")[1]);

					}

					LOGGER.log(Level.INFO, "HuaweiRMVSub JOB is successfully completed");
				}
				// End Next Function

				// Calling function to telnet HSS
				else if (args[0].equals("telnet") == true) {

					HuaweiTelnetListSub ht = new HuaweiTelnetListSub("10.134.4.103", "", "");
					ht.TelnetQueryCustomer();
					LOGGER.log(Level.INFO, "telnet JOB is successfully completed");
				}
				// End Next Function

				// Calling function to find invoice
				else if (args[0].equals("billimagefind") == true) {

					LOGGER.log(Level.INFO, "Process: billimagefind started...");
					BigInteger size = new BigInteger("1");

					imageurlfind iuf = new imageurlfind();
					iuf.retreiveCred(new File("src/config/soapconnection.cfg"));

					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						iuf.callingImageURLFind(line);

						// get the url of web page
						URL url = new URL(iuf.fileurl);

						// create a connection
						HttpURLConnection conn;
						try {
							// open stream to get size of page
							conn = (HttpURLConnection) url.openConnection();

							// set request method.
							conn.setRequestMethod("HEAD");

							// get the input stream of process
							conn.getInputStream();

							// store size of file
							size = BigInteger.valueOf(conn.getContentLength() / 1024);

							LOGGER.log(Level.INFO,
									line + "::" + FilenameUtils.getBaseName(url.getPath()).split("_")[2] + "::"
											+ FilenameUtils.getBaseName(url.getPath()) + ".pdf" + "::" + size + "KB::"
											+ iuf.fileurl);
							conn.getInputStream().close();
						} catch (Exception e) {
							LOGGER.log(Level.SEVERE, "Reading file fail.");
						}

					}

					LOGGER.log(Level.INFO, "billimagefind is successfully completed");
				}

				// End Next Function

				// Calling SO disconnection
				else if (args[0].equals("soDisconnection") == true) {

					OfferDisconnect od = new OfferDisconnect();
					od.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "soDisconnection starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// od.callingOfferDisconnect(subscrNo, subscrNoReset, OfferId);
						od.callingOfferDisconnect(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
					}

					LOGGER.log(Level.INFO, "soDisconnection JOB is successfully completed");

				}

				// End Next Function
				// Invoice detail adj
				else if (args[0].equals("invoicedetailADJ") == true) {

					InvoiceDetailsAdj ida = new InvoiceDetailsAdj();
					ida.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "InvoiceDetailsADJ starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// ida.callPostBillAdj(AccountNo, vadjReasonCode, vamount, vannotation,
						// vorigBillRefNo, vorigBillRefResets, TransCode, vbillInvoiceRow);
						ida.callPostBillAdj(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7]);
					}

					LOGGER.log(Level.INFO, "InvoiceDetailsADJ JOB is successfully completed");

				}

				// End Next Function

				else if (args[0].equals("searchOrderWorkRef") == true) {

					SearchOrderWorkRef swr = new SearchOrderWorkRef();

					LOGGER.log(Level.INFO, "searchOrderWorkRef starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						swr.QueryWorkOrder(line);
					}

					LOGGER.log(Level.INFO, "searchOrderWorkRef JOB is successfully completed");

				}

				// End Next Function

				else if (args[0].equals("SubsAdjBal") == true) {

					SubscriberAdjBalance sab = new SubscriberAdjBalance();
					sab.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SubsAdjBal JOB starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// sab.callSubsAdjBalance(MSISDN, AMOUNT, Annotation, BalanceId, Day);
						sab.callSubsAdjBalance(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4]);
					}

					LOGGER.log(Level.INFO, "SubsAdjBal JOB is successfully completed");

				}

				// End Next Function

				// Calling function to Remove subscriber from huawei HLR
				else if (args[0].equals("HuaweiAPNProv") == true) {

					HuaweiAPNProv hap = new HuaweiAPNProv();

					LOGGER.log(Level.INFO, "Removing Subscriber from Huawei HLR");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// hap.callHuaweiAPNProv(PhoneNumber, APNID);
						hap.callHuaweiAPNProv(line.split("::")[0], line.split("::")[1]);

					}

					LOGGER.log(Level.INFO, "HuaweiRMVSub JOB is successfully completed");
				}
				// End Next Function

				else if (args[0].equals("ExtendOfferAdd") == true) {

					OfferAddExtended oae1 = new OfferAddExtended();
					oae1.retreiveCred(new File("src/config/soapconnection.cfg"));

					/*
					 * OfferAddExtended oae2 = new OfferAddExtended(); oae2.retreiveCred(new
					 * File("src/config/soapconnection.cfg"));
					 * 
					 * OrderCommitted oc = new OrderCommitted(); oc.retreiveCred(new
					 * File("src/config/soapconnection.cfg"));
					 */

					LOGGER.log(Level.INFO, "ExtendOfferAdd JOB starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						// oae1.callingOfferAdd(MSISDN, OfferID, OrderId, ServiceOrderId);
						oae1.callingOfferAdd(line.split("::")[0], line.split("::")[1], line.split("::")[2], "NA", "NA");
						Thread.sleep(500);

					}

					LOGGER.log(Level.INFO, "ExtendOfferAdd JOB is successfully completed");

				}

				// End Next Function

				else if (args[0].equals("AccountSubsInfoUpdate") == true) {

					AccountSubsInfoAcctUpdate aiau = new AccountSubsInfoAcctUpdate();
					aiau.retreiveCred(new File("src/config/soapconnection.cfg"));

					AccountSubsInfoSubsUpdate aisu = new AccountSubsInfoSubsUpdate();
					aisu.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "AccountSubsInfoUpdate JOB starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						try {
							// aiau.callingAccountInfoUpdate(MSISDN, Fname, Lname, address, City,
							// ContactPhone, id, Company, genderV, DoB, IDType);
							aiau.callingAccountInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
									line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
									line.split("::")[7], line.split("::")[8], line.split("::")[9],
									line.split("::")[10]);
							aisu.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
									line.split("::")[5], line.split("::")[9]);
							Thread.sleep(1000);
						} catch (Exception e) {
							LOGGER.log(Level.SEVERE,
									"RESULT::FAIL SUB/ACCT INFO UPDATE : " + line + "==>" + e.getMessage());
						}
					}

					LOGGER.log(Level.INFO, "AccountSubsInfoUpdate JOB is successfully completed");

				}

				// End Next Function

				// Calling function to Redirect RC/NRC liability
				else if (args[0].equals("NCAFixed") == true) {

					NCAFixed fixed = new NCAFixed();
					fixed.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Fixed Subscriber Creation Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// fixed.callNCAFixed(ModeFlag, PrimaryOfferId, ParentAccount, Fname, Lname,
						// CompanyName, ID, IDType, IDExpiry, NotifNumber, Address1, Address2,
						// Address3, Address4, city, state, country, FIXEDEXTERNALID, ExternalIDType);
						fixed.callNCAFixed(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7], line.split("::")[8], line.split("::")[9], line.split("::")[10],
								line.split("::")[11], line.split("::")[12], line.split("::")[13], line.split("::")[14],
								line.split("::")[15], line.split("::")[16], line.split("::")[17], line.split("::")[18]);
					}

					LOGGER.log(Level.INFO, "Fixed Subscriber Creation JOB is successfully completed");

				}
				// End Next Function

				// Calling function to transfer subscriber from one account to another
				else if (args[0].equals("SubsTransfer") == true) {

					SubscriberTransfer st = new SubscriberTransfer();
					st.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SubsTransfer Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// st.callSubsTransfer(MSISDN, TargetAccount, Comments, ExternalIDType);

						LOGGER.log(Level.INFO, "--------------------------------------");
						st.callSubsTransfer(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3]);

					}

					LOGGER.log(Level.INFO, "SubsTransfer JOB is successfully completed");

				}
				// End Next Function

				// Querying CRM user info from username
				else if (args[0].equals("QueryCRMUserDetailsByUserName") == true) {

					QueryCRMUserDetailsByUserName Q = new QueryCRMUserDetailsByUserName();
					LOGGER.log(Level.INFO, "QueryCRMUserDetailsByUserName Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						Q.queryCRMUserDetailsByUserName(line);

					}

					LOGGER.log(Level.INFO, "QueryCRMUserDetailsByUserName JOB is successfully completed");

				}
				// End Next Function

				// Creating Directory Subscriber
				else if (args[0].equals("DirectoryServiceSubsCreation") == true) {

					DirectoryServiceSubsCreation d = new DirectoryServiceSubsCreation();
					d.retreiveCred(new File("src/config/soapconnection.cfg"));

					DirectoryServiceRcterminstRateOverride rateoverride = new DirectoryServiceRcterminstRateOverride();
					rateoverride.retreiveCred(new File("src/config/soapconnection.cfg"));

					OrderCommitted oc = new OrderCommitted();
					oc.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "DirectoryServiceSubsCreation Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// d.callSCFO(ExternalID, ExternalIDType, Company, Fname, lname, AcctNo,
						// address1, address2, offerID);
						d.callSCFO(line.split("::")[0], line.split("::")[1], line.split("::")[2], line.split("::")[3],
								line.split("::")[4], line.split("::")[5], line.split("::")[6], line.split("::")[7],
								line.split("::")[8], line.split("::")[9], line.split("::")[11], line.split("::")[12],
								line.split("::")[13], line.split("::")[14]);
						rateoverride.callRTIRO(d.rcTermInstId, line.split("::")[9], line.split("::")[10]);
						// Ensuring all calls with no error before committing the order
						if (rateoverride.errorRateOverride == 0 && d.errorFindRCTerm == 0 && d.errorCreatingSub == 0) {
							oc.callingCommitOrder(d.orderId, "true");
						} else {

							LOGGER.log(Level.SEVERE, "RESULT FAIL CREATING SUBS: " + line.split("::")[0]);
						}

						Thread.sleep(10000);
					}

					LOGGER.log(Level.INFO, "DirectoryServiceSubsCreation JOB is successfully completed");

				}
				// End Next Function

				// PCRF account transfer
				else if (args[0].equals("PCRFAccountTransfer") == true) {

					PCRFAccountCheck ac = new PCRFAccountCheck();
					PCRFAccountCreation acr = new PCRFAccountCreation();
					PCRFCheckSubscriber pcs = new PCRFCheckSubscriber();
					PCRFAccountTransfer pat = new PCRFAccountTransfer();

					LOGGER.log(Level.INFO, "PCRFAccountTransfer Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						// if Subscriber doesn't exist
						if (!pcs.callPCRFGetUser(line.split("::")[2], "1")) {
							LOGGER.log(Level.SEVERE, "SUBSCRIBERCHECK FAIL: " + line + " ==> Subscriber doesn't exist");
						}
						// if account b doesn't exist
						else if (!ac.checkAccount(line.split("::")[0] + line.split("::")[1])) {
							LOGGER.log(Level.SEVERE,
									"TARGETACCOUNTCHECK: " + line + " ==> Target account doesn't exist ");
							LOGGER.log(Level.INFO, line.split("::")[1] + " ==> Creating this account on PCRF");

							// Check if TargetAccount is created correctly.
							if (!acr.pcrfCreateAccount(line.split("::")[0] + line.split("::")[1])) {

								LOGGER.log(Level.SEVERE, "TARGETACCOUNTCREATE FAIL: " + line.split("::")[1]
										+ " ==> Creating Account Fail.");

							} else {
								LOGGER.log(Level.INFO, "Performing : " + line);
								if (!pat.pcrfAccountTransfer(line.split("::")[0] + line.split("::")[1],
										pcs.loginName)) {

									LOGGER.log(Level.SEVERE,
											"ACCOUNTTRANSFER FAIL: " + line + " - " + pat.responseAccountTransfer);
								} else {
									LOGGER.log(Level.SEVERE,
											"ACCOUNTTRANSFER SUCCESS: " + line + " - " + pat.responseAccountTransfer);
								}
							}
						}
						// if both exist then just call transfer directly
						else {
							LOGGER.log(Level.INFO, "Performing Transferring : " + line);
							if (!pat.pcrfAccountTransfer(line.split("::")[0] + line.split("::")[1], pcs.loginName)) {

								LOGGER.log(Level.SEVERE,
										"ACCOUNTTRANSFER FAIL: " + line + " - " + pat.responseAccountTransfer);
							} else {
								LOGGER.log(Level.INFO,
										"ACCOUNTTRANSFER SUCCESS: " + line + " - " + pat.responseAccountTransfer);
							}
						}
					}

					LOGGER.log(Level.INFO, "PCRFAccountTransfer JOB is successfully completed");

				}
				// End Next Function

				// Calling function to disconnect subscriber's profile from HSS
				else if (args[0].equals("MakPayment") == true) {

					PaymentInsert p = new PaymentInsert();
					p.retreiveCred(new File("src/config/soapconnection.cfg"));

					// LOGGER.log(Level.INFO, "Session Refresh Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						LOGGER.log(Level.INFO, "Making Payment : " + line);
						boolean checkifsuccess;
						checkifsuccess = p.Makingpayment(line.split("::")[0], line.split("::")[1], line.split("::")[3]);
						if (checkifsuccess) {
							LOGGER.log(Level.FINEST, "RESULT::PASS MAKE PAYMENT :: " + p.trackingId + ";"
									+ line.split("::")[0] + "::" + line.split("::")[1] + "::" + line.split("::")[3]);
						}
						LOGGER.log(Level.INFO, "--------------------------------------");

					}

				}
				// End Next Function

				// Calling function to updateExpirationDate and active the subs
				else if (args[0].equals("SubUpdateExpirationDateNActivate") == true) {

					SubscriberUpdateBalExpirationDate su = new SubscriberUpdateBalExpirationDate();
					su.retreiveCred(new File("src/config/soapconnection.cfg"));
					SubscriberStateChange ss = new SubscriberStateChange();
					ss.retreiveCred(new File("src/config/soapconnection.cfg"));

					// LOGGER.log(Level.INFO, "Session Refresh Starts...");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						LOGGER.log(Level.INFO, "INPUT : " + line);

						LOGGER.log(Level.INFO, "-----PROLONGING BALANCE EXPIRATION DATE-------");
						su.callingUpdateBalExpiration(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3]);
						LOGGER.log(Level.INFO, "-----ACTIVING SUBSCRIBER-------");
						ss.callSubsStateChange(su.subscrNo, su.subscrNoReset, "2");
						LOGGER.log(Level.INFO, "--------------------------------------");

					}

				}
				// End Next Function

				// Calling function to convert BRAS file
				else if (args[0].equals("BRASinfoconvert") == true) {
					BRASnoBandwidthSub b = new BRASnoBandwidthSub();
					Readbrasconvertbbuserid r = new Readbrasconvertbbuserid();
					LOGGER.log(Level.INFO, "START CONVERTING BRAS INFO");
					b.formatBrasFile(args[1], args[2]);
					r.formatBrasFile(args[1], args[2]);
					LOGGER.log(Level.INFO, "COMPLETE CONVERTING BRAS INFO");
				}
				// End Next Function

				// Calling Function to createUser
				else if (args[0].equals("PCRFCreateUser") == true) {

					PCRFCreateUser cu = new PCRFCreateUser();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						cu.callPCRFCreateUser(line.split("::")[0], line.split("::")[1], "/BTCL_FIXED",
								line.split("::")[2], "btcbroadband.co.bw", line.split("::")[3], line.split("::")[4]);
						Thread.sleep(1000);

					}

					LOGGER.log(Level.INFO, "PCRFCreateUser JOB is successfully completed");
				}
				// End Next Function

				// Calling Function to createUser
				else if (args[0].equals("AccountNoteAdd") == true) {

					AccountNoteAdd ana = new AccountNoteAdd();
					ana.retreiveCred(new File("src/config/soapconnection.cfg"));
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));

					String line;
					while ((line = bfrdr.readLine()) != null) {

						ana.callnoteAdd(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
						Thread.sleep(1000);

					}

					LOGGER.log(Level.INFO, "PCRFCreateUser JOB is successfully completed");
				}
				// End Next Function

				// Calling Function to skip ADDPCRF2c1 for wholesale subscriber
				else if (args[0].equals("AddPCRF2C1Bypass") == true) {

					AddPCRF2C1Bypass a = new AddPCRF2C1Bypass();
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));

					String line;
					while ((line = bfrdr.readLine()) != null) {

						a.addPCRFc1ByPass(line.split("::")[0], Integer.parseInt(line.split("::")[1]),
								line.split("::")[2], line.split("::")[3], line.split("::")[4], line.split("::")[5]);

					}

					LOGGER.log(Level.INFO, "AddPCRF2C1Bypass JOB is successfully completed");
				}
				// End Next Function

				// Calling function to update parent account
				else if (args[0].equals("parentAccountUpdateViaAccount") == true) {

					parentAccountUpdateViaAccount pau = new parentAccountUpdateViaAccount();
					pau.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "Parent Account Update by childAccount Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						// pau.callingAccountInfoUpdate(ChildAccount, ParentAccount);
						pau.callingParentAccountUpdate(line.split("::")[0], line.split("::")[1]);
					}

					LOGGER.log(Level.INFO, "parentAccountUpdateViaAccount JOB is successfully completed");
				}
				// End Next Function

				// Calling function to get Mac Address BRAS file
				else if (args[0].equals("BRASGetMacAddress") == true) {
					BRASGetMacAddress b = new BRASGetMacAddress();
					b.formatBrasFile(args[1], args[2]);
					LOGGER.log(Level.INFO, "COMPLETE BRASGetMacAddress!!");
				}
				// End Next Function

				// Calling function to get DocumentID from bill ref NO
				else if (args[0].equals("documentIDRetrieve") == true) {

					imagedocumentid idi = new imagedocumentid();
					idi.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "PdocumentIDRetrieve Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						LOGGER.log(Level.INFO, "--------------------------------------");
						idi.callingImageDocumentIDFind(line.split("::")[0], line.split("::")[1]);

					}

					LOGGER.log(Level.INFO, "documentIDRetrieve JOB is successfully completed");
				}

				// End Next Function

				// Calling function to PDFInvoicemove2docserver
				else if (args[0].equals("PDFInvoicemove2docserver") == true) {

					disableWarning();
					// get utility function
					Utility u = new Utility();
					imagedocumentid idid = new imagedocumentid();
					idid.retreiveCred(new File("src/config/soapconnection.cfg"));

					u.walk("src\\InvoiceInput");

					// loop through input folder to see input file
					Date date = new Date();
					SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String billRefNo = null;
					String billRefNoReset = null;
					Integer SuccesfulUpload = 0;
					Integer FailUpload = 0;

					// Print Out Number of File
					LOGGER.log(Level.INFO, "-------------------------------------------------------");
					LOGGER.log(Level.INFO, "--------AMOUNT OF FILE TO BE PROCESSED: " + u.listFiles.size());
					LOGGER.log(Level.INFO, "-------------------------------------------------------");

					for (int i = 0; i < u.listFiles.size(); i++) {

						if (u.getBillfileName(u.listFiles.get(i)).contains("BILL")) {
							billRefNo = u.listFiles.get(i).split("_")[2];
							billRefNoReset = u.listFiles.get(i).split("_")[1];

							idid.callingImageDocumentIDFind(billRefNo, billRefNoReset);
							LOGGER.log(Level.FINEST, u.listFiles.get(i) + " " + u.getBillfileName(u.listFiles.get(i)));

							// check size then decide to move the new pdf to invoice file or move to error
							if (idid.fileSize < 1.3) {
								LOGGER.log(Level.INFO,
										billRefNo + "-" + billRefNoReset + "::SUCCESS::Move from "
												+ u.listFiles.get(i).replaceAll(
														Pattern.quote(FileSystems.getDefault().getSeparator()), "/")
												+ " TO " + idid.fileurl.replaceAll("http://10.128.202.81:8001",
														"/staging/billing/docs"));
								// move command

								// Get stats
								SuccesfulUpload += 1;
								billRefNo = null;
								billRefNoReset = null;

							} else {
								LOGGER.log(Level.INFO,
										billRefNo + "-" + billRefNoReset + "::ERROR::Move from "
												+ u.listFiles.get(i).replaceAll(
														Pattern.quote(FileSystems.getDefault().getSeparator()), "/")
												+ " TO Error as Max Document has already had Valid PDF");
								// Move file to processed after the last record of each file.
								/*
								 * Files.move( Paths.get(u.listFiles.get(i).replaceAll(
								 * Pattern.quote(FileSystems.getDefault().getSeparator()), "/")),
								 * Paths.get("src/InvoiceError/" + u.getBillfileName(u.listFiles.get(i))),
								 * StandardCopyOption.REPLACE_EXISTING);
								 */
								// Get Stats
								FailUpload += 1;
								billRefNo = null;
								billRefNoReset = null;
							}
						} // else move to error
						else {

							LOGGER.log(Level.INFO,
									billRefNo + "-" + billRefNoReset + "::ERROR::Move from "
											+ u.listFiles.get(i).replaceAll(
													Pattern.quote(FileSystems.getDefault().getSeparator()), "/")
											+ " TO Error as It is not BILL FILE");
							/*
							 * Files.move( Paths.get(u.listFiles.get(i).replaceAll(
							 * Pattern.quote(FileSystems.getDefault().getSeparator()), "/")),
							 * Paths.get("src/InvoiceError/"+u.getBillfileName(u.listFiles.get(i))) ,
							 * StandardCopyOption.REPLACE_EXISTING);
							 */
							FailUpload += 1;
							billRefNo = null;
							billRefNoReset = null;
						}

					}

					// Print Out Number of File
					LOGGER.log(Level.INFO, "-------------------------------------------------------");
					LOGGER.log(Level.INFO, "--------Total Invoice Processed==> " + u.listFiles.size() + "; Success==> "
							+ SuccesfulUpload + "; Fail==> " + FailUpload);
					LOGGER.log(Level.INFO, "-------------------------------------------------------");

					LOGGER.log(Level.INFO, "PDFInvoicemove2docserver JOB is successfully completed");
				}

				// End Next Function

				// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistration") == true) {

					SimRegistration sr = new SimRegistration();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistration Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						sr.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6],
								line.split("::")[7], line.split("::")[8], line.split("::")[9], line.split("::")[10],
								line.split("::")[11], line.split("::")[12], line.split("::")[13], line.split("::")[14],
								line.split("::")[15], line.split("::")[16], line.split("::")[17]);
						Thread.sleep(1);

					}

					LOGGER.log(Level.INFO, "SimRegistration JOB is successfully completed");
				}

				// End Next Function

				// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistrationGenderOnly") == true) {

					SimRegistrationGenderOnly sr = new SimRegistrationGenderOnly();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistrationGenderOnly Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						try {
							sr.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1]);
							Thread.sleep(1);
						} catch (Exception e) {
							LOGGER.log(Level.SEVERE,
									"RESULT::FAIL SIMREGISTRATIN UPDATE : " + line + "==>" + e.getCause().toString());
						}
					}

					LOGGER.log(Level.INFO, "SimRegistrationGenderOnly JOB is successfully completed");
				}

				// End Next Function

				// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistrationDOBOnly") == true) {

					SimRegistrationDOBOnly sr = new SimRegistrationDOBOnly();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistrationDOBOnly Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						sr.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1]);
						Thread.sleep(1);

					}

					LOGGER.log(Level.INFO, "SimRegistrationDOBOnly JOB is successfully completed");
				}

				// End Next Function

				// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistrationLastnameFnameOnly") == true) {

					SimRegistrationLastnameFnameOnly sr = new SimRegistrationLastnameFnameOnly();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistrationLastnameFnameOnly Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						sr.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
						Thread.sleep(1);

					}

					LOGGER.log(Level.INFO, "SimRegistrationLastnameFnameOnly JOB is successfully completed");
				} // End Next Function
					// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistrationNationalIDOnly") == true) {

					SimRegistrationIDonAccount sr = new SimRegistrationIDonAccount();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistrationIDonAccount Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {

						sr.callingAccountInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2]);
						Thread.sleep(1);

					}

					LOGGER.log(Level.INFO, "SimRegistrationIDonAccount JOB is successfully completed");
				} // End Next Function

				// Calling function to do Sim Registration
				else if (args[0].equals("SimRegistrationAddressOnly") == true) {

					SimRegistrationAddressOnly sr = new SimRegistrationAddressOnly();
					sr.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "SimRegistrationIDonAccount Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						//sr.callingSubInfoUpdate(AccountNo, MSISDN, Add1, Add2, Add3, Add4, City);
						//Add1: house number; Add2: street(put all address here), address3:countrycode, address4:Postalcode
						sr.callingSubInfoUpdate(line.split("::")[0], line.split("::")[1], line.split("::")[2],
								line.split("::")[3], line.split("::")[4], line.split("::")[5], line.split("::")[6]);
						Thread.sleep(1);

					}

					LOGGER.log(Level.INFO, "SimRegistrationAddressOnly JOB is successfully completed");
				} // End Next Function

				// Calling function to do Address Creation
				else if (args[0].equals("AddressAssoc") == true) {

					SimRegistrationAddressAssocEnabled sraa = new SimRegistrationAddressAssocEnabled();
					sraa.retreiveCred(new File("src/config/soapconnection.cfg"));

					LOGGER.log(Level.INFO, "AddressAssoc Starts");
					BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" + args[1]));
					String line;
					while ((line = bfrdr.readLine()) != null) {
						sraa.callAddressAssoc(line.split("::")[0]);

					}

					LOGGER.log(Level.INFO, "AddressAssoc JOB is successfully completed");
				}

				// End Next Function
				// Calling function to Complete Order
				else if (args[0].equals("CompleteOrderManualNode") == true) {

					LOGGER.log(Level.INFO, "CompleteOrderManualNode Starts");
					// BufferedReader bfrdr = new BufferedReader(new FileReader("src/input/" +
					// args[1]));
					// String line;
					// while ((line = bfrdr.readLine()) != null) {
					// sraa.callAddressAssoc(line.split("::")[0]);

					// }

					OrderManualNodeCompletion onc = new OrderManualNodeCompletion();
					onc.CompleteOrder();

					LOGGER.log(Level.INFO, "CompleteOrderManualNode JOB is successfully completed");
				}

				// End Next Function

			} else {
				LOGGER.log(Level.SEVERE,
						"Usage: Arg[0] Arg[1] Arg[2] - Example: conerobotCalls SubscriberRetrieve batch file");
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Please ensure the file format which is provided is correct " + e.getMessage());
		}

	}

}
