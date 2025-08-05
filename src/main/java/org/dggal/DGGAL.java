
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

    private static final String LIBRARY_NAME = "dggal";

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
     * {@snippet lang=c : extern Module dggal_init(Module fromModule) }.
     */
    private final MethodHandle dggal_init;
    /**
     * {@snippet lang=c : ecrt_init(Module fromModule, bool loadEcere, bool guiApp, int argc, char *argv[]) }.
     */
    private final MethodHandle ecrt_init;

    /**
     * {@snippet lang=c : deletei(*pt) }.
     * which does not exist, it's a C define which points to __eCNameSpace__eC__types__eInstance_DecRef
     */
    private final MethodHandle deletei;

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

        dggal_init = lookup("dggal_init",                                 of(C_POINTER, C_POINTER));
        ecrt_init  = lookup("ecrt_init",                                  of(C_POINTER, C_POINTER, C_INT, C_INT, C_INT, C_POINTER));
        deletei    = lookup("__eCNameSpace__eC__types__eInstance_DecRef", ofVoid(C_POINTER));
    }

    // ///////////////////////////////////////
    // make method calls more friendly
    // ///////////////////////////////////////

    public MemorySegment dggal_init(MemorySegment app) throws Throwable {
        return (MemorySegment) dggal_init.invokeExact(app);
    }

    public MemorySegment ecrt_init(MemorySegment module, boolean loadEcere, boolean guiApp, int argc, MemorySegment argv) throws Throwable {
        if (module == null) module = NULL;
        if (argv == null) argv = NULL;
        return (MemorySegment) ecrt_init.invokeExact(module, loadEcere ? 1 : 0, guiApp ? 1 : 0, argc, argv);
    }

    public void deletei(MemorySegment inst) throws Throwable {
        deletei.invokeExact(inst);
    }
}
