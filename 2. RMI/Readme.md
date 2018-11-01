Jie Tao - G01723372

## 1.Prime

Call remote server to execute two functions: Pi and Prime. Source can be found [here](https://github.com/neoaksa/DBS/tree/master/2.%20RMI/2.1%20Prime/src)

#### Folder:

Client: Client source and class. put into client: `/home/$username/src`

Compute: interface source file.

Compute.jar: interface jar file. put into both server and client: `/home/$username/pulic_html/`

engine: implented interface. put into server: `/home/$username/src`

server.policy: server policy file. put into server:`/home/$username/src`, where we executes java file.

client.policy: client policy file. put into client:`/home/$username/src`, where we executes java file.

#### Scripts:

1.start server

```batch
cd src

rmiregistry &

java -cp /home/jie/src:/home/jie/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.13/~jie/classes/compute.jar -Djava.rmi.server.hostname=192.168.0.13 -Djava.security.policy=server.policy engine.ComputeEngine
```

2.start client:

```batch
cd src

java -cp /home/pi/src:/home/pi/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://192.168.0.11/~pi/classes/ -Djava.security.policy=client.policy client.ComputePi 192.168.0.13 45
```

## 2.Chat

Build old-school communication system through RMI.Source can be found [here](https://github.com/neoaksa/DBS/tree/master/2.%20RMI/2.2%20Chat/src)

#### Folder:

Client: Client source and class. put into client: `/home/$username/src`

chat: interface source file.

chat.jar: interface jar file. put into both server and client: `/home/$username/pulic_html/`

chatServer: implented interface. put into server: `/home/$username/src`

server.policy: server policy file. put into server:`/home/$username/src`, where we executes java file.

client.policy: client policy file. put into client:`/home/$username/src`, where we executes java file.

#### Scripts:

1. start server

```batch
cd src 

rmiregistry &

java -cp /home/jie/src:/home/jie/public_html/classes/compute.jar -Djava.rmi.server.codebase=http://127.0.0.1/~jie/classes/compute.jar -Djava.rmi.server.hostname=127.0.0.1 -Djava.security.policy=server.policy chatServer.chatServer
```

2. start client(for each client)

```batch
cd src
java -cp /home/jie/src:/home/jie/public_html/classes/chat.jar -Djava.rmi.server.codebase=http://127.0.0.1/~pi/classes/ -Djava.security.policy=client.policy client.EchoClient 127.0.0.1 [username] 
```

#### Command:

```batch
please input your option:
[friends] : list all available friends.
[talk]{username}{message} : talk to friends.
[broadcast]{message} : broadcast message.
[busy] : set status as busy.
[available] : set status as available.
[exit] : exit program
```

#### install apache2 and enable public_html on arch linux
````
sudo pacman -S apache
mkdir ~/public_html

````

