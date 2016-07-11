package db;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Parcel;
import android.os.Parcelable;
public class Project implements Parcelable
{
		private long projectID;
		public long getProjectID() { return projectID; }
		private String name;
		public String getName() { return name; };
		public String description = "";
		private SimpleDateFormat sdfProject = new
		SimpleDateFormat("yy_MM_dd", Locale.GERMAN);
		private String format = "yy_MM_dd";
		private SimpleDateFormat sdf = new SimpleDateFormat(format);
		public boolean isPersistent = false;
		public Project(long projectID, String name,
				String description)
				{
					this.projectID = projectID;
					this.name = name;
					this.description = description;
				}
		public Project()
		{
			projectID = System.currentTimeMillis();
			String result = sdfProject.format(new Date(projectID));
			name=result;
			//name="Prj"+projectID;
		}
		public void appendToName(String termToAppend) {
			name = sdf.format(new Date(projectID))+ "-" + termToAppend;
			//name="Prj"+projectID+termToAppend;
		}
		/* ******* Parcelable-Implementierung ******* */
		public static final Parcelable.Creator<Project> CREATOR =
		new Creator<Project>() {
			@Override
			public Project[] newArray(int size) {
					return new Project[size];
			}
			@Override
			public Project createFromParcel(Parcel source) {
					return new Project(source);
			}
		};
		private Project(Parcel source) {
			projectID = source.readLong();
			name = source.readString();
			description = source.readString();
		}
		
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeLong(projectID);
			dest.writeString(name);
			dest.writeString(description);
			
		}
		public String getInsertString(){
			//String description="des";
			StringBuilder sb = new StringBuilder(100);
			sb.append("INSERT INTO Projects VALUES(");
			sb.append(projectID);
			sb.append(",'");
			sb.append(name);
			sb.append("','");
			sb.append(description);
			sb.append("')");
			return sb.toString();
		}
		
		public String getUpdateString() {
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE Projects SET name='");
				sb.append(name);
				sb.append("', description='");
				sb.append(description);
				sb.append("' WHERE project_id=");
				sb.append(projectID);
				return sb.toString();
			}
		public String getdeleteString(){
			StringBuilder sb = new StringBuilder(100);
			sb.append("DELETE FROM Projects WHERE name='");
			sb.append(name);
			sb.append("'");
			return sb.toString();
		}

}