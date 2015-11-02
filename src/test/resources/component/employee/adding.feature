Feature: Adding
 
@adding
Scenario Outline: Add two numbers
    Given the input "<input1>" and "<input2>"
    When the calculator is run
    Then the output should be "<output>"
 
    Examples:
        | input1 | input2 | output |
        | 2      | 2      |   4    |
        | 98     | 1      |   99   |
        | 0      | 0      |   0    |