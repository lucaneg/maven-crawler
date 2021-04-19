# maven-crawler
A simple jar-file crawler for maven central.

## Usage

Main class is `it.lucaneg.mvncrawler.MVNCrawler`. The only required option is `-D`:

```
usage: MVNCrawler
 -D,--workdir <arg>      the directory where the downloaded jars will be
                         stored
 -H,--proxy-host <arg>   url of proxy host
 -l,--limit <arg>        maximum number of jars to download, default is
                         500
 -P,--proxy-port <arg>   port number of proxy host
 -p,--proxy-pass <arg>   password of proxy-authentication
 -u,--proxy-user <arg>   user name of proxy-authentication
 ```
