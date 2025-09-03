package org.dggal;

import java.lang.foreign.MemorySegment;
import java.nio.file.Paths;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Main {

    public static void main(String[] args) throws Throwable {

        final DGGAL dggal = DGGAL.load(Paths.get("../libdggal_c_fn.so"));
        MemorySegment module = dggal.init();

        MemorySegment dggrs = dggal.newDggrs(module, "IVEA3H");
        dggal.deleteDggrs(dggrs);

        dggal.terminate(module);
    }

}
