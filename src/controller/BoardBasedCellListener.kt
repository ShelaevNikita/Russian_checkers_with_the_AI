import core.Board
import core.Cell

class BoardBasedCellListener(private val board: Board) : CellListener {

    override fun restart() {
        board.startGame()
    }

    override fun mustBite(cell: Cell) {
        board.mustBite(cell)
    }

    override fun delete(cell: Cell) {
        board.delete(cell)
    }

}
