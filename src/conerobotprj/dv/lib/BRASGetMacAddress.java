package conerobotprj.dv.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BRASGetMacAddress {

	public static void formatBrasFile(String siteToRun, String fileName)
			throws IOException, ClassNotFoundException, SQLException {

		// Database connection
		RobotUtilities r = new RobotUtilities();

		Connection con = r.dbconnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@10.6.1.203:1521:brs",
				"brssystem", "btcchamp");
		PreparedStatement ps = con.prepareStatement("insert into BRAS_GET_MACADDRESS values(?,?,?, sysdate)");

		Connection conPCRF = r.dbconnection("oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@10.1.37.8:1521:cust1_1", "BTC_PCRF", "Pcrf2020");
		PreparedStatement psPCRF = conPCRF.prepareStatement("insert into BTC_PCRF.BRAS_GET_MACADDRESS values(?,?,?, sysdate)");

		//Clean up BRAS_GET_MACADDRESS tables
		//PreparedStatement psClean = con.prepareStatement("truncate table BRAS_GET_MACADDRESS");
		//PreparedStatement psCleanPCRF = conPCRF.prepareStatement("truncate table BRAS_GET_MACADDRESS");
		//psClean.execute();
		//psCleanPCRF.execute();
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd_hhmmss");

		BufferedReader bfrdr;
		List<String> brasSub = new ArrayList<String>();
		String tempSubs = "";

		try {
			bfrdr = new BufferedReader(new FileReader("src/input/" + fileName));
			// String siteToRun = "FTN";
			String line;
			while ((line = bfrdr.readLine()) != null) {

				line = line.replaceAll(" ----\\[42D                                          \\[42D  ", " ");
				if (findIFStartingPoint(line.toString(), "...  GE")) {
					tempSubs = line.toString().replaceAll("[\\t\\n\\r]+", " ").replaceAll("  ---- More", "")
							.replaceAll("\\s+", "::");

				}

				if (findIFStartingPoint(line.toString(), "IPOE")) {
					tempSubs = tempSubs + "::" + line.toString().replaceAll("[\\t\\n\\r]+", " ")
							.replaceAll("  ---- More", "").replaceAll("\\s+", "::");
					brasSub.add(tempSubs + "---END One SUB---");
					tempSubs = "";
				}

			}
			 FileWriter output = new FileWriter("src/output/" + siteToRun + "_OPMACADDRESS_" +
			 ft.format(date) + ".out", true);

			for (int i = 0; i < brasSub.size(); i++) {
				// System.out.println(brasSub.get(i).toString());
				// check if sub has user, vlan, bandwidth, interface
				// if no any enough info, sub is considered to be inactive
				// no need to show
				if (brasSub.get(i).split("::").length >= 0) {
					
						// System.out.println(brasSub.get(i));

						System.out.println("BTCBANDWIDTH::" + convertBBUserID(siteToRun, brasSub.get(i).toString())
								+ "::" + brasSub.get(i).split("::")[3]+ "::"
								+ ConvertMacAddress2CorrectFormat(brasSub.get(i).split("::")[5]));

						
						 String tout = "BTCBANDWIDTH::" + convertBBUserID(siteToRun, brasSub.get(i).toString())
								+ "::" + brasSub.get(i).split("::")[3]+ "::"
								+ ConvertMacAddress2CorrectFormat(brasSub.get(i).split("::")[5]);
						 					 
						 // Write to output
						 output.append(tout + "\n");
						 
						// Write to BRS database table: BRAS_GET_MACADDRESS
					
						 ps.setString(1,convertBBUserID(siteToRun, brasSub.get(i).toString())); 
						 ps.setString(2,ConvertMacAddress2CorrectFormat(brasSub.get(i).split("::")[5]));
						 ps.setString(3,brasSub.get(i).split("::")[3]);
						 ps.execute();
						  
						 //// Write to Cust For PCRF reconciliation
						 psPCRF.setString(1,convertBBUserID(siteToRun, brasSub.get(i).toString())); 
						 psPCRF.setString(2,ConvertMacAddress2CorrectFormat(brasSub.get(i).split("::")[5]));
						 psPCRF.setString(3,brasSub.get(i).split("::")[3]);
						 psPCRF.execute();
				}
			}
			output.close();
			con.close();
			conPCRF.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			con.close();
			conPCRF.close();
		}

	}

	public static String convertBBUserID(String GABOrFT, String brasSub) {

		String hostName = "";
		String BBuserID = "";

		if (brasSub.split("::").length >= 0) { //this means nothing but just the check from other BRAS check
			if (brasSub.split("::")[2].split(" ").length >= 0) { //this means nothing but just the check from other BRAS check
				if (GABOrFT.equalsIgnoreCase("GAB")) {
					hostName = "MSU-BRAS-ME60-01-";
					String slotNumber = brasSub.split("::")[3].split("/")[0].replaceAll("\\D+", "");
					String cardNumber = brasSub.split("::")[3].split("/")[1];
					String portNumber = brasSub.split("::")[3].split("/")[2].split("\\.")[0];
					String[] tempVlan = brasSub.split("::")[7].replaceAll("^\\D+", "").split("\\D+");
					String innervlan = tempVlan[1];
					String Outervlan = tempVlan[0];
//Host name + - + slot number (2 digits) + card number (1 digit)+ port number (2 digit) + outer VLAN ID (4 digits) + 0 + inner VLAN ID (4 digits)
					// BBuserID = BBuserID + hostName + String.format("%02d", slotNumber) +
					// cardNumber
					// + String.format("%02d", portNumber) + String.format("%04d", Outervlan) + "0"
					// + String.format("%04d", innervlan);

					BBuserID = BBuserID + hostName + StringUtils.leftPad(slotNumber, 2, "0") + cardNumber
							+ StringUtils.leftPad(portNumber, 2, "0") + StringUtils.leftPad(Outervlan, 4, "0") + "0"
							+ StringUtils.leftPad(innervlan, 4, "0");
				} else {
					// FTN-BRAS-ME60
					hostName = "FTN-BRAS-ME60-";
					String slotNumber = brasSub.split("::")[3].split("/")[0].replaceAll("\\D+", "");
					String cardNumber = brasSub.split("::")[3].split("/")[1];
					String portNumber = brasSub.split("::")[3].split("/")[2].split("\\.")[0];
					String[] tempVlan = brasSub.split("::")[7].replaceAll("^\\D+", "").split("\\D+");
					String innervlan = tempVlan[1];
					String Outervlan = tempVlan[0];
//Host name + - + slot number (2 digits) + card number (1 digit)+ port number (2 digit) + outer VLAN ID (4 digits) + 0 + inner VLAN ID (4 digits)
					// BBuserID = BBuserID + hostName + String.format("%02d", slotNumber) +
					// cardNumber
					// + String.format("%02d", portNumber) + String.format("%04d", Outervlan) + "0"
					// + String.format("%04d", innervlan);
					BBuserID = BBuserID + hostName + StringUtils.leftPad(slotNumber, 2, "0") + cardNumber
							+ StringUtils.leftPad(portNumber, 2, "0") + StringUtils.leftPad(Outervlan, 4, "0") + "0"
							+ StringUtils.leftPad(innervlan, 4, "0");

				}
			}
		}

		// System.out.println("BBuserID: " + BBuserID);
		return BBuserID;

	}

	private static boolean findIFStartingPoint(String readString, String TBFoundString) {
		if (readString.contains(TBFoundString)) {
			return true;
		}
		return false;
	}

	private static String ConvertMacAddress2CorrectFormat(String rawMacAddress) {

		rawMacAddress = rawMacAddress.replaceAll("-", "");
		String tempStr = "";
		int semicolonCount = 0;

		/// System.out.println(rawMacAddress.length());
		for (int i = 0; i < rawMacAddress.length(); i++) {
			// System.out.println(i);
			if ((i + 1) % 2 == 0 && semicolonCount <5) {
				tempStr += rawMacAddress.charAt(i) + ":";
				semicolonCount += 1;
				//System.out.println(tempStr);
			} else {

				tempStr += rawMacAddress.charAt(i);
				//System.out.println(tempStr);


			}

		}
		rawMacAddress = tempStr;
		return rawMacAddress.toUpperCase();

	}
}
