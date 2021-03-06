package org.terasology.oniatussmallgames.menschaergeredichnicht;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.terasology.oniatussmallgames.menschaergeredichnicht.BoardPositions.*;

public class MenschAergereDichNichtGameTest {

    public static final int DICE_RESULT = 6;
    private MenschAergereDichNichtGame game;

    @Before
    public void setUp() throws Exception {
        game = new MenschAergereDichNichtGame();
    }

    @Test
    public void shouldInitializeStartingGameState() throws Exception {
        verifySpawnPosition(game, OFFSET_SPAWN_GREEN, PlayerColor.GREEN);
        verifySpawnPosition(game, OFFSET_SPAWN_YELLOW, PlayerColor.YELLOW);
        verifySpawnPosition(game, OFFSET_SPAWN_RED, PlayerColor.RED);
        verifySpawnPosition(game, OFFSET_SPAWN_BLUE, PlayerColor.BLUE);
        assertEquals(PlayerColor.GREEN, game.getPlayerColorOnTurn());
    }

    @Test
    public void shouldAllowToMovePieceFromGreenSpawnOnSix() throws Exception {
        List<GameAction> possibleActions = game.findPossibleActions(6);

        GameAction pieceOneAction = possibleActions.get(0);
        verifyGameActionFromTo(pieceOneAction, 1, 17);
        GameAction pieceTwoAction = possibleActions.get(1);
        verifyGameActionFromTo(pieceTwoAction, 2, 17);
        GameAction pieceThreeAction = possibleActions.get(2);
        verifyGameActionFromTo(pieceThreeAction, 3, 17);
        ;
        GameAction pieceFourAction = possibleActions.get(3);
        verifyGameActionFromTo(pieceFourAction, 4, 17);
    }

    @Test
    public void shouldForbidToLeaveSpawnOnOneToFive() throws Exception {
        for (int diceResult = 1; diceResult <= 5; diceResult++) {
            game = new MenschAergereDichNichtGame();
            assertTrue(game.findPossibleActions(diceResult).isEmpty());
        }
    }

    @Test
    public void shouldChangePlayerAfterThreeAttemptsToLeaveSpawn() throws Exception {
        game.findPossibleActions(1);
        assertEquals(PlayerColor.GREEN, game.getPlayerColorOnTurn());
        game.findPossibleActions(2);
        assertEquals(PlayerColor.GREEN, game.getPlayerColorOnTurn());
        game.findPossibleActions(3);
        assertEquals(PlayerColor.YELLOW, game.getPlayerColorOnTurn());
    }

    @Test
    public void shouldCycleThroughPlayers() throws Exception {
        failToLeaveSpawnThreeTimes();
        assertEquals(PlayerColor.YELLOW, game.getPlayerColorOnTurn());
        failToLeaveSpawnThreeTimes();
        assertEquals(PlayerColor.BLUE, game.getPlayerColorOnTurn());
        failToLeaveSpawnThreeTimes();
        assertEquals(PlayerColor.RED, game.getPlayerColorOnTurn());
        failToLeaveSpawnThreeTimes();
        assertEquals(PlayerColor.GREEN, game.getPlayerColorOnTurn());
    }

    @Test
    public void shouldSpawnYellowPiecesOnSix() throws Exception {
        game.setPlayerOnTurnColor(PlayerColor.YELLOW);
        List<GameAction> possibleActions = game.findPossibleActions(6);
        verifyGameActionFromTo(possibleActions.get(0), 5, 27);
        verifyGameActionFromTo(possibleActions.get(1), 6, 27);
        verifyGameActionFromTo(possibleActions.get(2), 7, 27);
        verifyGameActionFromTo(possibleActions.get(3), 8, 27);
    }

    @Test
    public void shouldSpawnBluePiecesOnSix() throws Exception {
        game.setPlayerOnTurnColor(PlayerColor.BLUE);
        List<GameAction> possibleActions = game.findPossibleActions(6);
        verifyGameActionFromTo(possibleActions.get(0), 13, 37);
        verifyGameActionFromTo(possibleActions.get(1), 14, 37);
        verifyGameActionFromTo(possibleActions.get(2), 15, 37);
        verifyGameActionFromTo(possibleActions.get(3), 16, 37);
    }

