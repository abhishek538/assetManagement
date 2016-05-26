package assetManagement;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.bson.types.ObjectId;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import assetManagement.GenerateQR;

@Path("/manageEquipment")
public class EquipmentApi {

	Connection con;
	DBCollection assets;
	BasicDBObject doc;
	GenerateQR qrCode;

	private void initialize() {
		try {
			// Table with name "Assets" will be created (compared to SQL)
			con = new Connection();
			assets = con.getConnection().getCollection("Assets");
			doc = new BasicDBObject();
			qrCode = new GenerateQR();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		}
	}

	public EquipmentApi() {
		initialize();
	}

	@GET
	@Path("/countByTypeAndLocation/{location}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getCountByTypeAndLocation(@PathParam("location") String location) {
		DBObject match = new BasicDBObject("$match", new BasicDBObject("Product_Location", location));
		DBObject groupFields = new BasicDBObject("_id", "$Product_type");
		groupFields.put("count", new BasicDBObject("$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		DBObject sortFields = new BasicDBObject("count", -1);
		DBObject sort = new BasicDBObject("$sort", sortFields);

		AggregationOutput output = assets.aggregate(match, group, sort);
		System.out.println("output:" + output.results());
		return output.results().toString();
	}

	@POST
	@Path("/addEquipment")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addNewEquipment(String data) {

		doc = (BasicDBObject) JSON.parse(data);
		assets.insert(doc);
		String docId = doc.get("_id").toString();
		qrCode.generateQRCode(docId);
		String filePath = "C:\\Users\\i329414\\workspace\\assetManagement\\WebContent\\qrCodes\\" + docId + ".png";
		File image = new File(filePath);
		System.out.println(docId);
		return docId;
	}

	@GET
	@Path("/details/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String getEquipmentId(@PathParam("id") String id) {
		BasicDBObject ob = new BasicDBObject("_id", new ObjectId(id));
		System.out.println("Abhishek:" + id);
		DBCursor dbCursor = assets.find(ob);
		// System.out.print("dbcursor" + dbCursor.toArray());
		return dbCursor.toArray().toString();
	}

	@GET
	@Path("/byLocation")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAssetCountbyLocation() {
		// DBObject match = new BasicDBObject("$match", new
		// BasicDBObject("product_location", location));
		DBObject groupFields = new BasicDBObject("_id", "$Product_Location");
		groupFields.put("count", new BasicDBObject("$sum", 1));
		DBObject group = new BasicDBObject("$group", groupFields);
		DBObject sortFields = new BasicDBObject("count", -1);
		DBObject sort = new BasicDBObject("$sort", sortFields);

		AggregationOutput output = assets.aggregate(group, sort);
		System.out.println("output:" + output.results());
		return output.results().toString();
	}

	@GET
	@Path("/getImage/{imageName}")
	@Produces("image/png")
	public Response getImage(@PathParam("imageName") String imageName) {
		String filePath = "C:\\Users\\i329414\\workspace\\assetManagement\\WebContent\\qrCodes\\" + imageName + ".png";
		File image = new File(filePath);
		ResponseBuilder response = Response.ok((Object) image);
		response.header("Content-Disposition", "attachment; filename=" + imageName + ".png");
		return response.build();
	}
}
