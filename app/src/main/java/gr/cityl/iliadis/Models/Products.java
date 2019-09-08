package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dimitra on 21/07/2019.
 */

@Entity(tableName = "products")
public class Products implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "prodcode")
    String prodcode;
    @ColumnInfo(name = "realcode")
    @NonNull
    String realcode;
    @ColumnInfo(name = "prodescription")
    String prodescription;
    @ColumnInfo(name = "prodescriptopnen")
    String prodescriptionEn;
    @ColumnInfo(name = "vatcode")
    String vatcode;
    @ColumnInfo(name = "priceid")
    String priceid;
    @ColumnInfo(name = "price")
    String price;
    @ColumnInfo(name = "specialprice")
    String specialprice;
    @ColumnInfo(name = "reserved")
    String reserved;
    @ColumnInfo(name = "adate")
    String adate;
    @ColumnInfo(name = "minimumstep")
    String minimumstep;
    @ColumnInfo(name = "minquantity")
    String minquantity;
    @ColumnInfo(name = "quantityap")
    String quantityap;
    @ColumnInfo(name = "quantityav")
    String quantityav;
    @ColumnInfo(name = "quantitytotal")
    String quantitytotal;
    @ColumnInfo(name = "quantitywaiting")
    String quantitywaiting;

    public Products()
    {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdate(String adate) {
        this.adate = adate;
    }

    public void setMinimumstep(String minimumstep) {
        this.minimumstep = minimumstep;
    }

    public void setMinquantity(String minquantity) {
        this.minquantity = minquantity;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPriceid(String priceid) {
        this.priceid = priceid;
    }

    public void setProdcode(String prodcode) {
        this.prodcode = prodcode;
    }

    public void setProdescription(String prodescription) {
        this.prodescription = prodescription;
    }

    public void setProdescriptionEn(String prodescriptionEn) {
        this.prodescriptionEn = prodescriptionEn;
    }

    public void setRealcode(String realcode) {
        this.realcode = realcode;
    }

    public void setQuantityap(String quantityap) {
        this.quantityap = quantityap;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public void setQuantityav(String quantityav) {
        this.quantityav = quantityav;
    }

    public void setQuantitytotal(String quantitytotal) {
        this.quantitytotal = quantitytotal;
    }

    public void setSpecialprice(String specialprice) {
        this.specialprice = specialprice;
    }

    public void setQuantitywaiting(String quantitywaiting) {
        this.quantitywaiting = quantitywaiting;
    }

    public void setVatcode(String vatcode) {
        this.vatcode = vatcode;
    }

    public int getId() {
        return id;
    }

    public String getPrice() {
        return price;
    }

    public String getSpecialprice() {
        return specialprice;
    }

    public String getMinimumstep() {
        return minimumstep;
    }

    public String getMinquantity() {
        return minquantity;
    }

    public String getPriceid() {
        return priceid;
    }

    public String getProdcode() {
        return prodcode;
    }

    public String getQuantityap() {
        return quantityap;
    }

    public String getQuantityav() {
        return quantityav;
    }

    public String getQuantitytotal() {
        return quantitytotal;
    }

    public String getQuantitywaiting() {
        return quantitywaiting;
    }

    public String getRealcode() {
        return realcode;
    }

    public String getReserved() {
        return reserved;
    }

    public String getVatcode() {
        return vatcode;
    }

    public String getAdate() {
        return adate;
    }

    public String getProdescription() {
        return prodescription;
    }

    public String getProdescriptionEn() {
        return prodescriptionEn;
    }
}
