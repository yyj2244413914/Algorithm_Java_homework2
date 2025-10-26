import java.io.PrintWriter;

/**
 * 基于双向链表的List实现
 * 数据类型：Character
 */
public class DoublyLinkedListImpl implements List<Character> {
    private static final int DEFAULT_CAPACITY = 512;
    
    // 双向链表节点
    private static class Node {
        Character data;
        Node next;
        Node prev;
        
        Node(Character data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }
    
    private Node head;
    private Node tail;
    private Node cursor;
    private int size;
    private int capacity;
    
    public DoublyLinkedListImpl() {
        this(DEFAULT_CAPACITY);
    }
    
    public DoublyLinkedListImpl(int capacity) {
        this.capacity = capacity;
        this.head = null;
        this.tail = null;
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
            tail = newNode;
            cursor = newNode;
            size = 1;
        } else if (cursor == null) {
            // 光标为null，插入到头部
            newNode.next = head;
            if (head != null) {
                head.prev = newNode;
            }
            head = newNode;
            cursor = newNode;
            size++;
        } else {
            // 在光标位置后插入元素
            newNode.next = cursor.next;
            newNode.prev = cursor;
            
            if (cursor.next != null) {
                cursor.next.prev = newNode;
            } else {
                // 光标在尾部，更新tail
                tail = newNode;
            }
            
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
        
        if (cursor == head && cursor == tail) {
            // 只有一个元素
            head = null;
            tail = null;
            cursor = null;
        } else if (cursor == head) {
            // 删除头节点
            head = head.next;
            if (head != null) {
                head.prev = null;
            }
            cursor = head;
        } else if (cursor == tail) {
            // 删除尾节点
            tail = tail.prev;
            if (tail != null) {
                tail.next = null;
            }
            cursor = head; // 光标移到开头
        } else {
            // 删除中间节点
            cursor.prev.next = cursor.next;
            cursor.next.prev = cursor.prev;
            cursor = cursor.next; // 光标移到下一个节点
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
        tail = null;
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
        cursor = tail;
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
        if (isEmpty() || cursor == null || cursor.prev == null) {
            return false;
        }
        cursor = cursor.prev;
        return true;
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
            if (head != null) {
                head.prev = newNode;
            } else {
                tail = newNode;
            }
            head = newNode;
            cursor = newNode;
        } else if (n == size) {
            // 插入到尾部
            Node newNode = new Node(element);
            newNode.prev = tail;
            if (tail != null) {
                tail.next = newNode;
            } else {
                head = newNode;
            }
            tail = newNode;
            cursor = newNode;
        } else {
            // 插入到中间
            Node current = head;
            for (int i = 0; i < n; i++) {
                current = current.next;
            }
            
            Node newNode = new Node(element);
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
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
