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

import java.util.HashMap;

import org.jdice.calc.CalculatorException;
import org.jdice.calc.Function;
import org.jdice.calc.Operator;

/**
 * Class which hold used extension (operators, functions). In global and local scope.
 *  
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class UseExtension {

    /** Operators -> operator class */
    private HashMap<String, Class<? extends Operator>> operatorSymbols = new HashMap<String, Class<? extends Operator>>();

    /** Operator class -> Operator class instance */
    private HashMap<Class<? extends Operator>, Operator> operatorCache = new HashMap<Class<? extends Operator>, Operator>();

    /** Function -> function class */
    private HashMap<String, Class<? extends Function>> functionSymbols = new HashMap<String, Class<? extends Function>>();

    /** Function class -> function class instance */
    private HashMap<Class<? extends Function>, Function> functionCache = new HashMap<Class<? extends Function>, Function>();

    public void registerOperator(Class<? extends Operator> operatorClass) {
        if (!operatorSymbols.containsValue(operatorClass)) {
            if (Operator.class.isAssignableFrom(operatorClass)) {
                Operator operator = getOperator(operatorClass);
                operatorSymbols.put(operator.getSymbol(), operatorClass);
                operatorCache.put(operatorClass, operator);
            }
        }
    }

    public HashMap<String, Class<? extends Operator>> getOperatorSymbols() {
        return operatorSymbols;
    }

    public Operator getOperator(Class<? extends Operator> operatorClass) {
        Operator operator = null;
        operator = operatorCache.get(operatorClass);
        if (operator == null) {
            synchronized (operatorClass) {
                operator = operatorCache.get(operatorClass);
                if (operator == null) {
                    try {
                        operator = operatorClass.newInstance();
                    }
                    catch (Exception e) {
                        throw new CalculatorException(e);
                    }
                }
            }
        }

        return operator;
    }

    public Operator getOperator(String operator) {
        Operator _operator = null;
        Class<? extends Operator> cl = operatorSymbols.get(operator);
        if (cl != null && Operator.class.isAssignableFrom(cl)) {
            _operator = getOperator(cl);
        }

        return _operator;
    }

    public HashMap<Class<? extends Operator>, Operator>  getOperators() {
        return operatorCache;
    }

    public void registerFunction(Class<? extends Function> functionClass) {
        if (!functionSymbols.containsValue(functionClass)) {
            if (Function.class.isAssignableFrom(functionClass)) {
                Function function = getFunction(functionClass);
                functionSymbols.put(function.getSymbol(), functionClass);
                functionCache.put(functionClass, function);
            }
        }
    }

    public HashMap<String, Class<? extends Function>> getFunctionSymbols() {
        return functionSymbols;
    }

    public Function getFunction(String function) {
        Function _function = null;
        Class<? extends Function> cl = functionSymbols.get(function);
        if (cl != null && Function.class.isAssignableFrom(cl)) {
            _function = getFunction(cl);
        }

        return _function;
    }
    
    public HashMap<Class<? extends Function>, Function> getFunctions() {
        return functionCache;
    }

    public Function getFunction(Class<? extends Function> functionClass) {
        Function function = null;
        function = functionCache.get(functionClass);
        if (function == null) {
            synchronized (functionClass) {
                function = functionCache.get(functionClass);
                if (function == null) {
                    try {
                        function = functionClass.newInstance();
                    }
                    catch (Exception e) {
                        throw new CalculatorException(e);
                    }
                }
            }
        }

        return function;
    }
}
