package computo.uach.mx;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/**
 * Servlet implementation class SensorFeedServlet
 */
@WebServlet("/")
public class SensorFeedServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DatabaseConnection db_connection;
	private String [] sensor_names = {"None","Temp","Light","Vibration","Weight","Proximity", "Force"};
	private String [] sensor_sources = {"None","PHIDGET","MOTE","OTHER"};
	private String token = "fu0AZbxH4Ig:APA91bEIP0kAvwAB-Hch8a0PctVIM8OMwa48RsENORBXfDEhOWHFkpdX6YEhcX2scsr7P4jxgr_Ighw7ql6HIdqyc2bkjmp0vx73_aD84Sx89V1s4eMgFEMPautCcQOc1gfNOTWLOj8n";
	private static final String HttpPostURL = "https://fcm.googleapis.com/fcm/send";
	private static final String AutorizationKey = "key=AIzaSyB9s85hSIaA-CcUHpHtRw3LmSGYAIVjwSA";
    /**
     * Default constructor. 
     */
    public SensorFeedServlet() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		db_connection = new DatabaseConnection();
		String param1 = "sensor";
        String sensorType = request.getParameter(param1);       
        String param2 = "value";
        String sensorValue = request.getParameter(param2);
        String param3 = "source";
        String sensorSource = request.getParameter(param3);
        try {
        	response.setContentType("application/json");
        	if (!"".equals(sensorType) && sensorType!=null){        		
        		Sensor newSensor = setDetails(Integer.parseInt(sensorType),Integer.parseInt(sensorSource), sensorValue );
        		SendNotification(newSensor);
        		response.getWriter().write(getSensors(newSensor.get_id()));
        	}else{
        		response.getWriter().write(getSensors(""));
        	}        	
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private Sensor setDetails(int sensorId, int sensorSource, String sensorValue) {
    	Sensor sensor = null;
    	if (!"".equals(sensorValue) && sensorValue!=null){
    		sensor = new Sensor(sensor_names[sensorId], sensor_sources[sensorSource], sensorId, sensorValue);    	
    		return db_connection.persist(sensor);
    	}else{
    		sensor = new Sensor(sensor_names[sensorId], sensor_sources[sensorSource], sensorId);    		
    	}
    	return db_connection.persist(sensor);
    }
    private String getSensors(String Id){
    	if (db_connection == null) {
			return "[]";
		}

		List<String> names = new ArrayList<String>();
		if(Id.equals("") || Id == null){
			for (Sensor doc : db_connection.getAll()) {
				String name = doc.getName();
				String value = doc.getValue();
				String source = doc.getSource();
				String time = doc.getTime().toString();
				if (name != null){
					names.add(source+", "+name+", "+value+", "+time);
				}
			}
		}else{
			Sensor doc = db_connection.get(Id);
			String name = doc.getName();
			String value = doc.getValue();
			String time = doc.getTime().toString();
			String source = doc.getSource();
			if (name != null){
				names.add(source+", "+name+", "+value+", "+time);
			}
		}
		if (names.isEmpty()){
			names.add("Empty Data Base.");
		}
		return new Gson().toJson(names);
    }
    private void SendNotification(Sensor sensor){
    	
    	DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost postRequest = new HttpPost(HttpPostURL);
        // we already created this model class.
        // we will convert this model class to json object using google gson library.
        NotificationRequestModel notificationRequestModel = new NotificationRequestModel();
        NotificationData notificationData = new NotificationData();
        notificationData.setDetail("Value: "+sensor.getValue());
        notificationData.setTitle(sensor.getSource()+" SENSOR "+sensor.getName());
        notificationRequestModel.setData(notificationData);
        notificationRequestModel.setTo(token);
        Gson gson = new Gson();
        Type type = new TypeToken<NotificationRequestModel>() {
        }.getType();
        String json = gson.toJson(notificationRequestModel, type);
        StringEntity input = null;
		try {
			input = new StringEntity(json);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        input.setContentType("application/json");
        // server key of your firebase project goes here in header field.
        // You can get it from firebase console.
        postRequest.addHeader("Authorization", AutorizationKey);
        postRequest.setEntity(input);
        System.out.println("reques:" + json);
        HttpResponse response = null;
		try {
			response = httpClient.execute(postRequest);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (response.getStatusLine().getStatusCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
        } else if (response.getStatusLine().getStatusCode() == 200) {
            try {
				System.out.println("response:" + EntityUtils.toString(response.getEntity()));
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}           
        }
    }

}
