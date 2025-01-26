package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

import java.util.ArrayList;
import java.util.List;

public class PipeLine {

    private Connector connector;

    PipeLine(Connector connector) {
        this.connector = connector;
    }

    private List<ServerHandle> handleList = new ArrayList<>();

    /**
     * 表示已经处理的最后 handle的下标
     * */
    private int handlePoint = -1;

    public void handleNext(Object frame) {
        if (handleList.size() > handlePoint + 1) {
            handlePoint++;
            handleList.get(handlePoint).handle(frame, this);
        }
    }

    public void write(Object input) {
        connector.write(decode(input));
    }

    public void setAttr(Object key, Object value) {
        connector.getAttrMap().put(key, value);
    }

    public Object getAttr(Object key) {
        return connector.getAttrMap().get(key);
    }

    private FFrame decode(Object input) {
        Object output = input;
        // 倒序解码
        for (int i = handlePoint; i >= 0; i--) {
            output = handleList.get(i).decode(output);
        }
        return (FFrame) output;
    }

    List<ServerHandle> getHandleList() {
        return handleList;
    }
}
