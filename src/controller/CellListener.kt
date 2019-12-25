import core.Cell

interface CellListener {
    fun restart()
    fun mustBite()
    fun delete(cell: Cell)
}