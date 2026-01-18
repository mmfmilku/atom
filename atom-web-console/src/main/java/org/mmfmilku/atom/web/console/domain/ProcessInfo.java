package org.mmfmilku.atom.web.console.domain;

/**
 * 进程信息
 **/
public class ProcessInfo {

    /**
     * 进程号
     * */
    private String pid;

    /**
     * 进程启动目录
     * */
    private String workingDir;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }
}
