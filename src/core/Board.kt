package core

import java.lang.StringBuilder
import java.util.*

class Board @JvmOverloads constructor(private val width: Int = 8, private val height: Int = 8) {

    private val chips = HashMap<Cell, Chips>()

    var listener: BoardListener? = null
        private set

    fun registerListener(listener: BoardListener) {
        this.listener = listener
    }

    operator fun get(x: Int, y: Int): Chips? {
        return get(Cell(x, y))
    }

    operator fun get(cell: Cell): Chips? {
        return chips[cell]
    }

    val table =
            MutableList(height) {
                MutableList(width) { Chips.NO }
            }

    fun startGame() {
        for (x in 0 until height)
            for (y in 0 until width) {
                when {
                    ((x == 5) || (x == 7)) && (y % 2 == 0) -> table[x][y] = Chips.White_Simply
                    (x == 6) && (y % 2 == 1) -> table[x][y] = Chips.White_Simply
                    ((x == 0) || (x == 2)) && (y % 2 == 1) -> table[x][y] = Chips.Black_Simply
                    (x == 1) && (y % 2 == 0) -> table[x][y] = Chips.Black_Simply
                    else -> table[x][y] = Chips.NO
                }
            }
    }

    fun mustBite(cell: Cell): Boolean {
        val chipColor = table[cell.x][cell.y].color
        if (chipColor == 0) return false
        for (x in 0 until height)
            for (y in 0 until width)
                if (chipColor == table[x][y].color) {
                    val count = table[cell.x][cell.y].count
                    for (a in 0 until count) {
                        val height0 = x + a
                        val width0 = y + a
                        if ((height0 + 2 < height) && (width0 + 2 < width) &&
                                (table[height0 + 1][width0 + 1].color > 0) &&
                                (table[height0 + 1][width0 + 1].color != chipColor) &&
                                (table[height0 + 2][width0 + 2] == Chips.NO)
                        ) {
                            flagBite = true
                            return true
                        }
                    }
                    for (a in 0 until count) {
                        val height0 = x - a
                        val width0 = y - a
                        if ((height0 - 2 >= 0) && (width0 - 2 >= 0) &&
                                (table[height0 - 1][width0 - 1].color > 0) &&
                                (table[height0 - 1][width0 - 1].color != chipColor) &&
                                (table[height0 - 2][width0 - 2] == Chips.NO)
                        ) {
                            flagBite = true
                            return true
                        }
                    }
                    for (a in 0 until count) {
                        val height0 = x + a
                        val width0 = y - a
                        if ((height0 + 2 < height) && (width0 - 2 >= 0) &&
                                (table[height0 + 1][width0 - 1].color > 0) &&
                                (table[height0 + 1][width0 - 1].color != chipColor) &&
                                (table[height0 + 2][width0 - 2] == Chips.NO)
                        ) {
                            flagBite = true
                            return true
                        }
                    }
                    for (a in 0 until count) {
                        val height0 = x - a
                        val width0 = y + a
                        if ((height0 - 2 >= 0) && (width0 + 2 < width) &&
                                (table[height0 - 1][width0 + 1].color > 0) &&
                                (table[height0 - 1][width0 + 1].color != chipColor) &&
                                (table[height0 - 2][width0 + 2] == Chips.NO)
                        ) {
                            flagBite = true
                            return true
                        }
                    }
                }
        return false
    }

