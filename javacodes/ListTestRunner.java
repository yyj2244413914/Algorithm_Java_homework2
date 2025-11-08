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
                case 1 -> {
                    List<Character> list = new ArrayListImpl();
                    FileImt.runTests("顺序数组", list);
                    name = "顺序数组";
                }
                case 2 -> {
                    List<Character> list = new SinglyLinkedListImpl();
                    FileImt.runTests("单向链表", list);
                    name = "单向链表";
                }
                case 3 -> {
                    List<Character> list = new DoublyLinkedListImpl();
                    FileImt.runTests("双向链表", list);
                    name = "双向链表";
                }
                default -> System.out.println("无效选择！");
            }
        }
        // 对比结果文件与标准答案
        System.out.println("正在将" + name + "的测试结果与List_result.txt进行对比...");
    boolean isEqual = FileImt.filesEqual(
        FileImt.getDataFilePath(name.replaceAll("\s+", "") + "_result.txt"),
        FileImt.getDataFilePath("list_result.txt"));
        System.out.println("对比结果: " + (isEqual ? "相同" : "不同"));
    }

    // 文件读写行为已迁移到 FileImt.runTests/getDataFilePath

    public static void processCommand(List<Character> list, String command) throws ListException {
        if (command.length() < 1)
            return;

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
     * 
     * @param path1 第一个文件路径
     * @param path2 第二个文件路径
     * @return 相同返回 true，否则返回 false
     */
    // filesEqual 已集中到 FileImt.filesEqual
}
