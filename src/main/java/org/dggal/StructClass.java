package org.dggal;


import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/**
 * Panama can also map C struct if needed.
 *
 * @author Johann Sorel (Geomatys)
 */
abstract class StructClass {

    protected final MemorySegment struct;

    protected StructClass(MemorySegment struct) {
        this.struct = struct;
    }

    protected StructClass(SegmentAllocator allocator) {
        this.struct = allocator.allocate(getLayout());
    }

    protected abstract MemoryLayout getLayout();

}
