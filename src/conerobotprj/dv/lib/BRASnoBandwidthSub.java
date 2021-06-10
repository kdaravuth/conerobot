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
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class BRASnoBandwidthSub {

	public static void formatBrasFile(String siteToRun, String fileName)
			throws IOException, ClassNotFoundException, SQLException {

		// Database connection
		RobotUtilities r = new RobotUtilities();
		Connection con = r.dbconnection("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@10.6.1.203:1521:brs",
				"brssystem", "btcchamp");
		PreparedStatement ps = con.prepareStatement("insert into BRAS_CONVERTED_OUTPUT values(?,?,?,?,?,?,?)");

		Connection conPCRF = r.dbconnection("oracle.jdbc.driver.OracleDriver",
				"jdbc:oracle:thin:@10.1.37.8:1521:cust1_1", "BTC_PCRF", "Pcrf2020");
		PreparedStatement psPCRF = conPCRF
				.prepareStatement("insert into BTC_PCRF.BRAS_CONVERTED_OUTPUT values(?,?,?,?,?,?,?)");

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Date date = new Date();
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd_hhmmss");

		BufferedReader bfrdr;
		List<String> brasSub = new ArrayList<String>();
		String tempSubs = "";

		try {
			bfrdr = new BufferedReader(new FileReader("src/input/" + fileName));
			String line;
			while ((line = bfrdr.readLine()) != null) {

				if (findIFStartingPoint(line.toString(), "interface GigabitEthernet")) {
					tempSubs = line.toString().replaceAll("[\\t\\n\\r]+", " ");

				}

				if (findIFStartingPoint(line.toString(), "description ")) {
					tempSubs = tempSubs + "::" + line.toString().replaceAll("[\\t\\n\\r]+", " ");
				}

				if (findIFStartingPoint(line.toString(), " qinq termination pe-vid")) {
					tempSubs = tempSubs + "::" + line.toString().replaceAll("[\\t\\n\\r]+", " ");
					// brasSub.add(tempSubs + "---END One SUB---");
					// tempSubs = "";
				}
				// qos-profile

				if (findIFStartingPoint(line.toString(), "qos-profile")) {
					tempSubs = tempSubs + "::" + line.toString().replaceAll("[\\t\\n\\r]+", " ");
					brasSub.add(tempSubs + "---END One SUB---");
					tempSubs = "";
				}

			}
			FileWriter output = new FileWriter(
					"src/output/BRASNOBANDWIDTH_" + siteToRun + "_OP_" + ft.format(date) + ".out", true);

			for (int i = 0; i < brasSub.size(); i++) {
				// System.out.println(brasSub.get(i).toString());
				// check if sub has user, vlan, bandwidth, interface
				// if no any enough info, sub is considered to be inactive
				// no need to show
				if (brasSub.get(i).split("::").length >= 4) {
					// This is to check phone number cut only the maximum phone number
					if (brasSub.get(i).split("::")[1].replaceAll("\\D", "").length() > 10) {
					
						System.out
								.println("NOBTCBANDWIDTH::"
										+ getCleanExternalID(brasSub.get(i).split("::")[1]
												.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description")))
										+ "::" + convertBBUserID(siteToRun, brasSub.get(i).toString()) + "::"
										+ brasSub.get(i).split("::")[3]
												.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile"))
												.split(" ")[1]
										+ "::" + brasSub.get(i).split("::")[0].replaceAll(" ", "") + "::"
										+ brasSub.get(i).split("::")[1].replaceAll("[0-9]", "")
												.replace("description", "").replace("*", "").replace("  ", " ")
												.replace("---- More ----[D                     [D", ""));

						String tout = "NOBTCBANDWIDTH::"
								+ getCleanExternalID(brasSub.get(i).split("::")[1]
										.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description")))
								+ "::" + convertBBUserID(siteToRun, brasSub.get(i).toString()) + "::"
								+ brasSub.get(i).split("::")[3]
										.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile"))
										.split(" ")[1]
								+ "::" + brasSub.get(i).split("::")[0].replaceAll(" ", "") + "::"
								+ brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", "");

						// Write to output
						output.append(tout + "\n");
						// if externalID size is not 10 then just show all

						// Write to BRS database table: BRAS_CONVERTED_OUTPUT

						ps.setString(1, "NOBTCBANDWIDTH");
						ps.setString(2, getCleanExternalID(brasSub.get(i).split("::")[1]
								.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description"))));
						ps.setString(3, convertBBUserID(siteToRun, brasSub.get(i).toString()));
						ps.setString(4, brasSub.get(i).split("::")[3]
								.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile")).split(" ")[1]);
						ps.setString(5, brasSub.get(i).split("::")[0].replaceAll(" ", ""));
						ps.setString(6,
								brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", ""));
						ps.setTimestamp(7, timestamp);
						ps.execute();

						// Write to Cust For PCRF reconciliation
						psPCRF.setString(1, "NOBTCBANDWIDTH");
						psPCRF.setString(2, getCleanExternalID(brasSub.get(i).split("::")[1]
								.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description"))));
						psPCRF.setString(3, convertBBUserID(siteToRun, brasSub.get(i).toString()));
						psPCRF.setString(4, brasSub.get(i).split("::")[3]
								.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile")).split(" ")[1]);
						psPCRF.setString(5, brasSub.get(i).split("::")[0].replaceAll(" ", ""));
						psPCRF.setString(6,
								brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", ""));
						psPCRF.setTimestamp(7, timestamp);
						psPCRF.execute();

					} else {
						System.out.println("NOBTCBANDWIDTH::"
								+ getCleanExternalID(brasSub.get(i).split("::")[1]
										.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description")))
								+ "::" + convertBBUserID(siteToRun, brasSub.get(i).toString()) + "::"
								+ brasSub.get(i).split("::")[3]
										.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile"))
										.split(" ")[1]
								+ "::"
								+ brasSub.get(i).split("::")[0].replace(
										"---- More ----[42D                                          [42", "")
								+ "::"
								+ brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", ""));

						String tout = "NOBTCBANDWIDTH::"
								+ getCleanExternalID(brasSub.get(i).split("::")[1]
										.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description")))
								+ "::" + convertBBUserID(siteToRun, brasSub.get(i).toString()) + "::"
								+ brasSub.get(i).split("::")[3]
										.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile"))
										.split(" ")[1]
								+ "::"
								+ brasSub.get(i).split("::")[0].replace(
										"---- More ----[42D                                          [42", "")
								+ "::"
								+ brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", "");
						// Write to output
						output.append(tout + "\n");

						// Write to BRS database table: BRAS_CONVERTED_OUTPUT
						// PreparedStatement ps = con.prepareStatement("insert into
						// BRAS_CONVERTED_OUTPUT values(?,?,?,?,?,?,?)");
						ps.setString(1, "NOBTCBANDWIDTH");
						ps.setString(2, getCleanExternalID(brasSub.get(i).split("::")[1]
								.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description"))));
						ps.setString(3, convertBBUserID(siteToRun, brasSub.get(i).toString()));
						ps.setString(4, brasSub.get(i).split("::")[3]
								.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile")).split(" ")[1]);
						ps.setString(5, brasSub.get(i).split("::")[0]
								.replace("---- More ----[42D                                          [42", ""));
						ps.setString(6,
								brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", ""));
						ps.setTimestamp(7, timestamp);
						ps.execute();

						// Write to Cust For PCRF reconciliation
						psPCRF.setString(1, "NOBTCBANDWIDTH");
						psPCRF.setString(2, getCleanExternalID(brasSub.get(i).split("::")[1]
								.substring(getSubstrloc(brasSub.get(i).split("::")[1], "description"))));
						psPCRF.setString(3, convertBBUserID(siteToRun, brasSub.get(i).toString()));
						psPCRF.setString(4, brasSub.get(i).split("::")[3]
								.substring(getSubstrloc(brasSub.get(i).split("::")[3], "qos-profile")).split(" ")[1]);
						psPCRF.setString(5, brasSub.get(i).split("::")[0]
								.replace("---- More ----[42D                                          [42", ""));
						psPCRF.setString(6,
								brasSub.get(i).split("::")[1].replaceAll("[0-9]", "").replace("description", "")
										.replace("*", "").replace("  ", " ")
										.replace("---- More ----[D                     [D", ""));
						psPCRF.setTimestamp(7, timestamp);
						psPCRF.execute();

					}
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

	public static int getSubstrloc(String input, String findStr) {

		if (input.indexOf(findStr) == -1) {
			return 0;
		} else {
			return 1;
		}

	}

	public static String getCleanExternalID(String messExternalID) {
		String cleanExt = "";
		for (int i = 0; i < messExternalID.length(); i++) {

			if (isNumeric(Character.toString(messExternalID.charAt(i)))) {

				cleanExt += Character.toString(messExternalID.charAt(i));

			} else {
				if (cleanExt.length() != 0) {
					break;
				}

			}
		}

		return cleanExt;
	}

	public static boolean isNumeric(String strNum) {
		if (strNum == null) {
			return false;
		}
		try {
			double d = Double.parseDouble(strNum);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static String convertBBUserID(String GABOrFT, String brasSub) {

		String hostName = "";
		String BBuserID = "";

		if (brasSub.split("::").length >= 3) {
			if (brasSub.split("::")[2].split(" ").length >= 4) {
				if (GABOrFT.equalsIgnoreCase("GAB")) {
					hostName = "MSU-BRAS-ME60-01-";
					String slotNumber = brasSub.split("::")[0].split("/")[0].replaceAll("\\D+", "");
					String cardNumber = brasSub.split("::")[0].split("/")[1];
					String portNumber = brasSub.split("::")[0].split("/")[2].split("\\.")[0];
					String[] tempVlan = brasSub.split("::")[2].replaceAll("^\\D+", "").split("\\D+");
					String innervlan = tempVlan[0];
					String Outervlan = tempVlan[1];
//Host name + - + slot number (2 digits) + card number (1 digit)+ port number (2 digit) + outer VLAN ID (4 digits) + 0 + inner VLAN ID (4 digits)
					// BBuserID = BBuserID + hostName + String.format("%02d", slotNumber) +
					// cardNumber
					// + String.format("%02d", portNumber) + String.format("%04d", Outervlan) + "0"
					// + String.format("%04d", innervlan);

					BBuserID = BBuserID + hostName + StringUtils.leftPad(slotNumber, 2, "0") + cardNumber
							+ StringUtils.leftPad(portNumber, 2, "0") + StringUtils.leftPad(innervlan, 4, "0") + "0"
							+ StringUtils.leftPad(Outervlan, 4, "0");
				} else {
					// FTN-BRAS-ME60
					hostName = "FTN-BRAS-ME60-";
					String slotNumber = brasSub.split("::")[0].split("/")[0].replaceAll("\\D+", "");
					String cardNumber = brasSub.split("::")[0].split("/")[1];
					String portNumber = brasSub.split("::")[0].split("/")[2].split("\\.")[0];
					String[] tempVlan = brasSub.split("::")[2].replaceAll("^\\D+", "").split("\\D+");
					String innervlan = tempVlan[0];
					String Outervlan = tempVlan[1];
//Host name + - + slot number (2 digits) + card number (1 digit)+ port number (2 digit) + outer VLAN ID (4 digits) + 0 + inner VLAN ID (4 digits)
					// BBuserID = BBuserID + hostName + String.format("%02d", slotNumber) +
					// cardNumber
					// + String.format("%02d", portNumber) + String.format("%04d", Outervlan) + "0"
					// + String.format("%04d", innervlan);
					BBuserID = BBuserID + hostName + StringUtils.leftPad(slotNumber, 2, "0") + cardNumber
							+ StringUtils.leftPad(portNumber, 2, "0") + StringUtils.leftPad(innervlan, 4, "0") + "0"
							+ StringUtils.leftPad(Outervlan, 4, "0");

				}
			}
		}

		return BBuserID;

	}

	private static boolean findIFStartingPoint(String readString, String TBFoundString) {
		if (readString.contains(TBFoundString)) {
			return true;
		}
		return false;
	}
}
