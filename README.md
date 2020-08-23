# lwjgl-engine
So yeah, this is the most recent version of my LWJGL 3D Engine. (Or 2D, it wouldn't be hard to implement...)
This is a full rewrite of the old version, which is archived in the deprecated-branch. This was neccessary, as the old engine was basically just a rendering engine
with an ECS and Asset Management glued on, which made the project basically unmaintainable.

## Some Info and Stuff
Most of the engine is centered around the AssetManager, an improvement over the last version. Also, the EntityComponentSystem is now fully integrated into the engine.
Maybe I'll write some more stuff here, someday...

## Sub-Projects
Right now this repo contains the following sub-projects, each with its' own resources root, and src-folder:
* Engine (Platform as resources root) - The engine itself.
* Editor - A simple 3D editor to test the engine.
* SoundWav - Something to experiment with OpenAL, contains most of the sound-related components / assets - this will be moved into the main engine at some point...

I kept the projects in one repo as I am too lazy to switch projects all the time.
