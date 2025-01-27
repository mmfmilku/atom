package org.mmfmilku.atom.transport.protocol.handle.assembly;

import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

public class AssemblyDataFrame extends TypeFrame {

    public static final int MAX_PIECE_LENGTH = 65530;

    public AssemblyDataFrame(byte[] data) {
        super(data);
    }

}
