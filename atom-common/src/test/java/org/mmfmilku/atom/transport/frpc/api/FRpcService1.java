package org.mmfmilku.atom.transport.frpc.api;

import org.mmfmilku.atom.transport.frpc.FRPCService;

import java.util.*;

@FRPCService
public class FRpcService1 {

    @FRPCService
    public List<Map<String, String>> getList() {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(Collections.singletonMap("a", "1"));
        list.add(Collections.singletonMap("b", "2"));
        return list;
    }

    @FRPCService
    public List<Map<String, String>> getList2(String a, String b) {
        List<Map<String, String>> list = new ArrayList<>();
        list.add(Collections.singletonMap(a, "1"));
        list.add(Collections.singletonMap(b, "2"));
        return list;
    }

    private void add() {

    }

}
