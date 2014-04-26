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

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Abstract class that concrete calculator extends 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 * @param <CALC>
 */
public abstract class AbstractCalculator<CALC> {

    /**
     * Detect changes in expression
     */
    private CList infix = new CList(new CListListener() {
        @Override
        public void change() {
            isInfixChanged = true;
        }
    });
    private boolean isInfixChanged = true;
    private CList postfix = new CList();
    private Num lastCalculatedValue;
    private LinkedList<String> calculationSteps;

    private Properties properties;
    private OperationRegister scopeOperationRegister;
    private static boolean isImplOpRegistered = false;

    private AbstractCalculator<CALC> parentCalc;
    private AbstractCalculator<CALC> childCalc;
    private boolean isBind = false;
    private boolean isUnbind = false;

    //
    // Constructor's
    //
    public AbstractCalculator() {
    }

    /**
     * Register implemented operation and functions by subclass
     * 
     */
    private void registerImplmentedOperation() {
        if (isImplOpRegistered == false) {
            Object o = getThis();
            Class thisClass = o.getClass();

            // superclass interfaces
            Class[] declared = thisClass.getSuperclass().getInterfaces();
            for (Class declare : declared) {
                registerImplmentedOperation(declare);
            }

            // subclass interfaces
            declared = thisClass.getInterfaces();
            for (Class declare : declared) {
                registerImplmentedOperation(declare);
            }
            isImplOpRegistered = true;
        }
    }

    /**
     * Register defined operation or function class to global cache
     * 
     * @param declare
     */
    private void registerImplmentedOperation(Class declare) {
        Class c = LinkOperation.getOperation(declare);
        if (c == null && declare.isAnnotationPresent(Implementation.class)) {
            Implementation impl = (Implementation) declare.getAnnotation(Implementation.class);
            if (impl != null)
                c = impl.implementation();
            LinkOperation.link(declare, c);
        }

        if (c != null) {
            if (Operator.class.isAssignableFrom(c))
                Cache.registerOperator(c);
            if (Function.class.isAssignableFrom(c))
                Cache.registerFunction(c);
        }
    }

    /**
     * Return reference of subclass
     * @return
     */
    protected abstract CALC getThis();

    /**
     * Provide custom {@link Operator} or {@link Function} inside scope of this instance, that can be used during expression parsing.
     * With registration of custom operation it's possible to override existing default operation implementation.
     * Because during calculation API first scan scoped (registered) operation and after that default operation implementation inside {@link Cache}
     * 
     * @param operationClass
     * @return
     */
    public CALC register(Class<? extends Operation> operationClass) {
        if (scopeOperationRegister == null)
            scopeOperationRegister = new OperationRegister();

        if (Operator.class.isAssignableFrom(operationClass))
            scopeOperationRegister.registerOperator(operationClass.asSubclass(Operator.class));
        if (Function.class.isAssignableFrom(operationClass))
            scopeOperationRegister.registerFunction(operationClass.asSubclass(Function.class));

        return getThis();
    }

    /**
     * List registered local scoped operations.
     * 
     * @return
     */
    public OperationRegister getRegisteredOperations() {
        return this.scopeOperationRegister;
    }

    //
    // APPEND VALUE
    //

    /**
     * Append value to expression
     * 
     * @param value
     * @return
     */
    public CALC val(Object value) {
        Num val = null;
        if (value instanceof Num)
            val = (Num)value;
        else
            val = new Num(value);
                
        infix.add(val);
        return getThis();
    }

    /**
     * Append value to expression
     * 
     * @param value custom type value
     * @param converter class for convert custom type to {@link Num}
     * @return
     */
    public CALC val(Object value, Class<? extends NumConverter> converter) {
        infix.add(new Num(value, converter));
        return getThis();
    }

    /**
     * Append String value to expression that will be parsed to {@link Num} with defined decimal separator 
     * 
     * 
     * @param value String representation of number
     * @param decimalSeparator used in string representation of number
     * @return
     */
    public CALC val(String value, char decimalSeparator) {
        infix.add(new Num(value, decimalSeparator));
        return getThis();
    }

