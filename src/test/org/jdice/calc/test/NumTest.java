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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import org.jdice.calc.Calculator;
import org.jdice.calc.Num;
import org.jdice.calc.NumConverter;
import org.jdice.calc.Rounding;
import org.jdice.calc.SingletonExtension;
import org.jdice.calc.internal.CacheExtension;
import org.junit.Test;

public class NumTest {

    @Test
    public void testIsEqual() throws Exception {
        Num A = new Num(5);
        Num B = new Num(5);
        assertTrue(A.isEqual(B));

        A = new Num("5");
        B = new Num("5.0");
        assertTrue(A.isEqual(B));

        A = new Num(5.5295d);
        B = new Num(5.5295);
        assertTrue(A.isEqual(B));

        A = new Num("5.529500");
        B = new Num(5.5295);
        assertTrue(A.isEqual(B));

        A = new Num("5.529500");
        B = new Num(5.529500d);
        assertTrue(A.isEqual(B));

        A = new Num("5.529500");
        B = new Num(5.5295001d);
        assertTrue("Not equal", !A.isEqual(B));

        A = new Num("5.529500");
        B.set(5.529500d);
        assertTrue(A.isEqual(B));

        A = new Num("5.529500");
        B.set(5.529500d);
        assertTrue(A.isEqual("5.5295"));
        
        
        A = new Num("5.529500");
        B = new Num("5.52951258");

        assertTrue(A.isEqual(B, 2));
        
        assertTrue(!A.isEqual(B, 20));	// not same: 5.529512|58| 
        
        assertTrue(A.isEqual(B, 4, Rounding.HALF_DOWN));
        
        
        A = new Num("1254.5848");
        B = new Num("1254.58");
        assertTrue(!A.isEqual(B));
		assertTrue(A.isEqual(B, 2));

		assertTrue(A.isEqual(B, true));
    }

    @Test
    public void testEqualsAndIsEqual() throws Exception {
        Num A = new Num(1);
        Num B = new Num(1);
        assertTrue(A.equals(B));
        assertTrue(A.isEqual(B));

        A = new Num("2");
        B = new Num("2.0");
        assertTrue(!A.equals(B));
        assertTrue(A.isEqual(B));

        A = new Num(3.5295d);
        B = new Num(3.5295);
        assertTrue(A.equals(B));
        assertTrue(A.isEqual(B));
        
        A = new Num("4.529500");
        B = new Num(4.5295);
        assertTrue(!A.equals(B));
        assertTrue(A.isEqual(B));

        A = new Num("5.529500");
        B = new Num(5.52950000d);
        assertTrue(!A.equals(B));
        assertTrue(A.isEqual(B));

        A = new Num("6.529500");
        B = new Num(6.5295001d);
        assertTrue(!A.equals(B));
        assertTrue(!A.isEqual(B));

        A = new Num("7.5295000");
        B = new Num(7.529500d);
        assertTrue(!A.equals(B));
        assertTrue(A.isEqual(B));

        A = new Num("8.5295001");
        B.set(8.5295001d);
        assertTrue(A.equals(B));
        assertTrue(A.isEqual(B));
        
        A = new Num("9.529500");
        B = new Num("9.52951258");
        assertTrue(!A.equals(B));
        assertTrue(!A.isEqual(B));
        
        A = new Num(5.1000987);
        assertTrue(!A.equals("5.1000987")); //because objects are not same data type BigDecimal != String
        assertTrue(A.isEqual("5.1000987"));
        
        A = new Num(6.101);
        B = new Num(6.101987);  //when scale = 3, new number is 6.102
        assertTrue(!A.equals(B));
        assertTrue(!A.isEqual(B, true));

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
    public void testParseStringnumber() throws Exception {
        Num num = new Num("44,551.06", '.');
        assertEquals("num number", "44551.06", num.toString());

        num = new Num("12 558 44,551.06", '.');
        assertEquals("num number", "1255844551.06", num.toString());

        num = new Num("+44,551.06");
        assertEquals("num number", "44551.06", num.toString());

        num = new Num("-44,551.06");
        assertEquals("num number", "-44551.06", num.toString());

        num = new Num("-12 558 44,551.06", '.');
        assertEquals("num number", "-1255844551.06", num.toString());

        num = new Num("-44,551.06", '.');
        assertEquals("Strip number", "-44551.06", num.toString());
    }
    
    @Test
    public void testNumConverter() throws Exception {
        CacheExtension.registerNumConverter(CustomObject.class, CustomObjectNumConverter.class);
        
        CustomObject co1 = new CustomObject();
        co1.set("10");
        CustomObject co2 = new CustomObject();
        co2.set("2.5");
        
        Calculator calc = Calculator.builder().val(co1).mul(co2);
        Num c = calc.calculate();
        
        assertEquals("25", c.toString());
        
        CustomObject co = c.toObject(CustomObject.class);
        assertEquals("25", co.get());
    }

    @Test
    public void testScaleStripTrailingZeros() throws Exception {
        Num n3a = new Num(3.10);
        assertEquals("3.1", n3a.toString());
        
        Num n3b = new Num("3.10");
        assertEquals("3.10", n3b.toString());
    }
    
    @Test
    public void testHasFraction() throws Exception {
        Num f = new Num("20");
        assertTrue(!f.hasRemainder());

        f = new Num("20.2");
        assertTrue(f.hasRemainder());

        f = new Num(20);
        assertTrue(!f.hasRemainder());

        f = new Num(20.2);
        assertTrue(f.hasRemainder());
        
    }    
    
    @Test
    public void serialization() throws Exception {
        Num numOut = new Num("60.987654321123456789");
        
        //
        // Write
        //
        FileOutputStream fos = new FileOutputStream("numObject.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(numOut);
        oos.flush();
        oos.close();
        
        //
        // Read
        //
        FileInputStream fis = new FileInputStream("numObject.ser");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Num numIn = (Num) ois.readObject();
        ois.close();
        
//        System.out.println("numOut: " + numOut);
//        System.out.println("numIn:  " + numIn);
        
        assertEquals(numOut, numIn);
        assertEquals(numOut.toString(), numIn.toString());
        assertTrue(numOut.equals(numIn));
        assertTrue(numOut.getProperties().equals(numIn.getProperties()));
        
        File toDelet = new File("numObject.ser");
        toDelet.delete();
        
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
    
    @SingletonExtension
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

    }

}
