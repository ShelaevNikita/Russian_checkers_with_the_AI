import core.Cell

interface CellListener {
    fun restart()
    fun mustBite(cell: Cell)
    fun delete(cell: Cell)
}