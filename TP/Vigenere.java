public class Vigenere {
    /*Encriptação por Vigenêre
     * A cifra de Vigenêre é uma técnica de criptografia que utiliza uma chave para criptografar dados.
     * A chave é repetida para corresponder ao tamanho dos dados a serem criptografados.
     * A cifra é baseada na adição modular dos valores ASCII dos caracteres dos dados e da chave.
     */
    public static byte[] criptografar(byte[] data, byte[] chave) {
        byte[] cript = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            // Soma do valor do byte da sequencia com da chave
            // Repete a chave se for menor que a sequencia
            cript[i] = (byte) ((data[i] + chave[i % chave.length]));
        }
        return cript;   
    }

    public static byte[] descriptografar(byte[] data, byte[] chave) {
        byte[] decript = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            // Subtração do valor do byte da sequencia com da chave
            // Repetea chave se for menor que a sequencia
            decript[i] = (byte) (data[i] - chave[i % chave.length]);
        }
        return decript;
    }
}