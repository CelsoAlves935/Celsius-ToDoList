package com.example.todolist;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.paint.Color;

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

    @FXML
    protected void onCompletarMissaoClick() {
        // 1. Descobrir qual linha o usuário clicou
        int indiceSelecionado = listaTarefas.getSelectionModel().getSelectedIndex();

        // 2. O JavaFx retorna -1 se o usuário clicar no botão sem selecionar a missão.
        if (indiceSelecionado >= 0) {
            listaTarefas.getItems().remove(indiceSelecionado);
            exibirPopupGif("sucesso.gif");
        } else {
            System.out.println("Nenhuma missão foi selecionada para ser completada!");

            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("ERRO DE SISTEMA");
            alerta.setHeaderText("Ação Inválida");
            alerta.setContentText("Nenhuma missão foi selecionado no painel tático. Selecione uma missão");

            alerta.showAndWait();
        }


    }

    private void exibirPopupGif(String nomeArquivoGif) {
        try {
            // 1. Carrega o GIF da pasta de recursos
            Image imagem = new Image(getClass().getResourceAsStream(nomeArquivoGif));
            ImageView imageView = new ImageView(imagem);
            imageView.setFitWidth(400); // Ajuste o tamanho conforme o seu GIF
            imageView.setPreserveRatio(true);

            // 2. Coloca o GIF dentro de um container
            StackPane painel = new StackPane(imageView);
            painel.setStyle("-fx-background-color: transparent;"); // Fundo transparente

            // 3. Cria uma nova janela (Stage) para o Pop-up
            Stage popupStage = new Stage();
            // O correto é passar Color.TRANSPARENT para a cena nascer sem fundo
            Scene cenaPopup = new Scene(painel, 400, 300, Color.TRANSPARENT);
            cenaPopup.setFill(null); // Remove o fundo branco padrão da cena

            popupStage.setScene(cenaPopup);
            popupStage.initStyle(StageStyle.TRANSPARENT); // Remove as bordas do Windows (X, minimizar, etc)
            popupStage.setAlwaysOnTop(true); // Faz o GIF aparecer na frente de tudo

            // 4. Mostra o GIF
            popupStage.show();

            // 5. Cronômetro: Fecha a janela automaticamente após 3 segundos
            PauseTransition delay = new PauseTransition(Duration.seconds(3));
            delay.setOnFinished(event -> popupStage.close());
            delay.play();

        } catch (Exception e) {
            System.out.println("Erro ao carregar o GIF: " + e.getMessage());
        }
    }

    @FXML
    protected void onFracassarMissaoClick() {
        // 1. Pega a missão selecionada
        int indiceSelecionado = listaTarefas.getSelectionModel().getSelectedIndex();

        if (indiceSelecionado >= 0) {
            // 2. Remove a missão que falhou
            listaTarefas.getItems().remove(indiceSelecionado);

            // 3. Dispara o GIF de Fracasso (ex: YOU DIED ou MISSION FAILED)
            exibirPopupGif("fracasso.gif");
        } else {
            // Exibe o mesmo pop-up de aviso se nada estiver selecionado
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("ERRO DE SISTEMA");
            alerta.setHeaderText("Ação Inválida!");
            alerta.setContentText("Nenhuma missão foi selecionada para ser abortada. Selecione um alvo no painel.");
            alerta.showAndWait();
        }
    }
}
