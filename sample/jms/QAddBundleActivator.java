package sample.jms;

import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.ObjectFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import com.ibm.mq.jms.MQConnectionFactoryFactory;
import com.ibm.mq.jms.MQQueueFactory;
import com.sun.jndi.fscontext.RefFSContextFactory;

/* Licensed Materials - Property of IBM                                   */
/*                                                                        */
/* SAMPLE                                                                 */
/*                                                                        */
/* (c) Copyright IBM Corp. 2015,2016 All Rights Reserved                  */       
/*                                                                        */
/* US Government Users Restricted Rights - Use, duplication or disclosure */
/* restricted by GSA ADP Schedule Contract with IBM Corp                  */
/*                                                                        */  

/**
 * Bundle activator that registers the MQ object factories and the initial context factory
 * for the file system JNDI provider.
 */          
public class QAddBundleActivator implements BundleActivator
{
  @Override
  public void start(BundleContext ctx) throws Exception
  {
    //Register the initial context factory implementation.
    ctx.registerService(new String[]{InitialContextFactory.class.getName(), RefFSContextFactory.class.getName()},
                        new RefFSContextFactory(), 
                        null); 

    //Register the MQ object factories that we are using.
    //If you were using other MQ JMS objects, such as a MQTopic, you would register the relevant
    //object factory here too.
    String ofClassName = ObjectFactory.class.getName();

    ctx.registerService(new String[]{ofClassName, MQConnectionFactoryFactory.class.getName()}, 
                        new MQConnectionFactoryFactory(), 
                        null);

    ctx.registerService(new String[]{ofClassName, MQQueueFactory.class.getName()}, 
                        new MQQueueFactory(), 
                        null);
  }

  @Override
  public void stop(BundleContext ctx) throws Exception
  {
  }
}
