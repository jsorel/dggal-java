
package org.dggal;

import java.io.File;
import java.lang.foreign.AddressLayout;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import static java.lang.foreign.FunctionDescriptor.of;
import static java.lang.foreign.FunctionDescriptor.ofVoid;
import java.lang.foreign.Linker;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SymbolLookup;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * Binding for library DGGAL.
 * https://dggal.org/
 *
 * @author Johann Sorel (Geomatys)
 */
public final class DGGAL implements AutoCloseable {

    private static final String LIBRARY_NAME = "dggal_c_fn";

    // Common C mappings
    public static final ValueLayout.OfBoolean C_BOOL      = ValueLayout.JAVA_BOOLEAN;
    public static final ValueLayout.OfByte    C_CHAR      = ValueLayout.JAVA_BYTE;
    public static final ValueLayout.OfShort   C_SHORT     = ValueLayout.JAVA_SHORT;
    public static final ValueLayout.OfInt     C_INT       = ValueLayout.JAVA_INT;
    public static final ValueLayout.OfLong    C_LONG_LONG = ValueLayout.JAVA_LONG;
    public static final ValueLayout.OfFloat   C_FLOAT     = ValueLayout.JAVA_FLOAT;
    public static final ValueLayout.OfDouble  C_DOUBLE    = ValueLayout.JAVA_DOUBLE;
    public static final AddressLayout         C_POINTER   = ValueLayout.ADDRESS.withTargetLayout(MemoryLayout.sequenceLayout(java.lang.Long.MAX_VALUE, ValueLayout.JAVA_BYTE));
    public static final ValueLayout.OfLong    C_LONG      = ValueLayout.JAVA_LONG;
    public static final MemorySegment         NULL        = MemorySegment.ofAddress(0L);
    public static final int                   EOF         = -1;

    /**
     * The global instance, created when first needed.
     * This field shall be read and updated in a synchronized block.
     */
    private static DGGAL global;
    /**
     * Whether an error occurred during initialization of {@link #global}.
     * Shall be read and updated in the same synchronization block as {@link #global}.
     */
    private static String globalStatus;


    private final Arena arena;
    /**
     * The lookup for retrieving the address of a symbol in the native library.
     */
    private final SymbolLookup symbols;
    /**
     * The linker to use for fetching method handles from the {@linkplain #symbols}.
     * In current version, this is always {@link Linker#nativeLinker()}.
     */
    private final Linker linker;

    /**
     * Search for a native function.
     *
     * @param name native function name
     * @param desc native function arguments and return description
     * @return method handle for the ANARI function.
     * @throws IllegalArgumentException if function has not been found
     */
    private MethodHandle lookup(final String name, final FunctionDescriptor desc) {
        return symbols.find(name).map((method) -> linker.downcallHandle(method, desc)).orElseThrow();
    }

    @Override
    public void close() throws Exception {
        if (arena != null) {
            arena.close();
        }
    }

    /**
     * Loads the native library from the given file path.
     *
     * @param library library path to load.
     * @return java binding
     * @throws IllegalArgumentException if the native library has not been found.
     * @throws NoSuchElementException if a native function has not been found in the library.
     * @throws IllegalCallerException if this module is not authorized to call native methods.
     */
    public static DGGAL load(final Path library) {
        final Arena arena = Arena.ofShared();
        final DGGAL anari;
        try {
            final SymbolLookup symbols = SymbolLookup.libraryLookup(library, arena);
            anari = new DGGAL(arena, symbols);
        } catch (Throwable e) {
            arena.close();
            throw e;
        }
        return anari;
    }

