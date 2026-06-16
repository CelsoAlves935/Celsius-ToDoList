package com.example.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class HelloController {

    // Conecta com o campo de digitação do usuario
    @FXML
    private TextField campoNovaTarefa;

    @FXML
    private ListView<String> listaTarefas;

    @FXML
    protected void onAdicionarTarefaClick() {
        String textoTarefa = campoNovaTarefa.getText().trim();

        if (!textoTarefa.isEmpty()) {
            listaTarefas.getItems().add(textoTarefa);

            campoNovaTarefa.clear();
        }
    }
}
