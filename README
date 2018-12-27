License information
===================

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation version 3 as published by
the Free Software Foundation. You may not use, modify or distribute
this program under any other version of the GNU Affero General Public
License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Contributions/Patches
=====================

If you decide to take part in the OdinMS project please post your 
contributions in form of a patch file in the official OdinMS developer forum 
located at <http://www.odinms.de/forum/>.

Requirements
============

OdinMS requires a JVM compliant with the Java SE 6.0. The Sun JVM can be 
acquired at <http://java.sun.com/javase/downloads/index.jsp>.

If you are using the Sun JVM additionally the JCE Unlimited Strength files 
are required. <http://java.sun.com/javase/downloads/index.jsp>

You will also need a few libraries:
- Apache MINA 1.1 <http://mina.apache.org/downloads.html>
- MySQL Connector/J <http://dev.mysql.com/downloads/connector/j/>
- slf4j 1.5 <http://www.slf4j.org/download.html>

Finally you will also need the Maple Story data files (*.wz) available.

For storage purposes OdinMS requires a database backend which is accessible 
using a JDBC driver. Since other DBMS have not been tested with OdinMS, we 
recommend MySQL. <http://dev.mysql.com>


OdinMS Architecture concept
===========================

     World Server
     /      |    \
  Login  Channel1  Channel2

The OdinMS Maple Story server is split up into three parts:
	- The login server handles user authentication and interacts with the 
client in the early stages of the game (server selection etc.)
	- The channel server actually hosts a Maple Story channel and handles 
most of the gameplay.
	- The world server is responsible for inter-channel communication and 
provides interfaces for channel and login servers to interact with each other.

The communication between login, channel and world servers is implemented using 
RMI. It is therefore possible to deploy OdinMS in a distributed environment.
It is also possible to run multiple channel servers inside a single process, so 
they can share resources, especially concerning data file access.

Please note: The database access is not managed by the world server in any way. 
Each channel server must have access to the same database. (When using MySQL this 
may be achieved by simply using remote connections or a MySQL Cluster solution)


Required JVM Options
====================

To run the different OdinMS server you will have to provide some defines to the JVM:

net.sf.odinms.recvops
	- Path to the recvops.properties file containing the protocol op codes
net.sf.odinms.sendops
	- Path to the sendops.properties file containing the protocol op codes
net.sf.odinms.login.config
	- Path to the login.properties file containing configuration information 
for the login server
net.sf.odinms.channel.config
	- Path to the channel.proerties file containing configuration information 
for the channel server(s)
net.sf.odinms.wzpath
	- Path to the folder containing the Maple Story .wz files

Configuration files
===================

For detailed information about the several configurable options please refer to the 
comments included in the configuration files themselves.
Additionally to the files mentioned in the JVM options the following configuration 
files are required in the server's working directory:

On all servers:
	db.properties
		- Contains information and credentials for the database connection

On the world server:
	world.properties
		- Contains gameplay related configuration information (valid for all 
channels connected to this world server)

Gameplay related configuration changes for single channels can be made in the 
database using the channelconfig table.


Keystores
=========

OdinMS uses SSL encryption for the RMI communication between world, channel and 
login servers. You will have to generate a public/private key pair for each server 
and create appropriate key and trust stores.

You can create and manage these using the keytool application provided with the JDK
http://java.sun.com/javase/6/docs/technotes/tools/windows/keytool.html

Paths and passwords for the key and trust stores can be provided using the following 
JVM defines:
javax.net.ssl.keyStore
javax.net.ssl.keyStorePassword
javax.net.ssl.trustStore
javax.net.ssl.trustStorePassword

Startup
=======

An example of JVM configurations is provided in the launch_*.sh/launch_*.bat files of
the release.

Launch order:
	1. World Server
	2. Login Server
	3. Channel Server(s)

Since the world server manages all inter-server interaction and is hosting the RMI 
registry you will have to launch it first. After that the login server has to be started. 
If you try to register a channel server when no login server is active you may encounter 
problems.