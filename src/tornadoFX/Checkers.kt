import javafx.application.Application
import tornadofx.App

class Checkers : App(CheckersView::class)

fun main() {
    Application.launch(Checkers::class.java)
}