gcc -o portunes.out main.c -lpthread -lwiringPi -lrt $(mysql_config --cflags) $(mysql_config --libs)
sudo ./portunes.out
