package gr.cityl.iliadis.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.RoomMasterTable;
import android.content.Context;

import gr.cityl.iliadis.Interfaces.DaoAccess;

/**
 * Created by dimitra on 26/07/2019.
 */

@Database(entities = {Products.class,Customers.class,Catalog.class,FPA.class,SecCustomers.class,Country.class}, version = 2, exportSchema = false)
public abstract class IliadisDatabase extends RoomDatabase {

    private static final String DB_NAME="Iliadis_DB";
    private static IliadisDatabase instance;

    public static synchronized IliadisDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),IliadisDatabase.class,DB_NAME)
                    .allowMainThreadQueries().build();
        }
        return instance;
    }
    public abstract DaoAccess daoAccess();
}
