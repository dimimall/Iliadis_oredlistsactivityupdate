package gr.cityl.iliadis.Models;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import gr.cityl.iliadis.Interfaces.DaoShop;

/**
 * Created by dimitra on 08/08/2019.
 */
@Database(entities = {Order.class,Cart.class}, version = 4, exportSchema = false)

public abstract class ShopDatabase extends RoomDatabase {

    public static final String DB_NAME="shop_db";
    private static ShopDatabase instance;

    public static synchronized ShopDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),ShopDatabase.class,DB_NAME)
                    .allowMainThreadQueries().build();
        }
        return instance;
    }


    public abstract DaoShop daoShop();
}
