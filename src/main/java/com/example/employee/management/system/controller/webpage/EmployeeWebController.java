package com.example.employee.management.system.controller.webpage;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmployeeWebController {

    @GetMapping("/employees")
    public String employeesPage(){
        return "employees";
    }
}
