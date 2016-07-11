package db;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.google.android.gms.maps.model.LatLng;
public class NoteLocation implements Parcelable
{
	public LatLng geoPoint;
	public int altitude;
	private Date timeStamp;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private long time;
	public long getProjectID() { return projectID; }
	public long getTime() { return time; }
	private long projectID;
	private String provider;
  //xxx
	public String getGeo(){
		StringBuilder sb = new StringBuilder(100);
		sb.append("Latitude: ");
		sb.append(geoPoint.latitude);
		sb.append(" , ");
		sb.append(" Longitude: ");
		sb.append(geoPoint.longitude );
		sb.append(" , ");
		sb.append(" Altitude: ");
		sb.append(altitude);
		return sb.toString();
	}
public NoteLocation(double latitude,double longitude, int altitude, String provider, Project project)
{
	this.geoPoint = new LatLng(latitude, longitude);
	this.altitude = altitude;
	this.timeStamp = new Date();
	this.provider = provider;
	this.time = timeStamp.getTime();
	this.projectID = project.getProjectID();
}
NoteLocation(long time, double latitude, double longitude, int altitude, String provider,
Project project)
{
	this(latitude, longitude, altitude,
	provider, project);
	this.time = time;
}
@Override
public String toString() {
	return geoPoint.latitude + "/" + geoPoint.longitude
	+ ", Altitude=" + altitude + "m ("
	+ sdf.format(timeStamp) + ")";
}
public String getInsertString(){
	StringBuilder sb = new StringBuilder(100);
	sb.append("INSERT INTO Locations VALUES(");
	sb.append(time);
	sb.append(",");
	sb.append(projectID);
	sb.append(",");
	sb.append(geoPoint.latitude);
	sb.append(",");
	sb.append(geoPoint.longitude);
	sb.append(",");
	sb.append(altitude);
	sb.append(",'");
	sb.append(provider);
	sb.append("')");
	return sb.toString();
}



public static final Parcelable.Creator<NoteLocation> CREATOR =
new Creator<NoteLocation>() {
@Override
	public NoteLocation[] newArray(int size) {
	return new NoteLocation[size];
}
@Override
public NoteLocation createFromParcel(Parcel source) {
	return new NoteLocation(source);
}
};
private NoteLocation(Parcel source) {
	time = source.readLong();
	//geoPoint.latitude = source.readDouble();
	//geoPoint.longitude = source.readDouble();
	this.geoPoint = new LatLng(source.readDouble(), source.readDouble());
	altitude=source.readInt();
	projectID= source.readLong();
 
//location= (NoteLocation) source.readValue(null);
}
@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void writeToParcel(Parcel dest, int flags) {
	// TODO Auto-generated method stub
	dest.writeLong(time);
	dest.writeDouble(geoPoint.latitude);
	dest.writeDouble(geoPoint.longitude );
	dest.writeInt(altitude);
	dest.writeLong(projectID);
}
}


