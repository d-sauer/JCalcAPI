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

import org.jdice.calc.internal.BindExtensionProvider;
import org.jdice.calc.internal.Bracket;
import org.jdice.calc.internal.CList;
import org.jdice.calc.internal.CListListener;
import org.jdice.calc.internal.CacheExtension;
import org.jdice.calc.internal.FunctionData;
import org.jdice.calc.internal.InfixParser;
import org.jdice.calc.internal.PostfixCalculator;
import org.jdice.calc.internal.UseExtension;

/**
 * Abstract class which is extended by concrete calculator (e.g. {@link Calculator}
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 * @param <CALC>
 */
public abstract class AbstractCalculator<CALC> {

    /**
     * Detect changes in infix expression
     */
    private CList infix = new CList(new CListListener() {
        @Override
        public void change() {
            isInfixChanged = true;
        }
    });
    private boolean isInfixChanged = true;
    private InfixParser infixParser;
    
    private final PostfixCalculator postfixCalculator = new PostfixCalculator();
    private CList postfix = new CList();
    private Num lastCalculatedValue;
    private LinkedList<Step> calculatingSteps;

    private Properties properties;
    /**
     * User defined extensions in scope of instance
     */
    private UseExtension useExtensions;
    private static boolean isImplExtRegistered = false;

    private AbstractCalculator<CALC> parentCalculator;
    private AbstractCalculator<CALC> childCalculator;
    private boolean isBind = false;
    private boolean isUnbind = false;
    
    private boolean trackSteps = false;


    /**
     * Read implemented extensions by subclass
     * 
     */
    private void detectImplmentedExtension() {
        if (isImplExtRegistered == false) {
            Object o = getThis();
            Class thisClass = o.getClass();

            // superclass interfaces
            Class[] declared = thisClass.getSuperclass().getInterfaces();
            for (Class declare : declared) {
                detectImplmentedExtension(declare);
            }

            // subclass interfaces
            declared = thisClass.getInterfaces();
            for (Class declare : declared) {
                detectImplmentedExtension(declare);
            }
            isImplExtRegistered = true;
        }
    }

    /**
     * Register defined operation or function class to global cache
     * 
     * @param declare
     */
    private void detectImplmentedExtension(Class declare) {
        Class c = BindExtensionProvider.getExtension(declare);
        if (c == null && declare.isAnnotationPresent(BindExtension.class)) {
            BindExtension impl = (BindExtension) declare.getAnnotation(BindExtension.class);
            if (impl != null)
                c = impl.implementation();
            BindExtensionProvider.bind(declare, c);
        }

        if (c != null) {
            if (Operator.class.isAssignableFrom(c))
                CacheExtension.setOperator(c);
            if (Function.class.isAssignableFrom(c))
                CacheExtension.setFunction(c);
        }
    }

    /**
     * Return reference of subclass
     * @return
     */
    protected abstract CALC getThis();

    /**
     * Use custom {@link Operator} or {@link Function} inside scope of this calculation, which can be used during parsing expression.
     * With using custom extension it's possible to override existing extension from global scope.
     * Because during calculation API first scan scoped (locally used) extensions and after that search in global extension from {@link CacheExtension}
     * 
     * @param operationClass
     * @return
     */
    public CALC use(Class<? extends Extension> operationClass) {
        if (useExtensions == null)
            useExtensions = new UseExtension();

        if (Operator.class.isAssignableFrom(operationClass))
            useExtensions.registerOperator(operationClass.asSubclass(Operator.class));
        if (Function.class.isAssignableFrom(operationClass))
            useExtensions.registerFunction(operationClass.asSubclass(Function.class));

        return getThis();
    }

    /**
     * List registered local scoped operations.
     * 
     * @return
     */
    public UseExtension getUsedExtensions() {
        return this.useExtensions;
    }

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
     * Append operator to expression
     * @param operator
     * @return
     */
    public final CALC operator(Class<? extends Operator> operator) {
        infix.add(operator);
        return getThis();
    }

