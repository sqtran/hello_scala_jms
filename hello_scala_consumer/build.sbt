lazy val commonSettings = Seq(
  organization := "com.example",
  version := "0.1.0",
  scalaVersion := "2.11.7"
)

lazy val root = (project in file(".")).
  settings(commonSettings: _*).
  settings(
    name := "hello",
    libraryDependencies ++= Seq(
       "javax.jms" % "javax.jms-api" % "2.0",
       "org.jboss" % "jboss-remote-naming" % "1.0.7.Final",
       "org.jboss.xnio" % "xnio-nio" % "3.0.0.CR7",
       "org.hornetq" % "hornetq-jms-client" % "2.3.25.Final"
     )
  )