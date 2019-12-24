import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.scene.control.Dialog
import javafx.scene.layout.Priority
import tornadofx.*

class ChoosePlayerDialog : Dialog<ButtonType>() {
    private val model = ViewModel()

    private val blackPlayer = model.bind { SimpleStringProperty() }
    val blackComputer: Boolean get() = blackPlayer.value == "Computer"

    private val whitePlayer = model.bind { SimpleStringProperty() }
    val whiteComputer: Boolean get() = whitePlayer.value == "Computer"

    init {
        title = "CheckersWithAI"
        with (dialogPane) {
            headerText = "Choose players"
            buttonTypes.add(ButtonType("Start Game", ButtonBar.ButtonData.OK_DONE))
            buttonTypes.add(ButtonType("Quit", ButtonBar.ButtonData.CANCEL_CLOSE))
            content = hbox {
                vbox {
                    label("Black")
                    togglegroup {
                        bind(blackPlayer)
                        radiobutton("Human") {
                            isSelected = true
                        }
                        radiobutton("Computer")
                    }
                }
                spacer(Priority.ALWAYS)
                vbox {
                    label("White")
                    togglegroup {
                        bind(whitePlayer)
                        radiobutton("Human")
                        radiobutton("Computer") {
                            isSelected = true
                        }
                    }
                }
            }
        }
    }
}