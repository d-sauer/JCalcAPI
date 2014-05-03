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
import org.jdice.calc.extension.ArcCos;
import org.jdice.calc.extension.ArcCosFunction;
import org.jdice.calc.extension.ArcSin;
import org.jdice.calc.extension.ArcSinFunction;
import org.jdice.calc.extension.ArcTan;
import org.jdice.calc.extension.ArcTanFunction;
import org.jdice.calc.extension.Cos;
import org.jdice.calc.extension.CosFunction;
import org.jdice.calc.extension.Cosh;
import org.jdice.calc.extension.CoshFunction;
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
import org.jdice.calc.extension.Sin;
import org.jdice.calc.extension.SinFunction;
import org.jdice.calc.extension.Sinh;
import org.jdice.calc.extension.SinhFunction;
import org.jdice.calc.extension.Sqrt;
import org.jdice.calc.extension.SqrtFunction;
import org.jdice.calc.extension.Sub;
import org.jdice.calc.extension.SubOperator;
import org.jdice.calc.extension.Tan;
import org.jdice.calc.extension.TanFunction;
import org.jdice.calc.extension.Tanh;
import org.jdice.calc.extension.TanhFunction;

/**
 * Calculator implementation with trigonometric functions.
 * Support operation: Sin, Cos, Tan, ArcSin, ArcCos, ArcTan, Sinh, Cosh, Tanh, Add, Sub, Div, Mul, Mod, Pow, Abs, Sqrt, Log
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public final class TrigCalculator extends AbstractCalculator<TrigCalculator> implements
        Sin<TrigCalculator>, Cos<TrigCalculator>, Tan<TrigCalculator>, ArcSin<TrigCalculator>,
        ArcCos<TrigCalculator>, ArcTan<TrigCalculator>, Sinh<TrigCalculator>, Cosh<TrigCalculator>,
        Tanh<TrigCalculator>, Add<TrigCalculator>, Sub<TrigCalculator>, Div<TrigCalculator>,
        Mul<TrigCalculator>, Mod<TrigCalculator>, Pow<TrigCalculator>, Abs<TrigCalculator>,
        Sqrt<TrigCalculator>, Log<TrigCalculator> {

    @Override
    protected TrigCalculator getThis() {
        return this;
    }

    //
    // Builder
    //
    public static TrigCalculator builder() {
        TrigCalculator calc = new TrigCalculator();
        return calc;
    }

    public static TrigCalculator builder(String expression) throws ParseException {
        TrigCalculator calc = new TrigCalculator();
        calc.expression(expression);
        return calc;
    }

    public static TrigCalculator builder(String expression, Object... values) throws ParseException {
        TrigCalculator calc = new TrigCalculator();
        calc.expression(expression, values);
        return calc;
    }

    //
    // DEFAULT OPERATIONS
    //
    // -----------------
    @Override
    public TrigCalculator add() {
        return append(AddOperator.class);
    }

    @Override
    public TrigCalculator add(Object value) {
        return append(AddOperator.class, value);
    }

    @Override
    public TrigCalculator add(String value, char decimalSeparator) {
        return append(AddOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public TrigCalculator sub() {
        return append(SubOperator.class);
    }

    @Override
    public TrigCalculator sub(Object value) {
        return append(SubOperator.class, value);
    }


    @Override
    public TrigCalculator sub(String value, char decimalSeparator) {
        return append(SubOperator.class, value, decimalSeparator);
    }


    // -----------------

    @Override
    public TrigCalculator div() {
        return append(DivOperator.class);
    }

    @Override
    public TrigCalculator div(Object value) {
        return append(DivOperator.class, value);
    }

    @Override
    public TrigCalculator div(String value, char decimalSeparator) {
        return append(DivOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public TrigCalculator mul() {
        return append(MulOperator.class);
    }

    @Override
    public TrigCalculator mul(Object value) {
        return append(MulOperator.class, value);
    }

    @Override
    public TrigCalculator mul(String value, char decimalSeparator) {
        return append(MulOperator.class, value, decimalSeparator);
    }

    // -----------------

    @Override
    public TrigCalculator pow() {
        return append(PowOperator.class);
    }

    @Override
    public TrigCalculator pow(Object value) {
        return append(PowOperator.class, value);
    }

    @Override
    public TrigCalculator pow(String value, char decimalSeparator) {
        return append(PowOperator.class, value, decimalSeparator);
    }


    @Override
    public TrigCalculator abs(AbstractCalculator expression) {
        return append(AbsFunction.class);
    }

    @Override
    public TrigCalculator abs(Object value) {
        return append(AbsFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator abs(String value, char decimalSeparator) {
        return append(AbsFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator sqrt(AbstractCalculator expression) {
        return append(SqrtFunction.class, expression);
    }

    @Override
    public TrigCalculator sqrt(Object value) {
        return append(SqrtFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator sqrt(String value, char decimalSeparator) {
        return append(SqrtFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator mod() {
        return append(ModOperator.class);
    }

    @Override
    public TrigCalculator mod(Object value) {
        return append(ModOperator.class, value);
    }

    @Override
    public TrigCalculator mod(String value, char decimalSeparator) {
        return append(ModOperator.class, value, decimalSeparator);
    }

    //
    // OPERATIONS
    //
    @Override
    public TrigCalculator sin(AbstractCalculator expression) {
        return append(SinFunction.class, expression);
    }

    @Override
    public TrigCalculator sin(Object value) {
        return append(SinFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator sin(String value, char decimalSeparator) {
        return append(SinFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator cos(AbstractCalculator expression) {
        return append(CosFunction.class, expression);
    }

    @Override
    public TrigCalculator cos(Object value) {
        return append(CosFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator cos(String value, char decimalSeparator) {
        return append(CosFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator tan(AbstractCalculator expression) {
        return append(TanFunction.class, expression);
    }

    @Override
    public TrigCalculator tan(Object value) {
        return append(TanFunction.class, new Num(value));
    }


    @Override
    public TrigCalculator tan(String value, char decimalSeparator) {
        return append(TanFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator atan(AbstractCalculator expression) {
        return append(ArcTanFunction.class, expression);
    }

    @Override
    public TrigCalculator atan(Object value) {
        return append(ArcTanFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator atan(String value, char decimalSeparator) {
        return append(ArcTanFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator acos(AbstractCalculator expression) {
        return append(ArcCosFunction.class, expression);
    }

    @Override
    public TrigCalculator acos(Object value) {
        return append(ArcCosFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator acos(String value, char decimalSeparator) {
        return append(ArcCosFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator asin(AbstractCalculator expression) {
        return append(ArcSinFunction.class, expression);
    }

    @Override
    public TrigCalculator asin(Object value) {
        return append(ArcSinFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator asin(String value, char decimalSeparator) {
        return append(ArcSinFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator tanh(AbstractCalculator expression) {
        return append(TanhFunction.class, expression);
    }

    @Override
    public TrigCalculator tanh(Object value) {
        return append(TanhFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator tanh(String value) {
        return append(TanhFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator tanh(String value, char decimalSeparator) {
        return append(TanhFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator cosh(AbstractCalculator expression) {
        return append(CoshFunction.class, expression);
    }

    @Override
    public TrigCalculator cosh(Object value) {
        return append(CoshFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator cosh(String value, char decimalSeparator) {
        return append(CoshFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator sinh(AbstractCalculator expression) {
        return append(SinhFunction.class, expression);
    }

    @Override
    public TrigCalculator sinh(Object value) {
        return append(SinhFunction.class, new Num(value));
    }

    @Override
    public TrigCalculator sinh(String value, char decimalSeparator) {
        return append(SinhFunction.class, new Num(value, decimalSeparator));
    }

    @Override
    public TrigCalculator log(AbstractCalculator expression) {
        return append(LogFunction.class, expression);
    }

    @Override
    public TrigCalculator log(Object value) {
        return append(LogFunction.class, value);
    }

    @Override
    public TrigCalculator log(String value, char decimalSeparator) {
        return append(LogFunction.class, value);
    }

}
