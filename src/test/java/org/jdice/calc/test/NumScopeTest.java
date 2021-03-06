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

import org.jdice.calc.Calculator;
import org.jdice.calc.Num;
import org.junit.Test;
import static org.junit.Assert.*;

public class NumScopeTest {
    
    
    
    /**
     * Scopes propagation
     *   1. properties on Calculator/Num instance by set methods 
     *   2. Global - defined in properties file 'jcalc.properties' in [bin/class] root
     *   3. Default properties from Properties class
     *   
     * @throws Exception
     */
	@Test
    public void testScope1() throws Exception {
        Calculator calc1 = new Calculator();
        Num num1 = new Num();
        Num num2 = new Num();

        calc1.setScale(2).setDecimalSeparator(',');
        num1.set("2.87634567");
        num2.set("5.12234868");
        
        calc1.val(num1).mul(num2);
        
        Num cn1 = calc1.calculate(); // inherit properties from calc
        assertEquals("14,73", cn1.toString());
        
        //
        //
        Calculator calc2 = new Calculator();
        calc2.val(num1).mul(num2);

        Num cn2 = calc2.calculate(); // use default scope (maximum)
        assertTrue(cn2.toString().startsWith("14.7336454459482156"));

        //
        //
        Calculator calc3 = new Calculator();
        calc3.val(num1.setScale(1)).mul(num2.setScale(1));
        
        Num cn3 = calc2.setTracingSteps(true).calculate(); 	// calculate with scaled numbers
//        for(String s : calc2.getCalculationSteps())
//            System.out.println(s);
        assertEquals("14.79", cn3.setScale(2).toString());
    }
    

}
