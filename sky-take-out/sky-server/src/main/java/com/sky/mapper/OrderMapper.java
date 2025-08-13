package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    @Select("select * from orders where status = #{pendingPayment} and order_time < #{time}")
    List<Orders> selectByStatusAndTime(Integer pendingPayment, LocalDateTime time);

    void update(Orders o);

    @Update("update orders set status = #{status} where id = #{id}")
    void updateStatus(Orders o);

    @Select("select * from orders where user_id = #{userId} and number = #{orderNumber}")
    Orders selectByUserIdAndNum(Long userId, String orderNumber);
}
