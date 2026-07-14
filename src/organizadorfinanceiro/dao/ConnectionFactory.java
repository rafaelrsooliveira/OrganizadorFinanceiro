
package organizadorfinanceiro.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Responsável por criar e fechar a conexão com o banco de dados MySQL.
 * Chamar ConnectionFactory.getConnection() para obter uma conexão.
 */
public class ConnectionFactory {
    // URL, usuário e senha do MySQL
    private static final String URL    = "jdbc:mysql://localhost:3306/organizador_financeiro";
    private static final String USUARIO = "root";
    private static final String SENHA   = "root";

    // Abre e retorna uma conexão com o banco.
    public static Connection getConnection() throws SQLException {
        try {
            // Registra o driver JDBC do MySQL.
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado. Verifique se o mysql-connector.jar está no projeto.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }

    // Fecha a conexão com segurança (evita deixar conexão aberta).
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}