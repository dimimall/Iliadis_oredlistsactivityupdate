package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by dimitra on 21/07/2019.
 */
@Entity(tableName = "catalog")
public class Catalog {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    int id;
    @ColumnInfo(name = "catid")
    @NonNull
    String catid;
    @ColumnInfo(name = "custcatid")
    String custcatid;
    @ColumnInfo(name = "discountqstart1")
    int discountqstart1;
    @ColumnInfo(name = "discountqstart2")
    int discountqstart2;
    @ColumnInfo(name = "discountqstart3")
    int discountqstart3;
    @ColumnInfo(name = "discountqstart4")
    int discountqstart4;
    @ColumnInfo(name = "discountqstart5")
    int discountqstart5;
    @ColumnInfo(name = "discountqend1")
    int discountqend1;
    @ColumnInfo(name = "discountqend2")
    int discountqend2;
    @ColumnInfo(name = "discountqend3")
    int discountqend3;
    @ColumnInfo(name = "discountqend4")
    int discountqend4;
    @ColumnInfo(name = "discountqend5")
    int discountqend5;
    @ColumnInfo(name = "discount1")
    int discount1;
    @ColumnInfo(name = "discount2")
    int discount2;
    @ColumnInfo(name = "discount3")
    int discount3;
    @ColumnInfo(name = "discount4")
    int discount4;
    @ColumnInfo(name = "discount5")
    int discount5;

    public void setId(int id) {
        this.id = id;
    }

    public void setCatid(String catid) {
        this.catid = catid;
    }

    public void setCustcatid(String custcatid) {
        this.custcatid = custcatid;
    }

    public void setDiscount1(int discount1) {
        this.discount1 = discount1;
    }

    public void setDiscount2(int discount2) {
        this.discount2 = discount2;
    }

    public void setDiscount3(int discount3) {
        this.discount3 = discount3;
    }

    public void setDiscountqend1(int discountqend1) {
        this.discountqend1 = discountqend1;
    }

    public void setDiscount4(int discount4) {
        this.discount4 = discount4;
    }

    public void setDiscount5(int discount5) {
        this.discount5 = discount5;
    }

    public void setDiscountqend2(int discountqend2) {
        this.discountqend2 = discountqend2;
    }

    public void setDiscountqend3(int discountqend3) {
        this.discountqend3 = discountqend3;
    }

    public void setDiscountqend4(int discountqend4) {
        this.discountqend4 = discountqend4;
    }

    public void setDiscountqend5(int discountqend5) {
        this.discountqend5 = discountqend5;
    }

    public void setDiscountqstart1(int discountqstart1) {
        this.discountqstart1 = discountqstart1;
    }

    public void setDiscountqstart2(int discountqstart2) {
        this.discountqstart2 = discountqstart2;
    }

    public void setDiscountqstart3(int discountqstart3) {
        this.discountqstart3 = discountqstart3;
    }

    public void setDiscountqstart4(int discountqstart4) {
        this.discountqstart4 = discountqstart4;
    }

    public void setDiscountqstart5(int discountqstart5) {
        this.discountqstart5 = discountqstart5;
    }

    public int getId() {
        return id;
    }

    public String getCatid() {
        return catid;
    }

    public String getCustcatid() {
        return custcatid;
    }

    public int getDiscount1() {
        return discount1;
    }

    public int getDiscount2() {
        return discount2;
    }

    public int getDiscount3() {
        return discount3;
    }

    public int getDiscount4() {
        return discount4;
    }

    public int getDiscount5() {
        return discount5;
    }

    public int getDiscountqend1() {
        return discountqend1;
    }

    public int getDiscountqend2() {
        return discountqend2;
    }

    public int getDiscountqend3() {
        return discountqend3;
    }

    public int getDiscountqend4() {
        return discountqend4;
    }

    public int getDiscountqend5() {
        return discountqend5;
    }

    public int getDiscountqstart1() {
        return discountqstart1;
    }

    public int getDiscountqstart2() {
        return discountqstart2;
    }

    public int getDiscountqstart3() {
        return discountqstart3;
    }

    public int getDiscountqstart4() {
        return discountqstart4;
    }

    public int getDiscountqstart5() {
        return discountqstart5;
    }
}
