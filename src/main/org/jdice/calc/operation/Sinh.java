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

package org.jdice.calc.operation;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.Implementation;
import org.jdice.calc.Num;

/**
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 * @param <CALC>
 */
@Implementation(implementatio = SinhFunction.class)
public interface Sinh<CALC> {

	public CALC sinh(AbstractCalculator equation);

	public CALC sinh(Object value);

	public CALC sinh(String value);

	public CALC sinh(String value, char decimalSeparator);

	public CALC sinh(Num value);
}