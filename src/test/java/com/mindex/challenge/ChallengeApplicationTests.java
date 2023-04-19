package com.mindex.challenge;

import com.mindex.challenge.dao.EmployeeRepository;
import com.mindex.challenge.data.Compensation;
import com.mindex.challenge.data.Employee;
import com.mindex.challenge.data.ReportingStructure;
import com.mindex.challenge.service.ReportingStructureService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChallengeApplicationTests {
	private String compensationUrl;
	private String employeeIdUrl;
	private String compensationEmployeeIdUrl;
	private String reportingStructureEmployeeIdUrl;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Before
	public void setup() {
		compensationUrl = "http://localhost:" + port + "/compensation";
		compensationEmployeeIdUrl = "http://localhost:" + port + "/compensation/{id}";
		employeeIdUrl = "http://localhost:" + port + "/employee/{id}";
		reportingStructureEmployeeIdUrl = "http://localhost:" + port + "/reportingstructure/{id}";
	}

	@Test
	public void contextLoads() {
	}

	@Test
	public void test() {
		//------reporting structure challenge tests------
		//reporting structure read tests
		ReportingStructure reportingStructure = restTemplate.getForEntity(reportingStructureEmployeeIdUrl,
				ReportingStructure.class, "16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();
		assertNotNull(reportingStructure);
		assertEquals("John", reportingStructure.getEmployee().getFirstName());
		assertEquals("Lennon", reportingStructure.getEmployee().getLastName());
		assertEquals((Integer)4, reportingStructure.getNumberOfReports());


		//------compensation challenge tests------
		Employee readEmployee = restTemplate.getForEntity(employeeIdUrl, Employee.class,
				"16a596ae-edd3-4847-99fe-c4518e82c86f").getBody();

		Compensation testCompensation = new Compensation();
		testCompensation.setEmployee(readEmployee);
		testCompensation.setSalary("100M");
		testCompensation.setEffectiveDate(LocalDateTime.now().toString());

		//compensation post tests
		Compensation createdCompensation = restTemplate.postForEntity(compensationUrl, testCompensation, Compensation.class).getBody();
		assertNotNull(createdCompensation);
		assertEquals(testCompensation.getEmployee().getEmployeeId(), createdCompensation.getEmployee().getEmployeeId());
		assertEquals("John", createdCompensation.getEmployee().getFirstName());
		assertEquals("Lennon", createdCompensation.getEmployee().getLastName());
		assertEquals("100M", createdCompensation.getSalary());
		assertEquals(testCompensation.getEffectiveDate(), createdCompensation.getEffectiveDate());

		//compensation read tests
		Compensation readCompensation = restTemplate.getForEntity(compensationEmployeeIdUrl, Compensation.class,
				createdCompensation.getEmployee().getEmployeeId()).getBody();
		assertNotNull(readCompensation);
		assertEquals(createdCompensation.getEmployee().getEmployeeId(), readCompensation.getEmployee().getEmployeeId());
		assertEquals("John", readCompensation.getEmployee().getFirstName());
		assertEquals("Lennon", readCompensation.getEmployee().getLastName());
		assertEquals("100M", readCompensation.getSalary());
		assertEquals(createdCompensation.getEffectiveDate(), readCompensation.getEffectiveDate());
	}

}
