# InvitEx

A plugin for Minecraft hMod that provides invite commands with approval system.

## Installation

1. Copy InvitEx.jar to plugins directory
1. Add InvitEx to plugins line in server.properties

## Configuration

Edit bin/InvitEx/plugin.ini, and reload plugin

* expires = 60
	* Time to expire (seconds) 

## Commands

### /invite [player]
Invite player

### /accept
Accept invite (will jump to position of player who invite)<br />
If invitee didn't use command in expire time, invitation will be cancelled.

## Download
[Latest version](https://github.com/palm3r/Hey0Plugins/raw/master/InvitEx/build/latest/InvitEx.jar)
