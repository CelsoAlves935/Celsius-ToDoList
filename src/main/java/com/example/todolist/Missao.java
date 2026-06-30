package com.example.todolist;

import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import java.util.List;

public class Missao {
    private String categoria; // Não é mais final, pode ser alterada
    private VBox cardVisual;
    private List<CheckBox> checkboxesSalvos;

    public Missao(String categoria, VBox cardVisual, List<CheckBox> checkboxesSalvos) {
        this.categoria = categoria;
        this.cardVisual = cardVisual;
        this.checkboxesSalvos = checkboxesSalvos;
    }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; } // Permite mover de aba
    public VBox getCardVisual() { return cardVisual; }
    public List<CheckBox> getCheckboxesSalvos() { return checkboxesSalvos; }
}