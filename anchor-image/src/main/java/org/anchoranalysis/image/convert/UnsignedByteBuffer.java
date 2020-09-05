package org.anchoranalysis.image.convert;

import java.nio.ByteBuffer;
import lombok.Getter;

/**
 * Wraps a {@code ByteBuffer} but automatically performs conversion to {@code int}.
 * 
 * <p>The conversion applies to {@link ByteBuffer#get} and {@link ByteBuffer#put} of single
 * elements, but not to any mass get or put operations.
 * 
 * <p>The user has a choice of getting/setting using raw ({@link #getRaw}, {@link #putRaw} etc.) or
 * unsigned-conversion ({@link #getUnsigned}, {@link #putUnsigned} etc.) methods. The raw methods are always more
 * efficient, and so should be preferred when conversion is not needed.
 * 
 * @author Owen Feehan
 *
 */
public final class UnsignedByteBuffer extends UnsignedBuffer {

    /** The underlying storage buffer, to which calls are delegated with our without conversion. */
    @Getter private final ByteBuffer delegate;
    
    /**
     * Allocates a new (direct) buffer of unsigned-bytes.
     * 
     * @param capacity size of buffer.
     * @return newly created buffer (non-direct, i.e. backed by an array).
     */
    public static UnsignedByteBuffer allocate(int capacity) {
        return new UnsignedByteBuffer( ByteBuffer.allocate(capacity) );
    }

    /**
     * Exposes a raw byte-array as a buffer with unsigned-bytes.
     * 
     * @param array the byte-array
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code array} internally. 
     */
    public static UnsignedByteBuffer wrapRaw(byte[] array) {
        return new UnsignedByteBuffer( ByteBuffer.wrap(array) );
    }

    /**
     * Exposes a raw {@link ByteBuffer} as a buffer with unsigned-bytes.
     * 
     * @param bufferRaw the raw-buffer
     * @return a new instance of {@link UnsignedByteBuffer} that reuses {@code bufferRaw} internally. 
     */
    public static UnsignedByteBuffer wrapRaw(ByteBuffer bufferRaw) {
        return new UnsignedByteBuffer(bufferRaw);
    }

    
    /**
     * Creates given a delegate.
     * 
     * @param delegate the delegate
     */
    private UnsignedByteBuffer(ByteBuffer delegate) {
        super(delegate);
        this.delegate = delegate;
    }
    
    /**
     * Gets an unsigned-byte (represented as a byte) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsigned()}.
     * 
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw() {
        return delegate.get();
    }

    /**
     * Gets an unsigned-byte (represented as a byte) at a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #getUnsigned(int)}.
     * 
     * @param index the buffer position
     * @return unsigned-byte (represented by a byte)
     */
    public byte getRaw(int index) {
        return delegate.get(index);
    }
    
    /**
     * Gets an unsigned-byte (represented as a int) at the current buffer position.
     * 
     * @return unsigned-byte (represented by a int)
     */
    public int getUnsigned() {
        return PrimitiveConverter.unsignedByteToInt( getRaw() );
    }

    /**
     * Gets an unsigned-byte (represented as a int) at a particular buffer position.
     * 
     * @param index the buffer position
     * @return unsigned-byte (represented by a int)
     */
    public int getUnsigned(int index) {
        return PrimitiveConverter.unsignedByteToInt( getRaw(index) );
    }
    
    /**
     * Puts an unsigned-byte (represented as a byte) at the current buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsigned(int)}.
     * 
     * @param value unsigned-byte (represented by a byte)
     */
    public void putRaw(byte value) {
        delegate.put(value);
    }

    /**
     * Puts an unsigned-byte (represented as a byte) a particular buffer position.
     * 
     * <p>No further conversion occurs, so this method is more efficient than {@link #putUnsigned(int,int)}.
     * 
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public void putRaw(int index, byte value) {
        delegate.put(index, value);
    }
    
    /**
     * Puts an unsigned-byte (represented by an int) at current buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param value the unsigned-byte (represented by an int)
     */
    public void putUnsigned(int value) {
        putRaw( (byte) value);
    }

    /**
     * Puts an unsigned-byte (represented as a int) a particular buffer position.
     * 
     * <p>A conversion occurs from int to byte.
     * 
     * @param index the buffer position
     * @param value the unsigned-byte (represented by an int)
     */
    public void putUnsigned(int index, int value) {
        putRaw(index, (byte) value);
    }

    /**
     * Puts an unsigned-int (represented by a int) at current buffer position.
     * 
     * <p>A conversion occurs from int to long and then to byte.
     * 
     * @param value the unsigned-int (represented by an int)
     */
    public void putUnsignedInt(int value) {
        putRaw( (byte) PrimitiveConverter.unsignedIntToLong(value) );
    }
    
    /**
     * Puts a long at the current buffer position.
     * 
     * <p>A conversion occurs from long to byte.
     * 
     * @param value the long.
     */
    public void putLong(long value) {
        putRaw((byte) value);
    }
    
    /**
     * Puts a float at the current buffer position.
     * 
     * <p>A conversion occurs from float to byte.
     * 
     * @param value the float.
     */
    public void putFloat(float value) {
        putRaw( (byte) value);
    }

    /**
     * Puts a float at a particular buffer position.
     * 
     * <p>A conversion occurs from float to byte.
     * 
     * @param index the buffer position
     * @param value the float.
     */    
    public void putFloat(int index, float value) {
        putRaw( index, (byte) value);
    }
    
    /**
     * Puts a double at the current buffer position.
     * 
     * <p>A conversion occurs from double to byte.
     * 
     * @param value the double
     */
    public void putDouble(double value) {
        putRaw( (byte) value);
    }
    
    /**
     * Puts a double at a particular buffer position.
     * 
     * <p>A conversion occurs from double to byte.
     * 
     * @param index the buffer position
     * @param value the double
     */
    public void putDouble(int index, double value) {
        putRaw(index, (byte) value);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link ByteBuffer}.
     * 
     * This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     * 
     * @param source source of bytes to put
     */
    public void put(ByteBuffer source) {
        delegate.put(source);
    }

    /**
     * Relative put-method from an unsigned byte buffer, represented by a {@link UnsignedByteBuffer}.
     * 
     * This is identical to {@link ByteBuffer#put(ByteBuffer)}, with no return value.
     * 
     * @param source source of bytes to put
     */
    public void put(UnsignedByteBuffer source) {
        delegate.put(source.getDelegate());
    }
        
    /**
     * The array of the buffer ala {@link ByteBuffer#array}.
     * 
     * @return the array
     */
    public final byte[] array() {
        return delegate.array();
    }
}
