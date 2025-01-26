package org.mmfmilku.atom.transport.protocol.handle.assembly;

public class AssemblyDataFrame {

    public static final int MAX_PIECE_LENGTH = 65530;

    private byte[] data;

    public AssemblyDataFrame(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
