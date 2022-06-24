![Icon](https://raw.githubusercontent.com/jezzsantos/automate.plugin-rider/main/src/main/resources/META-INF/pluginIcon.svg)

# automate Rider Plugin

This is an interactive plugin for JetBrains Rider (Rider only) that integrates [automate](https://github.com/jezzsantos/automate) into a coding IDE. 

> Plugins for other popular IDE's may be supported in the future, once we have a proven effective design experience in Rider

INSTALLATION: The plugin is hosted from the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/19421-automate) and installed by adding the plugin with within Rider.

## How It Works

The plugin has to be written in Java (we are using IntelliJ), but the core runtime of automate (`automate.Core`) is written in .NET. 

### Interop

Since the [automate project](https://github.com/jezzsantos/automate) is itself in its early stages of development, thus maintaining two rapidly evolving codebases (one in .NET and one in Java) will be unnecessary maintenance, we will necessarily take another approach. 

There are today several options around interop between Java and .NET (e.g. [JNBridge](https://jnbridge.com/), [JNI4NET](http://jni4net.com/), and [JCOBridge](https://www.jcobridge.com/)), however, to reduce dependencies, and incurring extra license costs, we are choosing to interop through the already existing [CLI interface into automate](https://www.nuget.org/packages/automate). 

So, the first iterations of the plugin will simply:

1. Install the automate CLI locally (using `dotnet tool install --global automate`)
1. Execute the all commands through the CLI
1. Capturing all the outputs of the CLI (using the `--output-structured` option) which delivers output in structured JSON.

> This approach may require better support from the automate CLI, but, at this point, this is thought to be rather more sustainable than maintaining the same code in two different codebases (Java and .NET).  


## More...

Read our [Documentation](https://jezzsantos.github.io/automate/)

What to contribute? We sure welcome you!

See our [Contributing Guidelines](https://github.com/jezzsantos/automate.plugin-rider/blob/main/CONTRIBUTING.md).

Join the [Discussion](https://discord.gg/vpc3gDPR) on Discord
