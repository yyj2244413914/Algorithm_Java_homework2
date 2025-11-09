import java.util.Scanner;

/**
 * 表达式计算器：支持整数和小数，运算符 + - * / ^，以及圆括号。
 * 使用项目中的 MyStack 实现运算符栈和值栈（遵循题目要求）。
 * 对于括号不匹配或语法错误会抛出异常并在 main 中打印错误信息。
 */
public class ExpressionEvaluator {

    //规定运算符优先级
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
    
    /**
     * 执行二元运算的通用方法
     * 提取重复的运算逻辑到单一方法
     */
    private static double performOperation(char op, double a, double b) throws Exception {
        switch (op) {
            case '+': return a + b;
            case '-': return a - b;
            case '*': return a * b;
            case '/':
                if (b == 0.0) throw new Exception("除以零错误");
                return a / b;
            case '^': return Math.pow(a, b);
            default: throw new Exception("未知运算符: " + op);
        }
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
        
        // 使用提取的通用方法执行运算
        double res = performOperation(op, a, b);
        vals.push(res);
    }

    /**
     * 计算表达式并返回结果（double）。
     * 支持空格，整数和小数，括号和运算符 + - * / ^ 。
     */
    public static double evaluate(String expr) throws Exception {
        // 先将中缀表达式转换为后缀（逆波兰），再用 MyStack 对后缀表达式求值
        if (expr == null) throw new Exception("表达式为空");
        java.util.List<String> postfix = infixToPostfix(expr);
        return evalPostfix(postfix);
    }
    
    /**
     * 第一步扫描：将输入表达式解析为标准的token数组
     * 该方法专门用于将字符串表达式分解为操作数、运算符和括号等token
     */
    private static java.util.List<String> parseToTokens(String expr) throws Exception {
        String s = expr.trim();
        int n = s.length();
        java.util.List<String> tokens = new java.util.ArrayList<>();
        boolean expectOperand = true; // 标记是否期望遇到操作数（用于识别一元负号）

        // 从左到右扫描表达式，将其分解为token
        for (int i = 0; i < n; ) {
            char ch = s.charAt(i);
            if (Character.isWhitespace(ch)) {  // 跳过空格
                i++;
                continue;
            }

            // 处理数字（包括小数和带一元负号的数字）
            if (Character.isDigit(ch) || ch == '.' || (ch == '-' && expectOperand)) {
                // 处理一元负号作为数字的一部分
                int j = i;
                if (s.charAt(j) == '-') j++; // 包含负号
                boolean hasDot = false;
                // 收集完整数字（包括小数点）
                while (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) {
                    if (s.charAt(j) == '.') {
                        if (hasDot) throw new Exception("数字格式错误：多个小数点");
                        hasDot = true;
                    }
                    j++;
                }
                if (i == j) throw new Exception("无法解析数字");
                String num = s.substring(i, j);
                tokens.add(num);  // 添加数字token
                i = j;
                expectOperand = false;  // 数字后期望遇到运算符或右括号
                continue;
            }

            // 处理左括号
            if (ch == '(') {
                tokens.add(String.valueOf(ch));  // 添加左括号token
                i++;
                expectOperand = true;  // 左括号后期望遇到操作数
                continue;
            }

            // 处理右括号
            if (ch == ')') {
                tokens.add(String.valueOf(ch));  // 添加右括号token
                i++;
                expectOperand = false;  // 右括号后期望遇到运算符或右括号
                continue;
            }

            // 处理运算符
            if (isOperator(ch)) {
                // 特殊处理：如果是一元负号并且后面跟左括号，如 -(a+b)，转换为 0 - (a+b)
                if (ch == '-' && expectOperand) {
                    int k = i + 1;
                    while (k < n && Character.isWhitespace(s.charAt(k))) k++;
                    if (k < n && s.charAt(k) == '(') {
                        tokens.add("0");  // 添加 0 作为一元负号的左操作数
                        // 接下来按二元减处理
                    }
                }

                tokens.add(String.valueOf(ch));  // 添加运算符token
                i++;
                expectOperand = true;  // 运算符后期望遇到操作数
                continue;
            }

            throw new Exception("无法识别的字符: '" + ch + "'");
        }

        return tokens;
    }

