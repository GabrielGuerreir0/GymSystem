package view.aula;

import dao.AulaDAO;
import dao.PersonalTrainerDAO;
import dao.AlunoDAO;
import model.Aula;
import model.PersonalTrainer;
import model.Aluno;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class AulaFormDialog extends JDialog {
    private JTextField nomeAulaField, dataField, duracaoField, buscaAlunoField;
    private JComboBox<PersonalTrainer> personalComboBox;  // ComboBox para selecionar o Personal Trainer
    private JList<Aluno> alunosList;  // Lista de alunos que serão adicionados à aula
    private JList<Aluno> alunosDisponiveisList;  // Lista de alunos disponíveis para adicionar
    private DefaultListModel<Aluno> alunosListModel;
    private DefaultListModel<Aluno> alunosDisponiveisModel;
    private Aula aula;
    private AulaDAO aulaDAO;
    private boolean isCadastro;  // Para verificar se é um cadastro ou edição
    private JButton adicionarAlunoButton; // Botão para adicionar mais alunos depois
    private JButton excluirAlunoButton; // Botão para excluir alunos da aula

    public AulaFormDialog(int aulaId, boolean isCadastro) {
        this.isCadastro = isCadastro;
        aulaDAO = new AulaDAO(); // Instanciar o DAO
        setTitle(isCadastro ? "Cadastrar Aula" : "Editar Aula");
        setSize(700, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new GridLayout(9, 2, 10, 10));

        // Se não for cadastro, buscamos os dados da aula no banco
        if (!isCadastro) {
            aula = aulaDAO.getAulaById(aulaId);
            if (aula == null) {
                JOptionPane.showMessageDialog(this, "Erro: Aula não encontrada!");
                dispose();
                return;
            }
        } else {
            aula = new Aula();  // Para cadastro de nova aula
        }

        // Campos de texto para informações da aula
        add(new JLabel("Nome da Aula:"));
        nomeAulaField = new JTextField(isCadastro ? "" : aula.getNomeAula());
        add(nomeAulaField);

        add(new JLabel("Data (yyyy-MM-dd):"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dataField = new JTextField(isCadastro ? "" : sdf.format(aula.getData()));
        add(dataField);

        add(new JLabel("Duração (min):"));
        duracaoField = new JTextField(isCadastro ? "" : String.valueOf(aula.getDuracao()));
        add(duracaoField);

        // ComboBox para selecionar o Personal Trainer
        add(new JLabel("Personal Trainer:"));
        personalComboBox = new JComboBox<>();
        carregarPersonais();  // Preencher o ComboBox com os personais disponíveis
        add(personalComboBox);

        if (!isCadastro) {
            // Selecionar o personal trainer atual no combo box
            PersonalTrainer personal = aula.getPersonalTrainer();
            if (personal != null) {
                personalComboBox.setSelectedItem(personal);
            }
        }

        // Lista de alunos associados à aula
        add(new JLabel("Alunos na Aula:"));
        alunosListModel = new DefaultListModel<>();
        alunosList = new JList<>(alunosListModel);
        alunosList.setVisibleRowCount(5);  // Exibir 5 alunos por vez na lista
        JScrollPane alunosScrollPane = new JScrollPane(alunosList);
        add(alunosScrollPane);

        // Botão para excluir aluno da aula
        excluirAlunoButton = new JButton("Excluir Aluno");
        excluirAlunoButton.addActionListener(e -> excluirAlunoDaAula());
        add(excluirAlunoButton);

        if (!isCadastro) {
            carregarAlunosDaAula();  // Carregar alunos associados à aula
        }

        // Campo de busca para filtrar os alunos disponíveis
        add(new JLabel("Buscar Aluno Disponível:"));
        buscaAlunoField = new JTextField();
        add(buscaAlunoField);

        // Lista de alunos disponíveis
        add(new JLabel("Alunos Disponíveis:"));
        alunosDisponiveisModel = new DefaultListModel<>();
        alunosDisponiveisList = new JList<>(alunosDisponiveisModel);
        alunosDisponiveisList.setVisibleRowCount(5);
        JScrollPane disponiveisScrollPane = new JScrollPane(alunosDisponiveisList);
        add(disponiveisScrollPane);

        // Carregar alunos disponíveis
        carregarAlunosDisponiveis();

        // Adicionar um listener para o campo de busca de alunos
        buscaAlunoField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filtrarAlunosDisponiveis();
            }
        });

        // Botão para adicionar novos alunos
        adicionarAlunoButton = new JButton("Adicionar Aluno");
        adicionarAlunoButton.addActionListener(e -> adicionarAlunoNaAula());
        add(adicionarAlunoButton);

        // Botões de ação
        JButton salvarButton = new JButton(isCadastro ? "Cadastrar" : "Salvar");
        JButton cancelarButton = new JButton("Cancelar");

        salvarButton.addActionListener(e -> salvarAula());
        cancelarButton.addActionListener(e -> dispose());

        add(salvarButton);
        add(cancelarButton);
    }

    // Método para carregar os personal trainers no ComboBox
    private void carregarPersonais() {
        PersonalTrainerDAO personalDAO = new PersonalTrainerDAO();
        List<PersonalTrainer> personalTrainers = personalDAO.getAllPersonalTrainers();
        for (PersonalTrainer personal : personalTrainers) {
            personalComboBox.addItem(personal);  // Carregar os personais no ComboBox
        }
    }

    // Método para carregar alunos que já estão na aula
    private void carregarAlunosDaAula() {
        AlunoDAO alunoDAO = new AlunoDAO();
        List<Aluno> alunosDaAula = alunoDAO.getAlunosByAulaId(aula.getId());
        alunosListModel.clear();  // Limpar a lista de alunos associados
        for (Aluno aluno : alunosDaAula) {
            alunosListModel.addElement(aluno);  // Adicionar alunos à lista
        }
    }

    // Método para carregar alunos disponíveis, excluindo os já associados
    private void carregarAlunosDisponiveis() {
        AlunoDAO alunoDAO = new AlunoDAO();
        List<Aluno> todosAlunos = alunoDAO.getAllAlunos();  // Buscar todos os alunos disponíveis
        List<Aluno> alunosAssociados = alunoDAO.getAlunosByAulaId(aula.getId());  // Buscar alunos já na aula

        alunosDisponiveisModel.clear();  // Limpar a lista de disponíveis

        // Filtrar alunos que já estão na aula
        for (Aluno aluno : todosAlunos) {
            if (!alunosAssociados.contains(aluno)) {
                alunosDisponiveisModel.addElement(aluno);  // Adicionar à lista de disponíveis
            }
        }
    }

    // Método para adicionar aluno na aula
    private void adicionarAlunoNaAula() {
        Aluno alunoSelecionado = alunosDisponiveisList.getSelectedValue();
        if (alunoSelecionado != null && !alunosListModel.contains(alunoSelecionado)) {
            alunosListModel.addElement(alunoSelecionado);  // Adicionar aluno à lista da aula
        }
    }

    // Método para excluir aluno da aula
    private void excluirAlunoDaAula() {
        Aluno alunoSelecionado = alunosList.getSelectedValue();
        if (alunoSelecionado != null) {
            alunosListModel.removeElement(alunoSelecionado);  // Remover aluno da lista da aula
        }
    }

    // Método para salvar a aula
    private void salvarAula() {
        aula.setNomeAula(nomeAulaField.getText());

        // Converter a data
        try {
            java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataField.getText());
            aula.setData(new Date(utilDate.getTime()));
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida!");
            return;
        }

        aula.setDuracao(Integer.parseInt(duracaoField.getText()));

        // Obter o personal selecionado no ComboBox
        PersonalTrainer personal = (PersonalTrainer) personalComboBox.getSelectedItem();
        aula.setPersonalTrainer(personal);

        AlunoDAO alunoDAO = new AlunoDAO();
        if (isCadastro) {
            // Adicionar nova aula no banco de dados
            aulaDAO.addAula(aula);
            JOptionPane.showMessageDialog(this, "Aula cadastrada com sucesso!");
        } else {
            // Atualizar aula existente no banco de dados
            aulaDAO.updateAula(aula);
            JOptionPane.showMessageDialog(this, "Aula atualizada com sucesso!");
        }

        // Atualizar os alunos da aula
        alunoDAO.excluirAlunosDaAula(aula.getId());
        for (int i = 0; i < alunosListModel.size(); i++) {
            Aluno aluno = alunosListModel.getElementAt(i);
            alunoDAO.addAlunoNaAula(aluno.getId(), aula.getId());
        }

        dispose();  // Fechar o dialog após salvar
    }

    // Método para filtrar alunos disponíveis com base no campo de busca
    private void filtrarAlunosDisponiveis() {
        String filtro = buscaAlunoField.getText().trim().toLowerCase();
        alunosDisponiveisModel.clear();  // Limpar a lista de disponíveis

        AlunoDAO alunoDAO = new AlunoDAO();
        List<Aluno> todosAlunos = alunoDAO.getAllAlunos();
        List<Aluno> alunosAssociados = alunoDAO.getAlunosByAulaId(aula.getId());

        for (Aluno aluno : todosAlunos) {
            if (!alunosAssociados.contains(aluno) && aluno.getNome().toLowerCase().contains(filtro)) {
                alunosDisponiveisModel.addElement(aluno);
            }
        }
    }
}
