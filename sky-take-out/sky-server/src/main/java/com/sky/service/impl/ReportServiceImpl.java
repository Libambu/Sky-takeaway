package com.sky.service.impl;

import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import io.swagger.annotations.Api;
import io.swagger.models.auth.In;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;

    /**
     * 统计订单信息
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getturnoverStastisc(LocalDate begin, LocalDate end) {

        //记录当前时间的List对象
        List<LocalDate> timeList = new ArrayList<>();
        timeList.add(begin);
        while(!begin.equals(end)){
            begin =  begin.plusDays(1);
            timeList.add(begin);
        }
        //将List转String
        String Stringtime = StringUtils.join(timeList, ",");
        TurnoverReportVO turnoverReportVO = new TurnoverReportVO();
        turnoverReportVO.setDateList(Stringtime);
        //开始在数据库中查金额
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : timeList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            //select sum(amount) from orders where order-time<endTime and order_time>begin_time and status = 5;
            Double turnover =  orderMapper.selectTurnover(beginTime,endTime,5);
            //如果当日无订单会null的
            if(turnover==null){
                turnover=0.0;
            }
            turnoverList.add(turnover);
        }
        String turnover = StringUtils.join(turnoverList, ",");
        turnoverReportVO.setTurnoverList(turnover);
        return turnoverReportVO;
    }

    /**
     * 统计用户信息
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getUserReport(LocalDate begin, LocalDate end) {
        UserReportVO userReportVO = new UserReportVO();
        //记录当前时间的List对象
        List<LocalDate> timeList = new ArrayList<>();
        timeList.add(begin);
        while(!begin.equals(end)){
            begin =  begin.plusDays(1);
            timeList.add(begin);
        }
        //存放结果
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList = new ArrayList<>();
        //将List转String
        String Stringtime = StringUtils.join(timeList, ",");
        userReportVO.setDateList(Stringtime);
        //遍历时间
        for(LocalDate time : timeList){
            LocalDateTime beginTime = LocalDateTime.of(time, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(time,LocalTime.MAX);
            Map map = new HashMap<>();
            map.put("end",endTime);
            Integer totalNum = userMapper.getUserNumByDate(map);
            map.put("begin",beginTime);
            Integer newUserNum = userMapper.getUserNumByDate(map);
            newUserList.add(newUserNum);
            totalUserList.add(totalNum);
        }
        String newUser = StringUtils.join(newUserList, ",");
        String totalUser = StringUtils.join(totalUserList,",");
        userReportVO.setNewUserList(newUser);
        userReportVO.setTotalUserList(totalUser);
        return userReportVO;
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getordersStatistics(LocalDate begin, LocalDate end) {
        OrderReportVO orderReportVO = new OrderReportVO();
        //记录当前时间的List对象
        List<LocalDate> timeList = new ArrayList<>();
        timeList.add(begin);
        while(!begin.equals(end)){
            begin =  begin.plusDays(1);
            timeList.add(begin);
        }
        String timelist = StringUtils.join(timeList, ",");
        orderReportVO.setDateList(timelist);

        List<Integer> totalOrderCount = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for(LocalDate date : timeList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date,LocalTime.MAX);
            //查询date所有订单 select count(id) from orders where create_time between _ and _
            Map map = new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer sum = orderMapper.sumBymap(map);
            totalOrderCount.add(sum);
            //查询date有效订单 select count(id) from orders where create_time between _ and _ and status = _
            map.put("status",5);
            Integer sum2 = orderMapper.sumBymap(map);
            validOrderCountList.add(sum2);
        }
        String totalOrderCountans = StringUtils.join(totalOrderCount,",");
        String validOrderCountListans = StringUtils.join(validOrderCountList,",");
        orderReportVO.setOrderCountList(totalOrderCountans);
        orderReportVO.setValidOrderCountList(validOrderCountListans);

        Integer validsum = 0;
        Integer totalsum = 0;
        for(int i=0;i<totalOrderCount.size();i++){
            validsum += validOrderCountList.get(i);
            totalsum += totalOrderCount.get(i);
        }
        Double orderCompletionRate = 0.0;
        if(totalsum!=0){
            orderCompletionRate = validsum.doubleValue()/totalsum;
        }
        orderReportVO.setValidOrderCount(validsum);
        orderReportVO.setTotalOrderCount(totalsum);
        orderReportVO.setOrderCompletionRate(orderCompletionRate);
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end,LocalTime.MAX);
        List<Top10sqlVO> top10List = orderMapper.top10List(beginTime,endTime);
        List<String> dish_name = new ArrayList<>();
        List<Integer> dish_num = new ArrayList<>();
        for(Top10sqlVO top10sqlVO : top10List){
            dish_name.add(top10sqlVO.getName());
            dish_num.add( top10sqlVO.getNum());
        }
        String nameList = StringUtils.join(dish_name,',');
        String numberList = StringUtils.join(dish_num,',');
        SalesTop10ReportVO salesTop10ReportVO = new SalesTop10ReportVO(nameList,numberList);
        return salesTop10ReportVO;
    }

}
