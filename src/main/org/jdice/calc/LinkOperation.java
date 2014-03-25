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

import java.util.HashMap;

/**
 * Link interfaces of operation methods with concrete class that extends {@link Operator} or {@link Function}  and implement functionality. 
 * e.g. Link interface {@link Add} that concrete calculator implements with {@link AddOperator}
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class LinkOperation {
    
    private static HashMap<Class, Class<? extends Operation>> bind = new HashMap<Class, Class<? extends Operation>>();
    
    /**
     * Bind implemented interface used in {@link AbstractCalculator} instances with concrete implementation of {@link Operator} or {@link Function}
     * @param clazz
     * @param implementation
     */
    public static void link(Class clazz, Class<? extends Operation> implementation) {
        bind.put(clazz, implementation);
    }
    
    /**
     * Get operation ({@link Operator}, {@link Function}) implementation class
     * @param clazz
     * @return
     */
    public static Class<? extends Operation> getOperation(Class clazz) {
        return bind.get(clazz);
    }

}
