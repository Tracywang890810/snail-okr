package com.seblong.okr.services;

import com.seblong.okr.entities.Employee;
import com.seblong.okr.entities.Follow;

import java.util.List;

public interface EmployeeService {
    Employee getEmployee(String code, String cookie);

    List<Employee> queryByName(String keyword);

    Employee findById(String fromId);

    Follow follow(Employee from, Employee target);

    void unFollow(String unique);

    List<Follow> getFollows(String employeeId);

    String getAuth2Url();

    String getAccessToken(String corpId);

    Follow getFollow(String employeeId, String target);

    List<Employee> queryUser(String keyword, String corpId);
}
