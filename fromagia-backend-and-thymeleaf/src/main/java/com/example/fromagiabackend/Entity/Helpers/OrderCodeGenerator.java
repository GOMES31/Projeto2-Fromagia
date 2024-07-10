package com.example.fromagiabackend.Entity.Helpers;

public class OrderCodeGenerator {

    public static String generateNextOrderCode(String currentCode) {
        char[] codeArray = currentCode.toCharArray();
        for (int i = codeArray.length - 1; i >= 0; i--) {
            if (codeArray[i] < 'Z') {
                codeArray[i]++;
                break;
            } else {
                codeArray[i] = 'A';
            }
        }
        return new String(codeArray);
    }
}
