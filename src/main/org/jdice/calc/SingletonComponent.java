package org.jdice.calc;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotated operator, function or number converter implementation will be act like singleton. 
 * 
 * @author Davor Sauer <davor.sauer@gmail.com>
 *
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface SingletonComponent {

}
