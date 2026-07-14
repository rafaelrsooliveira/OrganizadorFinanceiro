
package organizadorfinanceiro.model;

public class DespesasNaoRecorrentes {
    
    private String descricao;
    private double valorMensal;
    private byte qtdeParcelas;
    
    // construtor    
    public DespesasNaoRecorrentes(String descricao, double valorMensal, byte qtdeParcelas) {
        this.descricao = descricao;
        this.valorMensal = valorMensal;
        this.qtdeParcelas = qtdeParcelas;
    }
    
    // calcular o valor total das parcelas
    public double calcularParcelas() {
        return (valorMensal * qtdeParcelas);
    }
       
    // getters/setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getValorMensal() {
        return valorMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.valorMensal = valorMensal;
    }

    public byte getQtdeParcelas() {
        return qtdeParcelas;
    }

    public void setQtdeParcelas(byte qtdeParcelas) {
        this.qtdeParcelas = qtdeParcelas;
    }
    
        
}
