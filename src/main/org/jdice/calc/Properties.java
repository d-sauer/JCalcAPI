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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import org.jdice.calc.internal.CacheExtension;
import org.jdice.calc.internal.Objects;

/**
 * Properties for {@link AbstractCalculator}, {@link Num}
 * 
 * With ability to declare global properties on scope of running application by providing properties file in class path.
 * Or by saving properties file with {@link #saveGlobalProperties()} 
 * 
 * <br/>
 * It's possible to define default configuration with <b>jcalc.properties</b> file in root of classpath.
 * <br/>
 * Example of jcalc.properties file where all properties are optional:
 * 
 * <pre>
 *   roundingMode=HALF_UP
 *   scale=2
 *   stripTrailingZeros=true
 *   decimalSeparator.out='.'
 *   decimalSeparator.in='.'
 *   numconverter[0]=org.jdice.calc.test.NumTest$CustomObject > org.jdice.calc.test.NumTest$CustomObjectNumConverter
 *   operator[0]=org.jdice.calc.test.CustomOperatorFunctionTest$QuestionOperator
 *   function[0]=org.jdice.calc.test.CustomOperatorFunctionTest$SumFunction
 * </pre>
 * @author Davor Sauer <davor.sauer@gmail.com>
 */
public class Properties implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    //
	// Defaults
	//
	public static final boolean DEFAULT_STRIP_TRAILING_ZEROS = true;
	public static final String DEFAULT_OUTPUT_FORMAT = "#,###,###,##0.00";
	public static final int DEFAULT_SCALE = 64;
	public static final char DEFAULT_DECIMAL_SEPARATOR = '.';
	public static final Rounding DEFAULT_ROUNDING_MODE = Rounding.HALF_UP;
	
    //
    // set defaults / load global scope defaults
    //
    private static Rounding defRoundingMode = Properties.DEFAULT_ROUNDING_MODE;
    private static Integer defScale;
    private static boolean defStripTrailingZeros = Properties.DEFAULT_STRIP_TRAILING_ZEROS;
    private static char defDecimalSeparatorIN = Properties.DEFAULT_DECIMAL_SEPARATOR;
    private static char defDecimalSeparatorOUT = Properties.DEFAULT_DECIMAL_SEPARATOR;
    private static Character defGroupingSeparator;
    private static String defOutputFormat;

    static {
        String propPath = getGlobalPropertiesFile();
        if (propPath != null) {
            File propFile = new File(propPath);
            if (propFile != null && propFile.exists() && propFile.isFile() && propFile.canRead()) {
                java.util.Properties prop = new java.util.Properties();
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(propFile);
                    prop.load(fis);

                    // calc.roundingMode
                    String propRM = prop.getProperty("roundingMode");
                    if (propRM != null && propRM.trim().length() != 0) {
                        Rounding rm = Rounding.getRoundingMode(propRM.trim());
                        if (rm != null)
                            defRoundingMode = rm;
                    }

                    // calc.scale
                    String propS = prop.getProperty("scale");
                    if (propS != null && propS.trim().length() != 0) {
                        try {
                            Integer sc = Integer.parseInt(propS);
                            if (sc != null)
                                defScale = sc;
                        }
                        catch (Exception e) {
                        }
                    }

                    // calc.stripTrailingZeros
                    String propSTZ = prop.getProperty("stripTrailingZeros");
                    if (propSTZ != null && propSTZ.trim().length() != 0) {
                        Boolean stz = Boolean.parseBoolean(propSTZ.trim());
                        if (stz != null)
                            defStripTrailingZeros = stz;
                    }

                    // calc.decimalSeparator = '.' or calc.decimalSeparator = "."
                    String propDS = prop.getProperty("decimalSeparator");
                    if (propDS != null && propDS.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorIN = propDS.trim().charAt(1);
                    }

                    // calc.decimalSeparator.IN = '.' or calc.decimalSeparator.IN = "."
                    String propDSin = prop.getProperty("decimalSeparator.in");
                    if (propDSin != null && propDSin.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorIN = propDSin.trim().charAt(1);
                    }

                    // calc.decimalSeparator.OUT = '.' or calc.decimalSeparator.OUT = "."
                    String propDSout = prop.getProperty("decimalSeparator.out");
                    if (propDSout != null && propDSout.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorOUT = propDSout.trim().charAt(1);
                    }

                    // calc.groupingSeparator = ' ' or calc.groupingSeparator = " "
                    String propGS = prop.getProperty("groupingSeparator");
                    if (propGS != null && propGS.trim().matches("['\"](.){1}['\"]")) {
                        defGroupingSeparator = propGS.trim().charAt(1);
                    }

                    // calc.outputFormat
                    String propOF = prop.getProperty("outputFormat");
                    if (propOF != null && propOF.trim().length() != 0) {
                        defOutputFormat = propOF.trim();
                    }

                    //
                    // Load custom NumConverters, Operators, Functions
                    //
                    CacheExtension.loadProperties(prop);

                }
                catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                finally {
                    if (fis != null)
                        try {
                            fis.close();
                        }
                        catch (IOException e) {
                        }
                }
            }
        }
    }

    //
    // Set instance defaults
    //
    private Rounding roundingMode = defRoundingMode;
    private Integer scale = defScale;
    private boolean stripTrailingZeros = defStripTrailingZeros;
    private char decimalSeparatorIN = defDecimalSeparatorIN;
    private char decimalSeparatorOUT = defDecimalSeparatorOUT;
    private Character groupingSeparator = defGroupingSeparator;
    private String outputFormat = defOutputFormat;
	
    
    public Properties() {
        
    }
    
    public Rounding getRoundingMode() {
        return roundingMode;
    }

    public Properties setRoundingMode(Rounding roundingMode) {
        this.roundingMode = roundingMode;
        return this;
    }

    public Integer getScale() {
        return scale;
    }

    public Properties setScale(Integer scale) {
		if (scale != null && scale >= 0)
			this.scale = scale;
		else
			this.scale = null;
		
        return this;
    }

    public boolean hasStripTrailingZeros() {
        return stripTrailingZeros;
    }

    public Properties setStripTrailingZeros(boolean stripTrailingZeros) {
        this.stripTrailingZeros = stripTrailingZeros;
        return this;
    }

    public String getOutputFormat() {
        return outputFormat;
    }

    public Properties setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
        return this;
    }

    public char getInputDecimalSeparator() {
        return decimalSeparatorIN;
    }

    public char getOutputDecimalSeparator() {
        return decimalSeparatorOUT;
    }

    public Properties setDecimalSeparator(char inputDecimalSeparator, char outputDecimalSeparator) {
        this.decimalSeparatorIN = inputDecimalSeparator;
        this.decimalSeparatorOUT = outputDecimalSeparator;
        return this;
    }

    public Properties setInputDecimalSeparator(char inputDecimalSeparator) {
        this.decimalSeparatorIN = inputDecimalSeparator;
        return this;
    }

    public Properties setOutputDecimalSeparator(char outputDecimalSeparator) {
        this.decimalSeparatorOUT = outputDecimalSeparator;
        return this;
    }

    public Character getGroupingSeparator() {
        return groupingSeparator;
    }

    public void setGroupingSeparator(Character groupingSeparator) {
        this.groupingSeparator = groupingSeparator;
    }

    public void load(Properties properties) {
        setScale(properties.getScale());

        setRoundingMode(properties.getRoundingMode());

        setStripTrailingZeros(properties.hasStripTrailingZeros());

        setOutputFormat(properties.getOutputFormat());

        setInputDecimalSeparator(properties.getInputDecimalSeparator());

        setOutputDecimalSeparator(properties.getOutputDecimalSeparator());

        setGroupingSeparator(properties.getGroupingSeparator());
    }

    @Override
    public String toString() {
        return "scale: " + (scale != null ? scale : "none")
                + ", rounding mode: " + roundingMode
                + ", strip trailing zeros: " + stripTrailingZeros
                + ", decimal separator: " + decimalSeparatorIN
                + (groupingSeparator != null ? ", grouping separator: '" + groupingSeparator + "'" : "")
                + ", outputFormat: "
                + (outputFormat != null ? outputFormat : "none");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        else if (obj == this)
            return true;
        else if (obj instanceof Properties) {
            Properties second = (Properties) obj;
            
            if(!Objects.equals(this.getGroupingSeparator(), second.getGroupingSeparator()))
                return false;

            if(!Objects.equals(this.getInputDecimalSeparator(), second.getInputDecimalSeparator()))
                return false;

            if(!Objects.equals(this.getOutputDecimalSeparator(), second.getOutputDecimalSeparator()))
                return false;

            if(!Objects.equals(this.getOutputFormat(), second.getOutputFormat()))
                return false;

            if(!Objects.equals(this.getRoundingMode(), second.getRoundingMode()))
                return false;

            if(!Objects.equals(this.getScale(), second.getScale()))
                return false;
            
            if(!Objects.equals(this.hasStripTrailingZeros(), second.hasStripTrailingZeros()))
                return false;

            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[] {groupingSeparator, decimalSeparatorIN, decimalSeparatorOUT, outputFormat, roundingMode, scale, stripTrailingZeros});
    }
    
    /**
     * File location ..\bin\org.jdice.calc.properties
     * 
     * File content:
     *   roundingMode=HALF_UP
     *   stripTrailingZeros=true
     *   decimalSeparator.out='.'
     *   decimalSeparator.in='.'
     *   numconverter[0]=org.jdice.calc.test.NumTest$CustomObject > org.jdice.calc.test.NumTest$CustomObjectNumConverter
     *   operator[0]=org.jdice.calc.test.CustomOperatorFunctionTest$QuestionOperator
     *   function[0]=org.jdice.calc.test.CustomOperatorFunctionTest$SumFunction
     * 
     * @throws IOException
     */
    public void saveGlobalProperties() throws IOException {
        File propFile = new File(getGlobalPropertiesFile());
        if (propFile.createNewFile() || propFile.isFile()) {
            java.util.Properties prop = new java.util.Properties();

            if (getRoundingMode() != null)
                prop.put("roundingMode", getRoundingMode().name());
            if (getScale() != null)
                prop.put("scale", getScale());

            prop.put("stripTrailingZeros", Boolean.toString(hasStripTrailingZeros()));

            prop.put("decimalSeparator.in", "'" + getInputDecimalSeparator() + "'");

            prop.put("decimalSeparator.out", "'" + getOutputDecimalSeparator() + "'");

            if (getGroupingSeparator() != null)
                prop.put("groupingSeparator", getGroupingSeparator());

            if (getOutputFormat() != null)
                prop.put("outputFormat", getOutputFormat());

            //
            // Global NumConverter
            //
            HashMap<Class, NumConverter> cncs = CacheExtension.getAllNumConverter();
            int count = 0;
            for (Entry<Class, NumConverter> cnc : cncs.entrySet()) {
                prop.put("numconverter[" + count++ + "]", cnc.getKey().getName() + " > " + cnc.getValue().getClass().getName());
            }
            
            //
            // Global Operator
            //
            HashMap<Class<? extends Operator>, Operator> cops = CacheExtension.getOperators();
            count = 0;
            for (Entry<Class<? extends Operator>, Operator> cop : cops.entrySet()) {
                prop.put("operator[" + count++ + "]", cop.getKey().getName());
            }

            //
            // Global Function
            //
            HashMap<Class<? extends Function>, Function> cfns = CacheExtension.getFunctions();
            count = 0;
            for (Entry<Class<? extends Function>, Function> cfn : cfns.entrySet()) {
                prop.put("function[" + count++ + "]", cfn.getKey().getName());
            }
            
            FileOutputStream fos = new FileOutputStream(propFile);
            prop.store(fos, "Global properties for jCalc");

            fos.close();
            fos.flush();
        }
    }

    /**
     * If {@link Num} don't define scale then use scale from {@link AbstractCalculator} instance.
     * Othervise use default scale {@link Properties#DEFAULT_SCALE}
     * 
     * @param calc
     * @param value
     */
    public static Rounding getInheritedRoundingMode(AbstractCalculator calc, Num value) {
        if (value.getRoundingMode() == null) {
            if (calc != null && calc.getRoundingMode() != null)
                return calc.getRoundingMode();
            else
                return Properties.defRoundingMode;
        }
        else
            return value.getRoundingMode();
    }

    public static Properties getInheritedProperties(AbstractCalculator calc, Num value) {
        Properties prop = new Properties(); // load global and default properties
        
        
        
        
        return prop;
    }
    
    /**
     * If {@link Num} don't define scale then use scale from {@link AbstractCalculator} instance.
     * Othervise use default scale {@link Properties#DEFAULT_SCALE}
     * 
     * @param calc
     * @param value
     */
    public static int getInheritedScale(AbstractCalculator calc, Num value) {
        if (value.getScale() == null) {
            if (calc != null && calc.getScale() != null)
                return calc.getScale();
            else
                return Properties.DEFAULT_SCALE;
        }
        else
            return value.getScale();
    }

    public static String getGlobalPropertiesFile() {
        final String CONF_PROPERTIES = "jcalc.properties";

        String path = null;
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader(); 
            URL res = classLoader.getResource(".");
            String abspath = res.getFile();

            if (abspath.endsWith(File.separator))
                path = abspath + CONF_PROPERTIES;
            else
                path = abspath + File.separatorChar + CONF_PROPERTIES;
        }
        catch (Exception e) {

        }
        return path;
    }

}