    /**
     * Copy calculator expression into this expression within brackets
     * 
     * @param expression
     * @return
     */
    public CALC append(AbstractCalculator expression) {
        return append(expression, true);
    }

    /**
     * 
     * Copy expression from given calculator into this expression within or without brackets
     * 
     * @param expression
     * @param withinBrackets
     * @return
     */
    public CALC append(AbstractCalculator expression, boolean withinBrackets) {
        append(expression.infix, withinBrackets);
        return getThis();
    }

    /**
     * 
     * Append copy of given infix expression into this expression within or without brackets
     * 
     * @param infix
     * @param withinBrackets
     * @return
     */
    public CALC append(CList infix, boolean withinBrackets) {
        if (withinBrackets)
            this.infix.add(Bracket.OPEN);

        Iterator<Object> it = infix.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (o instanceof Num) {
                this.infix.add((Num) o);
            }
            else if (o instanceof Operator) {
                this.infix.add((Operator) o);
            }
            else if (o instanceof FunctionData) {
                this.infix.add((FunctionData) o);
            }
            else if (o instanceof Function) {
                this.infix.add((Function) o);
            }
            else if (o instanceof Bracket) {
                this.infix.add((Bracket) o);
            }
        }

        if (withinBrackets)
            this.infix.add(Bracket.CLOSE);

