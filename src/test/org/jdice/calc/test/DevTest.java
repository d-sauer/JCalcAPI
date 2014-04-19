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

import org.jdice.calc.Num;
import org.junit.Test;

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

    public void equals_1a () {
        Num A = new Num(1);
        Num B = new Num(1);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num("2");
        B = new Num("2.0");
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num(3.5295d);
        B = new Num(3.5295);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));
        
        A = new Num("4.529500");
        B = new Num(4.5295);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num("5.529500");
        B = new Num(5.52950000d).setStripTrailingZeros(false);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num("6.529500");
        B = new Num(6.5295001d);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num("7.529500");
        B = new Num(7.529500d);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));

        A = new Num("8.529500");
        B.set(8.529500d);
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));
        
        A = new Num("9.529500");
        B = new Num("9.52951258");
        System.out.println(A.toString() + " equals " + B.toString()  + " : " +  A.equals(B));
        System.out.println(A.toString() + " isEqual " + B.toString()  + " : " +  A.isEqual(B));
    }
    
    public static void main (String [] args) {
        DevTest ndt = new DevTest();
//        ndt.input_1a();
        ndt.equals_1a();
    }
}