    /**
     * Loads the native library from the default library path.
     * The returned instance is valid for JVM lifetime.
     * This method returns a singleton.
     *
     * <p>If the native library is not found, the current default is {@link SymbolLookup#loaderLookup()}
     * for allowing users to invoke {@link System#loadLibrary(String)} as a fallback.</p>
     *
     * @return java binding
     * @throws IllegalStateException if the native library could not be loaded.
     */
    public static synchronized DGGAL global() throws IllegalStateException {
        if (globalStatus == null) {
            final String library = LIBRARY_NAME;
            String status = "Library not found";
            RuntimeException error = null;
            try {
                String filename = (File.separatorChar == '\\') ? (library + ".dll") : ("lib" + library + ".so");
                DGGAL instance  = null;
                SymbolLookup symbols;
                create: try {
                    try {
                        symbols = SymbolLookup.libraryLookup(filename, Arena.global());
                    } catch (IllegalArgumentException e) {
                        error    = e;
                        filename = "system";
                        symbols  = SymbolLookup.loaderLookup(); // In case user called `System.loadLibrary(…)`.
                        error = null;
                    }
                    try {
                        instance = new DGGAL(null, symbols);
                        status   = "";
                    } catch (RuntimeException e) {
                        if (error != null) {
                            error.addSuppressed(e);
                        } else {
                            error = e;
                        }
                        break create;
                    }
                } catch (IllegalCallerException e) {
                    error  = e;
                    status = "Native access not allowed";
                } catch (NoSuchElementException e) {
                    error  = e;
                    status = "Function not found";
                }
                global = instance;
            } finally {
                globalStatus = status;
            }
            report(error);
        }
        report(null);
        return global;
    }

    /**
     * Throws an exception if the native library is not available.
     *
     * @param cause the cause of the error, or {@code null} if none.
     * @throws IllegalStateException if the status is not an empty string or if the given cause is not null.
     */
    private static void report(Exception cause) throws IllegalStateException {
        if (!globalStatus.isEmpty() || cause != null) {
            // Note: `NativeAccessNotAllowed` will ignore the `library` argument.
            String text = globalStatus;
            if (cause != null) {
                throw new IllegalStateException(LIBRARY_NAME + ": " + text, cause);
            } else {
                throw new IllegalStateException(LIBRARY_NAME + ": " + text);
            }
        }
    }


    // ////////////////////////////////////////////////////////////////////////
    // GENERATED BY BGEN //////////////////////////////////////////////////////
    // ////////////////////////////////////////////////////////////////////////

    // //////////////////////////////
    // List the native functions here
    // //////////////////////////////

    /**
     * {@snippet lang=c : DGGALModule DGGAL_init(); }.
     */
    private final MethodHandle DGGAL_init;
    /**
     * {@snippet lang=c : void DGGAL_terminate(DGGALModule mDGGAL); }.
     */
    private final MethodHandle DGGAL_terminate;
    /**
     * {@snippet lang=c : DGGRS DGGAL_DGGRS_new(DGGALModule mDGGAL, constString name); }.
     */
    final MethodHandle DGGAL_DGGRS_new;
    /**
     * {@snippet lang=c : void DGGAL_DGGRS_delete(DGGRS self); }.
     */
    final MethodHandle DGGAL_DGGRS_delete;

