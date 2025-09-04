
package org.dggal;

import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;
import java.lang.foreign.ValueLayout;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalPointd extends StructClass {

    private static final ValueLayout.OfDouble LAYOUT_X;
    private static final ValueLayout.OfDouble LAYOUT_Y;

    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
       LAYOUT_X = DGGAL.C_DOUBLE.withName("x"),
       LAYOUT_Y = DGGAL.C_DOUBLE.withName("y")
    ).withName("Pointd");

    public DggalPointd(MemorySegment address) {
        super(address);
    }

    public DggalPointd(SegmentAllocator allocator) {
        super(allocator);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    public double getX() {
        return struct.get(LAYOUT_X, 0);
    }

    public void setX(double value) {
        struct.set(LAYOUT_X, 0, value);
    }

    public double getY() {
        return struct.get(LAYOUT_Y, 0);
    }

    public void setY(double value) {
        struct.set(LAYOUT_Y, 0, value);
    }

}
