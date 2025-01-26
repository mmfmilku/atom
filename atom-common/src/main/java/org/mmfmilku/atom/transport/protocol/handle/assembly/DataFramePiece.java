package org.mmfmilku.atom.transport.protocol.handle.assembly;

public class DataFramePiece {

    public static final int MAX_PIECE_LENGTH = 65530;

    private int pieceNo;
    private int pieceTotal;
    private int currPieceLen;
    private byte[] pieceData;

}
