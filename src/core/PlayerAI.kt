package core

import java.lang.StringBuilder

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

    private fun biteOfCell(color: Int,
                           cell: Cell, table: MutableList<MutableList<Chips>>): Set<Pair<Cell, Cell>> {
        val chipColor = table[cell.x][cell.y].color
        if (chipColor == 0) return emptySet()
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
        createSets(table)
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

    private fun delete(cell0: Cell, cell: Cell, table: MutableList<MutableList<Chips>>): Int {
        val chip = table[cell0.x][cell0.y]
        table[cell0.x][cell0.y] = Chips.NO
        table[cell.x][cell.y] = chip
        var result = 0
        val count = chip.count
        val x = cell.x
        val y = cell.y
        when {
            ((cell0.x > cell.x) && (cell0.y > cell.y)) ->
                for (a in 0 until count) {
                    val height0 = x + a
                    val width0 = y + a
                    if ((height0 + 1 < height) && (width0 + 1 < width)) {
                        if (table[height0 + 1][width0 + 1] == chip) break
                        else if (table[height0 + 1][width0 + 1] != Chips.NO) {
                            val f = table[height0 + 1][width0 + 1].count
                            result = when (f) {
                                height -> makeDamka
                                1 -> makeSimply
                                else -> 0
                            }
                            table[height0 + 1][width0 + 1] = Chips.NO
                            break
                        }
                    }
                }
            ((cell0.x > cell.x) && (cell0.y < cell.y)) ->
                for (a in 0 until count) {
                    val height0 = x + a
                    val width0 = y - a
                    if ((height0 + 1 < height) && (width0 - 1 >= 0)) {
                        if (table[height0 + 1][width0 - 1] == chip) break
                        else if (table[height0 + 1][width0 - 1] != Chips.NO) {
                            val f = table[height0 + 1][width0 - 1].count
                            result = when (f) {
                                height -> makeDamka
                                1 -> makeSimply
                                else -> 0
                            }
                            table[height0 + 1][width0 - 1] = Chips.NO
                            break
                        }
                    }
                }
            ((cell0.x < cell.x) && (cell0.y < cell.y)) ->
                for (a in 0 until count) {
                    val height0 = x - a
                    val width0 = y - a
                    if ((height0 - 1 >= 0) && (width0 - 1 >= 0)) {
                        if (table[height0 - 1][width0 - 1] == chip) break
                        else if (table[height0 - 1][width0 - 1] != Chips.NO) {
                            val f = table[height0 - 1][width0 - 1].count
                            result = when (f) {
                                height -> makeDamka
                                1 -> makeSimply
                                else -> 0
                            }
                            table[height0 - 1][width0 - 1] = Chips.NO
                            break
                        }
                    }
                }
            else ->
                for (a in 0 until count) {
                    val height0 = x - a
                    val width0 = y + a
                    if ((height0 - 1 >= 0) && (width0 + 1 < width)) {
                        if (table[height0 - 1][width0 + 1] == chip) break
                        else if (table[height0 - 1][width0 + 1] != Chips.NO) {
                            val f = table[height0 - 1][width0 + 1].count
                            result = when (f) {
                                height -> makeDamka
                                1 -> makeSimply
                                else -> 0
                            }
                            table[height0 - 1][width0 + 1] = Chips.NO
                            break
                        }
                    }
                }
        }
        createSets(table)
        return result
    }

    private fun chipToDamka(color: Int, table: MutableList<MutableList<Chips>>): Int {
        var flag = 0
        val set = when (color) {
            1 -> whiteSet
            2 -> blackSet
            else -> emptySet<Cell>()
        }
        for (cells in set) {
            val x = cells.x
            val y = cells.y
            if ((x == 0) && (table[x][y] == Chips.WhiteSimply)) {
                flag = makeDamka
                table[x][y] = Chips.WhiteDamka
                break
            }
            if ((x == height - 1) && (table[x][y] == Chips.BlackSimply)) {
                flag = makeDamka
                table[x][y] = Chips.BlackDamka
                break
            }
        }
        return flag
    }

    private fun changeColor(color: Int): Int = when (color) {
        1 -> 2
        2 -> 1
        else -> 0
    }

    private val table = board.table

    private val tableGhost0 = mutableListOf<MutableList<Chips>>()

    private val tableGhost1 = mutableListOf<MutableList<Chips>>()

    private val tableGhost2 = mutableListOf<MutableList<Chips>>()

    private val tableGhost3 = mutableListOf<MutableList<Chips>>()

    fun nextStep(color0: Int): Pair<Cell, Cell> {
        var color = color0
        var random = 1.0
        var result = Pair(Cell(0, 0), Cell(7, 7))
        var max = -50.0
        var min = 50.0
        val setBite0 = mustBite(color, table)
        val set0 = if (setBite0.isEmpty())
            nextStepSimply(color, table) else setBite0
        if (setBite0.size == 1) {
            var biteSet = setBite0
            var pair: Pair<Cell, Cell>
            while (biteSet.isNotEmpty()) {
                pair = biteSet.first()
                delete(pair.first, pair.second, table)
                biteSet = biteOfCell(color, pair.second, table)
            }
        }
        for (cells0 in set0) {
            tableGhost0.clear()
            for (x in table) {
                val list = mutableListOf<Chips>()
                for (y in x) list += y
                tableGhost0 += list
            }
            var count0 = 0.0
            count0 += random * delete(cells0.first, cells0.second, tableGhost0)
            count0 += random * chipToDamka(color, tableGhost0)
            var biteSetOfCell0 = biteOfCell(color, cells0.second, tableGhost0)
            while (biteSetOfCell0.isNotEmpty()) {
                random -= 0.1
                val first = biteSetOfCell0.first()
                if (random > 0) {
                    count0 += random * delete(first.first, first.second, tableGhost0)
                    count0 += random * chipToDamka(color, tableGhost0)
                }
                biteSetOfCell0 = biteOfCell(color, first.second, tableGhost0)
            }
            random = 0.9
            color = changeColor(color)
            val setBite1 = mustBite(color, tableGhost0)
            val set1 = if (setBite1.isEmpty())
                nextStepSimply(color, tableGhost0) else setBite1
            for (cells1 in set1) {
                tableGhost1.clear()
                for (x in tableGhost0) {
                    val list = mutableListOf<Chips>()
                    for (y in x) list += y
                    tableGhost1 += list
                }
                var count1 = 0.0
                count1 += random * delete(cells1.first, cells1.second, tableGhost1)
                count1 += random * chipToDamka(color, tableGhost1)
                var biteSetOfCell1 = biteOfCell(color, cells1.second, tableGhost1)
                while (biteSetOfCell1.isNotEmpty()) {
                    random -= 0.1
                    val first = biteSetOfCell1.first()
                    if (random > 0) {
                        count1 += random * delete(first.first, first.second, tableGhost1)
                        count1 += random * chipToDamka(color, tableGhost1)
                    }
                    biteSetOfCell1 = biteOfCell(color, first.second, tableGhost1)
                }
                random = 0.8
                color = changeColor(color)
                val setBite2 = mustBite(color, tableGhost1)
                val set2 = if (setBite2.isEmpty())
                    nextStepSimply(color, tableGhost1) else setBite2
                for (cells2 in set2) {
                    tableGhost2.clear()
                    for (x in tableGhost1) {
                        val list = mutableListOf<Chips>()
                        for (y in x) list += y
                        tableGhost2 += list
                    }
                    var count2 = 0.0
                    count2 += random * delete(cells2.first, cells2.second, tableGhost2)
                    count2 += random * chipToDamka(color, tableGhost2)
                    var biteSetOfCell2 = biteOfCell(color, cells2.second, tableGhost2)
                    while (biteSetOfCell2.isNotEmpty()) {
                        random -= 0.1
                        val first = biteSetOfCell2.first()
                        if (random > 0) {
                            count2 += random * delete(first.first, first.second, tableGhost2)
                            count2 += random * chipToDamka(color, tableGhost2)
                        }
                        biteSetOfCell2 = biteOfCell(color, first.second, tableGhost2)
                    }
                    random = 0.7
                    color = changeColor(color)
                    val setBite3 = mustBite(color, tableGhost2)
                    val set3 = if (setBite3.isEmpty())
                        nextStepSimply(color, tableGhost2) else setBite2
                    for (cells3 in set3) {
                        tableGhost3.clear()
                        for (x in tableGhost2) {
                            val list = mutableListOf<Chips>()
                            for (y in x) list += y
                            tableGhost3 += list
                        }
                        var count3 = 0.0
                        count3 += random * delete(cells3.first, cells3.second, tableGhost3)
                        count3 += random * chipToDamka(color, tableGhost3)
                        var biteSetOfCell3 = biteOfCell(color, cells3.second, tableGhost3)
                        while (biteSetOfCell3.isNotEmpty()) {
                            random -= 0.1
                            val first = biteSetOfCell3.first()
                            if (random > 0) {
                                count3 += random * delete(first.first, first.second, tableGhost3)
                                count3 += random * chipToDamka(color, tableGhost3)
                            }
                            biteSetOfCell3 = biteOfCell(color, first.second, tableGhost3)
                        }
                        val countMax = count0 - count1 + count2 - count3
                        val control = max
                        max = maxOf(max, countMax)
                        min = minOf(min, countMax)
                        if ((control < max) && (max > -min))
                            result = Pair(cells0.first, cells0.second)
                    }
                }
            }
        }
        return result
    }

    private fun toString(table: MutableList<MutableList<Chips>>): String {
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