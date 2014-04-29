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

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jdice.calc.operation.Add;
import org.jdice.calc.operation.AddOperator;

/**
 * Link operator or function interface that contains overloaded methods to implements in {@link AbstractCalculator}
 * with concrete class that implement calculation logic for annotated operator or function.
 * <br/>
 * For example it link {@link Add} interface which is implemented in concrete calculator like {@link Calculator}, and 
 * implementation od add operator {@link AddOperator}
 * 
 * @author  Davor Sauer <davor.sauer@gmail.com>
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Implementation {

    Class<? extends Operation> implementation();
    
}