        return getThis();
    }

    /**
     * Append operator to expression
     * @param operator
     * @return
     */
    public CALC append(Class<? extends Operator> operator) {
        infix.add(operator);
        return getThis();
    }

    /**
     * Append operator and number to expression 
     * @param operator
     * @param value
     * @return
     */
    public CALC append(Class<? extends Operator> operator, Object value) {
        Num tmp = null;
        if (value instanceof Num)
            tmp = (Num)value;
        else
            tmp = new Num(value);
        
        infix.add(Cache.getOperator(operator));
        infix.add(tmp);
        return getThis();
    }

    /**
     * Append operator and parsed String value with custom decimal separator used in String representation of value 
     * @param operator
     * @param value
     * @param decimalSeparator
     * @return
     */
    public CALC append(Class<? extends Operator> operator, String value, char decimalSeparator) {
        return append(operator, new Num(value, decimalSeparator));
    }

    /**
     * Append function with value to expression.
     * 
     * <br/>
     * e.g. Abs.class, -5 => abs(-5)
     * 
     * @param function
     * @param values can accept any object that {@link Num} can work with
     * @return
     * @see {@link Function}
     */
    public CALC append(Class<? extends Function> function, Object... values) {
        Function fn = Cache.getFunction(function);
        FunctionData fd = new FunctionData(fn, values);
        this.infix.addFunction(fd);

        return getThis();
    }

    /**
     * Parse and append given expression to existing expression
     * 
     * @param expression
     * @return
     * @throws ParseException
     */
    public CALC expression(String expression) throws ParseException {
        registerImplmentedOperation();
        append(InfixParser.parseInfix(scopeOperationRegister, getProperties(), expression), false);
        return getThis();
    }

    /**
     * Parse and append given expression to existing expression
     * String representation of expression that will be parsed with unknown variables.
     * It is possible to define name of <tt>Num</tt> with {@link Num#setName(String)} then name will be matched with name of unknown variable.
     * Otherwise unknown variable will be matched by declared order.
     *   
     * <br/>
     * e.g. X + 5 - (2 * X - Y)
     * 
     * @param expression
     * @return
     * @throws ParseException
     *             @
     */
    public CALC expression(String expression, Object... values) throws ParseException {
        registerImplmentedOperation();
        append(InfixParser.parseInfix(scopeOperationRegister, getProperties(), expression, values), false);
        return getThis();
    }

    //
    // BRACKET'S
    //
    /**
     * Open bracket
     * @return
     */
    public CALC ob() {
        infix.add(Bracket.OPEN);
        return getThis();
    }

    /**
     * Open bracket
     * @return
     */
    public CALC openBracket() {
        infix.add(Bracket.OPEN);
        return getThis();
    }

    // ---------------
    /**
     * Close bracket
     * 
     * @return
     */
    public CALC cb() {
        infix.add(Bracket.CLOSE);
        return getThis();
    }

    /**
     * Close bracket
     * 
     * @return
     */
    public CALC closeBracket() {
        infix.add(Bracket.CLOSE);
        return getThis();
    }

    /**
     * Get defined properties
     * @return
     */
    public Properties getProperties() {
        if (properties == null)
            properties = new Properties();

        return properties;
    }

    /**
     * Set proeprties
     * @param properties
     * @return
     */
    public CALC setProperties(Properties properties) {
        this.properties = properties;
        return getThis();
    }

    /**
     * Set scale for entire expression
     * @param scale
     * @return
     */
    public CALC setScale(Integer scale) {
        getProperties().setScale(scale);
        return getThis();
    }

    /**
     * Get scale mode used throughout expression
     * @return
     */
    public Integer getScale() {
        return getProperties().getScale();
    }

    /**
     * Set rounding mode for entire expression
     * @param roundingMode
     * @return
     */
    public CALC setRoundingMode(Rounding roundingMode) {
        getProperties().setRoundingMode(roundingMode);
        return getThis();
    }

    /**
     * Get rounding mode used throughout expression
     * @return
     */
    public Rounding getRoundingMode() {
        return getProperties().getRoundingMode();
    }

    /**
     * Set decimal separator for entire expression
     * @param decimalSeparator
     * @return
     */
    public CALC setDecimalSeparator(char decimalSeparator) {
        getProperties().setInputDecimalSeparator(decimalSeparator);
        getProperties().setOutputDecimalSeparator(decimalSeparator);
        return getThis();
    }

    /**
     * Get decimal separator used throughout expression
     * @return
     */
    public char getDecimalSeparator() {
        return getProperties().getInputDecimalSeparator();
    }

    /**
     * Set number grouping separator for entire expression
     * @param decimalSeparator
     * @return
     */
    public CALC setGroupingSeparator(char decimalSeparator) {
        getProperties().setGroupingSeparator(decimalSeparator);
        return getThis();
    }

    /**
     * Get grouping separator used throughout expression
     * @return
     */
    public char getGroupingSeparator() {
        return getProperties().getGroupingSeparator();
    }

    public boolean hasStripTrailingZeros() {
        return getProperties().hasStripTrailingZeros();
    }

    public CALC setStripTrailingZeros(boolean stripTrailingZeros) {
        getProperties().setStripTrailingZeros(stripTrailingZeros);
        return getThis();
    }
    
    /**
     * Calculate prepared expression
     * 
     * @return
     * @see {@link #calculate()}
     * @see {@link #calculate(boolean)}
     * @see {@link #getResult()}
     */
    public Num calculate() {
        return calculate(false, false);
    }

    /**
     * Calculate expression and trace calculation steps accessible with {@link getCalculationSteps()}.
     * For more detailed information of every step, set rememberDetails to true.
     * 
     * @param traceSteps
     * @param rememberDetails - more detailed information of every step
     * @return
     */
    public Num calculate(boolean traceSteps, boolean rememberDetails) {
        unbind();
        prepareForNewCalculation();
        PostfixCalculator pc = convertToPostfix();
        Num cv = pc.calculate(this, postfix, traceSteps, rememberDetails);

        lastCalculatedValue = cv.clone();

        return cv;
    }

    /**
     * Bind another Calculator class functionalities to expression.
     * 
     * Way to combine two different implementation of calculators
     * 
     * @param clazz
     * @return
     */
    public <T extends AbstractCalculator> T bind(Class<T> clazz) {
        T childCalc = null;
        try {
            childCalc = clazz.newInstance();
        }
        catch (Exception e) {
            throw new CalculatorException(e);
        }

        if (childCalc instanceof AbstractCalculator) {
            // find last child from root
            AbstractCalculator<CALC> bParent = this;
            while (bParent != null) {
                if (bParent.childCalc != null)
                    bParent = bParent.childCalc;
                else
                    break;
            }

            ((AbstractCalculator) childCalc).parentCalc = bParent;
            ((AbstractCalculator) childCalc).isBind = true;
            bParent.childCalc = childCalc;
        }
        else {
            throw new CalculatorException("Use only Calculator class subclases", new IllegalArgumentException());
        }

        return childCalc;
    }

    // /**
    // * Return to functionalities provided by original Calculator object
    // * @return
    // * @
    // */
    // private CALC unbind() {
    // if (parentCalc != null) {
    // parentCalc.append(this, false);
    // this.isUnbind = true;
    // return (CALC) parentCalc;
    // }
    // else if (this.childCalc != null)
    // return (CALC)unbindAll(this);
    // else
    // return (CALC) this;
    // }

    /**
     * Unbind binded calculator
     * @return
     */
    private CALC unbind() {
        if (childCalc != null)
            unbindAll(this);

        return (CALC) this;
    }

    /**
     * Unbind all binded calculators
     * @param undbindFrom
     * @return
     */
    private CALC unbindAll(AbstractCalculator<CALC> undbindFrom) {
        // find root and first child
        AbstractCalculator root = undbindFrom.parentCalc != null ? undbindFrom.parentCalc : undbindFrom;
        AbstractCalculator child = root.childCalc;
        while (root != null) {
            AbstractCalculator tmpParent = root.parentCalc;
            if (tmpParent == null)
                break;
            else
                root = tmpParent;

            child = root.childCalc;
        }

        // undbind all from root to last child
        while (child != null) {
            if (child.isUnbind == false)
                root.append(child, false);

            child.isUnbind = true;
            child = child.childCalc; // new unbind child
        }

        return (CALC) undbindFrom;
    }

    /**
     * Convert defined expression to postfix <tt>String</tt>
     * 
     * @return
     */
    public String getPostfix() {
        unbind();
        convertToPostfix();
        return InfixParser.printInfix(this.postfix);
    }

    /**
     * Convert infix to postfix
     * Conversion is made only first time or after any change in structure of infix expression
     * @return
     */
    private PostfixCalculator convertToPostfix() {
        PostfixCalculator pc = new PostfixCalculator();
        if (postfix == null || postfix.size() == 0 || isInfixChanged) {
            pc.toPostfix(infix);
            postfix = pc.getPostfix();
            isInfixChanged = false;
        }

        return pc;
    }

    /**
     * Get infix (common arithmetic and logical expression notation) representation of given expression
     * 
     * @return
     * @see {@link getPostfix()}
     */
    public String getInfix() {
        unbind();

        return toString();
    }

    /**
     * Provide infix list for this calculator.
     * 
     * @param infix
     * @return
     */
    CALC setInfix(CList infix) {
        this.infix = infix;

        return getThis();
    }

    /**
     * Check whether the calculation is made according to a expression
     * 
     * @return
     * @see {@link getResult()}
     */
    public boolean hasResult() {
        if (lastCalculatedValue != null)
            return true;
        else
            return false;
    }

    /**
     * Return copy of calculated result
     * 
     * @return
     * @see {@link hasResult()}
     */
    public Num getResult() {
        if (lastCalculatedValue != null)
            return lastCalculatedValue.clone();
        else
            return null;
    }

    /**
     * Reset result and calculation steps of previous calculation
     */
    private void prepareForNewCalculation() {
        lastCalculatedValue = null;
        this.calculationSteps = null;
    }

    final void setSteps(LinkedList<String> calculationSteps) {
        this.calculationSteps = calculationSteps;
    }

    /**
     * Check if calculation steps are remembered during calculation.
     * 
     * @return
     * @see {@link getCalculationSteps()}
     */
    public boolean hasCalculationSteps() {
        return calculationSteps != null ? true : false;
    }

    /**
     * Get calculation steps if calculation is initiated with {@link calcWithSteps(boolean)}
     * 
     * @return
     * @see {@link}
     */
    public LinkedList<String> getCalculationSteps() {
        return calculationSteps;
    }

    @Override
    public String toString() {
        return InfixParser.printInfix(this.infix);
    }

}
