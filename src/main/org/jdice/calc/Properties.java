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
import java.net.URL;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Properties for {@link AbstractCalculator}, {@link Num}
 * 
 * With ability to declare global properties on scope of running application by providing properties file in class path.
 * Or by saving properties file with {@link #saveGlobalProperties()} 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 */
public class Properties {
	
	//
	// Defaults
	//
	public static final boolean DEFAULT_STRIP_TRAILING_ZEROS = false;
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
                    String propRM = prop.getProperty("calc.roundingMode");
                    if (propRM != null && propRM.trim().length() != 0) {
                        Rounding rm = Rounding.getRoundingMode(propRM.trim());
                        if (rm != null)
                            defRoundingMode = rm;
                    }

                    // calc.scale
                    String propS = prop.getProperty("calc.scale");
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
                    String propSTZ = prop.getProperty("calc.stripTrailingZeros");
                    if (propSTZ != null && propSTZ.trim().length() != 0) {
                        Boolean stz = Boolean.parseBoolean(propSTZ.trim());
                        if (stz != null)
                            defStripTrailingZeros = stz;
                    }

                    // calc.decimalSeparator = '.' or calc.decimalSeparator = "."
                    String propDS = prop.getProperty("calc.decimalSeparator");
                    if (propDS != null && propDS.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorIN = propDS.trim().charAt(1);
                    }

                    // calc.decimalSeparator.IN = '.' or calc.decimalSeparator.IN = "."
                    String propDSin = prop.getProperty("calc.decimalSeparator.in");
                    if (propDSin != null && propDSin.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorIN = propDSin.trim().charAt(1);
                    }

                    // calc.decimalSeparator.OUT = '.' or calc.decimalSeparator.OUT = "."
                    String propDSout = prop.getProperty("calc.decimalSeparator.out");
                    if (propDSout != null && propDSout.trim().matches("['\"](.){1}['\"]")) {
                        defDecimalSeparatorOUT = propDSout.trim().charAt(1);
                    }

                    // calc.groupingSeparator = ' ' or calc.groupingSeparator = " "
                    String propGS = prop.getProperty("calc.groupingSeparator");
                    if (propGS != null && propGS.trim().matches("['\"](.){1}['\"]")) {
                        defGroupingSeparator = propGS.trim().charAt(1);
                    }

                    // calc.outputFormat
                    String propOF = prop.getProperty("calc.outputFormat");
                    if (propOF != null && propOF.trim().length() != 0) {
                        defOutputFormat = propOF.trim();
                    }

                    //
                    // Load custom NumConverters
                    //
                    Cache.loadProperties(prop);

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

    /**
     * File location ..\bin\org.jdice.calc.properties
     * 
     * File content:
     * calc.roundingMode = HALF_UP
     * calc.scale = 3
     * calc.stripTrailingZeros = false
     * calc.decimalSeparator = '.' // IN/OUT
     * calc.decimalSeparator.IN = '.'
     * calc.decimalSeparator.OUT = '.'
     * calc.groupingSeparator= ' '
     * calc.outputFormat = # ### ### ##0.00
     * 
     * @throws IOException
     */
    public void saveGlobalProperties() throws IOException {
        File propFile = new File(getGlobalPropertiesFile());
        if (propFile.createNewFile() || propFile.isFile()) {
            java.util.Properties prop = new java.util.Properties();

            if (getRoundingMode() != null)
                prop.put("calc.roundingMode", getRoundingMode().name());
            if (getScale() != null)
                prop.put("calc.scale", getScale());

            prop.put("calc.stripTrailingZeros", Boolean.toString(hasStripTrailingZeros()));

            prop.put("calc.decimalSeparator.in", "'" + getInputDecimalSeparator() + "'");

            prop.put("calc.decimalSeparator.out", "'" + getOutputDecimalSeparator() + "'");

            if (getGroupingSeparator() != null)
                prop.put("calc.groupingSeparator", getGroupingSeparator());

            if (getOutputFormat() != null)
                prop.put("calc.outputFormat", getOutputFormat());

            //
            // Global NumConverter
            //
            HashMap<Class, NumConverter> cncs = Cache.getAllNumConverter();
            int count = 0;
            for (Entry<Class, NumConverter> cnc : cncs.entrySet()) {
                prop.put("calc.numconverter[" + count++ + "]", cnc.getKey().getName() + ":" + cnc.getValue().getClass().getName());
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
        String path = null;
        try {
            URL res = Properties.class.getResource(".");
            String abspath = res.getFile();
            Package pack = Properties.class.getPackage();
            final String CONF_PROPERTIES = "jcalc.properties";
            int c = pack.getName().split("\\.").length;

            // get lib root
            for (int i = 0; i <= c; i++)
                abspath = abspath.substring(0, abspath.lastIndexOf('/'));

            path = abspath + File.separatorChar + CONF_PROPERTIES;
        }
        catch (Exception e) {

        }
        return path;
    }

}
