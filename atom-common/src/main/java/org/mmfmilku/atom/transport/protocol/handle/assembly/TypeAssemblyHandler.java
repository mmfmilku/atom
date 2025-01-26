package org.mmfmilku.atom.transport.protocol.handle.assembly;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.handle.PipeLine;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;
import org.mmfmilku.atom.util.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class TypeAssemblyHandler implements ServerHandle<TypeFrame, AssemblyDataFrame> {

//    @Override
//    public ChannelContext<AssemblyDataFrame> getChannelContext(ChannelContext<TypeFrame> channelContext) {
//        // 拆分数据
//        return assemblyDataFrame -> {
//            List<TypeFrame> typeFrames = decodeList(assemblyDataFrame);
//            for (TypeFrame typeFrame : typeFrames) {
//                channelContext.write(typeFrame);
//            }
//        };
//    }

    @Override
    public AssemblyDataFrame code(TypeFrame typeFrame) {
        throw new MessageCodeException("not support");
    }

    @Override
    public TypeFrame decode(AssemblyDataFrame assemblyDataFrame) {
        throw new MessageCodeException("not support");
    }

    private AssemblyDataFrame codeList(List<TypeFrame> typeFrames) {
        // TODO 数据校验
        List<byte[]> dataList = new ArrayList<>();
        for (TypeFrame typeFrame : typeFrames) {
            byte[] data = typeFrame.getData();
            dataList.add(Arrays.copyOfRange(data, 4, data.length));
        }
        byte[] assembly = ArrayUtils.assembly(dataList.toArray(new byte[][]{}));
        return new AssemblyDataFrame(assembly);
    }

    public List<TypeFrame> decodeList(AssemblyDataFrame assemblyDataFrame) {
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

    @Override
    public void handle(TypeFrame typeFrame, PipeLine pipeLine) {

        byte[] data = typeFrame.getData();
        int total = MessageUtils.decodeInt(Arrays.copyOfRange(data, 0, 2));
        List<TypeFrame> typeFrames = new ArrayList<>();
        typeFrames.add(typeFrame);
        for (int i = 2; i <= total; i++) {
            // TODO 获取第二分片及之后的数据

        }
        AssemblyDataFrame assemblyDataFrame = codeList(typeFrames);
        pipeLine.handleNext(assemblyDataFrame);
    }

}
