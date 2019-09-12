package gr.cityl.iliadis.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import gr.cityl.iliadis.Models.Catalog;
import gr.cityl.iliadis.Models.Country;
import gr.cityl.iliadis.Models.Customers;
import gr.cityl.iliadis.Models.FPA;
import gr.cityl.iliadis.Models.Products;
import gr.cityl.iliadis.Models.SecCustomers;

/**
 * Created by dimitra on 26/07/2019.
 */

@Dao
public interface DaoAccess {

    @Insert
    void insertTask(Products products);

    @Insert
    void insertTask(Customers customers);

    @Insert
    void insertTask(Catalog catalog);

    @Insert
    void insertTask(FPA fpa);

    @Insert
    void insertTask(Country country);

    @Insert
    void insertTask(SecCustomers secCustomers);

    @Insert
    void insertTaskProducts(List<Products> products);

    @Insert
    void insertTaskCustomers(List<Customers> customers);

    @Insert
    void insertTaskShops(List<SecCustomers> secCustomers);

    @Insert
    void insertTaskCatalog(List<Catalog> catalogs);

    @Insert
    void insertTaskCountry(List<Country> countries);

    @Insert
    void insertTaskFpa(List<FPA> fpaList);

    @Query("Select * from Products")
    List<Products> getProductsList();

    @Query("Select * from Customers")
    List<Customers> getCustomersList();

    @Query("Select * from FPA")
    List<FPA> getFpaList();

    @Query("Select * from FPA where custvatid =:custvatid")
    FPA getFpa(String custvatid);

    @Query("Select * from Catalog")
    List<Catalog> getCatalogList();

    @Query("Select * from Country")
    List<Country> getCountryList();

    @Query("Select * from SecCustomers")
    List<SecCustomers> getSecCustomersList();

    @Query("Select * from customers Where afm = :afm")
    Customers getCustomerByAfm(String afm);

    @Query("Select * from customers Where custid = :cust_id")
    Customers getCustomerByCustid(String cust_id);

    @Query("Select c1.companyName from customers c1 Where c1.companyName Like :companyname ||'%'")
    List<String> getCustomerByName(String companyname);

    @Query("Select * from customers Where companyName =:companyname")
    Customers getCustomerName(String companyname);

    @Query("Select * from seccustomers s where s.custid =:cust_id")
    List<SecCustomers> getShopsByCust(String cust_id);

    @Query("Select * from seccustomers s where s.shopid =:shopid")
    SecCustomers getShopsByShopid(String shopid);

    @Query("Select s.* from seccustomers s inner join customers c on c.custid=s.custid where s.custid =:cust_id")
    List<SecCustomers> getShopsByJoin(String cust_id);

    @Query("Select * from products  where prodcode =:prodcode")
    Products getProductByProdCode(String prodcode);

    @Query("Select c1.minimumstep from products c1  where prodcode =:prodcode")
    String getProductQuantity(String prodcode);

    @Query("Select * from products  where realcode =:realcode")
    Products getProductByRealCode(String realcode);

    @Query("Select * from catalog  where custcatid =:custcatid")
    Catalog getCatalogByCustCatId(int custcatid);

    @Query("Select c2.* from country c2 where c2.countryid =:countryid")
    Country getCountry(String countryid);

    @Query("Select c1.* from catalog c1 where c1.custcatid =:custcatid and c1.catid =:priceid")
    Catalog getCatalogueDiscount(int custcatid, String priceid);

    @Delete
    void deleteProducts(List<Products> products);

    @Delete
    void deleteCustomer(List<Customers> customers);

    @Delete
    void deleteCatalog(List<Catalog> catalogs);

    @Delete
    void deleteShops(List<SecCustomers> secCustomers);

    @Delete
    void deleteCountry(List<Country> countries);

    @Delete
    void deleteFpa(List<FPA> fpaList);

    @Update
    void update(Products products);

    @Update
    void updateProductList(List<Products> products);
}
