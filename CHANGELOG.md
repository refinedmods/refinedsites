# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Fixed

-   Snapshot components now get the latest version as current version in the releases overview.
-   Milestone and beta releases are now sorted correctly.

## [0.6.1] - 2025-03-29

### Fixed

-   Ability to set the snapshot component version as the latest one.

## [0.6.0] - 2025-03-29

### Added

-   Renderer for releases provides more information about release platforms now.

## [0.5.2] - 2025-03-27

### Fixed

-   Fixed inline images in a list item not loading.

## [0.5.1] - 2025-03-26

### Fixed

-   Fixed inline images not loading.

## [0.5.0] - 2025-03-26

### Added

-   Support for inline images and images in a table.
-   Multiple GitHub repositories are supported for a single component now.

### Changed

-   The GitHub snapshot branch is optional now for a component.

### Fixed

-   Page title escaping for special characters.
-   Blockquote styling.
-   Failing to build when using `xref` on a page that contains an image.

## [0.4.1] - 2024-08-16

### Fixed

-   Articles are retrieved from snapshot version if possible.
-   Playbook filename is now a command line argument. Default is `playbook.json`.

## [0.4.0] - 2024-08-15

### Added

-   Support for article pages.
-   Support for RSS feed generation for articles.

### Changed

-   Snapshot versions are no longer considered the latest version.

### Fixed

-   Asciidoc Xref bug on Windows.

## [0.3.0] - 2023-12-25

### Added

-   Support for images.

## [0.2.3] - 2023-12-24

### Fixed

-   Fixed insufficient logging.

## [0.2.2] - 2023-12-24

### Fixed

-   Fixed release version ordering.

## [0.2.1] - 2023-12-24

### Fixed

-   Fixed trying to generate sitemap for empty component.

## [0.2.0] - 2023-12-24

### Added

-   Added sitemap generation.

## [0.1.2] - 2023-12-23

### Fixed

-   Fixed Thymeleaf layout plugin not working with shadow JAR.

## [0.1.1] - 2023-12-23

### Fixed

-   Fixed JAR not being executable.

## [0.1.0] - 2023-12-23

### Added

-   Initial release.

[Unreleased]: https://github.com/refinedmods/refinedsites/compare/v0.6.1...HEAD

[0.6.1]: https://github.com/refinedmods/refinedsites/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/refinedmods/refinedsites/compare/v0.5.2...v0.6.0

[0.5.2]: https://github.com/refinedmods/refinedsites/compare/v0.5.1...v0.5.2

[0.5.1]: https://github.com/refinedmods/refinedsites/compare/v0.5.0...v0.5.1

[0.5.0]: https://github.com/refinedmods/refinedsites/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/refinedmods/refinedsites/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/refinedmods/refinedsites/compare/v0.3.0...v0.4.0

[0.3.0]: https://github.com/refinedmods/refinedsites/compare/v0.2.3...v0.3.0

[0.2.3]: https://github.com/refinedmods/refinedsites/compare/v0.2.2...v0.2.3

[0.2.2]: https://github.com/refinedmods/refinedsites/compare/v0.2.1...v0.2.2

[0.2.1]: https://github.com/refinedmods/refinedsites/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/refinedmods/refinedsites/compare/v0.1.2...v0.2.0

[0.1.2]: https://github.com/refinedmods/refinedsites/compare/v0.1.1...v0.1.2

[0.1.1]: https://github.com/refinedmods/refinedsites/compare/v0.1.0...v0.1.1

[0.1.0]: https://github.com/refinedmods/refinedsites/compare/32dcd996fc886487d1de05db803bb4e3e1bda6cd...v0.1.0