    @Test
    public void shouldSpawnRedPiecesOnSix() throws Exception {
        game.setPlayerOnTurnColor(PlayerColor.RED);
        List<GameAction> possibleActions = game.findPossibleActions(6);
        verifyGameActionFromTo(possibleActions.get(0), 9, 47);
        verifyGameActionFromTo(possibleActions.get(1), 10, 47);
        verifyGameActionFromTo(possibleActions.get(2), 11, 47);
        verifyGameActionFromTo(possibleActions.get(3), 12, 47);
    }

    @Test
    public void shouldMovePieceToSpawn() throws Exception {
        List<GameAction> possibleActions = game.findPossibleActions(6);
        game.execute(possibleActions.get(0));
        assertEquals(17, game.getPiecePosition(PlayerColor.GREEN, 0));
    }

    @Test
    public void shouldAllowOnlyPiecesOnBoardToMoveWithLessThanSix() throws Exception {
        game.teleportPiece(1, 17);
        List<GameAction> possibleActions = game.findPossibleActions(1);
        assertEquals(1, possibleActions.size());
        assertEquals(17, possibleActions.get(0).getFromPosition());
        assertEquals(18, possibleActions.get(0).getToPosition());
    }

    @Test
    public void shouldGiveAnExtraMoveAfterLeavingSpawn() throws Exception {
        PlayerColor firstPlayerOnTurn = game.getPlayerColorOnTurn();
        leaveSpawn();
        PlayerColor playerOnTurnAfterLeavingSpawn = game.getPlayerColorOnTurn();
        assertEquals(firstPlayerOnTurn, playerOnTurnAfterLeavingSpawn);
    }

    private void leaveSpawn() {
        List<GameAction> possibleActions = game.findPossibleActions(6);
        game.execute(possibleActions.get(0));
    }

    @Test
    public void shouldMoveToNextPlayerAfterReqularMove() throws Exception {
        PlayerColor firstPlayerOnTurn = game.getPlayerColorOnTurn();
        leaveSpawn();
        List<GameAction> possibleActions = game.findPossibleActions(1);
        game.execute(possibleActions.get(0));
        PlayerColor nextPlayerOnTurn = game.getPlayerColorOnTurn();
        assertNotEquals(firstPlayerOnTurn, nextPlayerOnTurn);
    }

    @Test
    public void shouldHaveExtraMovesOnSix() throws Exception {
        Piece piece = game.teleportPiece(1, 18);
        executeActionForPiece(piece, game.findPossibleActions(6));
        executeActionForPiece(piece, game.findPossibleActions(6));
        assertEquals(18 + 6 + 6, game.getPiecePosition(piece.getPlayerColor(), piece.getIndex()));
        assertEquals(piece.getPlayerColor(), game.getPlayerColorOnTurn());
    }

    @Test
    public void shouldNotAllowMoveOnSamePosition() throws Exception {
        game.teleportPiece(9, 50);
        game.teleportPiece(10, 51);
        game.setPlayerOnTurnColor(PlayerColor.RED);
        List<GameAction> possibleActions = game.findPossibleActions(1);
        assertEquals(1, possibleActions.size());
        GameAction gameAction = possibleActions.get(0);
        assertEquals(51, gameAction.getFromPosition());
    }

    @Test
    public void shouldCaptureEnemyPiecesWhenLeavingSpawn() throws Exception {
        Piece yellowPiece = game.teleportPiece(5, 17);
        leaveSpawn();
        assertEquals(5, game.getPiecePosition(yellowPiece.getPlayerColor(), yellowPiece.getIndex()));
        assertEquals(17, game.getPiecePosition(PlayerColor.GREEN, 0));
    }

    @Test
    public void shouldCaptureEnemyPiecesWhileMovingOnTheBoard() throws Exception {
        Piece yellowPiece = game.teleportPiece(5, 19);
        Piece greenPiece = game.teleportPiece(1, 18);
        List<GameAction> possibleActions = game.findPossibleActions(1);
        executeActionForPiece(greenPiece, possibleActions);
        assertEquals(5, game.getPiecePosition(yellowPiece));
    }

    @Test
    public void shouldMoveGreenInHouse() throws Exception {
        Piece greenPiece = game.teleportPiece(1, 56);
        game.execute(game.findPossibleActions(1).get(0));
        assertEquals(57, game.getPiecePosition(greenPiece.getPlayerColor(), greenPiece.getIndex()));
    }

