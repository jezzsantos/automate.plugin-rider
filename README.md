![Icon](https://raw.githubusercontent.com/jezzsantos/automate.plugin-rider/main/src/main/resources/META-INF/pluginIcon.svg)

# automate Rider Plugin

This is an interactive plugin for Jetbrains Rider (Rider only) that integrates [automate](https://github.com/jezzsantos/automate) into a coding IDE. 

> Plugins for other popular IDE's may be supported in the future, once we have a proven effective design experience in Rider

INSTALLATION: The plugin is hosted from the [Jetbrains Marketplace](https://plugins.jetbrains.com/plugin/19421-automate) and installed by adding the plugin with within Rider.

[Documentation](https://jezzsantos.github.io/automate/) for the plugin.

## How It Works

The plugin has to be written in Java (we are using IntelliJ), but the core runtime of automate (`automate.Core`) is written in .NET. 

### Interop

Since the [automate project](https://github.com/jezzsantos/automate) is itself in its early stages of development, thus maintaining two rapidly evolving codebases (one in .NET and one in Java) will be unecessary maintenance, we will necessarily take another approach. 

There are today several options around interop between Java and .NET (e.g. [JNBridge](https://jnbridge.com/), [JNI4NET](http://jni4net.com/), and [JCOBridge](https://www.jcobridge.com/)), however, to reduce dependencies, and incuring extra license costs, we are choosing to interop through the already existing [CLI interface into automate](https://www.nuget.org/packages/automate). 

So, the first iterations of the plugin will simply:

1. Install the automate CLI locally (using `dotnet tool install --global automate`)
1. Execute the all commands through the CLI
1. Capturing all the outputs of the CLI (using the `--output-structured` option) which delivers output in structured JSON.

> This approach may require better support from the automate CLI, but, at this point, this is thought to be rather more sustainable than maintaining the same code in two different codebases (Java and .NET).  

## Developing the Plugin

- Set the environment variable (in your Terminal) `JAVA_HOME` to your local installation of the `corretto-17.0.3` JDK. Which this project currently uses. By default IntelliJ installs to (on Windows): `%userprofile%\.jdks\corretto-17.0.3`.

### Build the Code

- `./gradlew :buildPlugin -PbuildType=stable`

### Run or Debug

- Start the `Plugin Stable` run configuration


### Contributing

All contributions and suggestions are very welcome.

Join the [Discussion](https://discord.gg/vpc3gDPR) on Discord

The process for [versioning and releasing this plugin](https://github.com/jezzsantos/automate.plugin-rider/wiki/Contributor-Notes) is to be found in the wiki.
