package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dimitra on 08/08/2019.
 */
@Entity(tableName = "cart")
public class Cart implements Serializable{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "cartid")
    @NonNull
    int cartid;
    @ColumnInfo(name = "discountid")
    String discountid;
    @ColumnInfo(name = "orderid")
    int orderid;
    @ColumnInfo(name = "realcode")
    @NonNull
    String realcode;
    @ColumnInfo(name = "prodcode")
    @NonNull
    String prodcode;
    @ColumnInfo(name = "price")
    @NonNull
    String price;
    @ColumnInfo(name = "comment")
    String comment;
    @ColumnInfo(name = "description")
    @NonNull
    String description;
    @ColumnInfo(name = "quantity")
    @NonNull
    int quantity;
    @ColumnInfo(name = "vatcode")
    @NonNull
    String vatcode;
    @ColumnInfo(name = "priceid")
    @NonNull
    String priceid;

    public Cart(int orderid,String realcode,String prodcode,String price,String comment,String description,int quantity,String vatcode,String priceid,String discountid)
    {
        this.orderid=orderid;
        this.realcode=realcode;
        this.prodcode=prodcode;
        this.price=price;
        this.comment=comment;
        this.description = description;
        this.quantity=quantity;
        this.vatcode=vatcode;
        this.priceid=priceid;
        this.discountid = discountid;
    }

    public void setDiscountid(String discountid) {
        this.discountid = discountid;
    }

    public void setDescription(@NonNull String description) {
        this.description = description;
    }

    public void setVatcode(@NonNull String vatcode) {
        this.vatcode = vatcode;
    }

    public void setRealcode(@NonNull String realcode) {
        this.realcode = realcode;
    }

    public void setProdcode(@NonNull String prodcode) {
        this.prodcode = prodcode;
    }

    public void setPrice(@NonNull String price) {
        this.price = price;
    }

    public void setPriceid(@NonNull String priceid) {
        this.priceid = priceid;
    }

    public void setCartid(int cartid) {
        this.cartid = cartid;
    }

    public void setComment(@NonNull String comment) {
        this.comment = comment;
    }

    public void setQuantity(@NonNull int quantity) {
        this.quantity = quantity;
    }

    public void setOrderid(@NonNull int orderid) {
        this.orderid = orderid;
    }

    public String getDiscountid() {
        return discountid;
    }

    @NonNull
    public String getDescription() {
        return description;
    }

    public int getCartid() {
        return cartid;
    }

    @NonNull
    public int getOrderid() {
        return orderid;
    }

    @NonNull
    public String getRealcode() {
        return realcode;
    }

    @NonNull
    public String getProdcode() {
        return prodcode;
    }

    @NonNull
    public String getComment() {
        return comment;
    }

    @NonNull
    public String getPrice() {
        return price;
    }

    @NonNull
    public String getVatcode() {
        return vatcode;
    }

    @NonNull
    public String getPriceid() {
        return priceid;
    }

    @NonNull
    public int getQuantity() {
        return quantity;
    }
}
