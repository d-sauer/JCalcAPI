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

import java.util.Date;

import org.jdice.calc.Num;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

import static java.lang.Thread.*;

/**
 * Stress test for tracking performance with Oracle Mission control (JVM parameters: -XX:+UnlockCommercialFeatures -XX:+FlightRecorder),
 * and analyze heapdump with Eclipse MAT for futher optimization.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public class JUnitStresTest {

    public static void main(String[] args) throws Exception {
        int maxThreads = 4;
        for (int i = 0; i < maxThreads; i++) {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    JUnitStresTest st = new JUnitStresTest();
                    try {
                        st.test();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

            thread.start();
        }

    }

    public static void test() throws Exception {
        final int iteration = 10000;
        final int iterationSleep = 0;

        System.out.println("Iterations:" + iteration);
        for (int s = 20; s >= 0; s--) {
            System.out.println("Wait " + s + "sec...");
            sleep(1000);
        }
        long start = System.currentTimeMillis();
        System.out.println("Start:" + new Date(start));

        JUnitCore junit = new JUnitCore();
        boolean successful = true;
        for (int l = 0; l < iteration; l++) {
            Result result = junit.run(CalcTest.class, CalcFactoryTest.class, CustomFunctionTest.class, NumTest.class, PostfixTest.class);
            if (iterationSleep > 0)
                sleep(iterationSleep);

            if (l % 100 == 0)
                System.out.printf("\n %7d %s ", l, currentThread().getId());

            if (result.wasSuccessful())
                System.out.print(".");
            else {
                System.out.print("!");
                successful = false;
            }
        }
        long end = System.currentTimeMillis();
        long delta = end - start;

        System.out.println("\nEnd:" + new Date(end));
        System.out.println("Th: " + currentThread().getId() + " Duration:" + Num.toNum(delta).toString(' ', '.') + "ms");
        System.out.println("Successful:" + successful);

        sleep(15000);
        System.out.println("Finish!");

    }

}
