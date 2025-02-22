package org.mmfmilku.atom.transport.frpc.api.impl;

import org.mmfmilku.atom.transport.frpc.server.FRPCService;
import org.mmfmilku.atom.transport.frpc.api.FRpcServiceTwo;

import java.util.HashMap;
import java.util.Map;

@FRPCService
public class FRpcServiceTwoImpl implements FRpcServiceTwo {

    public Map<String, String> getMap(String type) {
        Map<String, String> map = new HashMap<>();
        switch (type) {
            case "1":
                map.put("k1", "v1");
            case "2":
                map.put("k2", "v2");
            case "3":
                map.put("k3", "v3");
            case "4":
                map.put("k4", "v4");
            case "5":
                map.put("k5", "v5");
            default:
                map.put("k0", "v0");
        }
        return map;
    }

}
