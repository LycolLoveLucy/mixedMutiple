package com.xxl.job.admin.core.util;

import java.util.regex.Pattern;

public class NumericValidationUtil {

    static final Pattern pattern=Pattern.compile("^[1-9]d*|0$");

    public  static boolean  isNumeric(String str){
        return pattern.matcher(str).find();
    }

}
