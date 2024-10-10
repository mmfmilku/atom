package org.mmfmilku.atom.transport.frpc.api;

import java.util.List;
import java.util.Map;

public interface FRpcServiceOne {

    List<Map<String, String>> getList();

    List<Map<String, String>> getList2(String a, String b);

}
