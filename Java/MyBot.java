// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.time.LocalDate;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class MyBot {
    public static void main(final String[] args) {
        final long rngSeed;
        if (args.length > 1) {
            rngSeed = Integer.parseInt(args[1]);
        } else {
            rngSeed = System.nanoTime();
        }
        final Random rng = new Random(rngSeed);

        Game game = new Game();
        // At this point "game" variable is populated with initial map data.
        // This is a good place to do computationally expensive start-up pre-processing.
        // As soon as you call "ready" function below, the 2 second per turn timer will start.

        // Get Halite Spawns
        int[][][] haliteAmount;
        int xCoordinate;
        int yCoordinate;
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy-HHmmss");  
        Date date = new Date(); 
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/dinof_000/Desktop/Halite/Java/haliteSpawns/HaliteSpawnsInfo-" + game.myId + "-" + formatter.format(date) + "-" + rngSeed + ".txt"), StandardCharsets.UTF_8))) {
            for (yCoordinate = 0; yCoordinate < game.gameMap.height; yCoordinate++) {
                for (xCoordinate = 0; xCoordinate < game.gameMap.width; xCoordinate++ ) {
                    int cellAmount = game.gameMap.at(new Position(xCoordinate,yCoordinate)).halite;
                    // haliteAmount = new int[cellAmount][xCoordinate][yCoordinate];
                    writer.write(cellAmount + " " + xCoordinate + " " + yCoordinate);
                    writer.newLine();
                    haliteAmount = new int[cellAmount][xCoordinate][yCoordinate];
                }
                xCoordinate = 0;
            }       
        }  
        catch (IOException ex) {
            // Handle me
        } 

        game.ready("JavaBois");

        Log.log("Successfully created bot! My Player ID is " + game.myId + ". Bot rng seed is " + rngSeed + ".");

        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;

            final ArrayList<Command> commandQueue = new ArrayList<>();

            for (final Ship ship : me.ships.values()) {
                if (gameMap.at(ship).halite < Constants.MAX_HALITE / 10 || ship.isFull()) {
                    final Direction randomDirection = Direction.ALL_CARDINALS.get(rng.nextInt(4));
                    commandQueue.add(ship.move(randomDirection));
                } else {
                    commandQueue.add(ship.stayStill());
                }
            }

            if (
                game.turnNumber <= 200 &&
                me.halite >= Constants.SHIP_COST &&
                !gameMap.at(me.shipyard).isOccupied())
            {
                commandQueue.add(me.shipyard.spawn());
            }

            game.endTurn(commandQueue);
        }
    }
}
