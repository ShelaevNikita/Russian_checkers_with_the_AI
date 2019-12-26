package core

import java.lang.StringBuilder

class Board @JvmOverloads constructor(val width: Int = 8, val height: Int = 8) {

    private var listener: BoardListener? = null

    fun registerListener(listener: BoardListener) {
        this.listener = listener
    }

    operator fun get(x: Int, y: Int): Chips? {
        return get(Cell(x, y))
    }

    operator fun get(cell: Cell): Chips? {
        return table[cell.x][cell.y]
    }

    val table =
            MutableList(height) {
                MutableList(width) { Chips.NO }
            }

    private val whiteSet = mutableSetOf<Cell>()

    private val blackSet = mutableSetOf<Cell>()

    var turn = true

    fun startGame() {
        for (x in 0 until height)
            for (y in 0 until width) {
                when {
                    ((x == 5) || (x == 7)) && (y % 2 == 0) -> table[x][y] = Chips.WhiteSimply
                    (x == 6) && (y % 2 == 1) -> table[x][y] = Chips.WhiteSimply
                    ((x == 0) || (x == 2)) && (y % 2 == 1) -> table[x][y] = Chips.BlackSimply
                    (x == 1) && (y % 2 == 0) -> table[x][y] = Chips.BlackSimply
                    else -> table[x][y] = Chips.NO
                }
            }
    }

    private fun createSets() {
        whiteSet.clear()
        blackSet.clear()
        for (x in 0 until height)
            loop@ for (y in 0 until width) {
                val cell = Cell(x, y)
                when {
                    table[x][y].color == 1 -> whiteSet += cell
                    table[x][y].color == 2 -> blackSet += cell
                    else -> continue@loop
                }
            }
    }