    @Test
    public void shouldForbidGreenHouseForOtherColor() throws Exception {
        Piece redPiece = game.teleportPiece(9, 56);
        game.setPlayerOnTurnColor(PlayerColor.RED);
        game.execute(game.findPossibleActions(1).get(0));
        assertEquals(17, game.getPiecePosition(redPiece.getPlayerColor(), redPiece.getIndex()));
    }

    @Test
    public void shouldMoveYellowInHouse() throws Exception {
        Piece yellowPiece = game.teleportPiece(5, 26);
        game.setPlayerOnTurnColor(PlayerColor.YELLOW);
        executeActionForPiece(yellowPiece, game.findPossibleActions(1));
        assertEquals(61, game.getPiecePosition(yellowPiece));
    }

    @Test
    public void shouldForbidYellowHouseForOtherColor() throws Exception {
        Piece bluePiece = game.teleportPiece(13, 26);
        game.setPlayerOnTurnColor(PlayerColor.BLUE);
        executeActionForPiece(bluePiece, game.findPossibleActions(1));
        assertEquals(27, game.getPiecePosition(bluePiece));
    }

    @Test
    public void shouldMoveBlueInHouse() throws Exception {
        Piece bluePiece = game.teleportPiece(13, 36);
        game.setPlayerOnTurnColor(PlayerColor.BLUE);
        executeActionForPiece(bluePiece, game.findPossibleActions(1));
        assertEquals(65, game.getPiecePosition(bluePiece));
    }

    @Test
    public void shouldForbidBlueHouseForOtherColor() throws Exception {
        Piece greenPiece = game.teleportPiece(1, 36);
        executeActionForPiece(greenPiece, game.findPossibleActions(1));
        assertEquals(37, game.getPiecePosition(greenPiece));
    }

    @Test
    public void shouldMoveRedInHouse() throws Exception {
        Piece redPiece = game.teleportPiece(9, 46);
        game.setPlayerOnTurnColor(PlayerColor.RED);
        executeActionForPiece(redPiece, game.findPossibleActions(1));
        assertEquals(69, game.getPiecePosition(redPiece));
    }

    @Test
    public void shouldForbidRedHouseForOtherColor() throws Exception {
        Piece greenPiece = game.teleportPiece(1, 46);
        executeActionForPiece(greenPiece, game.findPossibleActions(1));
        assertEquals(47, game.getPiecePosition(greenPiece));
    }

    @Test
    public void shouldForbidJumpingOverOwnPiecesInHouse() throws Exception {
        game.teleportPiece(1, 57);
        game.teleportPiece(2, 58);
        game.teleportPiece(3, 59);
        game.teleportPiece(4, 56);
        //pieces in house cant move and piece on 56 is not allowed to jump over other pieces to 60
        List<GameAction> possibleActions = game.findPossibleActions(4);
        assertEquals(0, possibleActions.size());
    }

    @Test
    public void shouldAllowGreenToWinTheGame() throws Exception {
        MenschAergereDichNichtGameListener listener = mock(MenschAergereDichNichtGameListener.class);
        game.registerListener(listener);
        Piece pieceInFrontOfHouse = game.teleportPiece(1, 56);
        game.teleportPiece(2, 58);
        game.teleportPiece(3, 59);
        game.teleportPiece(4, 60);

        List<GameAction> possibleActions = game.findPossibleActions(1);
        executeActionForPiece(pieceInFrontOfHouse, possibleActions);

        verify(listener).onPlayerWon(eq(PlayerColor.GREEN));
    }

    @Test
    public void shouldAllowYellowToWinTheGame() throws Exception {
        MenschAergereDichNichtGameListener listener = mock(MenschAergereDichNichtGameListener.class);
        game.registerListener(listener);
        game.setPlayerOnTurnColor(PlayerColor.YELLOW);
        Piece pieceInFrontOfHouse = game.teleportPiece(5, 26);
        game.teleportPiece(6, 62);
        game.teleportPiece(7, 63);
        game.teleportPiece(8, 64);

        List<GameAction> possibleActions = game.findPossibleActions(1);
        executeActionForPiece(pieceInFrontOfHouse, possibleActions);

        verify(listener).onPlayerWon(eq(PlayerColor.YELLOW));
    }

