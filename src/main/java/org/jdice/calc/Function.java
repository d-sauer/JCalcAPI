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
 * Methods for function implementation
 *  
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public interface Function extends Extension {

    /**
     * Define maximum number of parameters that implemented function can accept.
     * Zero define unlimited number of parameters.
     * 
     * @return
     */
    public int getFunctionAttributes();

    /**
     * Implementation of function that returns a calculated value
     * 
     * @param calc
     * @param values
     * @return
     * @throws Exception
     */
    public Num calc(AbstractCalculator calc, Num... values) throws Exception;
}
