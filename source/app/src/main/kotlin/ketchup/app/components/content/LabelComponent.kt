package ketchup.app.components.content

import javafx.scene.control.Label

class LabelComponent: Label {
    constructor(label: String) {
        this.prefHeight = 17.0
        this.prefWidth = 70.0
        this.text = label
    }
}