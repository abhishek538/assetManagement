package assetManagement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

@Path("/manageDonor")
public class DonorApi {
	Connection con;
	DBCollection donor;
	BasicDBObject doc;

	private void initialize() {
		try {
			// Table with name "Assets" will be created (compared to SQL)
			con = new Connection();
			donor = con.getConnection().getCollection("Donor");
			doc = new BasicDBObject();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public DonorApi() {
		initialize();
	}

	@POST
	@Path("/addDonor")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewDonor(String data) {
		doc = (BasicDBObject) JSON.parse(data);
		donor.insert(doc);
		String docId = doc.get("_id").toString();
		System.out.println(docId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getDonorsList() {
		DBCursor dbCursor = donor.find();
		//System.out.print("dbcursor" + dbCursor.toArray());
		return dbCursor.toArray().toString();
	}
}