    /**
     * DGGRSZone DGGAL_DGGRS_getZoneFromTextID(const DGGRS self, constString zoneID);
     */
    final MethodHandle DGGAL_DGGRS_getZoneFromTextID;
    /**
     * int DGGAL_DGGRS_getZoneLevel(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_getZoneLevel;
    /**
     * int DGGAL_DGGRS_countZoneEdges(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_countZoneEdges;
    /**
     * int DGGAL_DGGRS_getRefinementRatio(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getRefinementRatio;
    /**
     * int DGGAL_DGGRS_getMaxDGGRSZoneLevel(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getMaxDGGRSZoneLevel;
    /**
     * void DGGAL_DGGRS_getZoneWGS84Centroid(const DGGRS self, DGGRSZone zone, GeoPoint * outCentroid);
     */
    final MethodHandle DGGAL_DGGRS_getZoneWGS84Centroid;
    /**
     * int DGGAL_DGGRS_getZoneWGS84Vertices(const DGGRS self, DGGRSZone zone, GeoPoint * outVertices);
     */
    final MethodHandle DGGAL_DGGRS_getZoneWGS84Vertices;
    /**
     * double DGGAL_DGGRS_getZoneArea(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_getZoneArea;
    /**
     * uint64_t DGGAL_DGGRS_countSubZones(const DGGRS self, DGGRSZone zone, int depth);
     */
    final MethodHandle DGGAL_DGGRS_countSubZones;
    /**
     * void DGGAL_DGGRS_getZoneTextID(const DGGRS self, DGGRSZone zone, char outId[256]);
     */
    final MethodHandle DGGAL_DGGRS_getZoneTextID;
    /**
     * int DGGAL_DGGRS_getZoneParents(const DGGRS self, DGGRSZone zone, DGGRSZone outParents[3]);
     */
    final MethodHandle DGGAL_DGGRS_getZoneParents;
    /**
     * int DGGAL_DGGRS_getZoneChildren(const DGGRS self, DGGRSZone zone, DGGRSZone outChildren[13]);
     */
    final MethodHandle DGGAL_DGGRS_getZoneChildren;
    /**
     * int DGGAL_DGGRS_getZoneNeighbors(const DGGRS self, DGGRSZone zone, DGGRSZone outNeighbors[6], int outNbTypes[6]);
     */
    final MethodHandle DGGAL_DGGRS_getZoneNeighbors;
    /**
     * DGGRSZone DGGAL_DGGRS_getZoneCentroidParent(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_getZoneCentroidParent;
    /**
     * DGGRSZone DGGAL_DGGRS_getZoneCentroidChild(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_getZoneCentroidChild;
    /**
     * int DGGAL_DGGRS_isZoneCentroidChild(const DGGRS self, DGGRSZone zone);
     */
    final MethodHandle DGGAL_DGGRS_isZoneCentroidChild;
    /**
     * void DGGAL_DGGRS_getZoneWGS84Extent(const DGGRS self, DGGRSZone zone, GeoExtent * outExtent);
     */
    final MethodHandle DGGAL_DGGRS_getZoneWGS84Extent;
    /**
     * Array_DGGRSZone DGGAL_DGGRS_listZones(const DGGRS self, int level, const GeoExtent * bbox);
     */
    final MethodHandle DGGAL_DGGRS_listZones;
    /**
     * Array_GeoPoint DGGAL_DGGRS_getZoneRefinedWGS84Vertices(const DGGRS self, DGGRSZone zone, int refinement);
     */
    final MethodHandle DGGAL_DGGRS_getZoneRefinedWGS84Vertices;
    /**
     * Array_DGGRSZone DGGAL_DGGRS_getSubZones(const DGGRS self, DGGRSZone zone, int depth);
     */
    final MethodHandle DGGAL_DGGRS_getSubZones;
    /**
     * DGGRSZone DGGAL_DGGRS_getZoneFromWGS84Centroid(const DGGRS self, int level, const GeoPoint * point);
     */
    final MethodHandle DGGAL_DGGRS_getZoneFromWGS84Centroid;
    /**
     * uint64_t DGGAL_DGGRS_countZones(const DGGRS self, int level);
     */
    final MethodHandle DGGAL_DGGRS_countZones;
    /**
     * DGGRSZone DGGAL_DGGRS_getFirstSubZone(const DGGRS self, DGGRSZone parent, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getFirstSubZone;
    /**
     * int DGGAL_DGGRS_getIndexMaxDepth(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getIndexMaxDepth;
    /**
     * int DGGAL_DGGRS_getMaxChildren(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getMaxChildren;
    /**
     * int DGGAL_DGGRS_getMaxNeighbors(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getMaxNeighbors;
    /**
     * int DGGAL_DGGRS_getMaxParents(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getMaxParents;
    /**
     * DGGRSZone DGGAL_DGGRS_getSubZoneAtIndex(const DGGRS self, DGGRSZone parent, int relativeDepth, int64_t index);
     */
    final MethodHandle DGGAL_DGGRS_getSubZoneAtIndex;
    /**
     * int64_t DGGAL_DGGRS_getSubZoneIndex(const DGGRS self, DGGRSZone parent, DGGRSZone subZone);
     */
    final MethodHandle DGGAL_DGGRS_getSubZoneIndex;
    /**
     * Array_Pointd DGGAL_DGGRS_getSubZoneCRSCentroids(const DGGRS self, DGGRSZone parent, CRS crs, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getSubZoneCRSCentroids;
    /**
     * Array_GeoPoint DGGAL_DGGRS_getSubZoneWGS84Centroids(const DGGRS self, DGGRSZone parent, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getSubZoneWGS84Centroids;
    /**
     * Array_Pointd DGGAL_DGGRS_getZoneRefinedCRSVertices(const DGGRS self, DGGRSZone zone, CRS crs, int refinement);
     */
    final MethodHandle DGGAL_DGGRS_getZoneRefinedCRSVertices;
    /**
     * void DGGAL_DGGRS_getZoneCRSCentroid(const DGGRS self, DGGRSZone zone, CRS crs, Pointd * outCentroid);
     */
    final MethodHandle DGGAL_DGGRS_getZoneCRSCentroid;
    /**
     * void DGGAL_DGGRS_getZoneCRSExtent(const DGGRS self, DGGRSZone zone, CRS crs, CRSExtent * outExtent)
     */
    final MethodHandle DGGAL_DGGRS_getZoneCRSExtent;
    /**
     * void DGGAL_DGGRS_compactZones(const DGGRS self, Array_DGGRSZone zones);
     */
    final MethodHandle DGGAL_DGGRS_compactZones;
    /**
     * int DGGAL_DGGRS_get64KDepth(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_get64KDepth;
    /**
     * int DGGAL_DGGRS_getMaxDepth(const DGGRS self);
     */
    final MethodHandle DGGAL_DGGRS_getMaxDepth;
    /**
     * int DGGAL_DGGRS_areZonesNeighbors(const DGGRS self, DGGRSZone a, DGGRSZone b);
     */
    final MethodHandle DGGAL_DGGRS_areZonesNeighbors;
    /**
     * int DGGAL_DGGRS_areZonesSiblings(const DGGRS self, DGGRSZone a, DGGRSZone b);
     */
    final MethodHandle DGGAL_DGGRS_areZonesSiblings;
    /**
     * int DGGAL_DGGRS_doZonesOverlap(const DGGRS self, DGGRSZone a, DGGRSZone b);
     */
    final MethodHandle DGGAL_DGGRS_doZonesOverlap;
    /**
     * int DGGAL_DGGRS_doesZoneContain(const DGGRS self, DGGRSZone hayStack, DGGRSZone needle);
     */
    final MethodHandle DGGAL_DGGRS_doesZoneContain;
    /**
     * int DGGAL_DGGRS_isZoneAncestorOf(const DGGRS self, DGGRSZone ancestor, DGGRSZone descendant, int maxDepth);
     */
    final MethodHandle DGGAL_DGGRS_isZoneAncestorOf;
    /**
     * int DGGAL_DGGRS_isZoneContainedIn(const DGGRS self, DGGRSZone needle, DGGRSZone hayStack);
     */
    final MethodHandle DGGAL_DGGRS_isZoneContainedIn;
    /**
     * int DGGAL_DGGRS_isZoneDescendantOf(const DGGRS self, DGGRSZone descendant, DGGRSZone ancestor, int maxDepth);
     */
    final MethodHandle DGGAL_DGGRS_isZoneDescendantOf;
    /**
     * int DGGAL_DGGRS_isZoneImmediateChildOf(const DGGRS self, DGGRSZone child, DGGRSZone parent);
     */
    final MethodHandle DGGAL_DGGRS_isZoneImmediateChildOf;
    /**
     * int DGGAL_DGGRS_isZoneImmediateParentOf(const DGGRS self, DGGRSZone parent, DGGRSZone child);
     */
    final MethodHandle DGGAL_DGGRS_isZoneImmediateParentOf;
    /**
     * int DGGAL_DGGRS_zoneHasSubZone(const DGGRS self, DGGRSZone hayStack, DGGRSZone needle);
     */
    final MethodHandle DGGAL_DGGRS_zoneHasSubZone;
    /**
     * int DGGAL_DGGRS_getLevelFromMetersPerSubZone(const DGGRS self, double physicalMetersPerSubZone, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getLevelFromMetersPerSubZone;
    /**
     * int DGGAL_DGGRS_getLevelFromPixelsAndExtent(const DGGRS self, const GeoExtent * extent, int width, int height, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getLevelFromPixelsAndExtent;
    /**
     * int DGGAL_DGGRS_getLevelFromRefZoneArea(const DGGRS self, double metersSquared);
     */
    final MethodHandle DGGAL_DGGRS_getLevelFromRefZoneArea;
    /**
     * int DGGAL_DGGRS_getLevelFromScaleDenominator(const DGGRS self, double scaleDenominator, int relativeDepth, double mmPerPixel);
     */
    final MethodHandle DGGAL_DGGRS_getLevelFromScaleDenominator;
    /**
     * double DGGAL_DGGRS_getMetersPerSubZoneFromLevel(const DGGRS self, int parentLevel, int relativeDepth);
     */
    final MethodHandle DGGAL_DGGRS_getMetersPerSubZoneFromLevel;
    /**
     * double DGGAL_DGGRS_getRefZoneArea(const DGGRS self, int level);
     */
    final MethodHandle DGGAL_DGGRS_getRefZoneArea;
    /**
     * double DGGAL_DGGRS_getScaleDenominatorFromLevel(const DGGRS self, int parentLevel, int relativeDepth, double mmPerPixel);
     */
    final MethodHandle DGGAL_DGGRS_getScaleDenominatorFromLevel;

