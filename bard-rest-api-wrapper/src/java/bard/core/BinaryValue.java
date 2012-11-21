package bard.core;

public class BinaryValue extends Value {
    private static final long serialVersionUID = 0x7ffb8bdf6786e543l;

    protected byte[] value;
    protected int size;
    protected int offset;

    protected BinaryValue() {
    }

    public BinaryValue(Value parent) {
        super(parent);
    }

    public BinaryValue(Value parent, String id) {
        super(parent, id);
    }

    public BinaryValue(Value parent, String id, byte[] value) {
        super(parent, id);
       this.setValue(value);
    }

    public BinaryValue(DataSource source, String id) {
        this(source, id, null);
    }

    public BinaryValue(DataSource source, String id, byte[] value) {
        this(source, id, value, 0, value == null? 0: value.length);
    }

    public BinaryValue(DataSource source, String id,
                       byte[] value, int offset, int size) {
        super(source, id);
        this.offset = offset;
        this.size = size;
        this.setValue(value);
    }

    public void setValue(byte[] value) {
        setValue(value, 0, value == null ? 0 :value.length);
    }

    public void setValue(byte[] value, int offset, int length) {
        this.value = value;
        this.offset = offset;
        this.size = length;
    }

    @Override
    public Object getValue() {
        return value;
    }

    public int size() {
        return size;
    }

    public int offset() {
        return offset;
    }
}
