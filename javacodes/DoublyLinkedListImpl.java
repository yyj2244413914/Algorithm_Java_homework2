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
            // next 和 prev 默认初始化为 null，无需显式赋值
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
            size = 1;
        } else if (cursor == null) {
            // 光标为null，插入到头部
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
            size++;
        } else {
            // 在光标位置后插入元素
            newNode.next = cursor.next;
            newNode.prev = cursor;
            if (cursor.next != null) {
                cursor.next.prev = newNode;
            } else {
                tail = newNode; // 光标在尾部，更新tail
            }
            cursor.next = newNode;
            size++;
        }
        // 统一设置光标指向新节点
        cursor = newNode;
    }
    
    @Override
    public void remove() {
        if (isEmpty() || cursor == null) {
            return; // 空列表或光标为null，什么都不做
        }
        
        if (size == 1) {
            // 只有一个元素
            head = null;
            tail = null;
            cursor = null;
        } else if (cursor == head) {
            // 删除头节点
            head = head.next;
            head.prev = null;  // head肯定不为null，因为size > 1
            cursor = head;
        } else if (cursor == tail) {
            // 删除尾节点
            tail = tail.prev;
            tail.next = null;  // tail肯定不为null，因为size > 1
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
        if (isEmpty() || cursor == null) {
            return false;
        }
        if (cursor.next == null) {
            return false;
        }
        cursor = cursor.next;
        return true;
    }
    
    @Override
    public boolean gotoPrev() {
        if (isEmpty() || cursor == null) {
            return false;
        }
        if (cursor.prev == null) {
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
        
        int currentPos = getCursorPosition();
        // 如果光标已经在目标位置，不需要移动
        if (currentPos == n) {
            return;
        }
        
        Node nodeToMove = cursor;
        
        // 先断开当前节点
        if (nodeToMove.prev != null) {
            nodeToMove.prev.next = nodeToMove.next;
        } else {
            head = nodeToMove.next;  // 是头节点
        }
        
        if (nodeToMove.next != null) {
            nodeToMove.next.prev = nodeToMove.prev;
        } else {
            tail = nodeToMove.prev;  // 是尾节点
        }
        
        // 断开节点后，列表大小减少了1
        // 如果目标位置n > 当前光标位置，需要调整目标位置（因为移除了当前节点）
        int targetPos = (n > currentPos) ? n - 1 : n;
        size--;  // 临时减少size
        
        // 重新插入到目标位置
        if (targetPos == 0) {
            // 插入到头部
            nodeToMove.prev = null;
            nodeToMove.next = head;
            if (head != null) {
                head.prev = nodeToMove;
            } else {
                // 列表断开后为空，这是唯一元素
                tail = nodeToMove;
            }
            head = nodeToMove;
        } else if (targetPos >= size) {
            // 插入到尾部（size是断开后的值，且size > 0，所以tail肯定不为null）
            nodeToMove.prev = tail;
            nodeToMove.next = null;
            tail.next = nodeToMove;
            tail = nodeToMove;
        } else {
            // 插入到中间位置（targetPos在1到size-1之间）
            Node target = head;
            for (int i = 0; i < targetPos; i++) {
                target = target.next;
            }
            // 此时target.prev肯定不为null，因为targetPos > 0
            nodeToMove.prev = target.prev;
            nodeToMove.next = target;
            target.prev.next = nodeToMove;
            target.prev = nodeToMove;
        }
        
        cursor = nodeToMove;
        size++;  // 恢复size
    }
    
    @Override
    public boolean find(Character searchElement) {
        if (isEmpty() || searchElement == null) {
            return false;
        }
        
        // 从光标位置开始搜索（如果cursor为null，从head开始）
        Node current = (cursor != null) ? cursor : head;
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
