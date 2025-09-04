
package org.dggal;

import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalArrayZone implements AutoCloseable {

    private final DGGAL dggal;
    private final MemorySegment pointer;

    public DggalArrayZone(DGGAL dggal, MemorySegment pointer) {
        this.dggal = dggal;
        this.pointer = pointer;
    }

    public int getCount() throws Throwable {
        return (int) dggal.DGGAL_Array_DGGRSZone_getCount.invokeExact(pointer);
    }

    public long[] toArray() throws Throwable {
        final MemorySegment mem = (MemorySegment) dggal.DGGAL_Array_DGGRSZone_getPointer.invokeExact(pointer);
        final long[] dst = new long[getCount()];
        mem.reinterpret(dst.length*8).asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(dst);
        return dst;
    }

    @Override
    public void close() throws Exception {
        try {
            dggal.DGGAL_Array_DGGRSZone_delete.invokeExact(pointer);
        } catch (Throwable ex) {
            throw new Exception(ex);
        }
    }

}
