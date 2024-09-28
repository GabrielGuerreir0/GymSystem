package model;

public class Plano {
    private String tipo;
    private float valor;
    private String descricao;

    public Plano(String tipo, float valor, String descricao) {
        this.tipo = tipo;
        this.valor = valor;
        this.descricao = descricao;
    }

    public void calcularDesconto(float desconto) {
        this.valor -= desconto;
    }

    public void alterarValor(float valor) {
        this.valor = valor;
    }
}
