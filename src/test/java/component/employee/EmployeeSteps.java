package component.employee;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nisum.domain.Employee;
import com.nisum.service.EmployeeService;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class EmployeeSteps extends ComponentBase {
    private EmployeeService employeeService ;
    Employee expectedEmployee = new Employee();
    

    ObjectMapper jacksonObjectMapper;
    
    @Override
    public void initDependencyReferences() {
    	employeeService = servlet.getBean(EmployeeService.class);
    	jacksonObjectMapper = servlet.getBean("jacksonObjectMapper");
    };
    
    @Given("^I have employee details$")
    public void setup_employee()  {
    	expectedEmployee.setEmpId("1134");
    	expectedEmployee.setEmployeeName("Naresh");
    }

    @When("^I save employee details$")
    public void save_employee() {
    	employeeService.save(expectedEmployee);
    }

    @Then("^I see saved employee details$")
    public void verify_employee_details() throws JsonParseException, JsonMappingException, UnsupportedEncodingException, IOException {
    	MockHttpServletResponse httpServletResponse = servlet.get("/employee/"+1134);
    	
        Employee actualEmployee = jacksonObjectMapper.readValue(httpServletResponse.getContentAsString(), Employee.class);
    	
    	//Employee actualEmployee = employeeService.getEmployeeById("123");
        assertEquals(expectedEmployee.getEmpId(), actualEmployee.getEmpId());
        assertEquals(expectedEmployee.getEmployeeName(), actualEmployee.getEmployeeName());
    }
}