package hank.wang.client.zookeeper;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ZookeeperFactory {

    // 静态变量用于保存单例的CuratorFramework客户端实例
    // 无论开启多少个netty客户端，都会只有一个client去zookeeper注册netty节点
    public static CuratorFramework client;

    public static CuratorFramework create() {
        if (client == null) {
            // 创建重试策略，首次重试等待1秒，之后重试间隔时间以指数方式增加，最多重试3次
            RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
            // 使用CuratorFrameworkFactory来创建一个新的CuratorFramework客户端
            // 设置ZooKeeper服务器地址和重试策略
            client = CuratorFrameworkFactory.newClient("localhost:2181", retryPolicy);
            client.start();
        }

        return client;
    }

    public static void main(String[] args) throws Exception {
        CuratorFramework client = create();
        // 这里的create是CuratorFramework客户端提供的方法，用于创建zookeeper中的节点
        client.create().forPath("/netty");
    }
}
