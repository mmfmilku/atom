package org.mmfmilku.atom.transport.protocol.handle.assembly;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.handle.Codec;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;
import org.mmfmilku.atom.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AssemblyCodec implements Codec<List<TypeFrame>, AssemblyDataFrame> {

    @Override
    public AssemblyDataFrame code(List<TypeFrame> typeFrames) {
        // TODO 数据校验
        List<byte[]> dataList = new ArrayList<>();
        for (TypeFrame typeFrame : typeFrames) {
            byte[] data = typeFrame.getData();
            dataList.add(Arrays.copyOfRange(data, 4, data.length));
        }
        byte[] assembly = ArrayUtils.assembly(dataList.toArray(new byte[][]{}));
        return new AssemblyDataFrame(assembly);
    }

    @Override
    public List<TypeFrame> decode(AssemblyDataFrame assemblyDataFrame) {
        List<TypeFrame> typeFrames = new ArrayList<>();
        byte[] data = assemblyDataFrame.getData();
        int pieceTotal = Math.floorDiv(data.length, AssemblyDataFrame.MAX_PIECE_LENGTH) +
                (data.length % AssemblyDataFrame.MAX_PIECE_LENGTH == 0 ? 0 : 1);
        byte[] total = MessageUtils.codeInt(pieceTotal);
        int dataPoint = 0;
        for (int pieceNo = 1; pieceNo <= pieceTotal; pieceNo++) {
            int copyDataLen = Math.min(data.length - dataPoint, AssemblyDataFrame.MAX_PIECE_LENGTH);
            byte[] pieceData = new byte[copyDataLen + 4];
            // 总分片数
            pieceData[0] = total[0];
            pieceData[1] = total[1];
            // 分片编号
            byte[] noByte = MessageUtils.codeInt(pieceNo);
            pieceData[2] = noByte[0];
            pieceData[3] = noByte[1];
            // 数据
            System.arraycopy(data, dataPoint, pieceData, 4, copyDataLen);
            typeFrames.add(new TypeFrame(pieceData));
            dataPoint += copyDataLen;
        }
        return typeFrames;
    }
}
