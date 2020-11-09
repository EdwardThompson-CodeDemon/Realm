package sparta.realm.spartautils;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class DatabaseHelper extends SQLiteOpenHelper {

	//database
	public static String DB_PATH,GUIDE_PATH;
	public static String DB_NAME = svars.DB_NAME;
	public static SQLiteDatabase database;
	public final Context context;
	public static final String ID_COLUMN = "_id";
	private static DatabaseHelper instance;



	public static synchronized DatabaseHelper getHelper(Context context) {

		if (instance == null)
			instance = new DatabaseHelper(context);
		return instance;
	}

	public SQLiteDatabase getDb() {
		return database;
	}
	
/*	public DatabaseHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;
		
		String packageName = context.getPackageName();
		DB_PATH = "/data/data/"+packageName+"/databases/";
		DB_NAME = databaseName;
		openDataBase();
	}*/
	public DatabaseHelper(Context context) {
        super(context, context.getExternalFilesDir(null).getAbsolutePath() + "/" + svars.DB_NAME, null, 1);
		  this.context = context;
		  	String packageName = context.getPackageName();
		  	DB_PATH = context.getExternalFilesDir(null).getAbsolutePath() + "/";
		GUIDE_PATH = context.getExternalFilesDir(null).getAbsolutePath() + "/";
		  	DB_NAME = svars.DB_NAME;
		  		openDataBase();
    }
	
	public void createDataBase() {
		
		boolean dbExist = checkDataBase();
		if (!dbExist) {
			//this.getReadableDatabase();
			this.getWritableDatabase();//getReadableDatabase();//
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.e(this.getClass().toString(), "Copying error");
				throw new Error("Error copying database!");
			}
		} else {
			Log.i(this.getClass().toString(), "Database already exists");
			
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			String path = DB_PATH + DB_NAME;
			checkDb = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
			Log.e(this.getClass().toString(), "checking db" + checkDb);
		} catch (SQLException e) {
			Log.e(this.getClass().toString(), "Error while checking db");
		}

		if (checkDb != null) {
			checkDb.close();
		}
		return checkDb != null;
	}

	private void copyDataBase() throws IOException {

		//assets
		InputStream externalDbStream = context.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;

		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();
		copy_guide();
	}
private void copy_guide() throws IOException {

		//assets
		InputStream externalDbStream = context.getAssets().open(svars.GUIDE_NAME);
		String outFileName = GUIDE_PATH + svars.GUIDE_NAME;

		OutputStream localDbStream = new FileOutputStream(outFileName);

		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = externalDbStream.read(buffer)) > 0) {
			localDbStream.write(buffer, 0, bytesRead);
		}
		localDbStream.close();
		externalDbStream.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String path = DB_PATH + DB_NAME;
		if (database == null) {
			createDataBase();
			database = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
		}
		return database;
	}
	
	@Override
	public synchronized void close() {
		if (database != null) {
			database.close();
		}
		super.close();
	}
	public void getDGSDCARD(){
		PullDatabase p = new PullDatabase();
	    p.getDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
