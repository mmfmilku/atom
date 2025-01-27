package org.mmfmilku.atom.transport.protocol.handle.assembly;

import org.mmfmilku.atom.transport.protocol.MessageUtils;
import org.mmfmilku.atom.transport.protocol.exception.MessageCodeException;
import org.mmfmilku.atom.transport.protocol.handle.Codec;
import org.mmfmilku.atom.transport.protocol.handle.PipeLine;
import org.mmfmilku.atom.transport.protocol.handle.ServerHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypeAssemblyHandler implements ServerHandle<TypeFrame, TypeFrame> {

    private static Codec<List<TypeFrame>, AssemblyDataFrame> codec = new AssemblyCodec();

    @Override
    public AssemblyDataFrame code(TypeFrame typeFrame) {
        throw new MessageCodeException("not support");
    }

    @Override
    public TypeFrame decode(TypeFrame typeFrame) {
        throw new MessageCodeException("not support");
    }

    @Override
    public List<TypeFrame> handleDecode(TypeFrame typeFrame) {
        return codec.decode(new AssemblyDataFrame(typeFrame.getData()));
    }

    private AssemblyDataFrame codeList(List<TypeFrame> typeFrames) {
        return codec.code(typeFrames);
    }

    @Override
    public void handle(TypeFrame typeFrame, PipeLine pipeLine) {

        byte[] data = typeFrame.getData();
        int total = MessageUtils.decodeInt(Arrays.copyOfRange(data, 0, 2));
        Object attr = pipeLine.getAttr(this);
        if (attr != null) {
            List<TypeFrame> typeFrames = (List<TypeFrame>) attr;
            typeFrames.add(typeFrame);
            if (typeFrames.size() < total) {
                pipeLine.setAttr(this, typeFrames);
            } else {
                // 聚合所有帧
                AssemblyDataFrame assemblyDataFrame = codeList(typeFrames);
                pipeLine.handleNext(assemblyDataFrame);
            }
        } else {
            // 首帧
            List<TypeFrame> typeFrames = new ArrayList<>();
            typeFrames.add(typeFrame);
            if (total == 1) {
                // 无需聚合
                AssemblyDataFrame assemblyDataFrame = codeList(typeFrames);
                pipeLine.handleNext(assemblyDataFrame);
            } else {
                // 需要聚合，保存首帧
                pipeLine.setAttr(this, typeFrames);
            }
        }

    }

}
