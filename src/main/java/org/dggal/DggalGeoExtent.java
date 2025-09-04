
package org.dggal;

import java.lang.foreign.AddressLayout;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SegmentAllocator;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DggalGeoExtent extends StructClass {

    private static final GroupLayout LAYOUT_LL;
    private static final GroupLayout LAYOUT_UR;

    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
       LAYOUT_LL = DggalGeoPoint.LAYOUT.withName("ll"),
       LAYOUT_UR = DggalGeoPoint.LAYOUT.withName("ur")
    ).withName("GeoExtent");

    public DggalGeoExtent(MemorySegment address) {
        super(address);
    }

    public DggalGeoExtent(SegmentAllocator allocator) {
        super(allocator);
    }

    @Override
    protected MemoryLayout getLayout() {
        return LAYOUT;
    }

    public DggalGeoPoint getLl() {
        return new DggalGeoPoint(struct.asSlice(0, DggalGeoPoint.LAYOUT.byteSize()));
    }


    public DggalGeoPoint getUr() {
        return new DggalGeoPoint(struct.asSlice(16, DggalGeoPoint.LAYOUT.byteSize()));
    }

    @Override
    public String toString() {
        return "GeoExtent(ll:" + getLl() + ", ur:" + getUr()+")";
    }

}
