package dao;

import db.DatabaseConnection;
import model.PersonalTrainer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonalTrainerDAO {

    // Método para adicionar um novo personal trainer ao banco de dados
    public void addPersonalTrainer(PersonalTrainer personalTrainer) {
        String pessoaSql = "INSERT INTO pessoa (nome, cpf, telefone, email) VALUES (?, ?, ?, ?)";
        String personalSql = "INSERT INTO personal_trainer (pessoa_id, registro_profissional, especialidade) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pessoaStmt = connection.prepareStatement(pessoaSql, PreparedStatement.RETURN_GENERATED_KEYS);
             PreparedStatement personalStmt = connection.prepareStatement(personalSql)) {

            // Inserir os dados da pessoa
            pessoaStmt.setString(1, personalTrainer.getNome());
            pessoaStmt.setString(2, personalTrainer.getCPF());
            pessoaStmt.setString(3, personalTrainer.getTelefone());
            pessoaStmt.setString(4, personalTrainer.getEmail());
            pessoaStmt.executeUpdate();

            // Obter o ID gerado automaticamente para a pessoa
            ResultSet rs = pessoaStmt.getGeneratedKeys();
            if (rs.next()) {
                int pessoaId = rs.getInt(1);
                personalTrainer.setId(pessoaId);  // Definir o ID da pessoa no personalTrainer

                // Inserir os dados na tabela personal_trainer usando o ID gerado para pessoa
                personalStmt.setInt(1, pessoaId);
                personalStmt.setString(2, personalTrainer.getRegistroProfissional());
                personalStmt.setString(3, personalTrainer.getEspecialidade());
                personalStmt.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para atualizar os dados de um personal trainer existente
    public void updatePersonalTrainer(PersonalTrainer personalTrainer) {
        String sql = "UPDATE personal_trainer pt JOIN pessoa p ON pt.pessoa_id = p.id " +
                "SET p.nome = ?, p.cpf = ?, p.telefone = ?, p.email = ?, pt.registro_profissional = ?, pt.especialidade = ? " +
                "WHERE pt.pessoa_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, personalTrainer.getNome());
            stmt.setString(2, personalTrainer.getCPF());
            stmt.setString(3, personalTrainer.getTelefone());
            stmt.setString(4, personalTrainer.getEmail());
            stmt.setString(5, personalTrainer.getRegistroProfissional());
            stmt.setString(6, personalTrainer.getEspecialidade());
            stmt.setInt(7, personalTrainer.getId());  // O ID da pessoa é usado aqui
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para excluir um personal trainer do banco de dados
    public void deletePersonalTrainer(int personalId) {
        String sql = "DELETE FROM personal_trainer WHERE id = ?";
        String pessoaSql = "DELETE FROM pessoa WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             PreparedStatement pessoaStmt = connection.prepareStatement(pessoaSql)) {

            // Primeiro exclui o registro da tabela PersonalTrainer
            stmt.setInt(1, personalId);
            stmt.executeUpdate();

            // Depois, exclui o registro da tabela Pessoa associado
            pessoaStmt.setInt(1, personalId);
            pessoaStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para buscar um personal trainer por ID
    public PersonalTrainer getPersonalTrainerById(int id) {
        PersonalTrainer personal = null;
        String sql = "SELECT pt.id, p.nome, p.cpf, p.telefone, p.email, pt.registro_profissional, pt.especialidade " +
                "FROM personal_trainer pt " +
                "JOIN pessoa p ON pt.pessoa_id = p.id " +
                "WHERE pt.id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                personal = new PersonalTrainer();
                personal.setId(rs.getInt("id"));
                personal.setNome(rs.getString("nome"));
                personal.setCPF(rs.getString("cpf"));
                personal.setTelefone(rs.getString("telefone"));
                personal.setEmail(rs.getString("email"));
                personal.setRegistroProfissional(rs.getString("registro_profissional"));
                personal.setEspecialidade(rs.getString("especialidade"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personal;
    }

    // Método para buscar todos os personal trainers com filtro de nome
    public List<PersonalTrainer> getPersonalTrainersByName(String nome) {
        List<PersonalTrainer> personalTrainers = new ArrayList<>();
        String sql = "SELECT pt.id, p.nome, pt.registro_profissional, pt.especialidade " +
                "FROM personal_trainer pt " +
                "JOIN pessoa p ON pt.pessoa_id = p.id " +
                "WHERE p.nome LIKE ?";  // Busca pelo nome do personal

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, "%" + nome + "%");  // Filtro de nome com "LIKE"
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PersonalTrainer personal = new PersonalTrainer();
                personal.setId(rs.getInt("pt.id"));
                personal.setNome(rs.getString("p.nome"));
                personal.setRegistroProfissional(rs.getString("pt.registro_profissional"));
                personal.setEspecialidade(rs.getString("pt.especialidade"));

                personalTrainers.add(personal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personalTrainers;
    }

    // Método para buscar todos os personal trainers (sem filtro)
    public List<PersonalTrainer> getAllPersonalTrainers() {
        List<PersonalTrainer> personalTrainers = new ArrayList<>();
        String sql = "SELECT pt.id, p.nome, pt.registro_profissional, pt.especialidade " +
                "FROM personal_trainer pt " +
                "JOIN pessoa p ON pt.pessoa_id = p.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PersonalTrainer personal = new PersonalTrainer();
                personal.setId(rs.getInt("pt.id"));
                personal.setNome(rs.getString("p.nome"));
                personal.setRegistroProfissional(rs.getString("pt.registro_profissional"));
                personal.setEspecialidade(rs.getString("pt.especialidade"));

                personalTrainers.add(personal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return personalTrainers;
    }
}
