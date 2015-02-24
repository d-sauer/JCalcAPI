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

package org.jdice.calc.extension;

import org.jdice.calc.AbstractCalculator;
import org.jdice.calc.BindExtension;

/**
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 * @param <CALC>
 */
@BindExtension(implementation = ArcTanFunction.class)
public interface ArcTan<CALC> {

    public CALC atan(AbstractCalculator expression);

    public CALC atan(Object value);

    public CALC atan(String value, char decimalSeparator);

}
