package com.jielu.util;

import java.util.Collection;

public class CollectionUtils {

    private  CollectionUtils(){

    }

    public  static boolean isNotEmpty(Collection<?> collections){

        return  !collections.isEmpty()&&collections.size()>0;

    }
}