    @Test
    public void shouldAllowRedToWinTheGame() throws Exception {
        MenschAergereDichNichtGameListener listener = mock(MenschAergereDichNichtGameListener.class);
        game.registerListener(listener);
        game.setPlayerOnTurnColor(PlayerColor.RED);
        Piece pieceInFrontOfHouse = game.teleportPiece(9, 46);
        game.teleportPiece(10, 70);
        game.teleportPiece(11, 71);
        game.teleportPiece(12, 72);

        List<GameAction> possibleActions = game.findPossibleActions(1);
        executeActionForPiece(pieceInFrontOfHouse, possibleActions);

        verify(listener).onPlayerWon(eq(PlayerColor.RED));
    }

    @Test
    public void shouldAllowBlueToWinTheGame() throws Exception {
        MenschAergereDichNichtGameListener listener = mock(MenschAergereDichNichtGameListener.class);
        game.registerListener(listener);
        game.setPlayerOnTurnColor(PlayerColor.BLUE);
        Piece pieceInFrontOfHouse = game.teleportPiece(13, 36);
        game.teleportPiece(14, 66);
        game.teleportPiece(15, 67);
        game.teleportPiece(16, 68);

        List<GameAction> possibleActions = game.findPossibleActions(1);
        executeActionForPiece(pieceInFrontOfHouse, possibleActions);

        verify(listener).onPlayerWon(eq(PlayerColor.BLUE));
    }

    @Test(timeout = 5000)
    public void smokeTest() throws Exception {
        int numberOfGames = 500;
        Map<PlayerColor, Integer> wins = new EnumMap<>(PlayerColor.class);
        Arrays.stream(PlayerColor.values()).forEach(color -> wins.put(color, 0));
        for (int i = 0; i < numberOfGames; i++) {
            long seed = i;
            PlayerColor winnerColor = playNewGameUntilPlayerWins(seed);
            wins.put(winnerColor, wins.get(winnerColor) + 1);
        }
        wins.forEach((key, value) -> {
            assertTrue(value > 0);
        });
    }

    private PlayerColor playNewGameUntilPlayerWins(long seed) {
        game = new MenschAergereDichNichtGame();
        WinnerListener winnerListener = new WinnerListener();
        game.registerListener(winnerListener);
        Random random = new Random(seed);
        while (winnerListener.winnerColor == null) {
            List<GameAction> possibleActions = game.findPossibleActions(random.nextInt(6) + 1);
            if (!possibleActions.isEmpty()) {
                GameAction gameAction = possibleActions.get(random.nextInt(possibleActions.size()));
                game.execute(gameAction);
            }
        }
        return winnerListener.winnerColor;
    }

    private void executeActionForPiece(Piece piece, List<GameAction> possibleActions) {
        assertEquals(game.getPlayerColorOnTurn(), piece.getPlayerColor());
        int piecePosition = game.getPiecePosition(piece.getPlayerColor(), piece.getIndex());
        for (GameAction action : possibleActions) {
            if (action.getFromPosition() == piecePosition) {
                game.execute(action);
                return;
            }
        }
        Assert.fail("No action for piece");
    }

    private void failToLeaveSpawnThreeTimes() {
        for (int i = 0; i < 3; i++) {
            game.findPossibleActions(1);
        }
    }

    private void verifyGameActionFromTo(GameAction gameAction, int expectedFrom, int expectedTo) {
        assertEquals(expectedFrom, gameAction.getFromPosition());
        assertEquals(expectedTo, gameAction.getToPosition());
    }

    private void verifySpawnPosition(MenschAergereDichNichtGame game, int startIndex, PlayerColor colorToCheck) {
        assertEquals(startIndex, game.getPiecePosition(colorToCheck, 0));
        assertEquals(startIndex + 1, game.getPiecePosition(colorToCheck, 1));
        assertEquals(startIndex + 2, game.getPiecePosition(colorToCheck, 2));
        assertEquals(startIndex + 3, game.getPiecePosition(colorToCheck, 3));
    }

    private static class WinnerListener implements MenschAergereDichNichtGameListener {
        PlayerColor winnerColor;

        @Override
        public void onPlayerWon(PlayerColor playerColor) {
            winnerColor = playerColor;
        }
    }
}
