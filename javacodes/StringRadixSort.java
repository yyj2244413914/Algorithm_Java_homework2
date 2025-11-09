import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * 字符串基数排序实现类
 * 使用ResizingQueue作为桶来实现等宽字符串的基数排序
 */
public class StringRadixSort {

    /**
     * 对等宽字符串数组进行基数排序（仅支持26个英文字母）
     * @param array 待排序的等宽字符串数组
     * @return 排序后的字符串数组
     */
    public static String[] sort(String[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        // 检查字符串是否等宽
        int length = array[0].length();
        for (String str : array) {
            if (str.length() != length) {
                throw new IllegalArgumentException("字符串必须等宽");
            }
            // 检查字符串是否只包含英文字母
            for (char c : str.toCharArray()) {
                if (!Character.isLetter(c)) {
                    throw new IllegalArgumentException("字符串只能包含英文字母");
                }
            }
        }
        
        // 创建26个队列作为桶（对应A-Z或a-z的字母）
        ResizingQueue<String>[] buckets = createBuckets();
        
        // 从右到左对每个字符位置进行排序
        for (int i = length - 1; i >= 0; i--) {
            // 将数据分配到对应的桶中
            for (String str : array) {
                // 将字符映射到0-25的范围（忽略大小写）
                char c = str.charAt(i);
                int bucketIndex = Character.toLowerCase(c) - 'a';
                buckets[bucketIndex].enqueue(str);
            }
            
            // 将桶中的数据按顺序放回到数组中
            int index = 0;
            for (ResizingQueue<String> bucket : buckets) {
                while (!bucket.isEmpty()) {
                    array[index++] = bucket.dequeue();
                }
            }
        }
        
        return array;
    }
    
    // 辅助方法：创建字符串排序的桶（26个桶，对应26个字母）
    private static ResizingQueue<String>[] createBuckets() {
        @SuppressWarnings("unchecked")
        ResizingQueue<String>[] buckets = new ResizingQueue[26];
        for (int i = 0; i < 26; i++) {
            buckets[i] = new ResizingQueue<>();
        }
        return buckets;
    }

    // 保存所有排序结果到单个文件
    private static boolean saveAllResultsToFile(List<String> allResults, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String resultLine : allResults) {
                writer.println(resultLine);
            }
            System.out.println("所有排序结果已保存到：" + fileName);
            return true;
        } catch (IOException e) {
            System.err.println("保存文件失败：" + e.getMessage());
            return false;
        }
    }
    
    /**
     * 主方法，用于测试字符串基数排序
     */
    public static void main(String[] args) {
        // 测试字符串排序
        String input = FileImt.getDataFilePath("radixSort2.txt");
        // 从文件中读取字符串数组
        System.out.println("正在从radixSort2.txt文件中读取数据：");
        
        // 用于存储所有行的排序结果
        List<String> allResults = new ArrayList<>();
        
        try (Scanner scanner = new Scanner(new File(input))) {
            int lineNumber = 0;
            // 逐行读取文件
            while (scanner.hasNextLine()) {
                lineNumber++;
                String line = scanner.nextLine().trim();
                
                // 跳过空行
                if (line.isEmpty()) {
                    System.out.println("第" + lineNumber + "行：(空行)");
                    continue;
                }
                
                // 假设每行包含多个字符串，以空格分隔
                String[] numberStrs = line.split("\\s+");
                List<String> elementsInLine = new ArrayList<>();
                
                // 解析每行的字符串
                for (String numStr : numberStrs) {
                    try {
                        String element = numStr;
                        elementsInLine.add(element);
                    } catch (NumberFormatException e) {
                        System.err.println("第" + lineNumber + "行：跳过无效的字符串格式: " + numStr);
                    }
                }
                
                if (elementsInLine.isEmpty()) {
                    System.out.println("第" + lineNumber + "行：无有效字符串");
                } else {
                    // 对当前行的字符串进行排序
                    String[] array = elementsInLine.toArray(new String[0]);
                    String[] sortedArray = sort(array);
                    
                    // 构建排序结果行
                    StringBuilder resultLine = new StringBuilder();
                    for (int i = 0; i < sortedArray.length; i++) {
                        resultLine.append(sortedArray[i]);
                        if (i < sortedArray.length - 1) {
                            resultLine.append(" ");
                        }
                    }
                    
                    // 添加到结果列表
                    allResults.add(resultLine.toString());
                    System.out.println("====第" + lineNumber + "行排序结果====\n" + Arrays.toString(sortedArray));
                }
            }
            
            // 处理完所有行后，将结果保存到单个文件
            if (!allResults.isEmpty()) {
                saveAllResultsToFile(allResults, input.replace(".txt", "_sorted.txt"));
            }
        } catch (FileNotFoundException e) {
            System.err.println("找不到文件: " + input);
        }
    }
}