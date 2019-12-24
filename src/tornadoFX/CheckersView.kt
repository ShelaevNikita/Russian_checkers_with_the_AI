import core.*
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.scene.text.Font
import tornadofx.*
import kotlin.concurrent.timer

class CheckersView : View(), BoardListener {

    private data class AutoTurnEvent(val player: PlayerAI) : FXEvent()

    private val columnsNumber = 8

    private val rowsNumber = 8

    private val buttons = mutableMapOf<Cell, Button>()

    private val board = Board(columnsNumber, rowsNumber)

    private var stage = true

    private var whiteComputer =
            if ((app as Checkers).whiteHuman) null else PlayerAI(board)

    private var blackComputer =
            if ((app as Checkers).blackHuman) null else PlayerAI(board)

    private val computerToMakeTurn: PlayerAI?
        get() = if (stage) whiteComputer else blackComputer

    private lateinit var statusLabel: Label

    private var inProcess = true

    private var grid = GridPane()

    private val buttons0 = mutableSetOf<Cell>()

    override val root = BorderPane()

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
                                reconfigureGame()
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
                                                    } else stage = !stage
                                                }
                                                for (cell0 in buttons0) {
                                                    buttons[cell0]?.apply {
                                                        graphic = circle(radius = 20.0) {
                                                            fill = Color.BLACK
                                                        }
                                                    }
                                                }
                                                buttons0.clear()
                                                if (((stage) && (board[cell]!!.color == 1)) ||
                                                        ((!stage) && (board[cell]!!.color == 2))) {
                                                    listener.mustBite(cell)
                                                    if (board.mustBite(cell)) {
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
                                                        val set = board.nextStepSimply(cell)
                                                        if (set.isEmpty()) {
                                                            inProcess = false
                                                            statusLabel.text = if (stage)
                                                                "Black wins" else "White wins"
                                                        }
                                                        for (cells in set) {
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
                subscribe<AutoTurnEvent> {
                    when {
                        whiteComputer != null -> nextStepBest(1)
                        blackComputer != null -> nextStepBest(2)
                    }
                }
            }
        }
        startTimerIfNeeded()
    }

    override fun restart(cell: Cell) {
        updateBoardAndStatus(cell)
    }

    private fun updateBoardAndStatus(cell: Cell? = null) {
        val winner = board.win()
        statusLabel.text = when (winner) {
            1 -> {
                inProcess = false
                "White win! Press 'Restart' or 'Exit'"
            }
            2 -> {
                inProcess = false
                "Black win! Press 'Restart' or 'Exit'"
            }
            else -> "Начало игры. Белых: ${board.score().first}, Чёрных: ${board.score().second}"
        }
        if (cell == null) return
        val chip = board[cell]
        buttons[cell]?.apply {
            graphic = circle(radius = 20.0) {
                fill = when (chip) {
                    Chips.WhiteSimply -> Color.ALICEBLUE
                    Chips.BlackSimply -> Color.DARKBLUE
                    Chips.BlackDamka -> Color.GRAY
                    Chips.WhiteDamka -> Color.CORAL
                    else -> Color.BLACK
                }
            }
        }
    }

    private fun startTimerIfNeeded() {
        if (whiteComputer != null || blackComputer != null) {
            timer(daemon = true, period = 2000) {
                if (inProcess) {
                    computerToMakeTurn?.let {
                        fire(AutoTurnEvent(it))
                    }
                } else {
                    this.cancel()
                }
            }
        }
    }

    private fun reconfigureGame() {
        val dialog = ChoosePlayerDialog()
        val result = dialog.showAndWait()
        if (result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE) {
            whiteComputer = if (dialog.whiteComputer) PlayerAI(board) else null
            blackComputer = if (dialog.blackComputer) PlayerAI(board) else null
            restartGame()
        } else {
            close()
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
        startTimerIfNeeded()
    }

    private fun nextStepBest(color: Int) {
        val pair = PlayerAI(board).nextStep(color)
        if (board.mustBite(pair.first)) board.biteOfCell(pair.first) else
            board.nextStepSimply(pair.first)
        board.delete(pair.second)
        stage = !stage
        for (x in 0 until rowsNumber)
            for (y in 0 until columnsNumber)
                updateBoardAndStatus(Cell(x, y))
    }
}