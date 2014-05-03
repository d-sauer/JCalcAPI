package org.jdice.calc.internal;

import org.jdice.calc.Num;

public class Utils {

    public static boolean equals(Object objA, Object objB) {
        if (objA == objB)
            return true;
        else if (objA == null && objB == null)
            return true;
        else if (objA != null && objB == null)
            return false;
        else if (objA == null && objB != null)
            return false;
        else if (objA != null && objB != null && objA.getClass().isAssignableFrom(objB.getClass()))
            return objA.equals(objB);
        
        return false;
    }
    
    public static Num[] toNums(Object... object) {
        Num[] values = new Num[object.length];
        for (int i = 0; i < object.length; i++) {
            values[i] = Num.toNum(object[i]);
        }

        return values;
    }
}
