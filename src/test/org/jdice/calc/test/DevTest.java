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

import java.math.BigDecimal;

import org.jdice.calc.Calc;
import org.jdice.calc.Num;
import org.junit.Test;

import static org.junit.Assert.*;

public class DevTest {

    @Test
    public void test() throws Exception {
        Num n3a = new Num(3.10);
        System.out.println(n3a.toString());
        assertEquals("3.1", n3a.toString());

        Num n3b = new Num("3.10");
        System.out.println(n3b.toString());
        assertEquals("3.10", n3b.toString());

    }

}
