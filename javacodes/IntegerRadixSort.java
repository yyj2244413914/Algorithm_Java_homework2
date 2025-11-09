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
 * 整数基数排序实现类
 * 使用ResizingQueue作为桶来实现整数的基数排序
 */
public class IntegerRadixSort {

    /**
     * 对整数数组进行基数排序
     * @param array 待排序的整数数组
     * @return 排序后的整数数组
     */
    public static Integer[] sort(Integer[] array) {
        if (array == null || array.length <= 1) {
            return array;
        }
        
        // 找出最大数，确定最大位数
        int max = findMax(array);
        int maxDigits = countDigits(max);
        
        // 创建10个队列作为桶（对应0-9的数字）
        ResizingQueue<Integer>[] buckets = createBuckets();
        
        int mod = 10; // 当前处理的位的模数
        int div = 1;  // 当前处理的位的除数
        
        // 从低位到高位进行排序
        for (int i = 0; i < maxDigits; i++) {
            // 将数据分配到对应的桶中
            for (int num : array) {
                int digit = (num % mod) / div;
                buckets[digit].enqueue(num);
            }
            
            // 将桶中的数据按顺序放回到数组中
            int index = 0;
            for (ResizingQueue<Integer> bucket : buckets) {
                while (!bucket.isEmpty()) {
                    array[index++] = bucket.dequeue();
                }
            }
            
            // 更新模数和除数，准备处理下一位
            mod *= 10;
            div *= 10;
        }
        
        return array;
    }
    
    // 辅助方法：找出数组中的最大值
    private static int findMax(Integer[] array) {
        int max = array[0];
        for (int num : array) {
            if (num > max) {
                max = num;
            }
        }
        return max;
    }
    
    // 辅助方法：计算一个数的位数
    private static int countDigits(int number) {
        if (number == 0) return 1;
        int count = 0;
        while (number > 0) {
            count++;
            number /= 10;
        }
        return count;
    }
    
    // 辅助方法：创建整数排序的桶
    private static ResizingQueue<Integer>[] createBuckets() {
        @SuppressWarnings("unchecked")
        ResizingQueue<Integer>[] buckets = new ResizingQueue[10];
        for (int i = 0; i < 10; i++) {
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
     * 主方法，用于测试整数基数排序
     */
    public static void main(String[] args) {
        // 测试整数排序
        String input=FileImt.getDataFilePath("radixSort1.txt");
        System.out.println("正在从radixSort1.txt文件中读取数据：");
        
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
                
                // 假设每行包含多个整数，以空格分隔
                String[] numberStrs = line.split("\\s+");
                List<Integer> numbersInLine = new ArrayList<>();
                
                // 解析每行的整数
                for (String numStr : numberStrs) {
                    try {
                        int number = Integer.parseInt(numStr);
                        numbersInLine.add(number);
                    } catch (NumberFormatException e) {
                        System.err.println("第" + lineNumber + "行：跳过无效的数字格式: " + numStr);
                    }
                }
                
                if (numbersInLine.isEmpty()) {
                    System.out.println("第" + lineNumber + "行：无有效数字");
                } else {
                    // 对当前行的数字进行排序
                    Integer[] array = numbersInLine.toArray(new Integer[0]);
                    Integer[] sortedArray = sort(array);
                    
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