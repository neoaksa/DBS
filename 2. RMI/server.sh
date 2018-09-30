java -cp /home/jie/src:/home/jie/public_html/classes/compute.jar
     -Djava.rmi.server.codebase=http://192.168.0.13/~jie/classes/compute.jar
     -Djava.rmi.server.hostname=192.168.0.13
     -Djava.security.policy=server.policy
        engine.ComputeEngine
