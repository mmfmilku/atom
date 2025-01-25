package org.mmfmilku.atom.transport.protocol;

import org.mmfmilku.atom.transport.protocol.base.FServer;
import org.mmfmilku.atom.transport.protocol.handle.string.StringHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeHandler;

public class FServers {

    public static FServer newStringFServer(String fDir) {
        return new FServer(fDir)
                .addHandle(new TypeHandler())
                .addHandle(new StringHandle());
    }

}
