package com.sky.Task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时订单的方法
     */
    @Scheduled(cron = "0 * * * * ?")//每分钟触发一次
    public void processTimeOrder(){
        log.info("定时处理订单：{}", LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);
        //select * from orders where status = ? and order_time<time;
        List<Orders> orders = orderMapper.selectByStatusAndTime(Orders.PENDING_PAYMENT,time);
        if (orders!=null){
            for(Orders o : orders){
                o.setStatus(Orders.CANCELLED);
                o.setCancelReason("订单超时，自动取消");
                o.setCancelTime(LocalDateTime.now());
                orderMapper.update(o);
            }
        }
    }

    /**
     * 凌晨1点处理未完成订单
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void processDeliveryOrder(){
        log.info("定时处理派送中订单():{}",LocalDateTime.now());
        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);
        List<Orders> ordersList = orderMapper.selectByStatusAndTime(Orders.DELIVERY_IN_PROGRESS,time);
        if(ordersList!=null){
            for(Orders o : ordersList){
                o.setStatus(Orders.COMPLETED);
                orderMapper.updateStatus(o);
            }
        }
    }
}
