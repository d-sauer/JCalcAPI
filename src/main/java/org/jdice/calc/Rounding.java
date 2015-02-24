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
import java.math.RoundingMode;

/**
 * Map rounding between <tt>{@link java.math.BigDecimal}</tt> rounding mode, and <tt>{@link java.math.RoundingMode}</tt>  
 * JCalc API use <tt>BigDecimal</tt> rounding when it's possible, otherwise it use standard <tt>RoundingModey</tt>.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public enum Rounding {
    CEILING(BigDecimal.ROUND_CEILING, RoundingMode.CEILING),
    DOWN(BigDecimal.ROUND_DOWN, RoundingMode.DOWN),
    FLOOR(BigDecimal.ROUND_FLOOR, RoundingMode.FLOOR),
    HALF_DOWN(BigDecimal.ROUND_HALF_DOWN, RoundingMode.DOWN),
    HALF_EVEN(BigDecimal.ROUND_HALF_EVEN, RoundingMode.HALF_EVEN),
    HALF_UP(BigDecimal.ROUND_HALF_UP, RoundingMode.HALF_UP),
    UNNECESSARY(BigDecimal.ROUND_UNNECESSARY, RoundingMode.UNNECESSARY),
    UP(BigDecimal.ROUND_UP, RoundingMode.UP);
    
    private int modeBigDecimal;
    private RoundingMode modeRoundingMode;
    
    Rounding(int mode, RoundingMode roundingMode) {
        this.modeBigDecimal = mode;
        this.modeRoundingMode = roundingMode;
    }
    
    public int getBigDecimalRound() {
        return this.modeBigDecimal;
    }

    public RoundingMode getRoundingMode() {
        return this.modeRoundingMode;
    }

    public static Rounding getRoundingMode(String roundingMode) {
        for(Rounding rm : values()) {
            if (rm.name().equalsIgnoreCase(roundingMode))
                return rm;
        }
        
        return null;
    }

}
