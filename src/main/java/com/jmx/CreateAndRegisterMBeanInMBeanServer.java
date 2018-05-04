package com.jmx;


import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.List;

public class CreateAndRegisterMBeanInMBeanServer {

    public static void register(List<MbeanEntity> mbeanToRegister) throws Exception {

        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        for(MbeanEntity mbeanEntity : mbeanToRegister){
            ObjectName mbeanName = new ObjectName(mbeanEntity.getObjectName());

            server.registerMBean(mbeanEntity.getObj(), mbeanName);
        }

    }

    static public class Hello implements HelloMBean {

        private String message = "Hello World";

        @Override
        public String getMessage() {
            return this.message;
        }

        @Override
        public void sayHello() {
            System.out.println(message);
        }


    }

    public interface HelloMBean {

        // operations
        public void sayHello();

        // attributes

        // a read-write attribute called Message of type String
        public String getMessage();

    }

}
