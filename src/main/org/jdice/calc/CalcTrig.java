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
 * Calculator extension with trigonometric functions
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

	// 1
	public CalcTrig(short value) {
		super(value);
	}

	// 2
	public CalcTrig(int value) {
		super(value);
	}

	// 3
	public CalcTrig(long value) {
		super(value);
	}

	// 4
	public CalcTrig(float value) {
		super(value);
	}

	// 5
	public CalcTrig(double value) {
		super(value);
	}

	// 6
	public CalcTrig(Object value) {
		super(value);
	}

	// 7
	public CalcTrig(String value) {
		super(value);
	}

	// 7
	public CalcTrig(String value, char decimalSeparator) {
		super(value, decimalSeparator);
	}

	// 8
	public CalcTrig(Num value) {
		super(value);
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
		calc.parse(expression);
		return calc;
	}

	public static CalcTrig builder(String expression, Object... values)
			throws ParseException {
		CalcTrig calc = new CalcTrig();
		calc.parse(expression, values);
		return calc;
	}

	//
	// DEFAULT OPERATIONS
	//
	// -----------------
	public CalcTrig add() {
		return append(AddOperator.class);
	}

	// 1
	public CalcTrig add(short value) {
		return append(AddOperator.class, value);
	}

	// 2
	public CalcTrig add(int value) {
		return append(AddOperator.class, value);
	}

	// 3
	public CalcTrig add(long value) {
		return append(AddOperator.class, value);
	}

	// 4
	public CalcTrig add(float value) {
		return append(AddOperator.class, value);
	}

	// 5
	public CalcTrig add(double value) {
		return append(AddOperator.class, value);
	}

	// 6
	public CalcTrig add(Object value) {
		return append(AddOperator.class, value);
	}

	// 7
	public CalcTrig add(String value) {
		return append(AddOperator.class, value);
	}

	// 7
	public CalcTrig add(String value, char decimalSeparator) {
		return append(AddOperator.class, value, decimalSeparator);
	}

	// 8
	public CalcTrig add(Num value) {
		return append(AddOperator.class, value);
	}

	// -----------------

	public CalcTrig subtract() {
		return append(SubOperator.class);
	}

	// 1
	public CalcTrig sub(short value) {
		return append(SubOperator.class, value);
	}

	// 2
	public CalcTrig sub(int value) {
		return append(SubOperator.class, value);
	}

	// 3
	public CalcTrig sub(long value) {
		return append(SubOperator.class, value);
	}

	// 4
	public CalcTrig sub(float value) {
		return append(SubOperator.class, value);
	}

	// 5
	public CalcTrig sub(double value) {
		return append(SubOperator.class, value);
	}

	// 6
	public CalcTrig sub(Object value) {
		return append(SubOperator.class, value);
	}

	// 7
	public CalcTrig sub(String value) {
		return append(SubOperator.class, value);
	}

	// 7
	public CalcTrig sub(String value, char decimalSeparator) {
		return append(SubOperator.class, value, decimalSeparator);
	}

	// 8
	public CalcTrig sub(Num value) {
		return append(SubOperator.class, value);
	}

	// -----------------

	public CalcTrig divide() {
		return append(DivOperator.class);
	}

	// 1
	public CalcTrig div(short value) {
		return append(DivOperator.class, value);
	}

	// 2
	public CalcTrig div(int value) {
		return append(DivOperator.class, value);
	}

	// 3
	public CalcTrig div(long value) {
		return append(DivOperator.class, value);
	}

	// 4
	public CalcTrig div(float value) {
		return append(DivOperator.class, value);
	}

	// 5
	public CalcTrig div(double value) {
		return append(DivOperator.class, value);
	}

	// 6
	public CalcTrig div(Object value) {
		return append(DivOperator.class, value);
	}

	// 7
	public CalcTrig div(String value) {
		return append(DivOperator.class, value);
	}

	// 7
	public CalcTrig div(String value, char decimalSeparator) {
		return append(DivOperator.class, value, decimalSeparator);
	}

	// 8
	public CalcTrig div(Num value) {
		return append(DivOperator.class, value);
	}

	// -----------------

	public CalcTrig multiply() {
		return append(MulOperator.class);
	}

	// 1
	public CalcTrig mul(short value) {
		return append(MulOperator.class, value);
	}

	// 2
	public CalcTrig mul(int value) {
		return append(MulOperator.class, value);
	}

	// 3
	public CalcTrig mul(long value) {
		return append(MulOperator.class, value);
	}

	// 4
	public CalcTrig mul(float value) {
		return append(MulOperator.class, value);
	}

	// 5
	public CalcTrig mul(double value) {
		return append(MulOperator.class, value);
	}

	// 6
	public CalcTrig mul(Object value) {
		return append(MulOperator.class, value);
	}

	// 7
	public CalcTrig mul(String value) {
		return append(MulOperator.class, value);
	}

	// 7
	public CalcTrig mul(String value, char decimalSeparator) {
		return append(MulOperator.class, value, decimalSeparator);
	}

	// 8
	public CalcTrig mul(Num value) {
		return append(MulOperator.class, value);
	}

	// -----------------

	public CalcTrig power() {
		return append(PowOperator.class);
	}

	// 1
	public CalcTrig pow(short value) {
		return append(PowOperator.class, value);
	}

	// 2
	public CalcTrig pow(int value) {
		return append(PowOperator.class, value);
	}

	// 3
	public CalcTrig pow(long value) {
		return append(PowOperator.class, value);
	}

	// 4
	public CalcTrig pow(float value) {
		return append(PowOperator.class, value);
	}

	// 5
	public CalcTrig pow(double value) {
		return append(PowOperator.class, value);
	}

	// 6
	public CalcTrig pow(Object value) {
		return append(PowOperator.class, value);
	}

	// 7
	public CalcTrig pow(String value) {
		return append(PowOperator.class, value);
	}

	// 7
	public CalcTrig pow(String value, char decimalSeparator) {
		return append(PowOperator.class, value, decimalSeparator);
	}

	// 8
	public CalcTrig pow(Num value) {
		return append(PowOperator.class, value);
	}

	public CalcTrig abs(AbstractCalculator expression) {
		return append(AbsFunction.class);
	}

	public CalcTrig abs(short value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(int value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(long value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(float value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(double value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(Object value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(String value) {
		return append(AbsFunction.class, new Num(value));
	}

	public CalcTrig abs(String value, char decimalSeparator) {
		return append(AbsFunction.class, new Num(value, decimalSeparator));
	}

	public CalcTrig abs(Num value) {
		return append(AbsFunction.class, value);
	}

	@Override
	public CalcTrig sqrt(AbstractCalculator expression) {
		return append(SqrtFunction.class, expression);
	}

	public CalcTrig sqrt(short value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(int value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(long value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(float value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(double value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(Object value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(String value) {
		return append(SqrtFunction.class, new Num(value));
	}

	public CalcTrig sqrt(String value, char decimalSeparator) {
		return append(SqrtFunction.class, new Num(value, decimalSeparator));
	}

	public CalcTrig sqrt(Num value) {
		return append(SqrtFunction.class, value);
	}

	public CalcTrig modulo() {
		return append(ModOperator.class);
	}

	public CalcTrig mod(short value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(int value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(long value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(float value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(double value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(Object value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(String value) {
		return append(ModOperator.class, value);
	}

	public CalcTrig mod(String value, char decimalSeparator) {
		return append(ModOperator.class, value, decimalSeparator);
	}

	public CalcTrig mod(Num value) {
		return append(ModOperator.class, value);
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
	public CalcTrig sin(String value) {
		return append(SinFunction.class, new Num(value));
	}

	@Override
	public CalcTrig sin(String value, char decimalSeparator) {
		return append(SinFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig sin(Num value) {
		return append(SinFunction.class, value);
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
	public CalcTrig cos(String value) {
		return append(CosFunction.class, new Num(value));
	}

	@Override
	public CalcTrig cos(String value, char decimalSeparator) {
		return append(CosFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig cos(Num value) {
		return append(CosFunction.class, value);
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
	public CalcTrig tan(String value) {
		return append(TanFunction.class, new Num(value));
	}

	@Override
	public CalcTrig tan(String value, char decimalSeparator) {
		return append(TanFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig tan(Num value) {
		return append(TanFunction.class, value);
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
	public CalcTrig atan(String value) {
		return append(ArcTanFunction.class, new Num(value));
	}

	@Override
	public CalcTrig atan(String value, char decimalSeparator) {
		return append(ArcTanFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig atan(Num value) {
		return append(ArcTanFunction.class, value);
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
	public CalcTrig acos(String value) {
		return append(ArcCosFunction.class, new Num(value));
	}

	@Override
	public CalcTrig acos(String value, char decimalSeparator) {
		return append(ArcCosFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig acos(Num value) {
		return append(ArcCosFunction.class, value);
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
	public CalcTrig asin(String value) {
		return append(ArcSinFunction.class, new Num(value));
	}

	@Override
	public CalcTrig asin(String value, char decimalSeparator) {
		return append(ArcSinFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig asin(Num value) {
		return append(ArcSinFunction.class, value);
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
	public CalcTrig tanh(Num value) {
		return append(TanhFunction.class, value);
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
	public CalcTrig cosh(String value) {
		return append(CoshFunction.class, new Num(value));
	}

	@Override
	public CalcTrig cosh(String value, char decimalSeparator) {
		return append(CoshFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig cosh(Num value) {
		return append(CoshFunction.class, value);
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
	public CalcTrig sinh(String value) {
		return append(SinhFunction.class, new Num(value));
	}

	@Override
	public CalcTrig sinh(String value, char decimalSeparator) {
		return append(SinhFunction.class, new Num(value, decimalSeparator));
	}

	@Override
	public CalcTrig sinh(Num value) {
		return append(SinhFunction.class, value);
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
	public CalcTrig log(String value) {
		return append(LogFunction.class, value);
	}

	@Override
	public CalcTrig log(String value, char decimalSeparator) {
		return append(LogFunction.class, value);
	}

	@Override
	public CalcTrig log(Num value) {
		return append(LogFunction.class, value);
	}

}
