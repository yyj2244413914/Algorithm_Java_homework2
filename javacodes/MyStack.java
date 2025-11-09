// MyStack.java：独立的自定义栈实现（泛型支持）
public class MyStack<T> {
    private Object[] elements;  // 存储元素的数组
    private int top;            // 栈顶指针（-1表示空栈）
    private static final int DEFAULT_CAPACITY = 10;  // 默认初始容量

    // 构造方法：初始化栈
    public MyStack() {
        elements = new Object[DEFAULT_CAPACITY];
        top = -1;
    }

    // 入栈：添加元素到栈顶
    public void push(T item) {
        // 若栈满则扩容（容量翻倍）
        if (top == elements.length - 1) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
        elements[++top] = item;  // 栈顶指针上移并存储元素
    }

    // 出栈：移除并返回栈顶元素
    @SuppressWarnings("unchecked")
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("栈为空，无法执行出栈操作");
        }
        return (T) elements[top--];  // 返回栈顶元素并下移指针
    }

    // 判断栈是否为空
    public boolean isEmpty() {
        return top == -1;
    }
}
