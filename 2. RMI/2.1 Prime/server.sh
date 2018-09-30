compile client:
javac -cp /mnt/disk2/Git/DBS/"2. RMI"/src/compute.jar client/ComputePi.java client/Pi.java client/Prime.java

rmiregistry


start server:
java -cp /home/jie/src:/home/jie/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.13/~jie/classes/compute.jar -Djava.rmi.server.hostname=192.168.0.13 -Djava.security.policy=server.policy engine.ComputeEngine

start client:
java -cp /home/pi/src:/home/pi/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.11/~pi/classes/ -Djava.security.policy=client.policy client.ComputePi 192.168.0.13 45
