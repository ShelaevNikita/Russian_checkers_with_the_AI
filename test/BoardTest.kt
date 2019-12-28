import core.Board
import core.Cell
import core.PlayerAI
import org.junit.Assert.*
import org.junit.Test

class BoardTest1 {
    @Test
    fun control() {
        val board = Board(8, 8)
        board.startGame()
        assertEquals(false, board.mustBite())
        assertEquals(setOf(Cell(4, 1)), board.nextStepSimply(Cell(5, 0)))
        assertEquals(emptySet<Cell>(), board.nextStepSimply(Cell(7, 0)))
        assertEquals(Pair(12, 12), board.score())
        assertEquals(0, board.win())
        board.move(Cell(4, 1))
        assertEquals(false, board.mustBite())
        assertEquals(setOf(Cell(3, 2), Cell(3, 0)), board.nextStepSimply(Cell(2, 1)))
        board.move(Cell(3, 0))
        assertEquals(setOf(Cell(4, 3)), board.nextStepSimply(Cell(5, 2)))
        board.move(Cell(4, 3))
        assertEquals(true, board.mustBite())
        assertEquals(setOf(Cell(5, 2)), board.biteOfCell(Cell(3, 0)))
        board.move(Cell(5, 2))
        assertEquals(setOf(Cell(3, 4)), board.biteOfCell(Cell(5, 2)))
        board.move(Cell(3, 4))
        assertEquals(Pair(10, 12), board.score())
        val player = PlayerAI(board)
        println(player.nextStep(true, 1))
        println(player.nextStep(true, 2))
        println(player.nextStep(true, 3))
        println(player.nextStep(true, 4))
        println(player.nextStep(true, 5))
        val board1 = Board(8, 8)
        board1.startGame()
        assertEquals(0, board1.win())
        board1.nextStepSimply(Cell(5, 2))
        board1.move(Cell(4, 3))
        val player1 = PlayerAI(board1)
        println(player1.nextStep(false, 4))
    }
}