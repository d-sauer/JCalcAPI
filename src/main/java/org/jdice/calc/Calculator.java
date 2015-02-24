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

import org.jdice.calc.extension.Abs;
import org.jdice.calc.extension.AbsFunction;
import org.jdice.calc.extension.Add;
import org.jdice.calc.extension.AddOperator;
import org.jdice.calc.extension.Div;
import org.jdice.calc.extension.DivOperator;
import org.jdice.calc.extension.Log;
import org.jdice.calc.extension.LogFunction;
import org.jdice.calc.extension.Mod;
import org.jdice.calc.extension.ModOperator;
import org.jdice.calc.extension.Mul;
import org.jdice.calc.extension.MulOperator;
import org.jdice.calc.extension.Pow;
import org.jdice.calc.extension.PowOperator;
import org.jdice.calc.extension.Sqrt;
import org.jdice.calc.extension.SqrtFunction;
import org.jdice.calc.extension.Sub;
import org.jdice.calc.extension.SubOperator;

/**
 * Calculator implementation with basic operations.
 * Support extensions: Add, Sub, Div, Mul, Mod, Pow, Abs, Sqrt, Log
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public final class Calculator extends AbstractCalculator<Calculator> implements Add<Calculator>,
        Sub<Calculator>, Div<Calculator>, Mul<Calculator>, Mod<Calculator>, Pow<Calculator>, Abs<Calculator>,
        Sqrt<Calculator>, Log<Calculator> {

    protected Calculator getThis() {
        return this;
    }

    //
    // Builder
    //
    public static Calculator builder() {
        Calculator calc = new Calculator();
        return calc;
    }

    public static Calculator builder(String expression) throws ParseException {
        Calculator calc = new Calculator();
        calc.expression(expression);
        return calc;
    }

    public static Calculator builder(String expression, Object... values) throws ParseException {
        Calculator calc = new Calculator();
        calc.expression(expression, values);
        return calc;
    }

    //
    // DEFAULT OPERATIONS
    //
    // -----------------
    @Override
    public Calculator add() {
        return operator(AddOperator.class);
    }

    @Override
    public Calculator add(Object value) {
        return operator(AddOperator.class, value);
    }

    @Override
    public Calculator add(String value, char decimalSeparator) {
        return operator(AddOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public Calculator sub() {
        return operator(SubOperator.class);
    }

    @Override
    public Calculator sub(Object value) {
        return operator(SubOperator.class, value);
    }

    @Override
    public Calculator sub(String value, char decimalSeparator) {
        return operator(SubOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calculator div() {
        return operator(DivOperator.class);
    }

    @Override
    public Calculator div(Object value) {
        return operator(DivOperator.class, value);
    }

    @Override
    public Calculator div(String value, char decimalSeparator) {
        return operator(DivOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calculator mul() {
        return operator(MulOperator.class);
    }

    @Override
    public Calculator mul(Object value) {
        return operator(MulOperator.class, value);
    }

    @Override
    public Calculator mul(String value, char decimalSeparator) {
        return operator(MulOperator.class, value, decimalSeparator);
    }

    // -----------------
    @Override
    public Calculator pow() {
        return operator(PowOperator.class);
    }

    @Override
    public Calculator pow(Object value) {
        return operator(PowOperator.class, value);
    }

    @Override
    public Calculator pow(String value, char decimalSeparator) {
        return operator(PowOperator.class, value, decimalSeparator);
    }

    @Override
    public Calculator abs(AbstractCalculator expression) {
        return function(AbsFunction.class, expression);
    }

    @Override
    public Calculator abs(Object value) {
        return function(AbsFunction.class, new Num(value));
    }

    @Override
    public Calculator abs(String value, char decimalSeparator) {
        return function(AbsFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public Calculator sqrt(AbstractCalculator expression) {
        return function(SqrtFunction.class, expression);
    }

    @Override
    public Calculator sqrt(Object value) {
        return function(SqrtFunction.class, new Num(value));
    }

    @Override
    public Calculator sqrt(String value, char decimalSeparator) {
        return function(SqrtFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public Calculator mod() {
        return operator(ModOperator.class);
    }

    @Override
    public Calculator mod(Object value) {
        return operator(ModOperator.class, value);
    }

    @Override
    public Calculator mod(String value, char decimalSeparator) {
        return operator(ModOperator.class, value, decimalSeparator);
    }

    @Override
    public Calculator log(AbstractCalculator expression) {
        return function(LogFunction.class, expression);
    }

    @Override
    public Calculator log(Object value) {
        return function(LogFunction.class, value);
    }

    @Override
    public Calculator log(String value, char decimalSeparator) {
        return function(LogFunction.class, value, decimalSeparator);
    }

}
