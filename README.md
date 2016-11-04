# Connect Four
A Libgdx application to play Connect Four with a simple defense-playing AI

This game was made using the Libgdx Graphics library for an internship coding challenge. Intern candidates were given a spec 
of required features that the application must have and 4 days to construct the game. There are two screens in this project:

**MainScreen** - Here, I make an API call to OpenWeatherMap API to display the current weather in San Francisco. I needed include Buttons and Labels to display my proficiency in Libgdx to my interviewers. I added the background and computer screen to make the UI more appealing.

**GameScreen** - This Screen shows the Connect 4 game board, a series of Button options, and game status notifications (eg. the game state, the current player, etc.). Users can click in the board region to drop pieces. This Screen controls the game logic with user click inputs and manipulates the one instance of GameBoard (see below).In addition, users can toggle the AI mode ON and OFF, allowing them to switch between playing the two-player mode or against my AI. 

# Implementation
I partitioned the game logic into two separate classes to abstract the game itself away from the controller, GameScreen. To manage all the assets, I created an Assets object to handle LibGdx's method of loading / getting game assets.

**GameBoard** - I isolated the game logic into the class GameBoard. This utlizes a 2D array to model the board, and self-sufficiently monitors the game status, adds pieces, and all other game logistics. 

**GameOptions** - This object stores all of the game settings in a single location. By changing a few variables in GameOptions before running the application, I can manipulate the board size, the number in a row needed to win, and toggle the AI mode. All calculations and game logic in the other classes are determined by the information stored in applicatons single instance of GameOptions.


# Screenshots
![alt text](/screenshots/main-screen.png?raw=true "Main Screen")

![alt text](/screenshots/gameplay-ai.png?raw=true "Game Screen")
