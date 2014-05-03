package org.jdice.calc;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation properties for {@link NumConverter}, {@link Operator}, {@link Function} implementation which 
 * provide that only one instance of implementation can be instantiate. 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(value=ElementType.TYPE)
public @interface SingletonExtension {

}
