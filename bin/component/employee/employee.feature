Feature: save employee 

  @employee
  Scenario: save employee details
    Given I have employee details
    When I save employee details
    Then I see saved employee details
    
