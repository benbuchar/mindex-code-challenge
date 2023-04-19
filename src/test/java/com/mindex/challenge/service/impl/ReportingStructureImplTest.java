package com.mindex.challenge.service.impl;

import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReportingStructureImplTest {

    private String employeeUrl;
    private String reportingStructureEmployeeIdUrl;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Before
    public void setup() {
        employeeUrl = "http://localhost:" + port + "/employee";
        reportingStructureEmployeeIdUrl = "http://localhost:" + port + "/reportingstructure/{id}";
    }

    @Test
    public void testRead () {

        Employee testEmployee4 = new Employee();
        testEmployee4.setFirstName("Ben");
        testEmployee4.setLastName("Doe");
        testEmployee4.setDepartment("Engineering");
        testEmployee4.setPosition("Developer");

        Employee testEmployee3 = new Employee();
        testEmployee3.setFirstName("Jane");
        testEmployee3.setLastName("Doe");
        testEmployee3.setDepartment("Engineering");
        testEmployee3.setPosition("Developer");
        Employee createdEmployee4 = restTemplate.postForEntity(employeeUrl, testEmployee4, Employee.class).getBody();
        Employee createdEmployee3 = restTemplate.postForEntity(employeeUrl, testEmployee3, Employee.class).getBody();

        Employee testEmployee2 = new Employee();
        testEmployee2.setFirstName("Joe");
        testEmployee2.setLastName("Doe");
        testEmployee2.setDepartment("Engineering");
        testEmployee2.setPosition("Developer");
        testEmployee2.setDirectReports(new ArrayList<>(Arrays.asList(new Employee[]{createdEmployee3, createdEmployee4})));
        Employee createdEmployee2 = restTemplate.postForEntity(employeeUrl, testEmployee2, Employee.class).getBody();

        Employee testEmployee1 = new Employee();
        testEmployee1.setFirstName("Marco");
        testEmployee1.setLastName("Doe");
        testEmployee1.setDepartment("Engineering");
        testEmployee1.setPosition("Developer");
        Employee createdEmployee1 = restTemplate.postForEntity(employeeUrl, testEmployee1, Employee.class).getBody();

        Employee testEmployee = new Employee();
        testEmployee.setFirstName("John");
        testEmployee.setLastName("Doe");
        testEmployee.setDepartment("Engineering");
        testEmployee.setPosition("Developer");
        testEmployee.setDirectReports(new ArrayList<>(Arrays.asList(new Employee[]{createdEmployee1, createdEmployee2})));
        Employee createdEmployee = restTemplate.postForEntity(employeeUrl, testEmployee, Employee.class).getBody();

        ReportingStructure testReportingStructure = new ReportingStructure();
        testReportingStructure.setEmployee(createdEmployee);
        testReportingStructure.setNumberOfReports(4);

        // Read checks
        ReportingStructure readReportingStructure = restTemplate.getForEntity(reportingStructureEmployeeIdUrl,
                ReportingStructure.class, testReportingStructure.getEmployee().getEmployeeId()).getBody();
        assertNotNull(readReportingStructure);
        assertNotNull(readReportingStructure.getEmployee());
        assertEquals(testReportingStructure.getEmployee().getEmployeeId(), readReportingStructure.getEmployee().getEmployeeId());
        assertReportingStructureEquivalence(testReportingStructure, readReportingStructure);
    }

    private static void assertReportingStructureEquivalence(ReportingStructure expected, ReportingStructure actual) {
        assertEquals(expected.getEmployee().getEmployeeId(), actual.getEmployee().getEmployeeId());
        assertEquals(expected.getNumberOfReports(), actual.getNumberOfReports());
    }
}
