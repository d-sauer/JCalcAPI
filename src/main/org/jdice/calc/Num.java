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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdice.calc.internal.CacheExtension;

/**
 * 
 * Mutable wrapper object around BigDecimal object. Supported input type: short,
 * int, long, float, double, Short, Integer, Long, Float, Double, BigInteger,
 * BigDecimal, String <br/>
 * AbstractCalculator, Num
 * 
 * To support custom type objects, you can use {@link NumConverter} to implement
 * conversion logic from specific type to <tt>Num</tt>.
 * 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 * 
 */
public class Num implements Cloneable, Comparable<Num>, Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;
	private Properties properties;
	private Object originalValue;

	private BigDecimal in = BigDecimal.ZERO;
	private BigDecimal out = BigDecimal.ZERO;

    /**
     * Create Num instance with zero default value
     */
    public Num() {
    }

	/**
	 * Create Num instance with value of given Object
	 * 
	 * @param value
	 *            that can be short, int, long, float, double, Short, Integer,
	 *            Long, Float, Double, BigInteger, BigDecimal, String or custom
	 *            object which need conversion with {@link NumConverter}
	 */
	public Num(Object value) {
		setValue(value, null, null);
	}

	/**
	 * Create Num instance with specific name and value of given Object
	 * 
	 * @param name
	 *            used in expression with unknown value
	 * @param value
	 *            that can be short, int, long, float, double, Short, Integer,
	 *            Long, Float, Double, BigInteger, BigDecimal, String or custom
	 *            object which need conversion with {@link NumConverter}
	 */
	public Num(String name, Object value) {
		this(value);
		this.name = name;
	}

	/**
	 * Create Num instance with value of given Object
	 * 
	 * @param value
	 *            that can be short, int, long, float, double, Short, Integer,
	 *            Long, Float, Double, BigInteger, BigDecimal, String or custom
	 *            object which need conversion with {@link NumConverter}
	 * @param converter
	 *            class used for converting custom object.
	 * @see NumConverter
	 */
	public Num(Object value, Class<? extends NumConverter> converter) {
		setValue(value, null, converter);
	}

	/**
	 * Create Num instance with specific name and value of given Object
	 * 
	 * @param name
	 *            used in expression with unknown value
	 * 
	 * @param value
	 *            that can be short, int, long, float, double, Short, Integer,
	 *            Long, Float, Double, BigInteger, BigDecimal, String or custom
	 *            object which need conversion with {@link NumConverter}
	 * @param converter
	 *            class used for converting custom object.
	 * @see NumConverter
	 */
	public Num(String name, Object value, Class<? extends NumConverter> converter) {
		this.name = name;
		setValue(value, null, converter);
	}

	/**
	 * Create Num instance with value from String which use defined decimal
	 * separator
	 * 
	 * @param value
	 * @param decimalSeparator
	 *            used in String value
	 */
	public Num(String value, char decimalSeparator) {
		setValue(value, decimalSeparator, null);
	}

	/**
	 * Create Num instance with specific name and value from String which use
	 * defined decimal separator
	 * 
	 * @param name
	 *            used in expression with unknown value
	 * @param value
	 * @param decimalSeparator
	 *            used in String value
	 */
	public Num(String name, String value, char decimalSeparator) {
		this(value, decimalSeparator);
		this.name = name;
	}

	public Num set(Object value) {
		return setValue(value, null, null);
	}

	public Num set(Object value, Class<? extends NumConverter> converter) {
		return setValue(value, null, converter);
	}

	public Num set(String value, char decimalSeparator) {
		return setValue(value, decimalSeparator, null);
	}

	private Num setValue(Object value, Character decimalSeparator, Class<? extends NumConverter> converterClass) {
		try {
			originalValue = value;

			if (value instanceof Short) {
				in = new BigDecimal((Short) originalValue);
			} else if (value instanceof Integer) {
				in = new BigDecimal((Integer) originalValue);
			} else if (value instanceof Long) { 
				in = new BigDecimal((Long) originalValue);
			} else if (value instanceof Float) { 
				in = new BigDecimal(((Float) originalValue).toString());
			} else if (value instanceof Double) { 
				in = new BigDecimal(((Double) originalValue).toString());
			} else if (value instanceof BigInteger) { 
				in = new BigDecimal((BigInteger) originalValue);
			} else if (value instanceof BigDecimal) {
				in = (BigDecimal) originalValue;
			} else if (value instanceof String) {
				if (decimalSeparator == null)
					decimalSeparator = getProperties().getInputDecimalSeparator();
				else
					getProperties().setInputDecimalSeparator(decimalSeparator);

				String strValue = (String) value;
				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setDecimalSeparator(Properties.DEFAULT_DECIMAL_SEPARATOR);
				DecimalFormat df = new DecimalFormat("#0" + Properties.DEFAULT_DECIMAL_SEPARATOR + "0#", dfs);
                df.setParseBigDecimal(true);

                String csr = (decimalSeparator == '.') ? "([^-+\\d\\" + decimalSeparator + "]+)" : "([^-+\\d" + decimalSeparator + "]+)";

                Pattern p = Pattern.compile(csr);
                Matcher m = p.matcher(strValue);
                boolean isComplexStr = m.find();
                if (isComplexStr) {
                    strValue = parseComplexString(strValue, decimalSeparator);
                    
                    if (decimalSeparator != '.')
                        strValue = strValue.replace(decimalSeparator, Properties.DEFAULT_DECIMAL_SEPARATOR);
                    
                    in = (BigDecimal) df.parse(strValue);
                }
                else {
                    try {
                        in = (BigDecimal) df.parse(strValue);
                    }
                    catch (ParseException pe) {
                        // try with parsing complex number
                        strValue = parseComplexString(strValue, decimalSeparator);
                        in = (BigDecimal) df.parse(strValue);
                    }
                }
				
				// set auto scale
		        String tmp = in.toString();
		        int scale = tmp.indexOf(Properties.DEFAULT_DECIMAL_SEPARATOR);
		        if (scale != -1) {
		            setScale(tmp.length() - scale - 1);

		            // default: disable strip trailing zeros if string ends with zero
		            if (strValue.endsWith("0"))
		                setStripTrailingZeros(false);
		        } 
			} else if (value instanceof AbstractCalculator) {
				AbstractCalculator ac = (AbstractCalculator) value;
				originalValue = ac;

				Num n = ac.calculate();
				in = n.toBigDecimal();
			} else if (value instanceof Num) {
				Num tmp = (Num) value;
				setProperties(tmp.getProperties());
				in = tmp.toBigDecimal();
			} else if (value == null) {
				originalValue = 0;
				in = new BigDecimal((Integer) originalValue);
			} else {
				NumConverter nc = null;

				if (converterClass != null) {
					nc = (NumConverter) converterClass.newInstance();

					if (nc != null && converterClass.isAnnotationPresent(SingletonExtension.class))
						CacheExtension.setNumConverter(value.getClass(), nc);
				}

				if (nc == null) {
					nc = CacheExtension.getNumConverter(value.getClass());
				}

				if (nc != null)
					in = nc.toNum(value);
				else
					throw new CalculatorException("Unsupported type '" + value.getClass() + "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String");
			}
		} catch (ParseException pe) {
			throw new CalculatorException("Parse exception with '" + value + "'", pe);
		} catch (Exception e) {
			throw new CalculatorException(e);
		}

		out = in;
		
		return this;
	}

	public Num setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name of this variable. Used for expression with unknown variables e.g.
	 * 5 + X
	 * 
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	public Properties getProperties() {
		if (properties == null)
			properties = new Properties();
		return properties;
	}

	public Num setProperties(Properties properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Sets the scale for division operations.
	 * 
	 * @param scale
	 *            scale for division operations
	 * @return
	 */
	public Num setScale(int scale) {
		getProperties().setScale(scale);
		return this;
	}

	/**
	 * Sets the scale and rounding mode for division operations.
	 * 
	 * @param scale
	 *            scale for division operations
	 * @param roundingMode
	 *            rounding mode for division operations
	 * @return
	 */
	public Num setScale(int scale, Rounding roundingMode) {
		setScale(scale);
		setRoundingMode(roundingMode);
		return this;
	}

	/**
	 * Sets the scale for division operations. The default is 64
	 * 
	 * @return
	 */
	public Integer getScale() {
		return getProperties().getScale();
	}

	/**
	 * Sets the rounding mode for decimal divisions.
	 * 
	 * @param roundingMode
	 *            rounding mode for decimal divisions
	 * @return
	 */
	public Num setRoundingMode(Rounding roundingMode) {
		getProperties().setRoundingMode(roundingMode);
		return this;
	}

	/**
	 * Gets the rounding mode for division operations The default is
	 * {@code RoundingMode.HALF_UP}
	 * 
	 * @return
	 */
	public Rounding getRoundingMode() {
		return getProperties().getRoundingMode();
	}

	public Num setStripTrailingZeros(boolean stripTrailingZeros) {
		getProperties().setStripTrailingZeros(stripTrailingZeros);
		return this;
	}

	public boolean hasStripTrailingZeros() {
    	return getProperties().hasStripTrailingZeros();
    }

    /**
	 * Set output format for number.
	 * 
	 * @param format
	 *            - DecimalFormat e.g. #,###,###,##0.00
	 * @return
	 */
	public Num setOutputFormat(String format) {
		getProperties().setOutputFormat(format);
		return this;
	}

	/**
	 * Return number output format
	 * @return
	 */
	public String getOutputFormat() {
	    return getProperties().getOutputFormat();
	}
	
	/**
	 * Get <tt>BigDecimal</tt> without trailing zeros if {@link hasStripTrailingZeros()} is TRUE
     * And use scale and rounding mode defined by properties 
	 * 
	 * @return BigDecimal
	 */
	public BigDecimal toBigDecimal() {
	    return toBigDecimal(getProperties().getScale(), getProperties().getRoundingMode(), getProperties().hasStripTrailingZeros());
	}

	/**
	 * Get BigDecimal in defined scale, and use rounding mode and strip trailing zeros from defined properties 
	 * 
	 * @return BigDecimal
	 */
	public BigDecimal toBigDecimal(int scale) {
	    return toBigDecimal(scale, getProperties().getRoundingMode(), getProperties().hasStripTrailingZeros());
	}
	
	/**
     * Return BigDecimal in defined scale with specified rounding mode. Use strip trailing zeros from properties
     * 
     * @param scale
     * @param roundingMode
     * @return BigDecimal
     */
    public BigDecimal toBigDecimal(int scale, Rounding roundingMode) {
        return toBigDecimal(scale, roundingMode, getProperties().hasStripTrailingZeros());
    }

	/**
	 * Return BigDecimal with given parameters.
	 * 
	 * @param scale
	 * @param rounding
	 * @param stripTrailingZeros
	 * @return BigDecimal
	 */
	public BigDecimal toBigDecimal(Integer scale, Rounding rounding, boolean stripTrailingZeros) {
	    out = this.in;
	    
	    if (scale != null && rounding != null)
	        out = out.setScale(scale, rounding.getBigDecimalRound());
	    else if (scale != null && rounding == null)
	        out = out.setScale(scale);
	    
	    if (stripTrailingZeros)
	        out = out.stripTrailingZeros();
	    
	    return out;
	}

	/**
	 * Same as BigDecimal.reminder(BigDecimal.ONE)
	 * 
	 * @return
	 */
	public BigDecimal remainder() {
	    BigDecimal out = toBigDecimal();
		BigDecimal fraction = out.remainder(BigDecimal.ONE);
		return fraction;
	}

	/**
	 * Return count of number in reminder.
	 * @return
	 */
	public int remainderSize() {
	    BigDecimal fraction = remainder();
		String tmp = fraction.toString();
		int n = tmp.indexOf(Properties.DEFAULT_DECIMAL_SEPARATOR);
		if (n != -1) {
			return tmp.length() - n - 1;
		} else
			return 0;
	}

	/**
	 * Return TRUE if reminder exist.
	 * @return
	 */
	public boolean hasRemainder() {
	    String out = toString();
		if (out.contains("."))
			return true;
		else
			return false;
	}

	public boolean isZero() {
	    BigDecimal out = toBigDecimal();
	    if (out.doubleValue() == 0)
			return true;
		else
			return false;
	}

	public boolean isNegative() {
	    BigDecimal out = toBigDecimal();
		if (out.doubleValue() < 0)
			return true;
		else
			return false;
	}

	/**
	 * Convert <tt>Num</tt> to defined custom object
	 * 
	 * @param customObject
	 * @return
	 */
	public <T> T toObject(Class<T> toClass) {
		return (T) toObject(toClass, null, null);
	}

	/**
	 * Convert <tt>Num</tt> to defined custom object
	 * 
	 * @param customObject
	 * @return
	 */
	public <T> T toObject(Class<T> toClass, Class<? extends NumConverter> converterClass) {
		return (T) toObject(toClass, null, converterClass);
	}

	/**
	 * Convert <tt>Num</tt> to defined custom object
	 * 
	 * @param customObject
	 * @return
	 */
	public <T> T toObject(Class<T> toClass, NumConverter numConverter) {
		return (T) toObject(toClass, numConverter, null);
	}

	private Object toObject(Class toClass, NumConverter converter, Class<? extends NumConverter> converterClass) {
		try {
			NumConverter nc = null;

			if (converter != null) {
				nc = converter;
				if (converterClass.isAnnotationPresent(SingletonExtension.class))
					CacheExtension.setNumConverter(toClass, nc);
			} else if (converterClass != null) {
				nc = (NumConverter) converterClass.newInstance();
				if (nc != null && converterClass.isAnnotationPresent(SingletonExtension.class))
					CacheExtension.setNumConverter(toClass, nc);
			}

			if (nc == null) {
				nc = CacheExtension.getNumConverter(toClass);
			}

			if (nc != null) {
				return nc.fromNum(this);
			} else {
				throw new CalculatorException("Unsupported type '" + toClass + "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String");
			}
		} catch (Exception e) {
			throw new CalculatorException("Unsupported type '" + toClass + "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String", e);
		}
	}


	@Override
	/**
	 * Get String representation of Number defined by properties
	 */
	public String toString() {
	    return toString(getProperties().getGroupingSeparator(), getProperties().getOutputDecimalSeparator(), getProperties().getOutputFormat());
	}

	/**
	 * Use properties ({@link getProperties()}) grouping separator and decimal separator and specified format by rules of <tt>DecimalFormat</tt>
	 * DecimalFormat e.g. #,###,###,##0.00
	 * 
	 * @param format
	 * @return
	 */
	public String toString(String format) {
		return toString(getProperties().getGroupingSeparator(), getProperties().getOutputDecimalSeparator(), format);
	}

	public String toString(char decimalSeparator) {
		return toString(null, decimalSeparator, null);
	}

	public String toString(Character groupingSeparator, char decimalSeparator) {
		return toString(groupingSeparator, decimalSeparator, getProperties().getOutputFormat());
	}

	public String toString(Character groupingSeparator, char decimalSeparator, String format) {
	    BigDecimal out = toBigDecimal();
	    
	    DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator(decimalSeparator);
		if (groupingSeparator != null)
			custom.setGroupingSeparator(groupingSeparator);

		DecimalFormat decFormat = null;
		if (format != null)
		    decFormat = new DecimalFormat(format);
		else
		    decFormat = new DecimalFormat();
		    
		decFormat.setDecimalFormatSymbols(custom);
		if (groupingSeparator == null)
			decFormat.setGroupingUsed(false);

		Integer scale = getScale();
		if (scale != null && !hasStripTrailingZeros())
		    decFormat.setMinimumFractionDigits(scale);

		if (scale == null) {
		    scale = Properties.DEFAULT_SCALE;

		    if (!hasStripTrailingZeros()) {
		        int p = remainderSize();
		        decFormat.setMinimumFractionDigits(p);
		    }
		}
		decFormat.setMaximumFractionDigits(scale);

		
		return decFormat.format(out);
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	@Override
	public Num clone() {
		Num copy = new Num();

		copy.name = this.name;
		
		copy.getProperties().load(this.getProperties());
		copy.originalValue = this.originalValue;
		copy.in = new BigDecimal(this.in.toString());
		copy.out = new BigDecimal(this.out.toString());
		
		return copy;
	}

	/**
	 * Returns the hash code for this <tt>Decimal</tt>. Note that two
	 * <tt>Decimal</tt> objects that are numerically equal but differ in scale
	 * (like 2.0 and 2.00) will generally have the same hash code.
	 * 
	 * @return hash code for this <tt>Decimal</tt>.
	 * @see #equals(Object)
	 */
	@Override
	public int hashCode() {
		return toBigDecimal().hashCode();
	}

	/**
	 * Convert <tt>value</tt> to {@link Num} then compare values regardless scale
	 * 
	 * <pre>
     * e.g. 1254.5800 isEqual(1254.58)  = true 
     *      1254.58   isEqual(1254.580) = true 
     *      1254.5848 isEqual(1254.58)  = false
     *     
     * </pre>
	 * @param value
	 * @return
	 */
	public boolean isEqual(Object value) {
	    if (value == null)
	        return false;
	    else if (this == value)
	        return true;
	    else {
	        Num tmp = null;
	        if (value instanceof Num)
	            tmp = (Num)value;
	        else
	            tmp = new Num(value);
	        
	        int i = tmp.toBigDecimal().compareTo(this.toBigDecimal());
            if (i == 0)
                return true;
            else
                return false;
	    }
	}

	/**
     * Compare if two number are equal regardless scale
     * 
     * @param value
     * @return
     * @see #compareTo(Num)
     */
    public boolean isEqual(String value, char decimalSeparator) {
    	return isEqual(new Num(value, decimalSeparator));
    }

    /**
	 * Convert <tt>value</tt> to {@link Num}, and scale both value before comparing them.
	 * 
	 * @param value
	 * @return
	 */
	public boolean isEqual(Object value, int scale) {
		return isEqual(value, scale, null);
	}

	/**
	 * Scale both value to scale of number which have minimum scale.
	 * @param value
	 * @param autoscale
	 * @return
	 */
	public boolean isEqual(Object value, boolean autoscale) {
		Num numA = this;
		Num numB = null;
		if (value instanceof Num)
			numB = (Num) value;
		else
			numB = new Num(value);

		int minScale = numA.remainderSize();
		int bScale = numB.remainderSize();
		if (bScale < minScale)
			minScale = bScale;
		
		return isEqual(value, minScale);
	}

	/**
	 * Convert <tt>value</tt> to {@link Num}, and scale both value before
	 * comparing them.
	 * 
	 * @param value
	 * @return
	 */
	public boolean isEqual(Object value, int scale, Rounding rounding) {
		Num numA = this;
		Num numB = null;
		if (value instanceof Num)
			numB = (Num) value;
		else
			numB = new Num(value);
		
		BigDecimal a = numA.toBigDecimal();
		BigDecimal b = numB.toBigDecimal();
		
		if (rounding != null) {
			a = a.setScale(scale, rounding.getBigDecimalRound());
			b = b.setScale(scale, rounding.getBigDecimalRound());
		} else {
			a = a.setScale(scale, getProperties().getRoundingMode().getBigDecimalRound());
			b = b.setScale(scale, getProperties().getRoundingMode().getBigDecimalRound());
		}
		
		return a.equals(b);
	}

	public boolean isEqual(Object value, Class<? extends NumConverter> converter) {
		if (!(value instanceof Num)) {
			Num tmp = new Num(value, converter);
			return isEqual(tmp);
		} else {
			return isEqual((Num) value);
		}
	}

	/**
	 * Compares this <tt>Decimal</tt> (internally BigDecimal) with the specified
	 * <tt>Object</tt> for equality. Unlike {@link compareTo(Num) compareTo},
	 * this method considers two <tt>Decimal</tt> objects equal only if they are
	 * equal in value and scale (thus 2.0 is not equal to 2.00 when compared by
	 * this method).
	 * 
	 * @param object
	 *            <tt>Object</tt> to which this <tt>Decimal</tt> is to be
	 *            compared.
	 * @return <tt>true</tt> if and only if the specified <tt>Object</tt> is a
	 *         <tt>Decimal</tt> whose value and scale are equal to this
	 *         <tt>Decimal</tt>'s.
	 * @see #compareTo(java.math.Num)
	 * @see #hashCode
	 * @see #sameAs
	 */
	@Override
	public boolean equals(Object object) {
	    if (object == null)
	        return false;
	    else if (this == object)
			return true;
	    else if (object instanceof Num)
			return toBigDecimal().equals(((Num) object).toBigDecimal());

		return false;
	}

	public boolean equals(BigDecimal obj) {
		if (obj == null)
			return false;
		else if (this.toBigDecimal() == obj)
			return true;
		else
			return toBigDecimal().equals(obj);
	}
	
	/**
	 * Compares this <tt>Num</tt> with the specified <tt>Num</tt>. Two
	 * <tt>Decimal</tt> objects that are equal in value but have a different
	 * scale (like 2.0 and 2.00) are considered equal by this method. This
	 * method is provided in preference to individual methods for each of the
	 * six boolean comparison operators (&lt;, ==, &gt;, &gt;=, !=, &lt;=). The
	 * suggested idiom for performing these comparisons is:
	 * <tt>(x.compareTo(y)</tt> &lt;<i>op</i>&gt; <tt>0)</tt>, where
	 * &lt;<i>op</i>&gt; is one of the six comparison operators.
	 * 
	 * @param val
	 *            <tt>Decimal</tt> to which this <tt>Decimal</tt> is to be
	 *            compared.
	 * @return -1, 0, or 1 as this <tt>Num</tt> is numerically less than, equal
	 *         to, or greater than <tt>Num</tt>.
	 * @see #even
	 */
	public int compareTo(Num value) {
		return toBigDecimal().compareTo(value.toBigDecimal());
	}

	public byte byteValue() {
		return toBigDecimal().byteValue();
	}

	public byte byteValueExact() {
		return toBigDecimal().byteValueExact();
	}

	public double doubleValue() {
		return toBigDecimal().doubleValue();
	}

	public float floatValue() {
		return toBigDecimal().floatValue();
	}

	public int intValue() {
		return toBigDecimal().intValue();
	}

	public int intValueExact() {
		return toBigDecimal().intValueExact();
	}

	public long longValue() {
		return toBigDecimal().longValue();
	}

	public long longValueExact() {
		return toBigDecimal().longValueExact();
	}

	public short shortValue() {
		return toBigDecimal().shortValue();
	}

	public short shortValueExact() {
		return toBigDecimal().shortValueExact();
	}

	public int signum() {
		return toBigDecimal().signum();
	}

	public String toEngineeringString() {
		return toBigDecimal().toEngineeringString();
	}

	/**
	 * Return new instance of Num with absolute value
	 * 
	 * @return
	 */
	public Num abs() {
		Num abs = new Num(toBigDecimal().abs());
		return abs;
	}

	/**
	 * Return new instance of Num with ceil value. e.g. for 3.5 -> 4.0
	 * 
	 * @return
	 */
	public Num ceil() {
		Num ceil = new Num();
		BigDecimal c = this.toBigDecimal();
		ceil.setValue(c.setScale(0, BigDecimal.ROUND_CEILING), null, null);
		return ceil;
	}

	/**
	 * Return new instance of Num with floor value e.g. for 3.5 -> 3.0
	 * 
	 * @return
	 */
	public Num floor() {
		Num floor = new Num();
		BigDecimal c = this.toBigDecimal();
		floor.setValue(c.setScale(0, BigDecimal.ROUND_FLOOR), null, null);
		return floor;
	}

	    public static Num toNum(Object object) {
		if (object instanceof Num)
			return ((Num) object).clone();
		else {
			Num n = new Num(object);
			return n;
		}
	}
	    
	    /**
	     * Remove from string number representation all character except numbers and
	     * decimal point. <br>
	     * And replace given decimalSeparator with '.'
	     * 
	     * <pre>
	     * ("44,551.06", '.')        => 44551.06
	     * ("1 255 844,551.06", '.') => 1255844551.06
	     * ("44,551..06", '.')       => 44551.06
	     * </pre>
	     * 
	     * @param decimalSeparator
	     * @param value
	     */
	    private static String cleanNumber(String value, char decimalSeparator) {
	        String regex = "[^0-9-" + decimalSeparator + "]";
	        if (decimalSeparator == '.')
	            regex = regex.replace(".", "\\.");
	        String strip = value.replaceAll(regex, "");
	    
	        strip = strip.replace(decimalSeparator + "", Properties.DEFAULT_DECIMAL_SEPARATOR + "");
	        return strip;
	    }

	    public static String NUM_REGEX(char ds) {
        String r = "([-+\\d]\\s?[\\d ,'" + ds + "]+)+";
	        if (ds == '.')
	            r = r.replace(".", "\\.");
	        
	        return r;
	    }
	    
	    public static String parseComplexString(String value, Character decimalSeparator) throws ParseException {
	        String strValue = value;
	        Pattern pat = Pattern.compile(NUM_REGEX(Properties.DEFAULT_DECIMAL_SEPARATOR));

            // extract number from string
            Matcher m = pat.matcher(strValue);
            int find = 0;
            while(m.find()) {
                if(++find > 1)
                    throw new ParseException("Detect more than one number in String: " + value, 0);
                
                if (m.groupCount() == 1)
                    strValue = m.group(1);
                else 
                    throw new ParseException("Detect more than one number in String: " + value, 0);
            }
            
            if (find == 0)
                throw new ParseException("Can't parse " + strValue, 0);
            
            
            strValue = cleanNumber(strValue, decimalSeparator);
            
            if (decimalSeparator != null && decimalSeparator != Properties.DEFAULT_DECIMAL_SEPARATOR)
                strValue = strValue.replace(decimalSeparator + "", Properties.DEFAULT_DECIMAL_SEPARATOR + ""); // use default decimal separator
            
            return strValue;
	    }
}
