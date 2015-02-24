package org.jdice.calc.internal;

import org.jdice.calc.Num;
import org.jdice.calc.NumConverter;

public class Objects {

    public static boolean equals(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }
    
    public static Num[] toNums(Object... object) {
        Num[] values = new Num[object.length];
        for (int i = 0; i < object.length; i++) {
            values[i] = Num.toNum(object[i]);
        }

        return values;
    }

    public static Num toNum(Class<? extends NumConverter> converter, Object object) {
    	if (object instanceof Num)
    		return ((Num) object).clone();
    	else {
    		Num n = new Num(object, converter);
    		return n;
    	}
    }

}
