package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dimitra on 21/07/2019.
 */

@Entity(tableName = "fpa")
public class FPA implements Serializable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "vatid")
    @NonNull
    String vatid;
    @ColumnInfo(name = "vat")
    double vat;
    @ColumnInfo(name = "custvatid")
    String custvatid;

    public void setId(int id) {
        this.id = id;
    }

    public void setCustvatid(String custvatid) {
        this.custvatid = custvatid;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public void setVatid(String vatid) {
        this.vatid = vatid;
    }

    public String getVatid() {
        return vatid;
    }

    public double getVat() {
        return vat;
    }

    public String getCustvatid() {
        return custvatid;
    }

    public int getId() {
        return id;
    }
}
