import java.io.PrintWriter;

/**
 * 基于顺序数组的List实现
 * 数据类型：Character
 */
public class ArrayListImpl implements List<Character> {
    private static final int DEFAULT_CAPACITY = 512;
    private Character[] data;
    private int size;
    private int cursor;
    private int capacity;
    
    public ArrayListImpl() {
        this(DEFAULT_CAPACITY);
    }
    
    public ArrayListImpl(int capacity) {
        this.capacity = capacity;
        this.data = new Character[capacity];
        this.size = 0;
        this.cursor = -1; // 空列表时光标为-1
    }
    
    @Override
    public void insert(Character newElement) throws ListException {
        if (newElement == null) {
            throw new ListException("Cannot insert null element");
        }
        
        if (isFull()) {
            throw new ListException("List is full, cannot insert new element");
        }
        
        if (isEmpty()) {
            // 空列表，插入第一个元素
            data[0] = newElement;
            cursor = 0;
            size = 1;
        } else if (cursor == -1) {
            // 光标为-1，插入到头部
            for (int i = size; i > 0; i--) {
                data[i] = data[i - 1];
            }
            data[0] = newElement;
            cursor = 0;
            size++;
        } else {
            // 在光标位置后插入元素
            // 将光标后的元素向后移动
            for (int i = size; i > cursor + 1; i--) {
                data[i] = data[i - 1];
            }
            data[cursor + 1] = newElement;
            cursor++; // 移动光标到新插入的元素
            size++;
        }
    }
    
    @Override
    public void remove() {
        if (isEmpty()) {
            return; // 空列表，什么都不做
        }
        
        // 将光标后的元素向前移动
        for (int i = cursor; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        
        // 调整光标位置
        if (size == 0) {
            cursor = -1; // 列表变空
        } else if (cursor >= size) {
            cursor = 0; // 删除的是最后一个元素，光标移到开头
        }
        // 否则光标保持在当前位置
    }
    
    @Override
    public void replace(Character newElement) {
        if (isEmpty() || newElement == null) {
            return;
        }
        
        data[cursor] = newElement;
    }
    
    @Override
    public void clear() {
        size = 0;
        cursor = -1;
        // 不需要清空数组内容，size控制访问范围
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
        cursor = 0;
        return true;
    }
    
    @Override
    public boolean gotoEnd() {
        if (isEmpty()) {
            return false;
        }
        cursor = size - 1;
        return true;
    }
    
    @Override
    public boolean gotoNext() {
        if (isEmpty() || cursor >= size - 1) {
            return false;
        }
        cursor++;
        return true;
    }
    
    @Override
    public boolean gotoPrev() {
        if (isEmpty() || cursor <= 0) {
            return false;
        }
        cursor--;
        return true;
    }
    
    @Override
    public Character getCursor() {
        if (isEmpty()) {
            return null;
        }
        return data[cursor];
    }
    
    @Override
    public void showStructure(PrintWriter pw) {
        if (isEmpty()) {
            pw.println("Empty list {capacity = " + capacity + ", length = 0, cursor = -1}");
            return;
        }
        
        // 输出所有元素
        for (int i = 0; i < size; i++) {
            pw.print(data[i] + " ");
        }
        pw.println("{capacity = " + capacity + ", length = " + size + ", cursor = " + cursor + "}");
    }
    
    @Override
    public void moveToNth(int n) {
        if (isEmpty() || n < 0 || n >= size || cursor < 0 || cursor >= size) {
            return;
        }
        
        Character element = data[cursor];
        
        // 删除当前元素
        for (int i = cursor; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;
        
        // 在位置n插入元素
        for (int i = size; i > n; i--) {
            data[i] = data[i - 1];
        }
        data[n] = element;
        size++;
        cursor = n;
    }
    
    @Override
    public boolean find(Character searchElement) {
        if (isEmpty() || searchElement == null) {
            return false;
        }
        
        // 从光标位置开始搜索
        for (int i = cursor; i < size; i++) {
            if (data[i].equals(searchElement)) {
                cursor = i;
                return true;
            }
        }
        
        // 如果没找到，光标移到最后一个元素
        if (size > 0) {
            cursor = size - 1;
        }
        return false;
    }
    
    // 获取当前大小（用于调试）
    public int getSize() {
        return size;
    }
    
    // 获取当前光标位置（用于调试）
    public int getCursorPosition() {
        return cursor;
    }
}
