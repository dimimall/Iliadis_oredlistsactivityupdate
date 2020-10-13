package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by dimitra on 08/08/2019.
 */
@Entity(tableName = "order")
public class Order implements Serializable{

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "orderid")
    int orderid;
    @ColumnInfo(name = "custid")
    @NonNull
    String custid;
    @ColumnInfo(name = "status")
    @NonNull
    int status;
    @ColumnInfo(name = "dateparsed")
    @NonNull
    String dateparsed;
    @ColumnInfo(name = "shopid")
    @NonNull
    String shopid;

    @ColumnInfo(name = "commendorder")
    String commentorder;

    public Order()
    {

    }
    public Order(int orderid ,String custid,int status,String dateparsed,String shopid,String commentorder)
    {
        this.orderid = orderid;
        this.custid=custid;
        this.status=status;
        this.dateparsed=dateparsed;
        this.shopid = shopid;
        this.commentorder = commentorder;
    }

    public void setCommentorder(String commentorder) {
        this.commentorder = commentorder;
    }

    public void setShopid(@NonNull String shopid) {
        this.shopid = shopid;
    }

    public void setOrderid(@NonNull int orderid) {
        this.orderid = orderid;
    }

    public void setCustid(@NonNull String custid) {
        this.custid = custid;
    }

    public void setDateparsed(@NonNull String dateparsed) {
        this.dateparsed = dateparsed;
    }

    public void setStatus(@NonNull int status) {
        this.status = status;
    }

    public String getCommentorder() {
        return commentorder;
    }

    @NonNull
    public String getCustid() {
        return custid;
    }

    public int getStatus() {
        return status;
    }

    @NonNull
    public int getOrderid() {
        return orderid;
    }

    @NonNull
    public String getDateparsed() {
        return dateparsed;
    }

    @NonNull
    public String getShopid() {
        return shopid;
    }
}