    /**
     * Append operator and number to expression 
     * @param operator
     * @param value
     * @return
     */
    protected final CALC operator(Class<? extends Operator> operator, Object value) {
        Num tmp = null;
        if (value instanceof Num)
            tmp = (Num)value;
        else
            tmp = new Num(value);
        
        infix.add(CacheExtension.getOperator(operator));
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
    protected final CALC operator(Class<? extends Operator> operator, String value, char decimalSeparator) {
        return operator(operator, new Num(value, decimalSeparator));
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
    public final CALC function(Class<? extends Function> function, Object... values) {
        Function fn = CacheExtension.getFunction(function);
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
    public final CALC expression(String expression) throws ParseException {
        detectImplmentedExtension();
        
        if (infixParser == null)
            infixParser = new InfixParser();
        
        CList infix = infixParser.parse(useExtensions, getProperties(), expression);
        expression(infix, false);
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
     * @param values that match unknown variable by name or by order 
     * @return {@link AbstractCalculator}
     * @throws ParseException
     */
    public final CALC expression(String expression, Object... values) throws ParseException {
        detectImplmentedExtension();
        
        if (infixParser == null)
            infixParser = new InfixParser();
        
        CList infix = infixParser.parse(useExtensions, getProperties(), expression, values);
        expression(infix, false);
        return getThis();
    }

    /**
     * 
     * Copy expression from given calculator into this expression within or without brackets
     * 
     * @param expression
     * @param withinBrackets
     * @return
     */
    public CALC expression(AbstractCalculator expression, boolean withinBrackets) {
        expression(expression.infix, withinBrackets);
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
    private final CALC expression(CList infix, boolean withinBrackets) {
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
     * Open bracket
     * @return
     */
    public CALC openBracket() {
        infix.add(Bracket.OPEN);
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
     * If set to TRUE it will track step of calculation.
     * And provide those steps by {@link #getTracedSteps()}
     * @param trackSteps
     * @return
     */
    public CALC setTracingSteps(boolean trackSteps) {
        this.trackSteps = trackSteps;
        return getThis();
    }

    /**
     * Check if is tracking steps enabled or disabled.
     * 
     * @return
     * @see {@link #setTracingSteps(boolean)}
     */
    public boolean isTracingSteps() {
        return trackSteps;
    }

    /**
     * Get calculation steps if {@link #isTracingSteps()} is TRUE
     * 
     * @return
     * @see {@link}
     */
    public LinkedList<Step> getTracedSteps() {
        return calculatingSteps;
    }

    /**
     * Calculate prepared expression.
     * 
     * For tracking calculation 
     * 
     * @return
     * @see {@link #calculate()}
     * @see {@link #getCalculatedValue()}
     */
    public Num calculate() {
        unbind();
        prepareForNewCalculation();
        
        PostfixCalculator pc = convertToPostfix();
        Num cv = pc.calculate(this, postfix, trackSteps);

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
                if (bParent.childCalculator != null)
                    bParent = bParent.childCalculator;
                else
                    break;
            }

            ((AbstractCalculator) childCalc).parentCalculator = bParent;
            ((AbstractCalculator) childCalc).isBind = true;
            bParent.childCalculator = childCalc;
        }
        else {
            throw new CalculatorException("Use calculator which is type of AbstractCalculator", new IllegalArgumentException());
        }

        return childCalc;
    }

    /**
     * Unbind binded calculator
     * @return
     */
    private CALC unbind() {
        if (childCalculator != null)
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
        AbstractCalculator root = undbindFrom.parentCalculator != null ? undbindFrom.parentCalculator : undbindFrom;
        AbstractCalculator child = root.childCalculator;
        while (root != null) {
            AbstractCalculator tmpParent = root.parentCalculator;
            if (tmpParent == null)
                break;
            else
                root = tmpParent;

            child = root.childCalculator;
        }

        // undbind all from root to last child
        while (child != null) {
            if (child.isUnbind == false)
                root.expression(child, false);

            child.isUnbind = true;
            child = child.childCalculator; // new unbind child
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
        return InfixParser.toString(this.postfix);
    }

    /**
     * Convert infix to postfix
     * Conversion is made only first time or after any change in structure of infix expression
     * @return
     */
    private PostfixCalculator convertToPostfix() {
        if (postfix == null || postfix.size() == 0 || isInfixChanged) {
            postfixCalculator.toPostfix(infix);
            postfix = postfixCalculator.getPostfix();
            isInfixChanged = false;
        }

        return postfixCalculator;
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
    public final CALC setInfix(CList infix) {
        this.infix = infix;

        return getThis();
    }

    /**
     * Check whether the calculation is made according to a expression
     * 
     * @return
     * @see {@link getResult()}
     */
    public boolean isCalculated() {
        if (lastCalculatedValue != null)
            return true;
        else
            return false;
    }

    /**
     * Return calculated value
     * 
     * @return
     * @see {@link hasResult()}
     */
    public Num getCalculatedValue() {
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
        this.calculatingSteps = null;
    }

    public final void setSteps(LinkedList<Step> calculationSteps) {
        this.calculatingSteps = calculationSteps;
    }

    @Override
    public String toString() {
        return InfixParser.toString(this.infix);
    }

}
