package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        // TODO 后期需要进行md5加密，然后再进行比对,（已经实现）
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        System.out.println(password);
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    @Override
    public void addEmployee(EmployeeDTO employee) {
        Employee employee1 = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employee, employee1);
        //设置当前账号的状态，默认正常为1
        employee1.setStatus(StatusConstant.ENABLE);
        //设置密码
        employee1.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));
        //设置当前时间
//        employee1.setCreateTime(LocalDateTime.now());
//        employee1.setUpdateTime(LocalDateTime.now());
//        //设置修改人和创建人id
//        //TODO
//        employee1.setCreateUser(BaseContext.getCurrentId());
//        employee1.setUpdateUser(BaseContext.getCurrentId());
        employeeMapper.addEmployee(employee1);
    }
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        //自动实现分页查询
        PageHelper.startPage(employeePageQueryDTO.getPage(),employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        long total = page.getTotal();
        List<Employee> employees = page.getResult();
        return new PageResult(total,employees);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Employee e = new Employee();
        e.setStatus(status);
        e.setId(id);
        employeeMapper.update(e);
    }

    /**
     * getById
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        Employee e = employeeMapper.getById(id);
        e.setPassword("*****");
        return e;
    }

    @Override
    public void update(Employee employee) {
//        employee.setUpdateTime(LocalDateTime.now());
//        employee.setUpdateUser(BaseContext.getCurrentId());
        employee.setPassword(null);
        employeeMapper.update(employee);
    }
}
