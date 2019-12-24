import javafx.application.Application
import javafx.scene.control.ButtonBar
import javafx.stage.Stage
import tornadofx.App

class Checkers : App(CheckersView::class) {

    var blackHuman = true

    var whiteHuman = true

    override fun start(stage: Stage) {
        val dialog = ChoosePlayerDialog()
        val result = dialog.showAndWait()
        if (result.isPresent && result.get().buttonData == ButtonBar.ButtonData.OK_DONE) {
            whiteHuman = !dialog.whiteComputer
            blackHuman = !dialog.blackComputer
            super.start(stage)
        }
    }
}

fun main() {
    Application.launch(Checkers::class.java)
}