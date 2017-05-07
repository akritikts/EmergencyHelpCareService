package in.silive.emergency.model;

import android.provider.BaseColumns;




/**
 * Table Data class contains information about the Table and database that will be used to store contacts.
 */
public class TableData {

    public TableData(){}

    public static abstract class TableInfo implements BaseColumns{
        public static final String KEY_ID = "_id";
        public static final String CONTACT_NAME = "name";
        public static final String CONTACT_NUMBER = "number";
        public static final String DATABASE_NAME = "contacts_database";
        public static final String TABLE_NAME = "contacts_table";

    }
}
