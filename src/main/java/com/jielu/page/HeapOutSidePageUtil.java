package com.jielu.page;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HeapOutSidePageUtil<R> {


    /**
     * allocate direct physical  paging
     * @param dbList
     * @param index
     * @param pageSize
     * @return
     */
    public PageResult<R> getPageResult(List<?> dbList, int index, int pageSize){

        ByteBuffer bb = ByteBuffer.allocateDirect(1024*1024*512);


        return  new PageResult<>();


    }

    public static void main(String[] args) {
        String regEx_html = "<[^>]+>";
        Pattern pattern = Pattern.compile(regEx_html);
        Matcher m = pattern.matcher("<a></a>");
        System.out.println(m.matches());

    }
}
