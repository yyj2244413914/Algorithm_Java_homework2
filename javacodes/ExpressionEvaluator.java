import java.util.Scanner;

/**
 * 表达式计算器：支持整数和小数，运算符 + - * / ^，以及圆括号。
 * 使用项目中的 MyStack 实现运算符栈和值栈（遵循题目要求）。
 * 对于括号不匹配或语法错误会抛出异常并在 main 中打印错误信息。
 */
public class ExpressionEvaluator {

    // 运算优先级
    private static int precedence(char op) {
        switch (op) {
            case '+': case '-': return 1;
            case '*': case '/': return 2;
            case '^': return 3;
            default: return 0;
        }
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    // 对栈顶运算符执行计算并把结果压回值栈
    private static void applyTopOperator(MyStack<Character> ops, MyStack<Double> vals) throws Exception {
        if (ops.isEmpty()) throw new Exception("缺少运算符");
        char op = ops.pop();
        // 对于二元运算，先取右操作数，再取左操作数
        if (vals.isEmpty()) throw new Exception("操作数不足");
        double b = vals.pop();
        if (vals.isEmpty()) throw new Exception("操作数不足");
        double a = vals.pop();
        double res;
        switch (op) {
            case '+': res = a + b; break;
            case '-': res = a - b; break;
            case '*': res = a * b; break;
            case '/':
                if (b == 0.0) throw new Exception("除以零错误");
                res = a / b; break;
            case '^': res = Math.pow(a, b); break;
            default: throw new Exception("未知运算符: " + op);
        }
        vals.push(res);
    }

    /**
     * 计算表达式并返回结果（double）。
     * 支持空格，整数和小数，括号和运算符 + - * / ^ 。
     */
    public static double evaluate(String expr) throws Exception {
        if (expr == null) throw new Exception("表达式为空");
        MyStack<Double> vals = new MyStack<>();
        MyStack<Character> ops = new MyStack<>();

        String s = expr.trim();
        int n = s.length();
        for (int i = 0; i < n; ) {
            char ch = s.charAt(i);
            if (Character.isWhitespace(ch)) { i++; continue; }

            // 数字（整数或小数）
            if (Character.isDigit(ch) || ch == '.') {
                int j = i;
                boolean hasDot = false;
                while (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) {
                    if (s.charAt(j) == '.') {
                        if (hasDot) throw new Exception("数字格式错误：多个小数点");
                        hasDot = true;
                    }
                    j++;
                }
                String numStr = s.substring(i, j);
                double val = Double.parseDouble(numStr);
                vals.push(val);
                i = j;
                continue;
            }

            // 左括号
            if (ch == '(') {
                ops.push(ch);
                i++; continue;
            }

            // 右括号：弹出直到匹配左括号
            if (ch == ')') {
                boolean found = false;
                while (!ops.isEmpty()) {
                    // peek top
                    char top = ops.pop();
                    if (top == '(') { found = true; break; }
                    // top 不是 '('：它是运算符，要在 vals 上执行
                    // but we popped it already, so we need to apply it using temp behavior
                    // To apply, push top back then call applyTopOperator which pops it
                    ops.push(top);
                    applyTopOperator(ops, vals);
                }
                if (!found) throw new Exception("括号不匹配：缺少 '('");
                i++; continue;
            }

            // 运算符：包括处理一元负号
            if (isOperator(ch)) {
                // 判断是否为一元负号：当它位于表达式开头，或前一个非空字符是 '(' 或另一个运算符
                boolean unary = false;
                if (ch == '-') {
                    // look back for previous non-space char
                    int k = i - 1;
                    while (k >= 0 && Character.isWhitespace(s.charAt(k))) k--;
                    if (k < 0) unary = true;
                    else {
                        char prev = s.charAt(k);
                        if (prev == '(' || isOperator(prev)) unary = true;
                    }
                }
                if (unary) {
                    // 如果后面是数字，则把负号与数字一并解析为负数
                    int j = i + 1;
                    while (j < n && Character.isWhitespace(s.charAt(j))) j++;
                    if (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) {
                        int t = j;
                        boolean hasDot = false;
                        while (t < n && (Character.isDigit(s.charAt(t)) || s.charAt(t) == '.')) {
                            if (s.charAt(t) == '.') {
                                if (hasDot) throw new Exception("数字格式错误：多个小数点");
                                hasDot = true;
                            }
                            t++;
                        }
                        String numStr = s.substring(j, t);
                        double val = -Double.parseDouble(numStr);
                        vals.push(val);
                        i = t;
                        continue;
                    } else {
                        // 一元负号前置在括号或其他位置，例如 '-(3+2)'，等价于 0 - ( ... )
                        vals.push(0.0);
                        // treat '-' as binary operator below
                    }
                }

                // 处理运算符优先级和结合性：^ 为右结合，其它为左结合
                while (!ops.isEmpty()) {
                    // peek top operator
                    char top = ops.pop();
                    ops.push(top);
                    if (top == '(') break;
                    int pTop = precedence(top);
                    int pCur = precedence(ch);
                    if (pTop > pCur || (pTop == pCur && ch != '^')) {
                        // top 的优先级更高，或优先级相同且当前是左结合 -> 先执行 top
                        applyTopOperator(ops, vals);
                    } else break;
                }
                ops.push(ch);
                i++; continue;
            }

            throw new Exception("无法识别的字符: '" + ch + "'");
        }

        // 处理剩余运算符
        while (!ops.isEmpty()) {
            char top = ops.pop();
            if (top == '(' || top == ')') throw new Exception("括号不匹配");
            ops.push(top);
            applyTopOperator(ops, vals);
        }

        if (vals.isEmpty()) throw new Exception("没有计算结果");
        double result = vals.pop();
        if (!vals.isEmpty()) throw new Exception("表达式格式错误：多余操作数");
        return result;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("请输入算术表达式，支持 + - * / ^ 和小数，输入空行退出：");
        while (in.hasNextLine()) {
            String line = in.nextLine();
            if (line == null) break;
            line = line.trim();
            if (line.length() == 0) break;
            try {
                double res = evaluate(line);
                System.out.println("= " + res);
            } catch (Exception e) {
                System.out.println("错误: " + e.getMessage());
            }
        }
        in.close();
    }
}
