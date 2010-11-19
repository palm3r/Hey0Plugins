# WarpEx

A plugin for Minecraft hMod that extends warp functions to support namespace.

## Installation

1. Copy WarpEx.jar to plugins directory
1. Add WarpEx to plugins line in server.properties
1. If you updating from v1.1 or older, move your file like below:
	* WarpEx.properties to bin/WarpEx/plugin.ini
	* warpex-data.txt to bin/WarpEx/data.txt
1. Add these commands to the group which you want to give privileges:
	* /warpex-* : can use /warp and /listwarps to global namespace
	* /warpex-! : can use /warp and /listwarps to secret namespace
	* /warpex-modify-* : also can use /setwarp and /removewarp to global namespace
	* /warpex-modify-! : also can use /setwarp and /removewarp to secret namespace

## Configuration

Edit bin/WarpEx/plugin.ini, and reload plugin

* default-namespace = Personal
	* All commands use this setting when namespace is omitted.

## Namespaces

There are two special namespaces:

* Global namespace shown by "*".
* Secret namespace shown by "!".

All namespaces except these are treated as personal namespace.

## Commands (and aliases)

### /setwarp (/sw) <namespace:>[warp]
Add current position to specified namespace.

### /removewarp (/rw) <namespace:>[warp]
Remove warp from specified namespace.

### /warp (/go) <namespace:>[warp]
Move to warp position.

### /listwarps (/lw) <namespace>
Show warps.

### /listns
Show all namespaces without global and secret.

## Download
[Latest version](https://github.com/palm3r/Hey0Plugins/raw/master/WarpEx/build/latest/WarpEx.jar)

## Source codes
[Github](https://github.com/palm3r/Hey0Plugins/tree/master/WarpEx)
