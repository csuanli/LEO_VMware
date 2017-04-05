package com.leo;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.vmware.vim25.mo.ClusterComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;

public class ClusetDemo {

    public static void main(String[] args) throws Exception {
        List<DtreeObj> list = new ArrayList<DtreeObj>();

        ServiceInstance serviceInstance = null;
        URL url = new URL("https://10.45.66.100/sdk");
        serviceInstance = new ServiceInstance(url, "administrator@vsphere.local", "Zte$soft2012", true);

        list = getTree(serviceInstance);

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                DtreeObj obj = list.get(i);
                System.out.println("id is:" + obj.getId() + ";name is:" + obj.getName() + ";pid is:" + obj.getPid()
                    + ";type is:" + obj.getType());
                System.out.println("-----------------------------------------------");
            }
        }

    }

    public static List<String> getdifference(List<String> a, List<String> b) {
        List<String> content = new ArrayList<String>();
        if (a != null && b != null) {
            if (a.size() > 0 && b.size() > 0) {
                for (int i = 0; i < a.size(); i++) {
                    boolean target = false;// Ĭ����b�����в�����
                    String diffA = a.get(i);
                    // �ж��Ƿ��ַ�����ָ���ļ��ϵ���
                    for (int j = 0; j < b.size(); j++) {
                        String diffB = b.get(j);
                        if (diffA.equalsIgnoreCase(diffB)) {
                            target = true;
                        }
                    }
                    // ����������ݼ���
                    if (!target) {
                        content.add(diffA);
                    }

                }
            }
        }

        return content;
    }

    public static List<DtreeObj> getTree(ServiceInstance serviceInstance) throws Exception {
        List<DtreeObj> list = new ArrayList<DtreeObj>();

        // �������Ĺ���HostSystem
        List<String> allhost = new ArrayList<String>();
        // �Ѿ�����HostSystem
        List<String> hostList = new ArrayList<String>();
        // �Ǽ�ȺHostSystem
        List<String> noClusterHostList = null;

        // rootFolder-------���ļ���
        Folder rootFolder = serviceInstance.getRootFolder();
        System.out.println("datacenter is:" + rootFolder.getName());

        int counterInt = 1;

        int rootId = 0;
        DtreeObj root = new DtreeObj();
        root.setId(rootId);
        root.setName(rootFolder.getName());
        root.setPid(-1);
        root.setType(4);

        list.add(root);

        // inventoryNavigator----�ļ���Ŀ¼
        InventoryNavigator inventoryNavigator = new InventoryNavigator(rootFolder);
        // hostEntities--------��ѯʵ�����esxi��
        ManagedEntity[] hostEntities = inventoryNavigator.searchManagedEntities("HostSystem");
        if (hostEntities != null && hostEntities.length > 0) {
            for (int i = 0; i < hostEntities.length; i++) {
                HostSystem hostSystem = (HostSystem) hostEntities[i];
                allhost.add(hostSystem.getName());
            }
        }

        ManagedEntity[] managedEntities1 = inventoryNavigator.searchManagedEntities("Datacenter");
        if (managedEntities1 != null && managedEntities1.length > 0) {
            for (int i = 0; i < managedEntities1.length; i++) {
                Datacenter datacenter = (Datacenter) managedEntities1[i];
                System.out.println(datacenter.getName());
                ManagedEntity[] host_list = new InventoryNavigator(datacenter).searchManagedEntities("HostSystem");
                for(ManagedEntity entity : host_list){
                    System.out.println(entity.getName());
                }
            }
        }
        
        // managedEntities------��ѯʵ�����
        ManagedEntity[] managedEntities = inventoryNavigator.searchManagedEntities("ClusterComputeResource");
        if (managedEntities != null && managedEntities.length > 0) {
            for (int i = 0; i < managedEntities.length; i++) {
                ClusterComputeResource cluster = (ClusterComputeResource) managedEntities[i];
                counterInt = counterInt + i;// ͳ��������
                int clusterId = counterInt;// ��ȺId

                DtreeObj clusterObj = new DtreeObj();
                clusterObj.setId(clusterId);
                clusterObj.setName(cluster.getName());
                clusterObj.setPid(rootId);
                clusterObj.setType(1);

                list.add(clusterObj);

                // ��Ⱥ����������
                HostSystem[] hostSystems = cluster.getHosts();
                if (hostSystems != null && hostSystems.length > 0) {
                    for (int j = 0; j < hostSystems.length; j++) {
                        HostSystem system = hostSystems[j];
                        int a = j + 1;
                        counterInt = counterInt + (a);// ͳ��������
                        int hostId = counterInt;// ������Id

                        DtreeObj hostObj = new DtreeObj();
                        hostObj.setId(hostId);
                        hostObj.setName(system.getName());
                        hostObj.setPid(clusterId);
                        hostObj.setType(3);

                        hostList.add(system.getName());
                        list.add(hostObj);
                    }

                }
                // ��Ⱥ������Դ��
                ResourcePool resourcePool = cluster.getResourcePool();
                if (resourcePool != null) {
                    ResourcePool[] resourcePools = resourcePool.getResourcePools();
                    if (resourcePools != null && resourcePools.length > 0) {
                        for (int k = 0; k < resourcePools.length; k++) {
                            ResourcePool pool = resourcePools[k];
                            int b = k + 1;
                            counterInt = counterInt + (b);// ͳ��������
                            int poolId = counterInt;// ��Դ��Id

                            DtreeObj poolObj = new DtreeObj();
                            poolObj.setId(poolId);
                            poolObj.setName(pool.getName());
                            poolObj.setPid(clusterId);
                            poolObj.setType(2);

                            // poolList.add(pool.getName());
                            list.add(poolObj);
                        }

                    }
                }

            }

        }

        // ����Ǽ�ȺHostSystem
        noClusterHostList = getdifference(allhost, hostList);
        if (noClusterHostList != null && noClusterHostList.size() > 0) {
            for (int i = 0; i < noClusterHostList.size(); i++) {
                String content = noClusterHostList.get(i);
                int b = i + 1;
                counterInt = counterInt + (b);
                int nohostId = counterInt;

                DtreeObj nohostObj = new DtreeObj();
                nohostObj.setId(nohostId);
                nohostObj.setName(content);
                nohostObj.setPid(rootId);
                nohostObj.setType(3);

                list.add(nohostObj);

            }
        }

        return list;

    }

}