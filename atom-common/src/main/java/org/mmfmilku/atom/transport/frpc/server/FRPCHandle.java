package org.mmfmilku.atom.transport.frpc.server;

import org.mmfmilku.atom.transport.protocol.handle.PipeLine;
import org.mmfmilku.atom.transport.protocol.handle.RRModeHandle;
import org.mmfmilku.atom.transport.protocol.handle.type.TypeFrame;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Map;

public class FRPCHandle implements RRModeHandle<TypeFrame> {

    private Map<String, ServiceMapping> mappings;

    public FRPCHandle(Map<String, ServiceMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void onReceive(TypeFrame data, PipeLine pipeLine) {
        try {
            byte[] rawData = data.getData();

            FRPCParam frpcParam = IOUtils.deserialize(rawData);

            ServiceMapping serviceMapping = mappings.get(frpcParam.getServiceClass());
            if (serviceMapping == null) {
                throw new RuntimeException("service not found " + frpcParam.getServiceClass());
            }

            FRPCReturn frpcReturn = serviceMapping.execute(frpcParam.getApiName(), frpcParam);
            pipeLine.write(encode(frpcReturn));
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                FRPCReturn frpcReturn = new FRPCReturn();
                frpcReturn.setSuccess(Boolean.FALSE);
                frpcReturn.setFrpcException(new FRPCException(e.getMessage()));
                pipeLine.write(encode(frpcReturn));
            } catch (Exception ex) {
                // 异常处理再次报错
                ex.printStackTrace();
            }
        }
    }

    private TypeFrame encode(FRPCReturn frpcReturn) {
        byte[] serialize = IOUtils.serialize(frpcReturn);
        return new TypeFrame(serialize);
    }

}
