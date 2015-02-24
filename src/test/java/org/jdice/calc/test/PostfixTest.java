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

import org.jdice.calc.Calculator;
import org.junit.Test;


public class PostfixTest {

    @Test
    public void test() throws Exception {
        Calculator calc1 = Calculator.builder("(5 + 9 / 6 * 3 / 2) / (5 + 15 - 18)");
        String p  = calc1.getPostfix();
        assertEquals("5 9 6 / 3 * 2 / + 5 15 + 18 - /", p);

        calc1 = Calculator.builder("3 * 4 + 5");
        p  = calc1.getPostfix();
        assertEquals("3 4 * 5 +", p);

        calc1 = Calculator.builder("3 * (4 + 5) / 2");
        p  = calc1.getPostfix();
        assertEquals("3 4 5 + * 2 /", p);
        
        calc1 = Calculator.builder("(3 + 4) / (5 - 2)");
        p  = calc1.getPostfix();
        assertEquals("3 4 + 5 2 - /", p);
        
        calc1 = Calculator.builder("7 - (2 * 3 + 5) * (8 - 4 / 2)");
        p  = calc1.getPostfix();
        assertEquals("7 2 3 * 5 + 8 4 2 / - * -", p);

        calc1 = Calculator.builder("3-2+1");
        p  = calc1.getPostfix();
        assertEquals("3 2 - 1 +", p);
    }

    @Test
    public void test2() throws Exception {
      Calculator c1 = Calculator.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-30 * 12)))");
      String p = c1.getPostfix();
      assertEquals("6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -30 12 * ^ - /", p);
        
      Calculator c2 = Calculator.builder("((6.5 / 100 / 12) * 200000) / (1 - ((1 + (6.5 / 100 / 12)) ^ (-360)))");
      p = c2.getPostfix();
      assertEquals("6.5 100 / 12 / 200000 * 1 1 6.5 100 / 12 / + -360 ^ - /", p);
    }
    
}