    fun mustBite(): Boolean {
        val chipColor = if (turn) 1 else 2
        createSets()
        val set = when (turn) {
            true -> whiteSet
            else -> blackSet
        }
        for (cells in set) {
            val count = table[cells.x][cells.y].count
            val x = cells.x
            val y = cells.y
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y + a
                if ((height0 + 1 < height) && (width0 + 1 < width)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 + 1][width0 + 1] == Chips.NO) {
                            flagBite = true
                            return true
                        } else break
                }
            }
            for (a in 1..count) {
                val height0 = x - a
                val width0 = y - a
                if ((height0 - 1 >= 0) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 - 1][width0 - 1] == Chips.NO) {
                            flagBite = true
                            return true
                        } else break
                }
            }
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y - a
                if ((height0 + 1 < height) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 + 1][width0 - 1] == Chips.NO) {
                            flagBite = true
                            return true
                        } else break
                }
            }
            for (a in 1..count) {
                val height0 = x - a
                val width0 = y + a
                if ((height0 - 1 >= 0) && (width0 + 1 < width)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 - 1][width0 + 1] == Chips.NO) {
                            flagBite = true
                            return true
                        } else break
                }
            }
        }
        return false
    }

    fun biteOfCell(cell: Cell): Set<Cell> {
        val chipColor = table[cell.x][cell.y].color
        if (chipColor == 0) return emptySet()
        val moves = mutableSetOf<Cell>()
        val count = table[cell.x][cell.y].count
        val x = cell.x
        val y = cell.y
        var flag = false
        for (a in 1..count) {
            val height0 = x + a
            val width0 = y + a
            if (!flag) {
                if ((height0 + 1 < height) && (width0 + 1 < width)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 + 1][width0 + 1] == Chips.NO) {
                            moves += Cell(height0 + 1, width0 + 1)
                            flag = true
                        } else break
                }
            } else {
                if ((height0 < height) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y - a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 - 1][width0 - 1] == Chips.NO) {
                            moves += Cell(height0 - 1, width0 - 1)
                            flag = true
                        } else break
                }
            } else {
                if ((height0 >= 0) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x + a
            val width0 = y - a
            if (!flag) {
                if ((height0 + 1 < height) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 + 1][width0 - 1] == Chips.NO) {
                            moves += Cell(height0 + 1, width0 - 1)
                            flag = true
                        } else break
                }
            } else {
                if ((height0 < height) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y + a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 + 1 < width)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == chipColor) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != chipColor))
                        if (table[height0 - 1][width0 + 1] == Chips.NO) {
                            moves += Cell(height0 - 1, width0 + 1)
                            flag = true
                        } else break
                }
            } else {
                if ((height0 >= 0) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        if (moves.isNotEmpty()) cell0 = cell
        return moves
    }

    private var cell0 = Cell(0, 0)

    var flagBite = false

    fun nextStepSimply(cell: Cell): Set<Cell> {
        val chip = table[cell.x][cell.y]
        val moves = mutableSetOf<Cell>()
        val count = chip.count
        val x = cell.x
        val y = cell.y
        if ((chip == Chips.BlackSimply) || (count == width)) {
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y + a
                if ((height0 < height) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
            for (a in 1..count) {
                val height0 = x + a
                val width0 = y - a
                if ((height0 < height) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        if ((chip == Chips.WhiteSimply) || (count == width)) {
            for (a in 1..count) {
                val height0 = x - a
                val width0 = y - a
                if ((height0 >= 0) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
            for (a in 1..count) {
                val height0 = x - a
                val width0 = y + a
                if ((height0 >= 0) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Cell(height0, width0) else break
            }
        }
        if (moves.isNotEmpty()) cell0 = cell
        return moves
    }

    fun move(cell: Cell) {
        val chip = table[cell0.x][cell0.y]
        table[cell0.x][cell0.y] = Chips.NO
        table[cell.x][cell.y] = chip
        if (flagBite) {
            val count = chip.count
            val x = cell.x
            val y = cell.y
            when {
                ((cell0.x > cell.x) && (cell0.y > cell.y)) ->
                    for (a in 1..count) {
                        val height0 = x + a
                        val width0 = y + a
                        if ((height0 < height) && (width0 < width) &&
                                (table[height0][width0] != Chips.NO)) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                ((cell0.x > cell.x) && (cell0.y < cell.y)) ->
                    for (a in 1..count) {
                        val height0 = x + a
                        val width0 = y - a
                        if ((height0 < height) && (width0 >= 0) &&
                                (table[height0][width0] != Chips.NO)) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                ((cell0.x < cell.x) && (cell0.y < cell.y)) ->
                    for (a in 1..count) {
                        val height0 = x - a
                        val width0 = y - a
                        if ((height0 >= 0) && (width0 >= 0) &&
                                (table[height0][width0] != Chips.NO)) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                else ->
                    for (a in 1..count) {
                        val height0 = x - a
                        val width0 = y + a
                        if ((height0 >= 0) && (width0 < width) &&
                                (table[height0][width0] != Chips.NO)) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
            }
        }
        if (flagBite) {
            if (biteOfCell(cell).isEmpty()) {
                flagBite = false
                turn = !turn
            }
        } else turn = !turn
        createSets()
        chipToDamka()
    }

    private fun chipToDamka() {
        val set = if (!turn) whiteSet else blackSet
        for (cells in set) {
            val x = cells.x
            val y = cells.y
            if ((x == 0) && (table[x][y] == Chips.WhiteSimply)) {
                table[x][y] = Chips.WhiteDamka
                break
            }
            if ((x == height - 1) && (table[x][y] == Chips.BlackSimply)) {
                table[x][y] = Chips.BlackDamka
                break
            }
        }
    }

    fun score(): Pair<Int, Int> {
        var black = 0
        var white = 0
        for (x in 0 until height)
            for (y in 0 until width) {
                if (table[x][y].color == 1) white++
                if (table[x][y].color == 2) black++
            }
        return Pair(white, black)
    }

    fun win(): Int {
        createSets()
        var whiteMove = false
        var blackMove = false
        val cellFirst = cell0
        for (move in whiteSet) {
            cell0 = cellFirst
            if (nextStepSimply(move).isNotEmpty()) {
                whiteMove = true
                break
            }
        }
        for (move in blackSet) {
            cell0 = cellFirst
            if (nextStepSimply(move).isNotEmpty()) {
                blackMove = true
                break
            }
        }
        cell0 = cellFirst
        val pair = score()
        return when {
            ((pair.first == 0) || (!whiteMove)) -> 2
            ((pair.second == 0) || (!blackMove)) -> 1
            else -> 0
        }
    }

    fun toString(table: MutableList<MutableList<Chips>>): String {
        val sb = StringBuilder()
        for (x in 0 until height) {
            for (y in 0 until width) {
                when (table[x][y]) {
                    Chips.BlackSimply -> sb.append(" BlackSimply ")
                    Chips.BlackDamka -> sb.append(" BlackDamka ")
                    Chips.WhiteSimply -> sb.append(" WhiteSimply ")
                    Chips.WhiteDamka -> sb.append(" WhiteDamka ")
                    else -> sb.append("      No      ")
                }
            }
            sb.append("\n")
        }
        return sb.toString()
    }
}