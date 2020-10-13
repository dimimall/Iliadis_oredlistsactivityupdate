package gr.cityl.iliadis.Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by dimitra on 21/07/2019.
 */

@Entity(tableName = "customers")
public class Customers implements Serializable {

    @ColumnInfo(name = "address")
    String address;
    @ColumnInfo(name = "profession")
    String profession;
    @ColumnInfo(name = "taxOffice")
    String taxOffice;
    @ColumnInfo(name = "companyName")
    String companyName;
    @ColumnInfo(name = "city")
    String city;
    @ColumnInfo(name = "country")
    String country;
    @ColumnInfo(name = "company")
    int company;
    @ColumnInfo(name = "region")
    String region;
    @ColumnInfo(name = "phone")
    String phone;
    @ColumnInfo(name = "fax")
    String fax;
    @ColumnInfo(name = "afm")
    String afm;
    @ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "catalogueid")
    int catalogueid;
    @ColumnInfo(name = "postalCode")
    String postalCode;
    @ColumnInfo(name = "custid")
    String custid;
    @ColumnInfo(name = "paymentid")
    String paymentid;
    @ColumnInfo(name = "custvatid")
    String custvatid;
    @PrimaryKey
    @ColumnInfo(name = "customerid")
    @NonNull
    String customerid;

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public void setCatalogueid(int catalogueid) {
        this.catalogueid = catalogueid;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCustomerid(String customerid) {
        this.customerid = customerid;
    }

    public void setCustvatid(String custvatid) {
        this.custvatid = custvatid;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public void setPaymentid(String paymentid) {
        this.paymentid = paymentid;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public String getCustvatid() {
        return custvatid;
    }

    public String getAfm() {
        return afm;
    }

    public int getCatalogueid() {
        return catalogueid;
    }

    public int getCompany() {
        return company;
    }

    public String getCustomerid() {
        return customerid;
    }

    public String getPaymentid() {
        return paymentid;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCustid() {
        return custid;
    }

    public String getEmail() {
        return email;
    }

    public String getFax() {
        return fax;
    }

    public String getPhone() {
        return phone;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getProfession() {
        return profession;
    }

    public String getRegion() {
        return region;
    }
}
