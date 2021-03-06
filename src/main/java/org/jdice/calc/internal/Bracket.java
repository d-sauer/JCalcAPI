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
 
package org.jdice.calc.internal;


/**
 * Enum for declaring brackets like objects in infix and postfix notation.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public enum Bracket {
    OPEN(50, "(", "\\("), CLOSE(50, ")", "\\)");

    private int priority;
    private String symbol;
    private String regex;

    Bracket(int priority, String symbol, String regex) {
        this.priority = priority;
        this.symbol = symbol;
        this.regex = regex;
    }

    public int getPriority() {
        return priority;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getRegex() {
        return regex;
    }

}
