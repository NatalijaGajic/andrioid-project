package com.example.bookofbooks.Utility;

import android.util.Log;

public class TimestampConverter {

    public static String timestampToDate(String timestamp){
        String[] dateParts = timestamp.split(" ");
        StringBuilder sb = new StringBuilder();
        for(int i=1; i<dateParts.length;i++){

            if(i==4 && i==5 && i==dateParts.length-1){
                Log.d("",dateParts[i].toString());
                continue;
            }
            if(i==3) {
                String[] timeParts = dateParts[i].split(":");
                sb.append(timeParts[0]+":");
                sb.append(timeParts[1]);
            } else{
                sb.append(dateParts[i]);
            }
            sb.append(" ");

        }
        sb.delete(12,22);
        String year = sb.substring(12,17);
        sb.delete(12,29);
        String[] strings = sb.toString().split(" ");
        sb.delete(0,sb.capacity());
        sb.append(strings[0]+" ");
        sb.append(strings[1]+" ");
        sb.append(year+" ");
        sb.append(strings[2]);
        return sb.toString();
    }
}
