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

import org.jdice.calc.extension.SubOperator;

/**
 * Method for implementing operator.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public interface Operator extends Extension {

    /**
     * Define operator priority during calculation.
     * <br/>
     * Current used operator use next priority:
     * (5) {@link SubOperator}, {@link AddOperator} <br/> 
     * (10) {@link MulOperator}, {@link DivOperator}, {@link ModOperator} <br/>
     * (15) {@link PowOperator} <br/>
     * 
     * @return
     */
    public int getPriority();


    /**
     * Calculate value for implemented operator
     * 
     * @param calc
     * @param operandLeft
     * @param operandRight
     * @return {@link Num}
     */
    public abstract Num calc(AbstractCalculator calc, Num operandLeft, Num operandRight) throws Exception ;
}
