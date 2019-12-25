package core

import java.util.*

class PlayerAI(board: Board) {

    private val makeSimply = 15

    private val makeDamka = 45

    private val height = board.height

    private val width = board.width

    private val whiteSet = mutableSetOf<Cell>()

    private val blackSet = mutableSetOf<Cell>()

    private fun createSets(table: MutableList<MutableList<Chips>>) {
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

    private fun biteOfCell(color: Int, cell: Cell,
                           table: MutableList<MutableList<Chips>>): Set<Pair<Cell, Cell>> {
        val moves = mutableSetOf<Pair<Cell, Cell>>()
        val count = table[cell.x][cell.y].count
        val x = cell.x
        val y = cell.y
        var flag = false
        for (a in 1..count) {
            val height0 = x + a
            val width0 = y + a
            if (!flag) {
                if ((height0 + 1 < height) && (width0 + 1 < width) &&
                        (table[height0][width0].color > 0) &&
                        (table[height0][width0].color != color))
                    if (table[height0 + 1][width0 + 1] == Chips.NO) {
                        moves += Pair(cell, Cell(height0 + 1, width0 + 1))
                        flag = true
                    } else break
            } else {
                if ((height0 < height) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y - a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 - 1 >= 0) &&
                        (table[height0][width0].color > 0) &&
                        (table[height0][width0].color != color))
                    if (table[height0 - 1][width0 - 1] == Chips.NO) {
                        moves += Pair(cell, Cell(height0 - 1, width0 - 1))
                        flag = true
                    } else break
            } else {
                if ((height0 >= 0) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x + a
            val width0 = y - a
            if (!flag) {
                if ((height0 + 1 < height) && (width0 - 1 >= 0) &&
                        (table[height0][width0].color > 0) &&
                        (table[height0][width0].color != color))
                    if (table[height0 + 1][width0 - 1] == Chips.NO) {
                        moves += Pair(cell, Cell(height0 + 1, width0 - 1))
                        flag = true
                    } else break
            } else {
                if ((height0 < height) && (width0 >= 0) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y + a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 + 1 < width) &&
                        (table[height0][width0].color > 0) &&
                        (table[height0][width0].color != color))
                    if (table[height0 - 1][width0 + 1] == Chips.NO) {
                        moves += Pair(cell, Cell(height0 - 1, width0 + 1))
                        flag = true
                    } else break
            } else {
                if ((height0 >= 0) && (width0 < width) &&
                        (table[height0][width0] == Chips.NO)
                )
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        return moves
    }

    private fun mustBite(color: Int, table: MutableList<MutableList<Chips>>): Set<Pair<Cell, Cell>> {
        val moves = mutableSetOf<Pair<Cell, Cell>>()
        createSets(table)
        val set = when (color) {
            1 -> whiteSet
            2 -> blackSet
            else -> emptySet<Cell>()
        }
        for (cells in set) moves += biteOfCell(color, cells, table)
        return moves
    }

    private fun nextStepSimply(color: Int, table: MutableList<MutableList<Chips>>): Set<Pair<Cell, Cell>> {
        val moves = mutableSetOf<Pair<Cell, Cell>>()
        val set = when (color) {
            1 -> whiteSet
            2 -> blackSet
            else -> emptySet<Cell>()
        }
        for (cells in set) {
            val chip = table[cells.x][cells.y]
            val count = chip.count
            val x = cells.x
            val y = cells.y
            if ((chip == Chips.BlackSimply) || (count == width)) {
                for (a in 1..count) {
                    val height0 = x + a
                    val width0 = y + a
                    if ((height0 < height) && (width0 < width) &&
                            (table[height0][width0] == Chips.NO)
                    )
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
                for (a in 1..count) {
                    val height0 = x + a
                    val width0 = y - a
                    if ((height0 < height) && (width0 >= 0) &&
                            (table[height0][width0] == Chips.NO)
                    )
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
            }
            if ((chip == Chips.WhiteSimply) || (count == width)) {
                for (a in 1..count) {
                    val height0 = x - a
                    val width0 = y - a
                    if ((height0 >= 0) && (width0 >= 0) &&
                            (table[height0][width0] == Chips.NO)
                    )
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
                for (a in 1..count) {
                    val height0 = x - a
                    val width0 = y + a
                    if ((height0 >= 0) && (width0 < width) &&
                            (table[height0][width0] == Chips.NO)
                    )
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
            }
        }
        return moves
    }

    private fun delete(cell0: Cell, cell: Cell, table: MutableList<MutableList<Chips>>) {
        val chip = table[cell0.x][cell0.y]
        table[cell0.x][cell0.y] = Chips.NO
        table[cell.x][cell.y] = chip
        val count = chip.count
        val x = cell.x
        val y = cell.y
        if (mustBite(chip.color, table).isNotEmpty()) {
            when {
                ((cell0.x > x) && (cell0.y > y)) ->
                    for (a in 1..count) {
                        val height0 = x + a
                        val width0 = y + a
                        if ((height0 < height) && (width0 < width) &&
                                (table[height0][width0] != Chips.NO)
                        ) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                ((cell0.x > x) && (cell0.y < y)) ->
                    for (a in 1..count) {
                        val height0 = x + a
                        val width0 = y - a
                        if ((height0 < height) && (width0 >= 0) &&
                                (table[height0][width0] != Chips.NO)
                        ) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                ((cell0.x < x) && (cell0.y < y)) ->
                    for (a in 1..count) {
                        val height0 = x - a
                        val width0 = y - a
                        if ((height0 >= 0) && (width0 >= 0) &&
                                (table[height0][width0] != Chips.NO)
                        ) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                else ->
                    for (a in 1..count) {
                        val height0 = x - a
                        val width0 = y + a
                        if ((height0 >= 0) && (width0 < width) &&
                                (table[height0][width0] != Chips.NO)
                        ) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
            }
        }
        chipToDamka(chip.color, table)
    }

    private fun chipToDamka(color: Int, table: MutableList<MutableList<Chips>>) {
        createSets(table)
        val set = when (color) {
            1 -> whiteSet
            2 -> blackSet
            else -> emptySet<Cell>()
        }
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

    private fun changeColor(color: Int): Int = when (color) {
        1 -> 2
        2 -> 1
        else -> 0
    }

    private val table = board.table

    private fun clone(table: MutableList<MutableList<Chips>>):
            MutableList<MutableList<Chips>> {
        val result = mutableListOf<MutableList<Chips>>()
        for (x in table) {
            val list = mutableListOf<Chips>()
            for (y in x) list += y
            result += list
        }
        return result
    }

    private fun getValue(color: Int, count: Double,
                         table: MutableList<MutableList<Chips>>): Double {
        var result = 0
        var numberBlackSimply = 0
        var numberBlackDamka = 0
        var numberWhiteSimply = 0
        var numberWhiteDamka = 0
        for (cell in blackSet) {
            if (table[cell.x][cell.y].count == width)
                numberBlackDamka++ else numberBlackSimply++
        }
        for (cell in whiteSet) {
            if (table[cell.x][cell.y].count == width)
                numberWhiteDamka++ else numberWhiteSimply++
        }
        val whiteCount = numberWhiteDamka * makeDamka + numberWhiteSimply * makeSimply
        val blackCount = numberBlackDamka * makeDamka + numberBlackSimply * makeSimply
        if (color == 1) result = whiteCount - blackCount
        if (color == 2) result = blackCount - whiteCount
        return result * count
    }

    private fun minimax(color: Int, count: Double, depth: Int, maxPlayer: Boolean,
                        table: MutableList<MutableList<Chips>>,
                        alpha: Double, beta: Double): Double {
        if (depth == 0) return getValue(color, count, table)
        var alphaChange = alpha
        var betaChange = beta
        val mustBite = mustBite(color, table)
        val set = if (mustBite.isEmpty())
            nextStepSimply(color, table) else mustBite
        var initial: Double
        val countChange = count - 0.1 * depth
        if (maxPlayer) {
            initial = Double.MIN_VALUE
            for (move in set) {
                val tableGhost = clone(table)
                delete(move.first, move.second, tableGhost)
                val result = minimax(changeColor(color),
                        countChange, depth - 1, !maxPlayer,
                        tableGhost, alphaChange, betaChange) * count
                initial = maxOf(result, initial)
                alphaChange = maxOf(alphaChange, initial)
                if (alphaChange > betaChange) break
            }
        } else {
            initial = Double.MAX_VALUE
            for (move in set) {
                val tableGhost = clone(table)
                delete(move.first, move.second, tableGhost)
                val result = minimax(changeColor(color),
                        countChange, depth - 1, !maxPlayer,
                        tableGhost, alphaChange, betaChange) * count
                initial = minOf(result, initial)
                betaChange = minOf(betaChange, initial)
                if (alphaChange > betaChange) break
            }
        }
        return initial
    }

    fun nextStep(colorFirst: Int, depth: Int): Pair<Cell, Cell> {
        val alpha = Double.MIN_VALUE
        val beta = Double.MAX_VALUE
        val count = 1.0
        val maxPlayer = true
        val mustBite = mustBite(colorFirst, table)
        val set = if (mustBite.isEmpty())
            nextStepSimply(colorFirst, table) else mustBite
        val heuristics = mutableListOf<Double>()
        for (move in set) {
            val tableGhost = clone(table)
            delete(move.first, move.second, tableGhost)
            heuristics.add(minimax(changeColor(colorFirst), count,
                    depth - 1, !maxPlayer, tableGhost, alpha, beta))

        }
        var maxHeuristics = Double.MIN_VALUE
        val random = Random()
        for (x in heuristics.size - 1 downTo 0) {
            if (heuristics[x] > maxHeuristics) maxHeuristics = heuristics[x]
        }
        val listMove = set.toMutableList()
        for (x in 0 until heuristics.size) {
            if (heuristics[x] < maxHeuristics) listMove -= listMove[x]
        }
        return listMove[random.nextInt(listMove.size)]
    }
}