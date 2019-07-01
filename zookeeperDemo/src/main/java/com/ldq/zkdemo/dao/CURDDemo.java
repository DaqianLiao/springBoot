package com.ldq.zkdemo.dao;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.data.Stat;

import java.util.List;

public class CURDDemo {
    private static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new RetryUntilElapsed(5000, 1000))
            .build();

    public static void startZkClient() {
        client.start();
    }


    public static boolean isPathExists(CuratorFramework zkClient, String path) throws Exception {
        Stat stat = zkClient.checkExists().forPath(path);
        if (stat != null) {
            System.out.println("path = " + path + " is exists");
            System.out.println(stat);
        } else {
            System.out.println(path + " is not exists");
        }

        return stat != null;
    }

    public static void nodeList(CuratorFramework zkClient, String path) throws Exception {
        List<String> strings = zkClient.getChildren().forPath(path);
        strings.forEach(x -> System.out.println("path = " + x));

    }

    public static boolean createNode(CuratorFramework zkClient, String path) throws Exception {
        if (!isPathExists(zkClient, path)) {
            String forPath = zkClient.create().creatingParentsIfNeeded().forPath(path, "create init!".getBytes());
            System.out.println(forPath);
        }

        return true;
    }

    public static void transaction(CuratorFramework zkClient, String path) {


    }

    public static void rmrNode(CuratorFramework zkClient, String path) throws Exception {

        if (isPathExists(zkClient, path)) {
            Void aVoid = zkClient.delete().deletingChildrenIfNeeded().forPath(path);
            System.out.println(aVoid);
        }
    }

    public static void setData(CuratorFramework zkClient, String path, String message) throws Exception {
        zkClient.setData().forPath(path, message.getBytes());
    }

    public static void getNode(CuratorFramework zkClient, String path) throws Exception {

        byte[] bytes = zkClient.getData().forPath(path);
        System.out.println(path + " data = " + new String(bytes));
    }

    public static void testListen(CuratorFramework zkClient, String path, NodeCacheListener listener) {


    }

    public static void main(String[] args) throws Exception {

        String zookeeper = "/zookeeper";

        startZkClient();
        isPathExists(client, zookeeper);
        nodeList(client, zookeeper);
        getNode(client, zookeeper);


        String test = "/test1/test0";
        rmrNode(client, test);
//        isPathExists(client, test);
        createNode(client, test);
        nodeList(client, test);
        getNode(client, test);
        client.sync();

        NodeCache cache = new NodeCache(client, test, false);
        cache.getListenable().addListener(
                () -> {System.out.println("node data changed!" + new String(client.getData().forPath(test)));
                    System.out.println(" new data = " + new String(cache.getCurrentData().getData()));});
        try {
            cache.start(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Thread.sleep(1000);
        setData(client, test, "{\"test\":1}");
        Thread.sleep(1000);

//        client.close();

    }
}
