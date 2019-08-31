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

    @Update
    void updateCart(Cart cart);

    @Query("Select * from 'order' where custid =:custid")
    Order getOrder(String custid);

    @Query("Select * from 'order' where custid =:custid")
    List<Order> getListOrder(String custid);

    @Query("Select * from cart where orderid =:orderid")
    List<Cart> getCartList(int orderid);
}
