package computo.uach.mx;

import java.sql.Timestamp;
import java.util.Random;
/**
 * Represents a Sensor document stored in Cloudant.
 */
public class Sensor {
	private String _id;
	private String _rev;
	private String name = null;
	private int sensor_id = 0;
	private String value = null;
	private Timestamp time = null;
	public Sensor(String name, int sensor_id) {
		this.name = name;
		this.sensor_id = sensor_id;
		setTime(new Timestamp(System.currentTimeMillis()));
		Random r = new Random();
		this.value = r.doubles(0.000000000000000, 1.000000000000000).findFirst().getAsDouble()+"";
	}
	public Sensor(String name, int sensor_id, String value) {
		this.name = name;
		this.setSensorId(sensor_id);
		this.setValue(value);
		setTime(new Timestamp(System.currentTimeMillis()));		
		this.value = value;
	}

	/**
	 * Gets the ID.
	 * 
	 * @return The ID.
	 */
	public String get_id() {
		return _id;
	}

	/**
	 * Sets the ID
	 * 
	 * @param _id
	 *            The ID to set.
	 */
	public void set_id(String _id) {
		this._id = _id;
	}

	/**
	 * Gets the revision of the document.
	 * 
	 * @return The revision of the document.
	 */
	public String get_rev() {
		return _rev;
	}

	/**
	 * Sets the revision.
	 * 
	 * @param _rev
	 *            The revision to set.
	 */
	public void set_rev(String _rev) {
		this._rev = _rev;
	}
	
	/**
	 * Gets the visitorName of the document.
	 * 
	 * @return The name of the document.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name
	 * 
	 * @param name
	 *            The visitorName to set.
	 */
	public void setName(String visitorName) {
		this.name = visitorName;
	}
	public int getSensorId() {
		return sensor_id;
	}
	public void setSensorId(int sensor_id) {
		this.sensor_id = sensor_id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Timestamp getTime() {
		return time;
	}
	public void setTime(Timestamp time) {
		this.time = time;
	}
}