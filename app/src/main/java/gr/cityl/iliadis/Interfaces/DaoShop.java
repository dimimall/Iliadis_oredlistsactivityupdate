package gr.cityl.iliadis.Interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
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

    @Delete()
    void deleteCartList(List<Cart> carts);

    @Query("Delete from `order` where orderid=:orderid")
    void deleteOrderByOrderId(int orderid);

    @Update()
    void updateOrder(Order order);

    @Update(onConflict = OnConflictStrategy.IGNORE)
    void updateCart(Cart cart);

    @Query("Update cart set comment =:comment and price=:price and quantity=:qty where cartid=:cartid and orderid=:orderid")
    void updateCart2(String comment,String price,int qty,int cartid,int orderid);

    @Query("Update 'order' set status =:status and commendorder=:comment where orderid =:orderid")
    void updateOrderStatus(int status,String comment,int orderid);

    @Query("Select * from 'order' where custid =:custid")
    Order getOrder(String custid);

    @Query("Select * from 'order' where custid =:custid and status=0")
    List<Order> getListOrder(String custid);

    @Query("Select * from 'order' where status=0")
    List<Order> getListOrderStatus0();

    @Query("Select * from 'order' where status=2")
    List<Order> getListOrderStatus1();

    @Query("Select o1.orderid from 'order' as o1 where status=1")
    List<Integer> getListOrderCsv();

    @Query("Select * from cart where orderid =:orderid ORDER BY cartid desc")
    List<Cart> getCartList(int orderid);

    @Query("Select c2.prodcode from cart c2 where c2.orderid =:orderid and c2.prodcode =:prodcode")
    String getCartProdCode(int orderid,String prodcode);

    @Query("Select c2.realcode from cart c2 where c2.orderid =:orderid and c2.realcode =:realcode")
    String getCartRealCode(int orderid,String realcode);
}
