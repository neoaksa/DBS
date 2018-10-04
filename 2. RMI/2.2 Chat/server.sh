
# compile
javac -cp /mnt/disk2/Git/DBS/"2. RMI"/src/compute.jar client/ComputePi.java client/Pi.java client/Prime.java

# start server
1.
rmiregistry &
2.
java -cp /home/jie/src:/home/jie/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.13/~jie/classes/compute.jar -Djava.rmi.server.hostname=192.168.0.13 -Djava.security.policy=server.policy engine.ComputeEngine

# start client
1. 
cd src

2.
java -cp /home/pi/src:/home/pi/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.11/~pi/classes/ -Djava.security.policy=client.policy client.ComputePi 192.168.0.13 45


------------------------------------------------------------------------------------------

# compile
javac -cp chat.jar client/*.java
javac -cp chat.jar chatServer/*.java

# start server
rmiregistry &

java -cp /home/jie/src:/home/jie/public_html/classes/chat.jar -Djava.rmi.server.codebase=http://192.168.0.13/~jie/classes/chat.jar -Djava.rmi.server.hostname=192.168.0.13 -Djava.security.policy=server.policy chatServer.chatServer


# start client(for each client)
1. 
cd src
java client.EchoServer

2.# port is from feedback of client.EchoServer
cd src
java -cp /home/pi/src:/home/pi/public_html/classes/chat.jar -Djava.rmi.server.codebase=http://192.168.0.11/~pi/classes/ -Djava.security.policy=client.policy client.EchoClient 192.168.0.13 [name] [port]

