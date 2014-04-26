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

import org.jdice.calc.operation.Abs;
import org.jdice.calc.operation.AbsFunction;
import org.jdice.calc.operation.Add;
import org.jdice.calc.operation.AddOperator;
import org.jdice.calc.operation.Div;
import org.jdice.calc.operation.DivOperator;
import org.jdice.calc.operation.Log;
import org.jdice.calc.operation.LogFunction;
import org.jdice.calc.operation.Mod;
import org.jdice.calc.operation.ModOperator;
import org.jdice.calc.operation.Mul;
import org.jdice.calc.operation.MulOperator;
import org.jdice.calc.operation.Pow;
import org.jdice.calc.operation.PowOperator;
import org.jdice.calc.operation.Sqrt;
import org.jdice.calc.operation.SqrtFunction;
import org.jdice.calc.operation.Sub;
import org.jdice.calc.operation.SubOperator;

/**
 * Calculator implementation with basic operations.
 * Support operation: Add, Sub, Div, Mul, Mod, Pow, Abs, Sqrt, Log
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public class Calc extends AbstractCalculator<Calc> implements Add<Calc>,
        Sub<Calc>, Div<Calc>, Mul<Calc>, Mod<Calc>, Pow<Calc>, Abs<Calc>,
        Sqrt<Calc>, Log<Calc> {

    protected Calc getThis() {
        return this;
    }

    public Calc() {
        super();
    }

    //
    // Builder
    //
    public static Calc builder() {
        Calc calc = new Calc();
        return calc;
    }

    public static Calc builder(String expression) throws ParseException {
        Calc calc = new Calc();
        calc.expression(expression);
        return calc;
    }

    public static Calc builder(String expression, Object... values) throws ParseException {
        Calc calc = new Calc();
        calc.expression(expression, values);
        return calc;
    }

    //
    // DEFAULT OPERATIONS
    //
    // -----------------
    @Override
    public Calc add() {
        return append(AddOperator.class);
    }

    @Override
    public Calc add(Object value) {
        return append(AddOperator.class, value);
    }

    @Override
    public Calc add(String value, char decimalSeparator) {
        return append(AddOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public Calc sub() {
        return append(SubOperator.class);
    }

    @Override
    public Calc sub(Object value) {
        return append(SubOperator.class, value);
    }

    @Override
    public Calc sub(String value, char decimalSeparator) {
        return append(SubOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calc div() {
        return append(DivOperator.class);
    }

    @Override
    public Calc div(Object value) {
        return append(DivOperator.class, value);
    }

    @Override
    public Calc div(String value, char decimalSeparator) {
        return append(DivOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calc mul() {
        return append(MulOperator.class);
    }

    @Override
    public Calc mul(Object value) {
        return append(MulOperator.class, value);
    }

    @Override
    public Calc mul(String value, char decimalSeparator) {
        return append(MulOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calc pow() {
        return append(PowOperator.class);
    }

    @Override
    public Calc pow(Object value) {
        return append(PowOperator.class, value);
    }

    @Override
    public Calc pow(String value, char decimalSeparator) {
        return append(PowOperator.class, value, decimalSeparator);
    }

    @Override
    public Calc abs(AbstractCalculator expression) {
        return append(AbsFunction.class, expression);
    }

    @Override
    public Calc abs(Object value) {
        return append(AbsFunction.class, new Num(value));
    }

    @Override
    public Calc abs(String value, char decimalSeparator) {
        return append(AbsFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public Calc sqrt(AbstractCalculator expression) {
        return append(SqrtFunction.class, expression);
    }

    @Override
    public Calc sqrt(Object value) {
        return append(SqrtFunction.class, new Num(value));
    }

    @Override
    public Calc sqrt(String value, char decimalSeparator) {
        return append(SqrtFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public Calc mod() {
        return append(ModOperator.class);
    }

    @Override
    public Calc mod(Object value) {
        return append(ModOperator.class, value);
    }

    @Override
    public Calc mod(String value, char decimalSeparator) {
        return append(ModOperator.class, value, decimalSeparator);
    }

    @Override
    public Calc log(AbstractCalculator expression) {
        return append(LogFunction.class, expression);
    }

    @Override
    public Calc log(Object value) {
        return append(LogFunction.class, value);
    }

    @Override
    public Calc log(String value, char decimalSeparator) {
        return append(LogFunction.class, value, decimalSeparator);
    }

}
