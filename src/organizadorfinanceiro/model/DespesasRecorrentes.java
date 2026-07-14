
package organizadorfinanceiro.model;

public class DespesasRecorrentes {

    private String descricao;
    private double valorAtual;
    private double valorAnterior;
    private double valorDoisMeses;
    
    // construtor
    public DespesasRecorrentes(String descricao, double valorAtual, double valorAnterior, double valorDoisMeses) {
        this.descricao = descricao;
        this.valorAtual = valorAtual;
        this.valorAnterior = valorAnterior;
        this.valorDoisMeses = valorDoisMeses;
    }    
    
    // calcular a média dos últimos 3 meses
    public double calcularMedia() {
        return (valorAtual + valorAnterior + valorDoisMeses)/3;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValorAtual() {
        return valorAtual;
    }

    public void setValorAtual(double valorAtual) {
        this.valorAtual = valorAtual;
    }

    public double getValorAnterior() {
        return valorAnterior;
    }

    public void setValorAnterior(double valorAnterior) {
        this.valorAnterior = valorAnterior;
    }

    public double getValorDoisMeses() {
        return valorDoisMeses;
    }

    public void setValorDoisMeses(double valorDoisMeses) {
        this.valorDoisMeses = valorDoisMeses;
    }    
        
}
