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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Global cache for operator and function classes and instances
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public class Cache {

    private static volatile OperationRegister cacheData = new OperationRegister();
    private static volatile HashMap<Class, NumConverter> converterCache = new HashMap<Class, NumConverter>();
    private static volatile boolean numConverterPropLoaded = false;

    public static void registerOperator(Class<? extends Operator> operatorClass) {
        cacheData.registerOperator(operatorClass);
    }

    public static HashMap<String, Class<? extends Operator>> getOperatorSymbols() {
        return cacheData.getOperatorSymbols();
    }

    public static Operator getOperator(Class<? extends Operator> operatorClass) {
        return cacheData.getOperator(operatorClass);
    }

    public static Operator getOperator(String operator) {
        return cacheData.getOperator(operator);
    }

    static HashMap<Class<? extends Operator>, Operator> getOperators() {
        return cacheData.getOperators();
    }

    public static void registerFunction(Class<? extends Function> functionClass) {
        cacheData.registerFunction(functionClass);
    }

    public static HashMap<String, Class<? extends Function>> getFunctionSymbols() {
        return cacheData.getFunctionSymbols();
    }

    public static Function getFunction(String function) {
        return cacheData.getFunction(function);
    }

    public static Function getFunction(Class<? extends Function> functionClass) {
        return cacheData.getFunction(functionClass);
    }

    static HashMap<Class<? extends Function>, Function> getFunctions() {
        return cacheData.getFunctions();
    }
    /**
     * Register custom converter class on global scope.
     * 
     * @param customClass define class type for conversion
     * @param converterClass define class which knows how to convert <tt>customClass</tt>
     */
    public static void registerNumConverter(Class customClass, Class<? extends NumConverter> converterClass) {
        NumConverter nc = converterCache.get(customClass);
        if (nc == null) {
            synchronized (converterClass) {
                nc = converterCache.get(customClass);
                if (nc == null) {
                    try {
                        nc = (NumConverter) converterClass.newInstance();
                    }
                    catch (Exception e) {
                        throw new CalculatorException(e);
                    }
                    
                    if (nc != null) {
                        if (converterClass.isAnnotationPresent(SingletonComponent.class))
                            converterCache.put(customClass, nc);
                    }
                }
            }
        }
    }

    /**
     * Register custom converter class on global scope.
     * 
     * @param customClass define class type for conversion
     * @param converter define instance which knows how to convert <tt>customClass</tt>
     */
    public static NumConverter registerNumConverter(Class customClass, NumConverter converter) {
		converterCache.put(customClass, converter);
		return converter;
    }

    public static NumConverter getNumConverter(Class<?> customClass) {
    	NumConverter nc = converterCache.get(customClass);
    	return nc;
    }
    
    public static NumConverter getNumConverter(Class<?> customClass, Class<? extends NumConverter> convertClass) {
        if (numConverterPropLoaded == false)
            loadNumConvertersFromPropertiesFile(null);

        NumConverter nc = converterCache.get(convertClass);
        
        if (nc == null) {
        	try {
        	    synchronized (convertClass) {
    				nc = convertClass.newInstance();
    				if (convertClass.isAnnotationPresent(SingletonComponent.class))
    					converterCache.put(customClass, nc);
        	    }
			} catch (Exception e) {
				throw new CalculatorException(e);
			}
        }
        return nc;
    }

    public static HashMap<Class, NumConverter> getAllNumConverter() {
        return converterCache;
    }

    static void loadNumConvertersFromPropertiesFile(String absolutePath) {
        if (absolutePath == null) {
        	numConverterPropLoaded = true;
            absolutePath = Properties.getGlobalPropertiesFile();
        }

        File propFile = new File(absolutePath);
        if (propFile.exists() && propFile.isFile() && propFile.canRead()) {
            java.util.Properties prop = new java.util.Properties();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(propFile);
                prop.load(fis);
                loadProperties(prop);
            }
            catch (Exception e) {

            }
            finally {
                if (fis != null) {
                    try {
                        fis.close();
                    }
                    catch (IOException e) {
                    }
                }
            }
        }
    }

    static void loadProperties(java.util.Properties prop) {
        for (Entry<Object, Object> kv : prop.entrySet()) {
            if (kv.getKey() instanceof String) {
                String key = (String) kv.getKey();
                
                //
                // NumConverter
                //
                if (key.startsWith("numconverter[")) {
                    Object oValue = kv.getValue();
                    if (oValue instanceof String) {
                        String[] value = ((String) oValue).split(">");
                        if (value.length == 2) {
                            try {
                                Class customClass = Class.forName(value[0].trim());
                                Class converterClass = Class.forName(value[1].trim());

                                if (NumConverter.class.isAssignableFrom(converterClass)) {
                                    Cache.registerNumConverter(customClass, converterClass);
                                }
                            }
                            catch (ClassNotFoundException e) {
                            }

                        }
                    }
                }
                
                //
                // Operator
                //
                if (key.startsWith("operator[")) {
                    Object oValue = kv.getValue();
                    if (oValue instanceof String) {
                            try {
                                Class<?> customOperator = Class.forName(((String)oValue).trim());
                                
                                if (Operator.class.isAssignableFrom(customOperator)) {
                                    Cache.registerOperator(customOperator.asSubclass(Operator.class));
                                }
                            }
                            catch (ClassNotFoundException e) {
                            }
                            
                    }
                }

                //
                // Function
                //
                if (key.startsWith("function[")) {
                    Object oValue = kv.getValue();
                    if (oValue instanceof String) {
                        try {
                            Class<?> customFunction = Class.forName(((String)oValue).trim());
                            
                            if (Function.class.isAssignableFrom(customFunction)) {
                                Cache.registerFunction(customFunction.asSubclass(Function.class));
                            }
                        }
                        catch (ClassNotFoundException e) {
                        }
                        
                    }
                }
                
            }
        }
    }
}
