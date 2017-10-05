	mysql> INSERT INTO users (id,name,maildir,crypt) VALUES ('noreply@allschool.in','noreply','noreply/',encrypt('32f8e56f4827',CONCAT('$5$',MD5(RAND()))) );
	

	
sendmail noreply@allschool.in  < /tmp/email.txt

allschool-git@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:repo.git
ember server --proxy http://127.0.0.1:8080

ember build --environment=production

To do :

change isEditing on enter
fix UI when showing files and editing

notify users who are tagged
notify users who are added to groups

rearrange groups



on deleting group > delete all posts

store keeps fetching from server 
add to store on saving
duplicate store requests
fix tutorial setup , add mixin and adapter, fix language

generic ajax error msgs

channel api / pusher

web login
git user name : allschool-git
passphrase : $ch00l@pp

mobile login :
username : allschoolmobile
passphrase @sch00lm0b1l3
allschoolmobile.git
allschoolmobile@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:allschoolmobile.git

bare repo : 
username : allschoolbase
passphrase : b@s3r3p0
baserepo.git
allschoolbase@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:baserepo.git

ls -l
ls -lart
ls -l --block-size=K Notes.war
zip -r Notes.war .
cp -R to copy folder
-------------------------------
    <Host name="notes.bogaboga.org"  appBase="webapps2"
            unpackWARs="true" autoDeploy="true">
<Context path="" docBase="/usr/share/tomcat7/webapps2/Notes.war"/>
      
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="localhost_access_log." suffix=".txt"
               pattern="%h %l %u %t &quot;%r&quot; %s %b" />

      </Host>

export ANT_OPTS=-Dfile.encoding=UTF-8 


ssh -i "/Volumes/Xtra/workspace/BharatRailsTomcat/keys/jkbServer.pem" ec2-user@ec2-54-186-163-31.us-west-2.compute.amazonaws.com

scp -i "/Volumes/Xtra/workspace/BharatRailsTomcat/keys/jkbServer.pem" /Volumes/Xtra/workspace/Notes/Notes.war ec2-user@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:~/.

on mac , ssh ec2-54-186-163-31.us-west-2.compute.amazonaws.com , after http://stackoverflow.com/questions/19227642/include-pem-for-git-pull-push
/var/lib/tomcat7/webapps4
~/.ssh/config

find / -type f -name "Notes.war"

find / -name Notes

df -h
#########windows

click psftp
command : open BharatRails
command : put D:\del\Notes.war

################
git remote add origin ec2-user@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:notes.git
git remote set-url origin ec2-user@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:notes.git
git config --global remote.origin.receivepack "git receive-pack"

https://shirtdev.wordpress.com/2011/05/04/setting-up-a-git-repository-on-an-amazon-ec2-instance/
http://stackoverflow.com/questions/10904339/github-fatal-remote-origin-already-exists
ALTER TABLE your_database_name.your_table CONVERT TO CHARACTER SET utf8
git remote set-url origin ec2-user@ec2-54-186-163-31.us-west-2.compute.amazonaws.com:notes.git

dev setup : 
		install memcache
		install node.js , bower and ember
		install sass , bootstrap, moment
	commands : 
		npm install -g bower
		npm install -g ember-cli
		ember install ember-cli-sass
		ember install ember-cli-bootstrap-sassy
		ember install ember-moment
		ember install ember-content-editable
		to run memcache : memcached -p 11211 -u memcached -m 64 -M -vv 
	
	references : 
		https://nodejs.org/en/
		http://yoember.com
		https://www.npmjs.com/package/ember-moment
		https://www.npmjs.com/package/ember-content-editable
		http://www.ubergizmo.com/how-to/install-memcached-windows/
		https://code.google.com/archive/p/memcached-session-manager/wikis/SetupAndConfiguration.wiki
		setup memcache non sticky sessions, add to tomcat7/conf/context.xml :  <Manager className="de.javakaffee.web.msm.MemcachedBackupSessionManager" sticky="false" memcachedNodes="localhost:11211" failoverNodes="" lockingMode="all" requestUriIgnorePattern=".*\.(png|gif|jpg|css|js|ico)$" sessionBackupAsync="false" sessionBackupTimeout="100" />

production setup : 
	install development tools (for gcc compiler), java7 , mysql, tomcat7, libevent, memcache
	
	install development tools 
		yum group list
		yum group install "Development Tools"
		
	install libevent
		wget http://monkey.org/~provos/libevent-1.4.14b-stable.tar.gz
		tar xzf libevent-1.4.14b-stable.tar.gz
		cd libevent-1.4.14b-stable
		./autogen.sh  # run first if configure is not available 
		./configure --prefix=/var/lib/libevent
		make
		make install
	
	install memcache 
		wget http://memcached.org/latest
		tar -zxvf memcached-1.x.x.tar.gz
		cd memcached-1.x.x
		./configure && make && make test && sudo make install 
	
	setup memcache non sticky sessions, add to tomcat7/conf/context.xml :  
		<Manager className="de.javakaffee.web.msm.MemcachedBackupSessionManager" sticky="false" memcachedNodes="localhost:11211" failoverNodes="" lockingMode="all" requestUriIgnorePattern=".*\.(png|gif|jpg|css|js|ico)$" sessionBackupAsync="false" sessionBackupTimeout="100" />
	
	errors : 
		error while loading shared libraries: libevent-2.1.so.6 
 		ln -s /usr/local/lib/libevent-2.1.so.6 /usr/lib64/libevent-2.1.so.6
	
	setup upload folders
		chown -R :tomcat
		chmod 755
	
	references : 
		https://www.cyberciti.biz/faq/centos-rhel-7-redhat-linux-install-gcc-compiler-development-tools/
		https://www.liquidweb.com/kb/how-to-install-memcached-on-centos-6/
		http://libevent.org
		https://geeksww.com/tutorials/operating_systems/linux/installation/how_to_install_libevent_on_debianubuntucentos_linux.php
		https://github.com/libevent/libevent/issues/160
		http://memcached.org/downloads
		https://github.com/memcached/memcached/wiki/ConfiguringServer
		https://code.google.com/archive/p/memcached-session-manager/wikis/SetupAndConfiguration.wiki
		http://www.nigeldunn.com/2011/12/11/libevent-2-0-so-5-cannot-open-shared-object-file-no-such-file-or-directory/
		http://www.journaldev.com/16/memcached-telnet-commands-with-example
		https://www.lullabot.com/articles/installing-memcached-on-redhat-or-centos
		https://kyup.com/tutorials/install-use-memcache/

		
https://www.liquidweb.com/kb/how-to-install-memcached-on-centos-6/
 /usr/local/bin/memcached 
./memcached -d -u nobody -m 256 -p 11211 127.0.0.1
memcached -d -u [user] -m [memory size] -p [port] [listening IP]		

going to Start>Search and type “cmd”
Type the command “c:\memcached\memcached.exe -d start” to start the service
Type the command “c:\memcached\memcached.exe -d stop” to stop the service
To change the memory pool size, type “c:\memcached\memcached.exe -m 512” for 512MB

update script
	service tomcat7 stop
	rm -f /var/lib/tomcat7/webapps5/Notes.war
	rm -rf /usr/share/tomcat7/webapps5/*
	cp /home/ec2-user/Notes.war /var/lib/tomcat7/webapps5/Notes.war
	service tomcat7 start
	
	
	allschool.in
	
Steps to deploy : 
take pull
build ember distribution
copy files from UI/notes/dist to WebContent
remove integrity attribute from 4 lines in WebContent/index.html
build war and copy to server
deploy to appropriate folder	