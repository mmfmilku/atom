package org.mmfmilku.atom.transport.frpc.server;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.handle.ChannelContext;
import org.mmfmilku.atom.transport.protocol.handle.RRModeHandle;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Base64;
import java.util.Map;

public class FRPCHandle implements RRModeHandle<String> {

    private Map<String, ServiceMapping> mappings;

    public FRPCHandle(Map<String, ServiceMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void onReceive(String data, ChannelContext<String> ctx) {
        try {
            byte[] rawData = Base64.getDecoder().decode(data.trim());

            FRPCParam frpcParam = IOUtils.deserialize(rawData);

            ServiceMapping serviceMapping = mappings.get(frpcParam.getServiceClass());
            if (serviceMapping == null) {
                throw new RuntimeException("service not found " + frpcParam.getServiceClass());
            }

            FRPCReturn frpcReturn = serviceMapping.execute(frpcParam.getApiName(), frpcParam);
            // TODO 超长数据，分批写入
            ctx.write(encode(frpcReturn));
        } catch (Throwable e) {
            e.printStackTrace();
            try {
                FRPCReturn frpcReturn = new FRPCReturn();
                frpcReturn.setSuccess(Boolean.FALSE);
                frpcReturn.setFrpcException(new FRPCException(e.getMessage()));
                ctx.write(encode(frpcReturn));
            } catch (Exception ex) {
                // 异常处理再次报错
                ex.printStackTrace();
            }
        }
    }

    private String encode(FRPCReturn frpcReturn) {
        byte[] serialize = IOUtils.serialize(frpcReturn);
        String encode = Base64.getEncoder().encodeToString(serialize);
        return encode + "\r";
    }

}
