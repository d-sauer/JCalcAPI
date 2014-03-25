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
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.jdice.calc.Cache;
import org.jdice.calc.Calc;
import org.jdice.calc.Num;
import org.jdice.calc.NumConverter;
import org.jdice.calc.Rounding;
import org.junit.Test;

public class NumTest {

    @Test
    public void testSameAs() throws Exception {
        Num A = new Num(5);
        Num B = new Num(5);
        assertTrue(A.sameAs(B));

        A = new Num("5");
        B = new Num("5.0");
        assertTrue(A.sameAs(B));

        A = new Num(5.5295d);
        B = new Num(5.5295);
        assertTrue(A.sameAs(B));

        A = new Num("5.529500");
        B = new Num(5.5295);
        assertTrue(A.sameAs(B));

        A = new Num("5.529500");
        B = new Num(5.529500d);
        assertTrue(A.sameAs(B));

        A = new Num("5.529500");
        B = new Num(5.5295001d);
        assertTrue("Not equal", !A.sameAs(B));

        A = new Num("5.529500");
        B.set(5.529500d);
        assertTrue(A.sameAs(B));

        A = new Num("5.529500");
        B.set(5.529500d);
        assertTrue(A.sameAs("5.5295"));
        
        
        A = new Num("5.529500");
        B = new Num("5.52951258");

        assertTrue(A.sameAs(B, 2));
        
        assertTrue(!A.sameAs(B, 20));	// not same: 5.529512|58| 
        
        assertTrue(A.sameAs(B, 4, Rounding.HALF_DOWN));
        
        
        A = new Num("1254.5848");
        B = new Num("1254.58");
        assertTrue(!A.sameAs(B));
		assertTrue(A.sameAs(B, 2));

		assertTrue(A.sameAs(B, true));
    }

    @Test
    public void testHashCode() throws Exception {
        Num A = new Num(5);
        Num B = new Num(5);
        assertEquals(A.hashCode(), B.hashCode());

        B = new Num(6);
        assertNotEquals(A.hashCode(), B.hashCode());

        A.set(6);
        assertEquals(A.hashCode(), B.hashCode());
    }

    @Test
    public void testRounding() throws Exception {
        String no = "12.825";
        BigDecimal bd1 = new BigDecimal(no);
        bd1 = bd1.setScale(2, BigDecimal.ROUND_CEILING);
        // System.out.println(bd1);

        Num n1 = new Num(no);
        n1.setScale(2, Rounding.CEILING);
        // System.out.println(n1);
        assertEquals(bd1.toString(), n1.toString());

        BigDecimal bd2 = new BigDecimal(no);
        bd2 = bd2.setScale(2, BigDecimal.ROUND_FLOOR);
        // System.out.println(bd2);
        Num n2 = new Num(no);
        n2.setScale(2, Rounding.FLOOR);
        // System.out.println(n2);
        assertEquals(bd2.toString(), n2.toString());

        n2.setScale(2, Rounding.CEILING); // preserve original value and make new scale
        // System.out.println(n2);
        assertEquals(bd1.toString(), n2.toString());
    }

    @Test
    public void testStripString() throws Exception {
        String strip = Num.stripNumber("44,551.06", '.');
        // System.out.println(strip);
        assertEquals("Strip number", "44551.06", strip);

        strip = Num.stripNumber("12 558 44,551.06", '.');
        // System.out.println(strip);
        assertEquals("Strip number", "1255844551.06", strip);

        strip = Num.stripNumber("44,551.06", '.');
        // System.out.println(strip);
        assertEquals("Strip number", "44551.06", strip);
    }
    
    @Test
    public void testNumConverter() throws Exception {
        Cache.registerNumConverter(CustomObject.class, CustomObjectNumConverter.class);
        
        CustomObject co1 = new CustomObject();
        co1.set("10");
        CustomObject co2 = new CustomObject();
        co2.set("2.5");
        
        Calc calc = Calc.builder().val(co1).mul(co2);
        Num c = calc.calc();
        
        assertEquals("25", c.toString());
        
        CustomObject co = c.toObject(CustomObject.class);
        assertEquals("25", co.get());
    }

    public static class CustomObject {
        private String no;
        
        public void set(String number) {
            no = number;
        }
        
        public String get() {
            return no;
        }
    }
    
    public static class CustomObjectNumConverter implements NumConverter<CustomObject> {

        @Override
        public BigDecimal toNum(CustomObject value) throws Exception {
            BigDecimal db = new BigDecimal(value.get());
            return db;
        }

        @Override
        public CustomObject fromNum(Num value) {
            CustomObject co = new CustomObject();
            co.no = value.toString();
            return co;
        }

		@Override
		public boolean cache() {
			return true;
		}

        
        
    }

}
