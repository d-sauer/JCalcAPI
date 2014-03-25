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

import java.math.BigDecimal;

/**
 * Interface that define methods for conversion of custom Object to BigDecimal 
 * with which JCalc API knows to work.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * @see {@link Cache#registerNumConverter(Class, Class)}
 * @param <T>
 */
public interface NumConverter<T> {

    /**
     * Convert object to BigDecimal
     * @param object
     * @return BigDecimal
     * @throws Exception
     */
    public BigDecimal toNum(T object) throws Exception;
    
    /**
     * Convert Num to custom object
     * @param value
     * @return T
     */
    public T fromNum(Num value);
    
    /**
     * Register NumConverter to global cache on first use
     * 
     * @see {@link Cache#registerNumConverter(Class, Class)}
     * @return
     */
    public boolean cache();
}
