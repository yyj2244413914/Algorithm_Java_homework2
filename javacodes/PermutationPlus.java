import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PermutationPlus {

        // 递归生成全排列，要求在包含重复字符的情况下，结果集中不包含重复排列
    public List<String> permutationPlusOne(String s) {
        // 只在进入递归前增加约束条件（每层跳过已处理过的相同字符），
        // 因此这里用基于 remaining 字符串的递归，但在每层使用 seen[] 去重。
        List<String> result = new ArrayList<>();
        if (s == null || s.length() == 0) return result;
        recursionHelperNoDup("", s, result);
        return result;
    }

    // 基于 remaining 字符串的递归辅助，但在每一层使用 seen[] 跳过重复字符
    private void recursionHelperNoDup(String current, String remaining, List<String> result) {
        if (remaining.length() == 0) {
            result.add(current);
            return;
        }
        boolean[] seen = new boolean[26];
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            int idx = c - 'a';
            if (seen[idx]) continue;
            seen[idx] = true;
            String newCurrent = current + c;
            String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
            recursionHelperNoDup(newCurrent, newRemaining, result);
        }
    }

        // 递归生成部分排列。要求在长度为n的字符串s中，选择k个字符进行排列。
    public List<String> permutationPlusTwo(String s, int k) {
        List<String> result = new ArrayList<>();
        if (s == null || s.length() == 0 || k <= 0 || k > s.length()) return result;
        recursionHelperPart("", s, k, result);
        return result;
    }

    // 递归生成部分排列的辅助函数
    private void recursionHelperPart(String current, String remaining, int k, List<String> result) {
        if (k == 0) {
            result.add(current);
            return;
        }
        for (int i = 0; i < remaining.length(); i++) {
            char c = remaining.charAt(i);
            String newCurrent = current + c;
            String newRemaining = remaining.substring(0, i) + remaining.substring(i + 1);
            recursionHelperPart(newCurrent, newRemaining, k - 1, result);
        }
    }

    public static void main(String[] args) {
        PermutationPlus obj = new PermutationPlus();
        System.out.println("=== 测试 变形1 ===");
        System.out.println("输入含有重复字符的字符串：");
        String testone = new Scanner(System.in).next();
        List<String> resultone = obj.permutationPlusOne(testone);
        int sortCount = 0;
        for (String s : resultone) {
            sortCount++;
            System.out.println("----" + sortCount + ": " + s + "----");
        }
        System.out.println("=== 测试 变形2 ===");
        System.out.println("输入待部分排序的字符串（不含重复字符）：");
        String testtwo = new Scanner(System.in).next();
        System.out.println("输入整数k：");
        int k = new Scanner(System.in).nextInt();
        List<String> resulttwo = obj.permutationPlusTwo(testtwo, k);
        sortCount = 0;
        for (String s : resulttwo) {
            sortCount++;
            System.out.println("----" + sortCount + ": " + s + "----");
        }
    }
}
