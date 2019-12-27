import core.*
import javafx.scene.control.Button
import javafx.scene.control.ButtonBar
import javafx.scene.control.Label
import javafx.scene.layout.BorderPane
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

    private var whiteComputer =
            if ((app as Checkers).whiteHuman) null else PlayerAI(board)

    private var blackComputer =
            if ((app as Checkers).blackHuman) null else PlayerAI(board)

    private val computerToMakeTurn: PlayerAI?
        get() = if (board.turn) whiteComputer else blackComputer

    private lateinit var statusLabel: Label

    private var inProcess = true

    private val buttons0 = mutableSetOf<Cell>()

    override val root = BorderPane()

    init {
        title = "CheckersWithAI"

        val listener = BoardBasedCellListener(board)

        board.startGame()

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
                    text = "Начало игры. Ход белых"
                    font = Font(30.0)
                }
            }
            center {
                gridpane {
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
                                            backgroundColor += Color.GRAY
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
                                            }
                                        }
                                        for (cell0 in buttons0) {
                                            buttons[cell0]?.apply {
                                                graphic = circle(radius = 20.0) {
                                                    fill = Color.GRAY
                                                }
                                            }
                                        }
                                        buttons0.clear()
                                        if (((board.turn) && (board[cell]!!.color == 1)) ||
                                                ((!board.turn) && (board[cell]!!.color == 2))) {
                                            listener.mustBite()
                                            if (board.mustBite()) {
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
                                            statusLabel.text = if (board.turn) "Ход белых" else "Ход чёрных"
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
                }
            }
            subscribe<AutoTurnEvent> {
                if (whiteComputer != null) nextStepBest(true)
                if (blackComputer != null) nextStepBest(false)
            }
        }
        startTimerIfNeeded()
        for (x in 0 until rowsNumber)
            for (y in 0 until columnsNumber)
                updateBoardAndStatus(Cell(x, y))
    }

    override fun restart(cell: Cell) {
        updateBoardAndStatus(cell)
    }

    private fun updateBoardAndStatus(cell: Cell? = null) {
        when (board.win()) {
            1 -> {
                inProcess = false
                statusLabel.text = "White wins! Press 'Restart' or 'Exit'"
            }
            2 -> {
                inProcess = false
                statusLabel.text = "Black wins! Press 'Restart' or 'Exit'"
            }
        }
        if (cell == null) return
        val chip = board[cell]
        buttons[cell]?.apply {
            graphic = circle(radius = 20.0) {
                fill = when (chip) {
                    Chips.WhiteSimply -> Color.ALICEBLUE
                    Chips.BlackSimply -> Color.BLACK
                    Chips.BlackDamka -> Color.DARKBLUE
                    Chips.WhiteDamka -> Color.GOLD
                    else -> Color.GRAY
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
        board.turn = true
        buttons0.clear()
        board.startGame()
        for (x in 0 until columnsNumber)
            for (y in 0 until rowsNumber)
                updateBoardAndStatus(Cell(x, y))
        inProcess = true
        startTimerIfNeeded()
    }

    private fun nextStepBest(turn: Boolean) {
        val pair = PlayerAI(board).nextStep(turn, 6)
        if (pair == null) {
            inProcess = false
            statusLabel.text = if (board.turn) "Black wins" else "White wins"
        } else {
            println(pair)
            if (board.mustBite()) board.biteOfCell(pair.first) else
                board.nextStepSimply(pair.first)
            board.move(pair.second)
            for (x in 0 until rowsNumber)
                for (y in 0 until columnsNumber)
                    updateBoardAndStatus(Cell(x, y))
            if (inProcess) {
                statusLabel.text = if (board.turn) "Ход белых" else "Ход чёрных"
                statusLabel.text += ". Белых: ${board.score().first}," +
                        " Чёрных: ${board.score().second}"
            }
        }
    }
}