package com.jielu.page;

import java.util.Collection;

public class PageResult<R> {

    private  Long size=5L;

    private  Long total;

    private  Long index;

    private  Long pageSize;

    private  Long current=1L;

    private Collection<R> records;

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public Collection<R> getRecords() {
        return records;
    }

    public void setRecords(Collection<R> records) {
        this.records = records;
    }

    public Long getPageSize() {
        if(total==null){
            return 0L;
        }
        long down=total/size;
        long  mod=total%size;
        return  total==null?0:(mod==0?down:down+1);
    }


}