    /**
     * 第二步扫描：将token数组转换为后缀（逆波兰）表示
     * 
     * 中缀转后缀的算法步骤（Shunting-yard 算法）：
     * 1. 初始化一个空栈用于存储运算符
     * 2. 初始化一个空列表用于存储后缀表达式（输出）
     * 3. 从左到右扫描中缀表达式的每个 token：
     *    a. 如果是操作数，添加到输出列表
     *    b. 如果是左括号，压入栈中
     *    c. 如果是右括号，弹出栈中的元素并添加到输出，直到遇到左括号
     *       （左括号弹出但不添加到输出）
     *    d. 如果是运算符，比较其与栈顶运算符的优先级：
     *       - 如果栈为空或栈顶是左括号，直接压入栈
     *       - 如果当前运算符优先级高于栈顶，直接压入栈
     *       - 如果当前运算符优先级低于或等于栈顶（且当前运算符不是右结合的），
     *         弹出栈顶并添加到输出，重复比较直到条件不满足，然后压入当前运算符
     * 4. 扫描完成后，弹出栈中剩余的所有运算符并添加到输出
     */
    public static java.util.List<String> infixToPostfix(String expr) throws Exception {
        // 第一步：解析表达式为token数组
        java.util.List<String> tokens = parseToTokens(expr);
        
        // 第二步：将token数组转换为逆波兰表达式
        java.util.List<String> output = new java.util.ArrayList<>();  // 存储后缀表达式的输出列表
        MyStack<Character> ops = new MyStack<>();  // 用于存储运算符的栈

        // 从左到右扫描token数组
        for (String token : tokens) {
            // 处理数字操作数
            if (token.matches("^-?\\d+(\\.\\d+)?$") || token.equals("0")) {
                output.add(token);  // 数字直接添加到输出
                continue;
            }

            // 处理左括号
            if (token.equals("(")) {
                ops.push('(');  // 左括号入栈
                continue;
            }

            // 处理右括号
            if (token.equals(")")) {
                boolean found = false;
                // 弹出并添加运算符到输出，直到遇到左括号
                while (!ops.isEmpty()) {
                    char top = ops.pop();
                    if (top == '(') {
                        found = true;
                        break;
                    }
                    output.add(String.valueOf(top));
                }
                if (!found) throw new Exception("括号不匹配：缺少 '('");
                continue;
            }

            // 处理运算符
            if (token.length() == 1 && isOperator(token.charAt(0))) {
                char ch = token.charAt(0);
                // 根据运算符优先级处理
                int pCur = precedence(ch);
                while (!ops.isEmpty()) {
                    char top = ops.pop();
                    if (top == '(') {  // 遇到左括号停止弹出
                        ops.push(top);
                        break;
                    }
                    int pTop = precedence(top);
                    // 如果栈顶优先级更高，或相等且当前为左结合（^ 为右结合）则弹出
                    if (pTop > pCur || (pTop == pCur && ch != '^')) {
                        output.add(String.valueOf(top));
                    } else {
                        ops.push(top);  // 当前运算符优先级更高，停止弹出
                        break;
                    }
                }
                ops.push(ch);  // 当前运算符入栈
                continue;
            }

            throw new Exception("无法识别的token: '" + token + "'");
        }

        // 处理栈中剩余的所有运算符
        while (!ops.isEmpty()) {
            char top = ops.pop();
            if (top == '(' || top == ')') throw new Exception("括号不匹配");
            output.add(String.valueOf(top));
        }
        return output;
    }

    /**
     * 使用 MyStack 对后缀表达式求值
     * 
     * 后缀表达式求值步骤：
     * 1. 初始化一个空栈用于存储操作数
     * 2. 从左到右扫描后缀表达式的每个 token：
     *    a. 如果是操作数，转换为数字后压入栈
     *    b. 如果是运算符，弹出栈顶的两个操作数（先弹出的是右操作数，后弹出的是左操作数）
     *       执行运算，将结果压回栈
     * 3. 扫描完成后，栈中应只剩下一个元素，即为表达式的计算结果
     */
    public static double evalPostfix(java.util.List<String> postfix) throws Exception {
        MyStack<Double> vals = new MyStack<>();
        for (String tok : postfix) {
            if (tok.length() == 0) continue;
            char c0 = tok.charAt(0);
            // 判断是否为单字符运算符
            if (tok.length() == 1 && isOperator(c0)) {
                // 确保有足够的操作数
                if (vals.isEmpty()) throw new Exception("操作数不足");
                double b = vals.pop();  // 右操作数
                if (vals.isEmpty()) throw new Exception("操作数不足");
                double a = vals.pop();  // 左操作数
                
                // 使用提取的通用方法执行运算
                double res = performOperation(c0, a, b);
                vals.push(res);  // 结果入栈
            } else {
                // 认为是数字
                double v;
                try {
                    v = Double.parseDouble(tok);
                } catch (NumberFormatException e) {
                    throw new Exception("数字格式错误: " + tok);
                }
                vals.push(v);  // 操作数入栈
            }
        }
        // 检查结果是否唯一
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