package org.mmfmilku.atom.transport.protocol.handle;

import org.mmfmilku.atom.transport.protocol.Connector;
import org.mmfmilku.atom.transport.protocol.base.FFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        List<Object> fFrames = decode(input);
        for (Object fFrame : fFrames) {
            connector.write((FFrame) fFrame);
        }
    }

    public void setAttr(Object key, Object value) {
        connector.getAttrMap().put(key, value);
    }

    public Object getAttr(Object key) {
        return connector.getAttrMap().get(key);
    }

    public Object removeAttr(Object key) {
        return connector.getAttrMap().remove(key);
    }

    private List<Object> decode(Object input) {
        List<Object> outputs = new ArrayList();
        outputs.add(input);
        Stream stream = outputs.stream();
        // 倒序解码
        for (int i = handlePoint; i >= 0; i--) {
            int finalI = i;
            stream = stream.map(output -> handleList.get(finalI).handleDecode(output))
                    .flatMap(o -> ((List) o).stream());
        }
        return (List<Object>) stream.collect(Collectors.toList());
    }

    List<ServerHandle> getHandleList() {
        return handleList;
    }
}
