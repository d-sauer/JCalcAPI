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

import org.jdice.calc.internal.CacheExtension;

/**
 * This interface is extended by {@link Operator} or {@link Function} 
 * During execution every class that implement this interface will have only one instance stored into cache {@link CacheExtension}.
 * They with will act like Singleton classes unless they are instantiated outside scope of calculation engine.
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */
public interface Extension {

    /**
     * Symbol that represent mathematical operator or function
     * @return
     */
    public String getSymbol();
    
}
