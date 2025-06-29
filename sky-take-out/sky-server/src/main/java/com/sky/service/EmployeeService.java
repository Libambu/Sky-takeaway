package com.sky.service;

import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.result.PageResult;

public interface EmployeeService {

    /**
     * 员工登录
     * @param employeeLoginDTO
     * @return
     */
    Employee login(EmployeeLoginDTO employeeLoginDTO);

    //新增员工
    public void addEmployee(EmployeeDTO employee);
    //分页查询
    public PageResult pageQuery(EmployeePageQueryDTO employeeDTO);

    void startOrStop(Integer status, Long id);

    Employee getById(Long id);

    void update(Employee employee);
}
