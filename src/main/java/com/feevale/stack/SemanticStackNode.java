/*
**===========================================================================
**  @file    SemanticStackNode.java
**  @author  Eduardo Lorscheiter e Loreno Enrique Ribeiro 
**  @class   Projeto - Compiladores
**  @date    Junho/2026
**  @version 1.0
**  @brief   Nodos da pilha semântica do compilador
**===========================================================================
*/

package com.feevale.stack;

public class SemanticStackNode {
    private String code;
    private int ruleStacked;

    public SemanticStackNode() {

    }

    public SemanticStackNode(String code, int ruleStacked) {
        this.code = new String(code);
        this.ruleStacked = ruleStacked;
    }

    /**
     * @return Retorna o código objeto empilhado
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code Código a ser empilhado
     */
    public void setCode(String code) {
        this.code = new String(code);
    }

    /**
     * @return Retorna a regra que empilhou o código
     */
    public int getRuleStacked() {
        return ruleStacked;
    }

    /**
     * @param ruleStacked Número da regra que empilhou o código
     */
    public void setRuleStacked(int ruleStacked) {
        this.ruleStacked = ruleStacked;
    }

    /**
     * @return Retorna o código objeto empilhado em letras minúsculas
     */
    public String getCodeLowerCase() {
        if (code != null && code.length() >= 2 && code.startsWith("\"") && code.endsWith("\"")) {
            return code;
        }
        if (code.equals("True") || code.equals("true")) {
            return "True";
        }
    
        if (code.equals("False") || code.equals("false")) {
            return "False";
        }        
        return code.toLowerCase();
    }
}