import java.io.PrintWriter;

/**
 * 基于单向链表的List实现
 * 数据类型：Character
 */
public class SinglyLinkedListImpl implements List<Character> {
    private static final int DEFAULT_CAPACITY = 512;
    
    // 单向链表节点
    private static class Node {
        Character data;
        Node next;
        
        Node(Character data) {
            this.data = data;
            this.next = null;
        }
    }
    
    private Node head;
    private Node cursor;
    private int size;
    private int capacity;
    
    public SinglyLinkedListImpl() {
        this(DEFAULT_CAPACITY);
    }
    
    public SinglyLinkedListImpl(int capacity) {
        this.capacity = capacity;
        this.head = null;
        this.cursor = null;
        this.size = 0;
    }
    
    @Override
    public void insert(Character newElement) throws ListException {
        if (newElement == null) {
            throw new ListException("Cannot insert null element");
        }
        
        if (isFull()) {
            throw new ListException("List is full, cannot insert new element");
        }
        
        Node newNode = new Node(newElement);
        
        if (isEmpty()) {
            // 空列表，插入第一个元素
            head = newNode;
            cursor = newNode;
            size = 1;
        } else if (cursor == null) {
            // 光标为null，插入到头部
            newNode.next = head;
            head = newNode;
            cursor = newNode;
            size++;
        } else {
            // 在光标位置后插入元素
            newNode.next = cursor.next;
            cursor.next = newNode;
            cursor = newNode; // 移动光标到新插入的元素
            size++;
        }
    }
    
    @Override
    public void remove() {
        if (isEmpty()) {
            return; // 空列表，什么都不做
        }
        
        if (cursor == head) {
            // 删除头节点
            head = head.next;
            if (head == null) {
                cursor = null; // 列表变空
            } else {
                cursor = head; // 光标移到新的头节点
            }
        } else {
            // 删除非头节点
            Node prev = findPreviousNode(cursor);
            if (prev != null) {
                prev.next = cursor.next;
                if (cursor.next == null) {
                    // 删除的是最后一个节点，光标移到开头
                    cursor = head;
                } else {
                    // 光标移到下一个节点
                    cursor = cursor.next;
                }
            }
        }
        size--;
    }
    
    @Override
    public void replace(Character newElement) {
        if (isEmpty() || newElement == null || cursor == null) {
            return;
        }
        
        cursor.data = newElement;
    }
    
    @Override
    public void clear() {
        head = null;
        cursor = null;
        size = 0;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public boolean isFull() {
        return size >= capacity;
    }
    
    @Override
    public boolean gotoBeginning() {
        if (isEmpty()) {
            return false;
        }
        cursor = head;
        return true;
    }
    
    @Override
    public boolean gotoEnd() {
        if (isEmpty()) {
            return false;
        }
        
        Node current = head;
        while (current.next != null) {
            current = current.next;
        }
        cursor = current;
        return true;
    }
    
    @Override
    public boolean gotoNext() {
        if (isEmpty() || cursor == null || cursor.next == null) {
            return false;
        }
        cursor = cursor.next;
        return true;
    }
    
    @Override
    public boolean gotoPrev() {
        if (isEmpty() || cursor == null || cursor == head) {
            return false;
        }
        
        Node prev = findPreviousNode(cursor);
        if (prev != null) {
            cursor = prev;
            return true;
        }
        return false;
    }
    
    @Override
    public Character getCursor() {
        if (isEmpty() || cursor == null) {
            return null;
        }
        return cursor.data;
    }
    
    @Override
    public void showStructure(PrintWriter pw) {
        if (isEmpty()) {
            pw.println("Empty list {capacity = " + capacity + ", length = 0, cursor = -1}");
            return;
        }
        
        // 输出所有元素
        Node current = head;
        while (current != null) {
            pw.print(current.data + " ");
            current = current.next;
        }
        
        // 计算光标位置
        int cursorPos = getCursorPosition();
        pw.println("{capacity = " + capacity + ", length = " + size + ", cursor = " + cursorPos + "}");
    }
    
    @Override
    public void moveToNth(int n) {
        if (isEmpty() || n < 0 || n >= size || cursor == null) {
            return;
        }
        
        Character element = cursor.data;
        
        // 删除当前节点
        remove();
        
        // 调整目标位置（因为删除了一个元素）
        if (n >= size) {
            n = size - 1; // 如果目标位置超出范围，移到最后一个位置
        }
        
        // 在位置n插入元素
        if (n == 0) {
            // 插入到头部
            Node newNode = new Node(element);
            newNode.next = head;
            head = newNode;
            cursor = newNode;
        } else {
            // 插入到中间或尾部
            Node current = head;
            for (int i = 0; i < n - 1; i++) {
                current = current.next;
            }
            Node newNode = new Node(element);
            newNode.next = current.next;
            current.next = newNode;
            cursor = newNode;
        }
        size++;
    }
    
    @Override
    public boolean find(Character searchElement) {
        if (isEmpty() || searchElement == null) {
            return false;
        }
        
        // 从光标位置开始搜索
        Node current = cursor;
        while (current != null) {
            if (current.data.equals(searchElement)) {
                cursor = current;
                return true;
            }
            current = current.next;
        }
        
        // 如果没找到，光标移到最后一个元素
        gotoEnd();
        return false;
    }
    
    // 辅助方法：找到指定节点的前一个节点
    private Node findPreviousNode(Node target) {
        if (target == null || target == head) {
            return null;
        }
        
        Node current = head;
        while (current != null && current.next != target) {
            current = current.next;
        }
        return current;
    }
    
    // 获取当前光标位置（用于调试）
    private int getCursorPosition() {
        if (isEmpty() || cursor == null) {
            return -1;
        }
        
        int pos = 0;
        Node current = head;
        while (current != null && current != cursor) {
            current = current.next;
            pos++;
        }
        return pos;
    }
    
    // 获取当前大小（用于调试）
    public int getSize() {
        return size;
    }
}
