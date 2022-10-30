[![Build Test](https://github.com/jezzsantos/automate.plugin-rider/actions/workflows/build.yml/badge.svg)](https://github.com/jezzsantos/automate.plugin-rider/actions/workflows/build.yml)
[![Download](https://img.shields.io/badge/JetBrains-marketplace-orange?style=flat&logo=rider)](https://plugins.jetbrains.com/plugin/19421-automate)
![Icon](https://raw.githubusercontent.com/jezzsantos/automate.plugin-rider/main/src/main/resources/META-INF/pluginIcon.svg)

# automate Rider Plugin

This is an interactive plugin for JetBrains Rider that integrates [automate](https://github.com/jezzsantos/automate)
into an IDE.

> Plugins for other popular IDE's may be supported in the future, once we have a proven effective design experience in
> Rider. Rider is the first step, and the easiest to integrate since we have a dependence on the dotnet runtime to
> execute
> the CLI.

INSTALLATION: The plugin is found in the [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/19421-automate)
and installed from within Rider (File | Settings | Plugins | Marketplace).

## How It Works

The plugin essentially provides a GUI over the CLI, proffering all the same functionality of the automate CLI,
and also improves on the user's experience of making and using toolkits, for code bases that are developed in a Rider
IDE.

The plugin has to be written in Java (since we are creating a plugin for the IntelliJ IDEA), but the core runtime of
automate (`automate.Core`) is written in .NET, and is currently deployed on the machine in a cross-platform CLI.

Since the plugin requires integration between the IDE and the CLI, the CLI needs to be reliably installed to the local
machine, in a well-known location, and will need to kept in sync (version compatibility) with the plugin, as both the
CLI and plugin evolve. The CLI is cross-platform, but still requires the dotnet runtime to be installed
on the local machine.

### Interop Design

Since the [automate project](https://github.com/jezzsantos/automate) is itself in its early stages of development,
maintaining two rapidly evolving code bases (one in .NET and one in Java) will be unnecessary maintenance, we will
necessarily take another approach.

There are today several options around interop between Java and .NET (e.g. [JNBridge](https://jnbridge.com/)
, [JNI4NET](http://jni4net.com/), and [JCOBridge](https://www.jcobridge.com/)), however, to reduce dependencies, and
incurring extra license costs, we are choosing to interop through the already
existing [CLI interface into automate](https://www.nuget.org/packages/automate).

So, the first iterations of the plugin will simply:

1. Install the automate CLI locally (using `dotnet tool install --global automate`)
2. Execute the all commands through the CLI by spawning a new process.
3. Capturing all the outputs of the CLI (using the `--output-structured` option) which delivers output in structured
   JSON.

> This approach requires heavy support from the automate CLI, but, at this point, this is thought to be rather more
> sustainable than maintaining the same code in two different code bases (Java and .NET).

## More...

Read our [Documentation](https://jezzsantos.github.io/automate/)

What to contribute? We sure welcome you!

See our [Contributing Guidelines](https://github.com/jezzsantos/automate.plugin-rider/blob/main/CONTRIBUTING.md).

Join the [Discussion](https://discord.gg/vpc3gDPR) on Discord
