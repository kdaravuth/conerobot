/**
 * This is the generic utility to use in conerobot application
 */
package conerobotprj.dv.lib;

import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * @author khfighter
 *
 */
public class RobotUtilities {

	// Manapulating xml data
	public static Document convertStringToXMLDocument(String xmlString) {
		// Parser that produces DOM object trees from XML content
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		// API to obtain DOM Document instance
		DocumentBuilder builder = null;
		try {
			// Create DocumentBuilder with default configuration
			builder = factory.newDocumentBuilder();

			// Parse the content to Document object
			Document doc = builder.parse(new InputSource(new StringReader(xmlString)));
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// End Manapulating xml data
// Convert from Document to String
	public String ConvertXMLDocumentToString(Document xmlDoc) {
		try {
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(xmlDoc);

			transformer.transform(source, result);
			String xmlString = sw.toString();
			return xmlString;
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	// Oracle db connect
	public Connection dbconnection(String Driver, String ConnectionString, String dbUser, String dbPassword) throws ClassNotFoundException, SQLException {
		// step1 load the driver class
				//	Class.forName("oracle.jdbc.driver.OracleDriver");
		Class.forName(Driver);

		// step2 create the connection object
		//Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.6.1.203:1521:brs", "brssystem","btcchamp");
		Connection con = DriverManager.getConnection(ConnectionString, dbUser,dbPassword);
		
		return con;
	}
}
