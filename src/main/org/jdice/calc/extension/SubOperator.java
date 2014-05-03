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
 
package org.jdice.calc.extension;

import java.math.BigDecimal;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Num;
import org.jdice.calc.Operator;

/**
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class SubOperator implements Operator {

    @Override
    public int getPriority() {
        return 5;
    }

    @Override
    public String getSymbol() {
        return "-";
    }

    @Override
    public Num calc(AbstractCalculator calc, Num value1, Num value2)  {
        BigDecimal value = value1.toBigDecimal().subtract(value2.toBigDecimal());
        Num result = new Num(value);
        return result;
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
