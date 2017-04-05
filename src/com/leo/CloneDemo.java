package com.leo;

import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Datastore;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;

/**
 * Description: 虚机克隆 <br> 
 *  
 * @author li.an1 <br>
 * @version 8.0 <br>
 * @taskId <br>
 * @CreateDate 2017-3-1 <br>
 * @since V8 <br>
 * @see com.leo <br>
 */
public class CloneDemo {

    public static void main(String[] args) throws Exception {
        cloneTemplate();
    }
    
    public static String cloneTemplate() throws Exception {
        try {
            String vmName = "centos7.3-tmp";
            String cloneName = "anyshareTest1";
            String poolName = "10.45.66.116";
            String hostName = "10.45.66.116";
            String datastorename = "66.81_DX100S3_Lenovo_PDT_DS1";
            com.vmware.vim25.mo.VirtualMachine templateVM = null;
            com.vmware.vim25.mo.ResourcePool pool = null;
            ComputeResource computerResource = null;
            InventoryNavigator inventoryNavigator = null;
            Task task = null;

            ServiceInstance serviceInstance = null;
            URL url = new URL("https://10.45.66.100/sdk");
            serviceInstance = new ServiceInstance(url, "administrator@vsphere.local", "Zte$soft2012", true);
            inventoryNavigator = new InventoryNavigator(serviceInstance.getRootFolder());
            try {
                templateVM = (com.vmware.vim25.mo.VirtualMachine) inventoryNavigator.searchManagedEntity(
                    "VirtualMachine", vmName);
            }
            catch (RemoteException e) {
                System.out.println(e);
            }
            
            
            
            
            
            HostSystem hostSystem = (HostSystem) inventoryNavigator.searchManagedEntity("HostSystem", hostName);
            Datastore[] datastoreList = hostSystem.getDatastores();
            for (Datastore ds : datastoreList) {
                System.out.println(ds.getName());
            }
            
            System.out.println("=========");
                
            Datacenter dc = (Datacenter) inventoryNavigator.searchManagedEntity(
                "Datacenter", "PDT");
            Datastore[] dsList = dc.getDatastores();
            for(Datastore ds : dsList){
                System.out.println(ds.getName());
            }
            
//            Datastore datastore = null;
//            try {
//                ManagedEntity[] managedEntities = inventoryNavigator.searchManagedEntities("Datastore");
//                for (int i = 0; i < managedEntities.length; i++) {
//                    datastore = (Datastore) managedEntities[i];
//                    System.out.println("--" + datastore.getName());
//                }
//            }
//            catch (Exception e) {
//                System.out.println("指定Datastore存在问题:" + e.getMessage());
//                System.out.println(e);
//            }
            
            
            
            
            
            
            
            VirtualMachineRelocateSpec virtualMachineRelocateSpec = new VirtualMachineRelocateSpec();
            if (null != poolName && !"".equals(poolName)) {
                try {
                    pool = (ResourcePool) inventoryNavigator.searchManagedEntity("ResourcePool", poolName);
                    virtualMachineRelocateSpec.setPool(pool.getMOR());
                }
                catch (RemoteException e) {
                    System.out.println(e);
                }

            }
            else {
                try {
                    computerResource = (ComputeResource) inventoryNavigator.searchManagedEntity("ComputeResource",
                        hostName);
                    virtualMachineRelocateSpec.setPool(computerResource.getResourcePool().getMOR());
//                    virtualMachineRelocateSpec.setDatastore(datastore.getMOR());
                    
//                    virtualMachineRelocateSpec.setHost(computerResource.getHosts()[0].getMOR());
                }
                catch (RemoteException e) {
                    System.out.println(e);
                }

            }

            VirtualMachineConfigSpec configSpec = new VirtualMachineConfigSpec();

            configSpec.setNumCPUs(Integer.parseInt("4"));
            configSpec.setMemoryMB(Long.parseLong("1024"));

            VirtualMachineCloneSpec cloneSpec = new VirtualMachineCloneSpec();

            cloneSpec.setLocation(virtualMachineRelocateSpec);

            cloneSpec.setPowerOn(true);
            cloneSpec.setTemplate(false);
            cloneSpec.setConfig(configSpec);

            /*try {

                task = templateVM.cloneVM_Task((Folder) templateVM.getParent(), cloneName, cloneSpec);
                String result = task.waitForTask();
                if (result.equals(Task.SUCCESS)) {
                    System.out.println("success");
                }
                else {
                    System.out.println("failed");
                }

            }
            catch (RemoteException e) {
                System.out.println(e);
            }*/
        }
        catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

}
