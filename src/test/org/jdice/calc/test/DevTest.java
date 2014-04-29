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
import org.jdice.calc.Num;
import org.junit.Test;

public class DevTest {

    @Test
    public void test() throws Exception {
        Calculator calc1b = Calculator.builder("5+9/6").mul(3).div(2);
        Num cvb = calc1b.calculate(true, false);
        // print calculation steps
         for(String step : calc1b.getCalculationSteps())
             System.out.println("   " + step);

    }

    
    
    
    
    public static void main (String [] args) throws Exception {
        DevTest ndt = new DevTest();
        ndt.test();
    }
}
