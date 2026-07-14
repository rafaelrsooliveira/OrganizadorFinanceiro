
package organizadorfinanceiro.view;
 
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import organizadorfinanceiro.dao.UsuarioDAO;
import organizadorfinanceiro.model.DespesasRecorrentes;
import organizadorfinanceiro.model.DespesasNaoRecorrentes;
import organizadorfinanceiro.model.Usuario;
 
public class TelaPrevisao extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(TelaPrevisao.class.getName());
    
    // Atributos da tela.
    private String nomeUsuario;
    
    public TelaPrevisao(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
        initComponents();
        btnAtualizarCadastro.setText(nomeUsuario);
        carregarDadosDoUsuario(); // carrega tudo do banco antes de preencher a tabela
        txtOutPoupancaAcumulada.setText(String.format("%,.2f", acumuladoPoupanca));
        preencherTabela();
    }
 
    public TelaPrevisao() {
        this("Usuário");
    }
 
    /**
     * Carrega do banco todos os dados do usuário logado:
     * poupança, despesas recorrentes e não recorrentes.
     * Deve ser chamado antes de preencherTabela().
     */
    private void carregarDadosDoUsuario() {
        organizadorfinanceiro.dao.UsuarioDAO dao = new organizadorfinanceiro.dao.UsuarioDAO();
 
        // Poupança.
        double[] dadosPoupanca = dao.buscarPoupanca(nomeUsuario);
        if (dadosPoupanca != null) {
            poupancaMensal    = dadosPoupanca[0];
            acumuladoPoupanca = dadosPoupanca[2];
        }
 
        // Despesas recorrentes.
        despesasRecorrentes.clear();
        despesasRecorrentes.addAll(dao.buscarDespesasRecorrentes(nomeUsuario));
 
        // Despesas não recorrentes.
        despesasNaoRecorrentes.clear();
        despesasNaoRecorrentes.addAll(dao.buscarDespesasNaoRecorrentes(nomeUsuario));
    }
 
    // Criação de listas estáticas.
    private static List<DespesasRecorrentes> despesasRecorrentes = new ArrayList<>();
    private static List<DespesasNaoRecorrentes> despesasNaoRecorrentes = new ArrayList<>();
    private static double poupancaMensal = 0.0;
    private static double acumuladoPoupanca = 0.0;
    
    // Métodos de acesso
    public static void adicionarDespesaRecorrente(DespesasRecorrentes despesa) {
        despesasRecorrentes.add(despesa);
    }
    
    public static void removerDespesaRecorrente(int indice) {
        if (indice >= 0 && indice < despesasRecorrentes.size()) {
            despesasRecorrentes.remove(indice);
        }
    }
    
    public static List<DespesasRecorrentes> getDespesasRecorrentes() {
        return despesasRecorrentes;
    }
    
    public static void adicionarDespesaNaoRecorrente(DespesasNaoRecorrentes despesa) {
        despesasNaoRecorrentes.add(despesa);
    }
    
    public static void removerDespesaNaoRecorrente(int indice) {
        if (indice >= 0 && indice < despesasNaoRecorrentes.size()) {
            despesasNaoRecorrentes.remove(indice);
        }
    }
    
    public static List<DespesasNaoRecorrentes> getDespesasNaoRecorrentes() {
        return despesasNaoRecorrentes;
    }
    
    public static void setPoupancaMensal(double valor) {
        poupancaMensal = valor;
    }
    
    public static void setAcumuladoPoupanca(double valor) {
        acumuladoPoupanca = valor;
    }
    
    public static double getPoupancaMensal() {
        return poupancaMensal;
    }
    
    public static double getAcumuladoPoupanca() {
        return acumuladoPoupanca;
    }
    
    /**
     * Método que preenche todas as colunas da tabela com os dados estáticos.
     */
    private void preencherTabela() {
        DefaultTableModel model = (DefaultTableModel) tblPrevisaoFinanceira.getModel();
        model.setRowCount(0); // limpa a tabela
        
        // Obter renda mensal do usuário logado via banco de dados.
        double rendaMensal = 0.0;
        UsuarioDAO dao = new UsuarioDAO();
        Usuario usuarioLogado = dao.buscarPorLogin(nomeUsuario);
        if (usuarioLogado != null) {
            rendaMensal = usuarioLogado.getRendaMensal();
        }
        
            // Despesas recorrentes do usuário.
        double despesaRecorrenteTotal = 0.0;
        for (DespesasRecorrentes d : despesasRecorrentes) {
            despesaRecorrenteTotal += d.calcularMedia();
        }
        
        // Despesas não recorrentes do usuário.
        double[] despesaNaoRecorrentePorMes = new double[12];
        for (DespesasNaoRecorrentes d : despesasNaoRecorrentes) {
            int qtde = d.getQtdeParcelas();
            double valorMensal = d.getValorMensal();
            for (int i = 0; i < qtde && i < 12; i++) {
                despesaNaoRecorrentePorMes[i] += valorMensal;
            }
        }
        
        // Meses abreviados.
        String[] mesesAbreviados = {"JAN", "FEV", "MAR", "ABR", "MAI", "JUN", 
                                    "JUL", "AGO", "SET", "OUT", "NOV", "DEZ"};
        int mesAtual = LocalDate.now().getMonthValue() - 1;
        double poupanca = poupancaMensal; 
        
        // Preencher as 12 linhas.
        for (int i = 0; i < 12; i++) {
            int idx = (mesAtual + 1 + i) % 12; // próximo mês em diante
            int id = i + 1;
            String mes = mesesAbreviados[idx];
            
            double despesaRecorrente = despesaRecorrenteTotal;
            double despesaNaoRecorrente = despesaNaoRecorrentePorMes[i];
            double despesaTotal = despesaRecorrente + despesaNaoRecorrente;
            double saldoDisponivel = rendaMensal - despesaTotal - poupanca;
            
            model.addRow(new Object[]{
                id,
                mes,
                String.format("%,.2f", despesaRecorrente),
                String.format("%,.2f", despesaNaoRecorrente),
                String.format("%,.2f", despesaTotal),
                String.format("%,.2f", poupanca),
                String.format("%,.2f", saldoDisponivel)
            });
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnSair = new javax.swing.JButton();
        btnPrevisao = new javax.swing.JButton();
        btnDespesasRecorrentes = new javax.swing.JButton();
        btnDespesasNaoRecorrentes = new javax.swing.JButton();
        btnPoupanca = new javax.swing.JButton();
        btnAtualizarCadastro = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPrevisaoFinanceira = new javax.swing.JTable();
        jLabel6 = new javax.swing.JLabel();
        txtOutPoupancaAcumulada = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 51));

        jPanel2.setBackground(new java.awt.Color(147, 103, 48));

        btnSair.setBackground(new java.awt.Color(255, 204, 0));
        btnSair.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnSair.setText("Sair");
        btnSair.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSair.addActionListener(this::btnSairActionPerformed);

        btnPrevisao.setBackground(new java.awt.Color(255, 204, 0));
        btnPrevisao.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnPrevisao.setText("Previsão");
        btnPrevisao.addActionListener(this::btnPrevisaoActionPerformed);

        btnDespesasRecorrentes.setBackground(new java.awt.Color(255, 204, 0));
        btnDespesasRecorrentes.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnDespesasRecorrentes.setText("Despesas Recorrentes");
        btnDespesasRecorrentes.addActionListener(this::btnDespesasRecorrentesActionPerformed);

        btnDespesasNaoRecorrentes.setBackground(new java.awt.Color(255, 204, 0));
        btnDespesasNaoRecorrentes.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnDespesasNaoRecorrentes.setText("Despesas não Recorrentes");
        btnDespesasNaoRecorrentes.addActionListener(this::btnDespesasNaoRecorrentesActionPerformed);

        btnPoupanca.setBackground(new java.awt.Color(255, 204, 0));
        btnPoupanca.setFont(new java.awt.Font("Consolas", 1, 12)); // NOI18N
        btnPoupanca.setText("Poupança");
        btnPoupanca.addActionListener(this::btnPoupancaActionPerformed);

        btnAtualizarCadastro.setBackground(new java.awt.Color(255, 153, 0));
        btnAtualizarCadastro.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnAtualizarCadastro.setForeground(new java.awt.Color(255, 255, 255));
        btnAtualizarCadastro.setText("Usuário");
        btnAtualizarCadastro.setBorder(null);
        btnAtualizarCadastro.setBorderPainted(false);
        btnAtualizarCadastro.setContentAreaFilled(false);
        btnAtualizarCadastro.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnAtualizarCadastro.setFocusPainted(false);
        btnAtualizarCadastro.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAtualizarCadastro.addActionListener(this::btnAtualizarCadastroActionPerformed);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnSair, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDespesasRecorrentes, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPrevisao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDespesasNaoRecorrentes, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPoupanca, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(93, 93, 93)
                .addComponent(btnAtualizarCadastro)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(btnAtualizarCadastro)
                .addGap(68, 68, 68)
                .addComponent(btnPrevisao, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(btnDespesasRecorrentes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(btnDespesasNaoRecorrentes, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(btnPoupanca, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 227, Short.MAX_VALUE)
                .addComponent(btnSair, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jLabel7.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Previsão Financeira nos Próximos 12 Meses");

        tblPrevisaoFinanceira.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mês", "Despesas Recorrentes", "Despesas Não Recorrentes", "Despesa Total", "Poupança", "Saldo Disponível"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        tblPrevisaoFinanceira.setSelectionBackground(new java.awt.Color(142, 99, 47));
        jScrollPane1.setViewportView(tblPrevisaoFinanceira);
        if (tblPrevisaoFinanceira.getColumnModel().getColumnCount() > 0) {
            tblPrevisaoFinanceira.getColumnModel().getColumn(0).setPreferredWidth(25);
            tblPrevisaoFinanceira.getColumnModel().getColumn(0).setMaxWidth(25);
            tblPrevisaoFinanceira.getColumnModel().getColumn(1).setPreferredWidth(50);
            tblPrevisaoFinanceira.getColumnModel().getColumn(1).setMaxWidth(50);
        }

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 204, 0));
        jLabel6.setText("Poupança Acumulada nos Próximos 12 Meses:");

        txtOutPoupancaAcumulada.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        txtOutPoupancaAcumulada.setForeground(new java.awt.Color(255, 255, 255));
        txtOutPoupancaAcumulada.setText("0,00");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(78, 78, 78)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addGap(41, 41, 41)
                                .addComponent(txtOutPoupancaAcumulada, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(51, 51, 51)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 912, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 50, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel7)
                .addGap(26, 26, 26)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(104, 104, 104)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtOutPoupancaAcumulada))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAtualizarCadastroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarCadastroActionPerformed
        TelaAtualizarCadastro atualizarCadastro = new TelaAtualizarCadastro(nomeUsuario);
        atualizarCadastro.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnAtualizarCadastroActionPerformed

    private void btnPrevisaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevisaoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPrevisaoActionPerformed

    private void btnDespesasRecorrentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDespesasRecorrentesActionPerformed
        TelaDespesasRecorrentes despesasRecorrentes = new TelaDespesasRecorrentes(nomeUsuario);
        despesasRecorrentes.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnDespesasRecorrentesActionPerformed

    private void btnDespesasNaoRecorrentesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDespesasNaoRecorrentesActionPerformed
        TelaDespesasNaoRecorrentes despesasNaoRecorrentes = new TelaDespesasNaoRecorrentes(nomeUsuario);
        despesasNaoRecorrentes.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnDespesasNaoRecorrentesActionPerformed

    private void btnPoupancaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPoupancaActionPerformed
        TelaPoupanca poupanca = new TelaPoupanca(nomeUsuario);
        poupanca.setVisible(true);
        dispose();
    }//GEN-LAST:event_btnPoupancaActionPerformed

    private void btnSairActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSairActionPerformed
        System.exit(0);
    }//GEN-LAST:event_btnSairActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new TelaPrevisao().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizarCadastro;
    private javax.swing.JButton btnDespesasNaoRecorrentes;
    private javax.swing.JButton btnDespesasRecorrentes;
    private javax.swing.JButton btnPoupanca;
    private javax.swing.JButton btnPrevisao;
    private javax.swing.JButton btnSair;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblPrevisaoFinanceira;
    private javax.swing.JLabel txtOutPoupancaAcumulada;
    // End of variables declaration//GEN-END:variables
}
