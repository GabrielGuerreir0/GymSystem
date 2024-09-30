package dao;

import db.DatabaseConnection;
import model.Plano;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlanoDAO {
    private DatabaseConnection databaseConnection;

    public PlanoDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void inserir(Plano plano) throws SQLException {
        String sql = "INSERT INTO planos (tipo, valor, descricao) VALUES (?, ?, ?)";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plano.getTipo());
            stmt.setDouble(2, plano.getValor());
            stmt.setString(3, plano.getDescricao());
            stmt.executeUpdate();
        }
    }

    public List<Plano> listar() throws SQLException {
        List<Plano> planos = new ArrayList<>();
        String sql = "SELECT * FROM planos";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Plano plano = new Plano(rs.getString("tipo"), rs.getDouble("valor"), rs.getString("descricao"));
                plano.setId(rs.getInt("id"));
                planos.add(plano);
            }
        }
        return planos;
    }

    public void atualizar(Plano plano) throws SQLException {
        String sql = "UPDATE planos SET tipo=?, valor=?, descricao=? WHERE id=?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, plano.getTipo());
            stmt.setDouble(2, plano.getValor());
            stmt.setString(3, plano.getDescricao());
            stmt.setInt(4, plano.getId());
            stmt.executeUpdate();
        }
    }

    public void remover(int id) throws SQLException {
        String sql = "DELETE FROM planos WHERE id=?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Plano buscarPorId(int id) throws SQLException {
        Plano plano = null;
        String sql = "SELECT * FROM planos WHERE id=?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    plano = new Plano(rs.getString("tipo"), rs.getDouble("valor"), rs.getString("descricao"));
                    plano.setId(rs.getInt("id"));
                }
            }
        }
        return plano;
    }
}


