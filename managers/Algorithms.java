package managers;

import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;

import objects.AlarmModel;

public class Algorithms {
    
    public static boolean[] concatenateBooleanArrays(boolean[] A, boolean[] B) {
        int aLen = A.length;
        int bLen = B.length;

        @SuppressWarnings("unchecked")
        boolean[] C = (boolean[]) Array.newInstance(A.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);

        return C;
    }

    public static String getTimeText(int hours, int minutes) {
        return String.valueOf(hours) + ":" + ((minutes >= 10) ? String.valueOf(minutes) : "0" + String.valueOf(minutes));
    }

    public static Boolean[] castPrimitiveBoolArrayToObject(boolean[] array) {
        Boolean[] newArray = new Boolean[array.length];
        for (int i = 0; i < array.length; i++) newArray[i] = array[i];
        return newArray;
    }

    public static boolean[] castObjectBoolArrayToPrimitive(Boolean[] array) {
        boolean[] newArray = new boolean[array.length];
        for (int i = 0; i < array.length; i++) newArray[i] = array[i];
        return newArray;
    }

    public static int getActiveDaysCount(boolean[] days) {
        int count = 0;
        for (boolean day : days)
            if (day) count++;
        return count;
    }
    

}
