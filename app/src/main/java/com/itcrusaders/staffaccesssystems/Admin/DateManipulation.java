package com.itcrusaders.staffaccesssystems.Admin;

public class DateManipulation {

    public static int DateCountNum(int month,int year){
        switch (month){
            case 9: case 4 : case 6: case 11:
                return 30;
            case 2 :
                if((year%4) == 0){
                    return 29;
                }
                return 28;
            case 1 : case 3 : case 5 : case 7 :
                case 8 : case 10 : case 12 :
                return 31;
        }
        return 1;
    }
}
