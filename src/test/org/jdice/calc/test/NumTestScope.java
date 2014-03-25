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

import org.jdice.calc.Calc;
import org.jdice.calc.Num;
import org.junit.Test;
import static org.junit.Assert.*;

public class NumTestScope {
    
    
    
    /**
     * Scopes propagation
     *   1. properties on Calc/Num instance by set methods 
     *   2. Global - defined in properties file 'jcalc.properties' in [bin/class] root
     *   3. Default properties from Properties class
     *   
     * @throws Exception
     */
	@Test
    public void testScope1() throws Exception {
        Calc calc1 = new Calc();
        Num num1 = new Num();
        Num num2 = new Num();

        calc1.setScale(2).setDecimalSeparator(',');
        num1.set("2.87634567");
        num2.set("5.12234868");
        
        calc1.val(num1).mul(num2);
        
        Num cn1 = calc1.calc(); // inherit properties from calc
        assertEquals("14,73", cn1.toString());
        
        //
        //
        Calc calc2 = new Calc();
        calc2.val(num1).mul(num2);

        Num cn2 = calc2.calc(); // use default scope (maximum)
        assertTrue(cn2.toString().startsWith("14.7336454459482156"));

        //
        //
        Calc calc3 = new Calc();
        calc3.val(num1.setScale(1)).mul(num2.setScale(1));
        
        Num cn3 = calc2.calc(); 	// calculate with scaled numbers
        assertEquals("14.79", cn3.toString());
    }
    

}
