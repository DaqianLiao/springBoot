package com.ldq.zkdemo.dao;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.RetryUntilElapsed;
import org.apache.zookeeper.CreateMode;

public class CacheDemo {

    private static int num = 1;
    private static String parentPath = "/Curator-Recipes";//父节点

    private static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString("localhost:2181")
            .sessionTimeoutMs(5000)
            .retryPolicy(new RetryUntilElapsed(5000, 1000))
            .build();



    /**
     * 初始化操作，创建父节点
     */
    public static void init() {
        client.start();
        try {

            if (client.checkExists().forPath(parentPath) == null) {
                System.out.println("create persistent node =" + parentPath);
                client.create().creatingParentContainersIfNeeded()
                        .withMode(CreateMode.PERSISTENT)
                        .forPath(parentPath, "This is Parent Data!".getBytes());
            }

            client.create().withMode(CreateMode.EPHEMERAL)
                    .forPath(parentPath + "/c1", "This is C1.".getBytes());//创建第一个节点

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听子节点变化
     */
    public static void pathChildrenCache() {
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, parentPath, true);
        try {
            pathChildrenCache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);//启动模式
        } catch (Exception e) {
            e.printStackTrace();
        }
        //添加监听
        pathChildrenCache.getListenable().addListener((curatorFramework, pathChildrenCacheEvent) -> {
            System.out.println("pathChildrenCacheEvent = " + pathChildrenCacheEvent.getData());
            System.out.println(num++ + ".pathChildrenCache------发生的节点变化类型为："
                    + pathChildrenCacheEvent.getType()
                    + ",发生变化的节点内容为："
                    + new String(pathChildrenCacheEvent.getData().getData()) + "\n======================\n");
        });
    }


    /**
     * 监听节点数据变化
     */
    public static void nodeCache() {
        final NodeCache nodeCache = new NodeCache(client, parentPath, false);
        try {
            nodeCache.start(true);//true代表缓存当前节点
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (nodeCache.getCurrentData() != null) {//只有start中的设置为true才能够直接得到
            System.out.println(num++ + ".nodeCache-------CurrentNode Data is:" + new String(nodeCache.getCurrentData().getData()) + "\n===========================\n");//输出当前节点的内容
        }

        //添加节点数据监听
        nodeCache.getListenable().addListener(() ->
                System.out.println(num++ + ".nodeCache------节点数据发生了改变，发生的路径为："
                        + nodeCache.getCurrentData().getPath() + ",节点数据发生了改变 ，新的数据为："
                        + new String(nodeCache.getCurrentData().getData()) + "\n===========================\n"));
    }

    /**
     * 同时监听数据变化和子节点变化
     */
    public static void treeCache() {
        final TreeCache treeCache = new TreeCache(client, parentPath);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //添加错误
        treeCache.getUnhandledErrorListenable()
                .addListener((s, throwable) ->
                        System.out.println(num++ + ".错误原因：" + throwable.getMessage() + "\n==============\n"));

        treeCache.getListenable()
                .addListener((curatorFramework, treeCacheEvent) ->
                        System.out.println(num++ + ".treeCache------当前发生的变化类型为："
                                + treeCacheEvent.getType() + ",发生变化的节点内容为："
                                + new String(treeCacheEvent.getData().getData()) + "\n=====================\n"));
    }

    /**
     * 创建节点、修改数据、删除节点等操作，用来给其他的监听器测试使用
     */
    public static void testData() {
        try {
            client.create().withMode(CreateMode.EPHEMERAL).forPath(parentPath + "/c2", "This is C2.".getBytes());//创建第一个节点
            client.create().withMode(CreateMode.EPHEMERAL).forPath(parentPath + "/c3", "This is C3.".getBytes());//创建第一个节点

            client.setData().forPath(parentPath + "/c2", "This is New C2.".getBytes());//修改节点数据

            client.delete().forPath(parentPath + "/c3");//删除一个节点

//            client.delete().deletingChildrenIfNeeded().forPath(parentPath);//将父节点下所有内容删除

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     *   1、2 ：代表是TreeCache监听到了父节点和c1节点的创建缓存事件。
     *
     * 3、4 ：同时会发现并没有3这条语句，而是直接跳到了4，这是因为接收到的事件为：INITIALIZED，所以使用getData会得到null，而我们试图在null上调用getPath，所以才会触发异常。
     *
     * 5：NodeCache缓存进行start的时候传入true参数，所以能够直接得到当前节点的内容。
     *
     * 6： PathChildrenCache缓存成功c1的时候接收到的事件。
     *
     * 7：会发现没有7，因为PathChildrenCache的启动模式是：INITIALIZED，此时也是试图在null上调用GetPath，但是PathChildrenCache没有提供异常监听器，所以没办法获取。
     *
     * 8：第八点最让人疑惑了，因为上面的代码中并没有对父节点的数据进行改变，但是却监听到了这个事件，做了很多的测试发现，触发这个事件的原因为后面的testData方法中调用create导致的，并且只会监听到一次，这一点的具体原因还不太清楚。
     *
     * 9、10、11、12：创建c2、c3节点是TreeCache和PathChildrenCache监听到的事件。
     *
     * 13、14：修改c2节点数据，TreeCache和PathChildrenCache监听到的事件。
     *
     * 15、16、17、18、19、20：删除c2、c1、c3节点时，TreeCache和PathChildrenCache监听到的事件。
     *
     * 21：删除根节点时接收到的监听事件，此时只有TreeCache能够监听到。
     * @param args
     * @throws InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {

        init();
        treeCache();
        pathChildrenCache();
        nodeCache();
        testData();
        Thread.sleep(5 * 1000);

    }


}
