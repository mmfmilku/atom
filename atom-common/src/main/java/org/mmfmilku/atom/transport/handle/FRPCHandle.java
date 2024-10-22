package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;
import org.mmfmilku.atom.transport.frpc.FRPCException;
import org.mmfmilku.atom.transport.frpc.FRPCParam;
import org.mmfmilku.atom.transport.frpc.FRPCReturn;
import org.mmfmilku.atom.transport.frpc.ServiceMapping;
import org.mmfmilku.atom.util.IOUtils;

import java.util.Base64;
import java.util.Map;

public class FRPCHandle extends RRModeServerHandle {

    private Map<String, ServiceMapping> mappings;

    public FRPCHandle(Map<String, ServiceMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void onReceive(ConnectContext ctx, String data) {
        try {
            byte[] rawData = Base64.getDecoder().decode(data.trim());

            FRPCParam frpcParam = IOUtils.deserialize(rawData);

            ServiceMapping serviceMapping = mappings.get(frpcParam.getServiceClass());
            if (serviceMapping == null) {
                throw new RuntimeException("service not found " + frpcParam.getServiceClass());
            }

            FRPCReturn frpcReturn = serviceMapping.execute(frpcParam.getApiName(), frpcParam);
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
