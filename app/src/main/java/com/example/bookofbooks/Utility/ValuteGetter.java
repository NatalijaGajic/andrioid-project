package com.example.bookofbooks.Utility;

import com.example.bookofbooks.R;

public class ValuteGetter {

    public static int getIndexOfValute(String valute, String[] array){
        for(int i=0; i<array.length;i++){
            if(valute.equals(array[i])){
                return i;
            }
        }
        return 0;
    }
}
