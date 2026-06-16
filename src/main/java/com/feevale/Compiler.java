/*
**===========================================================================
**  @file    Compiler.java
**  @author  Eduardo Lorscheiter e Loreno Enrique Ribeiro 
**  @class   Projeto - Compiladores
**  @date    Junho/2026
**  @version 1.0
**  @brief   Projeto de um compilador de Java para Python
**===========================================================================
*/

package com.feevale;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import com.feevale.exception.*;
import com.feevale.stack.*;

/**
 * Gramática Java Simplificada:
 * 
 * =========================================================
 * Classe
 * =========================================================
 * <G> ::= <ACCESS_MODIFIER> 'class' <ID> '{' <MAIN_METHOD> '}'
 * 
 * <MAIN_METHOD> ::= 'public' 'static' 'void' 'main' '(' 'String' '[' ']' 'args'
 * ')' '{' <DECLS> <CMDS> '}'
 * 
 * =========================================================
 * Modificadores de Acesso
 * =========================================================
 * <ACCESS_MODIFIER> ::= 'private'
 * <ACCESS_MODIFIER> ::= 'protected'
 * <ACCESS_MODIFIER> ::= 'public'
 * <ACCESS_MODIFIER> ::= ε
 * 
 * =========================================================
 * Declarações
 * =========================================================
 * <DECLS> ::= <DECL> <DECLS>
 * <DECLS> ::= ε
 * 
 * <DECL> ::= <TYPE> <IDS> ';'
 * 
 * <TYPE> ::= 'int'
 * <TYPE> ::= 'double'
 * <TYPE> ::= 'String'
 * <TYPE> ::= 'boolean'
 * 
 * <IDS> ::= <ID> ',' <IDS>
 * <IDS> ::= <ID>
 * 
 * =========================================================
 * Comandos
 * =========================================================
 * <CMDS> ::= <CMD> <CMDS>
 * <CMDS> ::= ε
 * 
 * <CMD> ::= <CMD_IF>
 * <CMD> ::= <CMD_WHILE>
 * <CMD> ::= <CMD_FOR>
 * <CMD> ::= <CMD_SWITCH>
 * <CMD> ::= <CMD_PRINTLN>
 * <CMD> ::= <CMD_ASSIGNMENT>
 * 
 * =========================================================
 * If / Else
 * =========================================================
 * <CMD_IF> ::= 'if' '(' <CONDITION> ')' '{' <CMDS> '}'
 * <CMD_IF> ::= 'if' '(' <CONDITION> ')' '{' <CMDS> '}' 'else' '{' <CMDS> '}'
 * 
 * =========================================================
 * While
 * =========================================================
 * <CMD_WHILE> ::= 'while' '(' <CONDITION> ')' '{' <CMDS> '}'
 * 
 * =========================================================
 * For
 * =========================================================
 * <CMD_FOR> ::= 'for' '(' <ASSIGNMENT> ';' <CONDITION> ';' <ASSIGNMENT> ')'
 * '{' <CMDS> '}'
 * 
 * =========================================================
 * Switch
 * =========================================================
 * <CMD_SWITCH> ::= 'switch' '(' <ID> ')' '{' <CASES> '}'
 * 
 * <CASES> ::= <CASO> <CASES>
 * <CASES> ::= ε
 * 
 * <CASO> ::= 'case' <NUM> ':' <CMDS> 'break' ';'
 * <CASO> ::= 'default' ':' <CMDS>
 * 
 * =========================================================
 * Print
 * =========================================================
 * <CMD_PRINTLN> ::= 'System' '.' 'out' '.' 'println' '(' <E> ')' ';'
 * 
 * =========================================================
 * Atribuição
 * =========================================================
 * <CMD_ASSIGNMENT> ::= <ASSIGNMENT> ';'
 * 
 * <ASSIGNMENT> ::= <ID> '=' <E>
 * 
 * =========================================================
 * Condições
 * =========================================================
 * <CONDITION> ::= <E> '>' <E>
 * <CONDITION> ::= <E> '<' <E>
 * <CONDITION> ::= <E> '>=' <E>
 * <CONDITION> ::= <E> '<=' <E>
 * <CONDITION> ::= <E> '==' <E>
 * <CONDITION> ::= <E> '!=' <E>
 * 
 * =========================================================
 * Expressões
 * =========================================================
 * <E> ::= <E> + <T>
 * <E> ::= <E> - <T>
 * <E> ::= <T>
 * 
 * <T> ::= <T> * <F>
 * <T> ::= <T> / <F>
 * <T> ::= <T> % <F>
 * <T> ::= <F>
 * 
 * <F> ::= '(' <E> ')'
 * <F> ::= <NUM>
 * <F> ::= <ID>
 *
 * =========================================================
 * Tokens
 * =========================================================
 * <NUM> ::= [0-9]+('.'[0-9]+)?
 *
 * <ID> ::= [a-zA-Z_][a-zA-Z0-9_]*
 * 
 */

public class Compiler {
    // Lista de tokens
    private static final int T_CLASS = 1;
    private static final int T_PRIVATE = 2;
    private static final int T_PROTECTED = 3;
    private static final int T_PUBLIC = 4;
    private static final int T_STATIC = 5;
    private static final int T_VOID = 6;
    private static final int T_MAIN = 7;
    private static final int T_ARGS = 8;
    private static final int T_INT = 9;
    private static final int T_DOUBLE = 10;
    private static final int T_STRING = 11;
    private static final int T_BOOLEAN = 12;
    private static final int T_ID = 13;
    private static final int T_NUMBER = 14;
    private static final int T_BRACE_OPEN = 15;
    private static final int T_BRACE_CLOSE = 16;
    private static final int T_BRACKET_OPEN = 17;
    private static final int T_BRACKET_CLOSE = 18;
    private static final int T_PARENTHESIS_OPEN = 19;
    private static final int T_PARENTHESIS_CLOSE = 20;
    private static final int T_SEMICOLON = 21;
    private static final int T_COLON = 22;
    private static final int T_COMMA = 23;
    private static final int T_POINT = 24;
    private static final int T_ADD = 25;
    private static final int T_SUBTRACT = 26;
    private static final int T_MULTIPLY = 27;
    private static final int T_DIVIDE = 28;
    private static final int T_MOD = 29;
    private static final int T_GREATER_EQUAL = 30;
    private static final int T_GREATER = 31;
    private static final int T_SMALLER_EQUAL = 32;
    private static final int T_SMALLER = 33;
    private static final int T_EQUAL_EQUAL = 34;
    private static final int T_EQUAL = 35;
    private static final int T_DIFFERENT = 36;
    private static final int T_EXCLAMATION = 37;
    private static final int T_IF = 38;
    private static final int T_ELSE = 39;
    private static final int T_WHILE = 40;
    private static final int T_FOR = 41;
    private static final int T_SWITCH = 42;
    private static final int T_CASE = 43;
    private static final int T_DEFAULT = 44;
    private static final int T_BREAK = 45;
    private static final int T_SYSTEM = 46;
    private static final int T_OUT = 47;
    private static final int T_PRINTLN = 48;
    private static final int T_STRING_LITERAL = 49;
    private static final int T_INCREMENT = 50;
    private static final int T_DECREMENT = 51;
    private static final int T_TRUE = 52;
    private static final int T_FALSE = 53;
    private static final int T_END_SOURCE = 90;
    private static final int T_LEXICAL_ERROR = 98;
    private static final int T_NULL = 99;
    private static final int END_FILE = 226;

    // Estados de compilação
    private static final int E_WITHOUT_ERRORS = 0;
    private static final int E_LEXICAL_ERROR = 1;
    private static final int E_SYNTACTIC_ERROR = 2;
    private static final int E_SEMANTIC_ERROR = 3;

