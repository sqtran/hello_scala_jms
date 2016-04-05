import java.util.{Hashtable => JHashtable}
import javax.naming._
import javax.jms._
import java.util.Calendar
 
 /**
  * UACC port is remote://localhost:5190
  * Local port is remote://localhost:4447
  */
  object SimpleJMSClient {
 
    var DEFAULT_QCF_NAME = "jms/RemoteConnectionFactory"
    var DEFAULT_QUEUE_NAME = "queue/steveTestQueue"
    var DEFAULT_URL = "remote://localhost:4447"
    var DEFAULT_USER = "guest"
    var DEFAULT_PASSWORD =  "guest123!"
 
    def sendMessage(theMessage: String) {
      sendMessage(
        url = DEFAULT_URL,
        user = DEFAULT_USER,
        password = DEFAULT_PASSWORD,
        cf = DEFAULT_QCF_NAME,
        queue = DEFAULT_QUEUE_NAME,
        messageText = theMessage)
    }
 
    def sendMessage(url : String, user : String, password : String,
                    cf : String, queue : String, messageText : String) {
      // create InitialContext
      val properties = new JHashtable[String, String]
      properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory")
      properties.put(Context.PROVIDER_URL, url)
      properties.put(Context.SECURITY_PRINCIPAL, user)
      properties.put(Context.SECURITY_CREDENTIALS, password)
      properties.put("jboss.naming.client.ejb.context", "false");
      properties.put("remote.connectionprovider.create.options.org.xnio.Options.SSL_ENABLED", "false");
      properties.put("remote.connection.default.connect.options.org.xnio.Options.SASL_POLICY_NOANONYMOUS", "false");
      
 
 
      try {
        val ctx = new InitialContext(properties)
        println("Got InitialContext")
 
        // create QueueConnectionFactory
        val qcf = (ctx.lookup(cf)).asInstanceOf[QueueConnectionFactory]
        println("Got QueueConnectionFactory")
 
        // create QueueConnection
        val qc = qcf.createQueueConnection()
        println("Got QueueConnection")
 
        qc.start
 
        // create QueueSession
        val qsess = qc.createQueueSession(false, Session.AUTO_ACKNOWLEDGE)
        println("Got QueueSession")
 
        // lookup Queue
        val q = (ctx.lookup(queue)).asInstanceOf[Queue]
        println("Got Queue ")
 
        // create QueueSender
        val qsndr = qsess.createSender(q)
        println("Got QueueSender")
 
        // create TextMessage
        val message = qsess.createTextMessage()
        println("Got TextMessage")
 
        // set message text in TextMessage
        message.setText(messageText)
        println("Set text in TextMessage " + message.toString())
 
        // send message
        qsndr.send(message)
        println("Sent message ")
      
        
        qc.close()
        
      } catch {
        case _ : Throwable =>
          println("Got other/unexpected exception")
          System.exit(0)
      } finally {
         println("Finally")
      }
    }
 
    def main(args: Array[String]) = {
      var m:String = "hello world from Scala sendMessage(), " + Calendar.getInstance().getTime()
    
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
        else if(s(0) == "MESSAGE") {
	   m = s(1)
	   println("Overring Message with " + DEFAULT_QUEUE_NAME)
        }
        else {
          println("unknown argument " + s(0));
        }
      }  
 
      sendMessage(m)

    }
    
    
  }