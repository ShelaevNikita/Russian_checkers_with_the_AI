import core.Board
import core.Cell

class BoardBasedCellListener(private val board: Board) : CellListener {

    override fun restart() {
        board.startGame()
    }

    override fun mustBite() {
        board.mustBite()
    }

    override fun delete(cell: Cell) {
        board.move(cell)
    }

}
