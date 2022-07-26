package com.jielu.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MultipleThreadHandleFileUtil {

    static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            10,
            50,
            1000 * 60,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>());

    public static void copyFile(String source, String target) throws InterruptedException, IOException {

        Map<String, File> fileMap = getHasFileIsNotDirListOrIsLeafDir(source);
        Stream<String> fileStream = new ArrayList<>(fileMap.keySet()).stream();
        ExecutorService executorService = threadPoolExecutor;

        File targetFile = new File(target);
        targetFile.mkdirs();
        String packageFileName = targetFile.getAbsolutePath();
        int i=0;
        List<Callable<String>> callableList = fileStream.map(f -> (Callable<String>) () -> {
            File t = new File(packageFileName + File.separator + f.replace(source, ""));
            File fs = new File(f);
            if (fs.isDirectory()) {
                t.mkdir();
            } else {
                File fsd = new File(t.getParent());
                if (!fsd.isDirectory()) {
                    fsd.mkdirs();
                }
                FileReader fileReader = new FileReader(source + File.separator + f);
                FileWriter fileWriter = new FileWriter(t);
                IOUtils.copy(fileReader, fileWriter);
                IOUtils.close(fileReader);
                IOUtils.close(fileWriter);
            }
            return "callable type thread task：" + i;
        }).collect(Collectors.toList());
        // 执行给定的任务，返回持有他们的状态和结果的所有完成的期待列表。
        executorService.invokeAll(callableList).stream().map(futureTask -> {
            try {
                return futureTask.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        });
        executorService.shutdownNow();
    }

    private static Map<String, File> getHasFileIsNotDirListOrIsLeafDir(String source) throws IOException {
        File file = new File(source);
        file.setReadable(true);
        file.setExecutable(true);
        Stack<File> fileStack = new Stack<>();
        fileStack.push(file);
        Map<String, File> responseMap = new HashMap<>(2 << 2 << 2);
        while (!fileStack.isEmpty()) {
            File pop = fileStack.pop();
            List<File> fileList = null;
            File[] fs = pop.listFiles();
            if (fs != null && fs.length > 0) {
                fileList = Arrays.asList(fs);
                fileStack.addAll(fileList);
            }

            if (pop.isDirectory() && (fileList == null || fileList.size() == 0)) {
                responseMap.put(pop.getAbsolutePath().replace(file.getPath(), ""), pop);
            } else if (pop.isFile()) {
                responseMap.put(pop.getAbsolutePath().replace(file.getPath(), ""), pop);
            }
        }

        return responseMap;

    }


}
