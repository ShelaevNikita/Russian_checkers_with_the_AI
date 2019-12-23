import core.Board
import core.BoardListener
import core.Cell
import core.Chips
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*

class CheckersView : View(), BoardListener {

    private val columnsNumber = 8

    private val rowsNumber = 8

    private val buttons = mutableMapOf<Cell, Button>()

    private lateinit var statusLabel: Label

    private var inProcess = true

    private var grid = GridPane()

    private var stage = true

    private var f = false

    private val buttons0 = mutableSetOf<Cell>()

    override val root = BorderPane()

    private val board = Board(columnsNumber, rowsNumber)

    init {
        title = "CheckersWithAI"

        val listener = BoardBasedCellListener(board)

        board.registerListener(this)

        val dimension = Dimension(60.0, Dimension.LinearUnits.px)

        with(root) {
            top {
                vbox {
                    menubar {
                        menu("Game") {
                            item("Exit").action {
                                this@CheckersView.close()
                            }
                            item("Restart").action {
                                restartGame()
                            }
                        }
                    }
                }
            }
            bottom {
                statusLabel = label {
                    text = "Нажмите на экран"
                    font = Font(30.0)
                }
            }
            center {
                val button = button {
                    style {
                        backgroundColor += Color.WHITE
                        minWidth = 10 * dimension
                        minHeight = 10 * dimension
                        text = "Играть"
                        font = Font(40.0)
                    }
                }
                button.action {
                    listener.restart()
                    button.isVisible = false
                    center {
                        grid = gridpane {
                            hgap = 6.0
                            vgap = 6.0
                            for (row in 0 until rowsNumber) {
                                row {
                                    for (column in 0 until columnsNumber) {
                                        if (((row % 2 == 0) && (column % 2 == 1)) ||
                                                ((row % 2 == 1) && (column % 2 == 0))) {
                                            val cell = Cell(row, column)
                                            val button1 = button {
                                                style {
                                                    backgroundColor += Color.BLACK
                                                    minWidth = dimension
                                                    minHeight = dimension
                                                }
                                            }
                                            buttons[cell] = button1
                                            button1.action {
                                                if (cell in buttons0) {
                                                    listener.delete(cell)
                                                    for (x in 0 until rowsNumber)
                                                        for (y in 0 until columnsNumber)
                                                            updateBoardAndStatus(Cell(x, y))
                                                    buttons0 -= cell
                                                    if (board.flagBite) {
                                                        val list = board.biteOfCell(cell)
                                                        if (list.isNotEmpty()) {
                                                            for (cells in list) {
                                                                buttons0 += cells
                                                                buttons[cells]?.apply {
                                                                    graphic = circle(radius = 20.0) {
                                                                        fill = Color.RED
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        val h = stage
                                                        stage = f
                                                        f = h
                                                    }
                                                }
                                                for (cell0 in buttons0) {
                                                    buttons[cell0]?.apply {
                                                        graphic = circle(radius = 20.0) {
                                                            fill = Color.BLACK
                                                        }
                                                    }
                                                }
                                                buttons0.clear()
                                                if (((stage) && (board.table[cell.x][cell.y].color == 1)) ||
                                                        ((!stage) && (board.table[cell.x][cell.y].color == 2))) {
                                                    listener.mustBite(cell)
                                                    if (board.mustBite(cell)) {
                                                        println(board.biteOfCell(cell))
                                                        val list = board.biteOfCell(cell)
                                                        if (list.isNotEmpty()) {
                                                            for (cells in list) {
                                                                buttons0 += cells
                                                                buttons[cells]?.apply {
                                                                    graphic = circle(radius = 20.0) {
                                                                        fill = Color.RED
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } else {
                                                        println(board.nextStepSimply(cell))
                                                        for (cells in board.nextStepSimply(cell)) {
                                                            buttons0 += cells
                                                            buttons[cells]?.apply {
                                                                graphic = circle(radius = 20.0) {
                                                                    fill = Color.RED
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (inProcess) {
                                                    statusLabel.text = if (stage) "Ход белых" else "Ход чёрных"
                                                    statusLabel.text += ". Белых: ${board.score().first}," +
                                                            " Чёрных: ${board.score().second}"
                                                }
                                            }
                                        } else {
                                            button {
                                                style {
                                                    backgroundColor += Color.WHITE
                                                    minWidth = dimension
                                                    minHeight = dimension
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            for (x in 0 until rowsNumber)
                                for (y in 0 until columnsNumber)
                                    updateBoardAndStatus(Cell(x, y))
                        }
                    }
                }
            }
        }
    }

    override fun restart(cell: Cell) {
        updateBoardAndStatus(cell)
    }

    private fun updateBoardAndStatus(cell: Cell? = null) {
        val winner = board.win()
        statusLabel.text = when (winner) {
            1 -> {
                inProcess = false
                "White win! Press 'Restart' to continue or 'Exit'"
            }
            2 -> {
                inProcess = false
                "Black win! Press 'Restart' to continue or 'Exit'"
            }
            else -> "Начало игры. Белых: ${board.score().first}, Чёрных: ${board.score().second}"
        }
        if (cell == null) return
        val chip = board.table[cell.x][cell.y]
        buttons[cell]?.apply {
            graphic = circle(radius = 20.0) {
                fill = when (chip) {
                    Chips.White_Simply -> Color.ALICEBLUE
                    Chips.Black_Simply -> Color.DARKBLUE
                    Chips.Black_Damka -> Color.GRAY
                    Chips.White_Damka -> Color.CORAL
                    else -> Color.BLACK
                }
            }
        }
    }

    private fun restartGame() {
        statusLabel.text = ""
        stage = true
        buttons0.clear()
        board.startGame()
        for (x in 0 until columnsNumber)
            for (y in 0 until rowsNumber)
                updateBoardAndStatus(Cell(x, y))
        inProcess = true
    }

}