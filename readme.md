# Cascadia Game Simulation

[Watch the demo](https://vimeo.com/1081293219/1ab92ec63a?share=copy)

[cascadia online](https://cascadiagame.github.io/)

[cascadia game rules](https://drive.google.com/file/d/1fY8-__M2f0QSxvBi0P2oycG6F0l1yJiI/view)

## Overview
This project is a Java-based simulation of the *Cascadia* board game, a tile-placement and token-drafting game set in the Pacific Northwest. The game supports 1 to 4 players and offers three modes:
- **Terminal Mode** with square tiles.
- **Graphical Mode** with square tiles.
- **Graphical Mode** with hexagonal tiles (classic Cascadia style).

The project is structured to follow the Model-View-Controller (MVC) pattern, utilizing the Zen6 graphical library for rendering. It includes features like solo mode, scoring variants, and achievement tracking.

## Features
- **Game Modes**: Play in terminal or graphical interfaces with square or hexagonal tiles.
- **Scoring Variants**: Choose from Family, Intermediate, or Wildlife Card scoring methods.
- **Tile Mechanics**: Square tiles (single habitat, two animals, no rotation) and hexagonal tiles (1-2 habitats, 1-3 animals, supports rotation).
- **Nature Tokens**: Use tokens for strategic actions like swapping tiles or tokens.
- **Achievements**: Track scenario-based and normal game achievements saved in `ScenarioAchievements.txt` and `GameAchievements.txt`.
- **Solo Mode**: Unique repioche mechanics for single-player games.

## Requirements
- **Java**: JDK 23 or higher.
- **Zen6 Library**: For graphical rendering.
- **Apache Ant**: For building the project.

## Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   ```
2. Navigate to the project directory:
   ```bash
   cd cascadia
   ```
3. Ensure the Zen6 library is included in the project dependencies.
4. Build the project using Apache Ant:
   ```bash
   ant compile
   ant jar
   ```

## Usage
Run the game using the generated JAR file:
```bash
java -jar Cascadia.jar
```

Upon launching, you will be prompted to:
1. Select the number of players (1-4).
2. Choose tile type (Square or Hexagonal).
3. Select a scoring variant (Family, Intermediate, or Wildlife Cards).
4. Enter player names.
5. Choose display mode (Terminal or Graphical).

### Game Flow
- **Turn Summary**:
  1. Select a Habitat Tile and Wildlife Token pair from four available options.
  2. Handle overpopulation (e.g., replace three identical tokens if desired).
  3. Place the tile adjacent to an existing tile in your environment.
  4. Place the token on a compatible tile.
  5. Optionally use Nature Tokens for strategic actions.
- **End Game**: The game ends after 26 turns (20 in solo mode). Scores are calculated based on wildlife, habitats, majority bonuses, and Nature Tokens.

### Controls
- **Terminal Mode**: Input choices via text (e.g., 0-3 for tile selection, `y/n` for token replacement).
- **Graphical Mode (Square)**:
  - Right-click to select tile/token pairs and place them.
  - Press `Y` or `N` to handle overpopulation.
- **Graphical Mode (Hexagonal)**:
  - Left-click to select tiles/tokens.
  - Use arrow keys to rotate tiles.
  - Press `Space` to confirm tile placement.
  - Press `J` or `T` to use Nature Tokens, `N` to decline.

## Project Structure
The project is organized into packages:
- **fr.uge.cascadia**: Core game logic (`Game.java`, `Player.java`).
- **fr.uge.cascadia.board**: Manages the game board and tile placement (`Board.java`, `Shelf.java`).
- **fr.uge.cascadia.cachroller**: Handles user interaction and game loops (`GameInterface.java`, `ControllerGraphic.java`, `ControllerTerminal.java`, `GameManager.java`).
- **fr.uge.cascadia.tile**: Tile representations (`TileType.java`, `Habitat.java`, `Tile.java`, `SquareTile.java`, `HexagoTile.java`).
- **fr.uge.cascadia.animal**: Manages wildlife tokens and scoring cards (`Animal.java`, `AnimalToken.java`, `CardType.java`).
- **fr.uge.cascadia.score**: Score calculation (`Score.java`, animal-specific scoring classes, `ScoringCard.java`, `ScoringStrategy.java`).
- **fr.uge.cascadia.view**: Graphical rendering (`GameView.java`, `ViewUtils.java`, `ImageLoader.java`).
- **fr.uge.cascadia.success**: Achievement tracking (`ScenarioSuccess.java`, `GameSuccess.java`, `SuccessManager.java`, `SuccessUtils.java`).

### Data Files
- `hexagoTilesFile.txt`: Hexagonal tile configurations.
- `initialTiles.txt`: Initial tile setups.
- `hex/`: Images for hexagonal tiles.
- `data/`: Images for square tiles and wildlife tokens.
- `Scenarios.txt`: Predefined scenarios.
- `successNormal.txt`: Standard achievements.

## Build Commands
Use the `build.xml` file with Apache Ant:
- Compile sources: `ant compile`
- Create JAR: `ant jar`
- Generate Javadoc: `ant javadoc`
- Clean project: `ant clean`

## Challenges and Improvements
### Challenges
- Managing hexagonal grid logic.
- Ensuring seamless transitions between terminal and graphical modes.
- Accurate score calculation and achievement validation.
- Time management during development.

### Future Improvements
- Add online multiplayer mode.
- Integrate AI for solo mode.

## Acknowledgments
- The `ImageLoader.java` class was adapted from an example provided by the instructor.
- Thanks to the teaching staff for guidance on hexagonal tile implementation.

## License
This project is for educational purposes and is not licensed for commercial use.
