# Loki Render

This is a personal fork of [Loki Render][lr] (for distributed [Blender][b] rendering). 

It was originally forked in 2010 (at version 0.6.2) to add the following extra features:

* Manually specifying which master to connect to (since multicast will only get you so far), which was added to upstream in 2014 (shortly after this was published on GitHub)
* Wait until the master shows up if the connection is dropped (retrying every 30s)

The upstream source for version 0.7.2 was merged to this repository in October 2016, but the code is still untested. 

Please note that my interest in this is largely due to my needing a nice, CPU-heavy workload for testing cloud infrastructure orchestration (yes, it was cool even back in 2010). 

## TODO:

* [x] Merge upstream
* [ ] Remove NetBeans dependency (pick a simpler, CLI-only build system, even if just `ant`)
* [ ] Docker container (with [Blender][b] built in) 
* [ ] Automated/CI builds and testing

[b]: http://www.blender.org
[lr]: http://loki-render.berlios.de
