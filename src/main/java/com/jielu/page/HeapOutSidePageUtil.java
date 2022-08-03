package com.jielu.page;

import com.jielu.web.request.DirectoryTest;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Implements outside of heap pagination
 * @param <R>
 */
public class HeapOutSidePageUtil<R> {

   final ByteBuffer bb = ByteBuffer.allocateDirect(1024*1024*512);

    private List<R> sourceList;

    Map<Integer,Integer> byteMap=new HashMap<>();

    public HeapOutSidePageUtil(List<R> sourceList) {
        bb.flip();
        bb.clear();
        this.sourceList = sourceList;
        int index=0;
        for(byte[]b:splitByte(sourceList)) {
            bb.put(index, b);
            ++index;
        }
    }

    /**
     * allocate direct physical  paging
     * @param pageSize
     * @return
     */
    public List<R> getPageResult(int pageIndex, int pageSize) {
        List<R> rst=new ArrayList<>();
        int startIndex=(pageIndex-1)*pageSize;
        try {
            for (; startIndex < startIndex + pageSize; startIndex++) {
                System.out.println(byteMap.get(startIndex));
                ByteBuffer byteBuffer = bb.slice(startIndex, byteMap.get(startIndex));
                rst.add((R) bytesToObject(extractBytes(byteBuffer)));

            }
        }
        catch (Exception e){
            throw  new RuntimeException(e);
        }
        return  rst;


    }

    public static byte[] extractBytes(ByteBuffer buffer) {
        ByteBuffer duplicate = buffer.duplicate();
        byte[] bytes = new byte[duplicate.remaining()];
        duplicate.get(bytes);
        return bytes;
    }

        List<byte[]> splitByte(List<R>sourceList){
        List<byte[]> list=new ArrayList<>(sourceList.size());
        int index=0;
        for(R source:sourceList){
            System.out.println(source.toString());
            byte[]s=getBytesOf(source);
            list.add(s);
            byteMap.put(index,s.length);
        }
        return  list;
    }

    byte[] getBytesOf(R sourceObj){
            byte [] barray = null;
            try
            {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(baos);
                objectOutputStream.writeObject(sourceObj);
                barray = baos.toByteArray();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            return barray;
        }

    private static Object bytesToObject(byte[] bytes) throws IOException {
        ObjectInputStream sIn=null;
        Object returnObject=null;
        ByteArrayInputStream in=null;
        try {
                 in = new ByteArrayInputStream(bytes);
                 sIn = new   ObjectInputStream(in);
                  returnObject=sIn.readObject();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
        finally {
            in.close();

        }
        return returnObject;
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        List<DirectoryTest> feignGetRequests=new ArrayList<>();
        for(int i=1;i<=500;i++){
            DirectoryTest feignGetRequest=new DirectoryTest();
            feignGetRequest.setA("second-name-"+i);
            feignGetRequest.setB("first-name-"+i);
            feignGetRequests.add(feignGetRequest);
        }
        HeapOutSidePageUtil heapOutSidePageUtil=new HeapOutSidePageUtil(feignGetRequests);
        heapOutSidePageUtil.getBytesOf(feignGetRequests.get(0));
        DirectoryTest f= (DirectoryTest) heapOutSidePageUtil.bytesToObject(   heapOutSidePageUtil.getBytesOf(feignGetRequests.get(0)));
        System.out.println(heapOutSidePageUtil.getPageResult(1,10));
        System.out.println(f.getA());
        String regEx_html = "<[^>]+>";
        Pattern pattern = Pattern.compile(regEx_html);
        Matcher m = pattern.matcher("<a></a>");
        System.out.println(m.matches());



    }
}
