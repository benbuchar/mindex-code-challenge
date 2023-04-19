package com.mindex.challenge.service.impl;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.ArrayList;

@Service
public class ReportingStructureServiceImpl implements ReportingStructureService {

    private static final Logger LOG = LoggerFactory.getLogger(ReportingStructureServiceImpl.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ReportingStructure read(String id) {
        LOG.debug("Creating reporting structure under employee with id [{}]", id);

        Employee employee = employeeRepository.findByEmployeeId(id);

        if (employee == null) {
            throw new RuntimeException("Invalid employeeId: " + id);
        }

        ReportingStructure reportingStructure = new ReportingStructure();
        reportingStructure.setEmployee(employee);

        /**
         * Calculates the number of reports under employee.
         *
         * Breadth-first search of reporting structure under employee.
         *
         * Places root employee in queue, then iteratively pops from the queue
         * and places child nodes in the queue until the queue is empty.
         */
        Employee rootEmployee = employeeRepository.findByEmployeeId(employee.getEmployeeId());
        int numberOfReports = 0;
        ArrayDeque<Employee> queue = new ArrayDeque<>();
        queue.addLast(rootEmployee);
        while(!queue.isEmpty()) {
            Employee front = queue.pop();
            if(front != null && front.getDirectReports() != null) {
                ArrayList<Employee> detailedList = new ArrayList<>();
                for (Employee e : front.getDirectReports()) {
                    numberOfReports++;
                    queue.addLast(employeeRepository.findByEmployeeId(e.getEmployeeId()));
                    detailedList.add(employeeRepository.findByEmployeeId(e.getEmployeeId()));
                }
                front.setDirectReports(detailedList);
            }
        }
        reportingStructure.setEmployee(rootEmployee);
        reportingStructure.setNumberOfReports(numberOfReports);

        return reportingStructure;
    }

}