    /**
     * int DGGAL_Array_GeoPoint_getCount(const Array_GeoPoint self);
     */
    final MethodHandle DGGAL_Array_GeoPoint_getCount;
    /**
     * const GeoPoint * DGGAL_Array_GeoPoint_getPointer(const Array_GeoPoint self);
     */
    final MethodHandle DGGAL_Array_GeoPoint_getPointer;
    /**
     * void DGGAL_Array_GeoPoint_delete(Array_GeoPoint self);
     */
    final MethodHandle DGGAL_Array_GeoPoint_delete;

    /**
     * int DGGAL_Array_Pointd_getCount(const Array_Pointd self);
     */
    final MethodHandle DGGAL_Array_Pointd_getCount;
    /**
     * const Pointd * DGGAL_Array_Pointd_getPointer(const Array_Pointd self);
     */
    final MethodHandle DGGAL_Array_Pointd_getPointer;
    /**
     * void DGGAL_Array_Pointd_delete(Array_Pointd self);
     */
    final MethodHandle DGGAL_Array_Pointd_delete;

    /**
     * int DGGAL_Array_DGGRSZone_getCount(const Array_DGGRSZone self);
     */
    final MethodHandle DGGAL_Array_DGGRSZone_getCount;
    /**
     * const DGGRSZone * DGGAL_Array_DGGRSZone_getPointer(const Array_DGGRSZone self);
     */
    final MethodHandle DGGAL_Array_DGGRSZone_getPointer;
    /**
     * void DGGAL_Array_DGGRSZone_delete(Array_DGGRSZone self);
     */
    final MethodHandle DGGAL_Array_DGGRSZone_delete;


