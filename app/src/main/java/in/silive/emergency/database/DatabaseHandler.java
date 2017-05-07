package in.silive.emergency.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import in.silive.emergency.model.TableData.TableInfo;
import in.silive.emergency.model.Contact;




/** It handles all database operations. The operation includes creating database, deleting database, insertion
 * deletion, updating database. The database and table information is stored in TableDate class.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    /** Database version **/
    public static final int DATABASE_VERSION = 1;




    /** Constructor. Creates a database**/
    public DatabaseHandler(Context context) {
        super(context, TableInfo.DATABASE_NAME, null, DATABASE_VERSION ); // Creating database
        Log.d("Database Operations", "Database created successfully.");
    }


    /** Returns the name of database. **/
    @Override
    public String getDatabaseName() {
        return TableInfo.DATABASE_NAME;  /** return database name **/
    }


    /**
     * Creates a Table. Executed when the database is created.
     * @param sqLiteDatabase Database in which table will be created.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            /** String statement of Query to create a table --- insert semicolon at the last.**/
            String CREATE_QUERY = "CREATE TABLE " + TableInfo.TABLE_NAME +"(" + TableInfo._ID +
                    " INTEGER PRIMARY KEY, "+ TableInfo.CONTACT_NAME +" TEXT, " + TableInfo.CONTACT_NUMBER + " TEXT);";

            sqLiteDatabase.execSQL(CREATE_QUERY); // executes a sqLite statement which doesn't return any data(select)
            Log.d("Database Operations", "Table created successfully.");
        }
        catch(SQLException e){
            Log.e("Database Operations", "Cannot create table, invalid query.", e);
        }
    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /** To handle database upgrade **/

    }

    /**
     * Inserts a single contact into the database.
     * @param contact Contact to be inserted
     */
    public void putContact(Contact contact) {
        SQLiteDatabase database = this.getWritableDatabase();       // get database
        ContentValues cv = new ContentValues();         // used to store set of values, maps a value to a key
        cv.put(TableInfo.CONTACT_NAME, contact.getName());
        cv.put(TableInfo.CONTACT_NUMBER, contact.getPhoneNumber());

        /** cv object maps values to keys. They keys will act as column names for insert() method of database **/
        long id = database.insert(TableInfo.TABLE_NAME, null, cv);
        if (id == -1) {
            /** error in insertion **/
            Log.e("Database Operations", "Error in inserting single contact into the database.");
        }
    }

    /**
     * Insert an entire list of contact into the database.
     * @param contactList ArrayList of contacts.
     */
    public void putContactList(ArrayList<Contact> contactList ){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for(int index = 0; index < contactList.size(); index++){
            /** insert data into contentValue object **/
            cv.put(TableInfo.CONTACT_NAME, contactList.get(index).getName());
            cv.put(TableInfo.CONTACT_NUMBER, contactList.get(index).getPhoneNumber());

            long id = database.insert(TableInfo.TABLE_NAME, null, cv);
            if(id == -1){
                /** error in insertion **/
                Log.e("Database Operations", "Error in inserting ArrayList of contacts into the database.");
            }
            else Log.d("Database Operations", "Insertion Successful");
        }
    }


    /**
     * Returns cursor object and can be used for retrieval of data for custom queries
     * @return Cursor object
     */
    public Cursor getCursor(){
        SQLiteDatabase database = this.getReadableDatabase();    // only for reading
        String[] columns = {TableInfo.CONTACT_NAME, TableInfo.CONTACT_NUMBER };
        return database.query(TableInfo.TABLE_NAME, columns, null, null, null, null, null);
    }


    /**
     * Retrieves entire data from the database and saves in ArrayList of Contact.
     * @return  ArrayList of contacts containing all the contacts.
     */
    public ArrayList<Contact> getContactList() {
        ArrayList contactList = new ArrayList();
        Cursor cr = this.getCursor();

        if(cr.moveToFirst()){
            int index = 0;
            do {
                /** Get the name and phone number of contact from database and save it into Contact object **/
                Contact contact = new Contact(cr.getString(0), cr.getString(1));  // 0,1 represents column index
                contactList.add(index++, contact);
            }
            while(cr.moveToNext());
        }
        else {
            /** cursor is empty **/
            Log.e("Database Operations", "Unable to get contact list. The cursor is empty.");
        }
        cr.close();
        return contactList;
    }


    /**
     * Deletes a single contact from the database
     * @param contact Contact to be deleted
     */
    public void deleteContact(Contact contact){
        SQLiteDatabase database = this.getWritableDatabase();
        String selection = TableInfo.CONTACT_NAME + " LIKE ? AND " + TableInfo.CONTACT_NUMBER + " LIKE ?";
        String[] values = {contact.getName(), contact.getPhoneNumber()};
        int x =   database.delete(TableInfo.TABLE_NAME, selection, values); // table name, selection criteria and where to be deleted
        if(x <= 0){
            Log.e("Database Operations", "Error in deletion");
        }
    }

    /**
     * Clears the entire database. Deletes all the contacts in the database.
     */
    public void clearDatabase(){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TableInfo.TABLE_NAME, null, null); // deletes all rows
    }

    /**
     * Updates a contact in the database.
     * @param oldContact    The contact which needs to be updated. Old values of contact.
     * @param newContact    New Contact that need to be saved in place of old contact.
     */
    public void updateContact(Contact oldContact, Contact newContact){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        String selection = TableInfo.CONTACT_NAME + " LIKE ? AND " + TableInfo.CONTACT_NUMBER + " LIKE ?"; // create selection statement
        String[] values = {oldContact.getName(), oldContact.getPhoneNumber()};  // need values for selection statement
        cv.put(TableInfo.CONTACT_NAME, newContact.getName()); // create cv object for putting new values into the database
        cv.put(TableInfo.CONTACT_NUMBER, newContact.getPhoneNumber());
        database.update(TableInfo.TABLE_NAME, cv, selection, values); //update database
    }





}