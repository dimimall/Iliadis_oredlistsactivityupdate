package gr.cityl.iliadis.Models;

import java.util.List;

/**
 * Created by dimitra on 02/08/2019.
 */

public class ParamOrders {

    String custid;
    String afm;
    String companyname;
    String dateorder;
    String shopid;
    int orderid;

    public ParamOrders(String custid, String afm, String companyname, String dateorder, String shopid,int orderid)
    {
        this.custid = custid;
        this.afm = afm;
        this.companyname = companyname;
        this.dateorder = dateorder;
        this.shopid = shopid;
        this.orderid = orderid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public void setCustid(String custid) {
        this.custid = custid;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public void setDateorder(String dateorder) {
        this.dateorder = dateorder;
    }

    public String getShopid() {
        return shopid;
    }

    public int getOrderid() {
        return orderid;
    }

    public String getCustid() {
        return custid;
    }

    public String getAfm() {
        return afm;
    }

    public String getCompanyname() {
        return companyname;
    }

    public String getDateorder() {
        return dateorder;
    }
}
