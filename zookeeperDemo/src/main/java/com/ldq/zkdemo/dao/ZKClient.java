package com.ldq.zkdemo.dao;

import com.ldq.zkdemo.config.ZKConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ChildrenDeletable;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.List;

@Slf4j
@Component
public class ZKClient {

    private CuratorFramework client;

    @Autowired
    private ZKConfig zkConfig;

    @PostConstruct
    private void init() {
        client = CuratorFrameworkFactory.newClient(zkConfig.getServer(),
                zkConfig.getSessionTimeoutMs(),
                zkConfig.getConnectionTimeoutMs(),
                new RetryUntilElapsed(zkConfig.getMaxElapsedTimeMs(), zkConfig.getBaseSleepTimeMs()));
        client.start();
    }

    public CuratorFramework getClient() {
        return client;
    }

    /**
     * @param mode
     * @param path
     * @param data
     */
    public void createNode(CreateMode mode, String path, String data) {
        try {
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path, data.getBytes());
        } catch (Exception e) {
            log.error("创建节点出错！", e);
        }

    }

    /**
     * @param mode
     * @param path
     */
    public void createNode(CreateMode mode, String path) {
        try {
            client.create().creatingParentsIfNeeded().withMode(mode).forPath(path);
        } catch (Exception e) {
            log.error("创建节点出错！", e);
        }
    }

    private void deleteNode(String path, Boolean deleteChildren) {
        ChildrenDeletable guaranteed = client.delete().guaranteed();
        try {
            if (deleteChildren) {
                guaranteed.deletingChildrenIfNeeded().forPath(path);
            } else {
                guaranteed.forPath(path);
            }
        } catch (Exception e) {
            log.error("删除节点失败！", e);
        }
    }

    public void deleteNodeAll(String path) {
        deleteNode(path, true);
    }

    public void deleteNode(String path) {
        deleteNode(path, false);
    }

    public void setNodeData(String path, String data) {
        try {
            client.setData().forPath(path, data.getBytes());
            log.info("写入数据成功， data = ", data);
        } catch (Exception e) {
            log.error("写入数据失败", e);
        }
    }

    public String getNodeData(String path) {
        String data = null;
        try {
            data = new String(client.getData().forPath(path));
        } catch (Exception e) {
            log.error("获取数据失败！", e);
        }

        return data;
    }


    public List<String> nodeList(String path) throws Exception {
        List<String> nodeList = client.getChildren().forPath(path);
        nodeList.forEach(x -> System.out.println("path = " + x));
        return nodeList;
    }

    public boolean isExists(String path) {
        client.sync();
        try {
            return null != client.checkExists().forPath(path);
        } catch (Exception e) {
            return false;
        }
    }

    @PreDestroy
    public void destory() {
        client.close();
    }
}
