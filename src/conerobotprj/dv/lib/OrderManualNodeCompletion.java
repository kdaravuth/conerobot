package conerobotprj.dv.lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.xml.registry.infomodel.User;

import com.workpoint.client.ClientContext;
import com.workpoint.client.Job;
import com.workpoint.client.WorkItemEntry;
import com.workpoint.common.data.ActivityData;
import com.workpoint.common.data.ActivityInstData;
import com.workpoint.common.data.JobData;
import com.workpoint.common.data.JobNodeData;
import com.workpoint.common.data.TableDataList;
import com.workpoint.common.data.UserDataArray;
import com.workpoint.common.data.UserDataInfo;
import com.workpoint.common.data.UserDataList;
import com.workpoint.common.data.UserDataParent;
import com.workpoint.common.data.UserDataXMLWrapper;
import com.workpoint.common.data.WorkItemData;
import com.workpoint.common.data.table.ActivityTable;
import com.workpoint.common.data.table.ActivityUserDataTable;
import com.workpoint.common.data.table.JobUserDataTable;
import com.workpoint.common.data.table.TableData;
import com.workpoint.common.data.table.UserData;
import com.workpoint.common.data.table.UserDataTable;
import com.workpoint.common.script.SymbolData;
import com.workpoint.common.script.SymbolTable;
import com.workpoint.common.util.TableID;

public class OrderManualNodeCompletion {
	public static void CompleteOrder() {

		Hashtable<String, String> hashParam = new Hashtable();
		hashParam.put("client.connect", "XML");
		hashParam.put("client.connect.URL", "http://10.128.203.18:8051/wp/wpClientServlet");
		ClientContext context = ClientContext.createContext(hashParam, true);

		try {
			context.open("WPDS", "wfadmin", "Wfadmin123!");

			// List workList = WorkItemEntry.getWorkList(context, (short) 0, null, null,
			// null);

			List<Job> L = Job.getList(context, " PROCI_REF='41330884043'", "", null);

			if (L != null && !L.isEmpty()) {
				Job j = L.get(0);
				String sfilter = "WP_WORK_ITEM.proci_id = " + j.getJobID().getID() + "";

				Object workList = WorkItemEntry.getWorkList(context, (short) 0, null, sfilter, null);

				for (Iterator it1 = ((ArrayList) workList).iterator(); it1.hasNext();) {
					WorkItemEntry item = (WorkItemEntry) it1.next();

					System.out.println("Job Name= " + item.getJobName());
					System.out.println("Job ID= " + item.getJobID());
					System.out.println("ServiceOrderID= " + item.getJobReference());
					System.out.println("Job Start Date= " + item.getJobStartDate());
					System.out.println("Job Due Date= " + item.getJobDueDate());
					System.out.println("Activity= " + item.getName());
					System.out.println("Activity= " + item.getWorkStateString());
					System.out.println("Activity= " + item.getAppReference());

					// item.open();
					// item.complete(0.0f);

					// UserData k = (UserData) item.readNodeBulkUserDataList().get(0);
					// System.out.println(k.getVariableName() +"= "+ k.getVariableValue());

					// Get error

					System.out.println("--------------------------------------");
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
