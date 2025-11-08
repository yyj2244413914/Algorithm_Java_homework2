
/**
 * ResizingQueue测试类
 */

import java.util.Scanner;

public class ResizingQueueTest {

    public static void main(String[] args) {
        System.out.println("=== ResizingQueue 测试运行器 ===");
        System.out.println("1. test1000.txt");
        System.out.println("2. test5000.txt");
        System.out.print("请选择测试文件 (1-2): ");
        String choiceName = "";
        String outPutName = "";
        String resultName = "";
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {
                    choiceName = "test1000.txt";
                    outPutName = "test1000_result.txt";
                    resultName = "result1000.txt";
                    ResizingQueue<Integer> queue = new ResizingQueue<>();
                    FileImt.runTests("循环队列", "test1000.txt", queue);
                }
                case 2 -> {
                    choiceName = "test5000.txt";
                    outPutName = "test5000_result.txt";
                    resultName = "result5000.txt";
                    ResizingQueue<Integer> queue = new ResizingQueue<>();
                    FileImt.runTests("循环队列", "test5000.txt", queue);
                }
                default -> System.out.println("无效选择！");
            }
        }
        // 对比结果文件与标准答案
        System.out.println("正在将" + outPutName + "的测试结果与" + resultName + "进行对比...");
        boolean isEqual = FileImt.filesEqual(
                FileImt.getDataFilePath(outPutName),
                FileImt.getDataFilePath(resultName));
        System.out.println("对比结果: " + (isEqual ? "相同" : "不同"));
    }

    /**
     * 为 ResizingQueue 提供的命令处理器：被 FileImt 调用以保持与其他测试的一致性
     * 支持命令：
     * '+' 后跟元素 -> enqueue
     * '-' -> dequeue
     * 其它命令将被忽略（与之前在 FileImt 中的处理保持一致）
     */
    /**
     * 处理循环队列的单个命令，符合说明：
     * - 数字（例如 123）表示 enqueue 该整数；
     * - '-' 表示 dequeue；
     * - '?' 表示生成当前队列的字符串表示（由调用者写入结果文件）。
     * 返回在遇到 '?' 时的输出字符串，否则返回 null。
     */
    public static String processCommand(ResizingQueue<Integer> queue, String command) {
        if (command == null || command.isEmpty()) return null;
        // 直接匹配单字符命令
        if ("-".equals(command)) {
            queue.dequeue();
            return null;
        }
        if ("?".equals(command)) {
            return queue.toString();
        }

        // 否则尝试解析为整数并入队
        try {
            int val = Integer.parseInt(command);
            queue.enqueue(val);
        } catch (NumberFormatException e) {
            // 非整数且非特殊符号，忽略
        }
        return null;
    }

    /**
     * 比较两个文件的内容是否完全相同（按行比较，区分大小写，行尾差异视为不同）
     * 如果任何一行不同或读取出现错误则返回 false
     * 
     * @param path1 第一个文件路径
     * @param path2 第二个文件路径
     * @return 相同返回 true，否则返回 false
     */
}
