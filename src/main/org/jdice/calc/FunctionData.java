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
 * Data model for data forwarded to concrete function. 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class FunctionData {

    private Function function;
    private Object[] values;
    private Num result;

    public FunctionData(Class<? extends Function> function, Object... values)  {
        this.function = Cache.getFunction(function);
        setValues(values);
    }

    public FunctionData(Function function, Object ... values) {
        this.function = function;
        setValues(values);
    }

    public void setValues(Object ... values)  {
        this.values = new Object[values.length];
        for(int i = 0; i < values.length; i++) {
            Object o = values[i];
            if (o instanceof AbstractCalculator)
                this.values[i] = o;
            else
                this.values[i] = Num.toNum(o);
        }
    }
    
    public Function getFunction() {
        return function;
    }

    public Object[] getValues() {
        return values;
    }

    public Num getResult() {
        return result;
    }

    public Num calc(AbstractCalculator calc)  {
        Num [] allValues = new Num[this.values.length];
        for(int i = 0; i < this.values.length; i++) {
            Object o = this.values[i];
            
            if (o instanceof Num) {
                allValues[i] = (Num)o;
            } else if (o instanceof AbstractCalculator) {
                AbstractCalculator ac = (AbstractCalculator)o;
                allValues[i] = ac.calc();
            }
        }
        
        try {
            result = function.calc(calc, allValues);
        }
        catch (Exception e) {
            throw new CalculatorException(e);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(function.getSymbol());
        sb.append("(");
        for (int i = 0; i < values.length; i++) {
            Object d = values[i];

            if (i > 0)
                sb.append(", ");

            if (d instanceof Num)
                sb.append(((Num)d).toString());
            else if (d instanceof AbstractCalculator)
                try {
                    sb.append(((AbstractCalculator)d).getInfix());
                }
                catch (Exception e) {
                    sb.append("-error-");
                }
            else 
                sb.append("-unknown-");
        }
        sb.append(")");

        return sb.toString();
    }

    public String toStringWithDetail()  {
        StringBuilder sb = new StringBuilder();
        sb.append(function.getSymbol());
        sb.append("(");
        for (int i = 0; i < values.length; i++) {
            Object d = values[i];

            if (i > 0)
                sb.append(", ");

            if (d instanceof Num)
                sb.append(((Num)d).toStringWithDetail());
            else if (d instanceof AbstractCalculator)
                try {
                    sb.append(((AbstractCalculator)d).getInfix());
                }
                catch (Exception e) {
                    sb.append("-error-");
                }
            else 
                sb.append("-unknown-");
        }
        sb.append(")");

        return sb.toString();
    }

}