    fun biteOfCell(cell: Cell): List<Cell> {
        val chipColor = table[cell.x][cell.y].color
        if (chipColor == 0) return emptyList()
        val moves = mutableSetOf<Cell>()
        val count = table[cell.x][cell.y].count
        val x = cell.x
        val y = cell.y
        var flag = false
        for (a in 0 until count) {
            var height0 = x + a
            var width0 = y + a
            if (!flag) {
                if ((height0 + 2 < height) && (width0 + 2 < width) &&
                        (table[height0 + 1][width0 + 1].color > 0) &&
                        (table[height0 + 1][width0 + 1].color != chipColor) &&
                        (table[height0 + 2][width0 + 2] == Chips.NO)
                ) if (((a > 0) && (table[height0][width0] == Chips.NO)) || (a == 0)) {
                    moves += Cell(height0 + 2, width0 + 2)
                    flag = true
                }
            } else {
                height0 += 2
                width0 += 2
                if ((height0 < height) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else continue

            }
        }
        flag = false
        for (a in 0 until count) {
            var height0 = x - a
            var width0 = y - a
            if (!flag) {
                if ((height0 - 2 >= 0) && (width0 - 2 >= 0) &&
                        (table[height0 - 1][width0 - 1].color > 0) &&
                        (table[height0 - 1][width0 - 1].color != chipColor) &&
                        (table[height0 - 2][width0 - 2] == Chips.NO)
                ) if (((a > 0) && (table[height0][width0] == Chips.NO)) || (a == 0)) {
                    moves += Cell(height0 - 2, width0 - 2)
                    flag = true
                }
            } else {
                height0 -= 2
                width0 -= 2
                if ((height0 >= 0) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else continue
            }
        }
        flag = false
        for (a in 0 until count) {
            var height0 = x + a
            var width0 = y - a
            if (!flag) {
                if ((height0 + 2 < height) && (width0 - 2 >= 0) &&
                        (table[height0 + 1][width0 - 1].color > 0) &&
                        (table[height0 + 1][width0 - 1].color != chipColor) &&
                        (table[height0 + 2][width0 - 2] == Chips.NO)
                ) if (((a > 0) && (table[height0][width0] == Chips.NO)) || (a == 0)) {
                    moves += Cell(height0 + 2, width0 - 2)
                    flag = true
                }
            } else {
                height0 += 2
                width0 -= 2
                if ((height0 < height) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else continue
            }
        }
        flag = false
        for (a in 0 until count) {
            var height0 = x - a
            var width0 = y + a
            if (!flag) {
                if ((height0 - 2 >= 0) && (width0 + 2 < width) &&
                        (table[height0 - 1][width0 + 1].color > 0) &&
                        (table[height0 - 1][width0 + 1].color != chipColor) &&
                        (table[height0 - 2][width0 + 2] == Chips.NO)
                ) if (((a > 0) && (table[height0][width0] == Chips.NO)) || (a == 0)) {
                    moves += Cell(height0 - 2, width0 + 2)
                    flag = true
                }
            } else {
                height0 -= 2
                width0 += 2
                if ((height0 >= 0) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else continue
            }
        }
        if (moves.isNotEmpty()) cell0 = cell
        return moves.toList()
    }

    private var cell0 = Cell(0, 0)

    var flagBite = false

    fun nextStepSimply(cell: Cell): List<Cell> {
        val chip = table[cell.x][cell.y]
        val chipColor = chip.color
        if (chipColor == 0) return emptyList()
        val moves = mutableSetOf<Cell>()
        val count = table[cell.x][cell.y].count
        val x = cell.x
        val y = cell.y
        if ((chip == Chips.Black_Simply) || (chip.count == width)) {
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y + a
                if ((height0 < height) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else {
                    continue
                }
            }
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y - a
                if ((height0 < height) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else {
                    continue
                }
            }
        }
        if ((chip == Chips.White_Simply) || (chip.count == width)) {
            for (a in 1..count) {
                val height0 = x - a
                val width0 = y - a
                if ((height0 >= 0) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else {
                    continue
                }
            }
            for (a in 0..count) {
                val height0 = x - a
                val width0 = y + a
                if ((height0 >= 0) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Cell(height0, width0) else {
                    continue
                }
            }
        }
        if (moves.isNotEmpty()) cell0 = cell
        return moves.toList()
    }

    fun delete(cell: Cell) {
        val chip = table[cell0.x][cell0.y]
        table[cell0.x][cell0.y] = Chips.NO
        table[cell.x][cell.y] = chip
        if (flagBite) {
            val count = chip.count
            val x = cell.x
            val y = cell.y
            when {
                ((cell0.x > cell.x) && (cell0.y > cell.y)) ->
                    for (a in 0 until count) {
                        val height0 = x + a
                        val width0 = y + a
                        if ((height0 + 1 < height) && (width0 + 1 < width) &&
                                (table[height0 + 1][width0 + 1] != Chips.NO)
                        ) {
                            table[height0 + 1][width0 + 1] = Chips.NO
                            break
                        }
                    }
                ((cell0.x > cell.x) && (cell0.y < cell.y)) ->
                    for (a in 0 until count) {
                        val height0 = x + a
                        val width0 = y - a
                        if ((height0 + 1 < height) && (width0 - 1 >= 0) &&
                                (table[height0 + 1][width0 - 1] != Chips.NO)
                        ) {
                            table[height0 + 1][width0 - 1] = Chips.NO
                            break
                        }
                    }
                ((cell0.x < cell.x) && (cell0.y < cell.y)) ->
                    for (a in 0 until count) {
                        val height0 = x - a
                        val width0 = y - a
                        if ((height0 - 1 >= 0) && (width0 - 1 >= 0) &&
                                (table[height0 - 1][width0 - 1] != Chips.NO)
                        ) {
                            table[height0 - 1][width0 - 1] = Chips.NO
                            break
                        }
                    }
                else ->
                    for (a in 0 until count) {
                        val height0 = x - a
                        val width0 = y + a
                        if ((height0 - 1 >= 0) && (width0 + 1 < width) &&
                                (table[height0 - 1][width0 + 1] != Chips.NO)
                        ) {
                            table[height0 - 1][width0 + 1] = Chips.NO
                            break
                        }
                    }
            }
        }
        if (biteOfCell(cell).isEmpty()) flagBite = false
        chipToDamka()
    }

    private fun chipToDamka() {
        for (x in 0 until height)
            for (y in 0 until width) {
                if ((x == 0) && (table[x][y] == Chips.White_Simply))
                    table[x][y] = Chips.White_Damka
                if ((x == height - 1) && (table[x][y] == Chips.Black_Simply))
                    table[x][y] = Chips.Black_Damka
            }
    }

    fun score(): Pair<Int, Int> {
        var black = 0
        var white = 0
        for (x in 0 until height)
            for (y in 0 until width) {
                if ((table[x][y] == Chips.White_Damka)
                        || (table[x][y] == Chips.White_Simply)) white++
                if ((table[x][y] == Chips.Black_Damka)
                        || (table[x][y] == Chips.Black_Simply)) black++
            }
        return Pair(white, black)
    }

    fun win(): Int {
        val pair = score()
        return when {
            pair.first == 0 -> 2
            pair.second == 0 -> 1
            else -> 0
        }
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (x in 0 until height) {
            for (y in 0 until width) {
                when (table[x][y]) {
                    Chips.Black_Simply -> sb.append(" Black_Simply ")
                    Chips.Black_Damka -> sb.append(" Black_Damka ")
                    Chips.White_Simply -> sb.append(" White_Simply ")
                    Chips.White_Damka -> sb.append(" White_Damka ")
                    else -> sb.append("      No      ")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}