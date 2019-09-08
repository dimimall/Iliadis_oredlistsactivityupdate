package gr.cityl.iliadis.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import gr.cityl.iliadis.Models.Cart;
import gr.cityl.iliadis.Models.Order;

/**
 * Created by dimitra on 08/08/2019.
 */
@Dao
public interface DaoShop {

    @Insert
    void insertTask(Order order);

    @Insert
    void insertTask(Cart cart);

    @Delete()
    void deleteCart(Cart cart);

    @Query("Delete from `order` where orderid=:orderid")
    void deleteOrderByOrderId(int orderid);

    @Update
    void updateOrder(Order order);

    @Query("Update cart set comment =:comment and price=:price and quantity=:qty where cartid=:cartid")
    void updateCart(String comment,String price,int qty,int cartid);

    @Query("Update 'order' set status =:status where orderid =:orderid")
    void updateOrderStatus(int status,int orderid);

    @Query("Select * from 'order' where custid =:custid")
    Order getOrder(String custid);

    @Query("Select * from 'order' where custid =:custid")
    List<Order> getListOrder(String custid);

    @Query("Select * from 'order' where custid =:custid and status=0")
    List<Order> getListOrderStatus0(String custid);

    @Query("Select * from 'order' where custid =:custid and status=1 or status=2")
    List<Order> getListOrderStatus1(String custid);

    @Query("Select * from cart where orderid =:orderid")
    List<Cart> getCartList(int orderid);

    @Query("Select c2.prodcode from cart c2 where c2.orderid =:orderid and c2.prodcode =:prodcode")
    String getCartProdCode(int orderid,String prodcode);

    @Query("Select c2.realcode from cart c2 where c2.orderid =:orderid and c2.realcode =:realcode")
    String getCartRealCode(int orderid,String realcode);
}
