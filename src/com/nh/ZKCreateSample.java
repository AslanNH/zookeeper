package com.nh;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
/**
 * zookeeper�����ػ����첽��
 * 
 * @author nh
 *
 */
public class ZKCreateSample implements Watcher{

	private static final String HOST="localhost:2181";
	private static final int TIME_OUT = 6000;
	private static ZooKeeper zkClient = null;
	private static CountDownLatch connectedSemaphore = new CountDownLatch(1);
	
	public static void main(String[]args)throws Exception {
		connectZK();
		//testCreate();
		//testExist();
		//testGetChildren();
		//testGetData();
		//testSetData();
		//testDelete();
	}
	/**
	 * ����zk������
	 * @throws IOException
	 */
	public static void connectZK()throws IOException{
		zkClient = new ZooKeeper(HOST,TIME_OUT,new ZKCreateSample() );
		System.out.println("begin state="+zkClient.getState());
		try {
			connectedSemaphore.await();
		} catch (InterruptedException e) {
			
			e.printStackTrace();
			System.out.println("Zookeeper session established.");
		}
		System.out.println("end state="+zkClient.getState());
	}
	
	@Override
	public  void process(WatchedEvent event) {
		System.out.println("receive watched event:" + event);
		if(KeeperState.SyncConnected == event.getState()) {
			connectedSemaphore.countDown();
		}
	}
	//znode����ɾ�Ĳ�
	/**
	 * �������ݽڵ�
	 */
	public static String testCreate()throws Exception {
		String znode = zkClient.create("/sampleZkCli", "fristCreateData".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
		return znode;
	}
	/**
	 * �жϽڵ��Ƿ����
	 */
	public static boolean testExist()throws Exception{
		Stat stat = zkClient.exists("/sampleZkCli", false);
		System.out.println(!(stat==null));
		return stat==null;
	}
	/**
	 * ��ȡ�ӽڵ�
	 */
	public static List<String> testGetChildren()throws Exception{
		List<String> childrenList = zkClient.getChildren("/", false,null);
		for(String str :childrenList) {
			System.out.println(str);
		}
		return childrenList;
	}
	/**
	 * ��ȡznode������
	 */
	public static String testGetData()throws Exception{
		byte[] data = zkClient.getData("/sampleZkCli", false, null);
		System.out.println(new String(data));
		return new String(data);
	}
	
	/**
	 * ����znode
	 */
	public static String testSetData()throws Exception{
		zkClient.setData("/sampleZkCli", "firstUpdateData".getBytes(), -1);
		return testGetData();
	}
	/**
	 * ɾ��znode����
	 */
	public static void testDelete()throws Exception{
		//-1��ʾɾ�����а汾
		zkClient.delete("/sampleZkCli", -1);
	}
}
