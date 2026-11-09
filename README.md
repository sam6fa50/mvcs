# MVCS

## Minecraft Version Control
This is a small mod that adds version control to Minecraft. It's a personal project to primarily explore Minecraft's source code in a scoped way, existing version control systems (primarily Git), and efficient storage paradigms like Copy on Write.

## Architecture
There are two architectures I am currently attempting to filter, one of which is a callback based logging mechanism that essentially constructs changes as patches as time goes on. This is the one that is being implemented right now.

The other architecture I am thinking of, which has not yet been started, is a vanilla Minecraft inspired save system that essentially saves on commits, keeps tracking shallow for diagnostics and potential conflicts/beneficial use cases for said tracking, that utilizes a custom/semi-custom storage streaming mechanism to store game data in a format superior for precompression diffing, allowing chunk level diffs because the lack (or adequate minimization) of initial compression allows an easy byte by byte comparison. Changes can then be tracked using a regular .mca file, but with some caution taken to ensure duplication doesn't occur.

## License

Undecided (Likely MIT) 

## Legal Notice
MVCS is an independently developed modification for Minecraft: Java Edition.

MVCS is not affiliated with, endorsed by, sponsored by, or officially associated with Mojang Studios, Mojang AB, Microsoft Corporation, Xbox Game Studios, or any of their affiliates. All rights to Minecraft, and Minecraft: Java Edition, and their associated intellectual property remains with Mojang and Microsoft.

The Git project is inspiration for the project, but beyond that, MVCS is not associated with the Git Project and is an indepedent project without any affiliation, endorsement, or sponsorship by the Git project or Software Freedom Conservancy.
