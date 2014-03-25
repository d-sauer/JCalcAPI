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
import org.jdice.calc.Calc;
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
            Calc c = Calc.builder().val(values[1]).mul(values[0]);

            return c.calc();
        }
    }

    @Test
    public void testCustomFunction() throws Exception {
        String e = "test(1-2-5-2, 3)";
        Calc c = new Calc();
        c.register(test.class);
        c.parse(e);
        Num n = c.calc();

        assertEquals("-24", n.toString());
        
        
        Calc calc2 = Calc.builder().val(10).add().append(test.class, 2,3);
        calc2.register(test.class);
        Num n2 = calc2.calc();
        assertEquals("16", n2.toString());
        
    }
}
