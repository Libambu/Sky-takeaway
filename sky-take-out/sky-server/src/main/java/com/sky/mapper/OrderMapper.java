package com.sky.mapper;

import com.sky.entity.Orders;
import com.sky.vo.Top10sqlVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    Double selectTurnover(LocalDateTime beginTime, LocalDateTime endTime, int i);

    Integer sumBymap(Map map);

    List<Top10sqlVO> top10List(LocalDateTime begin, LocalDateTime end);

    Integer countByMap(Map map);

    Double sumByMap2(Map map);
}
