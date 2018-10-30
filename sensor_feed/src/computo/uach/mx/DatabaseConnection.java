package computo.uach.mx;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import com.cloudant.client.api.ClientBuilder;
import com.cloudant.client.api.CloudantClient;
import com.cloudant.client.api.Database;
import com.google.gson.JsonObject;

public class DatabaseConnection {
	private Database db = null;
	private static final String databaseName = "sensors_db";
	public DatabaseConnection() {
		CloudantClient cloudant = createClient();
		if(cloudant!=null){
		 db = cloudant.database(databaseName, true);
		}
	}	
	public Database getDB(){
		return db;
	}
	private static CloudantClient createClient() {		
		String url;
		if (System.getenv("VCAP_SERVICES") != null) {
			// When running in Bluemix, the VCAP_SERVICES env var will have the credentials for all bound/connected services
			// Parse the VCAP JSON structure looking for cloudant.
			JsonObject cloudantCredentials = APHelper.getCloudCredentials("cloudant");
			if(cloudantCredentials == null){
				System.out.println("No cloudant database service bound to this application");
				return null;
			}
			url = cloudantCredentials.get("url").getAsString();
		} else {
			System.out.println("Running locally. Looking for credentials in cloudant.properties");
			url = APHelper.getLocalProperties("cloudant.properties").getProperty("cloudant_url");
			if(url == null || url.length()==0){
				System.out.println("To use a database, set the Cloudant url in src/main/resources/cloudant.properties");
				return null;
			}
		}
		try {
			System.out.println("Connecting to Cloudant");
			CloudantClient client = ClientBuilder.url(new URL(url)).build();
			return client;
		} catch (Exception e) {
			System.out.println("Unable to connect to database");
			//e.printStackTrace();
			return null;
		}
	}	
	public Collection<Sensor> getAll(){
        List<Sensor> docs;
		try {
			docs = db.getAllDocsRequestBuilder().includeDocs(true).build().getResponse().getDocsAs(Sensor.class);
		} catch (IOException e) {
			return null;
		}
        return docs;
	}	
	public Sensor get(String id) {
		return db.find(Sensor.class, id);
	}	
	public Sensor persist(Sensor td) {
		String id = db.save(td).getId();
		return db.find(Sensor.class, id);
	}	
	public Sensor update(String id, Sensor newSensor) {
		Sensor sensor = db.find(Sensor.class, id);
		sensor.setName(newSensor.getName());
		db.update(sensor);
		return db.find(Sensor.class, id);		
	}	
	public void delete(String id) {
		Sensor sensor = db.find(Sensor.class, id);
		db.remove(id, sensor.get_rev());	
	}	
	public int count() throws Exception {
		return getAll().size();
	}	
}
