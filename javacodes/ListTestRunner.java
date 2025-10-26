import java.io.*;
import java.util.Scanner;

/**
 * List ADT 测试运行器
 * 支持三种实现：顺序数组、单向链表、双向链表
 */
public class ListTestRunner {
    
    public static void main(String[] args) {
        System.out.println("=== List ADT 测试运行器 ===");
        System.out.println("1. 顺序数组实现");
        System.out.println("2. 单向链表实现");
        System.out.println("3. 双向链表实现");
        System.out.print("请选择测试类型 (1-3): ");
        String name = "";
        try (Scanner scanner = new Scanner(System.in)) {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1 -> {testImplementation("顺序数组", new ArrayListImpl()); name = "顺序数组";}
                case 2 -> {testImplementation("单向链表", new SinglyLinkedListImpl()); name = "单向链表";}
                case 3 -> {testImplementation("双向链表", new DoublyLinkedListImpl()); name = "双向链表";}
                default -> System.out.println("无效选择！");
            }
        }
        // 对比结果文件与标准答案
        System.out.println("正在将" + name + "的测试结果与List_result.txt进行对比...");
        boolean isEqual = filesEqual(
            getDataFilePath(name.replaceAll("\\s+", "") + "_result.txt"), 
            getDataFilePath("list_result.txt")
        );
        System.out.println("对比结果: " + (isEqual ? "相同" : "不同"));
    }
    
    /**
     * 根据运行目录获取正确的文件路径
     * @param fileName 文件名
     * @return 正确的文件路径
     */
    private static String getDataFilePath(String fileName) {
        return new File("data/list_testcase.txt").exists() ? 
            "data/" + fileName : "../data/" + fileName;
    }
    
    //将list_testcase文档中字符转换为:ist.java中的对应方法。
    private static void testImplementation(String name, List<Character> list) {
        System.out.println("\n=== 测试 " + name + " 实现 ===");
        
        try {
            // 读取测试用例
            try (Scanner fileScanner = new Scanner(new File(getDataFilePath("list_testcase.txt")));
                 PrintWriter resultWriter = new PrintWriter(new FileWriter(getDataFilePath(name.replaceAll("\\s+", "") + "_result.txt")))) {
            
            int testCase = 1;
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine().trim();
                if (line.isEmpty()) continue;
                
                System.out.println("测试用例 " + testCase + ": " + line.substring(0, Math.min(50, line.length())) + "...");
                
                // 处理命令序列（不清空列表，保留上一个测试用例的状态）
                String[] commands = line.split("\\s+");
                for (String command : commands) {
                    if (command.isEmpty()) continue;
                    
                    try {
                        processCommand(list, command);
                    } catch (ListException e) {
                        resultWriter.println("Error: " + e.getMessage());
                    }
                }
                
                // 输出最终状态
                list.showStructure(resultWriter);
                testCase++;
            }
            }
            
            System.out.println(name + " 测试完成！结果已保存到 ../" + name.replaceAll("\\s+", "") + "_result.txt\n（若初次运行，则该txt文件会自动在data目录下创建；若多次运行，则该txt文件会自动覆盖）");
            
        } catch (FileNotFoundException e) {
            System.err.println("找不到测试文件 ../list_testcase.txt");
        } catch (IOException e) {
            System.err.println("文件读写错误: " + e.getMessage());
        }
    }
    
    private static void processCommand(List<Character> list, String command) throws ListException {
        if (command.length() < 1) return;
        
        char operation = command.charAt(0);
        Character element = null;
        
        // 提取元素（如果有）
        if (command.length() > 1) {
            element = command.charAt(1);
        }
        
        switch (operation) {
            case '+' -> { // insert
                if (element != null) {
                    list.insert(element);
                }
            }
            case '-' -> list.remove(); // remove
            case '=' -> { // replace
                if (element != null) {
                    list.replace(element);
                }
            }
            case '#' -> list.gotoBeginning(); // gotoBeginning
            case '*' -> list.gotoEnd(); // gotoEnd
            case '>' -> list.gotoNext(); // gotoNext
            case '<' -> list.gotoPrev(); // gotoPrev
            case '~' -> list.clear(); // clear
            default -> {
                // 忽略未知命令
            }
        }
    }

    /**
     * 比较两个文件的内容是否完全相同（按行比较，区分大小写，行尾差异视为不同）
     * 如果任何一行不同或读取出现错误则返回 false
     * @param path1 第一个文件路径
     * @param path2 第二个文件路径
     * @return 相同返回 true，否则返回 false
     */
    public static boolean filesEqual(String path1, String path2) {
        try (BufferedReader r1 = new BufferedReader(new FileReader(path1));
             BufferedReader r2 = new BufferedReader(new FileReader(path2))) {
            String l1, l2;
            while ((l1 = r1.readLine()) != null) {
                l2 = r2.readLine();
                if (l2 == null) return false; // 第二个文件提前结束
                if (!l1.equals(l2)) return false; // 内容不同
            }
            // 确认第二个文件也已结束
            return r2.readLine() == null;
        } catch (IOException e) {
            // 读取出错视为不相同
            System.err.println("文件读写错误: " + e.getMessage());
            return false;
        }
    }
}
