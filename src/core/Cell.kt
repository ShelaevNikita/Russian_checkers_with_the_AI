package core

class Cell(val x: Int, val y: Int) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is Cell) {
            val cell = other as Cell?
            return x == cell!!.x && y == cell.y
        }
        return false
    }

    override fun hashCode(): Int {
        var result = 13
        result = 17 * result + x
        return 17 * result + y
    }

    override fun toString(): String {
        return "$x:$y"
    }
}