    // Variáveis utilizadas para o léxico
    private static BufferedReader javaFile;
    private static File pythonFile;
    private static char lookAhead;
    private static int token = T_NULL;
    private static String lexeme;
    private static int pointer = 0;
    private static int currentLine = 0;
    private static int currentColumn = 0;
    private static String sourceLine = "";
    private static String errorMessage = "";
    private static StringBuffer identifiedTokens = new StringBuffer();

    // Variáveis utilizadas para o sintático
    private static StringBuffer recognizedRules = new StringBuffer();
    private static int compilationState = E_WITHOUT_ERRORS;

    // Variáveis utilizadas para o semântico
    private static String variableName;
    private static String currentType;
    private static String lastLexeme;
    private static StringBuffer codePython = new StringBuffer();
    private static int indentationLevel = 0;
    private static SemanticStackNode nodo_0;
    private static SemanticStackNode nodo_1;
    private static SemanticStackNode nodo_2;
    private static SemanticStack semanticStack = new SemanticStack();
    private static HashMap<String, String> symbolsTable = new HashMap<String, String>();
    private static String variavel;

    // Variáveis utilizadas para geração do "for" em Python
    private static String forVariable;
    private static String forStart;
    private static String forEnd;
    private static String forStep;
    private static String forConditionOperator;
    private static boolean insideForHeader = false;
    private static boolean readingForStep = false;

    // Controle de switch/case
    private static boolean switchFirstCase = true;

    // Ações semânticas
    private enum SemanticAction {
        START_PROGRAM(0),
        VARIABLE_DECLARE(1),
        VARIABLE_USAGE(2),
        ASSIGNMENT(3),
        ADD(3),
        SUBTRACT(4),
        MULTIPLY(5),
        DIVIDE(6),
        MOD(7),
        PARENTHESIS(8),
        NUMBER(9),
        BLOCK_END(11),
        IF_BEGIN(12),
        ELSE_BEGIN(13),
        WHILE_BEGIN(10),
        FOR_BEGIN(21),
        GREATER(14),
        SMALLER(15),
        GREATER_EQUAL(16),
        SMALLER_EQUAL(17),
        EQUAL_EQUAL(18),
        DIFFERENT(19),
        PRINT(20),
        LESS_TAB(22),
        MORE_TAB(23),
        SWITCH_BEGIN(24),
        CASE_BEGIN(25),
        DEFAULT_BEGIN(26),
        CASE_BREAK(27),
        INCREMENT(30),
        DECREMENT(31),
        STRING_LITERAL(32),
        BOOLEAN_LITERAL(33);

        private final int code;

