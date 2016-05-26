package assetManagement;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Connection {

	MongoClient mongoClient;

	public DB getConnection() {
		// To connect to mongodb servers
		mongoClient = new MongoClient("localhost", 27017);

		// Connect to Database
		DB db = mongoClient.getDB("assetManagement");
		System.out.println("Connected to database successfully");
		return db;
	}

	public void closeConnection() {
		mongoClient.close();
	}
}
