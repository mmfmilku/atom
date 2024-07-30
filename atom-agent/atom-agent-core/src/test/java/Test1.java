import org.junit.Test;
import org.mmfmilku.atom.agent.config.AgentProperties;
import org.mmfmilku.atom.agent.config.ClassORDDefine;
import org.mmfmilku.atom.agent.config.OverrideBodyHolder;
import org.mmfmilku.atom.agent.util.FileUtils;

import java.io.IOException;
import java.util.Map;

public class Test1 {


    @Test
    public void testProperties() {

        AgentProperties.loadProperties("base-path=E:/project/atom/atom-agent/src/main/resources/config;test=come;to-string-method=d.c.t");
        System.out.println(AgentProperties.getInstance());
        
    }

    @Test
    public void testReadFileToString() {

        String s = null;
        try {
            s = FileUtils.readText("E:/project/atom/atom-agent/src/main/resources/config/agent.conf");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(s);

    }

    @Test
    public void testLoadOr() {

        OverrideBodyHolder.load("E:/project/atom/atom-agent/src/main/resources/config/");
        Map<String, ClassORDDefine> a = OverrideBodyHolder.getORClassMap();
        
        System.out.println(a);

    }

    
}
