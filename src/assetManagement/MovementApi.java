package assetManagement;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.util.JSON;

@Path("/manageMovement")
public class MovementApi {
	Connection con;
	DBCollection movement;
	BasicDBObject doc;

	private void initialize() {
		try {
			// Table with name "Assets" will be created (compared to SQL)
			con = new Connection();
			movement = con.getConnection().getCollection("Movement");
			doc = new BasicDBObject();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public MovementApi() {
		initialize();
	}

	@POST
	@Path("/addMovement")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addNewDonor(String data) {
		doc = (BasicDBObject) JSON.parse(data);
		movement.insert(doc);
		String docId = doc.get("_id").toString();
		System.out.println(docId);
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public String getMovementsList() {
		DBCursor dbCursor = movement.find();
		// System.out.print("dbcursor" + dbCursor.toArray());
		return dbCursor.toArray().toString();
	}

	@GET
	@Path("/byStatus/{status}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMovementByStatus(@PathParam("status") String status) {
		BasicDBObject ob = new BasicDBObject("status", status);
		DBCursor dbCursor = movement.find(ob);
		// System.out.print("dbcursor" + dbCursor.toArray());
		return dbCursor.toArray().toString();
	}
	
	@GET
	@Path("/byId/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getMovementById(@PathParam("id") String id){
		BasicDBObject ob = new BasicDBObject("_id", new ObjectId(id));
		DBCursor dbCursor = movement.find(ob);
		//System.out.print("dbcursor" + dbCursor.toArray());
		return dbCursor.toArray().toString();
	}
}
