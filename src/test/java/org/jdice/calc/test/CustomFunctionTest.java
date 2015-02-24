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

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Calculator;
import org.jdice.calc.Function;
import org.jdice.calc.Num;

import static org.junit.Assert.*;

import org.junit.Test;

public class CustomFunctionTest {
    public static class test implements Function {
        @Override
        public String getSymbol() {
            return "test";
        }

        @Override
        public int getFunctionAttributes() {
            return 2;
        }

        @Override
        public Num calc(AbstractCalculator calc, Num... values) throws Exception {
            Calculator c = Calculator.builder().val(values[1]).mul(values[0]);

            return c.calculate();
        }
    }

    @Test
    public void testCustomFunction() throws Exception {
        String e = "test(1-2-5-2, 3)";
        Calculator c = new Calculator();
        c.use(test.class);
        c.expression(e);
        Num n = c.calculate();

        assertEquals("-24", n.toString());
        
        
        Calculator calc2 = Calculator.builder().val(10).add().function(test.class, 2,3);
        calc2.use(test.class);
        Num n2 = calc2.calculate();
        assertEquals("16", n2.toString());
        
    }
}
