package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by dimitra on 26/07/2019.
 */

@Entity(tableName = "country")
public class Country {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "countryid")
    @NonNull
    String countryid;
    @ColumnInfo(name = "country")
    String Country;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountry(String country) {
        Country = country;
    }

    public void setCountryid(String countryid) {
        this.countryid = countryid;
    }

    public String getCountryid() {
        return countryid;
    }

    public String getCountry() {
        return Country;
    }

    public int getId() {
        return id;
    }
}
