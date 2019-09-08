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
    String realcode;
    @ColumnInfo(name = "prodcode")
    String prodcode;
    @ColumnInfo(name = "price")
    String price;
    @ColumnInfo(name = "comment")
    String comment;
    @ColumnInfo(name = "description")
    String description;
    @ColumnInfo(name = "quantity")
    int quantity;
    @ColumnInfo(name = "vatcode")
    String vatcode;
    @ColumnInfo(name = "priceid")
    String priceid;

    public Cart()
    {
    }
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVatcode(String vatcode) {
        this.vatcode = vatcode;
    }

    public void setRealcode(String realcode) {
        this.realcode = realcode;
    }

    public void setProdcode(String prodcode) {
        this.prodcode = prodcode;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPriceid(String priceid) {
        this.priceid = priceid;
    }

    public void setCartid(@NonNull  int cartid) {
        this.cartid = cartid;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public String getDiscountid() {
        return discountid;
    }

    public String getDescription() {
        return description;
    }

    @NonNull
    public int getCartid() {
        return cartid;
    }

    public int getOrderid() {
        return orderid;
    }

    public String getRealcode() {
        return realcode;
    }

    public String getProdcode() {
        return prodcode;
    }

    public String getComment() {
        return comment;
    }

    public String getPrice() {
        return price;
    }

    public String getVatcode() {
        return vatcode;
    }

    public String getPriceid() {
        return priceid;
    }

    public int getQuantity() {
        return quantity;
    }
}
