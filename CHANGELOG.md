# Changelog

All notable changes to this project are documented in this file.

> The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
> to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Notes

### Added

### Changed

### Deprecated

### Removed

### Fixed

### Security

## [1.7.0] - 2024-11-14

### Changed

- Updated compatibility up to Rider 2024.4

## [1.6.0] - 2024-10-30

### Changed

- Updated compatibility up to Rider 2024.2
- replaced all obsolete APIs
- Added more information for user getting started with no toolkits installed.

## [1.5.0] - 2024-10-20

### Changed

- Updated compatibility for Rider 2024.1
- Updated to Java 21
- replaced all obsolete APIs

## 1.4.0 - 2024-10-13

### Changed

- Updated compatibility upto Rider 2024.1

## 1.3.0 - 2024-01-19

### Changed

- Updated compatibility upto Rider 2023.3

## 1.2.0 - 2023-07-02

### Fixed

- Improved the look of some dialog boxes
- Patterns: #46. Attributes that have choices but are not required now do not block with validation message
- Patterns: #44. Changed title of the action to edit the content of a code template
- Drafts: #39. Highlighted common property names (such as Id and Name)
- General: #42. Added visible progress indicators when communicating with CLI for long-running tasks
- General: #43. Fixed the problem with sharing context between different solution in separate instances of the IDE

## 1.1.4 - 2023-05-15

### Notes

- Minimum compatibility with `automate` CLI version 1.2.0

### Added

- Patterns: #7. Added, deleting new code templates
- Patterns: #7. Editing the contents of a code template in the IDE
- Patterns: #7. Adding, editing, deleting code-template-commands and CLI-commands and launch-points

### Fixed

- Updated the compatibility of this plugin with Rider versions 222 -> onwards

## 1.1.3 - 2022-12-11

### Notes

- Minimum compatibility with `automate` CLI version 1.1.0

### Fixed

- DevOps: Closed #34. Fixed incompatibility issues with RD 223.7571.232

## 1.1.2 - 2022-11-19

### Notes

- Minimum compatibility with `automate` CLI version 1.1.0

### Fixed

- General: Closed #32. Automatic installation on macOS and Linux now works

## 1.1.1 - 2022-11-13

### Notes

- Minimum compatibility with `automate` CLI version 1.1.0

### Fixed

- General: Closed #32. Automatic installation on macOS and Linux no longer relies on the path being set when run from
  Dock (in macOS)
- General: Null exception at runtime with null `ICorrelationBuilder` instance.

## 1.1.0 - 2022-11-06

### Notes

- Minimum compatibility with `automate` CLI version 1.1.0

### Added

- Patterns: Closed #6. Add, Edit and Delete of elements and update of pattern

### Fixed

- General: Closed #31. Fixed accessible colors throughout UI
- General: Closed #30. CLI versions pre-1.1.0 were not working for MacOS and Linux, due to local file limitations.
  see: https://github.com/jezzsantos/automate/issues/88

## 1.0.7 - 2022-10-29

### Notes

- Minimum compatibility with `automate` CLI version 1.0.7

### Added

- DevOps: Added recorder interface for tracing and exception handling
- DevOps: Closed #20. Added usage collection to Application Insights

## 1.0.6 - 2022-10-15

### Notes

- Minimum compatibility with `automate` CLI version 1.0.5

### Added

- Patterns: Closed #8. Can now publish patterns
- Drafts: Closed #17. Can now upgrade drafts and toolkits (if possible)
- Drafts: Closed #18. Can now delete drafts (including out of date drafts)

## 1.0.5 - 2022-10-03

### Notes

- Minimum compatibility with `automate` CLI version 1.0.4

### Added

- Drafts: Closed #5. Out of date drafts, can now be upgraded.
- DevOps: Integration tests can now be run

### Fixed

- Patterns: Fixed #16. Can now add new attributes to a pattern
- Docs: Instructions for installing plugin and CLI updated.

## 1.0.4 - 2022-09-29

### Notes

- Minimum compatibility with `automate` CLI version 1.0.2

### Added

- General: Closed #2. Self-installs automate CLI tool on the local machine
- General: Closed #2. Auto-upgrades compatible version of automate CLI on the local machine
- Drafts: Advanced #5. Added menu items for running commands configured on drafts.
- Patterns: Fixed #13. Added editing of choices for attributes.

### Fixed

- Drafts: Fixed #10. Able to edit draft properties that have no value.
- General: Fixed #11. Moved settings to non-source controlled persistence.

## 1.0.3

### Fixed

- General: Fixed #9. CLI commands are no longer wrapped in double-quotes, which do not work reliably on macOS or Linux
- Drafts: Fixed #4. Adding a new draft or selecting another draft does not update the selected item in the dropdown list
  in the toolbar.

## 1.0.2 - 2022-09-20

### Notes

- (First public stable release)
- Minimum compatibility with `automate` CLI version 1.0.1

### Added

- Introduces a tool window for viewing toolkits, and drafts
- Drafts: Creation of drafts
- Drafts: Viewing of drafts
- Drafts: Editing of drafts
- Patterns: Creation of patterns
- Patterns: Editing of pattern attributes (only)
- Settings: Editing of installation location of automate.exe executable
- Settings: Viewing of CLI commands
- Settings: Added "Authoring Mode"

## 1.0.1-Unstable - 2022-06-24

### Notes

- (initial version - not released to public)
