
package org.dggal;

import java.lang.foreign.MemorySegment;
import java.nio.ByteOrder;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalArrayPointd implements AutoCloseable {

    private final DGGAL dggal;
    private final MemorySegment pointer;

    public DggalArrayPointd(DGGAL dggal, MemorySegment pointer) {
        this.dggal = dggal;
        this.pointer = pointer;
    }

    public int getCount() throws Throwable {
        return (int) dggal.DGGAL_Array_Pointd_getCount.invokeExact(pointer);
    }

    public double[] toArray() throws Throwable {
        final MemorySegment mem = (MemorySegment) dggal.DGGAL_Array_Pointd_getPointer.invokeExact(pointer);
        final double[] dst = new double[getCount()*2];
        mem.reinterpret(dst.length*8).asByteBuffer().order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(dst);
        return dst;
    }

    @Override
    public void close() throws Exception {
        try {
            dggal.DGGAL_Array_Pointd_delete.invokeExact(pointer);
        } catch (Throwable ex) {
            throw new Exception(ex);
        }
    }

}
