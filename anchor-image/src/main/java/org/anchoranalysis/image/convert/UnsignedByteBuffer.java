package org.anchoranalysis.image.convert;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import org.anchoranalysis.image.convert.UnsignedByteBuffer;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Wraps a {@code ByteBuffer} but automatically performs conversion to {@code int}.
 * 
 * <p>The conversion applies to {@link ByteBuffer#get} and {@link ByteBuffer#put} of single
 * elements, but not to any mass get or put operations.
 * 
 * @author Owen Feehan
 *
 */
@AllArgsConstructor
public final class UnsignedByteBuffer {

    @Getter private final ByteBuffer delegate;
    
    public static UnsignedByteBuffer allocate(int capacity) {
        return new UnsignedByteBuffer( ByteBuffer.allocate(capacity) );
    }
    
    public static UnsignedByteBuffer wrap(byte[] array) {
        return new UnsignedByteBuffer( ByteBuffer.wrap(array) );
    }
    
    public byte getByte() {
        return delegate.get();
    }
    
    public byte getByte(int offset) {
        return delegate.get(offset);
    }
    
    public int getInt() {
        return PrimitiveConverter.unsignedByteToInt( getByte() );
    }
    
    public int getInt(int offset) {
        return PrimitiveConverter.unsignedByteToInt( getByte(offset) );
    }
     
    public byte get(int offset) {
        return delegate.get(offset);
    }
    
    public void put(byte b) {
        delegate.put(b);
    }
    
    public void put(int offset, byte b) {
        delegate.put(offset, b);
    }
    
    public void putByte(byte b) {
        delegate.put(b);
    }
    
    public void putByte(int offset, byte b) {
        delegate.put(offset, b);
    }
    
    public void putInt(int b) {
        putByte( (byte) b);
    }
    
    public void putInt(int offset, int b) {
        putByte( offset, (byte) b);
    }

    public final boolean hasRemaining() {
        return delegate.hasRemaining();
    }

    public final int position() {
        return delegate.position();
    }

    public final byte[] array() {
        return delegate.array();
    }

    public ByteBuffer put(ByteBuffer source) {
        return delegate.put(source);
    }
    
    public ByteBuffer put(UnsignedByteBuffer source) {
        return delegate.put(source.getDelegate());
    }

    public final int capacity() {
        return delegate.capacity();
    }

    public final Buffer clear() {
        return delegate.clear();
    }

    /**
     * Assigns a new position to the buffer.
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @param newPosition the offset to assign as position.
     */
    public final Buffer position(int newPosition) {
        return delegate.position(newPosition);
    }

    /**
     * Is this buffer direct or non-direct?
     * 
     * <p>This is meant in the sense of Java's NIO {@link Buffer} classes.
     * 
     * @return true iff the buffer is direct.
     */  
    public boolean isDirect() {
        return delegate.isDirect();
    }
}
