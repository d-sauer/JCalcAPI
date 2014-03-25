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

import org.jdice.calc.CalcFactory;
import org.jdice.calc.Num;
import static org.junit.Assert.*;
import org.junit.Test;

public class CalcFactoryTest {

    
    
    @Test
    public void testPercent() throws Exception {
        Num n = CalcFactory.percentOf(5, 200);
        assertEquals("10", n.toString());
    }

}