        SemanticAction(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public static void main(String[] args) throws LexicalErrorException {
        try {
            if (!openJavaFile())
                return;
            if (!openPythonFile())
                return;

            identifiedTokens.append("Tokens reconhecidos: \n\n");
            recognizedRules.append("\n\nRegras reconhecidas: \n\n");

            // Realiza a compilação do código fonte em Java para Python
            compileJavaPython();

            displayOutput();

            gravaSaida();

            closeSource();

        } catch (FileNotFoundException fnfe) {
            showErrorMessage("Arquivo não existe!", "FileNotFoundException!");
        } catch (UnsupportedEncodingException uee) {
            showErrorMessage("Erro desconhecido", "UnsupportedEncodingException!");
        } catch (IOException ioe) {
            showErrorMessage("Erro de io: " + ioe.getMessage(), "IOException!");
        } catch (LexicalErrorException lee) {
            showErrorMessage(lee.getMessage(), "LexicalErrorException!");
        } catch (SyntacticErrorException see) {
            showErrorMessage(see.getMessage(), "SyntacticErrorException!");
        } catch (SemanticErrorException see) {
            showErrorMessage(see.getMessage(), "SemanticErrorException!");
        }
    }

    /***********************************************************************************************
     * Seleção de arquivos
     ***********************************************************************************************/
    /**
     * Abre o arquivo Java para leitura
     * 
     * @return True se o arquivo foi aberto com sucesso, false caso contrário
     */
    private static boolean openJavaFile() {
        File filesDir = new File("files");
        JFileChooser fileChooser = new JFileChooser(filesDir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Filtra apenas por arquivos .java
        FileSelectionFilter filter = new FileSelectionFilter("java");
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }

        File fileSource = fileChooser.getSelectedFile();
        return openSource(fileSource);
    }

    /**
     * Abre o arquivo Java para leitura
     * 
     * @param fileName Arquivo selecionado
     * @return True se o arquivo foi aberto com sucesso, false caso contrário
     */
    private static boolean openSource(File fileSource) {
        if (fileSource == null || fileSource.getName().trim().equals("")) {
            showErrorMessage("Nome de arquivo inválido", "Nome de arquivo inválido");
            return false;
        }

        try {
            FileReader fileReader = new FileReader(fileSource);
            javaFile = new BufferedReader(fileReader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Abre o arquivo Python para escrita
     */
    private static boolean openPythonFile() {
        File filesDir = new File("files");
        JFileChooser fileChooser = new JFileChooser(filesDir);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);

        // Filtra apenas por arquivos .py
        FileSelectionFilter filter = new FileSelectionFilter("py");
        fileChooser.addChoosableFileFilter(filter);

        int result = fileChooser.showSaveDialog(null);
        if (result == JFileChooser.CANCEL_OPTION) {
            return false;
        }

        pythonFile = fileChooser.getSelectedFile();

        // Garante que o arquivo tenha a extensão .py
        if (!pythonFile.getName().toLowerCase().endsWith(".py"))
            pythonFile = new File(pythonFile.getAbsolutePath() + ".py");

        return true;
    }

    /**
     * Grava o código Python gerado em um arquivo Python de saída
     * 
     * @return True se o arquivo foi salvo com sucesso, false caso contrário
     */
    private static boolean gravaSaida() {
        if (pythonFile == null || pythonFile.getName().trim().equals("")) {
            showErrorMessage("Nome de arquivo inválido", "Nome de arquivo inválido");
            return false;
        }

        try {
            FileWriter fw;
            fw = new FileWriter(pythonFile);
            BufferedWriter bfw = new BufferedWriter(fw);
            bfw.write(codePython.toString());
            bfw.close();
            showInformationMessage("Arquivo salvo: " + pythonFile, "Salvando arquivo!");
        } catch (IOException e) {
            showErrorMessage(e.getMessage(), "Erro de Entrada/Saída");
        }

        return true;
    }

    /***********************************************************************************************
     * Compilação
     ***********************************************************************************************/
    /**
     * Realiza a compilação do código fonte em Java para Python
     * 
     * @throws IOException
     * @throws LexicalErrorException
     * @throws SyntacticErrorException
     * @throws SemanticErrorException
     */
    private static void compileJavaPython()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        // Posiciona no primeiro token
        moveLookAhead();
        searchNextToken();

        // Realiza a compilação do código fonte em Java para Python
        g();
        if (compilationState == E_LEXICAL_ERROR) {
            showErrorMessage(errorMessage, "Erro Léxico!");
        } else if (compilationState == E_SYNTACTIC_ERROR) {
            showErrorMessage(errorMessage, "Erro Sintático!");
        } else if (compilationState == E_SEMANTIC_ERROR) {
            showErrorMessage(errorMessage, "Erro Semântico!");
        } else {
            showInformationMessage("A compilação de Java para Python finalizou sem erros.",
                    "Compilação finalizada com sucesso!");
        }
    }

    // <G> ::= <ACCESS_MODIFIER> 'class' <ID> '{' <MAIN_METHOD> '}'
    private static void g() throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        semanticRule(SemanticAction.START_PROGRAM);
        access_modifier();
        if (token == T_CLASS) {
            searchNextToken();
            id();
            if (token == T_BRACE_OPEN) {
                searchNextToken();
                main_method();
                if (token == T_BRACE_CLOSE) {
                    searchNextToken();
                } else {
                    logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'class', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<G> ::= <ACCESS_MODIFIER> 'class' <ID> '{' <MAIN_METHOD> '}'");
    }

    // <ACCESS_MODIFIER> ::= 'private'
    // <ACCESS_MODIFIER> ::= 'protected'
    // <ACCESS_MODIFIER> ::= 'public'
    // <ACCESS_MODIFIER> ::= ε
    private static void access_modifier() throws IOException, LexicalErrorException {
        if (token == T_PRIVATE || token == T_PROTECTED || token == T_PUBLIC) {
            searchNextToken();
        }

        accumulateRecognizedSyntacticRule("<ACCESS_MODIFIER> ::= 'private' | 'protected' | 'public' | ε");
    }

    // <MAIN_METHOD> ::= 'public' 'static' 'void' 'main' '(' 'String' '[' ']' 'args'
    // ')' '{' <DECLS> <CMDS> '}'
    private static void main_method()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_PUBLIC) {
            searchNextToken();
            if (token == T_STATIC) {
                searchNextToken();
                if (token == T_VOID) {
                    searchNextToken();
                    if (token == T_MAIN) {
                        searchNextToken();
                        if (token == T_PARENTHESIS_OPEN) {
                            searchNextToken();
                            if (token == T_STRING) {
                                searchNextToken();
                                if (token == T_BRACKET_OPEN) {
                                    searchNextToken();
                                    if (token == T_BRACKET_CLOSE) {
                                        searchNextToken();
                                        if (token == T_ARGS) {
                                            searchNextToken();
                                            if (token == T_PARENTHESIS_CLOSE) {
                                                searchNextToken();
                                                if (token == T_BRACE_OPEN) {
                                                    searchNextToken();
                                                    decls();
                                                    cmds();
                                                    if (token == T_BRACE_CLOSE) {
                                                        searchNextToken();
                                                    } else {
                                                        logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                                                    }
                                                } else {
                                                    logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                                                }
                                            } else {
                                                logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                                            }
                                        } else {
                                            logSyntaxError("Esperava 'args', mas encontrou: " + lexeme);
                                        }
                                    } else {
                                        logSyntaxError("Esperava ']', mas encontrou: " + lexeme);
                                    }
                                } else {
                                    logSyntaxError("Esperava '[', mas encontrou: " + lexeme);
                                }
                            } else {
                                logSyntaxError("Esperava 'String', mas encontrou: " + lexeme);
                            }
                        } else {
                            logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava 'main', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava 'void', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava 'static', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'public', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule(
                "<MAIN_METHOD> ::= 'public' 'static' 'void' 'main' '(' 'String' '[' ']' 'args' ')' '{' <DECLS> <CMDS> '}'");
    }

    // <DECLS> ::= <DECL> <DECLS>
    // <DECLS> ::= ε
    private static void decls()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        while (token == T_INT || token == T_DOUBLE || token == T_STRING || token == T_BOOLEAN) {
            decl();
        }

        accumulateRecognizedSyntacticRule("<DECLS> ::= <DECL> <DECLS> | ε");
    }

    // <DECL> ::= <TYPE> <IDS> ';'
    private static void decl()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        type();
        ids();
        if (token == T_SEMICOLON) {
            searchNextToken();
        } else {
            logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<DECL> ::= <TYPE> <IDS> ';'");
    }

    // <TYPE> ::= 'int'
    // <TYPE> ::= 'double'
    // <TYPE> ::= 'String'
    // <TYPE> ::= 'boolean'
    private static void type() throws IOException, LexicalErrorException, SyntacticErrorException {
        if (token == T_INT || token == T_DOUBLE || token == T_STRING || token == T_BOOLEAN) {
            currentType = lexeme;
            searchNextToken();
        } else {
            logSyntaxError("Esperava um tipo de variável, mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<TYPE> ::= 'int' | 'double' | 'String' | 'boolean'");
    }

    // <IDS> ::= <ID> ',' <IDS>
    // <IDS> ::= <ID>
    // <IDS> ::= <ID> '=' <E>
    private static void ids()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        id();
        
        // Check if there's an assignment in the declaration
        if (token == T_EQUAL) {
            searchNextToken();
            e();
            semanticRule(SemanticAction.VARIABLE_DECLARE);
            // Manually generate assignment code for initialized variables
            nodo_2 = semanticStack.pop();
            codePython.append(tabulation(indentationLevel));
            codePython.append(variableName + " = " + nodo_2.getCodeLowerCase() + "\n");
        } else {
            semanticRule(SemanticAction.VARIABLE_DECLARE);
        }
        
        while (token == T_COMMA) {
            searchNextToken();
            id();
            
            if (token == T_EQUAL) {
                searchNextToken();
                e();
                semanticRule(SemanticAction.VARIABLE_DECLARE);
                nodo_2 = semanticStack.pop();
                codePython.append(tabulation(indentationLevel));
                codePython.append(variableName + " = " + nodo_2.getCodeLowerCase() + "\n");
            } else {
                semanticRule(SemanticAction.VARIABLE_DECLARE);
            }
        }

        accumulateRecognizedSyntacticRule("<IDS> ::= <ID> ',' <IDS> | <ID> | <ID> '=' <E>");
    }

    // <CMDS> ::= <CMD> <CMDS>
    // <CMDS> ::= ε
    private static void cmds()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        while (token == T_SYSTEM ||
                token == T_IF ||
                token == T_WHILE ||
                token == T_FOR ||
                token == T_SWITCH ||
                token == T_ID) {
            cmd();
        }

        accumulateRecognizedSyntacticRule("<CMDS> ::= <CMD> <CMDS> | ε");
    }

    // <CMD> ::= <CMD_IF>
    // <CMD> ::= <CMD_WHILE>
    // <CMD> ::= <CMD_FOR>
    // <CMD> ::= <CMD_SWITCH>
    // <CMD> ::= <CMD_PRINTLN>
    // <CMD> ::= <CMD_ASSIGNMENT>
    private static void cmd()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        switch (token) {
            case T_IF:
                cmd_if();
                break;
            case T_WHILE:
                cmd_while();
                break;
            case T_FOR:
                cmd_for();
                break;
            case T_SWITCH:
                cmd_switch();
                break;
            case T_SYSTEM:
                cmd_println();
                break;
            case T_ID:
                cmd_assignment();
                break;
            default:
                logSyntaxError("Comando não identificado. Encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule(
                "<CMD> ::= <CMD_IF> | <CMD_WHILE> | <CMD_FOR> | <CMD_SWITCH> | <CMD_PRINTLN> | <CMD_ASSIGNMENT>");
    }

    // <CMD_IF> ::= 'if' '(' <CONDITION> ')' '{' <CMDS> '}'
    // <CMD_IF> ::= 'if' '(' <CONDITION> ')' '{' <CMDS> '}' 'else' '{' <CMDS> '}'
    private static void cmd_if()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_IF) {
            searchNextToken();
            if (token == T_PARENTHESIS_OPEN) {
                searchNextToken();
                condition();
                semanticRule(SemanticAction.IF_BEGIN);
                if (token == T_PARENTHESIS_CLOSE) {
                    searchNextToken();
                    if (token == T_BRACE_OPEN) {
                        searchNextToken();
                        cmds();
                        if (token == T_BRACE_CLOSE) {
                            searchNextToken();
                            semanticRule(SemanticAction.LESS_TAB);
                            if (token == T_ELSE) {
                                searchNextToken();
                                semanticRule(SemanticAction.ELSE_BEGIN);
                                if (token == T_BRACE_OPEN) {
                                    searchNextToken();
                                    cmds();
                                    if (token == T_BRACE_CLOSE) {
                                        searchNextToken();
                                        semanticRule(SemanticAction.LESS_TAB);
                                    } else {
                                        logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                                    }
                                } else {
                                    logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                                }
                            }
                        } else {
                            logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
            }
        }

        accumulateRecognizedSyntacticRule("<CMD_IF>");
    }

    // <CMD_FOR> ::= 'for' '(' <ASSIGNMENT> ';' <CONDITION> ';' <ASSIGNMENT> ')'
    // '{' <CMDS> '}'
    private static void cmd_for()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_FOR) {
            searchNextToken();
            if (token == T_PARENTHESIS_OPEN) {
                insideForHeader = true;
                forConditionOperator = null;
                searchNextToken();
                assignment();
                forVariable = nodo_1.getCodeLowerCase();
                forStart = nodo_2.getCodeLowerCase();
                if (token == T_SEMICOLON) {
                    searchNextToken();
                    condition();
                    forEnd = nodo_2.getCodeLowerCase();
                    if (token == T_SEMICOLON) {
                        readingForStep = true;
                        searchNextToken();
                        assignment();
                        readingForStep = false;
                        insideForHeader = false;
                        if (token == T_PARENTHESIS_CLOSE) {
                            searchNextToken();
                            semanticRule(SemanticAction.FOR_BEGIN);
                            if (token == T_BRACE_OPEN) {
                                searchNextToken();
                                cmds();
                                if (token == T_BRACE_CLOSE) {
                                    indentationLevel--;
                                    searchNextToken();
                                } else {
                                    logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                                }
                            } else {
                                logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                            }
                        } else {
                            logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'for', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule(
                "<CMD_FOR> ::= 'for' '(' <ASSIGNMENT> ';' <CONDITION> ';' <ASSIGNMENT> ')' '{' <CMDS> '}'");
    }

    // <CMD_SWITCH> ::= 'switch' '(' <ID> ')' '{' <CASES> '}'
    private static void cmd_switch()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_SWITCH) {
            searchNextToken();
            if (token == T_PARENTHESIS_OPEN) {
                searchNextToken();
                id();
                semanticRule(SemanticAction.SWITCH_BEGIN);
                if (token == T_PARENTHESIS_CLOSE) {
                    searchNextToken();
                    if (token == T_BRACE_OPEN) {
                        searchNextToken();
                        cases();
                        if (token == T_BRACE_CLOSE) {
                            searchNextToken();
                        } else {
                            logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'switch', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<CMD_SWITCH> ::= 'switch' '(' <ID> ')' '{' <CASES> '}'");
    }

    // <CASES> ::= <CASO> <CASES>
    // <CASES> ::= ε
    private static void cases()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {

        while (token == T_CASE || token == T_DEFAULT) {
            caso();
        }

        accumulateRecognizedSyntacticRule("<CASES> ::= <CASO> <CASES> | ε");
    }

    // <CASO> ::= 'case' <NUM> ':' <CMDS> 'break' ';'
    // <CASO> ::= 'default' ':' <CMDS>
    private static void caso()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_CASE) {
            searchNextToken();
            if (token == T_NUMBER) {
                searchNextToken();
                semanticRule(SemanticAction.NUMBER);
                semanticRule(SemanticAction.CASE_BEGIN);
                if (token == T_COLON) {
                    searchNextToken();
                    cmds();
                    if (token == T_BREAK) {
                        searchNextToken();
                        semanticRule(SemanticAction.CASE_BREAK);
                        if (token == T_SEMICOLON) {
                            searchNextToken();
                        } else {
                            logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
                        }
                    }
                    semanticRule(SemanticAction.LESS_TAB);
                } else {
                    logSyntaxError("Esperava ':', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava um número, mas encontrou: " + lexeme);
            }
        } else if (token == T_DEFAULT) {
            searchNextToken();
            if (token == T_COLON) {
                searchNextToken();
                semanticRule(SemanticAction.DEFAULT_BEGIN);
                cmds();
                semanticRule(SemanticAction.LESS_TAB);
            } else {
                logSyntaxError("Esperava ':', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'case' ou 'default', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule(
                "<CASO> ::= 'case' <NUM> ':' <CMDS> 'break' ';' | 'default' ':' <CMDS>");
    }

    // <CMD_PRINTLN> ::= 'System' '.' 'out' '.' 'println' '(' <E> ')' ';'
    private static void cmd_println()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_SYSTEM) {
            searchNextToken();
            if (token == T_POINT) {
                searchNextToken();
                if (token == T_OUT) {
                    searchNextToken();
                    if (token == T_POINT) {
                        searchNextToken();
                        if (token == T_PRINTLN) {
                            searchNextToken();
                            if (token == T_PARENTHESIS_OPEN) {
                                searchNextToken();
                                e();
                                semanticRule(SemanticAction.PRINT);
                                if (token == T_PARENTHESIS_CLOSE) {
                                    searchNextToken();
                                    if (token == T_SEMICOLON) {
                                        searchNextToken();
                                    } else {
                                        logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
                                    }
                                } else {
                                    logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                                }
                            } else {
                                logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
                            }
                        } else {
                            logSyntaxError("Esperava 'println', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava '.', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava 'out', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '.', mas encontrou: " + lexeme);
            }
        } else {
            logSyntaxError("Esperava 'System', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<CMD_PRINTLN> ::= 'System' '.' 'out' '.' 'println' '(' <E> ')' ';'");
    }

    // <CMD_WHILE> ::= 'while' '(' <CONDITION> ')' '{' <CMDS> '}'
    private static void cmd_while()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_WHILE) {
            searchNextToken();
            if (token == T_PARENTHESIS_OPEN) {
                searchNextToken();
                condition();
                semanticRule(SemanticAction.WHILE_BEGIN);
                if (token == T_PARENTHESIS_CLOSE) {
                    searchNextToken();
                    if (token == T_BRACE_OPEN) {
                        searchNextToken();
                        cmds();
                        if (token == T_BRACE_CLOSE) {
                            searchNextToken();
                            semanticRule(SemanticAction.LESS_TAB);
                        } else {
                            logSyntaxError("Esperava '}', mas encontrou: " + lexeme);
                        }
                    } else {
                        logSyntaxError("Esperava '{', mas encontrou: " + lexeme);
                    }
                } else {
                    logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                }
            } else {
                logSyntaxError("Esperava '(', mas encontrou: " + lexeme);
            }
        }

        accumulateRecognizedSyntacticRule("<CMD_WHILE>");
    }

    // <CMD_ASSIGNMENT> ::= <ASSIGNMENT> ';'
    private static void cmd_assignment()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        assignment();
        if (token == T_SEMICOLON) {
            searchNextToken();
        } else {
            logSyntaxError("Esperava ';', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<CMD_ASSIGNMENT> ::= <ASSIGNMENT> ';'");
    }

    // <ASSIGNMENT> ::= <ID> '=' <E>
    // <ASSIGNMENT> ::= <ID> '++'
    // <ASSIGNMENT> ::= <ID> '--'
    private static void assignment()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        id();
        semanticRule(SemanticAction.VARIABLE_USAGE);
        if (token == T_EQUAL) {
            searchNextToken();
            e();
            semanticRule(SemanticAction.ASSIGNMENT);
        } else if (token == T_INCREMENT) {
            searchNextToken();
            semanticRule(SemanticAction.INCREMENT);
        } else if (token == T_DECREMENT) {
            searchNextToken();
            semanticRule(SemanticAction.DECREMENT);
        } else {
            logSyntaxError("Esperava '=', '++' ou '--', mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<ASSIGNMENT> ::= <ID> '=' <E> | <ID> '++' | <ID> '--'");
    }

    // <CONDITION> ::= <E> '>' <E>
    // <CONDITION> ::= <E> '<' <E>
    // <CONDITION> ::= <E> '>=' <E>
    // <CONDITION> ::= <E> '<=' <E>
    // <CONDITION> ::= <E> '==' <E>
    // <CONDITION> ::= <E> '!=' <E>
    private static void condition()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        e();
        switch (token) {
            case T_GREATER:
                if (insideForHeader) {
                    forConditionOperator = ">";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.GREATER);
                break;
            case T_SMALLER:
                if (insideForHeader) {
                    forConditionOperator = "<";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.SMALLER);
                break;
            case T_GREATER_EQUAL:
                if (insideForHeader) {
                    forConditionOperator = ">=";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.GREATER_EQUAL);
                break;
            case T_SMALLER_EQUAL:
                if (insideForHeader) {
                    forConditionOperator = "<=";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.SMALLER_EQUAL);
                break;
            case T_EQUAL_EQUAL:
                if (insideForHeader) {
                    forConditionOperator = "==";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.EQUAL_EQUAL);
                break;
            case T_DIFFERENT:
                if (insideForHeader) {
                    forConditionOperator = "!=";
                }
                searchNextToken();
                e();
                semanticRule(SemanticAction.DIFFERENT);
                break;
            default:
                logSyntaxError("Esperava um operador lógico, mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<CONDITION> ::= <E> ( '>' | '<' | '>=' | '<=' | '==' | '!=' ) <E> ");
    }

    // <E> ::= <E> + <T>
    // <E> ::= <E> - <T>
    // <E> ::= <T>
    private static void e() throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        t();
        while ((token == T_ADD) || (token == T_SUBTRACT)) {
            switch (token) {
                case T_ADD:
                    searchNextToken();
                    t();
                    semanticRule(SemanticAction.ADD);
                    break;
                case T_SUBTRACT:
                    searchNextToken();
                    t();
                    semanticRule(SemanticAction.SUBTRACT);
                    break;
            }
        }

        accumulateRecognizedSyntacticRule("<E> ::= <E> + <T> | <E> - <T> | <T>");
    }

    // <T> ::= <T> * <F>
    // <T> ::= <T> / <F>
    // <T> ::= <T> % <F>
    // <T> ::= <F>
    private static void t() throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        f();
        while ((token == T_MULTIPLY) || (token == T_DIVIDE) || (token == T_MOD)) {
            switch (token) {
                case T_MULTIPLY:
                    searchNextToken();
                    f();
                    semanticRule(SemanticAction.MULTIPLY);
                    break;
                case T_DIVIDE:
                    searchNextToken();
                    f();
                    semanticRule(SemanticAction.DIVIDE);
                    break;
                case T_MOD:
                    searchNextToken();
                    f();
                    semanticRule(SemanticAction.MOD);
                    break;
            }
        }

        accumulateRecognizedSyntacticRule("<T> ::= <T> * <F> | <T> / <F> | <T> % <F> | <F>");
    }

    // <F> ::= '(' <E> ')'
    // <F> ::= <NUM>
    // <F> ::= <ID>
    private static void f() throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        switch (token) {
            case T_PARENTHESIS_OPEN:
                searchNextToken();
                e();
                semanticRule(SemanticAction.PARENTHESIS);
                if (token == T_PARENTHESIS_CLOSE) {
                    searchNextToken();
                    accumulateRecognizedSyntacticRule("<F> ::= '(' <E> ')'");
                } else {
                    logSyntaxError("Esperava ')', mas encontrou: " + lexeme);
                }
                break;
            case T_NUMBER:
                num();
                semanticRule(SemanticAction.NUMBER);
                accumulateRecognizedSyntacticRule("<F> ::= <NUM>");
                break;
            case T_ID:
                id();
                semanticRule(SemanticAction.VARIABLE_USAGE);
                accumulateRecognizedSyntacticRule("<F> ::= <ID>");
                break;
            case T_STRING_LITERAL:

                semanticRule(SemanticAction.STRING_LITERAL);

                searchNextToken();

                accumulateRecognizedSyntacticRule(
                    "<F> ::= <STRING_LITERAL>"
                );
            
                break;
            case T_TRUE:
                semanticRule(SemanticAction.BOOLEAN_LITERAL);
                searchNextToken();
                accumulateRecognizedSyntacticRule("<F> ::= 'true'");
                break;
            case T_FALSE:
                semanticRule(SemanticAction.BOOLEAN_LITERAL);
                searchNextToken();
                accumulateRecognizedSyntacticRule("<F> ::= 'false'");
                break;
            default:
                logSyntaxError("Fator inválido. Encontrou: " + lexeme);
        }
    }

    // <NUM> ::= [0-9]+('.'[0-9]+)?
    private static void num()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_NUMBER) {
            searchNextToken();
        } else {
            logSyntaxError("Esperava um número, mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<NUM> ::= [0-9]+('.'[0-9]+)?");
    }

    // <ID> ::= [a-zA-Z_][a-zA-Z0-9_]*
    private static void id()
            throws IOException, LexicalErrorException, SyntacticErrorException, SemanticErrorException {
        if (token == T_ID) {
            variableName = lexeme;
            searchNextToken();
        } else {
            logSyntaxError("Esperava um identificador, mas encontrou: " + lexeme);
        }

        accumulateRecognizedSyntacticRule("<ID> ::= [a-zA-Z_][a-zA-Z0-9_]*");
    }

    static void searchNextToken() throws IOException, LexicalErrorException {
        if (lexeme != null) {
            lastLexeme = new String(lexeme);
        }

        StringBuffer sbLexeme = new StringBuffer("");

        // Desconsidera espaços, enters e tabs até o início do proximo token
        while ((lookAhead == 8) ||
                (lookAhead == 9) ||
                (lookAhead == 11) ||
                (lookAhead == 12) ||
                (lookAhead == 32) ||
                (lookAhead == '\n') ||
                (lookAhead == '\r')) {
            moveLookAhead();
        }

        // Caso o primeiro caractere seja alfabético, procura capturar a sequência de
        // caracteres que se segue a ele e classifica-la
        if (((lookAhead >= 'A') && (lookAhead <= 'Z')) ||
                ((lookAhead >= 'a') && (lookAhead <= 'z')) ||
                lookAhead == '_') {
            sbLexeme.append(lookAhead);
            moveLookAhead();

            while (((lookAhead >= 'A') && (lookAhead <= 'Z')) ||
                    ((lookAhead >= 'a') && (lookAhead <= 'z')) ||
                    ((lookAhead >= '0') && (lookAhead <= '9')) ||
                    (lookAhead == '_')) {
                sbLexeme.append(lookAhead);
                moveLookAhead();
            }

            lexeme = sbLexeme.toString();

            // Classifica o token como palavra reservada ou id
            if (lexeme.equalsIgnoreCase("class"))
                token = T_CLASS;
            else if (lexeme.equalsIgnoreCase("private"))
                token = T_PRIVATE;
            else if (lexeme.equalsIgnoreCase("protected"))
                token = T_PROTECTED;
            else if (lexeme.equalsIgnoreCase("public"))
                token = T_PUBLIC;
            else if (lexeme.equalsIgnoreCase("static"))
                token = T_STATIC;
            else if (lexeme.equalsIgnoreCase("void"))
                token = T_VOID;
            else if (lexeme.equalsIgnoreCase("main"))
                token = T_MAIN;
            else if (lexeme.equalsIgnoreCase("args"))
                token = T_ARGS;
            else if (lexeme.equalsIgnoreCase("int"))
                token = T_INT;
            else if (lexeme.equalsIgnoreCase("double"))
                token = T_DOUBLE;
            else if (lexeme.equalsIgnoreCase("string"))
                token = T_STRING;
            else if (lexeme.equalsIgnoreCase("boolean"))
                token = T_BOOLEAN;
            else if (lexeme.equalsIgnoreCase("if"))
                token = T_IF;
            else if (lexeme.equalsIgnoreCase("else"))
                token = T_ELSE;
            else if (lexeme.equalsIgnoreCase("while"))
                token = T_WHILE;
            else if (lexeme.equalsIgnoreCase("for"))
                token = T_FOR;
            else if (lexeme.equalsIgnoreCase("switch"))
                token = T_SWITCH;
            else if (lexeme.equalsIgnoreCase("case"))
                token = T_CASE;
            else if (lexeme.equalsIgnoreCase("default"))
                token = T_DEFAULT;
            else if (lexeme.equalsIgnoreCase("break"))
                token = T_BREAK;
            else if (lexeme.equalsIgnoreCase("system"))
                token = T_SYSTEM;
            else if (lexeme.equalsIgnoreCase("out"))
                token = T_OUT;
            else if (lexeme.equalsIgnoreCase("println"))
                token = T_PRINTLN;
            else if (lexeme.equalsIgnoreCase("true"))
                token = T_TRUE;
            else if (lexeme.equalsIgnoreCase("false"))
                token = T_FALSE;
            else {
                token = T_ID;
            }
        } else if ((lookAhead >= '0') && (lookAhead <= '9')) {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            while ((lookAhead >= '0') && (lookAhead <= '9')) {
                sbLexeme.append(lookAhead);
                moveLookAhead();
            }
            token = T_NUMBER;
        }else if (lookAhead == '"') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            while (lookAhead != '"' && lookAhead != END_FILE) {
            
                sbLexeme.append(lookAhead);
                moveLookAhead();
            
            }
        
            if (lookAhead == END_FILE) {
            
                token = T_LEXICAL_ERROR;
                lexeme = sbLexeme.toString();
            
                logLexicalError("String não finalizada: " + lexeme);
            
            }
        
            sbLexeme.append(lookAhead);
            moveLookAhead();
            token = T_STRING_LITERAL;
        } else if (lookAhead == '{') {
            sbLexeme.append(lookAhead);
            token = T_BRACE_OPEN;
            moveLookAhead();
        } else if (lookAhead == '}') {
            sbLexeme.append(lookAhead);
            token = T_BRACE_CLOSE;
            moveLookAhead();
        } else if (lookAhead == '[') {
            sbLexeme.append(lookAhead);
            token = T_BRACKET_OPEN;
            moveLookAhead();
        } else if (lookAhead == ']') {
            sbLexeme.append(lookAhead);
            token = T_BRACKET_CLOSE;
            moveLookAhead();
        } else if (lookAhead == '(') {
            sbLexeme.append(lookAhead);
            token = T_PARENTHESIS_OPEN;
            moveLookAhead();
        } else if (lookAhead == ')') {
            sbLexeme.append(lookAhead);
            token = T_PARENTHESIS_CLOSE;
            moveLookAhead();
        } else if (lookAhead == ';') {
            sbLexeme.append(lookAhead);
            token = T_SEMICOLON;
            moveLookAhead();
        } else if (lookAhead == ':') {
            sbLexeme.append(lookAhead);
            token = T_COLON;
            moveLookAhead();
        } else if (lookAhead == ',') {
            sbLexeme.append(lookAhead);
            token = T_COMMA;
            moveLookAhead();
        } else if (lookAhead == '.') {
            sbLexeme.append(lookAhead);
            token = T_POINT;
            moveLookAhead();
        } else if (lookAhead == '+') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '+') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_INCREMENT;
            } else {
                token = T_ADD;
            }
        } else if (lookAhead == '-') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '-') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_DECREMENT;
            } else {
                token = T_SUBTRACT;
            }
        } else if (lookAhead == '*') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            token = T_MULTIPLY;
        } else if (lookAhead == '/') {
            sbLexeme.append(lookAhead);
            token = T_DIVIDE;
            moveLookAhead();
        } else if (lookAhead == '%') {
            sbLexeme.append(lookAhead);
            token = T_MOD;
            moveLookAhead();
        } else if (lookAhead == '>') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '=') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_GREATER_EQUAL;
            } else {
                token = T_GREATER;
            }
        } else if (lookAhead == '<') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '=') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_SMALLER_EQUAL;
            } else {
                token = T_SMALLER;
            }
        } else if (lookAhead == '=') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '=') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_EQUAL_EQUAL;
            } else {
                token = T_EQUAL;
            }
        } else if (lookAhead == '!') {
            sbLexeme.append(lookAhead);
            moveLookAhead();
            if (lookAhead == '=') {
                sbLexeme.append(lookAhead);
                moveLookAhead();
                token = T_DIFFERENT;
            } else {
                token = T_EXCLAMATION;
            }
        } else if (lookAhead == END_FILE) {
            token = T_END_SOURCE;
        } else {
            token = T_LEXICAL_ERROR;
            sbLexeme.append(lookAhead);
        }

        lexeme = sbLexeme.toString();

        showToken();

        if (token == T_LEXICAL_ERROR) {
            logLexicalError("Token desconhecido: " + lexeme);
        }
    }

    static void moveLookAhead() throws IOException {
        if ((pointer + 1) > sourceLine.length()) {

            currentLine++;
            pointer = 0;

            if ((sourceLine = javaFile.readLine()) == null) {
                lookAhead = END_FILE;
            } else {

                StringBuffer sbLinhaFonte = new StringBuffer(sourceLine);
                sbLinhaFonte.append('\13').append('\10');
                sourceLine = sbLinhaFonte.toString();

                lookAhead = sourceLine.charAt(pointer);
            }
        } else {
            lookAhead = sourceLine.charAt(pointer);
        }

        pointer++;
        currentColumn = pointer + 1;
    }

    /**
     * Exibe o token reconhecido
     */
    private static void showToken() {
        StringBuffer tokenLexeme = new StringBuffer("");

        switch (token) {
            case T_CLASS:
                tokenLexeme.append("T_CLASS");
                break;
            case T_PRIVATE:
                tokenLexeme.append("T_PRIVATE");
                break;
            case T_PROTECTED:
                tokenLexeme.append("T_PROTECTED");
                break;
            case T_PUBLIC:
                tokenLexeme.append("T_PUBLIC");
                break;
            case T_STATIC:
                tokenLexeme.append("T_STATIC");
                break;
            case T_VOID:
                tokenLexeme.append("T_VOID");
                break;
            case T_MAIN:
                tokenLexeme.append("T_MAIN");
                break;
            case T_ARGS:
                tokenLexeme.append("T_ARGS");
                break;
            case T_INT:
                tokenLexeme.append("T_INT");
                break;
            case T_DOUBLE:
                tokenLexeme.append("T_DOUBLE");
                break;
            case T_STRING:
                tokenLexeme.append("T_STRING");
                break;
            case T_BOOLEAN:
                tokenLexeme.append("T_BOOLEAN");
                break;
            case T_ID:
                tokenLexeme.append("T_ID");
                break;
            case T_NUMBER:
                tokenLexeme.append("T_NUMBER");
                break;
            case T_BRACE_OPEN:
                tokenLexeme.append("T_BRACE_OPEN");
                break;
            case T_BRACE_CLOSE:
                tokenLexeme.append("T_BRACE_CLOSE");
                break;
            case T_BRACKET_OPEN:
                tokenLexeme.append("T_BRACKET_OPEN");
                break;
            case T_BRACKET_CLOSE:
                tokenLexeme.append("T_BRACKET_CLOSE");
                break;
            case T_PARENTHESIS_OPEN:
                tokenLexeme.append("T_PARENTHESIS_OPEN");
                break;
            case T_PARENTHESIS_CLOSE:
                tokenLexeme.append("T_PARENTHESIS_CLOSE");
                break;
            case T_SEMICOLON:
                tokenLexeme.append("T_SEMICOLON");
                break;
            case T_COLON:
                tokenLexeme.append("T_COLON");
                break;
            case T_COMMA:
                tokenLexeme.append("T_COMMA");
                break;
            case T_POINT:
                tokenLexeme.append("T_POINT");
                break;
            case T_ADD:
                tokenLexeme.append("T_ADD");
                break;
            case T_SUBTRACT:
                tokenLexeme.append("T_SUBTRACT");
                break;
            case T_MULTIPLY:
                tokenLexeme.append("T_MULTIPLY");
                break;
            case T_DIVIDE:
                tokenLexeme.append("T_DIVIDE");
                break;
            case T_MOD:
                tokenLexeme.append("T_MOD");
                break;
            case T_GREATER_EQUAL:
                tokenLexeme.append("T_GREATER_EQUAL");
                break;
            case T_GREATER:
                tokenLexeme.append("T_GREATER");
                break;
            case T_SMALLER_EQUAL:
                tokenLexeme.append("T_SMALLER_EQUAL");
                break;
            case T_SMALLER:
                tokenLexeme.append("T_SMALLER");
                break;
            case T_EQUAL_EQUAL:
                tokenLexeme.append("T_EQUAL_EQUAL");
                break;
            case T_EQUAL:
                tokenLexeme.append("T_EQUAL");
                break;
            case T_DIFFERENT:
                tokenLexeme.append("T_DIFFERENT");
                break;
            case T_EXCLAMATION:
                tokenLexeme.append("T_EXCLAMATION");
                break;
            case T_IF:
                tokenLexeme.append("T_IF");
                break;
            case T_WHILE:
                tokenLexeme.append("T_WHILE");
                break;
            case T_FOR:
                tokenLexeme.append("T_FOR");
                break;
            case T_SWITCH:
                tokenLexeme.append("T_SWITCH");
                break;
            case T_CASE:
                tokenLexeme.append("T_CASE");
                break;
            case T_DEFAULT:
                tokenLexeme.append("T_DEFAULT");
                break;
            case T_BREAK:
                tokenLexeme.append("T_BREAK");
                break;
            case T_SYSTEM:
                tokenLexeme.append("T_SYSTEM");
                break;
            case T_OUT:
                tokenLexeme.append("T_OUT");
                break;
            case T_PRINTLN:
                tokenLexeme.append("T_PRINTLN");
                break;
            case T_INCREMENT:
                tokenLexeme.append("T_INCREMENT");
                break;
            case T_DECREMENT:
                tokenLexeme.append("T_DECREMENT");
                break;
            case T_TRUE:
                tokenLexeme.append("T_TRUE");
                break;
            case T_FALSE:
                tokenLexeme.append("T_FALSE");
                break;
            case T_END_SOURCE:
                tokenLexeme.append("T_END_SOURCE");
                break;
            case T_LEXICAL_ERROR:
                tokenLexeme.append("T_ERRO_LEX");
                break;
            case T_NULL:
                tokenLexeme.append("T_NULL");
                break;
            default:
                tokenLexeme.append("N/A");
                break;
        }
        accumulateToken(tokenLexeme.toString() + " ( " + lexeme + " )");
        tokenLexeme.append(lexeme);
    }

    /**
     * Acumula o token identificado
     * 
     * @param identifiedToken Token identificado
     */
    private static void accumulateToken(String identifiedToken) {
        identifiedTokens.append(identifiedToken);
        identifiedTokens.append("\n");
    }

    /**
     * Acumula a regra reconhecida
     * 
     * @param recognizedRule Regra reconhecida
     */
    private static void accumulateRecognizedSyntacticRule(String recognizedRule) {
        recognizedRules.append(recognizedRule);
        recognizedRules.append("\n");
    }

    /**
     * Valida a regra semântica e gera o código em Python
     * 
     * @param ruleNumber Número da regra
     * @throws SemanticErrorException
     */
    private static void semanticRule(SemanticAction action) throws SemanticErrorException {
        int ruleNumber = action.getCode();

        switch (action) {
            case START_PROGRAM:
                codePython.append("# Código Python Gerado Automaticamente\n\n");
                break;
            case VARIABLE_DECLARE:
                insertIntoSymbolsTable(variableName, currentType);
                codePython.append(tabulation(indentationLevel));
                codePython.append(variableName + " = " + pythonDefaultValue(currentType) + "\n");
                break;
            case VARIABLE_USAGE:
                if (checkExistsInSymbolsTable(variableName)) {
                    semanticStack.push(variableName, ruleNumber);
                }
                break;
                case ASSIGNMENT:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                    if (readingForStep) {
                    String expression = nodo_2.getCodeLowerCase();
                    if(expression.contains("+")){
                        forStep = expression.substring(
                            expression.indexOf("+") + 1
                        ).trim();
                    }
                    else if(expression.contains("-")){                    
                        forStep = "-" + expression.substring(
                            expression.indexOf("-") + 1
                        ).trim();                    
                    }
                    else{
                        forStep = "1";
                    }
                    } else if (!insideForHeader) {
                    codePython.append(tabulation(indentationLevel));
                    codePython.append(nodo_1.getCodeLowerCase() + " = " + nodo_2.getCodeLowerCase() + "\n");
                    }
                break;
            case INCREMENT:
                nodo_1 = semanticStack.pop();
                if (readingForStep) {
                    forStep = "1";
                } else {
                    codePython.append(tabulation(indentationLevel));
                    codePython.append(nodo_1.getCodeLowerCase() + " = " + nodo_1.getCodeLowerCase() + " + 1\n");
                }
                break;
            case DECREMENT:
                nodo_1 = semanticStack.pop();
                if (readingForStep) {
                    forStep = "-1";
                } else {
                    codePython.append(tabulation(indentationLevel));
                    codePython.append(nodo_1.getCodeLowerCase() + " = " + nodo_1.getCodeLowerCase() + " - 1\n");
                }
                break;
            case IF_BEGIN:
                nodo_1 = semanticStack.pop();
                codePython.append(tabulation(indentationLevel));
                codePython.append("if " + nodo_1.getCodeLowerCase() + ":\n");
                indentationLevel++;
                break;
            case ELSE_BEGIN:
                codePython.append(tabulation(indentationLevel));
                codePython.append("else:\n");
                indentationLevel++;
                break;
            case WHILE_BEGIN:
                nodo_1 = semanticStack.pop();
                codePython.append(tabulation(indentationLevel));
                codePython.append("while " + nodo_1.getCodeLowerCase() + ":\n");
                indentationLevel++;
                break;
            case FOR_BEGIN:
                String rangeEnd = forEnd;
                if (forConditionOperator != null) {
                    switch (forConditionOperator) {
                        case "<=":
                            if (forStep == null || !forStep.startsWith("-")){
                                rangeEnd = forEnd + " + 1";
                            }
                            break;
                        case ">=":
                            if (forStep == null || forStep.startsWith("-")) {
                                rangeEnd = forEnd + " - 1";
                            }
                            break;
                        default:
                            break;
                    }
                }
                codePython.append(tabulation(indentationLevel));
                codePython.append("for " + forVariable + " in range(" + forStart + ", " + rangeEnd);
                if (!forStep.equals("1")) {
                    codePython.append(", " + forStep);
                }
                codePython.append("):\n");
                indentationLevel++;
                break;
            case ADD:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " + " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case SUBTRACT:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " - " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case MULTIPLY:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " * " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case DIVIDE:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " / " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case MOD:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " % " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case PARENTHESIS:
                nodo_1 = semanticStack.pop();
                semanticStack.push("(" + nodo_1.getCodeLowerCase() + ")", ruleNumber);
                break;
            case NUMBER:
                semanticStack.push(lastLexeme, ruleNumber);
                break;
            case GREATER:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " > " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case SMALLER:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " < " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case GREATER_EQUAL:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " >= " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case SMALLER_EQUAL:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " <= " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case EQUAL_EQUAL:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " == " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case DIFFERENT:
                nodo_2 = semanticStack.pop();
                nodo_1 = semanticStack.pop();
                semanticStack.push(nodo_1.getCodeLowerCase() + " != " + nodo_2.getCodeLowerCase(), ruleNumber);
                break;
            case PRINT:
                nodo_1 = semanticStack.pop();
                codePython.append(tabulation(indentationLevel));
                codePython.append("print(" + nodo_1.getCodeLowerCase() + ")\n");
                break;
            case LESS_TAB:
                indentationLevel--;
                break;
            case MORE_TAB:
                indentationLevel++;
                break;
            case SWITCH_BEGIN:
                variavel = variableName;
                switchFirstCase = true;
                break;
            case CASE_BEGIN:
                nodo_1 = semanticStack.pop();
                codePython.append(tabulation(indentationLevel));
                if (switchFirstCase) {
                    codePython.append("if " + variavel + " == " + nodo_1.getCodeLowerCase() + ":\n");
                    switchFirstCase = false;
                } else {
                    codePython.append("elif " + variavel + " == " + nodo_1.getCodeLowerCase() + ":\n");
                }
                indentationLevel++;
                break;
            case DEFAULT_BEGIN:
                codePython.append(tabulation(indentationLevel));
                codePython.append("else:\n");
                indentationLevel++;
                break;
            case CASE_BREAK:
                // Break do switch Java não possui equivalente em Python
                break;
            case STRING_LITERAL:
                semanticStack.push(lexeme, ruleNumber);
                break;
            case BOOLEAN_LITERAL:
            if (lexeme.equals("true")) {
                semanticStack.push("True", ruleNumber);
            }
            else if (lexeme.equals("false")) {
                semanticStack.push("False", ruleNumber);
            }
            else {
                semanticStack.push(lexeme, ruleNumber);
            }
                break;
            default:
                break;
        }
    }

    /**
     * Verifica se uma variável existe na tabela de símbolos
     * 
     * @param lastLexeme Último lexema reconhecido
     * @return True se a variável existe na tabela de símbolos, false caso contrário
     * @throws SemanticErrorException
     */
    private static boolean checkExistsInSymbolsTable(String lastLexeme) throws SemanticErrorException {
        if (!symbolsTable.containsKey(lastLexeme)) {
            throw new SemanticErrorException("Variável " + lastLexeme + " não está declarada!\nLinha: " + currentLine);
        } else {
            return true;
        }
    }

    /**
     * Insere uma variável na tabela de símbolos
     * 
     * @param lastLexeme Último lexema reconhecido
     * @param variableType Tipo da variável em Java
     * @throws SemanticErrorException
     */
    private static void insertIntoSymbolsTable(String lastLexeme, String variableType)
            throws SemanticErrorException {
            
        if (symbolsTable.containsKey(lastLexeme)) {
        
            throw new SemanticErrorException(
                "Variável " + lastLexeme + " já declarada!\nLinha: " + currentLine
            );
        
        } else {
        
            symbolsTable.put(lastLexeme, variableType);
        
        }
    }

    /**
     * Gera uma string de tabulação para o código Python gerado
     * 
     * @param quantity Quantidade de tabulações a serem geradas
     * @return String com a tabulação tabulação
     */
    private static String tabulation(int quantity) {
        StringBuffer stringBuffer = new StringBuffer();
        if (quantity < 0) {
            quantity = 0;
        }
        for (int t = 0; t < quantity; t++) {
            stringBuffer.append("    ");
        }
        return stringBuffer.toString();
    }

    /**
     * Registra um erro léxico encontrado durante a análise léxica
     * 
     * @param msg Mensagem de erro a ser registrada
     * @throws LexicalErrorException
     */
    private static void logLexicalError(String msg) throws LexicalErrorException {
        StringBuilder message = new StringBuilder();

        message.append("Erro Léxico!\n")
                .append("Linha: ").append(currentLine).append("\n")
                .append("Coluna: ").append(currentColumn).append("\n\n")
                .append("Linha com erro: <").append(sourceLine).append(">\n\n")
                .append(msg);

        if (compilationState == E_WITHOUT_ERRORS) {
            compilationState = E_LEXICAL_ERROR;
            errorMessage = message.toString();
        }

        throw new LexicalErrorException(message.toString());
    }

    /**
     * Registra um erro sintático encontrado durante a análise sintática
     * 
     * @param msg Mensagem de erro a ser registrada
     * @throws SyntacticErrorException
     */
    private static void logSyntaxError(String msg) throws SyntacticErrorException {
        StringBuilder message = new StringBuilder();

        message.append("Erro Sintático!\n")
                .append("Linha: ").append(currentLine).append("\n")
                .append("Coluna: ").append(currentColumn).append("\n\n")
                .append("Linha com erro: <").append(sourceLine).append(">\n\n")
                .append(msg);

        if (compilationState == E_WITHOUT_ERRORS) {
            compilationState = E_SYNTACTIC_ERROR;
            errorMessage = message.toString();
        }

        throw new SyntacticErrorException(message.toString());
    }

    /**
     * Define valor padrão para variáveis não inicializadas em Python, de acordo com seu tipo em Java
     * 
     * @param type Tipo da variável em Java
     */
    private static String pythonDefaultValue(String type){

        switch(type){

            case "int":
                return "0";

            case "double":
                return "0.0";

            case "String":
                return "\"\"";

            case "boolean":
                return "False";

            default:
                return "None";
        }
    }    

    /**
     * Registra um erro semântico encontrado durante a análise semântica
     * 
     * @param msg Mensagem de erro a ser registrada
     * @throws SemanticErrorException
     */
    @SuppressWarnings("unused")
    private static void logSemanticError(String msg) throws SemanticErrorException {
        StringBuilder message = new StringBuilder();

        message.append("Erro Semântico!\n")
                .append("Linha: ").append(currentLine).append("\n")
                .append("Coluna: ").append(currentColumn).append("\n\n")
                .append("Linha com erro: <").append(sourceLine).append(">\n\n")
                .append(msg);

        if (compilationState == E_WITHOUT_ERRORS) {
            compilationState = E_SEMANTIC_ERROR;
            errorMessage = message.toString();
        }

        throw new SemanticErrorException(message.toString());
    }

    private static void displayOutput() {
        JTextArea text = new JTextArea();
        text.append(identifiedTokens.toString());
        showInformationMessage(text, "Análise Léxica");

        text.setText(recognizedRules.toString());
        text.append("\n\nStatus da Compilacao:\n\n");
        text.append(errorMessage);
        showInformationMessage(text, "Resumo da Compilação");
    }

    /**
     * Fecha o arquivo fonte após a conclusão da compilação
     * 
     * @throws IOException
     */
    private static void closeSource() throws IOException {
        javaFile.close();
    }

    /**
     * Exibe uma mensagem de erro
     * 
     * @param message Mensagem a ser exibida
     * @param title   Título da mensagem
     */
    private static void showErrorMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Exibe uma mensagem de informação
     * 
     * @param message Mensagem a ser exibida
     * @param title   Título da mensagem
     */
    private static void showInformationMessage(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Exibe uma mensagem de informação
     * 
     * @param message Mensagem a ser exibida
     * @param title   Título da mensagem
     */
    private static void showInformationMessage(JTextArea message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }
}