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

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Calculator;
import org.jdice.calc.CalculatorException;
import org.jdice.calc.Extension;
import org.jdice.calc.Function;
import org.jdice.calc.Num;
import org.jdice.calc.Operator;
import org.jdice.calc.Properties;
import org.jdice.calc.extension.SubOperator;

/**
 * Utility class for infix parsing and manipulation.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public class InfixParser {

    private static final String REGEX_VARIABLE_NAMES = "([a-zA-Z]+)\\b(?!\\s*\\()";
    private static final String REGEX_FUNCTIONS = "[a-zA-Z0-9]+\\(((?<=(?:[(]))[^(].*?(?=[)]))"; // e.g. abs(..) <- function | abs <- name, no open brackets

    private static final Pattern pVariableNames  = Pattern.compile(REGEX_VARIABLE_NAMES);
    private static final Pattern pFunctions = Pattern.compile(REGEX_FUNCTIONS);;

    private CList infixNotation = new CList();
    private Properties properties;
    private UseExtension usedExtensions;
    private Pattern pUsedExtensions;

    public InfixParser() {
    }
    
    public InfixParser(Properties properties) {
    	this.properties = properties;
    }

    public InfixParser(UseExtension operationRegister, Properties properties) {
        pUsedExtensions = null;
        this.usedExtensions = operationRegister;
        this.properties = properties;
    }

    /**
     * Parse infix string expression with additional properties
     * @param operationRegister
     * @param properties
     * @param infixExpression
     * @param values
     * @return
     * @throws ParseException
     */
    public CList parse(UseExtension operationRegister, Properties properties, String infixExpression, Object... values) throws ParseException {
        pUsedExtensions = null;
        this.usedExtensions = operationRegister;
        this.properties = properties;

        return parse(infixExpression, values);
    }

    /**
     * Parse infix string expression
     * @param infixExpression
     * @param values
     * @return
     * @throws ParseException
     */
    public CList parse(String infixExpression, Object... values) throws ParseException {
        // get variable names
        LinkedHashMap<String, Num> vNames = mapValues(infixExpression, values);

        return parse(infixExpression, vNames);
    }

    private CList parse(String infixExpression, LinkedHashMap<String, Num> vNames) throws ParseException {
        //
        // Separate formulas and non formula parts
        //
        infixExpression = infixExpression.replace(" ", "");
        int infixLength = infixExpression.length();
        // separate function and reminders in groups
        Matcher mat = pFunctions.matcher(infixExpression);

        int gc = mat.groupCount() + 1;
        int[][] mf = new int[gc + gc + 1][];
        int count = 0;

        int firstIndex = 0;
        int lastIndex = 0;
        while (mat.find()) {
            String group = mat.group();
            // before
            if (firstIndex != mat.start())
                mf[count++] = new int[] { firstIndex, mat.start(), 0 };

            // middle
            firstIndex = mat.start();
            lastIndex = mat.end() + countOccurrences(group, '(');
            mf[count++] = new int[] { firstIndex, lastIndex, 1 };
            firstIndex = lastIndex;
        }

        // last
        if (lastIndex != infixLength)
            mf[count++] = new int[] { lastIndex, infixLength, 0 };

        for (int i = 0; i < mf.length; i++) {
            if (mf[i] != null) {
                int[] section = mf[i];
                String group = infixExpression.substring(section[0], section[1]);

                if (section[2] == 1) { // function
                    String function = group.substring(0, group.indexOf("("));

                    Extension op = null;
                    // local scope
                    if (usedExtensions != null)
                        op = usedExtensions.getFunction(function);

                    // global scope
                    if (op == null)
                        op = CacheExtension.getFunction(function);

                    if (op == null)
                        throw new CalculatorException("Can't find '" + function + "' function implementation class used in expression " + infixExpression);

                    Function f = (Function) op;
                    String _expression = group.substring(group.indexOf("(") + 1, group.length() - 1);
                    String[] expressions = _expression.split(",");
                    Object[] values = new Object[expressions.length];

                    for (int e = 0; e < expressions.length; e++) {
                        String expression = expressions[e];

                        InfixParser tmp = new InfixParser(properties);
                        CList fInfix = tmp.parse(expression, vNames);
                        if (fInfix.size() == 1 && fInfix.get(0) instanceof Num) {
                            Num n = (Num) fInfix.get(0);
                            values[e] = n;
                        }
                        else {
                            Calculator calc = new Calculator();
                            calc.setInfix(fInfix);

                            values[e] = calc;
                        }
                    }
                    FunctionData fd = new FunctionData(f, values);

                    infixNotation.add(fd);
                }
                else {
                    parseInfixGroup(group, vNames);
                }
            }
        }

        return infixNotation;
    }

    
    private CList parseInfixGroup(String infix, LinkedHashMap<String, Num> vNames) throws ParseException {
    	final String REGEX_NUMBER = getRegexNumber();
    	
    	if (pUsedExtensions == null) {
            StringBuilder regex = new StringBuilder();
            regex.append(REGEX_NUMBER);

            // OPERATORS
            for (Entry<String, Class<? extends Operator>> op : CacheExtension.getOperatorSymbols().entrySet()) {
                String symbol = op.getKey();
                if (".+-*^$?|()".contains(symbol))
                    regex.append("|(\\" + symbol + ")");
                else
                    regex.append("|(" + symbol + ")");
            }

            if (usedExtensions != null) {
                for (Entry<String, Class<? extends Operator>> op : usedExtensions.getOperatorSymbols().entrySet()) {
                    String symbol = op.getKey();
                    if (".+-*^$?|()".contains(symbol))
                        regex.append("|(\\" + symbol + ")");
                    else
                        regex.append("|(" + symbol + ")");
                }
            }

            // Brackets
            for (Bracket bracket : Bracket.values()) {
                String symbol = bracket.getSymbol();
                if (".+-*^$?|()".contains(symbol))
                    regex.append("|(\\" + symbol + ")");
                else
                    regex.append("|(" + symbol + ")");
            }
            regex.append("|([a-zA-Z]+)"); // for expression with variable name

            pUsedExtensions = Pattern.compile(regex.toString());
        }

        boolean hasVariableNames = (vNames != null && vNames.size() != 0) ? true : false;
        Object prev = null;
        
        Matcher mat = pUsedExtensions.matcher(infix);
        while (mat.find()) {
            String group = mat.group();

            if (group.matches(REGEX_NUMBER)) {
                // parse negative number from expression 
                // e.g. 1 + -8 will parse -8. 
                //      1 - 8 will parse only 8
                if (prev != null && prev instanceof SubOperator) {
                    Object prev2 = null;
                    int size = infixNotation.size();
                    boolean isNegative = false;
                    
                    if (size >= 2) {
                        prev2 = infixNotation.get(infixNotation.size() - 2);
                        if (prev2 != null && (prev2 instanceof Operator || prev2 instanceof Bracket))
                            isNegative = true;
                    } else if (size == 1) {
                        isNegative = true;
                    }
                    
                    if (isNegative) {
                        group = "-" + group;
                        infixNotation.remove(infixNotation.size() - 1);
                    }
                }
                
                Num value = new Num(group);
                infixNotation.add(value);
                
                prev = value;
            }
            else if (group.matches(Bracket.OPEN.getRegex())) {
                infixNotation.add(Bracket.OPEN);
                prev = Bracket.OPEN;
            }
            else if (group.matches(Bracket.CLOSE.getRegex())) {
                infixNotation.add(Bracket.CLOSE);
                prev = Bracket.CLOSE;
            }
            else {
                boolean isExists = false;
                Extension op = null;
                
                // local scope
                if (usedExtensions != null)
                    op = usedExtensions.getOperator(group);

                // global scope
                if (op == null)
                    op = CacheExtension.getOperator(group);

                // append...
                if (op != null && op instanceof Operator) {
                    infixNotation.add((Operator) op);
                    isExists = true;
                    prev = op;
                }
                else if (hasVariableNames) { // find variable by name
                    Num variable = vNames.get(group);
                    infixNotation.add(variable);
                    isExists = true;
                    prev = variable;
                }

                if (!isExists)
                    throw new ParseException("Exception while parsing '" + infix + "'. Can't find extension '" + group + "' or " + Num.class.getName() + " with name '" + group + "'", 0);
            }
        }
        return infixNotation;
    }

    
    public static String toString(CList infixNotation) {
        return InfixParser.toString(infixNotation, false);
    }

    /**
     * Print infix (linear, human readable) representation of expression
     * e.g. 5 + 10 / (5 * 2)
     * 
     * @param calc
     * @return
     */
    public static String toString(CList infixNotation, boolean showDetails) {
        StringBuilder sb = new StringBuilder();
        Iterator<Object> it = infixNotation.iterator();
        while (it.hasNext()) {
            Object value = it.next();
            if (value instanceof Operator) {
                Operator op = (Operator) value;
                sb.append(op.getSymbol() + " ");
            }
            else if (value instanceof FunctionData) {
                FunctionData fd = (FunctionData) value;
                sb.append(fd.getFunction().getSymbol() + "(");
                int count = 0;
                for (Object fObject : fd.getValues()) {
                    if (count++ != 0)
                        sb.append(", ");

                    if (fObject instanceof Num) {
                        Num fValue = (Num) fObject;
                        String name = fValue.getName();
                        if (showDetails == true && name != null && name.length() != 0)
                            sb.append(name + "[" + fValue.toString() + "] ");
                        else
                            sb.append(fValue.toString());
                    }
                    else if (fObject instanceof AbstractCalculator) {
                        AbstractCalculator fValue = (AbstractCalculator) fObject;
                        try {
                            sb.append(fValue.getInfix());
                        }
                        catch (Exception e) {
                            sb.append("-error-");
                        }

                    }
                }
                sb.append(") ");
            }
            else if (value instanceof Num) {
                Num cv = (Num) value;
                String name = cv.getName();
                if (showDetails == true && name != null && name.length() != 0)
                    sb.append(name + "[" + cv.toString() + "] ");
                else
                    sb.append(cv.toString() + " ");
            }
            else if (value instanceof Bracket) {
                Bracket bracket = (Bracket) value;
                sb.append(bracket.getSymbol() + " ");
            }
            else {
                sb.append("? ");
            }
        }

        return sb.toString().trim();
    }

    private Num findValue(String name, boolean remove, Object... values) {
        for (int i = 0; i < values.length; i++) {
            Object o = values[i];
            if (o instanceof Num && name.equals(((Num) o).getName())) {
                if (remove)
                    values[i] = null;
                return (Num) o;
            }
        }

        return null;
    }

    /**
     * map variable names from expression with values
     * 
     * @param infix
     * @param values
     * @return
     *         @
     */
    private LinkedHashMap<String, Num> mapValues(String infix, Object... values) {
        LinkedHashMap<String, Num> vNames = null;
        int remain = 0;
      
        Matcher mat = pVariableNames.matcher(infix);
        while (mat.find()) {
            if (vNames == null)
                vNames = new LinkedHashMap<String, Num>();

            String vName = mat.group();
            if (!vNames.containsKey(vName)) {
                remain++;
                Num num = findValue(vName, true, values);
                vNames.put(vName, num);

                if (num != null)
                    remain--;
            }
        }

        // pair names with values without name
        if (vNames != null) {
            int lastPos = 0;
            for (Entry<String, Num> entry : vNames.entrySet()) {
                if (entry.getValue() == null) {
                    for (int i = lastPos; i < values.length; i++) {
                        Object v = values[i];
                        if (v != null) {
                            if (v instanceof Num) {
                                Num n = (Num) v;
                                if (n.getName() == null)
                                    entry.setValue(n);
                                else if (n.getName().equals(entry.getValue()))
                                    entry.setValue(n);
                            }
                            else
                                entry.setValue(Num.toNum(v));

                            values[i] = null;
                            lastPos = i;
                            remain--;
                        }
                    }
                }
            }
        }

        if (remain > 0 && vNames != null) {
            StringBuilder sb = new StringBuilder();
            int c = 0;
            for (Entry<String, Num> entry : vNames.entrySet()) {
                if (entry.getValue() == null) {
                    if (c++ > 0)
                        sb.append(", ");
                    sb.append(entry.getKey());
                }
            }

            throw new CalculatorException("Undefined values for expression (" + infix + ") variables: " + sb.toString());
        }

        return vNames;
    }

    /**
     * Count how many time needle appears in haystack
     * @param haystack
     * @param needle
     * @return
     */
    private int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle)
                count++;
        }
        return count;
    }

    /**
     * Get regular expression for parsing number from string
     * @return
     */
    private String getRegexNumber() {
    	return "([\\d, ]*\\" + properties.getInputDecimalSeparator() + "\\d*)|(\\d+)";
    }
}
