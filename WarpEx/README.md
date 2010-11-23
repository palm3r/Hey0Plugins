# WarpEx

A plugin for Minecraft hMod that extends warp functions to support namespace.

## Installation

1. Copy WarpEx.jar to plugins directory

1. Add WarpEx to plugins line in server.properties

1. If you updating from v1.1 or older, move your file like below:

		move WarpEx.properties to bin/WarpEx/plugin.ini
		move warpex-data.txt to bin/WarpEx/data.txt

1. Add these commands to the group which you want to give privileges:

		/warpex-*         # can use /warp and /listwarps to global namespace
		/warpex-!         # can use /warp and /listwarps to secret namespace
		/warpex-modify-*  # also can use /setwarp and /removewarp to global namespace
		/warpex-modify-!  # also can use /setwarp and /removewarp to secret namespace

## Configuration

Edit bin/WarpEx/plugin.ini, and reload plugin.
See below about keys and default values.

### General

		warp-file         = data.txt	# file name that stored warp data
		default-namespace = Personal	# All commands use this setting when namespace is omitted.
		
### Command aliases

		warp-alias        = /go				# alias for /warp
		setwarp-alias     = /sw				# alias for /setwarp
		removewarp-alias  = /rw				# alias for /removewarp
		listwarps-alias   = /lw				# alias for /listwarps
		listns-alias      = /ln				# alias for /listns
		
### Hidden warp

		hidden-prefix     = @					# name prefix of hidden warp

## Namespaces

There are two special namespaces:

* Global namespace shown by *
* Secret namespace shown by !

All namespaces except these are treated as personal namespace.

## Commands (and aliases)

### /setwarp <namespace:>[warp]

Add current position to specified namespace.
Warp name must not contain blank, comma and semicolon.

### /removewarp <namespace:>[warp]

Remove warp from specified namespace.

### /warp <namespace:>[warp]

Move to warp position.

### /listwarps <namespace>

Show warps.

### /listns

Show all namespaces without global and secret.

## Download

[Latest version](https://github.com/palm3r/Hey0Plugins/raw/master/WarpEx/build/latest/WarpEx.jar)
