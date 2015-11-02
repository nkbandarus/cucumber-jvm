package component.employee;

import static org.junit.Assert.assertEquals;

import com.nisum.service.Addition;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class AdditionSteps extends ComponentBase{
	
	private int input1;
	private int input2;
	private int actualOutput;
	
	private Addition addition;
	
	public void initDependencyReferences() {
		addition = servlet.getBean(Addition.class);
	}
	
	@Given("^the input \"(.*?)\" and \"(.*?)\"$")
	public void the_input_and(String arg1, String arg2) throws Throwable {
		input1 = Integer.valueOf(arg1);
		input2 = Integer.valueOf(arg2);
	}

	@When("^the calculator is run$")
	public void the_calculator_is_run() throws Throwable {
		actualOutput = addition.add(input1, input2);
	}

	@Then("^the output should be \"(.*?)\"$")
	public void the_output_should_be(String output) throws Throwable {
		int expectedOutput = Integer.valueOf(output);
		assertEquals(expectedOutput, actualOutput);
	}
}
