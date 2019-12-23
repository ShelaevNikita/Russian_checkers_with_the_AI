import core.Board
import core.Cell
import org.junit.Assert.*
import org.junit.Test

class BoardTest {
    @Test
    fun control() {
        val board = Board(8, 8)
        board.startGame()
        assertEquals(false, board.mustBite(Cell(0, 0)))
        assertEquals(listOf(Cell(4, 1)), board.nextStepSimply(Cell(5, 0)))
        assertEquals(emptyList<Cell>(), board.nextStepSimply(Cell(7, 0)))
        assertEquals(Pair(12, 12), board.score())
        assertEquals(0, board.win())
        board.delete(Cell(4, 1))
        assertEquals(false, board.mustBite(Cell(4, 1)))
        assertEquals(listOf(Cell(3, 2), Cell(3, 0)), board.nextStepSimply(Cell(2, 1)))
        board.delete(Cell(3, 0))
        assertEquals(listOf(Cell(4, 3)), board.nextStepSimply(Cell(5, 2)))
        board.delete(Cell(4, 3))
        assertEquals(true, board.mustBite(Cell(3, 0)))
        assertEquals(listOf(Cell(5, 2)), board.biteOfCell(Cell(3, 0)))
        board.delete(Cell(5, 2))
        assertEquals(listOf(Cell(3, 4)), board.biteOfCell(Cell(5, 2)))
        board.delete(Cell(3, 4))
        assertEquals(Pair(10, 12), board.score())
    }
}