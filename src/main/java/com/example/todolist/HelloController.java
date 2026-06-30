package com.example.todolist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HelloController {

    @FXML private TextField campoNovaTarefa;
    @FXML private FlowPane mosaicoMissoes;

    @FXML private VBox overlayPopup;
    @FXML private Label lblPopupTitulo;
    @FXML private TextField campoNovaEtapa;
    @FXML private VBox containerEtapas;

    private String categoriaAtual = "IMPORTANTES";
    private final List<Missao> bancoDeMissoes = new ArrayList<>();

    private String tituloTemporario = "";
    private final List<CheckBox> checkboxesTemporarios = new ArrayList<>();
    private Missao missaoEmExibicao = null;

    @FXML
    protected void onCategoriaClick(ActionEvent event) {
        Button botaoClicado = (Button) event.getSource();
        if (botaoClicado.getUserData() != null) {
            categoriaAtual = botaoClicado.getUserData().toString();
            atualizarMosaicoNaTela();
        }
    }

    @FXML
    protected void onAdicionarTarefaClick() {
        String textoInput = campoNovaTarefa.getText().trim();

        if (!textoInput.isEmpty()) {
            tituloTemporario = textoInput.toUpperCase();
            missaoEmExibicao = null;

            lblPopupTitulo.setText(tituloTemporario);
            containerEtapas.getChildren().clear();
            checkboxesTemporarios.clear();

            overlayPopup.setVisible(true);
            campoNovaTarefa.clear();
        }
    }

    @FXML
    protected void onAdicionarEtapaClick() {
        String etapaTexto = campoNovaEtapa.getText().trim();
        if (!etapaTexto.isEmpty()) {
            CheckBox cb = new CheckBox(etapaTexto);
            cb.getStyleClass().add("popup-checkbox");

            cb.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (missaoEmExibicao != null) {
                    calcularEAtualizarProgresso(missaoEmExibicao);
                }
            });

            if (missaoEmExibicao == null) {
                checkboxesTemporarios.add(cb);
                renderizarLinhaEtapaNoPopUp(cb, checkboxesTemporarios);
            } else {
                missaoEmExibicao.getCheckboxesSalvos().add(cb);
                renderizarLinhaEtapaNoPopUp(cb, missaoEmExibicao.getCheckboxesSalvos());
                calcularEAtualizarProgresso(missaoEmExibicao);
            }
            campoNovaEtapa.clear();
        }
    }

    private void renderizarLinhaEtapaNoPopUp(CheckBox cb, List<CheckBox> listaOrigem) {
        HBox linha = new HBox(10);
        linha.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        HBox.setHgrow(cb, Priority.ALWAYS);
        cb.setMaxWidth(Double.MAX_VALUE);

        // Botão de deletar etapa aprimorado via CSS (.btn-deletar-etapa)
        Button btnDeletar = new Button("REMOVER");
        btnDeletar.getStyleClass().add("btn-deletar-etapa");

        btnDeletar.setOnAction(e -> {
            containerEtapas.getChildren().remove(linha);
            listaOrigem.remove(cb);
            if (missaoEmExibicao != null) {
                calcularEAtualizarProgresso(missaoEmExibicao);
            }
        });

        linha.getChildren().addAll(cb, btnDeletar);
        containerEtapas.getChildren().add(linha);
    }

    @FXML
    protected void onSalvarMissaoCompletaClick() {
        if (missaoEmExibicao != null) {
            calcularEAtualizarProgresso(missaoEmExibicao);
            overlayPopup.setVisible(false);
            missaoEmExibicao = null;
            return;
        }

        try {
            java.net.URL urlFxml = HelloController.class.getResource("missao-card.fxml");
            if (urlFxml == null) return;

            FXMLLoader loader = new FXMLLoader(urlFxml);
            VBox novoCard = loader.load();

            Label lblTitulo = (Label) novoCard.lookup("#tituloCard");
            if (lblTitulo != null) lblTitulo.setText(tituloTemporario);

            Label lblDesc = (Label) novoCard.lookup("#descricaoCard");
            if (lblDesc != null) lblDesc.setText("Diretrizes de Operação Ativas.");

            List<CheckBox> cbsDestaMissao = new ArrayList<>(checkboxesTemporarios);
            Missao novaMissao = new Missao(categoriaAtual, novoCard, cbsDestaMissao);

            novoCard.setOnMouseClicked(e -> reabrirPopupComPassosSalvos(novaMissao));

            bancoDeMissoes.add(novaMissao);
            mosaicoMissoes.getChildren().add(novoCard);

            calcularEAtualizarProgresso(novaMissao);
            overlayPopup.setVisible(false);

        } catch (IOException e) {
            System.out.println("Erro crítico ao forjar card: " + e.getMessage());
        }
    }

    // CRUD: Ação de Deletar a Missão inteira da existência
    @FXML
    protected void onDeletarMissaoCompletaClick() {
        if (missaoEmExibicao != null) {
            bancoDeMissoes.remove(missaoEmExibicao); // Remove do banco
            mosaicoMissoes.getChildren().remove(missaoEmExibicao.getCardVisual()); // Remove da tela
            overlayPopup.setVisible(false);
            missaoEmExibicao = null;
        } else {
            // Se o usuário clicar em deletar enquanto está criando, apenas fecha sem salvar nada
            overlayPopup.setVisible(false);
        }
    }

    private void reabrirPopupComPassosSalvos(Missao missao) {
        missaoEmExibicao = missao;
        lblPopupTitulo.setText(((Label) missao.getCardVisual().lookup("#tituloCard")).getText());
        containerEtapas.getChildren().clear();

        for (CheckBox cb : missao.getCheckboxesSalvos()) {
            renderizarLinhaEtapaNoPopUp(cb, missao.getCheckboxesSalvos());
        }
        overlayPopup.setVisible(true);
    }

    private void calcularEAtualizarProgresso(Missao missao) {
        if (missao == null) return;

        List<CheckBox> totalCheckboxes = missao.getCheckboxesSalvos();
        double total = totalCheckboxes.size();
        double concluidos = 0;

        for (CheckBox cb : totalCheckboxes) {
            if (cb.isSelected()) concluidos++;
        }

        double porcentagem = (total > 0) ? (concluidos / total) : 0.0;

        Label lblStatus = (Label) missao.getCardVisual().lookup(".card-texto-status");
        Label lblContagem = (Label) missao.getCardVisual().lookup(".card-texto-progresso");
        VBox barraPreenchimento = (VBox) missao.getCardVisual().lookup(".card-progresso-preenchimento");

        if (lblContagem != null) lblContagem.setText((int)concluidos + "/" + (int)total + " Etapas");
        if (barraPreenchimento != null) {
            barraPreenchimento.setPrefWidth(250.0 * porcentagem);
        }

        // Se a missão já foi enviada para o arquivo de completas, mantém o status fixado
        if (missao.getCategoria().equals("COMPLETAS")) {
            if (lblStatus != null) lblStatus.setText("STATUS: ARQUIVADA 🏆");
            return;
        }

        // MECÂNICA DE CONCLUÍDA + BOTÃO DE ENVIAR PARA NOVA ABA
        if (porcentagem == 1.0 && total > 0) {
            if (lblStatus != null) lblStatus.setText("STATUS: CONCLUÍDA");
            if (lblStatus != null) lblStatus.setStyle("-fx-text-fill: #00FF66;");

            if (!missao.getCardVisual().getStyleClass().contains("card-concluido")) {
                missao.getCardVisual().getStyleClass().add("card-concluido");

                // CRIA O BOTÃO DINÂMICO DE ARQUIVAMENTO DENTRO DO CARD
                Button btnArquivar = new Button("ARQUIVAR MISSÃO ➔");
                btnArquivar.getStyleClass().add("btn-arquivar-card");
                btnArquivar.setOnAction(e -> {
                    missao.setCategoria("COMPLETAS"); // Altera o destino no banco
                    atualizarMosaicoNaTela(); // Remove o card do painel ativo na hora!
                });

                missao.getCardVisual().getChildren().add(btnArquivar);
            }
        } else {
            if (lblStatus != null) lblStatus.setText("STATUS: EM EXECUÇÃO");
            if (lblStatus != null) lblStatus.setStyle("-fx-text-fill: #45f3ff;");
            missao.getCardVisual().getStyleClass().remove("card-concluido");

            // Remove o botão de arquivamento caso o usuário desmarque um passo
            missao.getCardVisual().getChildren().removeIf(node -> node instanceof Button);
        }
    }

    private void atualizarMosaicoNaTela() {
        mosaicoMissoes.getChildren().clear();
        for (Missao missao : bancoDeMissoes) {
            if (missao.getCategoria().equals(categoriaAtual)) {
                mosaicoMissoes.getChildren().add(missao.getCardVisual());
            }
        }
    }

    @FXML
    protected void onFecharPopupClick() {
        if (missaoEmExibicao != null) {
            calcularEAtualizarProgresso(missaoEmExibicao);
        }
        overlayPopup.setVisible(false);
        missaoEmExibicao = null;
    }
}