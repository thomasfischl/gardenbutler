Readme File:

Install python:

sudo apt-get install python-setuptools
sudo apt-get install python-serial


sudo chmod 777 max -R
chmod 755 service.py

chmod 755 watcherdaemon
update-rc.d watcherdaemon defaults

ln -s ../init.d/waterdaemon S90waterdaemon

edit /etc/rc.local
  /etc/init.d/waterdaemon start

http://blog.scphillips.com/2013/07/getting-a-python-script-to-run-in-the-background-as-a-service-on-boot/