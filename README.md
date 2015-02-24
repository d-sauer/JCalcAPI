# JCalc API

Fluent Java API for easier writing formula and calculations in Java.
<br/>
For more detail, features, documentation and examples visit project [homepage](http://www.jdice.org "JCalc homepage").

## Feature:
    * Simplify calculation by reducing code size and provide beter readability
    * Small size (jar < 100KB)
    * Fast evaluation (converted in postfix notation)
    * High precision (using BigDecimal)
    * Includes common math functions and operations
    * Support for strings and parsing formula
    * Support for assignment expressions (x = 5)
    * Extendable through user defined functions and operators
    * Support for custom type conversion
    * Basic configuration throught properties file
    * Fluent API
    * Overview of calculation steps
    * Easiest exception handling with equation overview
    * Java 1.6 compatible
    * Apache 2.0 License
    * More detail overview on project [homepage](http://www.jdice.org "JCalc Homepage")

## Example
If we want to calculate fixed monthly payment for a fixed rate mortgage in Java, <br/>using equation  c = (r * P) / (1 - (1 + r)^(-N))

In **plain Java** we would write those *15 LoC*:
```java
BigDecimal interestRate = new BigDecimal("6.5");    // fixed yearly interest rate in %
BigDecimal P = new BigDecimal(200000);
BigDecimal paymentYears = new BigDecimal(30);

// monthly interest rate => 6.5 / 12 / 100 = 0.0054166667
BigDecimal r = interestRate.divide(new BigDecimal(12), 10, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);

// numerator
// => 0.005416666 * 200000 = 1083.3333400000
BigDecimal numerator = r.multiply(P);

// denominator
r = r.add(new BigDecimal(1));   // => 1.0054166667
BigDecimal pow = new BigDecimal(30 * 12);   // N = 30 * 12

// => 1.0054166667 ^ (-30 * 12)  ===> 1 / 1.005416666 ^ (30 * 12) 
BigDecimal one = BigDecimal.ONE;
BigDecimal r_pow = r.pow(pow.intValue());   // => 1.0054166667 ^ 360 = 6.99179805731691416804....
r_pow = one.divide(r_pow, 10, BigDecimal.ROUND_HALF_UP);  // => 1 / 6.991798.. = 0.1430247258

// => 1 - 0.1430247258 =  0.8569752742
BigDecimal denominator = new BigDecimal(1);
denominator = denominator.subtract(r_pow);

// => 1083.3333400000 / 0.8569752742 = 1264.1360522464
BigDecimal c = numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_UP);

c = c.setScale(2, BigDecimal.ROUND_HALF_UP);

System.out.println("c = " + c);
```


<br/>
If we write *same* equation **with JCalc**, code look more readable and shorter (*8 LoC*)
```java
Num interestRate = new Num(6.5);    // fixed yearly interest rate in %
Num P = new Num(200000);
Num paymentYears = new Num(30);

// monthly interest rate : r = 6.5 / 100 / 12
Num r = Calculator.builder().openBracket().val(interestRate).div(100).closeBracket().div(12).calculate();

// N = 30 * 12 * -1
Num N = Calculator.builder().val(paymentYears).mul(12).mul(-1).calculate();

// c = (r * P) / (1 / (1 + r)^N
Calculator c = new Calculator()     
                .openBracket()
                    .val(r).mul(P)
                .closeBracket()      //    numerator
                .div()               // ---------------
                .openBracket()       //    denumerator
                    .val(1).sub().openBracket().val(1).add(r).closeBracket().pow(N)
                .closeBracket();

Num result = c.calculate().setScale(2);

System.out.println("c = " + result);
```

or even shorter (*6 LoC*):
```java
Num interestRate = new Num("A", 6.5);
Num P = new Num("B", 200000);
Num paymentYears = new Num("C", -30);

Calculator c = Calculator.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * 12)))", interestRate, P, paymentYears);

Num result = c.calculate();

System.out.println("c = " + result.setScale(2));
```

## Releases:
    * 2014.05.10 - 0.4 beta
    * 2014.04.21 - 0.3 beta
    * 2014.04.11 - 0.2 beta
    * 2014.03.24 - First beta release

## Roadmap:
    * Support for Java 8
    * Add new functions (like sum, avg, min, max)
