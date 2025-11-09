import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Permutation {

    // 递归生成全排列
    public List<String> permutationByRecursion(String s) {
        List<String> result = new ArrayList<>();
        if (s == null || s.length() == 0) {
            return result;
        }
        // 调用辅助函数：初始当前排列为空，剩余字符为原始字符串
        recursionHelper("", s, result);
        return result;
    }

    // 递归辅助函数：current为当前已构建的排列，remaining为剩余可用字符
    private void recursionHelper(String current, String remaining, List<String> result) {
        //  base case：剩余字符为空，当前排列完成，加入结果集
        if (remaining.length() == 0) {
            result.add(current);
            return;
        }
        // 递归逻辑：遍历剩余字符，每次选择一个字符加入当前排列，剩余字符移除该字符后递归
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            // 新的当前排列 = current + 选择的字符
            String newCurrent = current + c;
            // 新的剩余字符 = 移除选中字符后的字符串
            String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
            // 递归处理新状态
            recursionHelper(newCurrent, newRemaining, result);
        }
    }

    public static void main(String[] args) {
        Permutation obj = new Permutation();
        System.out.println("测试递归实现：请输入待全排列的字符串（字符两两不相同）:");
        String input = new Scanner(System.in).nextLine();
        List<String> permutations = obj.permutationByRecursion(input);
        System.out.println("全排列结果:");
        int sortCount = 0;
        for (String p : permutations) {
            sortCount++;
            System.out.println("----"+ sortCount + ": " + p+"----");
        }
    }
}
