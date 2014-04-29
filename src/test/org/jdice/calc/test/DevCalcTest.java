package org.jdice.calc.test;

import java.math.BigDecimal;
import java.text.ParseException;

import org.jdice.calc.Calc;
import org.jdice.calc.Num;
import org.junit.Test;
import static org.junit.Assert.*;

public class DevCalcTest {

    public static void example_1A() {
        System.out.println(2.00 - 1.10);
        
        
        // JCalc way...
        System.out.println(Calc.builder().val(2.00).sub(1.10).calculate());
    }

    public static void example_1B() {
        BigDecimal payment = new BigDecimal(2.00);
        BigDecimal cost = new BigDecimal(1.10);
        System.out.println(payment.subtract(cost));
        
        Num p = new Num(2.00);
        Num c = new Num(1.10);
        System.out.println(Calc.builder().val(p).sub(c).calculate());
    }

    public static void example_1C() throws ParseException {
        BigDecimal payment = new BigDecimal("2.00");
        BigDecimal cost = new BigDecimal("1.10");
        System.out.println(payment.subtract(cost));
        
        Num p = new Num("2.00");
        Num c = new Num("1.10");
        System.out.println(Calc.builder().val(p).sub(c).setStripTrailingZeros(false).calculate());

        System.out.println(Calc.builder("2.00 - 1.10").setStripTrailingZeros(false).calculate());
    }
    
    public static BigDecimal example_2A() {
        BigDecimal interestRate = new BigDecimal("6.5");
        BigDecimal P = new BigDecimal(200000);
        BigDecimal paymentYears = new BigDecimal(30);
        
        // => 6.5 / 12 / 100 = 0.0054166667
        BigDecimal r = interestRate.divide(new BigDecimal(12), 10, BigDecimal.ROUND_HALF_UP).divide(new BigDecimal(100), 10, BigDecimal.ROUND_HALF_UP);
        
        // => 0.005416666 * 200000 = 1083.3333400000
        BigDecimal numerator = r.multiply(P);
        
        // denominator
        r = r.add(new BigDecimal(1));   // => 1.0054166667
        BigDecimal pow = new BigDecimal(30 * 12);
        
        // => 1.005416666 ^ (-30 * 12)  ===> 1 / 1.005416666 ^ (30 * 12) 
        BigDecimal one = BigDecimal.ONE;
        BigDecimal r_pow = r.pow(pow.intValue());   // => 1.0054166667 ^ 360 = 6.991798057316914168045...
        r_pow = one.divide(r_pow, 10, BigDecimal.ROUND_HALF_UP);  // => 1 / 6.991798.. = 0.1430247258
       
        // => 1 - 0.1430247258 =  0.8569752742
        BigDecimal denominator = new BigDecimal(1);
        denominator = denominator.subtract(r_pow);
        
        // => 1083.3333400000 / 0.8569752742 = 1264.1360522464
        BigDecimal result = numerator.divide(denominator, 10, BigDecimal.ROUND_HALF_UP);
        
        result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        System.out.println(result);

        return result;
    }
    
    
   
    public static BigDecimal example_2B() throws ParseException {
        Num interestRate = new Num(6.5);
        Num P = new Num(200000);
        Num paymentYears = new Num(30);
        
        // r = 6.5 / 100 / 12
        Num r = Calc.builder().val(interestRate).div(100).div(12).calculate();
        
        // N = 30 * 12 -1
        Num N = Calc.builder().val(paymentYears).mul(12).mul(-1).calculate();
        
        // c = (r * P) / (1 / (1 + r)^N
        Calc c = new Calc()
                        .openBracket()
                            .val(r).mul(P)
                        .closeBracket()
                        .div()
                        .openBracket()
                            .val(1).sub().openBracket().val(1).add(r).closeBracket().pow(N)
                        .closeBracket();
        
        Num result = c.calculate().setScale(2);
        
        System.out.println(result);
        return result.toBigDecimal();
    }
    
    // TODO: NOT WORKING : http://en.wikipedia.org/wiki/Mortgage_calculator
    public static BigDecimal example_2C() throws ParseException {
        Num interestRate = new Num("A", 6.5);
        Num P = new Num("B", 200000);
        Num paymentYears = new Num("C", -30);
        
        Calc calcMP = Calc.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * 12)))", interestRate, P, paymentYears);
        calcMP.setScale(2);
        
        Num monthlyPayment = calcMP.calculate(true, false);
        
        for(String step : calcMP.getCalculationSteps())
            System.out.println(step);
        
        System.out.println("monthlyPayment: " + monthlyPayment);

        return monthlyPayment.toBigDecimal();
    }

    public static BigDecimal example_2D() throws ParseException {
        Num interestRate = new Num("A", 6.5);
        Num loan = new Num("B", 200000);
        Num paymentYears = new Num("C", 30);
        Num monthlyPayment = new Num();
        
        //Radi: C * -12
//        Calc calcMP = Calc.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * -12)))", interestRate, loan, paymentYears); 
        
        // Ne radi: -C * 12
        Calc calcMP = Calc.builder("((A / 100 / 12) * B) / (1 - ((1 + (A / 100 / 12)) ^ (C * 12)))", interestRate, loan, paymentYears);
//        Calc calcMP = Calc.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-30 * 12)))"); // 6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -30 12 * ^ - /
//        Calc calcMP = Calc.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-360)))");  // 6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -360 ^ - /
        
//        Calc calcMP = Calc.builder("-2 * 3");
        System.out.println(calcMP.getInfix());
        System.out.println(calcMP.getPostfix());
        calcMP.setScale(5);
        monthlyPayment = calcMP.calculate();
        System.out.println(monthlyPayment);
        
        return monthlyPayment.toBigDecimal();
    }

    
    @Test
    public void testMortageCalculator() throws Exception {
        BigDecimal result1 = example_2A();
        BigDecimal result2 = example_2B();
        
        assertEquals(result1, result2);
        
    }
    
    public static void main(String [] args ) throws ParseException {
        example_2A();
        example_2B();
        example_2C();
    }
    
}
