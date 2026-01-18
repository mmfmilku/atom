package org.mmfmilku.atom.api.dto;

import java.io.Serializable;

/**
 * FServer运行时信息
 *
 **/
public class FServerInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 监听路径：FServer监听的路径
    private String listenPath;
    // 连接数：当前已建立的连接数量
    private int connectCount;
    // 活跃线程数：当前正在执行任务的线程数量
    private int activeCount;
    // 线程池大小：当前线程池中的线程数量
    private int poolSize;
    // 核心线程数：线程池保持的最小线程数量
    private int corePoolSize;
    // 最大线程数：线程池允许的最大线程数量
    private int maximumPoolSize;
    // 队列大小：当前等待执行的任务队列中的任务数量
    private int queueSize;
    // 队列容量：任务队列能够容纳的最大任务数量
    private int queueCapacity;

    public String getListenPath() {
        return listenPath;
    }

    public void setListenPath(String listenPath) {
        this.listenPath = listenPath;
    }

    public int getConnectCount() {
        return connectCount;
    }

    public void setConnectCount(int connectCount) {
        this.connectCount = connectCount;
    }

    public int getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(int activeCount) {
        this.activeCount = activeCount;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }
}
