
To compile using MSYS2 on Windows (probably also works on Linux)

* install Java and set path (in .bash_profile) so javac and co are found
* install binutils, p7zip, wget, ... so 'get-dependencies.sh' can run.
* run 'get-dependencies.sh' in the external folder.
* run 'make' in the main folder. It should successfully build the java part
* install 'clang' to get the C compiler
* run 'make' in bridgelibs: see next section


'bridgelibs' build the .dll / .so file that actually links Java and device code.
There is currently three versions to build it that the Makefile goes through:

* Building on Windows with gcc in MSYS2 / MINGW64
* Building on Windows with native Windows SDK / MSVC Visual Studio
* Building on Linux

For the Windows native build to succeed, set the paths in 'Makefile-path-windows'




