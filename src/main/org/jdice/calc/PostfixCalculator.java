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

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

import org.jdice.calc.operation.AddOperator;
import org.jdice.calc.operation.SubOperator;

/**
 * Calculation from postfix
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
class PostfixCalculator {

    private Stack<Object> stack = new Stack<Object>();
    private CList postfix = new CList();
    private int bCount = 0;
    LinkedList<Object> step = null;
    private LinkedList<String> steps = null;

    public void toPostfix(CList infix) {
        Iterator<Object> it = infix.iterator();
        while (it.hasNext()) {
            Object current = it.next();

            if (current instanceof Operator || current instanceof Bracket) {
                // Check for open/close brackets
                if (current instanceof Bracket) {
                    Bracket bracket = (Bracket) current;
                    if (bracket == Bracket.OPEN)
                        bCount++;
                    else if (bracket == Bracket.CLOSE)
                        bCount--;
                }

                int currentPriority = getPriority(current);
                if (stack.size() == 0)
                    stack.push(current);
                else if (stack.size() > 0 && Bracket.CLOSE.equals(current)) {
                    while (stack.size() > 0 && !Bracket.OPEN.equals(stack.peek())) {
                        postfix.add((Operator) stack.pop());
                    }
                    stack.pop();
                }
                else if (stack.size() > 0) {
                    if (Bracket.OPEN.equals(current) && Bracket.OPEN.equals(stack.peek()) || (!Bracket.OPEN.equals(current) && getPriority(stack.peek()) >= currentPriority)) {
                        while (stack.size() > 0 && !Bracket.OPEN.equals(stack.peek()) && getPriority(stack.peek()) >= currentPriority) {
                            Object o = stack.pop();
                            if (o instanceof Operator)
                                postfix.add((Operator) o);
                        }
                        stack.push(current);
                    }
                    else if (getPriority(stack.peek()) < currentPriority) {
                        stack.push(current);
                    }
                }
            }
            else if (current instanceof Num) {
                Num calcValue = (Num) current;
                postfix.add(calcValue);
            }
            else if (current instanceof FunctionData) {
                FunctionData calcValue = (FunctionData) current;
                postfix.add(calcValue);
            }
        }
        popAll();

        missingBracketDetection(infix);
    }

    private int getPriority(Object input) {
        Bracket inputBracket = input instanceof Bracket ? (Bracket) input : null;
        Operator inputOperation = input instanceof Operator ? (Operator) input : null;
        int priority = (inputBracket != null) ? inputBracket.getPriority() : inputOperation.getPriority();

        return priority;
    }

    private void popAll()  {
        Enumeration<Object> e = stack.elements();
        while (e.hasMoreElements()) {
            Object peek = stack.peek();
            Bracket peekBracket = peek instanceof Bracket ? (Bracket) peek : null;

            if (peekBracket != null)
                stack.pop();
            else
                postfix.add((Operator) stack.pop());
        }
    }

    public CList getPostfix() {
        return postfix;
    }

    public Num calculate(AbstractCalculator calc, CList postfix)  {
        return calculate(calc, postfix, false, false);
    }

    public Num calculate(AbstractCalculator calc, CList postfix, boolean trackSteps, boolean showDetails)  {
        try {
            return calc(calc, postfix, trackSteps, showDetails);
        }
        catch (Exception e) {
            if (trackSteps == false) { // retrack steps to find problem
                try {
                    calc(calc, postfix, true, showDetails);
                } catch(Exception e2) {}
            }
            StringBuilder sb = new StringBuilder();
            if (steps != null) {
                int count = 0;
                int sSize = steps.size();
                for (String s : steps) {
                    count++;
                    sb.append(s);

                    if (count >= sSize)
                        sb.append("  <--- OK\n");
                    else
                        sb.append("\n");
                }

                String es = getStep(step, showDetails);
                sb.append(es + "  <--- Error: " + e.getMessage());
            }

            throw new CalculatorException("Error during calculation. Check if expression is correct: " + calc.getInfix() + "\n" + sb.toString(), e);
        }
    }

    private Num calc(AbstractCalculator calc, CList postfix, boolean trackSteps, boolean showDetails)  {
        if (trackSteps)
            steps = new LinkedList<String>();

        Stack<Object> values = new Stack<Object>();
        Iterator<Object> e = postfix.iterator();
        while (e.hasNext()) {

            Object o = e.next();
            if (o instanceof Num) {
                Num value = (Num) o;
                values.push(value);
            }
            else if (o instanceof FunctionData) {
                FunctionData function = (FunctionData) o;
                values.push(function);
            }
            else if (o instanceof Operator) {
                if (trackSteps)
                    step = new LinkedList<Object>();

                Operator operator = (Operator) o;

                Num left = null;
                Num right = null;

                //
                // Pop values from stack. First is right and second is left
                //
                Object oRight = null;
                try {
                    oRight = values.pop();
                } catch(Exception eR) {
                    throw new CalculatorException("Missing right operand");
                }

                Object oLeft = null;
                try {
                    oLeft = values.pop();
                } catch(Exception eL) {
                    if (operator instanceof SubOperator || operator instanceof AddOperator) {
                        oLeft = new Num(0);
                    } else {
                        throw new CalculatorException("Missing operand to the left of the operator '" + operator.getSymbol() + "'");
                    }
                }

                if (oLeft instanceof FunctionData) {
                    FunctionData fLeft = (FunctionData) oLeft;
                    trackStep(step, fLeft);
                    left = fLeft.calc(calc);
                    trackStep(step, ":", left, null);
                }
                else {
                    left = (Num) oLeft;
                    trackStep(step, left);
                }

                trackStep(step, operator);

                if (oRight instanceof FunctionData) {
                    FunctionData fRight = (FunctionData) oRight;
                    trackStep(step, fRight);
                    right = fRight.calc(calc);
                    trackStep(step, ":", right, null);
                }
                else {
                    right = (Num) oRight;
                    trackStep(step, right);
                }

                Num result = null;
                try {
                    result = operator.calc(calc, left, right);
                }
                catch (Exception e1) {
                    throw new CalculatorException(calc, "Error during calculation.", e1);
                }
                if (result == null)
                    result = new Num();

                trackStep(step, "\t = ", result, "");

                values.push(result);

                if (steps != null)
                    steps.add(getStep(step, showDetails));
            }

        }

        Object oResult = values.pop();
        Num result = null;
        if (oResult instanceof Num)
            result = (Num) oResult;
        else if (oResult instanceof FunctionData) {
            FunctionData fd = (FunctionData)oResult;
            result = fd.calc(calc);
        }

        if (trackSteps)
            calc.setSteps(steps);

        result.getProperties().load(calc.getProperties());
        return result;
    }

    private String getStep(LinkedList<Object> step, boolean showDetail)  {
        StringBuilder sb = new StringBuilder();
        if (step != null)
            for (Object o : step) {
                if (o instanceof Num) {
                    Num d = (Num) o;
                    if (showDetail)
                        sb.append("[" + d.getProperties().toString() + "] " + d.toString());
//                        sb.append(d.toStringWithDetail());
                    else
                        sb.append(d.toString());
                }
                else if (o instanceof FunctionData) {
                    FunctionData fd = (FunctionData) o;
                    if (showDetail)
                        sb.append(fd.toStringWithDetail());
                    else
                        sb.append(fd.toString());
                }
                else if (o instanceof Operator) {
                    Operator op = (Operator) o;
                    sb.append("\t" + op.getSymbol() + "  ");
                }
                else if (o instanceof String) {
                    String s = (String) o;
                    sb.append(s);
                }
                else {
                    sb.append(o.toString());
                }
            }

        return sb.toString();
    }

    private void trackStep(LinkedList<Object> step, Object o) {
        trackStep(step, null, o, null);
    }

    private void trackStep(LinkedList<Object> step, String before, Object o, String after) {
        if (step != null) {
            if (before != null && before.length() != 0)
                step.add(before);

            step.add(o);

            if (after != null && after.length() != 0)
                step.add(after);
        }
    }

    private void missingBracketDetection(CList infix) throws CalculatorException {
        if (bCount > 0) // to many open bracket - need to close some bracket
            throw new CalculatorException("To many open bracket. " + InfixParser.printInfix(infix));
        else if (bCount < 0) // to many closed bracket - need to reopen some bracket
            throw new CalculatorException("To many close bracket. " + InfixParser.printInfix(infix));
    }

}
