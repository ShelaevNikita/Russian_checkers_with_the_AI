package core

enum class Chips(val count: Int, val color: Int) {
    BlackSimply(1, 2),
    BlackDamka(8, 2),
    WhiteSimply(1, 1),
    WhiteDamka(8, 1),
    NO(Int.MAX_VALUE, 0);
}