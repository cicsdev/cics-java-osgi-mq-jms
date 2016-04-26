package sample.jms;

import java.util.Hashtable;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import com.ibm.cics.server.InvalidRequestException;
import com.ibm.cics.server.Task;

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
 * Receives up to 50 messages containing numbers from one queue, adds them together
 * and sends them to another queue.
 */
public class QAdd
{
  //Maximum number of messages to receive.
  private static final int MAX_MESSAGES = 50;
  
  public static void main(String args[])
  {
    Task task = Task.getTask();
    
    try
    {
      //The value we are going to put in a message on the targetQueue.
      int sum = 0;
      
      //Create JNDI initial context. 
      Hashtable<String, String> environment = new Hashtable<>();
      
      //NB: you will need to change this URL and point it to a directory containing a .bindings file
      //generated by the JMSAdmin tool.
      environment.put(Context.PROVIDER_URL, "file:///u/mleming/jndi/");
      environment.put(Context.INITIAL_CONTEXT_FACTORY, 
                      "com.sun.jndi.fscontext.RefFSContextFactory");
      Context ctx = new InitialContext(environment);
      
      //Locate the connection factory and the two queues.
      Queue sourceQueue = (Queue)ctx.lookup("sourceQueue");
      Queue targetQueue = (Queue)ctx.lookup("targetQueue");
      ConnectionFactory cf = (ConnectionFactory)ctx.lookup("cf");
      
      //Connect to MQ and create a session that will allow messages to be processed
      //under the CICS unit of work.
      Connection connection = cf.createConnection();
      
      //NB: if using JMS 2 you would probably code the following here as it is 
      //more concise. 
      //Session session = connection.createSession();
      //Or you would use a JMSContext instead.
      Session session = connection.createSession(true, Session.SESSION_TRANSACTED);
      
      //Remember to start the connection, otherwise we won't be able to 
      //receive messages.
      connection.start();
      
      //Create the message consumer.
      MessageConsumer consumer = session.createConsumer(sourceQueue);
      
      //Receive up to 50 messages from the queue. Don't wait for messages to arrive.
      int i = 0;

      for(; i < MAX_MESSAGES; i++)
      {
        Message message = consumer.receiveNoWait();
        
        //Break out of loop if we get a null message as this means that the 
        //queue is empty.
        if(message == null)
        {
          task.out.println("No message received, leaving loop.");
          break;
        }
        
        //Only process text messages.
        if(message instanceof TextMessage)
        {
          //Get the text and convert to an int.
          TextMessage textMessage = (TextMessage)message;
          String text = textMessage.getText();
          int nextInt;
          
          try
          {
            nextInt = Integer.parseInt(text);
          }
          catch(NumberFormatException nfe)
          {
            task.out.println("Message didn't contain an int, ignoring.");
            continue;
          }
          
          task.out.println("Message received, integer value = " + nextInt + ".");

          //Add to the running total.
          sum += nextInt;
        }
        else
        {
          task.out.println("Unexpected message received, ignoring.");
        }
      }
      
      //Create a message producer.
      MessageProducer producer = session.createProducer(targetQueue);
      
      //Create a text message containing sum.
      TextMessage sumMessage = session.createTextMessage("" + sum);
      
      //Send the message.
      producer.send(sumMessage);

      task.out.println("Sum of " + i + " messages is " + sum + ".");
      
      //Close all the JMS resources by closing the connection.
      connection.close();
      
      //Use implicit synch point to commit the messages.
    }
    catch(Exception e)
    {
      //Output debug information and rollback messages.
      task.out.println("Exception caught: " + e.getMessage());
      e.printStackTrace();
      
      try
      {
        task.rollback();
      } 
      catch (InvalidRequestException e1)
      {
        task.out.println("Exception caught from rollback: " + e1.getMessage());
        e1.printStackTrace();
      }
    }
  }
}