CS61b Class Projects
Hilfinger Class projects
Unique in class projects in that Hilfinger gave you a skeleton, gave you a desired functionality or product, and told you to get to work
Style checker that checked basic things like whitespace, line length, bracket positioning, camelcase, other basic java style guidelines
Lots of design choices to make

Proj0

German Enigma Encryption machines
First project, good for learning how class structures should work together, good refactoring of code.
Essentially fancy string manipulation
    Forward encrypt
        input: string, configuration
        output: encrpyted message
    Backwards decrypt:
        input: encrypted message, configuration
        output: decrypted message

Proj1

Simplified TEX editor
Matches regex patterns and other special character tokens to take into account spacing, indentation, styling, formatting, margins
another example of a project that really emphasized a lot of moving parts and how they all fit together
    Input parser parses input
    Line assembler assembles line from parsed input
    Page assembler assembles page
    Page collector aggregates all assembled pages and creates final document

Emphasized good testing, lots of edge cases based on particular patterns and lines. Stacked command tokens, whitespace + tabs, etc

Proj2

Solving Connect x

Project that delves into mad-reduce and using hadoop. Used amazon serves to calculate best possible moves for connect x up to 16x16

Working with command flow, created each player, the game board, and stored all possible moves.
Using min-max trees, calculate best possible move from every position, working from all possbile win conditions, going backwards to all initial starting positions.


Proj3

Simplified google maps, using A star algorithm and graphs
Definitely most challenging project that I've done in school
Lots of design choices to make, how to weigh which vectors effectively


