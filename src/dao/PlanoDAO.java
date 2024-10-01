package dao;

import db.DatabaseConnection;
import model.Plano;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoDAO {

    // Adicionar um novo plano
    public void addPlano(Plano plano) {
        String sql = "INSERT INTO plano (tipo, valor, descricao) VALUES (?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, plano.getTipo());
            stmt.setDouble(2, plano.getValor());
            stmt.setString(3, plano.getDescricao());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Atualizar um plano existente
    public void updatePlano(Plano plano) {
        String sql = "UPDATE plano SET tipo = ?, valor = ?, descricao = ? WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, plano.getTipo());
            stmt.setDouble(2, plano.getValor());
            stmt.setString(3, plano.getDescricao());
            stmt.setInt(4, plano.getId());
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Excluir um plano pelo ID
    public void deletePlano(int planoId) {
        String sql = "DELETE FROM plano WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, planoId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Buscar plano por ID
    public Plano getPlanoById(int id) {
        Plano plano = null;
        String sql = "SELECT * FROM plano WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                plano = new Plano(rs.getString("tipo"), rs.getDouble("valor"), rs.getString("descricao"));
                plano.setId(rs.getInt("id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return plano;
    }

    // Buscar todos os planos
    public List<Plano> getAllPlanos() {
        List<Plano> planos = new ArrayList<>();
        String sql = "SELECT * FROM plano";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Plano plano = new Plano(rs.getString("tipo"), rs.getDouble("valor"), rs.getString("descricao"));
                plano.setId(rs.getInt("id"));
                planos.add(plano);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return planos;
    }
}
