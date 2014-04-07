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
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class CalculatorException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    
    public CalculatorException(Throwable t) {
        super(t);
    }

    public CalculatorException(String message) {
        super(message);
    }

    public CalculatorException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CalculatorException(AbstractCalculator calc, String message)  {
        this(message + (calc != null && (calc.getInfix()) != null ? "  expression:" + calc.getInfix() : ""));
    }
    
    
    public CalculatorException(AbstractCalculator calc, String message, Throwable throwable)  {
        this(message + (calc != null && calc.getInfix() != null ? "  expression:" + calc.getInfix() : ""), throwable);
    }
    
}
