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
 
package org.jdice.calc;

/**
 * Common used expressions
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 */
public class CalcFactory {

    /**
     * Calculate percent of value
     * 
     * @param percent
     * @param ofValue
     * @return
     */
    public static Num percentOf(Object percent, Object ofValue) {
        Num _x = new Num(percent);
        Num _y = new Num(ofValue);
        Calc cPercent = Calc.builder().ob().val(_x).div(100).cb().mul(_y);
        return cPercent.calc();
    }

}
