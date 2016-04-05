import javax.jms._ 
import java.util.{Hashtable => JHashtable}
import javax.naming._
import java.util.Calendar

/**
 * UACC port is remote://localhost:5190
 * Local port is remote://localhost:4447
 */
object ConsumerSynchronous {

  var DEFAULT_QCF_NAME = "jms/RemoteConnectionFactory"
  var DEFAULT_QUEUE_NAME = "queue/steveTestQueue"
  var DEFAULT_URL = "remote://localhost:4447"
  var DEFAULT_USER = "guest"
  var DEFAULT_PASSWORD =  "guest123!"


   
  def main(args: Array[String]): Unit = {
    
    // parse through command line arguments
    for (i <- 0 until args.length) {
        var s = args(i).split("=")
        if(s(0) == "URL") {
          DEFAULT_URL = s(1)
          println("Overriding URL with " + DEFAULT_URL)
        }
        else if(s(0) == "QUEUE") {
          DEFAULT_QUEUE_NAME = s(1)
          println("Overring QUEUE with " + DEFAULT_QUEUE_NAME)
        }
        else {
          println("unknown argument " + s(0));
        }
    }

    val properties = new JHashtable[String, String]
    properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory")
    properties.put(Context.PROVIDER_URL, DEFAULT_URL)
    properties.put(Context.SECURITY_PRINCIPAL, DEFAULT_USER)
    properties.put(Context.SECURITY_CREDENTIALS, DEFAULT_PASSWORD)
 
    val ctx = new InitialContext(properties)
    println("Got InitialContext " + ctx.toString())
  
    val connectionFactory  = (ctx.lookup("jms/RemoteConnectionFactory")).asInstanceOf[QueueConnectionFactory]
    println("Got ConnectionFactory")
    val connection = connectionFactory.createQueueConnection
    println("Got connection")
    connection.start
    println("Connection started")
 
    val session: Session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
    println("Got session")
    val queue  = (ctx.lookup(DEFAULT_QUEUE_NAME)).asInstanceOf[Queue]
    println("Got queue")
    val consumer = session.createConsumer(queue)
    println("Got consumer")
 
    val listener = new MessageListener {
      def onMessage(message: Message) {
        message match {
          case text: TextMessage => {
              println("Received message: " + text.getText)          
          }
          case _ => {
            throw new Exception("Unhandled Message Type: " + message.getClass.getSimpleName)
          }
        }
      }
    }
    
    consumer.setMessageListener(listener)
    println("Message Listener set")

  }
}