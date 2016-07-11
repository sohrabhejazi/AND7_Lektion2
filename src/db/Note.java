package db;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

public class Note implements Parcelable{

	private long time;
	public long getTime() { return time; } //für Ausgaben
	public long time_loc;
	private long projectID;
	public String subject;
	public String note;
	public String category;
	public NoteLocation location;
	public boolean isPersistent = false;
	
	// Konstruktor zur Erzeugung aus der Servernachricht
	public Note(String time, String subject, String note, NoteLocation location) 
	{
		this.time = Long.parseLong(time);
		this.subject = subject;
		this.note = note;
		this.location = location;
		this.projectID = location.getProjectID();
		this.time_loc = location.getTime();
	}
	
	public Note(String subject, String note,
			String category, Project project,
			NoteLocation location)
			{
			this.subject = subject;
			this.note = note;
			this.category = category;
			this.location = location;
			this.time = System.currentTimeMillis();
			this.projectID = project.getProjectID();
			this.time_loc = location.getTime();
			}
	public Note(long time, String subject, String note,
			String category, Project project, long locationRef)
			{
				this.time = time;
				this.subject = subject;
				this.note = note;
				this.category = category;
				this.projectID = project.getProjectID();
				this.time_loc = locationRef;
			}
	
	
	public String getInsertString(){
		StringBuilder sb = new StringBuilder(100);
		sb.append("INSERT INTO Notes VALUES(");
		sb.append(time);
		sb.append(",");
		sb.append(time_loc);
		sb.append(",");
		sb.append(projectID);
		sb.append(",'");
		sb.append(subject);
		sb.append("','");
		sb.append(note);
		sb.append("','");
		sb.append(category);
		sb.append("')");
		return sb.toString();
	}
	
	public String getUpdateString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UPDATE Notes SET subject='");
		sb.append(subject);
		sb.append("', note='");
		sb.append(note);
		//sb.append("', category='");
		//sb.append(category);
		sb.append("' WHERE time=");
		sb.append(time);
		return sb.toString();
		}
	public static final Parcelable.Creator<Note> CREATOR =
				new Creator<Note>() {
				@Override
				public Note[] newArray(int size) {
					return new Note[size];
				}
				@Override
				public Note createFromParcel(Parcel source) {
					return new Note(source);
				}
			};
	public Note(Parcel source) {
				time = source.readLong();
				time_loc = source.readLong();
				projectID = source.readLong();
				subject=source.readString();
				note= source.readString();
				category= source.readString();
				location= (NoteLocation) source.readParcelable(NoteLocation.class.getClassLoader()) ;
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
		dest.writeLong(time_loc);
		dest.writeLong(projectID);
		dest.writeString(subject);
		dest.writeString(note);
		dest.writeString(category);
		dest.writeParcelable(location,flags); 
	}
	public void setGeo(LatLng LL)
	{
		location.geoPoint=LL;
	}
	public String getdeleteString(){
		StringBuilder sb = new StringBuilder(100);
		sb.append("DELETE FROM Notes WHERE time=");
		sb.append(time);
		return sb.toString();
	}
}
