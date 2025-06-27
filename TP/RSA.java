import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

    // Classe para representar a chave pública
    public static class ChavePublica {
        private BigInteger modulo; // O "modulo" é usado para operações modulares no RSA
        private BigInteger expoente; // O "expoente" é a chave pública usada para criptografia

        public ChavePublica(BigInteger modulo, BigInteger expoente) {
            this.modulo = modulo;
            this.expoente = expoente;
        }

        public BigInteger getModulo() {
            return modulo; 
        }

        public BigInteger getExpoente() {
            return expoente; 
        }
    }

    // Classe para representar a chave privada
    public static class ChavePrivada {
        private BigInteger modulo; // O "modulo" é compartilhado com a chave pública
        private BigInteger expoente; // O "expoente" é a chave privada usada para descriptografia

        public ChavePrivada(BigInteger modulo, BigInteger expoente) {
            this.modulo = modulo;
            this.expoente = expoente;
        }

        public BigInteger getModulo() {
            return modulo; 
        }

        public BigInteger getExpoente() {
            return expoente; 
        }
    }

    // Classe para encapsular o par de chaves (pública e privada)
    public static class ParDeChaves {
        private ChavePublica chavePublica; // Instância da chave pública
        private ChavePrivada chavePrivada; // Instância da chave privada

        public ParDeChaves(ChavePublica chavePublica, ChavePrivada chavePrivada) {
            this.chavePublica = chavePublica;
            this.chavePrivada = chavePrivada;
        }

        public ChavePublica getChavePublica() {
            return chavePublica; 
        }

        public ChavePrivada getChavePrivada() {
            return chavePrivada;
        }
    }

    // Método para gerar as chaves RSA
    public static ParDeChaves gerarChaves(int tamanhoChave) {
        SecureRandom random = new SecureRandom(); // Gerador de números aleatórios seguros
        BigInteger p = BigInteger.probablePrime(tamanhoChave / 2, random); // Gera um número primo p
        BigInteger q = BigInteger.probablePrime(tamanhoChave / 2, random); // Gera um número primo q
        BigInteger modulo = p.multiply(q); // Calcula o módulo n = p * q

        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE)); 
        // Calcula φ(n) = (p-1) * (q-1)

        BigInteger expoentePublico = BigInteger.valueOf(65537); 
        // Valor padrão para o expoente público (comum em RSA)

        BigInteger expoentePrivado = expoentePublico.modInverse(phi); 
        // Calcula o inverso modular do expoente público em relação a φ(n)

        ChavePublica chavePublica = new ChavePublica(modulo, expoentePublico); 
        // Cria a chave pública

        ChavePrivada chavePrivada = new ChavePrivada(modulo, expoentePrivado); 
        // Cria a chave privada

        return new ParDeChaves(chavePublica, chavePrivada); 
        // Retorna o par de chaves (pública e privada)
    }

    // Método para criptografar uma mensagem
    public static BigInteger[] criptografar(String mensagem, ChavePublica chavePublica) {
        BigInteger modulo = chavePublica.getModulo(); // Obtém o módulo da chave pública
        BigInteger expoente = chavePublica.getExpoente(); // Obtém o expoente público

        BigInteger[] mensagemCriptografada = new BigInteger[mensagem.length()];
        // Array para armazenar os caracteres criptografados

        for (int i = 0; i < mensagem.length(); i++) {
            BigInteger valorChar = BigInteger.valueOf(mensagem.charAt(i)); 
            // Converte o caractere para BigInteger

            mensagemCriptografada[i] = valorChar.modPow(expoente, modulo); 
            // Criptografa o caractere usando a fórmula: c = m^e mod n
        }
        return mensagemCriptografada; // Retorna a mensagem criptografada
    }

    // Método para descriptografar uma mensagem
    public static String descriptografar(BigInteger[] mensagemCriptografada, ChavePrivada chavePrivada) {
        BigInteger modulo = chavePrivada.getModulo(); // Obtém o módulo da chave privada
        BigInteger expoente = chavePrivada.getExpoente(); // Obtém o expoente privado

        StringBuilder mensagemDescriptografada = new StringBuilder();
        // StringBuilder para construir a mensagem descriptografada

        for (BigInteger charCriptografado : mensagemCriptografada) {
            BigInteger valorChar = charCriptografado.modPow(expoente, modulo); 
            // Descriptografa o caractere usando a fórmula: m = c^d mod n

            mensagemDescriptografada.append((char) valorChar.intValue()); 
            // Converte o valor BigInteger de volta para caractere e adiciona à mensagem
        }
        return mensagemDescriptografada.toString(); // Retorna a mensagem descriptografada
    }
}
