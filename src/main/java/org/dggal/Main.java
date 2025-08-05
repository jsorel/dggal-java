package org.dggal;

import java.lang.foreign.MemorySegment;
import java.nio.file.Paths;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public class Main {

    public static void main(String[] args) throws Throwable {

        final DGGAL dggal = DGGAL.load(Paths.get("../path/to/libdggal_c.so"));

        MemorySegment app = dggal.ecrt_init(null, true, false, 0, null);
        MemorySegment mdggal = dggal.dggal_init(app);
        //todo a simple demo case


        dggal.deletei(app);
    }

}
