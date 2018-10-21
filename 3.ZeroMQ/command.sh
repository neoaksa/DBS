mvn exec:java -Djava.rmi.server.useCodebaseOnly=false  -Djava.security.policy=policy  -Dexec.mainClass=edu.gvsu.cis.MyPresenceServer


mvn exec:java -Djava.rmi.server.useCodebaseOnly=false  -Djava.security.policy=policy  -Dexec.mainClass=edu.gvsu.cis.ChatClient -Dexec.args=bob
