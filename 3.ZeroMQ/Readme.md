Jie Tao - G01723372

#### Run Server:
mvn exec:java -Djava.rmi.server.useCodebaseOnly=false  -Djava.security.licy=policy  -Dexec.mainClass=edu.gvsu.cis.MyPresenceServer

#### Run Client
mvn exec:java -Djava.rmi.server.useCodebaseOnly=false  -Djava.security.licy=policy  -Dexec.mainClass=edu.gvsu.cis.ChatClient -Dexec.args=[username]
