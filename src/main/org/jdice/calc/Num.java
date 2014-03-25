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

	private BigDecimal in;
	private BigDecimal out;

	//
	// Constructors
	//
	/**
	 * Create Num instance with zero default value
	 */
	public Num() {
		if (in == null)
			setValue(0);

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
		setValue(value);
	}

	/**
	 * Create Num instance with specific name and value of given Object
	 * 
	 * @param name
	 *            used in equation with unknown value
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
	 *            used in equation with unknown value
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
	 * Create Num instance with value from String
	 * 
	 * @param value
	 */
	public Num(String value) {
		this();
		setValue(value);
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
		setValue(value, decimalSeparator);
	}

	/**
	 * Create Num instance with specific name and value from String which use
	 * defined decimal separator
	 * 
	 * @param name
	 *            used in equation with unknown value
	 * @param value
	 * @param decimalSeparator
	 *            used in String value
	 */
	public Num(String name, String value, char decimalSeparator) {
		this(value, decimalSeparator);
		this.name = name;
	}

	//
	// Setters
	//
	public Num set(Object value) {
		return setValue(value, null);
	}

	public Num set(Object value, Class<? extends NumConverter> converter) {
		return setValue(value, null, converter);
	}

	public Num set(String value) {
		return setValue(value, null);
	}

	public Num set(String value, char decimalSeparator) {
		return setValue(value, decimalSeparator);
	}

	private Num setValue(Object value) {
		return setValue(value, null);
	}

	private Num setValue(Object value, Character decimalSeparator) {
		return setValue(value, decimalSeparator, null);
	}

	private Num setValue(Object value, Character decimalSeparator,
			Class<? extends NumConverter> converterClass) {
		try {
			originalValue = value;

			if (value instanceof Short) { // 6.1
				in = new BigDecimal((Short) originalValue);
			} else if (value instanceof Integer) { // 6.2
				in = new BigDecimal((Integer) originalValue);
			} else if (value instanceof Long) { // 6.3
				in = new BigDecimal((Long) originalValue);
			} else if (value instanceof Float) { // 6.4
				in = new BigDecimal(((Float) originalValue).toString());
			} else if (value instanceof Double) { // 6.5
				in = new BigDecimal(((Double) originalValue).toString());
			} else if (value instanceof BigInteger) { // 6.6
				in = new BigDecimal((BigInteger) originalValue);
			} else if (value instanceof BigDecimal) { // 6.7
				in = (BigDecimal) originalValue;
			} else if (value instanceof String) {
				if (decimalSeparator == null)
					decimalSeparator = getProperties().getInputDecimalSeparator();
				else
					getProperties().setInputDecimalSeparator(decimalSeparator);

				String strValue = (String) value;

				strValue = Num.stripNumber(strValue, decimalSeparator);
				strValue = strValue.replace(decimalSeparator + "",
						Properties.DEFAULT_DECIMAL_SEPARATOR + ""); // use default
																	// decimal
																	// separator

				DecimalFormatSymbols dfs = new DecimalFormatSymbols();
				dfs.setDecimalSeparator(Properties.DEFAULT_DECIMAL_SEPARATOR);
				DecimalFormat df = new DecimalFormat("#0"
						+ Properties.DEFAULT_DECIMAL_SEPARATOR + "0#", dfs);
				df.setParseBigDecimal(true);

				in = (BigDecimal) df.parse(strValue);
			} else if (value instanceof AbstractCalculator) {
				AbstractCalculator ac = (AbstractCalculator) value;
				originalValue = ac;

				Num n = ac.calc();
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

					if (nc != null && nc.cache())
						Cache.registerNumConverter(value.getClass(), nc);
				}

				if (nc == null) {
					nc = Cache.getNumConverter(value.getClass());
				}

				if (nc != null)
					in = nc.toNum(value);
				else
					throw new CalculatorException(
							"Unsupported type '"
									+ value.getClass()
									+ "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String");
			}
		} catch (ParseException pe) {
			throw new CalculatorException("Parse exception with '" + value
					+ "'", pe);
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
	 * Get name of this variable. Used for equation with unknown variables e.g.
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

	public boolean hasStripTrailingZeros() {
		return getProperties().hasStripTrailingZeros();
	}

	public Num setStripTrailingZeros(boolean stripTrailingZeros) {
		getProperties().setStripTrailingZeros(stripTrailingZeros);
		return this;
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

	//
	// GET's
	//
	/**
	 * Return BigDecimal, same as {@link get()}
	 * 
	 * @return
	 * @see #get()
	 */
	public BigDecimal toBigDecimal() {
		return get();
	}

	/**
	 * Get <tt>BigDecimal</tt> without trailing zeros if {@link
	 * hasStripTrailingZeros()} is TRUE
	 * 
	 * @return
	 * @see #toBigDecimal()
	 */
	public BigDecimal get() {
		out = this.in;

		if (getProperties().getScale() != null)
			out = out.setScale(getProperties().getScale(), getProperties()
					.getRoundingMode().getBigDecimalRound());

		if (getProperties().hasStripTrailingZeros() && hasFraction())
			out = out.stripTrailingZeros();

		return out;
	}

	/**
	 * Get BigDecimal in scale
	 * 
	 * @param scale
	 * @return
	 */
	public BigDecimal get(int scale) {
		out = in.setScale(scale, this.getRoundingMode().getBigDecimalRound());

		if (getProperties().hasStripTrailingZeros() && hasFraction())
			out = out.stripTrailingZeros();

		return out;
	}

	/**
	 * Return BigDecimal in defined scale with specified rounding mode
	 * 
	 * @param scale
	 * @param roundingMode
	 * @return
	 */
	public BigDecimal get(int scale, Rounding roundingMode) {
		out = in.setScale(scale, this.getRoundingMode().getBigDecimalRound());

		if (getProperties().hasStripTrailingZeros() && hasFraction())
			out = out.stripTrailingZeros();

		return out;
	}

	public BigDecimal getFraction() {
		BigDecimal fraction = in.remainder(BigDecimal.ONE);
		return fraction;
	}

	public int getFractionSize() {
		String tmp = toString();
		if (tmp.contains(".") && !tmp.endsWith(".")) {
			return tmp.substring(tmp.indexOf(".")).length() - 1;
		} else
			return 0;
	}

	public boolean hasFraction() {
		if (out.toString().contains("."))
			return true;
		else
			return false;
	}

	public boolean isZero() {
		if (out.doubleValue() == 0)
			return true;
		else
			return false;
	}

	public boolean isNegative() {
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
	public <T> T toObject(Class<T> toClass,
			Class<? extends NumConverter> converterClass) {
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
				if (nc.cache())
					Cache.registerNumConverter(toClass, nc);
			} else if (converterClass != null) {
				nc = (NumConverter) converterClass.newInstance();
				if (nc != null && nc.cache())
					Cache.registerNumConverter(toClass, nc);
			}

			if (nc == null) {
				nc = Cache.getNumConverter(toClass);
			}

			if (nc != null) {
				return nc.fromNum(this);
			} else {
				throw new CalculatorException(
						"Unsupported type '"
								+ toClass
								+ "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String");
			}
		} catch (Exception e) {
			throw new CalculatorException(
					"Unsupported type '"
							+ toClass
							+ "'! Supported types are: short, int, long, float, double, Short, Integer, Long, Float, Double, BigInteger, BigDecimal, String",
					e);
		}
	}

	public String toStringWithDetail() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		if (getScale() != null)
			sb.append("\t scale:" + getScale());
		else
			sb.append("\t scale: none");
		sb.append("\t" + getRoundingMode());
		sb.append("\t strip trailing zeros:" + hasStripTrailingZeros());

		sb.append("] : " + get());

		return sb.toString();
	}

	/**
	 * Get formated representation of number defined by {@link
	 * setOutputFormat(String)}
	 * 
	 * @param format
	 *            - DecimalFormat e.g. #,###,###,##0.00
	 * @return
	 */
	public String getFormated() {
		return toString(getProperties().getGroupingSeparator(), getProperties()
				.getOutputDecimalSeparator(), getProperties().getOutputFormat());
	}

	@Override
	public String toString() {
		return getFormated();
	}

	/**
	 * DecimalFormat e.g. #,###,###,##0.00
	 * 
	 * @param format
	 * @return
	 */
	public String toString(String format) {
		return toString(getProperties().getGroupingSeparator(), getProperties()
				.getOutputDecimalSeparator(), format);
	}

	public String toString(char decimalSeparator) {
		return toString(getProperties().getGroupingSeparator(),
				decimalSeparator, null);
	}

	public String toString(Character groupingSeparator, char decimalSeparator) {
		return toString(groupingSeparator, decimalSeparator, getProperties()
				.getOutputFormat());
	}

	public String toString(Character groupingSeparator, char decimalSeparator, String format) {
		DecimalFormatSymbols custom = new DecimalFormatSymbols();
		custom.setDecimalSeparator(decimalSeparator);
		if (groupingSeparator != null)
			custom.setGroupingSeparator(groupingSeparator);

		DecimalFormat decFormat = new DecimalFormat();
		decFormat.setDecimalFormatSymbols(custom);
		if (groupingSeparator == null)
			decFormat.setGroupingUsed(false);

		Integer scale = getScale();
		if (scale != null)
			decFormat.setMaximumFractionDigits(scale);
		else
			decFormat.setMaximumFractionDigits(Properties.DEFAULT_SCALE);

		return decFormat.format(toBigDecimal());
	}

	public Object getOriginalValue() {
		return originalValue;
	}

	@Override
	public Num clone() {
		Num copy = new Num();

		copy.name = this.name;
		copy.setProperties(this.getProperties());
		copy.originalValue = this.originalValue;
		copy.in = new BigDecimal(this.in.toString());
		copy.set(this.toBigDecimal());
		
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
	 * Compare if two number are equal regardless scale
	 * 
	 * @param value
	 * @return
	 * @see #compareTo(Num)
	 */
	public boolean sameAs(String value) {
		return sameAs(new Num(value));
	}

	/**
	 * Compare if two number are equal regardless scale
	 * 
	 * @param value
	 * @return
	 * @see #compareTo(Num)
	 */
	public boolean sameAs(String value, char decimalSeparator) {
		return sameAs(new Num(value, decimalSeparator));
	}

	/**
	 * Compare if two number are equal regardless scale
	 * 
	 * <pre>
	 * e.g. 1254.5800 sameAs(1254.58)  = true 
	 *      1254.58   sameAs(1254.580) = true 
	 *      1254.5848 sameAs(1254.58)  = false
	 *     
	 * </pre>
	 * @param value
	 * @return
	 * @see #compareTo(Num)
	 */
	public boolean sameAs(Num value) {
		if (value == null)
			return false;
		else if (this == value)
			return true;
		else {
			Num other = (Num) value;
			int i = other.toBigDecimal().compareTo(this.toBigDecimal());
			if (i == 0)
				return true;
			else
				return false;
		}
	}

	/**
	 * Convert <tt>value</tt> to {@link Num} then compare values regardles scale
	 * 
	 * @param value
	 * @return
	 */
	public boolean sameAs(Object value) {
		if (!(value instanceof Num)) {
			Num num = new Num(value);
			return sameAs(num);
		} else {
			return sameAs((Num) value);
		}
	}

	/**
	 * Convert <tt>value</tt> to {@link Num}, and scale both value before comparing them.
	 * 
	 * @param value
	 * @return
	 */
	public boolean sameAs(Object value, int scale) {
		return sameAs(value, scale, null);
	}

	public boolean sameAs(Object value, boolean autoscale) {
		Num numA = this;
		Num numB = null;
		if (value instanceof Num)
			numB = (Num) value;
		else
			numB = new Num(value);

		int minScale = numA.getFractionSize();
		int bScale = numB.getFractionSize();
		if (bScale < minScale)
			minScale = bScale;
		
		return sameAs(value, minScale);
	}

	/**
	 * Convert <tt>value</tt> to {@link Num}, and scale both value before
	 * comparing them.
	 * 
	 * @param value
	 * @return
	 */
	public boolean sameAs(Object value, int scale, Rounding rounding) {
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

	public boolean sameAs(Object value, Class<? extends NumConverter> converter) {
		if (!(value instanceof Num)) {
			Num tmp = new Num(value, converter);
			return sameAs(tmp);
		} else {
			return sameAs((Num) value);
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
		if (this == object)
			return true;

		if (object instanceof Num)
			return toBigDecimal().equals(((Num) object).toBigDecimal());

		return false;
	}

	public boolean equals(Num obj) {
		if (obj == null)
			return false;
		else if (this == obj)
			return true;
		else
			return toBigDecimal().equals(obj.toBigDecimal());
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
	public int compareTo(Num value2) {
		return toBigDecimal().compareTo(value2.toBigDecimal());
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
		ceil.setValue(c.setScale(0, BigDecimal.ROUND_CEILING));
		ceil.setProperties(getProperties());
		return ceil;
	}

	/**
	 * Return new instance of Num with floor value e.g. for 3.5 -> 3.0
	 * 
	 * @return
	 */
	public Num floor() {
		Num ceil = new Num();
		BigDecimal c = this.toBigDecimal();
		ceil.setValue(c.setScale(0, BigDecimal.ROUND_FLOOR));
		ceil.setProperties(getProperties());
		return ceil;
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
	public static String stripNumber(String value, char decimalSeparator) {
		String regex = "[^0-9" + decimalSeparator + "]";
		if (decimalSeparator == '.')
			regex = regex.replace(".", "\\.");
		String strip = value.replaceAll(regex, "");

		strip = strip.replace(decimalSeparator + "",
				Properties.DEFAULT_DECIMAL_SEPARATOR + "");
		return strip;
	}

	public static Num toNum(Object object) {
		if (object instanceof Num)
			return ((Num) object).clone();
		else {
			Num n = new Num(object);
			return n;
		}
	}

	public static Num toNum(Object object, Class<? extends NumConverter> converter) {
		if (object instanceof Num)
			return ((Num) object).clone();
		else {
			Num n = new Num(object, converter);
			return n;
		}
	}

	public static Num[] toNums(Object... object) {
		Num[] values = new Num[object.length];
		for (int i = 0; i < object.length; i++) {
			values[i] = Num.toNum(object[i]);
		}

		return values;
	}

}
