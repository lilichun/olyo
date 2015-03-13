# Introduction #

Currently a p2psip lib, along with a modified Partysip is available for download. The modified Linphone is under testing. We will release it soon.

# p2psip lib #

The p2psip lib is a c lib providing interface to access a list of overlay. It is useful for developing a hierarchical P2P SIP proxy. The p2psip lib also provide the same interface to access different type of overlay. Currently it only supports bamboo DHT.

# modified Partysip #
The modified Partysip can run as a proxy defined in "[A Hierarchical P2P-SIP Architecture](http://www.p2psip.org/drafts/draft-shi-p2psip-hier-arch-00.html)" or a proxy defined in "[Interworking between P2PSIP Overlays and Conventional SIP Networks](http://www.p2psip.org/drafts/draft-marocco-p2psip-interwork-00.html)". The modified Partysip can also run as an adaptor. The modified Partysip can run in proxy mode and adaptor mode at the same time.

**[installation guide](partysipinstall.md)**

**partysipconfig configuration guide**

 [Configuration guide for adaptor mode](AdaptorModeCfg.md)

 [Configuration guide for hierarchical proxy mode](HierarchicalProxyModeCfg.md)