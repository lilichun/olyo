## Objectives ##
This guide describes the steps and commands necessary to configure and run Partysip in an Adaptor Mode. Corresponding reference information and examples are provided as well.

## Audience ##
This documentation is intended for users who want to run a SIP UA over
Bamboo DHT, but are not necessarily familier with configuration of Bamboo
and Partysip. In addition, some familiarity with P2PSIP, especially the
architecture of Adaptor Mode is required.

## Configuration Instructions ##
**Here is a task list to configure and run Partysip in an Adaptor Mode:**
  1. Configure Bamboo: edit arguments in Bamboo configurtion file used to join each node into the overlay.(If openDHT is used as underlying P2P overlay, skip this step)
  1. Configure Partysip: edit arguments in Partysip configuration file used for communication with Bamboo.
  1. Create a overlay: start Bamboo on each node and create a P2P overlay. (If you don't want to create your own bamboo overlay, you may use openDHT instead)
  1. Start Adaptor: start Partysip and connect a SIP UA with it. Partysip is considered as an adaptor for the SIP UA to run over P2P network.

Much more details are available in [adptorcfgguide0204.pdf](http://olyo.googlecode.com/files/adptorcfgguide0204.pdf).

**Enable adaptor to make a call to other domain(overlay)**

Locate inbound proxy of destination domain via DNS
> Add the line below to the partysip configuration file
> > DNS\_enable=on

Locate inbound proxy of destination domain via top overlay
  1. Run a hierarchical proxy in local overlay
  1. Add "useOProxy=yes" to the adaptor(partysip) configuration file



