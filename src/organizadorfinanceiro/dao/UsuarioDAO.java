
package organizadorfinanceiro.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import organizadorfinanceiro.model.Usuario;

/**
 * DAO (Data Access Object) do Usuario.
 * Contém todos os métodos que acessam o banco de dados para a entidade Usuario.
 */
public class UsuarioDAO {
    /**
     * Insere um novo usuário no banco de dados.
     *
     * @param usuario objeto Usuario a ser salvo
     * @return true se inserido com sucesso, false se o login já existe
     */
    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (usuario, senha, nome, renda_mensal) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, usuario.getUsuario());
            stmt.setString(2, usuario.getSenha());
            stmt.setString(3, usuario.getNome());
            stmt.setDouble(4, usuario.getRendaMensal());

            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // Código 1062 = entrada duplicada (login já existe)
            if (e.getErrorCode() == 1062) {
                return false;
            }
            System.err.println("Erro ao inserir usuário: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    /**
     * Busca um usuário pelo login e senha (usado na autenticação).
     *
     * @param login  nome de usuário
     * @param senha  senha do usuário
     * @return objeto Usuario se encontrado, ou null se credenciais inválidas
     */
    public Usuario buscarPorLoginESenha(String login, String senha) {
        String sql = "SELECT * FROM usuario WHERE usuario = ? AND senha = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setString(1, login);
            stmt.setString(2, senha);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Monta o objeto Usuario com os dados vindos do banco
                return new Usuario(
                    rs.getString("nome"),
                    rs.getDouble("renda_mensal"),
                    rs.getString("usuario"),
                    rs.getString("senha")
                );
            }

        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }

        return null; // não encontrado
    }

    /**
     * Busca um usuário apenas pelo login (sem senha).
     * Útil para recuperar dados do usuário já autenticado.
     *
     * @param login nome de usuário
     * @return objeto Usuario se encontrado, ou null caso contrário
     */
    public Usuario buscarPorLogin(String login) {
        String sql = "SELECT * FROM usuario WHERE usuario = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Usuario(
                    rs.getString("nome"),
                    rs.getDouble("renda_mensal"),
                    rs.getString("usuario"),
                    rs.getString("senha")
                );
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar usuário por login: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }

        return null;
    }

    /**
     * Atualiza a renda mensal de um usuário no banco de dados.
     *
     * @param login       nome de usuário a atualizar
     * @param novaRenda   novo valor da renda mensal
     * @return true se atualizado com sucesso, false caso contrário
     */
    public boolean atualizarRenda(String login, double novaRenda) {
        String sql = "UPDATE usuario SET renda_mensal = ? WHERE usuario = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);

            stmt.setDouble(1, novaRenda);
            stmt.setString(2, login);

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0; // true se encontrou e atualizou o usuário

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar renda: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    /**
     * Verifica se um login já está cadastrado no banco.
     *
     * @param login nome de usuário a verificar
     * @return true se já existe, false se está disponível
     */
    public boolean loginJaExiste(String login) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE usuario = ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao verificar login: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }

        return false;
    }

    /**
     * POUPANÇA
     * Salva ou atualiza a poupança do usuário (upsert).
     * Cada usuário tem apenas um registro de poupança.
     */
    public boolean salvarPoupanca(String login, double poupancaMensal, double taxaJurosAnual, double acumulado) {
        String sql = "INSERT INTO poupanca (usuario, poupanca_mensal, taxa_juros_anual, acumulado) "
                   + "VALUES (?, ?, ?, ?) "
                   + "ON DUPLICATE KEY UPDATE poupanca_mensal = VALUES(poupanca_mensal), "
                   + "taxa_juros_anual = VALUES(taxa_juros_anual), acumulado = VALUES(acumulado)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setDouble(2, poupancaMensal);
            stmt.setDouble(3, taxaJurosAnual);
            stmt.setDouble(4, acumulado);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao salvar poupança: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    /**
     * Busca a poupança do usuário. Retorna array [poupancaMensal, taxaJurosAnual, acumulado]
     * ou null se não houver registro.
     */
    public double[] buscarPoupanca(String login) {
        String sql = "SELECT poupanca_mensal, taxa_juros_anual, acumulado FROM poupanca WHERE usuario = ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new double[]{rs.getDouble(1), rs.getDouble(2), rs.getDouble(3)};
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar poupança: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return null;
    }

    /**
     * DESPESAS RECORRENTES
     * Insere uma despesa recorrente para o usuário.
     */
    public boolean inserirDespesaRecorrente(String login, organizadorfinanceiro.model.DespesasRecorrentes despesa) {
        String sql = "INSERT INTO despesa_recorrente (usuario, descricao, valor_atual, valor_anterior, valor_dois_meses) "
                   + "VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, despesa.getDescricao());
            stmt.setDouble(3, despesa.getValorAtual());
            stmt.setDouble(4, despesa.getValorAnterior());
            stmt.setDouble(5, despesa.getValorDoisMeses());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir despesa recorrente: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    /**
     * Busca todas as despesas recorrentes do usuário.
     */
    public java.util.List<organizadorfinanceiro.model.DespesasRecorrentes> buscarDespesasRecorrentes(String login) {
        String sql = "SELECT descricao, valor_atual, valor_anterior, valor_dois_meses FROM despesa_recorrente WHERE usuario = ?";
        java.util.List<organizadorfinanceiro.model.DespesasRecorrentes> lista = new java.util.ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new organizadorfinanceiro.model.DespesasRecorrentes(
                    rs.getString("descricao"),
                    rs.getDouble("valor_atual"),
                    rs.getDouble("valor_anterior"),
                    rs.getDouble("valor_dois_meses")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar despesas recorrentes: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return lista;
    }

    /**
     * Exclui uma despesa recorrente pelo índice (posição na lista do usuário).
     * Busca o ID do registro na posição desejada e o remove.
     */
    public boolean excluirDespesaRecorrente(String login, int indice) {
        String sqlBusca = "SELECT id FROM despesa_recorrente WHERE usuario = ? ORDER BY id LIMIT 1 OFFSET ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlBusca);
            stmt.setString(1, login);
            stmt.setInt(2, indice);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                PreparedStatement del = conn.prepareStatement("DELETE FROM despesa_recorrente WHERE id = ?");
                del.setInt(1, id);
                del.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir despesa recorrente: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return false;
    }

    /**
     * DESPESAS NÃO RECORRENTES
     * Insere uma despesa não recorrente para o usuário.
     */
    public boolean inserirDespesaNaoRecorrente(String login, organizadorfinanceiro.model.DespesasNaoRecorrentes despesa) {
        String sql = "INSERT INTO despesa_nao_recorrente (usuario, descricao, valor_mensal, qtde_parcelas) "
                   + "VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            stmt.setString(2, despesa.getDescricao());
            stmt.setDouble(3, despesa.getValorMensal());
            stmt.setByte(4, despesa.getQtdeParcelas());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao inserir despesa não recorrente: " + e.getMessage());
            return false;
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }

    /**
     * Busca todas as despesas não recorrentes do usuário.
     */
    public java.util.List<organizadorfinanceiro.model.DespesasNaoRecorrentes> buscarDespesasNaoRecorrentes(String login) {
        String sql = "SELECT descricao, valor_mensal, qtde_parcelas FROM despesa_nao_recorrente WHERE usuario = ?";
        java.util.List<organizadorfinanceiro.model.DespesasNaoRecorrentes> lista = new java.util.ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new organizadorfinanceiro.model.DespesasNaoRecorrentes(
                    rs.getString("descricao"),
                    rs.getDouble("valor_mensal"),
                    rs.getByte("qtde_parcelas")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar despesas não recorrentes: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return lista;
    }

    /**
     * Exclui uma despesa não recorrente pelo índice (posição na lista do usuário).
     */
    public boolean excluirDespesaNaoRecorrente(String login, int indice) {
        String sqlBusca = "SELECT id FROM despesa_nao_recorrente WHERE usuario = ? ORDER BY id LIMIT 1 OFFSET ?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sqlBusca);
            stmt.setString(1, login);
            stmt.setInt(2, indice);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                PreparedStatement del = conn.prepareStatement("DELETE FROM despesa_nao_recorrente WHERE id = ?");
                del.setInt(1, id);
                del.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erro ao excluir despesa não recorrente: " + e.getMessage());
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
        return false;
    }
}