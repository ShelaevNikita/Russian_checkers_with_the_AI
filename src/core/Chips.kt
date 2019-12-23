package core

enum class Chips(val count: Int, val color: Int) {
    Black_Simply(1, 2),
    Black_Damka(8, 2),
    White_Simply(1, 1),
    White_Damka(8, 1),
    NO(Int.MAX_VALUE, 0);
}