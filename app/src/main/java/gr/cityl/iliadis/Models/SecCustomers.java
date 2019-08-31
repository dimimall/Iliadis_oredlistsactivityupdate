package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dimitra on 26/07/2019.
 */
@Entity(tableName = "seccustomers")
public class SecCustomers implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "custid")
    String custid;
    @ColumnInfo(name = "shopid")
    @NonNull
    String shopid;
    @ColumnInfo(name = "companyName")
    String companyName;
    @ColumnInfo(name = "address")
    String address;
    @ColumnInfo(name = "phone")
    String phone;

    public void setId(int id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getPhone() {
        return phone;
    }

    public String getCustid() {
        return custid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getAddress() {
        return address;
    }

    public String getShopid() {
        return shopid;
    }

    public int getId() {
        return id;
    }
}
