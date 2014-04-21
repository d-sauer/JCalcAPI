package org.jdice.calc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation properties for {@link NumConverter}, {@link Operator}, {@link Function} implementation which 
 * provide that only one instance of implementation can be instantiate. 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SingletonComponent {

}
