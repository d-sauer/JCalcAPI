/*
 * Copyright 2014 Davor Sauer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jdice.calc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.jdice.calc.Calculator;
import org.jdice.calc.Num;
import org.jdice.calc.Step;
import org.jdice.calc.TrigCalculator;
import org.junit.Test;

public class CalculatorTest {

    @Test
    public void testBrackets() throws Exception {
        Calculator calc = new Calculator().val(10).add(20).sub(5);
        Num cv = calc.calculate();
        assertEquals("10 + 20 - 5 = 25", 25, cv.toBigDecimal().intValue());

        calc = new Calculator().val(10).add(20).sub(5).div(2).mul(3).add(8);
        cv = calc.calculate();
        assertEquals("10 + 20 - 5 / 2 * 3 + 8 = 30.5", "30.5", calc.getCalculatedValue().toString());
        assertTrue(cv != calc.getCalculatedValue()); // check if references are immutable

        calc = new Calculator().openBracket().openBracket().val(10).add(5).closeBracket().closeBracket();
        cv = calc.calculate();
        assertEquals("Calculate", 15, cv.toBigDecimal().intValue());

        calc = new Calculator().openBracket().val(10).add(5).closeBracket().add().openBracket().openBracket().val(5).add(2).closeBracket().closeBracket().sub(4);
        cv = calc.calculate();
        assertEquals("( 10 + 5 ) + ( ( 5 + 2 ) ) - 4  = 18", 18, cv.toBigDecimal().intValue());

        calc = new Calculator().openBracket().val(10).add(5).closeBracket().add().openBracket().openBracket().val(5).add(2).closeBracket().closeBracket().sub(4).pow(2);
        cv = calc.calculate();
        assertEquals("((10+5)+((5+2))-4^2)", 6, cv.toBigDecimal().intValue());

    }

    @Test
    public void testExpressions() throws Exception {
        Calculator calc1b = Calculator.builder("5+9/6").mul(3).div(2);
        Num cvb = calc1b.setTracingSteps(true).calculate();
        assertEquals("5 + 9 / 6 * 3 / 2", 7, cvb.intValue());
        // print calculation steps
        // for(String step : calc1b.getCalculationSteps())
        // System.out.println("   " + step);

        Calculator calc1 = Calculator.builder("5+9/6").mul(3).div(2);
        Num cv = calc1.calculate();
        assertEquals("5 + 9 / 6 * 3 / 2", 7, cv.intValue());

        Num x = new Num("x", 10);
        Num y = new Num("y", 3);
        Calculator calc2 = Calculator.builder().expression("5 + x - y", x, y).add(2);
        Num cv2 = calc2.calculate();
        assertEquals("5 + 10 - 3 + 2", 14, cv2.intValue());

        Calculator calc3 = Calculator.builder().expression(calc1, true).add().expression(calc2, true);
        Num cv3 = calc3.calculate();
        assertEquals("5 + 9 / 6 * 3 / 2  +  5 + 10 - 3 + 2", 21, cv3.intValue());

        Calculator calc4 = Calculator.builder("((1+2) + (5 + 3))");
        assertEquals(11, calc4.calculate().intValue());

        Calculator calc5 = Calculator.builder(" 2 % 5");
        Num cv4 = calc5.calculate();
        assertEquals("2 % 5", 2, cv4.intValue());
    }
    
    @Test
    public void testExpressions2() throws Exception {
        Calculator calc1 = Calculator.builder("5 / 9 * 12");
        assertEquals("5 / 9 * 12", calc1.toString());

        Calculator calc2 = Calculator.builder().val(10).add().expression("5 / 9 * 12");
        assertEquals("10 + 5 / 9 * 12", calc2.toString());
    }

    @Test
    public void testResultWithExcelResult() throws Exception {
        Calculator calc1 = Calculator.builder("5+9/6").mul(3).div(2);

        Num x = new Num("x", 10);
        Num y = new Num("y", 3);
        Calculator calc2 = Calculator.builder().expression("5 + x - y", x, y);
        Calculator calc3 = Calculator.builder().expression(calc1, true).div().expression(calc2, true);
        calc3.setScale(9).setDecimalSeparator(',');

        HashMap<String, String> excelResult = new HashMap<String, String>();
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 5 - 0 )", "0,725");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 6 - 2 )", "0,805555556");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 7 - 4 )", "0,90625");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 8 - 6 )", "1,035714286");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 9 - 8 )", "1,208333333");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 10 - 10 )", "1,45");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 11 - 12 )", "1,8125");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 12 - 14 )", "2,416666667");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 13 - 16 )", "3,625");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 14 - 18 )", "7,25");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 16 - 22 )", "-7,25");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 17 - 24 )", "-3,625");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 18 - 26 )", "-2,416666667");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 19 - 28 )", "-1,8125");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 20 - 30 )", "-1,45");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 21 - 32 )", "-1,208333333");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 22 - 34 )", "-1,035714286");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 23 - 36 )", "-0,90625");
        excelResult.put("( 5 + 9 / 6 * 3 / 2 ) / ( 5 + 24 - 38 )", "-0,805555556");

        for (int i = 0; i < 20; i++) {
            Num cv = calc1.calculate();
            assertTrue("5 + 9 / 6 * 3 / 2", cv.isEqual("7.25"));

            x.set(5 + i);
            y.set(2 * i);
            Num cv2 = calc2.calculate();

            int c2 = 5 + (5 + i) - (2 * i);
            assertTrue("5 + (" + (5 + i) + ") - (" + (2 * i) + ")=" + c2 + "  == " + calc2.getInfix() + "=" + cv2.toString(), cv2.isEqual(c2));

            if (cv2.isEqual(0))
                continue;
            Num cv3 = calc3.calculate();
            assertEquals(excelResult.get(calc3.getInfix()), cv3.toString(','));
        }
    }

    @Test
    public void testSteps() throws Exception {
        Calculator calc1 = Calculator.builder("(5 + 9 / 6 * 3 / 2) / (5 + 15 - 18)").add().sqrt(4);
        // System.out.println(calc1.getInfix());
        Num v = calc1.setTracingSteps(true).calculate();

        StringBuilder sb = new StringBuilder();
        for (Step p : calc1.getTracedSteps())
            sb.append(p);

        String test = "9   /  6     = 1.5" +
                "1.5 *  3     = 4.5" +
                "4.5 /  2     = 2.25" +
                "5   +  2.25  = 7.25" +
                "5   +  15    = 20" +
                "20  -  18    = 2" +
                "7.25    /  2     = 3.625" +
                "3.625   +  sqrt(4):2   = 5.625";

        assertEquals(test.replace(" ", ""), sb.toString().replace("\t", "").replace(" ", ""));
    }

    @Test
    public void testexpressions2() throws Exception {
        calculateTest(79.71d, 8310.00d);
        calculateTest(48.34d, 8310.00d);
        calculateTest(70.98d, 8310.00d);
    }

    private static void calculateTest(double cost, double costM2) throws Exception {
        double CONST_III = 2541.956123372554d;

        Calculator cSum = new Calculator().val(cost).mul(costM2).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num sum = cSum.calculate();

        Calculator cI = new Calculator().val(sum).mul(0.15);

        Num valueI = cI.calculate();

        Calculator cIII = new Calculator().val(CONST_III).mul(cost).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num valueIII = cIII.calculate();

        Calculator cII = new Calculator().val(valueIII).sub(valueI).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num valueII = cII.calculate();

        // System.out.println(cSum.getInfix() + "\t=\t" + sum);
        // System.out.println(cI.getInfix() + "\t=\t" + valueI.toString());
        // System.out.println(cII.getInfix() + "\t=\t" + valueII.toString());
        // System.out.println(cIII.getInfix() + "\t=\t" + valueIII.toString());
        //
        //
        // System.out.println("");
    }

    @Test
    public void testBind() throws Exception {
        Calculator calc = new Calculator().val(10).add(20).sub(5);
        calc.calculate();
        assertEquals("10 + 20 - 5", calc.getInfix());
        assertEquals("25", calc.getCalculatedValue().toString());
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        calc.bind(TrigCalculator.class).add().sin(10).add(2);
        calc.calculate();
        assertEquals("10 + 20 - 5 + sin(10) + 2", calc.getInfix());
        assertEquals("26.456", calc.getCalculatedValue().setScale(3).toString());
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        calc.bind(TrigCalculator.class).add().sin(5);
        calc.calculate();
        assertEquals("10 + 20 - 5 + sin(10) + 2 + sin(5)", calc.getInfix());
        assertTrue(calc.getCalculatedValue().setScale(3).isEqual("25.497"));
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        Calculator calc2 = Calculator.builder("1+2+ A +abs(-2 - (abs(A-10)))", 5);
        Num num = calc2.calculate();
        assertEquals("15", num.toString());
    }
}