    /**
     * Creates the handles for all <abbr>DGGAL</abbr> functions which will be needed.
     *
     * @param  loader  the object used for loading the library.
     * @throws NoSuchElementException if an <abbr>DGGAL</abbr> function has not been found in the library.
     */
    private DGGAL(Arena arena, SymbolLookup symbols) {
        this.arena = arena;
        this.symbols = symbols;
        this.linker = Linker.nativeLinker();

        // ///////////////////////////////////////
        // find every method in the native library
        // ///////////////////////////////////////

        DGGAL_init          = lookup("DGGAL_init",                                 of(C_POINTER));
        DGGAL_terminate     = lookup("DGGAL_terminate",                            ofVoid(C_POINTER));

        DGGAL_DGGRS_new     = lookup("DGGAL_DGGRS_new",                            of(C_POINTER, C_POINTER, C_POINTER));
        DGGAL_DGGRS_delete  = lookup("DGGAL_DGGRS_delete",                         ofVoid(C_POINTER));

        DGGAL_DGGRS_getZoneFromTextID               = lookup("DGGAL_DGGRS_getZoneFromTextID",           of(C_LONG, C_POINTER, C_POINTER));
        DGGAL_DGGRS_getZoneLevel                    = lookup("DGGAL_DGGRS_getZoneLevel",                of(C_INT, C_POINTER, C_LONG));
        DGGAL_DGGRS_countZoneEdges                  = lookup("DGGAL_DGGRS_countZoneEdges",              of(C_INT, C_POINTER, C_LONG));
        DGGAL_DGGRS_getRefinementRatio              = lookup("DGGAL_DGGRS_getRefinementRatio",          of(C_INT, C_POINTER));
        DGGAL_DGGRS_getMaxDGGRSZoneLevel            = lookup("DGGAL_DGGRS_getMaxDGGRSZoneLevel",        of(C_INT, C_POINTER));
        DGGAL_DGGRS_getZoneWGS84Centroid            = lookup("DGGAL_DGGRS_getZoneWGS84Centroid",        ofVoid(C_POINTER, C_LONG, C_POINTER));
        DGGAL_DGGRS_getZoneWGS84Vertices            = lookup("DGGAL_DGGRS_getZoneWGS84Vertices",        of(C_INT, C_POINTER, C_LONG, C_POINTER));
        DGGAL_DGGRS_getZoneArea                     = lookup("DGGAL_DGGRS_getZoneArea",                 of(C_DOUBLE, C_POINTER, C_LONG));
        DGGAL_DGGRS_countSubZones                   = lookup("DGGAL_DGGRS_countSubZones",               of(C_LONG, C_POINTER, C_LONG, C_INT));
        DGGAL_DGGRS_getZoneTextID                   = lookup("DGGAL_DGGRS_getZoneTextID",               ofVoid(C_POINTER, C_LONG, C_POINTER));
        DGGAL_DGGRS_getZoneParents                  = lookup("DGGAL_DGGRS_getZoneParents",              of(C_INT, C_POINTER, C_LONG , C_POINTER));
        DGGAL_DGGRS_getZoneChildren                 = lookup("DGGAL_DGGRS_getZoneChildren",             of(C_INT, C_POINTER, C_LONG, C_POINTER));
        DGGAL_DGGRS_getZoneNeighbors                = lookup("DGGAL_DGGRS_getZoneNeighbors",            of(C_INT, C_POINTER, C_LONG, C_POINTER, C_POINTER));
        DGGAL_DGGRS_getZoneCentroidParent           = lookup("DGGAL_DGGRS_getZoneCentroidParent",       of(C_LONG, C_POINTER, C_LONG));
        DGGAL_DGGRS_getZoneCentroidChild            = lookup("DGGAL_DGGRS_getZoneCentroidChild",        of(C_LONG, C_POINTER, C_LONG));
        DGGAL_DGGRS_isZoneCentroidChild             = lookup("DGGAL_DGGRS_isZoneCentroidChild",         of(C_INT, C_POINTER, C_LONG));
        DGGAL_DGGRS_getZoneWGS84Extent              = lookup("DGGAL_DGGRS_getZoneWGS84Extent",          ofVoid(C_POINTER, C_LONG, C_POINTER));
        DGGAL_DGGRS_listZones                       = lookup("DGGAL_DGGRS_listZones",                   of(C_POINTER, C_POINTER, C_INT, C_POINTER));
        DGGAL_DGGRS_getZoneRefinedWGS84Vertices     = lookup("DGGAL_DGGRS_getZoneRefinedWGS84Vertices", of(C_POINTER, C_POINTER, C_LONG, C_INT));
        DGGAL_DGGRS_getSubZones                     = lookup("DGGAL_DGGRS_getSubZones",                 of(C_POINTER, C_POINTER, C_LONG, C_INT));
        DGGAL_DGGRS_getZoneFromWGS84Centroid        = lookup("DGGAL_DGGRS_getZoneFromWGS84Centroid",    of(C_LONG, C_POINTER, C_INT, C_POINTER));
        DGGAL_DGGRS_countZones                      = lookup("DGGAL_DGGRS_countZones",                  of(C_LONG, C_POINTER, C_INT));
        DGGAL_DGGRS_getFirstSubZone                 = lookup("DGGAL_DGGRS_getFirstSubZone",             of(C_LONG, C_POINTER, C_LONG, C_INT));
        DGGAL_DGGRS_getIndexMaxDepth                = lookup("DGGAL_DGGRS_getIndexMaxDepth",            of(C_INT, C_POINTER));
        DGGAL_DGGRS_getMaxChildren                  = lookup("DGGAL_DGGRS_getMaxChildren",              of(C_INT, C_POINTER));
        DGGAL_DGGRS_getMaxNeighbors                 = lookup("DGGAL_DGGRS_getMaxNeighbors",             of(C_INT, C_POINTER));
        DGGAL_DGGRS_getMaxParents                   = lookup("DGGAL_DGGRS_getMaxParents",               of(C_INT, C_POINTER));
        DGGAL_DGGRS_getSubZoneAtIndex               = lookup("DGGAL_DGGRS_getSubZoneAtIndex",           of(C_LONG, C_POINTER, C_LONG, C_INT, C_LONG));
        DGGAL_DGGRS_getSubZoneIndex                 = lookup("DGGAL_DGGRS_getSubZoneIndex",             of(C_LONG, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_getSubZoneCRSCentroids          = lookup("DGGAL_DGGRS_getSubZoneCRSCentroids",      of(C_POINTER, C_POINTER, C_LONG, C_LONG, C_INT));
        DGGAL_DGGRS_getSubZoneWGS84Centroids        = lookup("DGGAL_DGGRS_getSubZoneWGS84Centroids",    of(C_POINTER, C_POINTER, C_LONG, C_INT));
        DGGAL_DGGRS_getZoneRefinedCRSVertices       = lookup("DGGAL_DGGRS_getZoneRefinedCRSVertices",   of(C_POINTER, C_POINTER, C_LONG, C_LONG, C_INT));
        DGGAL_DGGRS_getZoneCRSCentroid              = lookup("DGGAL_DGGRS_getZoneCRSCentroid",          ofVoid(C_POINTER, C_LONG, C_LONG, C_POINTER));
        DGGAL_DGGRS_getZoneCRSExtent                = lookup("DGGAL_DGGRS_getZoneCRSExtent",            ofVoid(C_POINTER, C_LONG, C_LONG, C_POINTER));
        DGGAL_DGGRS_compactZones                    = lookup("DGGAL_DGGRS_compactZones",                ofVoid(C_POINTER, C_POINTER));
        DGGAL_DGGRS_get64KDepth                     = lookup("DGGAL_DGGRS_get64KDepth",                 of(C_INT, C_POINTER));
        DGGAL_DGGRS_getMaxDepth                     = lookup("DGGAL_DGGRS_getMaxDepth",                 of(C_INT, C_POINTER));
        DGGAL_DGGRS_areZonesNeighbors               = lookup("DGGAL_DGGRS_areZonesNeighbors",           of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_areZonesSiblings                = lookup("DGGAL_DGGRS_areZonesSiblings",            of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_doZonesOverlap                  = lookup("DGGAL_DGGRS_doZonesOverlap",              of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_doesZoneContain                 = lookup("DGGAL_DGGRS_doesZoneContain",             of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_isZoneAncestorOf                = lookup("DGGAL_DGGRS_isZoneAncestorOf",            of(C_INT, C_POINTER, C_LONG, C_LONG, C_INT));
        DGGAL_DGGRS_isZoneContainedIn               = lookup("DGGAL_DGGRS_isZoneContainedIn",           of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_isZoneDescendantOf              = lookup("DGGAL_DGGRS_isZoneDescendantOf",          of(C_INT, C_POINTER, C_LONG, C_LONG, C_INT));
        DGGAL_DGGRS_isZoneImmediateChildOf          = lookup("DGGAL_DGGRS_isZoneImmediateChildOf",      of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_isZoneImmediateParentOf         = lookup("DGGAL_DGGRS_isZoneImmediateParentOf",     of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_zoneHasSubZone                  = lookup("DGGAL_DGGRS_zoneHasSubZone",              of(C_INT, C_POINTER, C_LONG, C_LONG));
        DGGAL_DGGRS_getLevelFromMetersPerSubZone    = lookup("DGGAL_DGGRS_getLevelFromMetersPerSubZone",of(C_INT, C_POINTER, C_DOUBLE, C_INT));
        DGGAL_DGGRS_getLevelFromPixelsAndExtent     = lookup("DGGAL_DGGRS_getLevelFromPixelsAndExtent", of(C_INT, C_POINTER, C_POINTER, C_INT, C_INT, C_INT));
        DGGAL_DGGRS_getLevelFromRefZoneArea         = lookup("DGGAL_DGGRS_getLevelFromRefZoneArea",     of(C_INT, C_POINTER, C_DOUBLE));
        DGGAL_DGGRS_getLevelFromScaleDenominator    = lookup("DGGAL_DGGRS_getLevelFromScaleDenominator",of(C_INT, C_POINTER, C_DOUBLE, C_INT, C_DOUBLE));
        DGGAL_DGGRS_getMetersPerSubZoneFromLevel    = lookup("DGGAL_DGGRS_getMetersPerSubZoneFromLevel",of(C_DOUBLE, C_POINTER, C_INT, C_INT));
        DGGAL_DGGRS_getRefZoneArea                  = lookup("DGGAL_DGGRS_getRefZoneArea",              of(C_DOUBLE, C_POINTER, C_INT));
        DGGAL_DGGRS_getScaleDenominatorFromLevel    = lookup("DGGAL_DGGRS_getScaleDenominatorFromLevel",of(C_DOUBLE, C_POINTER, C_INT, C_INT, C_DOUBLE));


        DGGAL_Array_GeoPoint_getCount               = lookup("DGGAL_Array_GeoPoint_getCount", of(C_INT, C_POINTER));
        DGGAL_Array_GeoPoint_getPointer             = lookup("DGGAL_Array_GeoPoint_getPointer", of(C_POINTER, C_POINTER));
        DGGAL_Array_GeoPoint_delete                 = lookup("DGGAL_Array_GeoPoint_delete", ofVoid(C_POINTER));

        DGGAL_Array_Pointd_getCount                 = lookup("DGGAL_Array_Pointd_getCount", of(C_INT, C_POINTER));
        DGGAL_Array_Pointd_getPointer               = lookup("DGGAL_Array_Pointd_getPointer", of(C_POINTER, C_POINTER));
        DGGAL_Array_Pointd_delete                   = lookup("DGGAL_Array_Pointd_delete", ofVoid(C_POINTER));

        DGGAL_Array_DGGRSZone_getCount              = lookup("DGGAL_Array_DGGRSZone_getCount", of(C_INT, C_POINTER));
        DGGAL_Array_DGGRSZone_getPointer            = lookup("DGGAL_Array_DGGRSZone_getPointer", of(C_POINTER, C_POINTER));
        DGGAL_Array_DGGRSZone_delete                = lookup("DGGAL_Array_DGGRSZone_delete", ofVoid(C_POINTER));
    }

    // ///////////////////////////////////////
    // make method calls more friendly
    // ///////////////////////////////////////

    public MemorySegment init() throws Throwable {
        return (MemorySegment) DGGAL_init.invokeExact();
    }

    public void terminate(MemorySegment module) throws Throwable {
        DGGAL_terminate.invokeExact(module);
    }

    public DggalDggrs newDggrs(MemorySegment module, String name) throws Throwable {
        try (Arena tempArena = Arena.ofConfined()){
            final MemorySegment pointer = (MemorySegment) DGGAL_DGGRS_new.invokeExact(module, tempArena.allocateFrom(name));
            if (pointer.equals(NULL)) throw new IllegalArgumentException("Unknown dggrs " + name);
            return new DggalDggrs(global, pointer, name);
        }
    }

}
