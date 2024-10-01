package dao;

import db.DatabaseConnection;
import model.Aluno;
import model.PersonalTrainer;
import model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlunoDAO {

    // Método para adicionar um novo aluno ao banco de dados
    public void addAluno(Aluno aluno) {
        String sql = "INSERT INTO aluno (pessoa_id, data_inicio, idade, personal_id, plano_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            // Inserir os dados da pessoa associada ao aluno na tabela Pessoa
            String pessoaSql = "INSERT INTO pessoa (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
            PreparedStatement pessoaStmt = connection.prepareStatement(pessoaSql, PreparedStatement.RETURN_GENERATED_KEYS);
            pessoaStmt.setString(1, aluno.getNome());
            pessoaStmt.setString(2, aluno.getCPF());
            pessoaStmt.setString(3, aluno.getTelefone());
            pessoaStmt.setString(4, aluno.getEmail());
            pessoaStmt.executeUpdate();

            // Obter o ID gerado automaticamente para a pessoa
            ResultSet rs = pessoaStmt.getGeneratedKeys();
            if (rs.next()) {
                int pessoaId = rs.getInt(1);
                aluno.setId(pessoaId);
            }

            // Inserir os dados na tabela Aluno
            stmt.setInt(1, aluno.getId());
            stmt.setDate(2, aluno.getDataInicio());
            stmt.setInt(3, aluno.getIdade());

            // Personal Trainer
            if (aluno.getPersonal() != null) {
                stmt.setInt(4, aluno.getPersonal().getId());
            } else {
                stmt.setNull(4, java.sql.Types.INTEGER);  // Personal_id como NULL se não for selecionado
            }

            // Plano
            if (aluno.getPlano() != null) {
                stmt.setInt(5, aluno.getPlano().getId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);  // Plano_id como NULL se não for selecionado
            }

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar os dados de um aluno existente
    public void updateAluno(Aluno aluno) {
        String sql = "UPDATE aluno a JOIN pessoa p ON a.pessoa_id = p.id " +
                "SET p.nome = ?, p.cpf = ?, p.telefone = ?, p.email = ?, a.data_inicio = ?, a.idade = ?, a.personal_id = ?, a.plano_id = ? " +
                "WHERE a.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, aluno.getNome());
            stmt.setString(2, aluno.getCPF());
            stmt.setString(3, aluno.getTelefone());
            stmt.setString(4, aluno.getEmail());
            stmt.setDate(5, aluno.getDataInicio());
            stmt.setInt(6, aluno.getIdade());

            // Personal Trainer
            if (aluno.getPersonal() != null) {
                stmt.setInt(7, aluno.getPersonal().getId());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);  // Personal_id como NULL se não for selecionado
            }

            // Plano
            if (aluno.getPlano() != null) {
                stmt.setInt(8, aluno.getPlano().getId());
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);  // Plano_id como NULL se não for selecionado
            }

            stmt.setInt(9, aluno.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir um aluno do banco de dados
    public void deleteAluno(int alunoId) {
        String sql = "DELETE FROM aluno WHERE id = ?";
        String pessoaSql = "DELETE FROM pessoa WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement pessoaStmt = connection.prepareStatement(pessoaSql)) {

            // Exclui o registro da tabela Aluno
            stmt.setInt(1, alunoId);
            stmt.executeUpdate();

            // Exclui o registro da tabela Pessoa associado
            pessoaStmt.setInt(1, alunoId);
            pessoaStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar um aluno por ID
    public Aluno getAlunoById(int id) {
        Aluno aluno = null;
        String sql = "SELECT a.id, p.nome, p.cpf, p.telefone, p.email, a.data_inicio, a.idade, " +
                "pt.id AS personal_id, pe.nome AS personal_nome, " +
                "pl.id AS plano_id, pl.tipo AS plano_tipo " +
                "FROM aluno a " +
                "JOIN pessoa p ON a.pessoa_id = p.id " +
                "LEFT JOIN personal_trainer pt ON a.personal_id = pt.id " +
                "LEFT JOIN pessoa pe ON pt.pessoa_id = pe.id " +
                "LEFT JOIN plano pl ON a.plano_id = pl.id " +
                "WHERE a.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setCPF(rs.getString("cpf"));
                aluno.setTelefone(rs.getString("telefone"));
                aluno.setEmail(rs.getString("email"));
                aluno.setDataInicio(rs.getDate("data_inicio"));
                aluno.setIdade(rs.getInt("idade"));

                // Verificar se o aluno tem um personal associado
                int personalId = rs.getInt("personal_id");
                if (!rs.wasNull()) {
                    PersonalTrainer personal = new PersonalTrainer();
                    personal.setId(personalId);
                    personal.setNome(rs.getString("personal_nome"));
                    aluno.setPersonal(personal);
                }

                // Verificar se o aluno tem um plano associado
                int planoId = rs.getInt("plano_id");
                if (!rs.wasNull()) {
                    Plano plano = new Plano();
                    plano.setId(planoId);
                    plano.setTipo(rs.getString("plano_tipo"));
                    aluno.setPlano(plano);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aluno;
    }

    // Método para buscar todos os alunos com filtro de nome
    public List<Aluno> getAlunosByName(String nome) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id, p.nome, a.data_inicio, a.idade, " +
                "pt.id AS personal_id, pe.nome AS personal_nome, " +
                "pl.id AS plano_id, pl.tipo AS plano_tipo " +
                "FROM aluno a " +
                "JOIN pessoa p ON a.pessoa_id = p.id " +
                "LEFT JOIN personal_trainer pt ON a.personal_id = pt.id " +
                "LEFT JOIN pessoa pe ON pt.pessoa_id = pe.id " +
                "LEFT JOIN plano pl ON a.plano_id = pl.id " +
                "WHERE p.nome LIKE ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setDataInicio(rs.getDate("data_inicio"));
                aluno.setIdade(rs.getInt("idade"));

                // Verificar se o aluno tem um personal associado
                int personalId = rs.getInt("personal_id");
                if (!rs.wasNull()) {
                    PersonalTrainer personal = new PersonalTrainer();
                    personal.setId(personalId);
                    personal.setNome(rs.getString("personal_nome"));
                    aluno.setPersonal(personal);
                }

                // Verificar se o aluno tem um plano associado
                int planoId = rs.getInt("plano_id");
                if (!rs.wasNull()) {
                    Plano plano = new Plano();
                    plano.setId(planoId);
                    plano.setTipo(rs.getString("plano_tipo"));
                    aluno.setPlano(plano);
                }

                alunos.add(aluno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }

    // Método para buscar todos os alunos associados a uma aula específica
    public List<Aluno> getAlunosByAulaId(int aulaId) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id, p.nome, a.data_inicio, a.idade " +
                "FROM aluno a " +
                "JOIN pessoa p ON a.pessoa_id = p.id " +
                "JOIN aula_aluno aa ON a.id = aa.aluno_id " +
                "WHERE aa.aula_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, aulaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setDataInicio(rs.getDate("data_inicio"));
                aluno.setIdade(rs.getInt("idade"));

                alunos.add(aluno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }

    // Método para adicionar aluno a uma aula específica
    public void addAlunoNaAula(int alunoId, int aulaId) {
        String sql = "INSERT INTO aula_aluno (aula_id, aluno_id) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, aulaId);
            stmt.setInt(2, alunoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para remover aluno de uma aula específica
    public void removerAlunoDaAula(int alunoId, int aulaId) {
        String deleteSql = "DELETE FROM aula_aluno WHERE aluno_id = ? AND aula_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(deleteSql)) {

            stmt.setInt(1, alunoId);
            stmt.setInt(2, aulaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir todos os alunos de uma aula
    public void excluirAlunosDaAula(int aulaId) {
        String sql = "DELETE FROM aula_aluno WHERE aula_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, aulaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar todos os alunos disponíveis
    public List<Aluno> getAllAlunos() {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id, p.nome, a.data_inicio, a.idade " +
                "FROM aluno a " +
                "JOIN pessoa p ON a.pessoa_id = p.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Aluno aluno = new Aluno();
                aluno.setId(rs.getInt("id"));
                aluno.setNome(rs.getString("nome"));
                aluno.setDataInicio(rs.getDate("data_inicio"));
                aluno.setIdade(rs.getInt("idade"));

                alunos.add(aluno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }
}
