package aurora.project;

import android.graphics.Bitmap;

public class Status {
	int id;
	String note;
	Bitmap image;
	int friendId;
	String username;
	String time;
	
	/**
	 * A mood status that was posted by some user.
	 * 
	 * @param id the status id
	 * @param note a note
	 * @param image a mood image
	 * @param friendId the id of the user who posted this status
	 * @param username the name of the user who posted this status
	 * @param time the time that this status was posed
	 */
	public Status(int id, String note, Bitmap image, int friendId, String username, String time) {
		this.id = id;
		this.note = note;
		this.image = image;
		this.friendId = friendId;
		this.username = username;
		this.time = time;
	}
}
