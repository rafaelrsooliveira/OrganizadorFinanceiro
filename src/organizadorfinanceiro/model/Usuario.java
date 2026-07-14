
package organizadorfinanceiro.model;

public class Usuario {
    
    private String usuario;
    private String senha;
    private String nome;
    private double rendaMensal;

    // construtor
    public Usuario(String nome, double rendaMensal, String usuario, String senha) {
        this.nome = nome;
        this.rendaMensal = rendaMensal;
        this.usuario = usuario;
        this.senha = senha;
    }
    
    // exibir dados de cadastro
    public void exibirUsuario() {
        System.out.println("Usuário: " + nome + "\nLogin: " + usuario);
    }
     
    // getters/setters
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public double getRendaMensal() {
        return rendaMensal;
    }

    public void setRendaMensal(double rendaMensal) {
        this.rendaMensal = rendaMensal;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }        
       
}
