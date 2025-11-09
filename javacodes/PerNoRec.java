import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PerNoRec {
    // PermutationState：存储全排列过程中的状态（内部私有静态类）
    private static class PermutationState {
        private final String current;    // 当前已构建的排列
        private final String remaining;  // 剩余可用的字符

        // 构造方法：初始化状态
        public PermutationState(String current, String remaining) {
            this.current = current;
            this.remaining = remaining;
        }

        // getter方法：获取当前排列
        public String getCurrent() {
            return current;
        }

        // getter方法：获取剩余字符
        public String getRemaining() {
            return remaining;
        }
    }
        public List<String> permutationByNoRecursion(String s) {
            List<String> result = new ArrayList<>();
            if (s == null || s.length() == 0) {
                return result;
        }

    // 创建自定义栈，存储全排列状态（泛型指定为 PermutationState）
    MyStack<PermutationState> stack = new MyStack<>();
        // 初始状态入栈：当前排列为空，剩余字符为原始字符串
        stack.push(new PermutationState("", s));

        // 处理栈中所有状态
        while (!stack.isEmpty()) {
            PermutationState state = stack.pop();  // 弹出栈顶状态
            String current = state.getCurrent();
            String remaining = state.getRemaining();

            // 若剩余字符为空，当前排列完成，加入结果集
            if (remaining.length() == 0) {
                result.add(current);
                continue;
            }

            // 遍历剩余字符，生成新状态并压入栈（逆序入栈以保持与递归相同顺序）
            for (int i = remaining.length() - 1; i >= 0; i--) {
                char c = remaining.charAt(i);
                String newCurrent = current + c;
                String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
                stack.push(new PermutationState(newCurrent, newRemaining));
            }
        }

        return result;
    }

    // 测试方法
    public static void main(String[] args) {
        // 测试非递归实现（使用自定义 MyStack）
        PerNoRec perm = new PerNoRec();
        System.out.println("测试非递归实现：请输入用于排列的字符串（字符两两不相同）：");
        String testStr = new Scanner(System.in).nextLine();
        List<String> noRecursionResult = perm.permutationByNoRecursion(testStr);
        System.out.println("非递归全排列结果：");
        int sortCount = 0;
        for (String p : noRecursionResult) {
             sortCount++;
             System.out.println("----" + sortCount + ": " + p + "----");
            }
    }
}
