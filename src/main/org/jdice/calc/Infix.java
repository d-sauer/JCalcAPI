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
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Infix manipulation and parsing.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
class Infix {

    private static String REGEX_VARIABLE_NAMES = "([a-zA-Z]+)\\b(?!\\s*\\()";
    private static String REGEX_FUNCTIONS = "[a-zA-Z0-9]+\\(((?<=(?:[(]))[^(].*?(?=[)]))"; // e.g. abs(..) <- function | abs <- name, no open brackets

    private static Pattern pVariableNames;
    private static Pattern pFunctions;
    private static Pattern pOperations;

    private CList infixList = new CList();
    private OperationRegister registeredOperations;
    private Properties properties;

    public Infix(Properties properties) {
    	this.properties = properties;
    }

    public Infix(OperationRegister operationRegister, Properties properties) {
        this.registeredOperations = operationRegister;
        this.properties = properties;
    }

    public static CList parseInfix(OperationRegister operationRegister, Properties properties, String infix, Object... values) throws ParseException {
        Infix infx = new Infix(operationRegister, properties);
        return infx.parse(infix, values);
    }

    public CList parse(String infix, Object... values) throws ParseException {
        // get variable names
        LinkedHashMap<String, Num> vNames = mapValues(infix, values);

        return parse(infix, vNames);
    }

    private CList parse(String infix, LinkedHashMap<String, Num> vNames) throws ParseException {

        // Separate formulas and non formula parts
        infix = infix.replace(" ", "");
        int infixLength = infix.length();
        // separate function and other operation
        if (pFunctions == null)
            pFunctions = Pattern.compile(REGEX_FUNCTIONS);
        Matcher mat = pFunctions.matcher(infix);

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
                String group = infix.substring(section[0], section[1]);

                if (section[2] == 1) { // function
                    String function = group.substring(0, group.indexOf("("));

                    Operation op = null;
                    // local scope
                    if (registeredOperations != null)
                        op = registeredOperations.getFunction(function);

                    // global scope
                    if (op == null)
                        op = Cache.getFunction(function);

                    if (op == null)
                        throw new CalculatorException("Can't find '" + function + "' function implementation class used in equation " + infix);

                    Function f = (Function) op;
                    String _equation = group.substring(group.indexOf("(") + 1, group.length() - 1);
                    String[] equations = _equation.split(",");
                    Object[] values = new Object[equations.length];

                    for (int e = 0; e < equations.length; e++) {
                        String equation = equations[e];

                        Infix tmp = new Infix(properties);
                        CList fInfix = tmp.parse(equation, vNames);
                        if (fInfix.size() == 1 && fInfix.get(0) instanceof Num) {
                            Num n = (Num) fInfix.get(0);
                            values[e] = n;
                        }
                        else {
                            Calc calc = new Calc();
                            calc.setInfix(fInfix);

                            values[e] = calc;
                        }
                    }
                    FunctionData fd = new FunctionData(f, values);

                    infixList.add(fd);
                }
                else {
                    parseInfixString(group, vNames);
                }
            }
        }

        return infixList;
    }

    private CList parseInfixString(String infix, LinkedHashMap<String, Num> vNames) throws ParseException {
    	final String REGEX_NUMBER = getRegexNumber();
    	
    	if (pOperations == null) {
            StringBuilder regex = new StringBuilder();
            regex.append(REGEX_NUMBER);

            // OPERATORS
            for (Entry<String, Class<? extends Operator>> op : Cache.getOperatorSymbols().entrySet()) {
                String symbol = op.getKey();
                if (".+-*^$?|()".contains(symbol))
                    regex.append("|(\\" + symbol + ")");
                else
                    regex.append("|(" + symbol + ")");
            }

            if (registeredOperations != null) {
                for (Entry<String, Class<? extends Operator>> op : registeredOperations.getOperatorSymbols().entrySet()) {
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
            regex.append("|([a-zA-Z]+)"); // for equation with variable name

            pOperations = Pattern.compile(regex.toString());
        }

        boolean hasVariableNames = (vNames != null && vNames.size() != 0) ? true : false;

        Matcher mat = pOperations.matcher(infix);
        while (mat.find()) {
            String group = mat.group();

            if (group.matches(REGEX_NUMBER)) {
                Num value = new Num(group);
                infixList.add(value);
            }
            else if (group.matches(Bracket.OPEN.getRegex())) {
                infixList.add(Bracket.OPEN);
            }
            else if (group.matches(Bracket.CLOSE.getRegex())) {
                infixList.add(Bracket.CLOSE);
            }
            else {
                boolean isExists = false;
                Operation op = null;
                // local scope
                if (registeredOperations != null)
                    op = registeredOperations.getOperator(group);

                // global scope
                if (op == null)
                    op = Cache.getOperator(group);

                // append...
                if (op != null && op instanceof Operator) {
                    infixList.add((Operator) op);
                    isExists = true;
                }
                else if (hasVariableNames) { // find variable by name
                    Num variable = vNames.get(group);
                    infixList.add(variable);
                    isExists = true;
                }

                if (!isExists)
                    throw new ParseException("Exception while parsing '" + infix + "'. Can't find operation '" + group + "' or " + Num.class.getName() + " with name '" + group + "'", 0);
            }
        }
        return infixList;
    }

    public static String printInfix(CList list) {
        return Infix.printInfix(list, false);
    }

    /**
     * Infix (linear, human readable) representation of equation
     * e.g. 5 + 10 / (5 * 2)
     * 
     * @param calc
     * @return
     */
    public static String printInfix(CList list, boolean showDetails) {
        StringBuilder sb = new StringBuilder();
        Iterator<Object> it = list.iterator();
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
                            sb.append(name + "[" + fValue.get() + "] ");
                        else
                            sb.append(fValue.get());
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
                    sb.append(name + "[" + cv.get() + "] ");
                else
                    sb.append(cv.get() + " ");
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
     * map variable names from equation with values
     * 
     * @param infix
     * @param values
     * @return
     *         @
     */
    private LinkedHashMap<String, Num> mapValues(String infix, Object... values) {
        LinkedHashMap<String, Num> vNames = null;
        int remain = 0;
        if (pVariableNames == null)
            pVariableNames = Pattern.compile(REGEX_VARIABLE_NAMES);
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

            throw new CalculatorException("Undefined values for equation (" + infix + ") variables: " + sb.toString());
        }

        return vNames;
    }

    private int countOccurrences(String haystack, char needle) {
        int count = 0;
        for (int i = 0; i < haystack.length(); i++) {
            if (haystack.charAt(i) == needle)
                count++;
        }
        return count;
    }

    private String getRegexNumber() {
    	return "([\\d, ]*\\" + properties.getInputDecimalSeparator() + "\\d*)|(\\d+)";
    }
}
