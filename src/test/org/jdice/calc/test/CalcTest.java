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

import org.jdice.calc.Calc;
import org.jdice.calc.CalcTrig;
import org.jdice.calc.Num;
import org.junit.Test;

public class CalcTest {

    @Test
    public void testBrackets() throws Exception {
        Calc calc = new Calc().val(10).add(20).sub(5);
        Num cv = calc.calc();
        assertEquals("10 + 20 - 5 = 25", 25, cv.toBigDecimal().intValue());

        calc = new Calc().val(10).add(20).sub(5).div(2).mul(3).add(8);
        cv = calc.calc();
        assertEquals("10 + 20 - 5 / 2 * 3 + 8 = 30.5", "30.5", calc.getCalculated().toString());
        assertTrue(cv != calc.getCalculated()); // check if references are immutable

        calc = new Calc().openBracket().openBracket(10).add(5).closeBracket().closeBracket();
        cv = calc.calc();
        assertEquals("Calculate", 15, cv.toBigDecimal().intValue());

        calc = new Calc().openBracket(10).add(5).closeBracket().add().openBracket().openBracket(5).add(2).closeBracket().closeBracket().sub(4);
        cv = calc.calc();
        assertEquals("( 10 + 5 ) + ( ( 5 + 2 ) ) - 4  = 18", 18, cv.toBigDecimal().intValue());

        calc = new Calc().openBracket(10).add(5).closeBracket().add().openBracket().openBracket(5).add(2).closeBracket().closeBracket().sub(4).pow(2);
        cv = calc.calc();
        assertEquals("((10+5)+((5+2))-4^2)", 6, cv.toBigDecimal().intValue());

    }

    @Test
    public void testEquations() throws Exception {
        Calc calc1b = Calc.builder("5+9/6").mul(3).div(2);
        Num cvb = calc1b.calcWithSteps(true);
        assertEquals("5 + 9 / 6 * 3 / 2", 7, cvb.intValue());
        // print calculation steps
        // for(String step : calc1b.getCalculationSteps())
        // System.out.println("   " + step);

        Calc calc1 = Calc.builder("5+9/6").mul(3).div(2);
        Num cv = calc1.calc();
        assertEquals("5 + 9 / 6 * 3 / 2", 7, cv.intValue());

        Num x = new Num("x", 10);
        Num y = new Num("y", 3);
        Calc calc2 = Calc.builder().equation("5 + x - y", x, y).add(2);
        Num cv2 = calc2.calc();
        assertEquals("5 + 10 - 3 + 2", 14, cv2.intValue());

        Calc calc3 = Calc.builder().append(calc1).add().append(calc2);
        Num cv3 = calc3.calc();
        assertEquals("5 + 9 / 6 * 3 / 2  +  5 + 10 - 3 + 2", 21, cv3.intValue());

        Calc calc4 = Calc.builder("((1+2) + (5 + 3))");
        assertEquals(11, calc4.calc().intValue());

        Calc calc5 = Calc.builder(" 2 % 5");
        Num cv4 = calc5.calc();
        assertEquals("2 % 5", 2, cv4.intValue());
    }

    @Test
    public void testResultWithExcelResult() throws Exception {
        Calc calc1 = Calc.builder("5+9/6").mul(3).div(2);

        Num x = new Num("x", 10);
        Num y = new Num("y", 3);
        Calc calc2 = Calc.builder().equation("5 + x - y", x, y);
        Calc calc3 = Calc.builder().append(calc1).divide().append(calc2);
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
            Num cv = calc1.calc();
            assertTrue("5 + 9 / 6 * 3 / 2", cv.sameAs("7.25"));

            x.set(5 + i);
            y.set(2 * i);
            Num cv2 = calc2.calc();

            int c2 = 5 + (5 + i) - (2 * i);
            assertTrue("5 + (" + (5 + i) + ") - (" + (2 * i) + ")=" + c2 + "  == " + calc2.getInfix() + "=" + cv2.toString(), cv2.sameAs(c2));

            if (cv2.sameAs(0))
                continue;
            Num cv3 = calc3.calc();
            assertEquals(excelResult.get(calc3.getInfix()), cv3.toString(','));
        }
    }

    @Test
    public void testSteps() throws Exception {
        Calc calc1 = Calc.builder("(5 + 9 / 6 * 3 / 2) / (5 + 15 - 18)").add().sqrt(4);
        // System.out.println(calc1.getInfix());
        Num v = calc1.calcWithSteps(false);

        StringBuilder sb = new StringBuilder();
        for (String p : calc1.getCalculationSteps())
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
    public void testEquations2() throws Exception {
        calculateTest(79.71d, 8310.00d);
        calculateTest(48.34d, 8310.00d);
        calculateTest(70.98d, 8310.00d);
    }

    private static void calculateTest(double cost, double costM2) throws Exception {
        double CONST_III = 2541.956123372554d;

        Calc cSum = new Calc(cost).mul(costM2).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num sum = cSum.calc();

        Calc cI = new Calc(sum).mul(0.15);

        Num valueI = cI.calc();

        Calc cIII = new Calc(CONST_III).mul(cost).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num valueIII = cIII.calc();

        Calc cII = new Calc(valueIII).sub(valueI).setDecimalSeparator(',').setGroupingSeparator(' ');
        Num valueII = cII.calc();

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
        Calc calc = new Calc().val(10).add(20).sub(5);
        calc.calc();
        assertEquals("10 + 20 - 5", calc.getInfix());
        assertEquals("25", calc.getCalculated().toString());
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        calc.bind(CalcTrig.class).add().sin(10).add(2);
        calc.calc();
        assertEquals("10 + 20 - 5 + sin(10) + 2", calc.getInfix());
        assertEquals("26.456", calc.getCalculated().setScale(3).toString());
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        calc.bind(CalcTrig.class).add().sin(5);
        calc.calc();
        assertEquals("10 + 20 - 5 + sin(10) + 2 + sin(5)", calc.getInfix());
        assertTrue(calc.getCalculated().setScale(3).sameAs("25.497"));
        // System.out.println(calc.getInfix() + " = " + calc.getCalculated());

        Calc calc2 = Calc.builder("1+2+ A +abs(-2 - (abs(A-10)))", 5);
        Num num = calc2.calc();
        assertEquals("15", num.toString());
    }
}
