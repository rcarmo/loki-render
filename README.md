# Loki Render

This is a personal fork of [Loki Render][lr] that allows you to do distributed [Blender][b] rendering. It's currently (2014) fairly out of date, but it supports the following extra features:

* Manually specifying which master to connect to (since multicast will only get you so far)
* Wait until the master shows up if the connection is dropped (retrying every 30s)

Updating this to merge with the main project is on my TODO list -- fairly near the bottom, so pull requests are welcome.

[b]: http://www.blender.org
[lr]: http://loki-render.berlios.de
