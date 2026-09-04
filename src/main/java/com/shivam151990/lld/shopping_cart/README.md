Problem Statement:

Create an API that receives a shopping cart as input and returns true or false 
based on whether the cart meets certain rules.

Details:

* The shopping cart contains multiple products, each with a category and quantity.
* There are predefined rules (criteria) for each product category. 
  For example, a rule might state that the maximum quantity allowed for a certain category is 5.
* The API should check the cart against these rules:
  * If any product's quantity exceeds the allowed maximum for its category, the API returns false.
  * Otherwise, it returns true.
     Example:

Cart: [{category: "electronics", quantity: 3}, {category: "clothing", quantity: 6}]
Rules: {electronics: max 5, clothing: max 5}
Since clothing quantity is 6 (exceeds max 5), API returns false.