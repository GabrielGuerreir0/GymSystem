package dao;

import db.DatabaseConnection;
import model.Aula;
import model.Aluno;
import model.PersonalTrainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AulaDAO {

    // Método para adicionar uma nova aula ao banco de dados
    public void addAula(Aula aula) {
        String sql = "INSERT INTO aula (nome_aula, data, duracao, personal_id) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, aula.getNomeAula());
            stmt.setDate(2, aula.getData());
            stmt.setInt(3, aula.getDuracao());

            // Verifica se um Personal Trainer foi selecionado
            if (aula.getPersonalTrainer() != null) {
                stmt.setInt(4, aula.getPersonalTrainer().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.executeUpdate();

            // Obter o ID gerado para a aula
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                aula.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar os dados de uma aula existente
    public void updateAula(Aula aula) {
        String sql = "UPDATE aula SET nome_aula = ?, data = ?, duracao = ?, personal_id = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, aula.getNomeAula());
            stmt.setDate(2, aula.getData());
            stmt.setInt(3, aula.getDuracao());

            // Verifica se um Personal Trainer foi selecionado
            if (aula.getPersonalTrainer() != null) {
                stmt.setInt(4, aula.getPersonalTrainer().getId());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setInt(5, aula.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir uma aula do banco de dados
    public void deleteAula(int aulaId) {
        String sql = "DELETE FROM aula WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, aulaId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar uma aula por ID
    public Aula getAulaById(int id) {
        Aula aula = null;
        String sql = "SELECT a.id, a.nome_aula, a.data, a.duracao, pt.id as personal_id, p.nome as personal_nome " +
                "FROM aula a " +
                "LEFT JOIN personal_trainer pt ON a.personal_id = pt.id " +
                "LEFT JOIN pessoa p ON pt.pessoa_id = p.id " +
                "WHERE a.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                aula = new Aula();
                aula.setId(rs.getInt("id"));
                aula.setNomeAula(rs.getString("nome_aula"));
                aula.setData(rs.getDate("data"));
                aula.setDuracao(rs.getInt("duracao"));

                // Verificar se a aula tem um personal associado
                int personalId = rs.getInt("personal_id");
                if (!rs.wasNull()) {
                    PersonalTrainer personal = new PersonalTrainer();
                    personal.setId(personalId);
                    personal.setNome(rs.getString("personal_nome"));  // Definir o nome do personal
                    aula.setPersonalTrainer(personal);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aula;
    }

    // Método para buscar todas as aulas com filtro de nome
    public List<Aula> getAulasByName(String nome) {
        List<Aula> aulas = new ArrayList<>();
        String sql = "SELECT a.id, a.nome_aula, a.data, a.duracao, pt.id as personal_id, p.nome as personal_nome " +
                "FROM aula a " +
                "LEFT JOIN personal_trainer pt ON a.personal_id = pt.id " +
                "LEFT JOIN pessoa p ON pt.pessoa_id = p.id " +
                "WHERE a.nome_aula LIKE ?";  // Busca pelo nome da aula

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");  // Filtro de nome com "LIKE"
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Aula aula = new Aula();
                aula.setId(rs.getInt("id"));
                aula.setNomeAula(rs.getString("nome_aula"));
                aula.setData(rs.getDate("data"));
                aula.setDuracao(rs.getInt("duracao"));

                // Verificar se a aula tem um personal associado
                int personalId = rs.getInt("personal_id");
                if (!rs.wasNull()) {
                    PersonalTrainer personal = new PersonalTrainer();
                    personal.setId(personalId);
                    personal.setNome(rs.getString("personal_nome"));  // Definir o nome do personal
                    aula.setPersonalTrainer(personal);
                }

                aulas.add(aula);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return aulas;
    }

    // Método para associar um aluno a uma aula no banco de dados
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

    // Método para buscar alunos associados a uma aula
    public List<Aluno> getAlunosByAulaId(int aulaId) {
        List<Aluno> alunos = new ArrayList<>();
        String sql = "SELECT a.id, p.nome, p.cpf, p.telefone, p.email " +
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
                aluno.setCPF(rs.getString("cpf"));
                aluno.setTelefone(rs.getString("telefone"));
                aluno.setEmail(rs.getString("email"));

                alunos.add(aluno);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return alunos;
    }
}
