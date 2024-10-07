package org.mmfmilku.atom.transport.handle;

import org.mmfmilku.atom.transport.ConnectContext;
import org.mmfmilku.atom.transport.frpc.ServiceMapping;

import java.util.Map;

public class FRPCHandle extends RRModeServerHandle {

    Map<String, ServiceMapping> mappings;

    public FRPCHandle(Map<String, ServiceMapping> mappings) {
        this.mappings = mappings;
    }

    @Override
    public void onReceive(ConnectContext ctx, String data) {

    }

}
