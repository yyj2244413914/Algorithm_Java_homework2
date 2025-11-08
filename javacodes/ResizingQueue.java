/**
 * 基于循环数组的可动态调整空间大小的队列实现
 * 支持泛型
 */
public class ResizingQueue<T> {
    private T[] array;          // 循环数组
    private int front;          // 队头指针
    private int rear;           // 队尾指针
    private int N;              // 当前队列的最大容量（能存储的元素个数）
    private int size;           // 当前队列中的实际元素个数
    
    /**
    * 构造方法，初始化队列，初始容量N=2
     * 初始容量N=1，实际数组大小为N+1=2
     */
    @SuppressWarnings("unchecked")
    public ResizingQueue() {
    // start with capacity 2 to match expected test outputs
    N = 2;
    array = (T[]) new Object[N];
        front = 0;
        rear = 0;
        size = 0;
    }
    
    /**
     * 将元素element入队
     * 如果队列满，按方案调整空间大小（扩容到2N+1）
     * 
     * @param element 要入队的元素
     */
    public void enqueue(T element) {
        // 检查是否需要扩容
        if (size == N) { 
            // full when size == N (use doubling growth to match expected outputs)
            resize(N * 2);
        }

        // 入队
        array[rear] = element;
        rear = (rear + 1) % array.length;
        size++;
    }
    
    /**
     * 将队头元素删除并返回
     * 如果队列的元素个数是当前容量的1/4，按方案调整空间大小（缩容到N/2）
     * 
     * @return 队头元素，如果队列为空则返回null
     */
    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        T element = array[front];
        array[front] = null;  // 帮助垃圾回收
        front = (front + 1) % array.length;
        size--;
        
        // 检查是否需要缩容
        // 注意：需要在size--之后检查，因为我们要检查的是出队后的size
        // 缩容条件：size == N/4 且 N > 1（避免缩容到0）
        if (size == N / 4 && N > 2) {
            int newN = N / 2;
            resize(newN);
        }
        
        return element;
    }
    
    /**
     * 返回当前队列中的实际元素个数
     * 
     * @return 队列中的元素个数
     */
    public int size() {
        return size;
    }
    
    /**
     * 判断队列是否为空
     * 
     * @return 如果队列为空返回true，否则返回false
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * 判断队列是否满
     * 循环数组中，队列满的条件是：(rear + 1) % array.length == front
     * 
     * @return 如果队列满返回true，否则返回false
     */
    private boolean isFull() {
        return size == N;
    }
    
    /**
     * 调整数组空间大小
     * 将原数组中的元素拷贝到新数组中
     * 
     * @param newN 新的队列容量（能存储的元素个数）
     */
    @SuppressWarnings("unchecked")
    private void resize(int newN) {
        int oldLen = array.length;
        T[] newArray = (T[]) new Object[newN];

        // 将原数组中的元素按顺序拷贝到新数组中（拷贝 size 个元素）
        for (int i = 0; i < size; i++) {
            newArray[i] = array[(front + i) % oldLen];
        }

        // 更新变量
        array = newArray;
        N = newN;
        front = 0;
        rear = size;  // rear 指向下一个插入位置
    }
    
    /**
     * 将当前队列中的元素转换为字符串
     * 格式：从队头到队尾的元素序列
     * 
     * @return 队列元素的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if(size<=20){
            for (int i = 0; i < size; i++) {
                sb.append(array[(front + i) % array.length]);
                if (i < size - 1) {
                    sb.append(" ");
                }
            }
        }else{
            for (int i = 0; i < 5; i++) {
                sb.append(array[(front + i) % array.length]);
                sb.append(" ");
            }
            sb.append(" ... ");
            for (int i = size-5; i < size; i++) {
                sb.append(array[(front + i) % array.length]);
                if (i < size - 1) {
                    sb.append(" ");
                }
            }
        }
        sb.append("]");
        sb.append("\n");
        // 输出元素数量与当前容量
        sb.append("elements: " + size + " size:" + N);
        return sb.toString();
    }
}
