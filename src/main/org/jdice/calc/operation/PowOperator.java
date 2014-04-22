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
 
package org.jdice.calc.operation;

import java.math.BigDecimal;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Num;
import org.jdice.calc.Operator;
import org.jdice.calc.Properties;
import org.jdice.calc.Rounding;

/**
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class PowOperator implements Operator {

    @Override
    public int getPriority() {
        return 15;
    }

    @Override
    public String getSymbol() {
        return "^";
    }

    @Override
    public Num calc(AbstractCalculator calc, Num value1, Num exponent)  {
        int scale = Properties.getInheritedScale(calc, exponent);
        Rounding roundingMode = Properties.getInheritedRoundingMode(calc, exponent);

        return calc(calc, value1, exponent, scale, roundingMode);
    }

    public Num calc(AbstractCalculator calc, Num value1, Num exponent, Integer scale, Rounding roundingMode)  {
        if (exponent.hasFraction()) {
            double result = Math.pow(value1.doubleValue(), exponent.doubleValue());
            return new Num(result);
        } else {
            int ex = exponent.intValue();
            
            if (ex == 0)
                return value1;
            else if (ex < 0) {
                BigDecimal one = BigDecimal.ONE;
                BigDecimal value = value1.toBigDecimal().pow(ex * -1);
                value = one.divide(value, scale, roundingMode.getBigDecimalRound());
                
                Num result = new Num(value);
                return result;
            } else {
                BigDecimal value = value1.toBigDecimal().pow(exponent.toBigDecimal().intValue());
                Num result = new Num(value);
                return result;
            }
        }
        
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
