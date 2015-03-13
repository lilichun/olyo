This page gives a description on steps and commands necessary to configure and run modified Partysip in an hierarchical proxy mode. Corresponding reference information and
examples are provided as well.

## Audience ##
This page is intended for users who want to startup Patysip as the proxy ofhierarchical p2p architecture over Bamboo DHT, but are not necessarily familier with configuration of Partysip. In addition, some familiarity with P2PSIP, understanding of hierarchical proxy mode, as well as [basic knowledge on configuring Bamboo](bambooCfg.md)are required.

## Configuration Instructions ##
Here is a task list to configure and run modified Partysip as a hierarchical proxy.

  1. Configure Bamboo and set up a hierarchical architecture.
  1. Configure Partysip as a hierarchical proxy mode.
  1. Startup Partysip.

### 1 Bamboo Configuration ###
Details about Bamboo configuration can be attained [here](bambooCfg.md). See more specific instrutions on [Bamboo.org](http://bamboo-dht.org/users-guide.html).

### 2 Partysip Configuration ###
  1. Download configuration template partysip.conf, and save it as
```
/<home>/<YOURCFGPATH>/partysip.conf
```

  1. Edit following arguments as needed.
| **Argument** | **Section** | **Description** |
|:-------------|:------------|:----------------|
| **serverip** |  |**IP address of proxy** |
| **overlay** | **overlaylist** | **Arguments of overlays the proxy joining in** |
| **isHProxy** |  | **Indicating whether the partysip runs as a hierarchical proxy** |

  * serverip: The IP address of proxy server connecting lower overlay and higher overlay when the partysip runs as a hirarchical proxy.
  * overlay: the arguments of overlay as the format: `overlay [overlay id] [overlay type] [hierarchical level] [IP] [port].` For example, _overlay wti.bamboo bamboo 0 127.0.0.1 6632_ means that the overlay identifier is wit.bamboo, and the type of overlay is bamboo. 0 means overlay is the lowest level in hierarchical network whereas 1 means it is a higher overlay.  127.0.0.1 is the IP address of a Bamboo node, and 6632 is the port to establish TCP connection between Bamboo and Partysip. Note the port should be the same with that configured in Bamboo configuration file in the identical overlay. When running as a hierarchical proxy, the partysip should connect with both the lower and higher overlays, therefore require two lines for configuration, one of which has the "overlay type" 1, the other 0.
  * isHProxy: Edit this line as "isHProxy = yes" to set the partysip running as a hierarchical proxy.

### 3 Partysip Startup ###
Start up Partysip as hierarchical proxy mode with the following command:
```
partysip -f /<home>/<YOURCFGPATH>/adaptor.conf -d6
```

## 4 Configuration Example ##
In our example, two kinds of overlays are included: bottom level and high level.

The first bottom overlay with the identifier `wti.bamboo` contains only one node whose id as well as gateway is 192.168.10.221:6630. See configure file gateway.cfg for 192.168.10.221. The second overlay named `bupt.bamboo` also includes a single node. Its gateway and overlay id is 192.168.10.222:6630. See configure file gateway.cfg for 192.168.10.222. The overlay id of high level overlay is `highlevel`. It contains two nodes: one has the id 192.168.10.221:3630, the other has the id 192.168.10.222:3630. The gateway of both nodes is 192.168.10.221:3630. See configure files on 192.168.10.221 gateway2.cfg and 192.168.10.222 gateway2.cfg.

There are two partysips linking to `wti.bamboo`. One runs as adaptor mode, connecting wti.bamboo through port 6632 and UA through port 8060. The other runs as hierarchical proxy with the port 3632 linking to the overlay `highlevel`. The configure files are adptor.conf and partysip.conf.

A single partysip acting as both a hierarchical proxy and an adaptor runs on 192.168.10.222. Through port 8060 it can be connected with UA while port 6632 to overlay `bupt.bamboo` and port 3632 to overlay `highlevel`. The configure file for partysip is partysip.conf on 192.168.10.222.

All the above-mentioned configure files can be downloaded [here](http://olyo.googlecode.com/files/hproxycfg20070226.tar.gz).







