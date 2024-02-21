# RealEnvSync

RealEnvSync is a Minecraft 1.20.4 Spigot plugin that syncs the Minecraft environment with the real world.

## Features

Currently implemented features work only on command execution.

- Sync the time of day with the real world
- Sync the moon phase with the real world

### Planned

To make the plugin work automatically as well, when and how is specified in the config file.

- WIP: Sync the weather with the real world based on the location specified in the config file using API;
- WIP: Sync the sun and moon position with the real world based on the location specified in the config file using API.

## Installation

1. Download the latest .jar file;
2. Place the .jar file in the `plugins` folder of your Spigot server;
3. Start the server.

## Usage

`/realenvsync`

- `time` - Set the time of day;
    - `now` - ... to the current time of day in the real world;
    - `hh:mm` - ... to the specified time of day in the real world;
    - `hh:mm:ss` - same as above.
- `datetime` - Set the time of day and the moon phase (full time);
    - `now` - ... to the current time of day in the real world;
    - `YYYY-MM-DDThh:mm` - ... to the specified time of day in the real world;
    - `YYYY-MM-DDThh:mm:ss` - same as above.

## Development

1. Clone the repository;
2. Open the project in your IDE of choice;
3. Build the project using Gradle `build` task;
4. The .jar file will be located in the `build/libs` folder.

If automatic testing is required, run the `test` task.

## Configuration

Not yet.
