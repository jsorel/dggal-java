package org.dggal;

import java.lang.foreign.MemorySegment;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Main {

    public static void main(String[] args) throws Throwable {

//        System.loadLibrary("ecrt");
//        System.loadLibrary("ecrt_c");
//        System.loadLibrary("dggal_c");
//        System.loadLibrary("dggal");
//        System.loadLibrary("dggal_c_fn");

//        System.load("/home/jsorel/dev/dggal/dgbuild/dggal/bindings/c_fn/obj/dggal.release.linux/libecrt.so");
//        System.load("/home/jsorel/dev/dggal/dgbuild/dggal/bindings/c_fn/obj/dggal.release.linux/libecrt_c.so");
//        System.load("/home/jsorel/dev/dggal/dgbuild/dggal/bindings/c_fn/obj/dggal.release.linux/libdggal_c.so");
//        final DGGAL dggal = DGGAL.load(Paths.get("/home/jsorel/dev/dggal/dgbuild/dggal/bindings/c_fn/obj/dggal.release.linux/libdggal_c_fn.so"));
        final DGGAL dggal = DGGAL.global();
//
        MemorySegment module = dggal.init();

        //todo a simple demo case
        final DggalDggrs dggrs = dggal.newDggrs(module, "ISEA3H");
        final long zid = dggrs.getZoneFromTextID("A2-0-A");
        final long childid = 18014398509481987l;
        System.out.println(dggrs);
        System.out.println("DGGAL_DGGRS_getZoneFromTextID : " + zid);
        System.out.println("DGGAL_DGGRS_getZoneLevel : " + dggrs.getZoneLevel(zid));
        System.out.println("DGGAL_DGGRS_countZoneEdges : " + dggrs.countZoneEdges(zid));
        System.out.println("DGGAL_DGGRS_getRefinementRatio : " + dggrs.getRefinementRatio());
        System.out.println("DGGAL_DGGRS_getMaxDGGRSZoneLevel : " + dggrs.getMaxDGGRSZoneLevel());
        System.out.println("DGGAL_DGGRS_getZoneWGS84Centroid : " + Arrays.toString(dggrs.getZoneWGS84Centroid(zid)));
        System.out.println("DGGAL_DGGRS_getZoneWGS84Vertices : " + Arrays.toString(dggrs.getZoneWGS84Vertices(zid)));
        System.out.println("DGGAL_DGGRS_getZoneArea : " + dggrs.getZoneArea(zid));
        System.out.println("DGGAL_DGGRS_countSubZones : " + dggrs.countSubZones(zid, 8));
        System.out.println("DGGAL_DGGRS_getZoneTextID : " + dggrs.getZoneTextID(zid));
        System.out.println("DGGAL_DGGRS_getZoneParents : " + Arrays.toString(dggrs.getZoneParents(zid)));
        System.out.println("DGGAL_DGGRS_getZoneChildren : " + Arrays.toString(dggrs.getZoneChildren(zid)));
        System.out.println("DGGAL_DGGRS_getZoneNeighbors : " + Arrays.toString(dggrs.getZoneNeighbors(zid)));
        System.out.println("DGGAL_DGGRS_getZoneCentroidParent : " + dggrs.getZoneCentroidParent(zid));
        System.out.println("DGGAL_DGGRS_getZoneCentroidChild : " + dggrs.getZoneCentroidChild(zid));
        System.out.println("DGGAL_DGGRS_getZoneWGS84Extent : " + Arrays.toString(dggrs.getZoneWGS84Extent(zid)));
        System.out.println("DGGAL_DGGRS_listZones : " + Arrays.toString(dggrs.listZones(1, new double[]{0,0,40,40})));
        System.out.println("DGGAL_DGGRS_getZoneRefinedWGS84Vertices : " + Arrays.toString(dggrs.getZoneRefinedWGS84Vertices(zid, 2)));
        System.out.println("DGGAL_DGGRS_getSubZones : " + Arrays.toString(dggrs.getSubZones(zid, 1)));
        System.out.println("DGGAL_DGGRS_getZoneFromWGS84Centroid : " + dggrs.getZoneFromWGS84Centroid(1, new double[]{45,10}));
        System.out.println("DGGAL_DGGRS_countZones : " + dggrs.countZones(1));
        System.out.println("DGGAL_DGGRS_getFirstSubZone : " + dggrs.getFirstSubZone(zid, 2));
        System.out.println("DGGAL_DGGRS_getIndexMaxDepth : " + dggrs.getIndexMaxDepth());
        System.out.println("DGGAL_DGGRS_getMaxChildren : " + dggrs.getMaxChildren());
        System.out.println("DGGAL_DGGRS_getMaxNeighbors : " + dggrs.getMaxNeighbors());
        System.out.println("DGGAL_DGGRS_getMaxParents : " + dggrs.getMaxParents());
        System.out.println("DGGAL_DGGRS_getSubZoneAtIndex : " + dggrs.getSubZoneAtIndex(zid, 1, 2));
        System.out.println("DGGAL_DGGRS_getSubZoneIndex : " + dggrs.getSubZoneIndex(zid, childid));
        System.out.println("DGGAL_DGGRS_getSubZoneCRSCentroids : " + Arrays.toString(dggrs.getSubZoneCRSCentroids(zid, 0, 1)));
        System.out.println("DGGAL_DGGRS_getSubZoneWGS84Centroids : " + Arrays.toString(dggrs.getSubZoneWGS84Centroids(zid, 1)));
        System.out.println("DGGAL_DGGRS_getZoneRefinedCRSVertices : " + Arrays.toString(dggrs.getZoneRefinedCRSVertices(zid, 0, 2)));
        System.out.println("DGGAL_DGGRS_getZoneCRSCentroid : " + Arrays.toString(dggrs.getZoneCRSCentroid(zid, 0)));
        System.out.println("DGGAL_DGGRS_getZoneCRSExtent : " + Arrays.toString(dggrs.getZoneCRSExtent(zid, 0)));
//        System.out.println("DGGAL_DGGRS_compactZones : " + Arrays.toString(dggrs.compactZones(new long[]{18014398509481985l, 18014398509481986l, 18014398509481987l, 9007199254740994l, 3l, 2l})));
        System.out.println("DGGAL_DGGRS_get64KDepth : " + dggrs.get64KDepth());
        System.out.println("DGGAL_DGGRS_getMaxDepth : " + dggrs.getMaxDepth());
        System.out.println("DGGAL_DGGRS_areZonesNeighbors : " + dggrs.areZonesNeighbors(zid, childid));
        System.out.println("DGGAL_DGGRS_areZonesSiblings : " + dggrs.areZonesSiblings(zid, childid));
        System.out.println("DGGAL_DGGRS_doZonesOverlap : " + dggrs.doZonesOverlap(zid, childid));
        System.out.println("DGGAL_DGGRS_doesZoneContain : " + dggrs.doesZoneContain(zid, childid));
        System.out.println("DGGAL_DGGRS_isZoneAncestorOf : " + dggrs.isZoneAncestorOf(zid, childid, 8));
        System.out.println("DGGAL_DGGRS_isZoneContainedIn : " + dggrs.isZoneContainedIn(zid, childid));
        System.out.println("DGGAL_DGGRS_isZoneDescendantOf : " + dggrs.isZoneDescendantOf(zid, childid, 8));
        System.out.println("DGGAL_DGGRS_isZoneImmediateChildOf : " + dggrs.isZoneImmediateChildOf(zid, childid));
        System.out.println("DGGAL_DGGRS_isZoneImmediateParentOf : " + dggrs.isZoneImmediateParentOf(zid, childid));
        System.out.println("DGGAL_DGGRS_zoneHasSubZone : " + dggrs.zoneHasSubZone(zid, childid));
        System.out.println("DGGAL_DGGRS_getLevelFromMetersPerSubZone : " + dggrs.getLevelFromMetersPerSubZone(100, 7));
        System.out.println("DGGAL_DGGRS_getLevelFromPixelsAndExtent : " + dggrs.getLevelFromPixelsAndExtent(new double[]{0,0,40,40}, 1024, 1024, 9));
        System.out.println("DGGAL_DGGRS_getLevelFromRefZoneArea : " + dggrs.getLevelFromRefZoneArea(150));
        System.out.println("DGGAL_DGGRS_getLevelFromScaleDenominator : " + dggrs.getLevelFromScaleDenominator(0.005, 7, 0.2));
        System.out.println("DGGAL_DGGRS_getMetersPerSubZoneFromLevel : " + dggrs.getMetersPerSubZoneFromLevel(7,3));
        System.out.println("DGGAL_DGGRS_getRefZoneArea : " + dggrs.getRefZoneArea(7));
        System.out.println("DGGAL_DGGRS_getScaleDenominatorFromLevel : " + dggrs.getScaleDenominatorFromLevel(7, 3, 0.2));

        dggrs.close();
        dggal.terminate(module);
    }

}
