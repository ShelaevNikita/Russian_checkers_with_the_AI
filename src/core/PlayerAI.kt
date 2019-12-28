package core

import java.util.*

class PlayerAI(private val board: Board) {

    private val height = board.height

    private val width = board.width

    private val whiteSet = mutableSetOf<Cell>()

    private val blackSet = mutableSetOf<Cell>()

    private fun createSets(turn: Boolean, table: MutableList<MutableList<Chips>>) {
        if (turn) {
            whiteSet.clear()
            for (x in 0 until height)
                for (y in 0 until width) {
                    val cell = Cell(x, y)
                    if (table[x][y].color == 1) whiteSet += cell
                }
        } else {
            blackSet.clear()
            for (x in 0 until height)
                for (y in 0 until width) {
                    val cell = Cell(x, y)
                    if (table[x][y].color == 2) blackSet += cell
                }
        }
    }

    private fun biteOfCell(turn: Boolean, cell: Cell,
                           table: MutableList<MutableList<Chips>>): List<Pair<Cell, Cell>> {
        val moves = mutableListOf<Pair<Cell, Cell>>()
        val color = if (turn) 1 else 2
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
                    if (table[height0][width0].color == color) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != color))
                        if (table[height0 + 1][width0 + 1] == Chips.NO) {
                            moves += Pair(cell, Cell(height0 + 1, width0 + 1))
                            flag = true
                        } else break
                }
            } else {
                if ((height0 < height) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y - a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == color) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != color))
                        if (table[height0 - 1][width0 - 1] == Chips.NO) {
                            moves += Pair(cell, Cell(height0 - 1, width0 - 1))
                            flag = true
                        } else break
                }
            } else {
                if ((height0 >= 0) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x + a
            val width0 = y - a
            if (!flag) {
                if ((height0 + 1 < height) && (width0 - 1 >= 0)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == color) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != color))
                        if (table[height0 + 1][width0 - 1] == Chips.NO) {
                            moves += Pair(cell, Cell(height0 + 1, width0 - 1))
                            flag = true
                        } else break
                }
            } else {
                if ((height0 < height) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        flag = false
        for (a in 1..count) {
            val height0 = x - a
            val width0 = y + a
            if (!flag) {
                if ((height0 - 1 >= 0) && (width0 + 1 < width)) {
                    if (table[height0][width0] == Chips.NO) continue
                    if (table[height0][width0].color == color) break
                    if ((table[height0][width0].color > 0) &&
                            (table[height0][width0].color != color))
                        if (table[height0 - 1][width0 + 1] == Chips.NO) {
                            moves += Pair(cell, Cell(height0 - 1, width0 + 1))
                            flag = true
                        } else break
                }
            } else {
                if ((height0 >= 0) && (width0 < width) && (table[height0][width0] == Chips.NO))
                    moves += Pair(cell, Cell(height0, width0)) else break
            }
        }
        return moves
    }

    private fun mustBite(turn: Boolean, table: MutableList<MutableList<Chips>>): List<Pair<Cell, Cell>> {
        val moves = mutableListOf<Pair<Cell, Cell>>()
        createSets(turn, table)
        val set = if (turn) whiteSet else blackSet
        for (cells in set) moves += biteOfCell(turn, cells, table)
        return moves
    }

    private fun nextStepSimply(turn: Boolean, table: MutableList<MutableList<Chips>>): List<Pair<Cell, Cell>> {
        val moves = mutableListOf<Pair<Cell, Cell>>()
        val set = if (turn) whiteSet else blackSet
        for (cells in set) {
            val chip = table[cells.x][cells.y]
            val count = chip.count
            val x = cells.x
            val y = cells.y
            if ((chip == Chips.BlackSimply) || (count == width)) {
                for (a in 1..count) {
                    val height0 = x + a
                    val width0 = y + a
                    if ((height0 < height) && (width0 < width) && (table[height0][width0] == Chips.NO))
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
                for (a in 1..count) {
                    val height0 = x + a
                    val width0 = y - a
                    if ((height0 < height) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
            }
            if ((chip == Chips.WhiteSimply) || (count == width)) {
                for (a in 1..count) {
                    val height0 = x - a
                    val width0 = y - a
                    if ((height0 >= 0) && (width0 >= 0) && (table[height0][width0] == Chips.NO))
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
                for (a in 1..count) {
                    val height0 = x - a
                    val width0 = y + a
                    if ((height0 >= 0) && (width0 < width) && (table[height0][width0] == Chips.NO))
                        moves += Pair(cells, Cell(height0, width0)) else break
                }
            }
        }
        return moves
    }

    private fun move(turn: Boolean, cellFirst: Cell, cellSecond: Cell,
                     table: MutableList<MutableList<Chips>>) {
        val chip = table[cellFirst.x][cellFirst.y]
        table[cellFirst.x][cellFirst.y] = Chips.NO
        table[cellSecond.x][cellSecond.y] = chip
        val count = chip.count
        val x = cellSecond.x
        val y = cellSecond.y
        if (mustBite(turn, table).isNotEmpty()) {
            when {
                ((cellFirst.x > x) && (cellFirst.y > y)) ->
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
                ((cellFirst.x > x) && (cellFirst.y < y)) ->
                    for (a in 1..count) {
                        val height0 = x + a
                        val width0 = y - a
                        if ((height0 < height) && (width0 >= 0) &&
                                (table[height0][width0] != Chips.NO)) {
                            table[height0][width0] = Chips.NO
                            break
                        }
                    }
                ((cellFirst.x < x) && (cellFirst.y < y)) ->
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
        chipToDamka(turn, table)
    }

    private fun chipToDamka(turn: Boolean, table: MutableList<MutableList<Chips>>) {
        createSets(turn, table)
        val set = if (turn) whiteSet else blackSet
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

    private val makeSimply = 15.0

    private val makeDamka = 30.0

    private fun getValue(turn: Boolean, table: MutableList<MutableList<Chips>>): Double {
        createSets(turn, table)
        var result = 0.0
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
        if (turn) result = whiteCount - blackCount
        if (!turn) result = blackCount - whiteCount
        return result
    }

    private var moveMaxSum = 0.0

    private var numberMoves = 0

    private val midSumMoves = mutableListOf<Pair<Int, Double>>()

    private var step = -1

    private fun minimax(turn: Boolean, count: Double, depth: Int, maxPlayer: Boolean,
                        table: MutableList<MutableList<Chips>>,
                        alpha: Double, beta: Double): Double {
        if ((board.win() == 1) && (turnFirst))
            return if (maxPlayer) 100.0 * count else -100.0 * count
        if ((board.win() == 2) && (!turnFirst))
            return if (maxPlayer) 100.0 * count else -100.0 * count
        if (depth <= 0) return getValue(turn, table)
        var alphaChange = alpha
        var betaChange = beta
        val mustBite = mustBite(turn, table)
        val listMove = if (mustBite.isEmpty())
            nextStepSimply(turn, table) else mustBite
        var initial: Double
        val countChange = count - 0.1
        if (maxPlayer) {
            initial = alpha
            for (move in listMove) {
                val tableGhost = clone(table)
                if (mustBite.isEmpty())
                    move(turn, move.first, move.second, tableGhost)
                else moveAll(turn, move, tableGhost)
                val result = minimax(!turn,
                        countChange, depth - 1, !maxPlayer,
                        tableGhost, alphaChange, betaChange) * count
                if (depth <= 2) {
                    moveMaxSum += result
                    numberMoves++
                }
                initial = maxOf(result, initial)
                alphaChange = maxOf(alphaChange, initial)
                if (alphaChange > betaChange) break
            }
            if (depth <= 2) {
                midSumMoves += Pair(step, moveMaxSum / numberMoves)
                moveMaxSum = 0.0
                numberMoves = 0
            }
        } else {
            initial = beta
            for (move in listMove) {
                val tableGhost = clone(table)
                if (mustBite.isEmpty())
                    move(turn, move.first, move.second, tableGhost)
                else moveAll(turn, move, tableGhost)
                val result = minimax(!turn,
                        countChange, depth - 1, !maxPlayer,
                        tableGhost, alphaChange, betaChange) * count
                initial = minOf(result, initial)
                betaChange = minOf(betaChange, initial)
                if (alphaChange > betaChange) break
            }
        }
        return initial
    }

    private var turnFirst = false

    fun nextStep(turn: Boolean, depth: Int): Pair<Cell, Cell>? {
        turnFirst = turn
        val alpha = -1000.0
        val beta = 1000.0
        val count = 1.0
        val maxPlayer = true
        val mustBite = mustBite(turn, table)
        val list = if (mustBite.isEmpty())
            nextStepSimply(turn, table) else mustBite
        if (list.isEmpty()) return null
        val heuristics = mutableListOf<Double>()
        step = -1
        for (move in list) {
            step++
            val tableGhost = clone(table)
            if (mustBite.isEmpty())
                move(turn, move.first, move.second, tableGhost)
            else moveAll(turn, move, tableGhost)
            if (list.size == 1) return move
            heuristics.add(minimax(!turn, count, depth - 1,
                    !maxPlayer, tableGhost, alpha, beta))
        }
        val maxHeuristics = heuristics.max()!!
        val listMidCount = mutableListOf<Pair<Int, Double>>()
        var midStep = 0
        val random = Random()
        if ((depth >= 3) && (midSumMoves.isNotEmpty())) {
            val midCountStep = midSumMoves.maxBy { it.second }!!
            for (moves in midSumMoves) {
                if (moves == midCountStep) listMidCount += moves
            }
            midStep = listMidCount[random.nextInt(listMidCount.size)].first
        }
        val listMove = mutableListOf<Pair<Cell, Cell>>()
        for (x in 0 until heuristics.size) {
            if (heuristics[x] == maxHeuristics) listMove += list[x]
        }
        return if ((depth >= 3) && (listMove.size > midStep) && (listMove.size > 1))
            listMove[midStep]
        else listMove[random.nextInt(listMove.size)]
    }

    private fun moveAll(turn: Boolean, pairFirst: Pair<Cell, Cell>,
                        table: MutableList<MutableList<Chips>>) {
        var biteList = listOf<Pair<Cell, Cell>>()
        var pair = pairFirst
        val random = Random()
        while (biteList.isNotEmpty()) {
            move(turn, pair.first, pair.second, table)
            pair = biteList[random.nextInt(biteList.size)]
            biteList = biteOfCell(turn, pair.second, table)
        }
    }
}