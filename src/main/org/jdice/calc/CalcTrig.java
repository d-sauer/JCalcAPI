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
import org.jdice.calc.operation.ArcCos;
import org.jdice.calc.operation.ArcCosFunction;
import org.jdice.calc.operation.ArcSin;
import org.jdice.calc.operation.ArcSinFunction;
import org.jdice.calc.operation.ArcTan;
import org.jdice.calc.operation.ArcTanFunction;
import org.jdice.calc.operation.Cos;
import org.jdice.calc.operation.CosFunction;
import org.jdice.calc.operation.Cosh;
import org.jdice.calc.operation.CoshFunction;
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
import org.jdice.calc.operation.Sin;
import org.jdice.calc.operation.SinFunction;
import org.jdice.calc.operation.Sinh;
import org.jdice.calc.operation.SinhFunction;
import org.jdice.calc.operation.Sqrt;
import org.jdice.calc.operation.SqrtFunction;
import org.jdice.calc.operation.Sub;
import org.jdice.calc.operation.SubOperator;
import org.jdice.calc.operation.Tan;
import org.jdice.calc.operation.TanFunction;
import org.jdice.calc.operation.Tanh;
import org.jdice.calc.operation.TanhFunction;

/**
 * Calculator implementation with trigonometric functions.
 * Support operation: Sin, Cos, Tan, ArcSin, ArcCos, ArcTan, Sinh, Cosh, Tanh, Add, Sub, Div, Mul, Mod, Pow, Abs, Sqrt, Log
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public class CalcTrig extends AbstractCalculator<CalcTrig> implements
        Sin<CalcTrig>, Cos<CalcTrig>, Tan<CalcTrig>, ArcSin<CalcTrig>,
        ArcCos<CalcTrig>, ArcTan<CalcTrig>, Sinh<CalcTrig>, Cosh<CalcTrig>,
        Tanh<CalcTrig>, Add<CalcTrig>, Sub<CalcTrig>, Div<CalcTrig>,
        Mul<CalcTrig>, Mod<CalcTrig>, Pow<CalcTrig>, Abs<CalcTrig>,
        Sqrt<CalcTrig>, Log<CalcTrig> {

    @Override
    protected CalcTrig getThis() {
        return this;
    }

    public CalcTrig() {
        super();
    }

    //
    // Builder
    //
    public static CalcTrig builder() {
        CalcTrig calc = new CalcTrig();
        return calc;
    }

    public static CalcTrig builder(String expression) throws ParseException {
        CalcTrig calc = new CalcTrig();
        calc.expression(expression);
        return calc;
    }

    public static CalcTrig builder(String expression, Object... values) throws ParseException {
        CalcTrig calc = new CalcTrig();
        calc.expression(expression, values);
        return calc;
    }

    //
    // DEFAULT OPERATIONS
    //
    // -----------------
    @Override
    public CalcTrig add() {
        return append(AddOperator.class);
    }

    @Override
    public CalcTrig add(Object value) {
        return append(AddOperator.class, value);
    }

    @Override
    public CalcTrig add(String value, char decimalSeparator) {
        return append(AddOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public CalcTrig sub() {
        return append(SubOperator.class);
    }

    @Override
    public CalcTrig sub(Object value) {
        return append(SubOperator.class, value);
    }


    @Override
    public CalcTrig sub(String value, char decimalSeparator) {
        return append(SubOperator.class, value, decimalSeparator);
    }


    // -----------------

    @Override
    public CalcTrig div() {
        return append(DivOperator.class);
    }

    @Override
    public CalcTrig div(Object value) {
        return append(DivOperator.class, value);
    }

    @Override
    public CalcTrig div(String value, char decimalSeparator) {
        return append(DivOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public CalcTrig mul() {
        return append(MulOperator.class);
    }

    @Override
    public CalcTrig mul(Object value) {
        return append(MulOperator.class, value);
    }

    @Override
    public CalcTrig mul(String value, char decimalSeparator) {
        return append(MulOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public CalcTrig pow() {
        return append(PowOperator.class);
    }

    @Override
    public CalcTrig pow(Object value) {
        return append(PowOperator.class, value);
    }

    @Override
    public CalcTrig pow(String value, char decimalSeparator) {
        return append(PowOperator.class, value, decimalSeparator);
    }


    @Override
    public CalcTrig abs(AbstractCalculator expression) {
        return append(AbsFunction.class);
    }

    @Override
    public CalcTrig abs(Object value) {
        return append(AbsFunction.class, new Num(value));
    }

    @Override
    public CalcTrig abs(String value, char decimalSeparator) {
        return append(AbsFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig sqrt(AbstractCalculator expression) {
        return append(SqrtFunction.class, expression);
    }

    @Override
    public CalcTrig sqrt(Object value) {
        return append(SqrtFunction.class, new Num(value));
    }

    @Override
    public CalcTrig sqrt(String value, char decimalSeparator) {
        return append(SqrtFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig mod() {
        return append(ModOperator.class);
    }

    @Override
    public CalcTrig mod(Object value) {
        return append(ModOperator.class, value);
    }

    @Override
    public CalcTrig mod(String value, char decimalSeparator) {
        return append(ModOperator.class, value, decimalSeparator);
    }

    //
    // OPERATIONS
    //
    @Override
    public CalcTrig sin(AbstractCalculator expression) {
        return append(SinFunction.class, expression);
    }

    @Override
    public CalcTrig sin(Object value) {
        return append(SinFunction.class, new Num(value));
    }

    @Override
    public CalcTrig sin(String value, char decimalSeparator) {
        return append(SinFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig cos(AbstractCalculator expression) {
        return append(CosFunction.class, expression);
    }

    @Override
    public CalcTrig cos(Object value) {
        return append(CosFunction.class, new Num(value));
    }

    @Override
    public CalcTrig cos(String value, char decimalSeparator) {
        return append(CosFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig tan(AbstractCalculator expression) {
        return append(TanFunction.class, expression);
    }

    @Override
    public CalcTrig tan(Object value) {
        return append(TanFunction.class, new Num(value));
    }


    @Override
    public CalcTrig tan(String value, char decimalSeparator) {
        return append(TanFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig atan(AbstractCalculator expression) {
        return append(ArcTanFunction.class, expression);
    }

    @Override
    public CalcTrig atan(Object value) {
        return append(ArcTanFunction.class, new Num(value));
    }

    @Override
    public CalcTrig atan(String value, char decimalSeparator) {
        return append(ArcTanFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig acos(AbstractCalculator expression) {
        return append(ArcCosFunction.class, expression);
    }

    @Override
    public CalcTrig acos(Object value) {
        return append(ArcCosFunction.class, new Num(value));
    }

    @Override
    public CalcTrig acos(String value, char decimalSeparator) {
        return append(ArcCosFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig asin(AbstractCalculator expression) {
        return append(ArcSinFunction.class, expression);
    }

    @Override
    public CalcTrig asin(Object value) {
        return append(ArcSinFunction.class, new Num(value));
    }

    @Override
    public CalcTrig asin(String value, char decimalSeparator) {
        return append(ArcSinFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig tanh(AbstractCalculator expression) {
        return append(TanhFunction.class, expression);
    }

    @Override
    public CalcTrig tanh(Object value) {
        return append(TanhFunction.class, new Num(value));
    }

    @Override
    public CalcTrig tanh(String value) {
        return append(TanhFunction.class, new Num(value));
    }

    @Override
    public CalcTrig tanh(String value, char decimalSeparator) {
        return append(TanhFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig cosh(AbstractCalculator expression) {
        return append(CoshFunction.class, expression);
    }

    @Override
    public CalcTrig cosh(Object value) {
        return append(CoshFunction.class, new Num(value));
    }

    @Override
    public CalcTrig cosh(String value, char decimalSeparator) {
        return append(CoshFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig sinh(AbstractCalculator expression) {
        return append(SinhFunction.class, expression);
    }

    @Override
    public CalcTrig sinh(Object value) {
        return append(SinhFunction.class, new Num(value));
    }

    @Override
    public CalcTrig sinh(String value, char decimalSeparator) {
        return append(SinhFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public CalcTrig log(AbstractCalculator expression) {
        return append(LogFunction.class, expression);
    }

    @Override
    public CalcTrig log(Object value) {
        return append(LogFunction.class, value);
    }

    @Override
    public CalcTrig log(String value, char decimalSeparator) {
        return append(LogFunction.class, value);
    }

}
