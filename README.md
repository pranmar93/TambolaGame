# TambolaGame
Multiplayer Offline Tambola (Housie) Game using Google Nearby API.

This Game is built on Google's Nearby Connections API using P2P_STAR topology which supports 1-to-N connection topology. Multile players can play this game at once with one device acting as a server and other as clients.
All the clients join the hotspot created by the server device. 

The SERVER device begins the game with some game settings and adding relevant games/winning patterns. Once the games are added, the connection point is created by SERVER which is discoverable by other clients.
The clients can join to the SERVER which initiates the process of generating tickets for the client and providing then with all the necessary information about the game.

## How to PLAY with this app
1. First select a member who will be assigned as SERVER and rest will join as CLIENT.
2. SERVER will input all the game settings.
3. Next, SERVER has to add games which are to be played in this session of play. (All the game price should add up to the amount collected)
4. After creating all the games, the server is created.
5. CLIENT devices can discover SERVER device and join it.
6. As the CLIENT connects to the SERVER, tickets and other information such as games will be passed on by the SERVER.
7. Once all the members join, SERVER can start the game. (No one can join, once the game starts).
8. SERVER can generate random numbers with every one in complete sync.

## About Tambola Game
Each player must buy at least one ticket to enter a game. Tambola is played with Numbers (1-90) being called out one at a time and players striking out those Numbers on their Tickets. It contains 27 spaces, arranged in nine columns by three rows. Each row contains five numbers and four blank spaces. Each column contains up to three numbers.The first column contains numbers from 1 to 9, the second column numbers from 10 to 20, the third, 20 to 30 and so on up until the last column, which contains numbers from 80 to 90.
The game begins with a number drawn. As the game progresses, the ticket is marked with each number that is drawn. The objective of the game is to mark/ dab all the numbers found in the ticket as called by the dealer. The player who first mark all the numbers in a winning pattern and calls a win is declared as the WINNER of that pattern after the dealer checks his ticket and verify it with numbers drawn.

If your claimed winning pattern is wrong, it will be called BOGUS and you cannot continue the game with the same ticket (sometimes :)).

The game ends when all 90 numbers are drawn, or when a winner is declared for all the patterns of the game, whichever comes first.In order to win in Tambola or Bingo, you need to match winning combinations.
The winning patterns or the games vary depending on where the game is played.


