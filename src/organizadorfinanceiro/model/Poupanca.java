
package organizadorfinanceiro.model;

public class Poupanca {
    
    private double poupancaMensal;
    private double taxaJurosAnual;
    private double acumulado = 0;
 
    public void configurarPoupanca(double poupancaMensal, double taxaJurosAnual) {
        this.poupancaMensal = poupancaMensal;
        this.taxaJurosAnual = taxaJurosAnual;
        System.out.printf("\nPoupança configurada: R$%.2f/mês com taxa de %.2f%% ao ano\n", poupancaMensal, taxaJurosAnual);
    }
    
    public double[] projetarPoupanca() {
        double[] evolucao = new double[12];
        double taxaMensal = Math.pow(1 + taxaJurosAnual / 100, 1.0/12) - 1;
        
        acumulado = 0;
        for (int i = 0; i < 12; i++) {
            acumulado = (acumulado + poupancaMensal) * (1 + taxaMensal);
            evolucao[i] = acumulado;
        }        
        return evolucao;
    }
    
    // getters/setters
    public double getValorMensal() {
        return poupancaMensal;
    }

    public void setValorMensal(double valorMensal) {
        this.poupancaMensal = valorMensal;
    }

    public double getTaxaJuros() {
        return taxaJurosAnual;
    }

    public void setTaxaJuros(double taxaJuros) {
        this.taxaJurosAnual = taxaJuros;
    }

    public double getAcumulado() {
        return acumulado;
    }
}